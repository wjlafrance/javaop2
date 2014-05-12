##
## Shell script to create a pom.xml for each plugin subdirectory
##
## Useful for regenerating poms if each one needs to change. Don't want to do
## that by hand.
##

for D in `find . -type d -d 1`
do

DIR=$(echo $D | sed s/\\.\\///)
echo $DIR
tee $DIR/pom.xml <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

	<modelVersion>4.0.0</modelVersion>
	<artifactId>$DIR</artifactId>
	<version>2.2.0-SNAPSHOT</version>

	<parent>
		<groupId>com.javaop</groupId>
		<artifactId>ParentPlugin</artifactId>
		<version>2.2.0-SNAPSHOT</version>
		<relativePath>../ParentPlugin</relativePath>
	</parent>

</project>

EOF

done