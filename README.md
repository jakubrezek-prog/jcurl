# jcurl 

**jcurl** is a lightweight, self-contained `curl`-like command-line tool written in Java.  
It’s designed for environments where you already have Java installed (like Docker containers or Kubernetes pods) but not `curl` or `wget`.

Run HTTP(S) requests directly from Java:

```bash
java -jar jcurl.jar -X GET "https://api.example.com/data" -H "Accept: application/json"
```

## Supported curl options
```bash
java -jar jcurl.jar  -h

Usage: jcurl [-hikvV] [-d=<data>] [-u=<basicAuth>] [-X=<method>]
[-H=<headerPairs>[,<headerPairs>...]]... URL
Lightweight curl-like CLI for REST API debugging in Java.
URL                  Target URL
-d, --data=<data>        Request body
-h, --help               Show this help message and exit.
-H, --header=<headerPairs>[,<headerPairs>...]
HTTP headers
-i, --include            Include response headers
-k, --insecure           Ignore SSL certificate errors
-u, --user=<basicAuth>   Basic auth username:password
-v, --verbose            Verbose output
-V, --version            Print version information and exit.
-X, --request=<method>   HTTP method
```
## Usage Examples

### Simple GET request
```bash
java -jar jcurl.jar https://jsonplaceholder.typicode.com/posts/1
```

### POST with JSON body
```bash
java -jar jcurl.jar -X POST \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}' \
  https://jsonplaceholder.typicode.com/posts
```

### Include headers and verbose output
```bash
java -jar jcurl.jar -v -i https://httpbin.org/get
```

### Ignore SSL certificate errors

```bash
java -jar jcurl.jar -k https://self-signed.badssl.com/
```

### Basic authentication

```bash
java -jar jcurl.jar -u admin:secret https://api.example.com/private
```

## Build from source

Requires Java 21 and Maven 3.9+.
```bash
git clone https://github.com/jakubrezek-prog/jcurl.git
cd jcurl
mvn clean package
```

## Wrapper scripts
To simplify usage, add a jcurl.bat (Windows) or jcurl.sh (Linux/macOS) script:

```bash
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

REM Run the jar
java -jar "%JAR_FILE%" %*

endlocal
```


```bash
#!/bin/sh
# Simple wrapper for jcurl
JAR="$(dirname "$0")/target/jcurl-<version>-jar-with-dependencies.jar"
exec java -jar "$JAR" "$@"
```

