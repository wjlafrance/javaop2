package com.javaop.BNetLogin.cdkey;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class Num13DecodeTest {

	static final String TEST_KEY = "2402752278792";

	private Num13Decode decoder;

	public @Before void setup() {
		decoder = (Num13Decode) Decode.getDecoder(TEST_KEY);
	}

	public @Test void testGetKeyHash() {
		int expected[] = new int[] { 0x59e2d093, 0x44a3a80f, 0x6d8be951, 0x46a33e7a, 0xe94a4455 };
		assertArrayEquals(expected, decoder.getKeyHash(DecodeTest.TEST_SERVER_TOKEN, DecodeTest.TEST_CLIENT_TOKEN));
	}

	public @Test void testGetProduct() {
		assertEquals(1, decoder.getProduct());
	}

	public @Test void testGetVal1() {
		assertEquals(5918536, decoder.getVal1());
	}

	public @Test void testGetVal2() {
		assertEquals(276, decoder.getVal2());
	}

	public @Test void testVerify() {
		boolean exceptionThrown = false;
		try {
			new Num13Decode("0000000000001").verify();
		} catch (Exception ex) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);

		assertTrue(new Num13Decode("0000000000003").verify());
	}

}
