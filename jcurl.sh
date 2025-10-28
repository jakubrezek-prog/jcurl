#!/bin/bash

# -------------------------------
# jcurl.sh
# -------------------------------

# Find the JAR in target directory
TARGET_DIR="target"
JCURL_JAR=$(find "$TARGET_DIR" -maxdepth 1 -type f -name "jcurl-*-jar-with-dependencies.jar" | sort | tail -n 1)

if [[ -n "$JCURL_JAVA" ]]; then
  JAVA_EXE="$JCURL_JAVA"
elif [[ -n "$JAVA_HOME" ]]; then
  JAVA_EXE="$JAVA_HOME/bin/java"
else
  JAVA_EXE="java"
fi

# Check if JAR exists
if [ -z "$JCURL_JAR" ]; then
    echo "Error: jcurl JAR not found in $TARGET_DIR. Have you built the project with Maven?"
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
