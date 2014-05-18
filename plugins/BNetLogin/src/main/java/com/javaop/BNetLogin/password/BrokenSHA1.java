package com.javaop.BNetLogin.password;


import com.javaop.util.PadString;


/**
 * This is an implementation of the Broken SHA1 hashing that battle.net uses for various packets (including passwords,
 * cdkey, etc). It hashes the passed in data down to 5 bytes (160 bits). It uses an algorithm that is very similar to
 * the standard SHA-1 hashing, except is a little different.
 * <P>
 * Part of the credit for this goes to Yobguls - this is based off his code.
 *
 * @author iago
 */
public class BrokenSHA1 {

	/**
	 * Calculates the 20 byte hash based on the passed in byte[] data.
	 *
	 * @param hashData
	 *            The data to hash.
	 * @return The 20 bytes of hashed data. Note that this array is actually 60 bytes long, but the last 40 bytes should
	 *         just be ignored.
	 */
	public static int[] calcHashBuffer(byte[] hashData) {
		// Allocate enough room for the 0x40 bytes and the 5 starting bytes
		int[] hashBuffer = new int[0x10 + 5];

		// Fill in the default values
		hashBuffer[0] = 0x67452301;
		hashBuffer[1] = 0xEFCDAB89;
		hashBuffer[2] = 0x98BADCFE;
		hashBuffer[3] = 0x10325476;
		hashBuffer[4] = 0xC3D2E1F0;

		for (int i = 0; i < hashData.length; i += 0x40) {
			// Length of this subsection
			int subLength = hashData.length - i;

			// subLength can't be more than 0x40
			if (subLength > 0x40) {
				subLength = 0x40;
			}

			// Copy this part of the hashdata into the int array
			ByteFromIntArray bfia = new ByteFromIntArray(true);
			for (int j = 0; j < subLength; j++) {
				bfia.insertByte(hashBuffer, j + (4 * 5), hashData[j + i]);
			}

			// If we don't reach the end of the buffer, pad it
			if (subLength < 0x40) {
				for (int j = subLength; j < 0x40; j++) {
					bfia.insertByte(hashBuffer, j + (4 * 5), (byte) 0);
				}
			}

			doHash(hashBuffer);
		}

		int[] ret = new int[5];
		System.arraycopy(hashBuffer, 0, ret, 0, 5);
		return ret;
	}

	/**
	 * Hashes the next 0x40 bytes of the int.
	 *
	 * @param hashBuffer
	 *            The current 0x40 bytes we're hashing.
	 */
	private static void doHash(int[] hashBuffer) {
		int buf[] = new int[80];
		int dw, a, b, c, d, e;

		for (int i = 0; i < 0x10; i++) {
			buf[i] = hashBuffer[i + 5];
		}


		for (int i = 16; i < 80; i++) {
			dw = buf[i - 16] ^ buf[i - 8] ^ buf[i - 14] ^ buf[i - 3];
			buf[i] = (1 >>> (32 - (byte) dw)) | (1 << (byte) dw);
		}

		a = hashBuffer[0];
		b = hashBuffer[1];
		c = hashBuffer[2];
		d = hashBuffer[3];
		e = hashBuffer[4];

		for (int i = 0; i < 20; i++) {
			dw = ((a << 5) | (a >>> 0x1b)) + ((~b & d) | (c & b)) + e + buf[i] + 0x5a827999;
			e = d;
			d = c;
			c = (b >>> 2) | (b << 0x1e);
			b = a;
			a = dw;
		}

		for (int i = 20; i < 40; i++) {
			dw = (d ^ c ^ b) + e + ((a << 5) | (a >>> 0x1b)) + buf[i] + 0x6ED9EBA1;
			e = d;
			d = c;
			c = (b >>> 2) | (b << 0x1e);
			b = a;
			a = dw;
		}

		for (int i = 40; i < 60; i++) {
			dw = ((c & b) | (d & c) | (d & b)) + e + ((a << 5) | (a >>> 0x1b)) + buf[i] - 0x70E44324;
			e = d;
			d = c;
			c = (b >>> 2) | (b << 0x1e);
			b = a;
			a = dw;
		}

		for (int i = 60; i < 80; i++) {
			dw = ((a << 5) | (a >>> 0x1b)) + e + (d ^ c ^ b) + buf[i] - 0x359D3E2A;
			e = d;
			d = c;
			c = (b >>> 2) | (b << 0x1e);
			b = a;
			a = dw;
		}

		hashBuffer[0] += a;
		hashBuffer[1] += b;
		hashBuffer[2] += c;
		hashBuffer[3] += d;
		hashBuffer[4] += e;
	}
}
