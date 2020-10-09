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
public abstract class Decode {

	/**
	 * Created a new CD-Key decoder
	 *
	 * @param cdkey The CDKey to decode
	 * @return CDKeyDecode instance with the decoded details for cdkey
	 * @throws IllegalArgumentException
	 *      If the key isn't 13, 16, or 26 characters long
	 */
	public static Decode getDecoder(String cdkey) throws IllegalArgumentException {
		String processedKey = cdkey.replaceAll("[^A-Za-z0-9]", "");
		
		if (null == processedKey || processedKey.length() == 0) {
			throw new IllegalArgumentException("CD-Key is missing!");
		}

		switch (processedKey.length()) {
			case Num13Decode.KEY_LENGTH: // Legacy StarCraft
				return new Num13Decode(processedKey);
			case Alpha16Decode.KEY_LENGTH: // Legacy Diablo II, current WarCraft II
				return new Alpha16Decode(processedKey);
			case Alpha26Decode.KEY_LENGTH: // All products except WarCraft II
				return new Alpha26Decode(processedKey);
			default:
				throw new IllegalArgumentException("CD-Key type cannot be determined: " + processedKey);
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
	public abstract int[] getKeyHash(int serverToken, int clientToken);

	/**
	 * @return Key's product value
	 */
	public abstract int getProduct();

	/**
	 * @return Key's value1
	 */
	public abstract int getVal1();

	/**
	 * @return Key's value2
	 */
	public abstract int getVal2();

}
