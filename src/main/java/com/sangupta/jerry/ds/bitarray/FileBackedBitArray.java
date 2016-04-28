/**
 *
 * jerry - Common Java Functionality
 * Copyright (c) 2012-2016, Sandeep Gupta
 *
 * http://sangupta.com/projects/jerry-core
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package com.sangupta.jerry.ds.bitarray;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.sangupta.jerry.util.BitUtils;

import net.jcip.annotations.NotThreadSafe;

/**
 * An implementation of {@link BitArray} that uses a normal random
 * file to persist all changes synchronously for the underlying bit
 * array. This is useful for stateful bit-arrays which are expensive
 * to construct yet need a good overall performance. This class is
 * not thread-safe.
 *
 * @author sangupta
 * @since 1.7
 */
@NotThreadSafe
public class FileBackedBitArray implements BitArray {

	/**
	 * Underlying file that represents the state of the
	 * {@link BitArray}.
	 *
	 */
	protected final RandomAccessFile backingFile;

	/**
	 * The maximum number of elements this file will store
	 */
	protected final int maxElements;

	/**
	 * The number of bytes being used for this byte-array
	 *
	 */
	protected final int numBytes;

	/**
	 * Construct a {@link BitArray} that is backed by the given file. Ensure
	 * that the file is a local file and not on a network share for performance
	 * reasons.
	 *
	 * @param backingFile
	 *            the file that needs to store the bit-array
	 *
	 * @param maxElements
	 *            the number of maximum elements that this {@link BitArray}
	 *            implementation will store
	 *
	 * @throws IOException
	 *             if something fails while reading the file initially
	 *
	 * @throws IllegalArgumentException
	 *             if the {@link #backingFile} is <code>null</code>, is not a
	 *             file, or the number of {@link #maxElements} are less than
	 *             equal to zero
	 */
	public FileBackedBitArray(File backingFile, int maxElements) throws IOException {
		if(backingFile == null) {
			throw new IllegalArgumentException("Backing file cannot be empty/null");
		}

		if(!backingFile.isFile()) {
			throw new IllegalArgumentException("Backing file does not represent a valid file");
		}

		if(maxElements <= 0) {
			throw new IllegalArgumentException("Max elements in array cannot be less than or equal to zero");
		}

		// we open in "rwd" mode, to save one i/o operation
		// than in "rws" mode
		this.backingFile = new RandomAccessFile(backingFile, "rwd");

		this.numBytes = (maxElements >> 3) + 1;
		extendFile(this.numBytes);

		// initialize the rest
		this.maxElements = maxElements;
	}

	/**
	 * @see BitArray#getBit(int)
	 */
	@Override
	public boolean getBit(int index) {
		if(index > maxElements) {
			throw new IndexOutOfBoundsException("Index is greater than max elements permitted");
		}

		int pos = index >> 3; // div 8
		int bit = 1 << (index & 0x7);

		try {
			this.backingFile.seek(pos);
			byte bite = this.backingFile.readByte();
			return (bite & bit) != 0;
		} catch(IOException e) {
			throw new RuntimeException("Unable to read bitset from disk");
		}
	}

	/**
	 * @see BitArray#setBit(int)
	 */
	@Override
	public boolean setBit(int index) {
		if(index > maxElements) {
			throw new IndexOutOfBoundsException("Index is greater than max elements permitted");
		}

		int pos = index >> 3; // div 8
		int bit = 1 << (index & 0x7);
		try {
			this.backingFile.seek(pos);
			byte bite = this.backingFile.readByte();
			bite = (byte) (bite | bit);

			this.backingFile.seek(pos);
			this.backingFile.writeByte(bite);
			return true;
		} catch(IOException e) {
			throw new RuntimeException("Unable to read bitset from disk");
		}
	}

	/**
	 * @see BitArray#clear()
	 */
	@Override
	public void clear() {
		byte[] bytes = new byte[this.numBytes];
		Arrays.fill(bytes, (byte) 0);

		try {
			this.backingFile.seek(0);
			this.backingFile.write(bytes);
		} catch(IOException e) {
			throw new RuntimeException("Unable to read bitset from disk");
		}
	}

	/**
	 * @see BitArray#clearBit(int)
	 */
	@Override
	public void clearBit(int index) {
		if(index > maxElements) {
			throw new IndexOutOfBoundsException("Index is greater than max elements permitted");
		}

		int pos = index >> 3; // div 8
		int bit = 1 << (index & 0x7);
		bit = ~bit;

		try {
			this.backingFile.seek(pos);
			byte bite = this.backingFile.readByte();
			bite = (byte) (bite & bit);


			this.backingFile.seek(pos);
			this.backingFile.writeByte(bite);
		} catch(IOException e) {
			throw new RuntimeException("Unable to read bitset from disk");
		}
	}

