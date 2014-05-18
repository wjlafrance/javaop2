package com.javaop.BNetLogin.password;

import org.junit.Test;

import static org.junit.Assert.*;

public class DoubleHashTest {

	private static final String TEST_PASSWORD = "correct horse battery staple";
	private static final int TEST_SERVER_TOKEN = 0x12345678;
	private static final int TEST_CLIENT_TOKEN = 0x87654321;

	public @Test void testDoubleHash() {
		int[] expected = new int[] { 0xB20C7E2C, 0x89D7AD87, 0xB4C78A, 0xFED5EC95, 0xD2741B01 };
		assertArrayEquals(expected, DoubleHash.doubleHash(TEST_PASSWORD, TEST_CLIENT_TOKEN, TEST_SERVER_TOKEN));
	}

}