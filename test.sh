BASE=`pwd`
CLASSPATH=$BASE/BNetLogin/bin:$BASE/javaop2/bin
export CLASSPATH
#rake test
jruby --ng -S rake
