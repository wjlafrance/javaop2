/*
 * CheckRevision.java
 * 
 * Created on March 10, 2004, 9:05 AM
 */
package com.javaop.BNetLogin.versioning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;

import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import java.util.StringTokenizer;

import com.javaop.util.RelativeFile;

import com.javaop.exceptions.LoginException;

/**
 * This takes care of the CheckRevision() for the main game files of any
 * program. This is done to prevent tampering and to make sure the version is
 * correct. This class is generally slow because it has to read through the
 * entire files. The majority of the time is spent in i/o, but I've tried to
 * optimize this as much as possible.
 * 
 * I don't think this works anymore.
 * 
 * @author iago, wjlafance
 */
public class CheckRevision {
    
    /**
     * This is the main entry point for doing CheckRevision. This sorts out
     * what kind of CheckRevision we're doing and then calls another function.
     * @param mpqName MPQ name specified in SID_AUTH_INFO response
     * @param files Files to run CheckRevision on
     * @param formula Version check formula specified in SID_AUTH_INFO response
     * @return
     */
    public static int doCheckRevision(String mpqName, String[] files,
            byte[] formula) throws LoginException, IOException
    {
        String mpq = mpqName.toLowerCase();
        
        // Windows (IX86)
        if(mpq.matches("ix86ver[0-7].mpq")) {
            int mpqNum = Integer.parseInt(mpq.substring(7, 8));
            return checkrevisionOld("IX86", mpqNum, files, new String(formula));
        } else if (mpq.matches("ver-ix86-[0-7].mpq")) {
            int mpqNum = Integer.parseInt(mpq.substring(9, 10));
            return checkrevisionOld("IX86", mpqNum, files, new String(formula));
        } else if (mpq.matches("lockdown-IX86-[0-1][0-9].mpq")) {
            int mpqNum = Integer.parseInt(mpq.substring(14, 16));
            return checkrevisionLockdown("IX86", mpqNum, files, formula);
        }
        
        // Power Macintosh
        if (mpq.matches("pmacver[0-7].mpq")) {
            int mpqNum = Integer.parseInt(mpq.substring(7, 8));
            return checkrevisionOld("PMAC", mpqNum, files, new String(formula));
        } else if (mpq.matches("ver-pmac-[0-7].mpq")) {
            int mpqNum = Integer.parseInt(mpq.substring(9, 10));
            return checkrevisionOld("PMAC", mpqNum, files, new String(formula));
        } else if (mpq.matches("lockdown-PMAC-[0-1][0-9].mpq")) {
            int mpqNum = Integer.parseInt(mpq.substring(14, 16));
            return checkrevisionLockdown("PMAC", mpqNum, files, formula);
        }
        
        // Mac OS X
        if (mpq.matches("xmacver[0-7].mpq")) {
            int mpqNum = Integer.parseInt(mpq.substring(7, 8));
            return checkrevisionOld("XMAC", mpqNum, files, new String(formula));
        } else if (mpq.matches("ver-xmac-[0-7].mpq")) {
            int mpqNum = Integer.parseInt(mpq.substring(9, 10));
            return checkrevisionOld("XMAC", mpqNum, files, new String(formula));
        } else if (mpq.matches("lockdown-XMAC-[0-1][0-9].mpq")) {
            int mpqNum = Integer.parseInt(mpq.substring(14, 16));
            return checkrevisionLockdown("XMAC", mpqNum, files, formula);
        }
        
        throw new LoginException("MPQ filename (" + mpqName + ") doesn't match "
                + "any known pattern.");
    }

