package com.javaop.BNetLogin.cdkey;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class Alpha26DecodeTest {

	static final String TEST_KEY = "F8K9B7PDZB44WTMBP2PXNGG2VP";

	private Alpha26Decode decoder;

	public @Before void setup() {
		decoder = (Alpha26Decode) Decode.getDecoder(TEST_KEY);
	}

	public @Test void testGetKeyHash() {
		int expected[] = new int[] { 0x2f8941a9, 0x2be0a05d, 0xd19cfac3, 0x338f742c, 0xa2eac22c};
		assertArrayEquals(expected, decoder.getKeyHash(DecodeTest.TEST_SERVER_TOKEN, DecodeTest.TEST_CLIENT_TOKEN));
	}

	public @Test void testGetProduct() {
		assertEquals(14, decoder.getProduct());
	}

	public @Test void testGetVal1() {
		assertEquals(2391900, decoder.getVal1());
	}

	public @Test void testGetVal2() {
		boolean exceptionThrown = false;
		try {
			decoder.getVal2();
		} catch (Exception ex) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
	}

	public @Test void testGetWar3Val2() {
		byte expected[] = new byte[] {
			(byte) 0x13, (byte) 0x36, (byte) 0xce, (byte) 0xfa, (byte) 0xb2,
			(byte) 0x0b, (byte) 0x94, (byte) 0x6e, (byte) 0xa5, (byte) 0xec
		};
		assertArrayEquals(expected, decoder.getWar3Val2());
	}

}
