# 0003. Bundle a Java runtime in each release archive

*2026-10-19*

## Context

ADR 0001 made Ronja require Java 25, so a user had to install Java 25 before the engine would run under a chess GUI. Ronja uses only the JDK modules `java.base` and `java.logging`. `jlink` can build a runtime with just those modules, about 17 MB packed. A runtime is native code, so a release would need one archive per platform. We considered three other options:

- Keep the jar-only archive next to the platform archives, with launchers that fall back to `JAVA_HOME` or the `PATH`.
- Make the engine a Java module and use `maven-jlink-plugin`, which needs a `module-info.java` and moves the tests to the module path.
- Build native installers with `jpackage`.

We dropped the jar-only archive so that there is only one way to run Ronja.

## Decision

We will ship one archive per platform, each holding a runtime that `jlink` builds from `java.base` and `java.logging`. The launchers will always run that runtime and ignore `JAVA_HOME` and the `PATH`. The jar stays an ordinary classpath jar.

## Consequences

A user no longer installs Java, and another Java on the machine cannot break the engine. Building Ronja still requires JDK 25, as ADR 0001 says. Ronja supports exactly six platforms: Linux, macOS and Windows, each on x86_64 and arm64. Temurin 25 has no `jmods` folder, so `jlink` links from the JDK that runs the build. Each archive must therefore be built on its own platform, and CI must build all six for a release. A user who already has Java 25 still downloads the bundled runtime. A new dependency on another JDK module must be added to the `jlink` module list, or the engine fails at runtime.
