/*
 * Decode.java
 *
 * Created on March 17, 2004, 8:14 AM
 */

package com.javaop.BNetLogin.cdkey;

/**
 * This class takes a CD-Key, determines it's type, and returns a decoder
 * (class that extends this class) appropriate for the key type. The below
 * methods are dummy methods, but serve as an interface for the other classes.
 * As of 09/15/2009 these are:
 *
 * Num13Decode - Legacy StarCraft keys
 * Alpha16Decode - Legacy Diablo II, WarCraft II keys
 * Alpha26Decode - All keyed products except WarCraft II
 *
 * @author iago, joe
 */
public class Decode {

	/**
	 * Created a new CD-Key decoder
	 *
	 * @param cdkey The CDKey to decode
	 * @return CDKeyDecode instance with the decoded details for cdkey
	 * @throws IllegalArgumentException
	 *      If the key isn't 13, 16, or 24 characters long
	 */
	public static Decode getDecoder(String cdkey) throws IllegalArgumentException {
		if(cdkey == null || cdkey.length() == 0)
			throw new IllegalArgumentException("CD-Key is missing!");

		switch(cdkey.length()) {
			case 13: // Legacy StarCraft
				return new Num13Decode(cdkey);
			case 16: // Legacy Diablo II, current WarCraft II
				return new Alpha16Decode(cdkey);
			case 26: // All products except WarCraft II
				return new Alpha26Decode(cdkey);
			default:
				throw new IllegalArgumentException("CD-Key type cannot be determined: " + cdkey.toString());
		}
	}

	/**
	 * Calculates 20 byte CD-Key hash, made by hashing the client token, server token, product value, value 1, zero, and
	 * value 2 together as DWORDs.
	 *
	 * @param serverToken
	 *            Server token received in SID_AUTH_INFO.
	 * @param clientToken
	 *            Generated client token sent in SID_AUTH_CHECK.
	 * @return (DWORD[5]) CD-Key hash for SID_AUTH_CHECK.
	 */
	public int[] getKeyHash(int serverToken, int clientToken) {
		return new int[] { 0, 0, 0, 0, 0 };
	}

	/**
	 * @return Key's product value
	 */
	public int getProduct() {
		return 0;
	}

	/**
	 * @return Key's value1
	 */
	public int getVal1() {
		return 0;
	}

	/**
	 * @return Key's value2
	 */
	public int getVal2() {
		return 0;
	}

}
