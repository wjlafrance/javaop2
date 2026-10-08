package com.javaop.SwingGui.gui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ChannelListTest {

	@Test public void clearRemovesEveryUser() throws Exception {
		System.setProperty("java.awt.headless", "true");
		ChannelList list = new ChannelList(true);
		list.addUser("]sV[HacK", "W2BN", "", 40, 0);
		list.addUser("[vL]Fusion", "W2BN", "", 40, 0);
		list.addUser("joe[x86]", "STAR", "", 40, 0);
		assertEquals(3, list.length());

		list.clear();

		assertEquals(0, list.length());
		assertEquals(0, list.getRowCount());
	}
}
