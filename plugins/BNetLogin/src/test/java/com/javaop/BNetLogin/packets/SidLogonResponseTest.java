package com.javaop.BNetLogin.packets;

import static org.junit.Assert.*;

import org.junit.Test;

import com.javaop.exceptions.LoginException;
import com.javaop.util.BnetPacket;

public class SidLogonResponseTest {

	private static BnetPacket reply(int result) {
		BnetPacket p = new BnetPacket((byte) 0x29);
		p.addDWord(result);
		return p;
	}

	@Test public void resultOneIsSuccess() throws Exception {
		SidLogonResponse.checkIncoming(reply(1));
	}

	@Test public void zeroAndTwoAreRefusals() {
		for (int result : new int[] { 0, 2 }) {
			try {
				SidLogonResponse.checkIncoming(reply(result));
				fail();
			} catch (LoginException e) {
				assertTrue(e.getMessage(), e.getMessage().contains("incorrect password"));
			}
		}
	}

	@Test public void unknownResultIsAnError() {
		try {
			SidLogonResponse.checkIncoming(reply(0x55));
			fail();
		} catch (LoginException e) {
			assertTrue(e.getMessage().contains("0x55"));
		}
	}
}
