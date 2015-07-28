/**
 *
 * jerry - Common Java Functionality
 * Copyright (c) 2012-2015, Sandeep Gupta
 * 
 * http://sangupta.com/projects/jerry
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
 
package com.sangupta.jerry.util;

/**
 * Static utility methods to work with byte-arrays.
 * 
 * @author sangupta
 *
 */
public class ByteArrayUtils {

	/**
	 * Read a long value from given byte-array at the given offset. The value is
	 * assumed to be stored in BigEndian format.
	 * 
	 * @param bytes
	 *            the byte array to write in
	 * 
	 * @param offset
	 *            the offset from where the value starts
	 * 
	 * @return the long value thus read
	 */
	public static long readLong(byte[] bytes, int offset) {
		if(bytes == null) {
			throw new IllegalArgumentException("Byte-array cannot be null");
		}
		
		if((offset + 7) >= bytes.length) {
			throw new IndexOutOfBoundsException("Byte-array is smaller than the provided offset");
		}
		
		int position = offset + 7;
		long value = bytes[position--] & 0xFF;
		
		value |= (long) (bytes[position--] & 0xFF) << 8;
		value |= (long) (bytes[position--] & 0xFF) << 16;
		value |= (long) (bytes[position--] & 0xFF) << 24;
		value |= (long) (bytes[position--] & 0xFF) << 32;
		value |= (long) (bytes[position--] & 0xFF) << 40;
		value |= (long) (bytes[position--] & 0xFF) << 48;
		value |= (long) (bytes[position--] & 0xFF) << 56;
		
		return value;
	}
	
	/**
	 * Write a long value in the given byte-array at the given offset. The value
	 * is stored in BigEndian format.
	 * 
	 * @param bytes
	 *            the byte array to write in
	 * 
	 * @param value
	 *            the value to store
	 * 
	 * @param offset
	 *            the offset to write at
	 */
	public static void writeLong(byte[] bytes, long value, int offset) {
		if(bytes == null) {
			throw new IllegalArgumentException("Byte-array cannot be null");
		}
		
		if((offset + 7) >= bytes.length) {
			throw new IndexOutOfBoundsException("Byte-array is smaller than the provided offset");
		}
		
		for (int index = 7; index >= 0; index--) {
			bytes[offset + index] = (byte) (value & 0xFF);
			value >>= 8;
		}
	}

	/**
	 * Return the cardinality of this byte-array. Cardinality is defined as the number of <code>true</code>
	 * bits in the bit-array.
	 * 
	 * @param bytes the byte-array to find cardinality of
	 * 
	 * @return the computed cardinality
	 */
	public static long cardinality(byte[] bytes) {
		if(bytes == null) {
			throw new IllegalArgumentException("Byte-array cannot be null");
		}
		
		if(bytes.length == 0) {
			return 0;
		}
		
		long cardinality = 0;
		for(int index = 0; index < bytes.length; index++) {
			cardinality += Integer.bitCount(bytes[index] & 0xFF);
		}
		
		return cardinality;
	}
}