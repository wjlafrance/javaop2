package com.javaop.BNetLogin.password;

import org.junit.Test;

import static org.junit.Assert.*;

public class BrokenSHA1Test {

	private static final byte[] TEST_INPUT = "This is a test of X-SHA-1".getBytes();

	public @Test void testCalcHashBuffer() {
		int[] expected = new int[] { 0xDBA341F5, 0x9EE4611F, 0xD33A0B6C, 0xA9A46601, 0xA7F678BA };
		assertArrayEquals(expected, BrokenSHA1.calcHashBuffer(TEST_INPUT));
	}

}
