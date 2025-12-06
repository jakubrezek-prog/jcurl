#!/bin/bash
# jcurl.sh - Wrapper script to run jcurl CLI tool
#
# This script locates jcurl.jar in the same directory and executes it with
# provided arguments. Simplifies usage by handling Java path detection and
# JAR location automatically.

# Find the JAR in the same directory as this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JCURL_JAR="$SCRIPT_DIR/jcurl.jar"

if [[ -n "$JCURL_JAVA" ]]; then
  JAVA_EXE="$JCURL_JAVA"
elif [[ -n "$JAVA_HOME" ]]; then
  JAVA_EXE="$JAVA_HOME/bin/java"
else
  JAVA_EXE="java"
fi

# Check if JAR exists
if [ ! -f "$JCURL_JAR" ]; then
    echo "Error: jcurl.jar not found in $SCRIPT_DIR."
    exit 1
fi

# If no arguments are passed, show usage
if [ "$#" -eq 0 ]; then
    echo "Usage: $0 [jcurl options]"
    echo "Example: $0 -X GET https://api.example.com/data -H 'Accept: application/json'"
    exit 1
fi

# Build the java command
JAVA_CMD=("$JAVA_EXE" "-jar" "$JCURL_JAR")

# Append all command-line arguments
JAVA_CMD+=("$@")

# Show command being executed
echo "Executing: ${JAVA_CMD[@]}"

# Run jcurl
"${JAVA_CMD[@]}"
