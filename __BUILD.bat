@echo off
setlocal EnableDelayedExpansion
cls

REM ------------
REM Vars
REM ------------
set "MAN=%1"
set "JAR_AC=%2"
set "JAR_OMUD=%3"
set "SRC=src"
set "BUILD_DIR=_BUILD"

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
set "SRC_DIRS=%SRC%\*.java"
for /d %%d in (%SRC%\*) do (set "SRC_DIRS=!SRC_DIRS! %%d\*.java")
javac -Xlint:deprecation -d %BUILD_DIR% -cp %BUILD_DIR%/lib/%JAR_AC% %SRC_DIRS%

REM ------------
REM Check Class Files
REM ------------
set "SRC_FILES="
for /r %SRC% %%f in (*.java) do (set "SRC_FILES=!SRC_FILES! %BUILD_DIR%\%%~nf.class")
for %%f in (%SRC_FILES%) do (
	if not exist %%f (
		echo:
		echo ---------------------------------------------
	    echo Failed: class file not found: %%f
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
