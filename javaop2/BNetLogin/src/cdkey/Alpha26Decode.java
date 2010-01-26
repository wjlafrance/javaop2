/*
 * Alpha26Decode.java
 * 
 * Created on May 21, 2004, 3:23 AM
 */

package cdkey;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import password.ByteFromIntArray;
import password.IntFromByteArray;

import exceptions.LoginException;

/**
 * 
 * @author iago, wjlafrance
 */
class Alpha26Decode extends Decode
{

    public final static byte[] KeyTable = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0xFF, (byte) 0x01,
            (byte) 0x05, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C,
            (byte) 0xFF, (byte) 0x0D, (byte) 0x0E, (byte) 0xFF, (byte) 0x0F, (byte) 0x10, (byte) 0xFF, (byte) 0x11,
            (byte) 0xFF, (byte) 0x12, (byte) 0xFF, (byte) 0x13, (byte) 0xFF, (byte) 0x14, (byte) 0x15, (byte) 0x16,
            (byte) 0x17, (byte) 0x18, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C,
            (byte) 0xFF, (byte) 0x0D, (byte) 0x0E, (byte) 0xFF, (byte) 0x0F, (byte) 0x10, (byte) 0xFF, (byte) 0x11,
            (byte) 0xFF, (byte) 0x12, (byte) 0xFF, (byte) 0x13, (byte) 0xFF, (byte) 0x14, (byte) 0x15, (byte) 0x16,
            (byte) 0x17, (byte) 0x18, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };

    public final static byte[] TranslateTable = { (byte) 0x09, (byte) 0x04, (byte) 0x07, (byte) 0x0F, (byte) 0x0D,
            (byte) 0x0A, (byte) 0x03, (byte) 0x0B, (byte) 0x01, (byte) 0x02, (byte) 0x0C, (byte) 0x08, (byte) 0x06,
            (byte) 0x0E, (byte) 0x05, (byte) 0x00, (byte) 0x09, (byte) 0x0B, (byte) 0x05, (byte) 0x04, (byte) 0x08,
            (byte) 0x0F, (byte) 0x01, (byte) 0x0E, (byte) 0x07, (byte) 0x00, (byte) 0x03, (byte) 0x02, (byte) 0x0A,
            (byte) 0x06, (byte) 0x0D, (byte) 0x0C, (byte) 0x0C, (byte) 0x0E, (byte) 0x01, (byte) 0x04, (byte) 0x09,
            (byte) 0x0F, (byte) 0x0A, (byte) 0x0B, (byte) 0x0D, (byte) 0x06, (byte) 0x00, (byte) 0x08, (byte) 0x07,
            (byte) 0x02, (byte) 0x05, (byte) 0x03, (byte) 0x0B, (byte) 0x02, (byte) 0x05, (byte) 0x0E, (byte) 0x0D,
            (byte) 0x03, (byte) 0x09, (byte) 0x00, (byte) 0x01, (byte) 0x0F, (byte) 0x07, (byte) 0x0C, (byte) 0x0A,
            (byte) 0x06, (byte) 0x04, (byte) 0x08, (byte) 0x06, (byte) 0x02, (byte) 0x04, (byte) 0x05, (byte) 0x0B,
            (byte) 0x08, (byte) 0x0C, (byte) 0x0E, (byte) 0x0D, (byte) 0x0F, (byte) 0x07, (byte) 0x01, (byte) 0x0A,
            (byte) 0x00, (byte) 0x03, (byte) 0x09, (byte) 0x05, (byte) 0x04, (byte) 0x0E, (byte) 0x0C, (byte) 0x07,
            (byte) 0x06, (byte) 0x0D, (byte) 0x0A, (byte) 0x0F, (byte) 0x02, (byte) 0x09, (byte) 0x01, (byte) 0x00,
            (byte) 0x0B, (byte) 0x08, (byte) 0x03, (byte) 0x0C, (byte) 0x07, (byte) 0x08, (byte) 0x0F, (byte) 0x0B,
            (byte) 0x00, (byte) 0x05, (byte) 0x09, (byte) 0x0D, (byte) 0x0A, (byte) 0x06, (byte) 0x0E, (byte) 0x02,
            (byte) 0x04, (byte) 0x03, (byte) 0x01, (byte) 0x03, (byte) 0x0A, (byte) 0x0E, (byte) 0x08, (byte) 0x01,
            (byte) 0x0B, (byte) 0x05, (byte) 0x04, (byte) 0x02, (byte) 0x0F, (byte) 0x0D, (byte) 0x0C, (byte) 0x06,
            (byte) 0x07, (byte) 0x09, (byte) 0x00, (byte) 0x0C, (byte) 0x0D, (byte) 0x01, (byte) 0x0F, (byte) 0x08,
            (byte) 0x0E, (byte) 0x05, (byte) 0x0B, (byte) 0x03, (byte) 0x0A, (byte) 0x09, (byte) 0x00, (byte) 0x07,
            (byte) 0x02, (byte) 0x04, (byte) 0x06, (byte) 0x0D, (byte) 0x0A, (byte) 0x07, (byte) 0x0E, (byte) 0x01,
            (byte) 0x06, (byte) 0x0B, (byte) 0x08, (byte) 0x0F, (byte) 0x0C, (byte) 0x05, (byte) 0x02, (byte) 0x03,
            (byte) 0x00, (byte) 0x04, (byte) 0x09, (byte) 0x03, (byte) 0x0E, (byte) 0x07, (byte) 0x05, (byte) 0x0B,
            (byte) 0x0F, (byte) 0x08, (byte) 0x0C, (byte) 0x01, (byte) 0x0A, (byte) 0x04, (byte) 0x0D, (byte) 0x00,
            (byte) 0x06, (byte) 0x09, (byte) 0x02, (byte) 0x0B, (byte) 0x06, (byte) 0x09, (byte) 0x04, (byte) 0x01,
            (byte) 0x08, (byte) 0x0A, (byte) 0x0D, (byte) 0x07, (byte) 0x0E, (byte) 0x00, (byte) 0x0C, (byte) 0x0F,
            (byte) 0x02, (byte) 0x03, (byte) 0x05, (byte) 0x0C, (byte) 0x07, (byte) 0x08, (byte) 0x0D, (byte) 0x03,
            (byte) 0x0B, (byte) 0x00, (byte) 0x0E, (byte) 0x06, (byte) 0x0F, (byte) 0x09, (byte) 0x04, (byte) 0x0A,
            (byte) 0x01, (byte) 0x05, (byte) 0x02, (byte) 0x0C, (byte) 0x06, (byte) 0x0D, (byte) 0x09, (byte) 0x0B,
            (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x0F, (byte) 0x07, (byte) 0x03, (byte) 0x04, (byte) 0x0A,
            (byte) 0x0E, (byte) 0x08, (byte) 0x05, (byte) 0x03, (byte) 0x06, (byte) 0x01, (byte) 0x05, (byte) 0x0B,
            (byte) 0x0C, (byte) 0x08, (byte) 0x00, (byte) 0x0F, (byte) 0x0E, (byte) 0x09, (byte) 0x04, (byte) 0x07,
            (byte) 0x0A, (byte) 0x0D, (byte) 0x02, (byte) 0x0A, (byte) 0x07, (byte) 0x0B, (byte) 0x0F, (byte) 0x02,
            (byte) 0x08, (byte) 0x00, (byte) 0x0D, (byte) 0x0E, (byte) 0x0C, (byte) 0x01, (byte) 0x06, (byte) 0x09,
            (byte) 0x03, (byte) 0x05, (byte) 0x04, (byte) 0x0A, (byte) 0x0B, (byte) 0x0D, (byte) 0x04, (byte) 0x03,
            (byte) 0x08, (byte) 0x05, (byte) 0x09, (byte) 0x01, (byte) 0x00, (byte) 0x0F, (byte) 0x0C, (byte) 0x07,
            (byte) 0x0E, (byte) 0x02, (byte) 0x06, (byte) 0x0B, (byte) 0x04, (byte) 0x0D, (byte) 0x0F, (byte) 0x01,
            (byte) 0x06, (byte) 0x03, (byte) 0x0E, (byte) 0x07, (byte) 0x0A, (byte) 0x0C, (byte) 0x08, (byte) 0x09,
            (byte) 0x02, (byte) 0x05, (byte) 0x00, (byte) 0x09, (byte) 0x06, (byte) 0x07, (byte) 0x00, (byte) 0x01,
            (byte) 0x0A, (byte) 0x0D, (byte) 0x02, (byte) 0x03, (byte) 0x0E, (byte) 0x0F, (byte) 0x0C, (byte) 0x05,
            (byte) 0x0B, (byte) 0x04, (byte) 0x08, (byte) 0x0D, (byte) 0x0E, (byte) 0x05, (byte) 0x06, (byte) 0x01,
            (byte) 0x09, (byte) 0x08, (byte) 0x0C, (byte) 0x02, (byte) 0x0F, (byte) 0x03, (byte) 0x07, (byte) 0x0B,
            (byte) 0x04, (byte) 0x00, (byte) 0x0A, (byte) 0x09, (byte) 0x0F, (byte) 0x04, (byte) 0x00, (byte) 0x01,
            (byte) 0x06, (byte) 0x0A, (byte) 0x0E, (byte) 0x02, (byte) 0x03, (byte) 0x07, (byte) 0x0D, (byte) 0x05,
            (byte) 0x0B, (byte) 0x08, (byte) 0x0C, (byte) 0x03, (byte) 0x0E, (byte) 0x01, (byte) 0x0A, (byte) 0x02,
            (byte) 0x0C, (byte) 0x08, (byte) 0x04, (byte) 0x0B, (byte) 0x07, (byte) 0x0D, (byte) 0x00, (byte) 0x0F,
            (byte) 0x06, (byte) 0x09, (byte) 0x05, (byte) 0x07, (byte) 0x02, (byte) 0x0C, (byte) 0x06, (byte) 0x0A,
            (byte) 0x08, (byte) 0x0B, (byte) 0x00, (byte) 0x0F, (byte) 0x04, (byte) 0x03, (byte) 0x0E, (byte) 0x09,
            (byte) 0x01, (byte) 0x0D, (byte) 0x05, (byte) 0x0C, (byte) 0x04, (byte) 0x05, (byte) 0x09, (byte) 0x0A,
            (byte) 0x02, (byte) 0x08, (byte) 0x0D, (byte) 0x03, (byte) 0x0F, (byte) 0x01, (byte) 0x0E, (byte) 0x06,
            (byte) 0x07, (byte) 0x0B, (byte) 0x00, (byte) 0x0A, (byte) 0x08, (byte) 0x0E, (byte) 0x0D, (byte) 0x09,
            (byte) 0x0F, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x06, (byte) 0x01, (byte) 0x0C, (byte) 0x07,
            (byte) 0x0B, (byte) 0x02, (byte) 0x05, (byte) 0x03, (byte) 0x0C, (byte) 0x04, (byte) 0x0A, (byte) 0x02,
            (byte) 0x0F, (byte) 0x0D, (byte) 0x0E, (byte) 0x07, (byte) 0x00, (byte) 0x05, (byte) 0x08, (byte) 0x01,
            (byte) 0x06, (byte) 0x0B, (byte) 0x09, (byte) 0x0A, (byte) 0x0C, (byte) 0x01, (byte) 0x00, (byte) 0x09,
            (byte) 0x0E, (byte) 0x0D, (byte) 0x0B, (byte) 0x03, (byte) 0x07, (byte) 0x0F, (byte) 0x08, (byte) 0x05,
            (byte) 0x02, (byte) 0x04, (byte) 0x06, (byte) 0x0E, (byte) 0x0A, (byte) 0x01, (byte) 0x08, (byte) 0x07,
            (byte) 0x06, (byte) 0x05, (byte) 0x0C, (byte) 0x02, (byte) 0x0F, (byte) 0x00, (byte) 0x0D, (byte) 0x03,
            (byte) 0x0B, (byte) 0x04, (byte) 0x09, (byte) 0x03, (byte) 0x08, (byte) 0x0E, (byte) 0x00, (byte) 0x07,
            (byte) 0x09, (byte) 0x0F, (byte) 0x0C, (byte) 0x01, (byte) 0x06, (byte) 0x0D, (byte) 0x02, (byte) 0x05,
            (byte) 0x0A, (byte) 0x0B, (byte) 0x04, (byte) 0x03, (byte) 0x0A, (byte) 0x0C, (byte) 0x04, (byte) 0x0D,
            (byte) 0x0B, (byte) 0x09, (byte) 0x0E, (byte) 0x0F, (byte) 0x06, (byte) 0x01, (byte) 0x07, (byte) 0x02,
            (byte) 0x00, (byte) 0x05, (byte) 0x08 };


    public static final int KEYLEN = 26;
    public static final int BUFLEN = (KEYLEN * 2);

    private int val1;
    private byte[] val2;
    private int product;

    public Alpha26Decode(String cdkey) throws LoginException {
        
        if(cdkey == null || cdkey.isEmpty())
            throw new LoginException("CD-Key is missing!");

        if(cdkey.length() != KEYLEN)
            throw new LoginException("CDKey is not 24 characters!");

        byte[] table = new byte[BUFLEN];
        int[] values = new int[4];
        tableLookup(cdkey.toUpperCase(), table);

        for(int i = BUFLEN; i > 0; i--)
            Mult(4, 5, values, values, table[i - 1]);

        decodeKeyTablePass1(values);
        decodeKeyTablePass2(values);

        product = values[0] >> 0x0a;
        val1 = ((values[0] & 0x03FF) << 0x10) | (values[1] >>> 0x10);

        val2 = new byte[10];
        val2[0] = (byte) ((values[1] & 0x00FF) >> 0);
        val2[1] = (byte) ((values[1] & 0xFF00) >> 8);

        IntFromByteArray.LITTLEENDIAN.insertInteger(val2, 2, values[2]);
        IntFromByteArray.LITTLEENDIAN.insertInteger(val2, 6, values[3]);

    }


    private void tableLookup(String key, byte[] buf) {
        int a;
        int b = 0x21;
        byte decode;

        for(int i = 0; i < KEYLEN; i++) {
            a = (b + 0x07B5) % BUFLEN;
            b = (a + 0x07B5) % BUFLEN;
            decode = KeyTable[key.charAt(i)];
            buf[a] = (byte) (decode / 5);
            buf[b] = (byte) (decode % 5);
        }
    }

    private void Mult(int rounds, int mulx, int[] bufA, int[] bufB, int decodedByte) {
        int posA = rounds - 1;
        int posB = rounds - 1;

        while(rounds-- > 0) {
            long param1 = bufA[posA--];
            param1 &= 0x00000000FFFFFFFFl;

            long param2 = mulx;
            param2 &= 0x00000000FFFFFFFFl;

            long edxeax = param1 * param2;

            // ULONGLONG edxeax = UInt32x32To64(*BufA--, Mulx);
            bufB[posB--] = decodedByte + (int) edxeax;
            decodedByte = (int) (edxeax >> 32);
        }

    }

    private void decodeKeyTablePass1(int[] keyTable) {
        int ebx, ecx, esi, ebp;
        int var_C, var_4;
        int var_8 = 29;

        for(int i = 464; i >= 0; i -= 16) {
            esi = (var_8 & 7) << 2;
            var_4 = var_8 >>> 3;
            var_C = (keyTable[3 - var_4] & (0x0F << esi)) >>> esi;

            if(i < 464) {
                for(int j = 29; j > var_8; j--) {
                    ecx = (j & 7) << 2;
                    ebp = (keyTable[0x03 - (j >>> 3)] & (0x0F << ecx)) >>> ecx;
                    var_C = TranslateTable[ebp ^ TranslateTable[var_C + i] + i];
                }
            }

            for(int j = --var_8; j >= 0; j--) {
                ecx = (j & 7) << 2;
                ebp = (keyTable[0x03 - (j >>> 3)] & (0x0F << ecx)) >>> ecx;
                var_C = TranslateTable[ebp ^ TranslateTable[var_C + i] + i];
            }

            int index = 3 - var_4;
            ebx = (TranslateTable[var_C + i] & 0x0F) << esi;
            keyTable[index] = (ebx | ~(0x0F << esi) & (keyTable[index]));
        }
    }

    void decodeKeyTablePass2(int[] keyTable) {
        int eax, edx, ecx, edi, esi, ebp;
        byte[] Copy = ByteFromIntArray.LITTLEENDIAN.getByteArray(keyTable);
        esi = 0;

        for(edi = 0; edi < 120; edi++) {
            eax = edi & 0x1F;
            ecx = esi & 0x1F;
            edx = 3 - (edi >>> 5);

            int location = 12 - ((esi >>> 5) << 2);
            ebp = IntFromByteArray.LITTLEENDIAN.getInteger(Copy, location);

            ebp = (ebp & (1 << ecx)) >>> ecx;
            keyTable[edx] = ((ebp & 1) << eax) | (~(1 << eax) & keyTable[edx]);
            esi += 0x0B;
            if(esi >= 120)
                esi -= 120;
        }
    }

    public int[] getKeyHash(int clientToken, int serverToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            byte[] warBuf = new byte[26];

            IntFromByteArray.LITTLEENDIAN.insertInteger(warBuf, 0, clientToken);
            IntFromByteArray.LITTLEENDIAN.insertInteger(warBuf, 4, serverToken);
            IntFromByteArray.LITTLEENDIAN.insertInteger(warBuf, 8, getProduct());
            IntFromByteArray.LITTLEENDIAN.insertInteger(warBuf, 12, getVal1());

            for(int i = 16; i < 26; i++)
                warBuf[i] = getWar3Val2()[i - 16];

            digest.update(warBuf);
            return IntFromByteArray.LITTLEENDIAN.getIntArray(digest.digest());
        } catch(NoSuchAlgorithmException e) {
            System.out.println("Could not find SHA1 library " + e);
            System.exit(1);
            return null;
        }
    }

    public int getVal1() {
        return val1;
    }

    public int getVal2() {
        throw new UnsupportedOperationException("Can't use War3's getVal2() as an int");
    }

    public byte[] getWar3Val2() {
        return val2;
    }

    public int getProduct() {
        return product;
    }

    public String toString() {
        return "24-character alphanumeric decoder";
    }
}
