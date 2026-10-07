package com.javaop.util;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.junit.Test;

import static org.junit.Assert.*;

public class TimeoutSocketTest {

	@Test
	public void connectsToListeningLoopbackServer() throws Exception {
		try (ServerSocket server = new ServerSocket(0)) {
			try (Socket s = TimeoutSocket.getSocket("127.0.0.1", server.getLocalPort(), 5000)) {
				assertTrue(s.isConnected());
				assertEquals(server.getLocalPort(), s.getPort());
			}
		}
	}

	@Test
	public void refusedConnectionBecomesSocketException() throws Exception {
		int port;
		try (ServerSocket server = new ServerSocket(0)) {
			port = server.getLocalPort();
		}
		try {
			TimeoutSocket.getSocket("127.0.0.1", port, 5000);
			fail("expected SocketException");
		} catch (SocketException expected) {
			assertTrue(expected.getMessage().contains("ConnectException"));
		}
	}
}
