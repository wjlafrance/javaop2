/*
 * CDKeyDecode.java
 * 
 * Created on March 17, 2004, 8:14 AM
 */

package cdkey;

import password.BrokenSHA1;
import util.Buffer;
import exceptions.InvalidCDKey;

/** This decodes a standard numeric CDKey (Starcraft's) for sending to battle.net. */
public class CDKeyDecode
{

	protected String cdkey;

	public static CDKeyDecode getInstance(String cdkey) throws InvalidCDKey
	{
		if(cdkey == null || cdkey.length() == 0)
			throw new InvalidCDKey("Your CDKey wasn't filled in");

		if(cdkey.length() == 13) // Starcraft
			return new CDKeyDecode(cdkey);
		else if(cdkey.length() == 16) // Warcraft 2 / Diablo 2
			return new AlphaKeyDecode(cdkey);
		else if(cdkey.length() == 26) // Warcraft 3
			return new War3Decode(cdkey);

		throw new InvalidCDKey("Could not determine the type of key -- valid keys are 13, 16, or 26 characters long");
	}

	protected CDKeyDecode()
	{
	}

	protected CDKeyDecode(String cdkey) throws InvalidCDKey
	{
		this.cdkey = cdkey;
		if(cdkey == null || cdkey.length() == 0)
			throw new InvalidCDKey("CDKey not filled in");

		if(cdkey.length() != 13)
			throw new InvalidCDKey("CDKey is invalid length");

		if(verify() == false)
			throw new InvalidCDKey("CDKey is invalid: did not pass local checks");

		shuffle();
		getFinalValue();
	}

	/**
	 * Hashes the CDKey based on the client and server token, and returns the 5-byte hash.
	 * 
	 * @param clientToken
	 *            The client token to hash it with.
	 * @param serverToken
	 *            The server token to hash it with.
	 * @return The 20-byte hash (5 ints).
	 */
	public int[] getKeyHash(int clientToken, int serverToken)
	{
		Buffer hashData = new Buffer();

		hashData.addDWord(clientToken);
		hashData.addDWord(serverToken);
		hashData.addDWord(getProduct());
		hashData.addDWord(getVal1());
		hashData.addDWord(0);
		hashData.addDWord(getVal2());

		return BrokenSHA1.calcHashBuffer(hashData.getBytes());
	}


	/**
	 * Verifies that the CDKey is valid.
	 * 
	 * @return true if the CDKey is valid.<BR>
	 *         false if the CDKey is invalid.
	 */
	protected boolean verify()
	{
		int accum = 3;
		cdkey = cdkey.toLowerCase();

		for(int i = 0; i < (cdkey.length() - 1); i++)
			accum += ((cdkey.charAt(i) - '0') ^ (accum * 2));

		return ((accum % 10) == (cdkey.charAt(12) - '0'));
	}

	/**
	 * Swap two characters in a String. This was the best implementation I could think of, but it's pretty bad.
	 * 
	 * @param s
	 *            The string.
	 * @param a
	 *            The index of the first character to swap.
	 * @param b
	 *            The index of the second character to swap.
	 * @return The String with the characters swapped.
	 */
	protected static String swap(String s, int a, int b)
	{
		byte[] tempStr = s.getBytes();

		byte temp = tempStr[a];
		tempStr[a] = tempStr[b];
		tempStr[b] = temp;

		return new String(tempStr);

	}

	/** Does the CDKey shuffle. */
	protected void shuffle()
	{
		int position = 0x0B;

		for(int i = 0xC2; i >= 0x07; i -= 0x11)
			cdkey = swap(cdkey, position--, i % 0x0C);
	}


	/** Gets the final CDKey values. */
	protected void getFinalValue()
	{
		int hashKey = 0x13AC9741;

		byte[] key = cdkey.getBytes();

		for(int i = (cdkey.length() - 2); i >= 0; i--)
		{
			if(key[i] <= '7')
			{
				key[i] ^= (byte) (hashKey & 7);
				hashKey = hashKey >>> 3;
			}
			else if(key[i] < 'A')
			{
				key[i] ^= (byte) (i & 1);
			}
		}


		cdkey = new String(key);
	}

	/**
	 * Gets the game's product.
	 * 
	 * @return The game's product.
	 */
	public int getProduct()
	{
		return Integer.parseInt(cdkey.substring(0, 2));
	}

	/**
	 * Gets the second CDKey value.
	 * 
	 * @return The second CDKey value.
	 */
	public int getVal2()
	{
		return Integer.parseInt(cdkey.substring(9, 12));
	}

	/**
	 * Gets the first CDKey value.
	 * 
	 * @return The first CDKey value.
	 */
	public int getVal1()
	{
		return Integer.parseInt(cdkey.substring(2, 9));
	}

	public static void main(String[] args)
	{
		for(int i = 0; i < 10; i++)
		{
			String s = "";
			for(int j = 0; j < 13; j++)
				s = s + i;

			try
			{
				getInstance(s);
				System.out.println(s + " is valid!");
			}
			catch(Exception e)
			{
				System.out.println(s + " is invalid (" + e + ")");
			}
		}
	}
}
