# Ronja

<div style="text-align: left">

[![Build Status](https://github.com/dykstrom/ronja/actions/workflows/maven.yml/badge.svg)](https://github.com/dykstrom/ronja/actions/workflows/maven.yml)
[![Open Issues](https://img.shields.io/github/issues/dykstrom/ronja)](https://github.com/dykstrom/ronja/issues)
[![Latest Release](https://img.shields.io/github/v/release/dykstrom/ronja?display_name=release)](https://github.com/dykstrom/ronja/releases)
![Downloads](https://img.shields.io/github/downloads/dykstrom/ronja/total)
![License](https://img.shields.io/github/license/dykstrom/ronja)
![Top Language](https://img.shields.io/github/languages/top/dykstrom/ronja)
[![JDK compatibility: 17+](https://img.shields.io/badge/JDK_compatibility-17+-blue.svg)](https://adoptium.net)

</div>

An XBoard/WinBoard chess engine written in Java.


## System Requirements

Ronja is a chess engine. It provides  only a simple, character based user 
interface. It is highly recommended that you run it from a chess GUI like 
[XBoard/WinBoard](https://www.gnu.org/software/xboard) or
[Arena](http://www.playwitharena.de). You will also need a 
[Java runtime](https://adoptium.net) installed to run Ronja.

* Ronja versions 0.7.0 and earlier require Java 8.
* Ronja versions 0.8.x require Java 11.
* Ronja versions 0.9.0 and later require Java 17.


## Installation

Download the latest release from the GitHub 
[releases page](https://github.com/dykstrom/ronja/releases), 
and unzip it into a directory of your choice. Let's call this directory
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


## Description

Ronja is written entirely in [Java](https://adoptium.net), and has its 
own opening book. It implements a few of the commonly seen features of 
chess engines:

* Iterative deepening
* Alpha-beta pruning
* Move ordering

Ronja is designed in an object-oriented way, with classes representing 
positions, and other entities. This makes the engine rather slow, and
so may be changed in the future.
