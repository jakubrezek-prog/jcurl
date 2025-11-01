#!/usr/bin/env bash
set -euo pipefail

README="README.md"
TARGET_DIR="target"
JAR=$(find "$TARGET_DIR" -maxdepth 1 -type f -name "jcurl-*-jar-with-dependencies.jar" | sort | tail -n 1)
REAL_JAR=$(realpath "$JAR")
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

WORKDIR="$(mktemp -d)"
trap 'rm -rf "$WORKDIR"' EXIT

echo "== Running README examples in $WORKDIR =="
$DRY_RUN && echo "(dry-run mode — commands will not be executed)"
echo

# Extract lines that are either setup or jcurl commands
grep -E '^\s*(echo |cat |touch |rm |java -jar jcurl\.jar)' "$README" | while read -r line; do
  [[ -z "$line" ]] && continue
  cd "$WORKDIR"

  if [[ "$line" =~ java\ -jar\ jcurl\.jar ]]; then
    # Replace jar name with actual build path
    cmd="${line/java -jar jcurl.jar/$JAVA_EXE -jar $REAL_JAR}"
    echo "→ $cmd"
  else
    # setup line (e.g., echo, touch, etc.)
    cmd="$line"
    echo " setup: $cmd"
  fi

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
