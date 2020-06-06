# Ronja

An XBoard/WinBoard chess engine written in Java.

## System Requirements

Ronja is a chess engine. It provides  only a simple, character based user 
interface. It is highly recommended that you run it from a chess GUI like 
[XBoard/WinBoard](https://www.gnu.org/software/xboard) or
[Arena](http://www.playwitharena.de). You will also need a Java runtime
installed to run Ronja.

Ronja version 0.7.0 and earlier requires Java 8; download it 
[here](https://java.com/download).

Ronja version 0.8.0 and later requires Java 11; download it 
[here](https://jdk.java.net/11).

## Installation

Download the latest release from the GitHub 
[releases page](https://github.com/dykstrom/ronja/releases), 
and unzip it into a directory of your choice. Let's call this directory
&lt;ronja_home&gt;.

#### XBoard

Find the XBoard resource file .xboardrc in your home directory.
Now find the option -firstChessProgramNames in this file. Add a 
line like the following in the list of chess programs:

    "Ronja" -fcp ./ronja.sh -fd <ronja_home>

#### WinBoard

Find your WinBoard .ini file. It will be located somewhere in 
your home directory in AppData/Roaming. Now find the option 
/firstChessProgramNames in this file. Add a line like the
following in the list of chess programs:

    "Ronja" -fcp ronja.bat -fd <ronja_home>

#### Arena

In Arena, you should go to Engines | Install New Engine... In the 
dialog that appears, navigate to &lt;ronja_home&gt; and select ronja.bat.
In the next dialog, set the type of the engine to Winboard. That's it!

## Description

Ronja is written entirely in Java, and has its own opening book. It 
implements a few of the commonly seen features of chess engines:

* Iterative deepening
* Alpha-beta pruning
* Move ordering

Ronja is designed in an object-oriented way, with classes representing 
positions, and other entities. This makes the engine rather slow, and
so may be changed in the future.

[![Build Status](https://travis-ci.com/dykstrom/ronja.svg?branch=master)](https://travis-ci.com/dykstrom/ronja)
