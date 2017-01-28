#!/usr/bin/env bash

JAVA_CMD="java"
JAR_FILE="ronja-${project.version}.jar"

LOGGING_ARG="-Djava.util.logging.config.file=ronja.properties"
CONFIG_ARG="-Dronja.config.dir=."
JVM_ARGS="${LOGGING_ARG} ${CONFIG_ARG}"

${JAVA_CMD} ${JVM_ARGS} -jar ${JAR_FILE}
