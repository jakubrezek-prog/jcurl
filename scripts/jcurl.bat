@echo off
REM jcurl.bat - Windows wrapper script to run jcurl CLI tool
REM
REM This script locates jcurl.jar in the same directory and executes it with
REM provided arguments. Simplifies usage by handling Java path detection and
REM JAR location automatically.

setlocal enabledelayedexpansion

REM Get the directory where this script resides
set SCRIPT_DIR=%~dp0

REM Find the jar in the same directory
set JAR_FILE=%SCRIPT_DIR%jcurl.jar

if not exist "%JAR_FILE%" (
    echo [ERROR] Could not find jcurl.jar in %SCRIPT_DIR%.
    exit /b 1
)

rem Use JCURL_JAVA if set, otherwise JAVA_HOME, otherwise plain "java"
if defined JCURL_JAVA (
  set "JAVA_CMD=%JCURL_JAVA%"
) else if defined JAVA_HOME (
  set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
) else (
  set "JAVA_CMD=java"
)

REM Run the jar

REM echo Using Java: %JAVA_CMD%
"%JAVA_CMD%" -jar "%JAR_FILE%" %*

endlocal
