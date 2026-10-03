# 0001. Require Java 25 at runtime

*2026-09-28*

## Context

Ronja 0.9.x is compiled with `--release 17` and runs on Java 17 or later. For release 0.10.0, the build moves to JDK 25. JDK 25 can still compile with `--release 17`. That keeps Java 17 users, but limits the engine code to the language and library features of Java 17. The alternative is to compile with `--release 25`, which lets the code use newer features but requires every user to install Java 25. Users run Ronja under a chess GUI with a Java runtime they install themselves.

## Decision

From release 0.10.0, we will compile Ronja with `--release 25`, so the engine requires Java 25 or later at runtime.

## Consequences

Engine code can use language and library features added after Java 17. Users on Java 17 to 24 must install Java 25 before they upgrade to 0.10.0, and 0.9.x is the last line that runs on Java 17. The README lists the required Java version for each release line.
