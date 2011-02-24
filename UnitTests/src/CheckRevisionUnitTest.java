package com.javaop.UnitTests;

import com.javaop.BNetLogin.versioning.CheckRevision;
import com.javaop.BNetLogin.versioning.CheckRevisionResults;
import com.javaop.BNetLogin.versioning.Bnls;
import com.javaop.BNetLogin.versioning.Game;

import java.io.File;
import java.lang.reflect.Method;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author William LaFrance
 */
public class CheckRevisionUnitTest {

    @BeforeClass
    public static void initialSetUp() {
    }

    @AfterClass
    public static void tearDown() {	
        new File("_GameData.txt").delete();
        new File("_GlobalSettings.txt").delete();
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
		Class[] CheckRevision_doCheckRevisionReturnType = new Class[] {
				String.class,
				String[].class,
				byte[].class
		};
        Method localMethod = CheckRevision.class.getMethod("doCheckRevision", CheckRevision_doCheckRevisionReturnType);
        Assert.assertNotNull(localMethod);
    }

    @Test
    public void CheckRevision_doCheckRevisionCorrectReturnType() throws NoSuchMethodException {
		Class[] CheckRevision_doCheckRevisionReturnType = new Class[] {
				String.class,
				String[].class,
				byte[].class
		};
        Method localMethod = CheckRevision.class.getMethod("doCheckRevision", CheckRevision_doCheckRevisionReturnType);
        Assert.assertEquals(int.class, localMethod.getReturnType());
    }

    @Test
    public void testCheckRevisionChooserAgainstBnls() throws Exception {
        CheckRevisionResults d2dvBnlsResults = Bnls.CheckRevision(
                new Game("D2DV"),
                "ver-IX86-6.mpq",
                (long)0x1c75f7003518b00l,
                "A=3116746618 B=3629198554 C=229279865 4 A=A+S B=B-C C=C^A A=A^B".getBytes());
        int d2dvLocalHashChecksum = CheckRevision.doCheckRevision("ver-IX86-6.mpq",
                new String[] {
                        System.getProperty("user.dir") + "/Hashfiles/D2DV/Game.exe",
                        System.getProperty("user.dir") + "/Hashfiles/D2DV/Bnclient.dll",
                        System.getProperty("user.dir") + "/Hashfiles/D2DV/D2Client.dll"
                },
                "A=3116746618 B=3629198554 C=229279865 4 A=A+S B=B-C C=C^A A=A^B".getBytes());
        Assert.assertEquals(d2dvBnlsResults.checksum, d2dvLocalHashChecksum);
    }

    @Test
    public void testDiablo2LocalOldCheckRevisionAgainstBnls() throws Exception {
        CheckRevisionResults d2dvBnlsResults = Bnls.CheckRevision(
                new Game("D2DV"),
                "ver-IX86-6.mpq",
                (long)0x1c75f7003518b00l,
                "A=3116746618 B=3629198554 C=229279865 4 A=A+S B=B-C C=C^A A=A^B".getBytes());
        int d2dvLocalHashChecksum = CheckRevision.checkRevisionOld("IX86",	
            	6,
				new String[] {
                    System.getProperty("user.dir") + "/Hashfiles/D2DV/Game.exe",
                    System.getProperty("user.dir") + "/Hashfiles/D2DV/Bnclient.dll",
                    System.getProperty("user.dir") + "/Hashfiles/D2DV/D2Client.dll"
                },
                "A=3116746618 B=3629198554 C=229279865 4 A=A+S B=B-C C=C^A A=A^B");
        Assert.assertEquals(d2dvBnlsResults.checksum, d2dvLocalHashChecksum);
    }

    @Test
    public void testDiablo2LocalOldSlowCheckRevisionAgainstBnls() throws Exception {
        CheckRevisionResults d2dvBnlsResults = Bnls.CheckRevision(
                new Game("D2DV"),
                "ver-IX86-6.mpq",
                (long)0x1c75f7003518b00l,
                "A=3116746618 B=3629198554 C=229279865 4 A=A+S B=B-C C=C^A A=A^B".getBytes());
        int d2dvLocalHashChecksum = CheckRevision.checkRevisionOldSlow("IX86",	
            	6,
				new String[] {
                    System.getProperty("user.dir") + "/Hashfiles/D2DV/Game.exe",
                    System.getProperty("user.dir") + "/Hashfiles/D2DV/Bnclient.dll",
                    System.getProperty("user.dir") + "/Hashfiles/D2DV/D2Client.dll"
                },
                "A=3116746618 B=3629198554 C=229279865 4 A=A+S B=B-C C=C^A A=A^B");
        Assert.assertEquals(d2dvBnlsResults.checksum, d2dvLocalHashChecksum);
    }
}