    /**
     * Performs an old-style CheckRevision.
     * 
     * @param platform IX86, PMAC, XMAC
     * @param mpqNumber MPQ number specified in SID_AUTH_INFO response
     * @param files The array of files we're checking. Generally the main game
     * files, like Starcraft.exe, Storm.dll, and Battle.snp.
     * @param formula Version check formula from SID_AUTH_INFO response
     * @throws FileNotFoundException If the datafiles aren't found.
     * @throws IOException If there is an error reading from one of the datafiles.
     * @return The 32-bit CheckRevision hash.
     */
    private static int checkrevisionOld(String platform, int mpqNumber,
            String[] files, String formula) throws LoginException, IOException
    {
        if (!platform.equals("IX86"))
            throw new LoginException("Old style CheckRevision not supported "
                    + " for " + platform);
        
        int hashcodes[] = {
                    0xE7F4CB62, 0xF6A14FFC, 0xAA5504AF, 0x871FCDC2,
                    0x11BF6A18, 0xC57292E6, 0x7927D27E, 0x2FEC8733 };
        
        // First, parse the versionString to name=value pairs and put them
        // in the appropriate place
        long A = 0, B = 0, C = 0, D = 0;
        char[] opValueDest = new char[4];
        char[] opValueSrc1 = new char[4];
        char[] opValueSrc2 = new char[4];
        char[] operation = new char[4];
        
        // Break this apart at the spaces
        StringTokenizer s = new StringTokenizer(formula, " ");
        int currentFormula = 0;
        while (s.hasMoreTokens()) {
            String thisToken = s.nextToken();
            
            if (thisToken.indexOf('=') > 0) {
                // Break it apart at the '='
                StringTokenizer nameValue = new StringTokenizer(thisToken, "=");
                if(nameValue.countTokens() != 2)
                    throw new LoginException("Malformed formula.");
                
                String variable = nameValue.nextToken();
                String value = nameValue.nextToken();
                
                if (Character.isDigit(value.charAt(0))) {
                    switch (variable.charAt(0)) {
                        case 'A': A = Long.parseLong(value); break;
                        case 'B': B = Long.parseLong(value); break;
                        case 'C': C = Long.parseLong(value); break;
                    }
                } else {
                    opValueDest[currentFormula] = variable.charAt(0);
                    opValueSrc1[currentFormula] = value.charAt(0);
                    operation[currentFormula] = value.charAt(1);
                    opValueSrc2[currentFormula] = value.charAt(2);
                    currentFormula++;
                }
            }
        }
        
        // Now we actually do the hashing for each file
        // Start by hashing A by the hashcode
        
        A ^= hashcodes[mpqNumber];
        
        for(int i = 0; i < 3; i++) {
            byte []data = readFile(new File(files[i]));
            
            for(int j = 0; j < data.length; j += 4) {
                long S = 0;
                S |= ((data[j+0] << 0)  & 0x000000FF);
                S |= ((data[j+1] << 8)  & 0x0000FF00);
                S |= ((data[j+2] << 16) & 0x00FF0000);
                S |= ((data[j+3] << 24) & 0xFF000000);

                for (int k = 0; k < currentFormula; k++) {
                    long val1 = 0, val2 = 0;
                    switch (opValueSrc1[k]) {
                        case 'A': val1 = A; break;
                        case 'B': val1 = B; break;
                        case 'C': val1 = C; break;
                        case 'S': val1 = S; break;
                    }
                    switch (opValueSrc2[k]) {
                        case 'A': val2 = A; break;
                        case 'B': val2 = B; break;
                        case 'C': val2 = C; break;
                        case 'S': val2 = S; break;
                    }
                    
                    switch (opValueDest[k]) {
                        case 'A':
                            switch (operation[k]) {
                                case '^': A = val1 ^ val2;
                                case '+': A = val1 + val2;
                                case '-': A = val1 - val2;
                                case '*': A = val1 * val2;
                                case '/': A = val1 / val2;
                            }
                            break;
                        case 'B':
                            switch (operation[k]) {
                                case '^': B = val1 ^ val2;
                                case '+': B = val1 + val2;
                                case '-': B = val1 - val2;
                                case '*': B = val1 * val2;
                                case '/': B = val1 / val2;
                            }
                            break;
                        case 'C':
                            switch (operation[k]) {
                                case '^': C = val1 ^ val2;
                                case '+': C = val1 + val2;
                                case '-': C = val1 - val2;
                                case '*': C = val1 * val2;
                                case '/': C = val1 / val2;
                            }
                            break;
                    }
                }
             }
        }
        return (int)C;
    }
    
    
    /**
     * Reads a file and returns a byte array.
     */
    public static byte []readFile(File file) throws IOException {
        int length = (int) file.length();
        byte []ret = new byte[(length % 1024) == 0 ? length
            : (length / 1024 * 1024) + 1024];
        
        InputStream in = new FileInputStream(file);
        in.read(ret);
        in.close();
        
        int value = 0xFF;
        for(int i = (int) file.length(); i < ret.length; i++)
          ret[i] = (byte) value--;
        
        return ret;
    }
    
    /**
     * Performs an lockdown CheckRevision.
     * 
     * @param platform IX86, PMAC, XMAC
     * @param mpqNumber MPQ number specified in SID_AUTH_INFO response
     * @param files The array of files we're checking. Generally the main game
     * files, like Starcraft.exe, Storm.dll, and Battle.snp.
     * @param formula Version check formula from SID_AUTH_INFO response
     * @throws FileNotFoundException If the datafiles aren't found.
     * @throws IOException If there is an error reading from one of the datafiles.
     * @return The 32-bit CheckRevision hash.
     */
    private static int checkrevisionLockdown(String platform, int mpqNumber,
            String[] files, byte[] formula) throws LoginException, IOException
    {
        throw new LoginException("Lockdown CheckRevision not supported for "
                + platform);
    }

}