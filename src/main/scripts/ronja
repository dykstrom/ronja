#!/usr/bin/env bash

ORIGINAL_DIR="$PWD"
function finish {
  cd "$ORIGINAL_DIR"
}
trap finish EXIT

SCRIPT_DIR=$(dirname -- "$0")
cd "$SCRIPT_DIR"

if [ "$JAVA_HOME" == "" ]; then
  JAVA_CMD="java"
else
  JAVA_CMD="$JAVA_HOME/bin/java"
fi

LOGGING_ARG="-Djava.util.logging.config.file=ronja.properties"
CONFIG_ARG="-Dronja.config.dir=."
JVM_ARGS="${LOGGING_ARG} ${CONFIG_ARG}"
JAR_FILE="ronja-${project.version}.jar"

${JAVA_CMD} ${JVM_ARGS} -jar ${JAR_FILE}
