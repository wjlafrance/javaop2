/*
 * DoubleHash.java
 * 
 * Created on April 13, 2004, 12:55 PM
 */

package com.javaop.BNetLogin.password;

import com.javaop.util.Buffer;

/**
 * This class does the double-hash for passwords. It hashes them alone, then it hashes them again along with the client
 * and server tokens.
 *
 * H(C : S : H(P))
 * 
 * @author iago
 */
public class DoubleHash {

	/**
	 * This static method does the actual doublehash.
	 * 
	 * @param str The string we're doublehashing.
	 * @param clientToken The client token for this session.
	 * @param serverToken The server token for this session.
	 * @return The 5-DWord (20 byte) hash.
	 */
	public static int[] doubleHash(String str, int clientToken, int serverToken) {
		Buffer initialHash = new Buffer();
		initialHash.addNTString(str);
		int[] firstHash = BrokenSHA1.calcHashBuffer(initialHash.getBytes());

		Buffer secondHash = new Buffer();
		secondHash.add(clientToken);
		secondHash.add(serverToken);
		for (int i : firstHash) {
			secondHash.add(i);
		}

		return BrokenSHA1.calcHashBuffer(secondHash.getBytes());
	}
}
