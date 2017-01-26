@echo off

set JAVA_CMD=javaw
set JAR_FILE=ronja-${project.version}.jar

set LOGGING_ARG=-Djava.util.logging.config.file=ronja.properties
set CONFIG_ARG=-Dronja.config.dir=.
set JVM_ARGS=%LOGGING_ARG% %CONFIG_ARG%

%JAVA_CMD% %JVM_ARGS% -jar %JAR_FILE%
