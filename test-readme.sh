#!/usr/bin/env bash
set -euo pipefail

README="README.md"
TARGET_DIR="target"
JAR=$(find "$TARGET_DIR" -maxdepth 1 -type f -name "jcurl-*-jar-with-dependencies.jar" | sort | tail -n 1)
DRY_RUN=false

if [[ -n "$JAVA_HOME" ]]; then
  JAVA_EXE="$JAVA_HOME/bin/java"
else
  JAVA_EXE="java"
fi

# Parse arguments
if [[ "${1:-}" == "--dry-run" ]]; then
  DRY_RUN=true
fi

# Check that jar exists
if [[ ! -f "$JAR" ]]; then
  echo "Error: $JAR not found. Run 'mvn package' first." >&2
  exit 1
fi

echo "== Running README examples =="
$DRY_RUN && echo "(dry-run mode — commands will not be executed)"
echo

# Find all commands starting with "java -jar jcurl.jar"
grep -E '^\s*java -jar jcurl\.jar' "$README" | while read -r cmd; do
  [[ -z "$cmd" ]] && continue

  # Replace the jar name with the actual path
  cmd="${cmd/java -jar jcurl.jar/$JAVA_EXE -jar $JAR}"

  echo "→ $cmd"
  if ! $DRY_RUN; then
    bash -c "$cmd"
  fi
  echo
done

if $DRY_RUN; then
  echo "Dry run complete. No commands were executed."
else
  echo "All README examples executed successfully."
fi
