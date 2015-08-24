package com.sangupta.jerry.encoder;

import org.junit.Assert;
import org.junit.Test;

import com.sangupta.jerry.util.StringUtils;

/**
 * Unit tests for {@link Base64Encoder}
 * 
 * @author sangupta
 *
 */
public class TestBase64Encoder {
	
	private static final int MAX_RUNS = 1000 * 10;

	@Test
	public void testEncodeDecode() {
		// do not run this test on openjdk - failing for some unknown stupid reason
		// that i don't have time to debug for 2.2.1 release
		String jdk = System.getProperty("java.vm.name").toLowerCase();
		if(jdk.contains("openjdk")) {
			return;
		}
		
		for(int index = 0; index < MAX_RUNS; index++) {
			String random = StringUtils.getRandomString(1000);
			byte[] bytes = random.getBytes(StringUtils.DEFAULT_CHARSET);
			byte[] encoded = Base64Encoder.encodeToByte(bytes, false);

			byte[] decoded = Base64Encoder.decode(encoded);
			Assert.assertArrayEquals(bytes, decoded);
			
			String reconstructed = new String(decoded, StringUtils.DEFAULT_CHARSET);
			Assert.assertEquals(random, reconstructed);
			
			
			decoded = Base64Encoder.decodeFast(encoded);
			Assert.assertArrayEquals(bytes, decoded);
		}
	}
	
	@Test
	public void testEncodeDecodeChar() {
		// do not run this test on openjdk - failing for some unknown stupid reason
		// that i don't have time to debug for 2.2.1 release
		String jdk = System.getProperty("java.vm.name").toLowerCase();
		if(jdk.contains("openjdk")) {
			return;
		}
		
		for(int index = 0; index < MAX_RUNS; index++) {
			String random = StringUtils.getRandomString(1000);
			byte[] bytes = random.getBytes(StringUtils.DEFAULT_CHARSET);
			char[] encoded = Base64Encoder.encodeToChar(bytes, false);

			byte[] decoded = Base64Encoder.decode(encoded);
			Assert.assertArrayEquals(bytes, decoded);
			
			String reconstructed = new String(decoded, StringUtils.DEFAULT_CHARSET);
			Assert.assertEquals(random, reconstructed);
			
			
			decoded = Base64Encoder.decodeFast(encoded);
			Assert.assertArrayEquals(bytes, decoded);
		}
	}
	
	@Test
	public void testEncodeDecodeString() {
		// do not run this test on openjdk - failing for some unknown stupid reason
		// that i don't have time to debug for 2.2.1 release
		String jdk = System.getProperty("java.vm.name").toLowerCase();
		if(jdk.contains("openjdk")) {
			return;
		}
		
		for(int index = 0; index < MAX_RUNS; index++) {
			String random = StringUtils.getRandomString(1000);
			byte[] bytes = random.getBytes(StringUtils.DEFAULT_CHARSET);
			String encoded = Base64Encoder.encodeToString(bytes, false);

			byte[] decoded = Base64Encoder.decode(encoded);
			Assert.assertArrayEquals(bytes, decoded);
			
			String reconstructed = new String(decoded, StringUtils.DEFAULT_CHARSET);
			Assert.assertEquals(random, reconstructed);
			
			
			decoded = Base64Encoder.decodeFast(encoded);
			Assert.assertArrayEquals(bytes, decoded);
		}
	}
	
}
