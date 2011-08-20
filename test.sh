#BASE=`pwd`
#CLASSPATH=$BASE/jar:$BASE/jar/Plugins
#echo "Classpath: $CLASSPATH"
#export CLASSPATH
#rake test
#jruby --ng -S rake
jruby --ng -s spec/ts.rb 