	/**
	 * @see BitArray#setBitIfUnset(int)
	 */
	@Override
	public boolean setBitIfUnset(int index) {
		if(!this.getBit(index)) {
			return this.setBit(index);
		}

		return false;
	}

	/**
	 * @see BitArray#or(BitArray)
	 */
	@Override
	public void or(BitArray bitArray) {
		if(bitArray == null) {
			throw new IllegalArgumentException("BitArray to be combined with cannot be null");
		}

		if(this.numBytes != bitArray.numBytes()) {
			throw new IllegalArgumentException("BitArray to be combined with must be of equal length");
		}

		try {
			this.backingFile.seek(0);
			byte[] bytes = bitArray.toByteArray();
			for(int index = 0; index < bytes.length; index++) {
				byte bite = this.backingFile.readByte();
				bite |= bytes[index];
				this.backingFile.seek(this.backingFile.getFilePointer() - 1);
				this.backingFile.write(bite);
			}
		} catch(IOException e) {
			throw new RuntimeException("Unable to read/write bit-array from disk", e);
		}
	}

	/**
	 * @see BitArray#and(BitArray)
	 */
	@Override
	public void and(BitArray bitArray) {
		if(bitArray == null) {
			throw new IllegalArgumentException("BitArray to be combined with cannot be null");
		}

		if(this.numBytes != bitArray.numBytes()) {
			throw new IllegalArgumentException("BitArray to be combined with must be of equal length");
		}

		try {
			this.backingFile.seek(0);
			byte[] bytes = bitArray.toByteArray();
			for(int index = 0; index < bytes.length; index++) {
				byte bite = this.backingFile.readByte();
				bite &= bytes[index];
				this.backingFile.seek(this.backingFile.getFilePointer() - 1);
				this.backingFile.write(bite);
			}
		} catch(IOException e) {
			throw new RuntimeException("Unable to read/write bit-array from disk", e);
		}
	}

	/**
	 * @see BitArray#bitSize()
	 */
	@Override
	public int bitSize() {
		return this.numBytes;
	}

	/**
	 * Extend the file to its new length filling extra bytes with zeroes.
	 *
	 * @param newLength
	 *            the new length of the file that we need
	 *
	 * @throws IOException
	 *             if something fails
	 */
	protected void extendFile(final long newLength) throws IOException {
		long current = this.backingFile.length();
		int delta = (int) (newLength - current) + 1;
		if(delta <= 0) {
			return;
		}

		this.backingFile.setLength(newLength);
		this.backingFile.seek(current);
		byte[] bytes = new byte[delta];
		Arrays.fill(bytes, (byte) 0);
		this.backingFile.write(bytes);
	}

	@Override
	public void close() throws IOException {
		this.backingFile.close();
	}

	@Override
	public int numBytes() {
		return this.numBytes;
	}

	@Override
	public byte[] toByteArray() {
		byte[] bytes = new byte[this.numBytes];

		try {
			this.backingFile.seek(0);
			this.backingFile.readFully(bytes);
			return bytes;
		} catch(IOException e) {
			throw new RuntimeException("Unable to read bit-array from disk", e);
		}
	}

	@Override
	public int getHighestBitSet() {
		byte[] bytes = this.toByteArray();
		for(int index = bytes.length - 1; index >= 0; index--) {
			byte bite = bytes[index];
			if(bite != 0) {
				// this is the highest set bit
				if(index > 0) {
					return (index * 8) + BitUtils.getHighestSetBitIndex(bite);
				}

				return BitUtils.getHighestSetBitIndex(bite);
			}
		}

		// not found
		return -1;
	}

	@Override
	public int getLowestBitSet() {
		byte[] bytes = this.toByteArray();
		for(int index = 0; index < bytes.length; index++) {
			byte bite = bytes[index];
			if(bite != 0) {
				// this is the highest set bit
				if(index > 0) {
					return (index * 8) + BitUtils.getLowestSetBitIndex(bite);
				}

				return BitUtils.getLowestSetBitIndex(bite);
			}
		}

		// not found
		return -1;
	}

	@Override
	public int getNextSetBit(int fromIndex) {
	    // TODO: optimize this method
	    for(int index = fromIndex; index < this.maxElements; index++) {
	        if(this.getBit(index)) {
	            return index;
	        }
	    }

	    return -1;
	}
}
