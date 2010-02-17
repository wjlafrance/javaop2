package util;

import util.BnetPacket;

import exceptions.LoginException;

import callback_interfaces.PublicExposedFunctions;

import constants.ErrorLevelConstants;


public class ServerSignature {
    
    private static BigIntegerEx key = new BigIntegerEx(BigIntegerEx.LITTLE_ENDIAN, new byte[] {
        0x01, 0x00, 0x01, 0x00
    });
    private static BigIntegerEx mod = new BigIntegerEx(BigIntegerEx.LITTLE_ENDIAN, new byte[] {
        (byte) 0xD5, (byte) 0xA3, (byte) 0xD6, (byte) 0xAB, (byte) 0x0F, (byte) 0x0D, (byte) 0xC5, (byte) 0x0F,
        (byte) 0xC3, (byte) 0xFA, (byte) 0x6E, (byte) 0x78, (byte) 0x9D, (byte) 0x0B, (byte) 0xE3, (byte) 0x32,
        (byte) 0xB0, (byte) 0xFA, (byte) 0x20, (byte) 0xE8, (byte) 0x42, (byte) 0x19, (byte) 0xB4, (byte) 0xA1,
        (byte) 0x3A, (byte) 0x3B, (byte) 0xCD, (byte) 0x0E, (byte) 0x8F, (byte) 0xB5, (byte) 0x56, (byte) 0xB5,
        (byte) 0xDC, (byte) 0xE5, (byte) 0xC1, (byte) 0xFC, (byte) 0x2D, (byte) 0xBA, (byte) 0x56, (byte) 0x35,
        (byte) 0x29, (byte) 0x0F, (byte) 0x48, (byte) 0x0B, (byte) 0x15, (byte) 0x5A, (byte) 0x39, (byte) 0xFC,
        (byte) 0x88, (byte) 0x07, (byte) 0x43, (byte) 0x9E, (byte) 0xCB, (byte) 0xF3, (byte) 0xB8, (byte) 0x73,
        (byte) 0xC9, (byte) 0xE1, (byte) 0x77, (byte) 0xD5, (byte) 0xA1, (byte) 0x06, (byte) 0xA6, (byte) 0x20,
        (byte) 0xD0, (byte) 0x82, (byte) 0xC5, (byte) 0x2D, (byte) 0x4D, (byte) 0xD3, (byte) 0x25, (byte) 0xF4,
        (byte) 0xFD, (byte) 0x26, (byte) 0xFC, (byte) 0xE4, (byte) 0xC2, (byte) 0x00, (byte) 0xDD, (byte) 0x98,
        (byte) 0x2A, (byte) 0xF4, (byte) 0x3D, (byte) 0x5E, (byte) 0x08, (byte) 0x8A, (byte) 0xD3, (byte) 0x20,
        (byte) 0x41, (byte) 0x84, (byte) 0x32, (byte) 0x69, (byte) 0x8E, (byte) 0x8A, (byte) 0x34, (byte) 0x76,
        (byte) 0xEA, (byte) 0x16, (byte) 0x8E, (byte) 0x66, (byte) 0x40, (byte) 0xD9, (byte) 0x32, (byte) 0xB0,
        (byte) 0x2D, (byte) 0xF5, (byte) 0xBD, (byte) 0xE7, (byte) 0x57, (byte) 0x51, (byte) 0x78, (byte) 0x96,
        (byte) 0xC2, (byte) 0xED, (byte) 0x40, (byte) 0x41, (byte) 0xCC, (byte) 0x54, (byte) 0x9D, (byte) 0xFD,
        (byte) 0xB6, (byte) 0x8D, (byte) 0xC2, (byte) 0xBA, (byte) 0x7F, (byte) 0x69, (byte) 0x8D, (byte) 0xCF
    });
    
    public static void checkSignature(PublicExposedFunctions pub,
            BnetPacket b) throws LoginException
    {
        // should we stop on invalid signature?
        boolean stopOnInvalidSignature = pub.getLocalSettingDefault(
                "Battle.net Login Plugin", "Verify server",
                "true").equalsIgnoreCase("true");
                
        if(b.size() < 128) {
            // Signature NOT present
            if(stopOnInvalidSignature) {
                throw new LoginException("[BNET] Server signature is not " +
                        "present. To connect anyway, change the setting " +
                        "\"Verify server\" to false.");
            } else {
                pub.systemMessage(ErrorLevelConstants.WARNING,
                        "[BNET] Server signature is not present.");
            }
            
        } else {
            // test validity of signature
            boolean valid = compareSignatureToIp(b.removeBytes(128),
                    (byte[]) pub.getLocalVariable("address"));
            if (valid) {
                pub.systemMessage(ErrorLevelConstants.INFO,
                    "[BNET] Server has proven to be owned by Blizzard.");
            } else {
                if (stopOnInvalidSignature) {
                    throw new LoginException("[BNET] Server signature is " +
                            "invalid. To connect anyway, change the setting " +
                            "\"Verify server\" to false.");
                } else {
                    pub.systemMessage(ErrorLevelConstants.WARNING,
                            "[BNET] Server signature is invalid.");
                }
            }
        }
    }
    
    private static boolean compareSignatureToIp(byte[] sig, byte[] ip) {
        // Do the calculation
        byte[] result = new BigIntegerEx(BigIntegerEx.LITTLE_ENDIAN,
                sig).modPow(key, mod).toByteArray();
        
        // Create the array of the correct result
        byte[] correctResult = new byte[result.length];
        // Put the ip into the array
        correctResult[0] = ip[0];
        correctResult[1] = ip[1];
        correctResult[2] = ip[2];
        correctResult[3] = ip[3];
        
        // Pad the result with 0xBB's
        for(int i = 4; i < correctResult.length; i++)
            correctResult[i] = (byte) 0xBB;
        
        for(int i = 0; i < result.length; i++)
            if(result[i] != correctResult[i])
                return false;
        
        // If nothing bad happens, it's good.
        return true;
    }
}
