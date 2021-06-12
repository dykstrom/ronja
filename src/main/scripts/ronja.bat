@echo off

setlocal

set ORIGINAL_DIR=%CD%

set SCRIPT_DIR=%~dp0
cd %SCRIPT_DIR%

if "%JAVA_HOME%" == "" goto use_path
if not exist "%JAVA_HOME%\bin\javaw.exe" goto use_path

:use_java_home
set JAVA_CMD=%JAVA_HOME%\bin\javaw.exe
goto set_args

:use_path
set JAVA_CMD=javaw.exe

:set_args
set LOGGING_ARG=-Djava.util.logging.config.file=ronja.properties
set CONFIG_ARG=-Dronja.config.dir=.
set JVM_ARGS=%LOGGING_ARG% %CONFIG_ARG%
set JAR_FILE=ronja-${project.version}.jar

%JAVA_CMD% %JVM_ARGS% -jar %JAR_FILE%

cd %ORIGINAL_DIR%

endlocal
