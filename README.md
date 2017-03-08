# Ronja

An XBoard/WinBoard chess engine written in Java.

## System Requirements

Ronja is a chess engine. It provides  only a simple, character based user 
interface. It is highly recommended that you run it from a chess GUI like 
[XBoard/WinBoard](https://www.gnu.org/software/xboard) or
[Arena](http://www.playwitharena.com/). You will also need
Java 8 installed to run Ronja. The Java runtime can be downloaded
from [Oracle](https://java.com/download).

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

Ronja is my first try to write a chess engine. It is written entirely 
in Java, and has its own opening book. It implements some of the
commonly seen features of chess engines:

* Incremental move generation
* Iterative deepening
* Alpha-beta pruning

Ronja is designed in an object-oriented way, with classes representing 
moves, positions, and other entities. This probably slows the engine
down, and so may be changed in the future.

[![Build Status](https://travis-ci.org/dykstrom/ronja.svg?branch=master)](https://travis-ci.org/dykstrom/ronja)
