package com.javaop.util;

import java.io.IOException;
import java.io.InputStream;

import com.javaop.util.DisconnectReason.Kind;

/** Reads whole BNCS packets (0xFF, id, uint16 length including the 4 header bytes, body) from a stream. */
public final class PacketReader {

	private PacketReader() {
	}

	/**
	 * Reads exactly one packet.
	 *
	 * @throws ConnectionLostException EOF at any point (CLOSED_BY_SERVER), a bad start byte or length
	 *         (PROTOCOL_ERROR), or the classified form of any other IOException from the stream.
	 */
	public static BnetPacket read(InputStream in) throws ConnectionLostException {
		try {
			byte[] header = new byte[4];
			readFully(in, header);

			int start = header[0] & 0xFF;
			if (start != 0xFF) {
				throw new ConnectionLostException(new DisconnectReason(Kind.PROTOCOL_ERROR,
						"Protocol error: packet did not start with 0xFF (it started with 0x"
						+ Integer.toHexString(start) + ")", null));
			}

			int length = (header[2] & 0xFF) | ((header[3] & 0xFF) << 8);
			if (length < 4) {
				throw new ConnectionLostException(new DisconnectReason(Kind.PROTOCOL_ERROR,
						"Protocol error: bad packet length " + length, null));
			}

			byte[] body = new byte[length - 4];
			readFully(in, body);

			BnetPacket packet = new BnetPacket(header[1]);
			packet.add(body);
			return packet;
		} catch (ConnectionLostException e) {
			throw e;
		} catch (IOException e) {
			throw new ConnectionLostException(DisconnectReason.of(e));
		}
	}

	private static void readFully(InputStream in, byte[] buf) throws IOException {
		int off = 0;
		while (off < buf.length) {
			int n = in.read(buf, off, buf.length - off);
			if (n < 0) {
				throw new ConnectionLostException(new DisconnectReason(Kind.CLOSED_BY_SERVER,
						"Connection closed by server", null));
			}
			off += n;
		}
	}
}
