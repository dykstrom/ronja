# Ronja

<div style="text-align: left">

[![Build Status](https://github.com/dykstrom/ronja/actions/workflows/maven.yml/badge.svg)](https://github.com/dykstrom/ronja/actions/workflows/maven.yml)
[![Open Issues](https://img.shields.io/github/issues/dykstrom/ronja)](https://github.com/dykstrom/ronja/issues)
[![Latest Release](https://img.shields.io/github/v/release/dykstrom/ronja?display_name=release)](https://github.com/dykstrom/ronja/releases)
![Downloads](https://img.shields.io/github/downloads/dykstrom/ronja/total)
![License](https://img.shields.io/github/license/dykstrom/ronja)
![Top Language](https://img.shields.io/github/languages/top/dykstrom/ronja)
[![Build with: JDK 25+](https://img.shields.io/badge/Build_with-JDK_25+-blue.svg)](https://adoptium.net)

</div>

An XBoard/WinBoard chess engine written in Java.


## System Requirements

Ronja is a chess engine. It provides  only a simple, character based user 
interface. It is highly recommended that you run it from a chess GUI like 
[XBoard/WinBoard](https://www.gnu.org/software/xboard) or
[Arena](http://www.playwitharena.de).

Ronja runs on these platforms:

* Linux on x86_64 and arm64
* macOS on x86_64 and arm64
* Windows on x86_64 and arm64

Running a version older than 0.10.0? See [Old Versions](OLD_VERSIONS.md).


## Installation

Download the archive for your platform from the GitHub
[releases page](https://github.com/dykstrom/ronja/releases). The archive
name ends with your operating system and architecture, for example
`ronja-0.10.0-windows-x86_64.zip` or `ronja-0.10.0-macos-arm64.tar.gz`.
Unpack it into a directory of your choice. Let's call this directory
`<ronja_home>`.

### XBoard

Find the XBoard resource file `.xboardrc` in your home directory.
Now find the option `-firstChessProgramNames` in this file. Add a 
line like the following in the list of chess programs:

    "Ronja" -fcp ./ronja -fd <ronja_home>

### WinBoard

Find your WinBoard .ini file. It will be located somewhere in 
your home directory in AppData/Roaming. Now find the option 
`/firstChessProgramNames` in this file. Add a line like the
following in the list of chess programs:

    "Ronja" -fcp ronja.bat -fd <ronja_home>

### Arena

In Arena, you should go to Engines | Install New Engine... In the 
dialog that appears, navigate to `<ronja_home>` and select `ronja.bat`.
In the next dialog, set the type of the engine to Winboard. That's it!


## Log Files

Ronja writes its log file `ronja.log` to a folder of your own, so that
`<ronja_home>` can be a folder you cannot write to, such as `/usr/local`:

* Linux and macOS: `~/.local/state/ronja`, or `$XDG_STATE_HOME/ronja` if
  you have set `XDG_STATE_HOME`
* Windows: `%LOCALAPPDATA%\Ronja`

If you turn on the game log in `ronja.properties`, Ronja writes it to the
same folder. When you start Ronja in a terminal, it prints the folder on a
line that starts with `# Log folder`.


## Description

Ronja is written entirely in Java, and has its 
own opening book. It implements a few of the commonly seen features of 
chess engines:

* Iterative deepening
* Alpha-beta pruning
* Move ordering

Ronja is designed in an object-oriented way, with classes representing 
positions, and other entities. This makes the engine rather slow, and
so may be changed in the future.
