#!/bin/bash

# ------------
# Vars
# ------------
JAR_OMUD="$1"
JAR_AC="$2"
SRC="src"
BUILD_DIR="_BUILD"
MAN="MANIFEST.MF"

# ------------
# Create Build Dir Structure
# ------------
if [ -f $JAR_OMUD  ]; then rm $JAR_OMUD; fi;
if [ -d $BUILD_DIR ]; then rm -rf $BUILD_DIR; fi;
mkdir -p $BUILD_DIR/fonts
cp fonts/* $BUILD_DIR/fonts 2>/dev/null
cp lib/$JAR_AC $BUILD_DIR   2>/dev/null
cp src/$MAN $BUILD_DIR      2>/dev/null

echo -------------------------
echo Build: \"$JAR_OMUD\" \"$JAR_AC\" \"$JAVA_HOME\"
echo -------------------------
echo ""

# ------------
# Compile
# ------------
SRC_DIRS=$(find $SRC -type d -exec printf "{}/*.java " \;)
javac -Xlint:deprecation -d $BUILD_DIR -cp $BUILD_DIR/$JAR_AC $SRC_DIRS

# ------------
# Check Class Files
# ------------
SRC_FILES=$(find $SRC -type f -name *.java | sed "s/.*\//$BUILD_DIR\//; s/\.java/.class/")
for CLASS_FILE in $SRC_FILES; do
	if [ ! -f $CLASS_FILE ]; then
		echo ""
		echo ---------------------------------------------
	    echo Failed: class file not found: $CLASS_FILE
	    exit 0
	fi
done

# ------------
# Create Jar
# ------------
cd $BUILD_DIR
jar xf $JAR_AC
jar cfm $JAR_OMUD $MAN *.class fonts org
cd ../

# ------------
# Clean Up + Run Jar
# ------------
if [ -f $BUILD_DIR/$JAR_OMUD ]; then
	mv $BUILD_DIR/$JAR_OMUD .
	rm -rf $BUILD_DIR
	java -jar $JAR_OMUD
fi
