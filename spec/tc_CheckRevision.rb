require "test/unit"

include Java
#system("echo `pwd`")
require "jar/JavaOp2.jar"
require "jar/Plugins/BNetLogin.jar"

java_import 'com.javaop.exceptions.LoginException'
java_import 'com.javaop.BNetLogin.versioning.CheckRevision'
java_import 'com.javaop.BNetLogin.versioning.CheckRevisionResults'
java_import 'com.javaop.BNetLogin.versioning.Bnls'
java_import 'com.javaop.BNetLogin.versioning.Game'

class TestCheckRevision < Test::Unit::TestCase

  def setup
  end

  def teardown
    files_to_delete = ["_GameData.txt", "_GlobalSettings.txt"]
    files_to_delete.each do | file |
      File.delete(file) if File.exists?(file)
    end
  end


  def testCheckRevisionClassExists
    cr = CheckRevision.new
    assert_not_nil cr
  end


  def testCheckRevisionMethodExists
        #Class[] CheckRevision_doCheckRevisionReturnType = new Class[] {
        #        String.class,
        #        String[].class,
        #        byte[].class
        #};
        #Method localMethod = CheckRevision.class.getMethod("doCheckRevision", CheckRevision_doCheckRevisionReturnType);
        #Assert.assertNotNull(localMethod);
  end


  def testCheckRevisionMethodCorrectReturnType
        #Class[] CheckRevision_doCheckRevisionReturnType = new Class[] {
        #        String.class,
        #        String[].class,
        #        byte[].class
        #};
        #Method localMethod = CheckRevision.class.getMethod("doCheckRevision", CheckRevision_doCheckRevisionReturnType);
        #Assert.assertEquals(int.class, localMethod.getReturnType());
  end


  def testCheckRevisionAgainstBnls
        #CheckRevisionResults d2dvBnlsResults = Bnls.CheckRevision(
        #        new Game("D2DV"),
        #        "ver-IX86-6.mpq",
        #        (long)0x1c75f7003518b00l,
        #        "A=3116746618 B=3629198554 C=229279865 4 A=A+S B=B-C C=C^A A=A^B".getBytes());
        #int d2dvLocalHashChecksum = CheckRevision.doCheckRevision("ver-IX86-6.mpq",
        #        new String[] {
        #                System.getProperty("user.dir") + "/Hashfiles/D2DV/Game.exe",
        #                System.getProperty("user.dir") + "/Hashfiles/D2DV/Bnclient.dll",
        #                System.getProperty("user.dir") + "/Hashfiles/D2DV/D2Client.dll"
        #        },
        #        "A=3116746618 B=3629198554 C=229279865 4 A=A+S B=B-C C=C^A A=A^B".getBytes());
        #Assert.assertEquals(d2dvBnlsResults.checksum, d2dvLocalHashChecksum);
  end


  def testDiablo2LocalOldCheckRevisionAgainstBnls
        #CheckRevisionResults d2dvBnlsResults = Bnls.CheckRevision(
        #        new Game("D2DV"),
        #        "ver-IX86-6.mpq",
        #        (long)0x1c75f7003518b00l,
        #        "A=3116746618 B=3629198554 C=229279865 4 A=A+S B=B-C C=C^A A=A^B".getBytes());
        #int d2dvLocalHashChecksum = CheckRevision.checkRevisionOld("IX86",  
        #        6,
        #        new String[] {
        #            System.getProperty("user.dir") + "/Hashfiles/D2DV/Game.exe",
        #            System.getProperty("user.dir") + "/Hashfiles/D2DV/Bnclient.dll",
        #            System.getProperty("user.dir") + "/Hashfiles/D2DV/D2Client.dll"
        #        },
        #        "A=3116746618 B=3629198554 C=229279865 4 A=A+S B=B-C C=C^A A=A^B");
        #Assert.assertEquals(d2dvBnlsResults.checksum, d2dvLocalHashChecksum);
  end


  def testDiablo2LocalOldSlowCheckRevisionAgainstBnls
        #CheckRevisionResults d2dvBnlsResults = Bnls.CheckRevision(
        #        new Game("D2DV"),
        #        "ver-IX86-6.mpq",
        #        (long)0x1c75f7003518b00l,
        #        "A=3116746618 B=3629198554 C=229279865 4 A=A+S B=B-C C=C^A A=A^B".getBytes());
        #int d2dvLocalHashChecksum = CheckRevision.checkRevisionOldSlow("IX86",  
        #        6,
        #        new String[] {
        #            System.getProperty("user.dir") + "/Hashfiles/D2DV/Game.exe",
        #            System.getProperty("user.dir") + "/Hashfiles/D2DV/Bnclient.dll",
        #            System.getProperty("user.dir") + "/Hashfiles/D2DV/D2Client.dll"
        #        },
        #        "A=3116746618 B=3629198554 C=229279865 4 A=A+S B=B-C C=C^A A=A^B");
        #Assert.assertEquals(d2dvBnlsResults.checksum, d2dvLocalHashChecksum);
  end

end