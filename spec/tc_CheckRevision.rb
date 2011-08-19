#package com.javaop.UnitTests;

#import com.javaop.BNetLogin.versioning.CheckRevision;
#import com.javaop.BNetLogin.versioning.CheckRevisionResults;
#import com.javaop.BNetLogin.versioning.Bnls;
#import com.javaop.BNetLogin.versioning.Game;

#import java.io.File;
#import java.lang.reflect.Method;

#import org.junit.AfterClass;
#import org.junit.Assert;
#import org.junit.BeforeClass;
#import org.junit.Test;

include Java 
require "test/unit"

def setup
end


def teardown 
  File.delete("_GameData.txt")
  File.delete("_GlobalSettings.txt")
end



def testCheckRevisionClassExists
        Assert.assertNotNull(CheckRevision.class);
end

def testCheckRevisionMethodExists
        Class[] CheckRevision_doCheckRevisionReturnType = new Class[] {
                String.class,
                String[].class,
                byte[].class
        };
        Method localMethod = CheckRevision.class.getMethod("doCheckRevision", CheckRevision_doCheckRevisionReturnType);
        Assert.assertNotNull(localMethod);
end


def testCheckRevisionMethodCorrectReturnType
        Class[] CheckRevision_doCheckRevisionReturnType = new Class[] {
                String.class,
                String[].class,
                byte[].class
        };
        Method localMethod = CheckRevision.class.getMethod("doCheckRevision", CheckRevision_doCheckRevisionReturnType);
        Assert.assertEquals(int.class, localMethod.getReturnType());
end


def testCheckRevisionAgainstBnls
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
end


def testDiablo2LocalOldCheckRevisionAgainstBnls
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
end


def testDiablo2LocalOldSlowCheckRevisionAgainstBnls
        /*
        Method checkRevisionOldSlow = CheckRevision.class.getMethod("checkRevisionOldSlow", checkRevisionOldSlowArguments);     
        checkRevisionOldSlow.setAccessible(true);
        int d2dvLocalHashChecksum = (Integer)checkRevisionOldSlow.invoke(null,
                new Object[] {
                    "IX86",
                    6,
                    new String[] {
                        System.getProperty("user.dir") + "/Hashfiles/D2DV/Game.exe",
                        System.getProperty("user.dir") + "/Hashfiles/D2DV/Bnclient.dll",
                        System.getProperty("user.dir") + "/Hashfiles/D2DV/D2Client.dll"
                    },
                    "A=3116746618 B=3629198554 C=229279865 4 A=A+S B=B-C C=C^A A=A^B"
                } );
        */
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
end