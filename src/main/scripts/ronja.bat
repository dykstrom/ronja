@echo off

setlocal

set "ORIGINAL_DIR=%CD%"
set "SCRIPT_DIR=%~dp0"

rem Find the real script, following symbolic links, because the jar, the config
rem files, and the runtime are next to the script and not next to any link to it.
rem Batch files cannot read a link target, so this uses PowerShell, but only when
rem the script is a link.
dir /a:l /b "%~f0" >nul 2>&1
if errorlevel 1 goto change_dir
set "RONJA_SCRIPT=%~f0"
for /f "usebackq delims=" %%D in (`powershell -NoProfile -NonInteractive -Command "$p = $env:RONJA_SCRIPT; while ((Get-Item -LiteralPath $p -Force).LinkType -eq 'SymbolicLink') { $t = (Get-Item -LiteralPath $p -Force).Target | Select-Object -First 1; if (-not [IO.Path]::IsPathRooted($t)) { $t = Join-Path (Split-Path -Parent $p) $t }; $p = [IO.Path]::GetFullPath($t) }; Split-Path -Parent $p"`) do set "SCRIPT_DIR=%%D"

:change_dir
rem Remove the trailing backslash that %~dp0 has, because a backslash before the
rem closing quote of an argument would make Java read the quote as part of it
if "%SCRIPT_DIR:~-1%" == "\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

rem Run in a folder per user, because Ronja writes its log and game files to the
rem working folder, and the user may not be allowed to write to the install folder
set "STATE_DIR=%LOCALAPPDATA%\Ronja"
if not exist "%STATE_DIR%" mkdir "%STATE_DIR%"
cd /d "%STATE_DIR%" || exit /b 1

rem Always use the bundled runtime, and never a Java found through JAVA_HOME or the PATH
set "JAVA_CMD=%SCRIPT_DIR%\runtime\bin\javaw.exe"
set "LOGGING_ARG=-Djava.util.logging.config.file=%SCRIPT_DIR%\ronja.properties"
set "CONFIG_ARG=-Dronja.config.dir=%SCRIPT_DIR%"
set "JAR_FILE=%SCRIPT_DIR%\ronja-${project.version}.jar"

"%JAVA_CMD%" "%LOGGING_ARG%" "%CONFIG_ARG%" -jar "%JAR_FILE%"

cd /d "%ORIGINAL_DIR%"

endlocal
