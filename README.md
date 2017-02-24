# Ronja

An XBoard/WinBoard chess engine written in Java.

## Prerequisites

Ronja runs in XBoard/WinBoard on Linux/Windows. You will also need
Java 8 installed to run Ronja. The Java runtime can be downloaded
from [Oracle](https://java.com/download).

## Installation

Download the latest release from the GitHub 
[releases page](https://github.com/dykstrom/ronja/releases), 
and unzip it into a directory of your choice. Let's call this directory
<ronja_home>.

If you run Windows, find your WinBoard .ini file, and find the option 
"/firstChessProgramNames" in this file. Add a line like the following 
in the list of chess programs:

    "Ronja" -fcp ronja.bat -fd <ronja_home>

If you run Linux, start XBoard and open the "Edit engine list..." dialog.

To be continued...