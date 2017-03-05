# Ronja

An XBoard/WinBoard chess engine written in Java.

## System Requirements

Ronja runs in XBoard/WinBoard on Linux/Windows. You will also need
Java 8 installed to run Ronja. The Java runtime can be downloaded
from [Oracle](https://java.com/download). Ronja has not been tested
with Arena.

## Installation

Download the latest release from the GitHub 
[releases page](https://github.com/dykstrom/ronja/releases), 
and unzip it into a directory of your choice. Let's call this directory
<ronja_home>.

If you run Windows, find your WinBoard .ini file, and find the option 
/firstChessProgramNames in this file. Add a line like the following 
in the list of chess programs:

    "Ronja" -fcp ronja.bat -fd <ronja_home>

If you run Linux, find the XBoard resource file .xboardrc, and find the
option -firstChessProgramNames in this file. Add a line like the
following in the list of chess programs:

    "Ronja" -fcp ./ronja.sh -fd <ronja_home>

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
