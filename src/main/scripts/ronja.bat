@echo off

setlocal

set "ORIGINAL_DIR=%CD%"
set "SCRIPT_DIR=%~dp0"

rem Find the real script, following symbolic links, because the jar and the config
rem files are next to the script and not next to any link to it. Batch files cannot
rem read a link target, so this uses PowerShell, but only when the script is a link.
dir /a:l /b "%~f0" >nul 2>&1
if errorlevel 1 goto change_dir
set "RONJA_SCRIPT=%~f0"
for /f "usebackq delims=" %%D in (`powershell -NoProfile -NonInteractive -Command "$p = $env:RONJA_SCRIPT; while ((Get-Item -LiteralPath $p -Force).LinkType -eq 'SymbolicLink') { $t = (Get-Item -LiteralPath $p -Force).Target | Select-Object -First 1; if (-not [IO.Path]::IsPathRooted($t)) { $t = Join-Path (Split-Path -Parent $p) $t }; $p = [IO.Path]::GetFullPath($t) }; Split-Path -Parent $p"`) do set "SCRIPT_DIR=%%D"

:change_dir
cd /d "%SCRIPT_DIR%" || exit /b 1

if "%JAVA_HOME%" == "" goto use_path
if not exist "%JAVA_HOME%\bin\javaw.exe" goto use_path

:use_java_home
set "JAVA_CMD=%JAVA_HOME%\bin\javaw.exe"
goto set_args

:use_path
set "JAVA_CMD=javaw.exe"

:set_args
set "LOGGING_ARG=-Djava.util.logging.config.file=ronja.properties"
set "CONFIG_ARG=-Dronja.config.dir=."
set "JAR_FILE=ronja-${project.version}.jar"

"%JAVA_CMD%" %LOGGING_ARG% %CONFIG_ARG% -jar "%JAR_FILE%"

cd /d "%ORIGINAL_DIR%"

endlocal
