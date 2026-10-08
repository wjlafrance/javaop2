package com.javaop.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.junit.Test;

import com.javaop.util.DisconnectReason.Kind;

public class PacketReaderTest {

	private static InputStream bytes(int... values) {
		byte[] b = new byte[values.length];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) values[i];
		}
		return new ByteArrayInputStream(b);
	}

	private static DisconnectReason lost(InputStream in) {
		try {
			PacketReader.read(in);
			fail("expected ConnectionLostException");
			return null;
		} catch (ConnectionLostException e) {
			return e.getReason();
		}
	}

	/** An InputStream that returns its data one byte at a time, then throws. */
	private static InputStream thenThrow(final IOException e, final int... data) {
		return new InputStream() {
			int i = 0;
			@Override public int read() throws IOException {
				if (i < data.length) {
					return data[i++];
				}
				throw e;
			}
		};
	}

	@Test public void readsAPacketEvenWhenDeliveredInPieces() throws Exception {
		BnetPacket p = PacketReader.read(thenThrow(new IOException("unused"), 0xFF, 0x25, 0x08, 0x00, 1, 2, 3, 4));
		assertEquals(0x25, p.getCode());
		assertEquals(0x04030201, p.removeDWord());
	}

	@Test public void emptyBodyPacket() throws Exception {
		assertEquals(0x00, PacketReader.read(bytes(0xFF, 0x00, 0x04, 0x00)).getCode());
	}

	@Test public void eofAtHeaderIsClosedByServer() {
		assertEquals(Kind.CLOSED_BY_SERVER, lost(bytes()).getKind());
		assertEquals(Kind.CLOSED_BY_SERVER, lost(bytes(0xFF, 0x25)).getKind());
	}

	@Test public void eofMidBodyIsClosedByServerNotFilledWithFF() {
		DisconnectReason r = lost(bytes(0xFF, 0x25, 0x08, 0x00, 1, 2));
		assertEquals(Kind.CLOSED_BY_SERVER, r.getKind());
		assertEquals("Connection closed by server", r.getMessage());
		assertTrue(r.isExpected());
	}

	@Test public void badStartByteIsProtocolError() {
		DisconnectReason r = lost(bytes(0x41, 0x25, 0x04, 0x00));
		assertEquals(Kind.PROTOCOL_ERROR, r.getKind());
		assertEquals("Protocol error: packet did not start with 0xFF (it started with 0x41)", r.getMessage());
		assertFalse(r.isExpected());
	}

	@Test public void lengthBelowHeaderSizeIsProtocolError() {
		for (int len = 0; len < 4; len++) {
			assertEquals(Kind.PROTOCOL_ERROR, lost(bytes(0xFF, 0x25, len, 0x00)).getKind());
		}
	}

	@Test public void socketTimeoutIsTimeout() {
		DisconnectReason r = lost(thenThrow(new SocketTimeoutException("Read timed out")));
		assertEquals(Kind.TIMEOUT, r.getKind());
		assertEquals("Read timed out", r.getMessage());
		assertTrue(r.isExpected());
	}

	@Test public void osLevelTimeoutIsTimeout() {
		DisconnectReason r = lost(thenThrow(new SocketException("Operation timed out"), 0xFF, 0x25));
		assertEquals(Kind.TIMEOUT, r.getKind());
		assertEquals("Connection timed out", r.getMessage());
	}

	@Test public void connectionResetIsReset() {
		DisconnectReason r = lost(thenThrow(new SocketException("Connection reset")));
		assertEquals(Kind.RESET, r.getKind());
		assertEquals("Connection reset by peer", r.getMessage());
	}

	@Test public void otherIoErrorKeepsCauseForDebug() {
		IOException cause = new IOException("disk on fire");
		DisconnectReason r = lost(thenThrow(cause));
		assertEquals(Kind.IO_ERROR, r.getKind());
		assertEquals(cause, r.getCause());
		assertFalse(r.isExpected());
	}

	@Test public void readIntoBodyKeepsBytes() throws Exception {
		BnetPacket p = PacketReader.read(bytes(0xFF, 0x0F, 0x06, 0x00, 0xAA, 0xBB));
		byte[] rest = p.removeBytes(2);
		assertArrayEquals(new byte[] { (byte) 0xAA, (byte) 0xBB }, rest);
	}
}
