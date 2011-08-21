require "test/unit"

include Java
require "jar/JavaOp2.jar"
require "jar/Plugins/BNetLogin.jar"

java_import 'com.javaop.exceptions.LoginException'
java_import 'com.javaop.BNetLogin.versioning.CheckRevision'
java_import 'com.javaop.BNetLogin.versioning.CheckRevisionResults'
java_import 'com.javaop.BNetLogin.versioning.Bnls'
java_import 'com.javaop.BNetLogin.versioning.Game'

class TestCheckRevision < Test::Unit::TestCase

  def setup
    # do nothing
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
    assert CheckRevision.respond_to?('do_check_revision')
  end


  def testCheckRevisionAgainstBnls
    mpq = "ver-IX86-6.mpq"
    formula = 'A=166443184 B=361259356 C=197717253 4 A=A-S B=B-C C=C+A A=A+B'.to_java_bytes
    
    games = Hash["D2DV", [(Dir.pwd + "/hashes/D2DV/Game.exe"),
                          (Dir.pwd + "/hashes/D2DV/Bnclient.dll"),
                          (Dir.pwd + "/hashes/D2DV/D2Client.dll")],
                 "D2XP", [(Dir.pwd + "/hashes/D2XP/Game.exe"),
                          (Dir.pwd + "/hashes/D2XP/Bnclient.dll"),
                          (Dir.pwd + "/hashes/D2XP/D2Client.dll")],
                #"DRTL", [(Dir.pwd + "/hashes/DRTL/Diablo.exe"),
                #         (Dir.pwd + "/hashes/DRTL/Storm.dll"),
                #         (Dir.pwd + "/hashes/DRTL/Battle.snp")],
                #"DSHR", [(Dir.pwd + "/hashes/DSHR/Diablo_s.exe"),
                #         (Dir.pwd + "/hashes/DSHR/Storm.dll"),
                #         (Dir.pwd + "/hashes/DSHR/Battle.snp")],
                #"JSTR", [(Dir.pwd + "/hashes/D2DV/StarCraftJ.exe"),
                #         (Dir.pwd + "/hashes/D2DV/storm.dll"),
                #         (Dir.pwd + "/hashes/D2DV/battle.snp")],
                #"SSHR", [(Dir.pwd + "/hashes/SSHR/Starcraft.exe"),
                #         (Dir.pwd + "/hashes/SSHR/storm.dll"),
                #         (Dir.pwd + "/hashes/SSHR/battle.snp")],
                 "STAR", [(Dir.pwd + "/hashes/STAR/Starcraft.exe"),
                          (Dir.pwd + "/hashes/STAR/Storm.dll"),
                          (Dir.pwd + "/hashes/STAR/Battle.snp")],
                 "W2BN", [(Dir.pwd + "/hashes/W2BN/WarCraft II BNE.exe"),
                          (Dir.pwd + "/hashes/W2BN/storm.dll"),
                          (Dir.pwd + "/hashes/W2BN/battle.snp")],
                 "WAR3", [(Dir.pwd + "/hashes/WAR3/war3.exe"),
                          (Dir.pwd + "/hashes/WAR3/Storm.dll"),
                          (Dir.pwd + "/hashes/WAR3/game.dll")],
                 "W3XP", [(Dir.pwd + "/hashes/WAR3/war3.exe"),
                          (Dir.pwd + "/hashes/WAR3/Storm.dll"),
                          (Dir.pwd + "/hashes/WAR3/game.dll")]]

    games.each do | game, files |
     #print "Testing #{game}..  "
      remote_results = Bnls.check_revision(Game.new(game), mpq, 0x1c75f7003518b00, formula)
      local_hash = CheckRevision.do_check_revision(mpq, files, formula)
      assert_not_nil remote_results
      assert_equal local_hash, remote_results.checksum
     #puts "local: #{local_hash.to_s(16)}, remote: #{remote_results.checksum.to_s(16)}"
    end
    
  end


  def testSlowCheckRevisionAgainstBnls
  
    mpq = "ver-IX86-6.mpq"
    files = [(Dir.pwd + "/hashes/D2DV/Game.exe"),
             (Dir.pwd + "/hashes/D2DV/Bnclient.dll"),
             (Dir.pwd + "/hashes/D2DV/D2Client.dll")]
    
    weird_formulas = ['A=166443184 B=361259356 C=197717253 4 C=C+A A=A+B A=A-S B=B-C',
                      'A=166443184 C=197717253 B=361259356 4 C=C+A A=A+B A=A-S B=B-C']
    weird_formulas.each do | formula |
     #print "Testing weird formula #{formula} .."
      remote_results = Bnls.check_revision(Game.new("D2DV"), mpq, 0x1c75f7003518b00, formula.to_java_bytes)
      local_hash = CheckRevision.do_check_revision(mpq, files, formula.to_java_bytes)
      assert_not_nil remote_results
      assert_equal local_hash, remote_results.checksum
     #puts "local: #{local_hash.to_s(16)}, remote: #{remote_results.checksum.to_s(16)}"
    end
  end

end