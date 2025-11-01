@echo off
REM ========================================================
REM  jcurl.bat - Windows runner for jcurl CLI
REM ========================================================

setlocal enabledelayedexpansion

REM Get the directory where this script resides
set SCRIPT_DIR=%~dp0

REM Find the jar (prefer jar-with-dependencies if present)
set JAR_FILE=
for /f "delims=" %%f in ('dir /b /a-d "%SCRIPT_DIR%target\*jar-with-dependencies.jar" 2^>nul') do (
    set JAR_FILE=%SCRIPT_DIR%target\%%f
)

if "%JAR_FILE%"=="" (
    echo [ERROR] Could not find jcurl jar file in target folder.
    echo Build it first with: mvn clean package
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
