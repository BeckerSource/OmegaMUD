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
* Telnet connectivity.
* Basic ANSI/terminal support (adding more CSI/commands as they are found).
* Debug frame with various tabs to show debug info.
* Toggle between single and multi-char input (send) type.
* Command throttling/queueing. Helpful for commands like search, pick, and bash.
* MegaMUD RoomID generation for backward MegaMUD support.
* MajorMUD string parsing:
  * BBS Menu Detection
  * Editor/Prompt Detection
  * Statline
  * Rooms
  * Inventory

## Tested Systems
WinXP, Win7, Win8, Win10\
Slackware Linux 15.0 x64\
Mac: Untested

## Screenshots
![BBS Login](./screenshots/omegamud-bbs_login.png)
![BBS Main Menu](./screenshots/omegamud-bbs_main_menu.png)
![BBS Account Edit](./screenshots/omegamud-bbs_account_edit.png)
![MajorMUD Newhaven Arena](./screenshots/omegamud-mud_newhaven_arena.png)
![MajorMUD Train Stats](./screenshots/omegamud-mud_train_stats.png)
