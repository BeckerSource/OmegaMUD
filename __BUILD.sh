#!/bin/bash

# ------------
# Vars
# ------------
MAN="$1"
JAR_AC="$2"
JAR_OMUD="$3"
SRC="src"
BUILD_DIR="_BUILD"
FILES_LIST="___SRC_FILES.txt"

# ------------
# Create Build Dir Structure
# ------------
if [ -f $JAR_OMUD  ]; then rm $JAR_OMUD; fi;
if [ -d $BUILD_DIR ]; then rm -rf $BUILD_DIR; fi;
mkdir -p $BUILD_DIR/lib
mkdir    $BUILD_DIR/fonts
cp lib/$JAR_AC $BUILD_DIR/lib 2>/dev/null
cp fonts/* $BUILD_DIR/fonts 2>/dev/null
cp src/$MAN $BUILD_DIR 2>/dev/null

# ------------
# Compile
# ------------
javac -Xlint:deprecation -d $BUILD_DIR -cp $BUILD_DIR/lib/$JAR_AC $SRC/*.java

# ------------
# Check Class Files
# ------------
if [ -f $FILES_LIST ]; then
	while IFS= read -r CLASS_FILE; do
		CLASS_FILE="$BUILD_DIR/$CLASS_FILE.class"
		if [ ! -f $CLASS_FILE ]; then
			echo ""
			echo ---------------------------------------------
		    echo Failed: class file not found: $CLASS_FILE
		    exit 0
		fi
	done < $FILES_LIST
else
	echo ""
	echo ---------------------------------------------
    echo Failed: file with required class files list found: $FILES_LIST
    exit 0
fi

# ------------
# Create Jar
# ------------
cd $BUILD_DIR
jar cfm $JAR_OMUD $MAN *.class fonts/*
cd ../

# ------------
# Clean Up + Run Jar
# ------------
if [ -f $BUILD_DIR/$JAR_OMUD ]; then
	mv $BUILD_DIR/$JAR_OMUD .
	rm -rf $BUILD_DIR
	java -jar $JAR_OMUD
fi
