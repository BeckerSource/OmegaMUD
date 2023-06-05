# OmegaMUD
Another attempt at a MegaMUD replacement in Java.

## Java Version Requirements
Tested with OpenJDK 1.6 (J6), 1.7 (J7), and 1.8 (J8).\
I assume equivalent Oracle Java versions should work.

## Apache Commons Net
Apache Commons Net lib is required and used for telnet communications.

## Environment Setup (Building/Running)
JAVA_HOME environment variable must be set to your Java location.\
The PATH environment variable on your system must have the Java bin path.

## Building
Run the batch (Win) or bash (Linux) script for the Java/Apache version you want to build for (see below).\
These scripts should be run from within the OmegaMUD dir:
```
_BUILD_J6.bat/sh
_BUILD_J7.bat/sh
_BUILD_J8.bat/sh
````
*Example: If you are building for Java 1.8, just run _BUILD_J8.bat (or .sh if on Linux)*

## Running
The Apache Commons Net lib for your built version must be present in a folder named  'lib'.\
The OmegaMUD directory should look like this for just running:
```
OmegaMUD/lib/commons-net-#.#.#-telnet.jar
OmegaMUD/OmegaMUD_J#.jar
```
Command line run example for Java 1.6 version of OmegaMUD:
> java -jar OmegaMUD_J6.jar

*NOTE: the JAR filename will be different depending on which Java version it was built for.*

## What Works
* Telnet connections.
* Basic ANSI/BBS support (adding more CSI/commands as they are found).
* Basic MajorMUD string (ANSI) parsing (room data, etc.).
* Debug frame with various tabs to show debug info.
* Toggle between single and multi-char input (send) type.

## Tested Systems
WinXP, Win7, Win8, Win10\
Slackware Linux 15.0 x64\
Mac: Untested
