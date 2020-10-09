package com.javaop.BNetLogin.cdkey;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class Alpha16DecodeTest {

	static final String TEST_KEY = "74WB-HR8P-4B22-DPEG";

	private Alpha16Decode decoder;

	public @Before void setup() {
		decoder = (Alpha16Decode) Decode.getDecoder(TEST_KEY);
	}

	public @Test void testGetKeyHash() {
		int expected[] = new int[] { 0x96aab48, 0x4012ab89, 0x81bd3fc, 0xe161d248, 0x70cfbc33 };
		assertArrayEquals(expected, decoder.getKeyHash(DecodeTest.TEST_SERVER_TOKEN, DecodeTest.TEST_CLIENT_TOKEN));
	}

	public @Test void testGetProduct() {
		assertEquals(4, decoder.getProduct());
	}

	public @Test void testGetVal1() {
		assertEquals(1637902, decoder.getVal1());
	}

	public @Test void testGetVal2() {
		assertEquals(299034128, decoder.getVal2());
	}

}