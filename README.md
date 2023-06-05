# OmegaMUD
Another attempt at a MegaMUD replacement in Java.

## Java Version Requirements
Tested with OpenJDK 1.6 (J6), 1.7 (J7), and 1.8 (J8).\
I assume equivalent Oracle Java versions should work.

## Apache Commons Net
Apache Commons Net lib is used for telnet communications.\
This is also the reason Java 1.6 is the minimum version.

## Environment Setup (Building/Running)
JAVA_HOME environment variable must be set to your Java location.\
The PATH environment variable on your system must have the Java bin path.

## Building
Run the batch (Win) or bash (Linux) script for the Java/Apache version you want to build for (see below).\
These scripts should be run from within the OmegaMUD dir:
> _BUILD_J6.bat/sh\
> _BUILD_J7.bat/sh\
> _BUILD_J8.bat/sh

*Example: If you are building for Java 1.8, just run _BUILD_J8.bat (or .sh if on Linux)*

## Running
From the command line:
> java -jar OmegaMUD_J6.jar

NOTE: the JAR filename will be different depending on which Java version it was built for.

## Tested Systems
WinXP, Win7, Win 8, Win10\
Slackware Linux 15.0 x64
Mac: Untested
