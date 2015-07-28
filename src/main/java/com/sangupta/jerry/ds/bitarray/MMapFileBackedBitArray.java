package com.sangupta.jerry.ds.bitarray;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;

import net.jcip.annotations.NotThreadSafe;

/**
 * An implementation of {@link BitArray} that uses a memory-mapped
 * file to persist all changes synchronously for the underlying bit
 * array. This is useful for stateful bit-arrays which are expensive
 * to construct yet need the best overall performance.
 * 
 * @author sangupta
 * @since 1.7
 */
@NotThreadSafe
public class MMapFileBackedBitArray implements BitArray {
	
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
	 * The memory-mapped byte-buffer
	 */
	protected final MappedByteBuffer buffer;
	
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
	public MMapFileBackedBitArray(File backingFile, int maxElements) throws IOException {
		if(backingFile == null) {
			throw new IllegalArgumentException("Backing file cannot be empty/null");
		}
		
		if(backingFile.exists() && !backingFile.isFile()) {
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
		this.buffer = this.backingFile.getChannel().map(MapMode.READ_WRITE, 0, this.backingFile.length());
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
		byte bite = this.buffer.get(pos);
		return (bite & bit) != 0;
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
		byte bite = this.buffer.get(pos);
		bite = (byte) (bite | bit);
		this.buffer.put(pos, bite);
		return true;
	}

	/**
	 * @see BitArray#clear()
	 */
	@Override
	public void clear() {
		byte bite = 0;
		for(int index = 0; index < this.numBytes; index++) {
			this.buffer.put(index, bite);
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
		byte bite = this.buffer.get(pos);
		bite = (byte) (bite & bit);
		this.buffer.put(pos, bite);
	}

	/**
	 * @see BitArray#setBitIfUnset(int)
	 */
	@Override
	public boolean setBitIfUnset(int index) {
		if(this.getBit(index)) {
			return this.setBit(index);
		}
		
		return false;
	}

	/**
	 * @see BitArray#or(BitArray)
	 */
	@Override
	public void or(BitArray bitArray) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see BitArray#and(BitArray)
	 */
	@Override
	public void and(BitArray bitArray) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see BitArray#bitSize()
	 */
	@Override
	public int bitSize() {
		return this.numBytes;
	}
	
	/**
	 * Extend this file to the given new length filling extra bytes with zeros.
	 * 
	 * @param newLength
	 *            the new length expected for this file
	 * 
	 * @throws IOException
	 *             if file operation fails
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
		this.closeDirectBuffer(this.buffer);
		this.backingFile.close();
	}
	
	/**
	 * Method that helps unmap a memory-mapped file before being
	 * garbage-collected.
	 * 
	 * @param byteBuffer
	 *            the {@link ByteBuffer} instance to close
	 */
	protected void closeDirectBuffer(ByteBuffer byteBuffer) {
	    if (!byteBuffer.isDirect()) {
	    	return;
	    }

	    // we could use this type cast and call functions without reflection code,
	    // but static import from sun.* package is risky for non-SUN virtual machine.
	    //try { ((sun.nio.ch.DirectBuffer)cb).cleaner().clean(); } catch (Exception ex) { }
	    try {
	        Method cleaner = byteBuffer.getClass().getMethod("cleaner");
	        cleaner.setAccessible(true);
	        Method clean = Class.forName("sun.misc.Cleaner").getMethod("clean");
	        clean.setAccessible(true);
	        clean.invoke(cleaner.invoke(byteBuffer));
	    } catch(Exception ex) { 
	    	
	    }
	    
	    byteBuffer = null;
	}

	@Override
	public int numBytes() {
		return this.numBytes;
	}
	
	@Override
	public byte[] toByteArray() {
		if(this.buffer.hasArray()) {
			return this.buffer.array();
		}
		
		byte[] bytes = new byte[this.numBytes];
		for(int index = 0; index < this.numBytes; index++) {
			bytes[index]  = this.buffer.get(index);
		}
		
		return bytes;
	}
}
