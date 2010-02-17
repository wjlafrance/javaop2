/*
 * Numeric13Decode.java
 * 
 * Created on June 12, 2008 22:13
 */

package cdkey;

import util.Buffer;
import password.BrokenSHA1;

/**
 * This is the CDKey Decoder used for decoding legacy StarCraft keys.
 * 
 * @author iago, joe
 */
class Num13Decode extends Decode
{

    protected String cdkey;

    public Num13Decode(String cdkey) throws IllegalArgumentException {
        this.cdkey = cdkey;
        if(cdkey == null || cdkey.isEmpty())
            throw new IllegalArgumentException("CD-Key is missing!");

        if(cdkey.length() != 13)
            throw new IllegalArgumentException("CDKey is not 13 characters!");

        if(verify() == false)
            throw new IllegalArgumentException("CDKey is invalid: did not pass local checks");

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
    public int[] getKeyHash(int clientToken, int serverToken) {
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
    protected boolean verify() {
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
    protected static String swap(String s, int a, int b) {
        byte[] tempStr = s.getBytes();

        byte temp = tempStr[a];
        tempStr[a] = tempStr[b];
        tempStr[b] = temp;

        return new String(tempStr);

    }

    /** Does the CDKey shuffle. */
    protected void shuffle() {
        int position = 0x0B;

        for(int i = 0xC2; i >= 0x07; i -= 0x11)
            cdkey = swap(cdkey, position--, i % 0x0C);
    }


    /** Gets the final CDKey values. */
    protected void getFinalValue() {
        int hashKey = 0x13AC9741;

        byte[] key = cdkey.getBytes();

        for(int i = (cdkey.length() - 2); i >= 0; i--) {
            if(key[i] <= '7') {
                key[i] ^= (byte) (hashKey & 7);
                hashKey = hashKey >>> 3;
            } else if(key[i] < 'A') {
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
    public int getProduct() {
        return Integer.parseInt(cdkey.substring(0, 2));
    }

    /**
     * Gets the second CDKey value.
     * 
     * @return The second CDKey value.
     */
    public int getVal2() {
        return Integer.parseInt(cdkey.substring(9, 12));
    }

    /**
     * Gets the first CDKey value.
     * 
     * @return The first CDKey value.
     */
    public int getVal1() {
        return Integer.parseInt(cdkey.substring(2, 9));
    }

    public String toString() {
        return "13-character numeric decoder";
    }
}
