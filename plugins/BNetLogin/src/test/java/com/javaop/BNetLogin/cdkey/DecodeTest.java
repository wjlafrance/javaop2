package com.javaop.BNetLogin.cdkey;

import org.junit.Test;

import static org.junit.Assert.*;

public class DecodeTest {

	static final int TEST_SERVER_TOKEN = 0x12345678;
	static final int TEST_CLIENT_TOKEN = 0x87654321;

	public @Test void testGetDecoder() {
		Decode num13decoder = Decode.getDecoder(Num13DecodeTest.TEST_KEY);
		assertEquals(Num13Decode.class, num13decoder.getClass());

		Decode alpha16decoder = Decode.getDecoder(Alpha16DecodeTest.TEST_KEY);
		assertEquals(Alpha16Decode.class, alpha16decoder.getClass());

		Decode alpha26decoder = Decode.getDecoder(Alpha26DecodeTest.TEST_KEY);
		assertEquals(Alpha26Decode.class, alpha26decoder.getClass());
	}

}
