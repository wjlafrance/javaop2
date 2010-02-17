/*
 * AlphaKeyDecode.java
 * 
 * Created on April 5, 2004, 3:34 PM
 */

package cdkey;

import util.Buffer;
import password.BrokenSHA1;

/**
 * This is the CDKey Decoder used for decoding alphabetic keys - Warcraft 2, Diablo 2, etc.
 * 
 * @author iago, Feanor, wjlafrance
 */
class Alpha16Decode extends Decode {
    
    private String cdkey;
    
    int[] alpha16Map = {
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0x00, 0xFF, 0x01, 0xFF, 0x02, 0x03, 0x04, 0x05, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B,
         0x0C, 0xFF, 0x0D, 0x0E, 0xFF, 0x0F, 0x10, 0xFF, 0x11, 0xFF, 0x12, 0xFF,
         0x13, 0xFF, 0x14, 0x15, 0x16, 0xFF, 0x17, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0xFF, 0x0D, 0x0E,
         0xFF, 0x0F, 0x10, 0xFF, 0x11, 0xFF, 0x12, 0xFF, 0x13, 0xFF, 0x14, 0x15,
         0x16, 0xFF, 0x17, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
         0xFF, 0xFF, 0xFF, 0xFF
     };
     
    private int checksum = 0;

    protected Alpha16Decode(String cdkey) throws IllegalArgumentException {
        this.cdkey = cdkey;

        if(cdkey == null || cdkey.isEmpty())
            throw new IllegalArgumentException("CD-Key is missing!");

        if(cdkey.length() != 16)
            throw new IllegalArgumentException("CDKey is not 16 characters!");

        hash();
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

    private void hash() throws IllegalArgumentException {
        checksum = 0;
        char[] keyarray = cdkey.toCharArray();
        int r, n, n2, v, v2;
        byte c1, c2, c;

        for(int i = 0; i < cdkey.length(); i += 2) {
            r = 1;
            c1 = (byte) alpha16Map[cdkey.charAt(i)];
            n = c1 * 3;
            c2 = (byte) alpha16Map[cdkey.charAt(i + 1)];
            n = c2 + (n * 8);

            if(n >= 0x100) {
                n -= 0x100;
                checksum = checksum | (int) Math.pow(2, (i / 2));
            }
            n2 = n;
            n2 >>= 4;
            keyarray[i] = getHexVal(n2);
            keyarray[i + 1] = getHexVal(n);
            r <<= 1;
        }

        v = 3;
        for(int i = 0; i < 16; i++) {
            c = (byte) keyarray[i];
            n = getNumVal((char) c);
            n2 = v * 2;
            n ^= n2;
            v += n;
        }

        v &= 0xFF;
        if(v != checksum)
            throw new IllegalArgumentException("CD-Key is invalid.");

        for(int i = 15; i >= 0; i--) {
            c = (byte) keyarray[i];
            if(i > 8)
                n = i - 9;
            else
                n = 0xF - (8 - i);
            n &= 0xF;
            c2 = (byte) keyarray[n];
            keyarray[i] = (char) c2;
            keyarray[n] = (char) c;
        }
        v2 = 0x13AC9741;

        for(int i = 15; i >= 0; i--) {
            c = (byte) Character.toUpperCase(keyarray[i]);
            keyarray[i] = (char) c;
            if(c <= '7') {
                v = v2;
                c2 = (byte) (v & 0xFF);
                c2 &= 7;
                c2 ^= c;
                v >>= 3;
                keyarray[i] = (char) c2;
                v2 = v;
            } else if(c < 'A') {
                c2 = (byte) i;
                c2 &= 1;
                c2 ^= c;
                keyarray[i] = (char) c2;
            }
        }

        cdkey = new String(keyarray);
    }

    private char getHexVal(int v) {
        v &= 0xF;
        if(v < 10)
            return (char) (v + 0x30);
        return (char) (v + 0x37);
    }

    private int getNumVal(char c) {
        c = Character.toUpperCase(c);
        if(Character.isDigit(c))
            return (c - 0x30);
        return (c - 0x37);
    }

    public int getProduct() {
        return Integer.parseInt(cdkey.substring(0, 2), 16);
    }

    public int getVal1() {
        return (int) Long.parseLong(cdkey.substring(2, 8), 16);
    }

    public int getVal2() {
        return (int) Long.parseLong(cdkey.substring(8, 16), 16);
    }

    public String toString() {
        return "16-character alphanumeric decoder";
    }

}
