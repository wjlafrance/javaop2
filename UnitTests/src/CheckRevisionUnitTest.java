package com.javaop.UnitTests;
import junit.framework.*;

import com.javaop.BNetLogin.versioning.CheckRevision;
import com.javaop.BNetLogin.versioning.CheckRevisionResults;
import com.javaop.BNetLogin.versioning.Bnls;
import com.javaop.BNetLogin.versioning.Game;

import java.lang.reflect.Method;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author William LaFrance
 */
public class CheckRevisionUnitTest {
    
    private static Class[] CheckRevision_doCheckRevisionReturnType =
            new Class[] { String.class, String[].class, byte[].class };

    @BeforeClass
    public static void initialSetUp() {
    }

    @AfterClass
    public static void tearDown() {
    }


    @Test
    public void Bnls_ClassExists() {
        Assert.assertNotNull(Bnls.class);
    }


    @Test
    public void CheckRevision_ClassExists() {
        Assert.assertNotNull(CheckRevision.class);
    }

    @Test
    public void CheckRevision_doCheckRevisionMethodExists() throws NoSuchMethodException {
        Method localMethod = CheckRevision.class.getMethod("doCheckRevision", CheckRevision_doCheckRevisionReturnType);
        Assert.assertNotNull(localMethod);
    }

    @Test
    public void CheckRevision_doCheckRevisionCorrectReturnType() throws NoSuchMethodException {
        Method localMethod = CheckRevision.class.getMethod("doCheckRevision", CheckRevision_doCheckRevisionReturnType);
        Assert.assertEquals(int.class, localMethod.getReturnType());
    }

    @Test
    public void testLocalHashAgainstBnls() throws Exception {
        
        CheckRevisionResults d2dvBnlsResults = Bnls.CheckRevision(new Game("D2DV"), "ver-IX86-6.mpq", (long)0x1c75f7003518b00l,
                new byte[] { 0x41,0x3d,0x33,0x31,0x31,0x36,0x37,0x34,
                            0x36,0x36,0x31,0x38,0x20,0x42,0x3d,0x33,
                            0x36,0x32,0x39,0x31,0x39,0x38,0x35,0x35,
                            0x34,0x20,0x43,0x3d,0x32,0x32,0x39,0x32,
                            0x37,0x39,0x38,0x36,0x35,0x20,0x34,0x20,
                            0x41,0x3d,0x41,0x2b,0x53,0x20,0x42,0x3d,
                            0x42,0x2d,0x43,0x20,0x43,0x3d,0x43,0x5e,
                            0x41,0x20,0x41,0x3d,0x41,0x5e,0x42 });
        int d2dvLocalHashChecksum = CheckRevision.doCheckRevision("ver-IX86-6.mpq",
                new String[] { System.getProperty("user.dir") + "/Hashfiles/D2DV/Game.exe",
                System.getProperty("user.dir") + "/Hashfiles/D2DV/Bnclient.dll",
                System.getProperty("user.dir") + "/Hashfiles/D2DV/D2Client.dll"},
                new byte[] { 0x41,0x3d,0x33,0x31,0x31,0x36,0x37,0x34,
                            0x36,0x36,0x31,0x38,0x20,0x42,0x3d,0x33,
                            0x36,0x32,0x39,0x31,0x39,0x38,0x35,0x35,
                            0x34,0x20,0x43,0x3d,0x32,0x32,0x39,0x32,
                            0x37,0x39,0x38,0x36,0x35,0x20,0x34,0x20,
                            0x41,0x3d,0x41,0x2b,0x53,0x20,0x42,0x3d,
                            0x42,0x2d,0x43,0x20,0x43,0x3d,0x43,0x5e,
                            0x41,0x20,0x41,0x3d,0x41,0x5e,0x42 });
        Assert.assertEquals(d2dvBnlsResults.checksum, d2dvLocalHashChecksum);
        
        // TO DO: Remove _GameData.txt and _GlobalSettings.txt from working directory
    }
}