require "test/unit"

include Java
require "jar/JavaOp2.jar"
require "jar/Plugins/BNetLogin.jar"

java_import 'com.javaop.exceptions.LoginException'
java_import 'com.javaop.BNetLogin.versioning.CheckRevisionResults'
java_import 'com.javaop.BNetLogin.versioning.Bnls'
java_import 'com.javaop.BNetLogin.versioning.Game'

class TestBattleNetLoginServer < Test::Unit::TestCase

  def setup
    # do nothing
  end


  def teardown
    # do nothing
  end


  def testBnlsClassExists
    bnls = Bnls.new
    assert_not_nil bnls
  end


  def testCheckRevisionMethodExists
    assert Bnls.respond_to?('check_revision')
  end

end