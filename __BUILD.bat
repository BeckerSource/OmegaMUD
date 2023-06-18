@echo off
cls

REM ------------
REM Vars
REM ------------
set "MAN=%1"
set "JAR_AC=%2"
set "JAR_OMUD=%3"
set "SRC=src"
set "BUILD_DIR=_BUILD"
set "FILES_LIST=___SRC_FILES.txt"

REM ------------
REM Create Build Dir Structure
REM ------------
if exist %JAR_OMUD% 	del %JAR_OMUD%
if exist %BUILD_DIR% 	rmdir /s /q %BUILD_DIR%
mkdir %BUILD_DIR%\lib
mkdir %BUILD_DIR%\fonts
copy lib\%JAR_AC% %BUILD_DIR%\lib >NUL
copy fonts\* %BUILD_DIR%\fonts >NUL
copy src\%MAN% %BUILD_DIR% >NUL
 
REM ------------
REM Compile
REM ------------
javac -Xlint:deprecation -d %BUILD_DIR% -cp %BUILD_DIR%/lib/%JAR_AC% ^
    %SRC%/*.java ^
    %SRC%/ansi/*.java ^
    %SRC%/buffer/*.java ^
    %SRC%/gui/*.java ^
    %SRC%/mega/*.java ^
    %SRC%/mmud/*.java ^
    %SRC%/mmud_blocks/*.java ^
    %SRC%/telnet/*.java

REM ------------
REM Check Class Files
REM ------------
if not exist %FILES_LIST% (
	echo:
	echo ---------------------------------------------
    echo Failed: file with required class files list found: %FILES_LIST%
    goto :EOF
)
for /f "delims=" %%I in (%FILES_LIST%) do (
	if not exist %BUILD_DIR%\%%I.class (
		echo:
		echo ---------------------------------------------
	    echo Failed: class file not found: %BUILD_DIR%\%%I.class
	    goto :EOF
	)
)

REM ------------
REM Create Jar
REM ------------
cd %BUILD_DIR%
jar cfm %JAR_OMUD% %MAN% *.class fonts/*
cd ..\

REM ------------
REM Clean Up + Run Jar
REM ------------
if exist %BUILD_DIR%\%JAR_OMUD% (
	move /Y %BUILD_DIR%\%JAR_OMUD% .
	rmdir /s /q %BUILD_DIR%
	java -jar %JAR_OMUD%
)

REM ------------
REM EOF/Exit
REM ------------
:EOF
