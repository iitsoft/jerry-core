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


package com.sangupta.jerry.store;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link InMemoryUserLocalStore}.
 *
 * @author sangupta
 *
 */
public class TestInMemoryUserLocalStore {

	@Test
	public void testStore() {
		UserLocalStore store;

		store = new InMemoryUserLocalStore();

		Assert.assertNull(store.get("prop"));
		Assert.assertEquals("value-default", store.get("prop", "value-default"));

		store.put("prop", "value1");
		Assert.assertEquals("value1", store.get("prop"));
		Assert.assertEquals("value1", store.get("prop", "value-default"));

		store.put("prop", "value2");
		Assert.assertEquals("value2", store.get("prop"));

		store.delete("prop");
		Assert.assertNull(store.get("prop"));
		Assert.assertEquals("value-default", store.get("prop", "value-default"));
	}

	@Test
	public void testStoreReadWrite() {
		UserLocalStore store = new InMemoryUserLocalStore();

		MyValueObject mvo = new MyValueObject();

		mvo.bite = 36;
		mvo.chr = 's';
		mvo.shrt = 23;
		mvo.nt = 56;
		mvo.lng = 67;
		mvo.flt = 35.4f;
		mvo.dbl = 99.24d;
		mvo.bln = true;
		mvo.strng = "Hello World";

		mvo.byteArray = new byte[] { 110, 111, 112, 113 };
		mvo.charArray = new char[] { 'a', 'b', 'c', 'd' };
		mvo.shortArray = new short[] { 210, 211, 212, 213 };
		mvo.intArray = new int[] { 10, 11, 12, 13 };
		mvo.longArray = new long[] { 410, 411, 412, 413 };
		mvo.floatArray = new float[] { 10.2f, 11.2f, 12.2f, 13.2f };
		mvo.doubleArray = new double[] { 110.2d, 111.2d, 112.2d, 113.2d };
		mvo.booleanArray = new boolean[] { true, false, true, false };

		store.writeFrom(mvo);

		MyValueObject read = new MyValueObject();
		store.readTo(read);

		// compare both the objects
		Assert.assertEquals(mvo.bite, read.bite);
		Assert.assertEquals(mvo.chr, read.chr);
		Assert.assertEquals(mvo.shrt, read.shrt);
		Assert.assertEquals(mvo.nt, read.nt);
		Assert.assertEquals(mvo.lng, read.lng);
		Assert.assertEquals(mvo.flt, read.flt, 0f);
		Assert.assertEquals(mvo.dbl, read.dbl, 0d);
		Assert.assertEquals(mvo.bln, read.bln);
		Assert.assertEquals(mvo.strng, read.strng);

		Assert.assertArrayEquals(mvo.intArray, read.intArray);
		Assert.assertArrayEquals(mvo.byteArray, read.byteArray);
		Assert.assertArrayEquals(mvo.charArray, read.charArray);
		Assert.assertArrayEquals(mvo.shortArray, read.shortArray);
		Assert.assertArrayEquals(mvo.longArray, read.longArray);
		Assert.assertArrayEquals(mvo.floatArray, read.floatArray, 0f);
		Assert.assertArrayEquals(mvo.doubleArray, read.doubleArray, 0d);
		Assert.assertArrayEquals(mvo.booleanArray, read.booleanArray);
	}

	@Test
	public void testStoreReadWriteAnnotated() {
		UserLocalStore store = new InMemoryUserLocalStore();

		MyValueObjectAnnotated mvo = new MyValueObjectAnnotated();

		mvo.bite = 36;
		mvo.chr = 's';
		mvo.shrt = 23;
		mvo.nt = 56;
		mvo.lng = 67;
		mvo.flt = 35.4f;
		mvo.dbl = 99.24d;
		mvo.bln = true;
		mvo.strng = "Hello World";

		mvo.byteArray = new byte[] { 110, 111, 112, 113 };
		mvo.charArray = new char[] { 'a', 'b', 'c', 'd' };
		mvo.shortArray = new short[] { 210, 211, 212, 213 };
		mvo.intArray = new int[] { 10, 11, 12, 13 };
		mvo.longArray = new long[] { 410, 411, 412, 413 };
		mvo.floatArray = new float[] { 10.2f, 11.2f, 12.2f, 13.2f };
		mvo.doubleArray = new double[] { 110.2d, 111.2d, 112.2d, 113.2d };
		mvo.booleanArray = new boolean[] { true, false, true, false };

		store.writeFrom(mvo);

		MyValueObjectAnnotated read = new MyValueObjectAnnotated();
		store.readTo(read);

		// compare both the objects
		Assert.assertEquals(mvo.bite, read.bite);
		Assert.assertEquals(mvo.chr, read.chr);
		Assert.assertEquals(mvo.shrt, read.shrt);
		Assert.assertEquals(mvo.nt, read.nt);
		Assert.assertEquals(mvo.lng, read.lng);
		Assert.assertEquals(mvo.flt, read.flt, 0f);
		Assert.assertEquals(mvo.dbl, read.dbl, 0d);
		Assert.assertEquals(mvo.bln, read.bln);
		Assert.assertEquals(mvo.strng, read.strng);

		Assert.assertArrayEquals(mvo.intArray, read.intArray);
		Assert.assertArrayEquals(mvo.byteArray, read.byteArray);
		Assert.assertArrayEquals(mvo.charArray, read.charArray);
		Assert.assertArrayEquals(mvo.shortArray, read.shortArray);
		Assert.assertArrayEquals(mvo.longArray, read.longArray);
		Assert.assertArrayEquals(mvo.floatArray, read.floatArray, 0f);
		Assert.assertArrayEquals(mvo.doubleArray, read.doubleArray, 0d);
		Assert.assertArrayEquals(mvo.booleanArray, read.booleanArray);
	}

	private class MyValueObject {

		private byte bite;

		private char chr;

		private short shrt;

		private int nt;

		private long lng;

		private float flt;

		private double dbl;

		private boolean bln;

		private String strng;

		private int[] intArray;

		private char[] charArray;

		private byte[] byteArray;

		private short[] shortArray;

		private long[] longArray;

		private float[] floatArray;

		private double[] doubleArray;

		private boolean[] booleanArray;
	}

	private class MyValueObjectAnnotated {

		@PropertyName("value1")
		private byte bite;

		@PropertyName("value2")
		private char chr;

		@PropertyName("value3")
		private short shrt;

		@PropertyName("value4")
		private int nt;

		@PropertyName("value5")
		private long lng;

		@PropertyName("value6")
		private float flt;

		@PropertyName("value7")
		private double dbl;

		@PropertyName("value8")
		private boolean bln;

		@PropertyName("value9")
		private String strng;

		@PropertyName("int-array")
		private int[] intArray;

		@PropertyName("char-array")
		private char[] charArray;

		@PropertyName("byte-array")
		private byte[] byteArray;

		@PropertyName("short-array")
		private short[] shortArray;

		@PropertyName("long-array")
		private long[] longArray;

		@PropertyName("float-array")
		private float[] floatArray;

		@PropertyName("double-array")
		private double[] doubleArray;

		@PropertyName("boolean-array")
		private boolean[] booleanArray;
	}
}
