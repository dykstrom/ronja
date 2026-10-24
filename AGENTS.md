# AGENTS.md

## What is this

Ronja is a chess engine written in Java. It speaks the XBoard/WinBoard protocol
over stdin and stdout. You run it under a chess GUI such as XBoard, WinBoard, or
Arena, not directly. It ships with its own opening book, and implements iterative
deepening, alpha-beta pruning, and move ordering. Building it requires JDK 25 or
later. Running it requires no Java, because each release archive includes a Java
runtime that `jlink` builds.

## Stack

| Piece | Choice |
|-------|--------|
| Language | Java 25. CI builds on six platforms |
| Build | Maven. No wrapper in the repo, so builds use the `mvn` on your PATH |
| Dependencies | None at runtime. Every `pom.xml` dependency is `test` scope, so the shipped engine runs on the bundled Java runtime alone |
| Testing | JUnit 6 (Jupiter) assertions only. Awaitility for the protocol tests, picocli for test-side tooling. Hamcrest is on the test classpath only because Awaitility depends on it, so tests do not import it |
| Test split | Surefire runs `*Test`, Failsafe runs `*IT` |
| Interface | The XBoard/WinBoard protocol, over stdin and stdout |
| Data | No database. The opening book is the text file `book.csv` |
| Logging | `java.util.logging`, configured by `ronja.properties` |
| Packaging | `mvn package` builds `target/ronja-<version>-<os>-<arch>` for the platform that runs the build: the jar, the platform's launcher, `ronja.properties`, `book.csv`, and a `jlink` runtime in `runtime/`. Zip on Windows, tar.gz elsewhere. See `docs/system/packaging.md` |
| Releases | Release tags are `v<version>`. A pushed `v*` tag makes CI create a draft GitHub release with the six platform archives |
| Build checks | maven-enforcer-plugin at `validate`. Checkstyle and SpotBugs at `verify`, on main sources only. A Checkstyle error or any SpotBugs finding fails the build. See `docs/system/build.md` |

## Directory index

All Java lives under `src/main/java/se/dykstrom/ronja/`, which `…/` stands for below.

| Path | What's there |
|------|-------------|
| `…/common/` | The chess domain. `model/` (`Board`, `Position`, `Game`, `Move`), `parser/` (FEN, SAN, CAN, PGN), `book/` (opening book) |
| `…/engine/` | The engine that plays. `core/` (search, evaluation, move generation), `time/` (time controls), `ui/` (XBoard front end, `Ronja` main class, one class per command), `utils/` (`AppConfig`, `Version`) |
| `src/main/resources/` | `version.properties`, filtered by Maven so `${project.version}` is replaced at build time |
| `src/main/scripts/` | What ships beside the jar: the `ronja` and `ronja.bat` launchers, `ronja.properties`, `book.csv`. Also Maven-filtered, copied to `target/scripts/`. The launchers always run `runtime/bin/java`, and ignore `JAVA_HOME` and the `PATH` |
| `config/` | `checkstyle/checkstyle.xml` (the Checkstyle rules) and `spotbugs/exclude.xml` (the accepted SpotBugs findings, each with its reason) |
| `src/test/java/se/dykstrom/ronja/test/` | Test helpers, not tests: `AbstractTestCase`, `TestUtils`, `LoggingConfig` |
| `docs/` | Durable project context. Sub-folder layout below shows where each kind of doc goes. |

`engine` depends on `common`. `common` must not import from `engine`.

```
docs/
├── system/         ← what the code does today (updated as code changes)
├── architecture/   ← what the system must do (updated when rules change)
├── adr/            ← architecture decisions (immutable once shipped)
├── reference/      ← long-form rationale (append-only)
└── working-notes/  ← research; NOT authoritative — rules live in architecture/ + adr/
```

## Commands

| What | Command |
|------|---------|
| Unit tests | `mvn test` — runs `*Test` only |
| All tests and checks | `mvn verify` — adds the `*IT` tests, including `RonjaIT` and `XBoardProtocolIT`, which drive the engine over the protocol, and `LauncherIT`, which starts the unpacked archive through its launcher. Then runs Checkstyle and SpotBugs |
| Checkstyle report | `mvn verify` writes `target/reports/checkstyle.html`, which lists every violation, warnings included |
| Search benchmarks | `SlowFinderTest` is annotated `@Disabled`, so the build skips it. Run it by hand when changing the search |
| Build the release archive | `mvn package` — writes the archive for the current platform to `target/`, plus an unpacked copy in a folder of the same name |
| Run the engine | Start `se.dykstrom.ronja.engine.ui.Ronja` from the IDE, or point XBoard at the launcher in the unpacked copy, `target/ronja-<version>-<os>-<arch>/ronja-<version>/`. The launcher needs the jar, `ronja.properties`, `book.csv`, and `runtime/` in one directory, which only the archive gives you |

## Gotchas

- Everything the engine writes to stdout reaches XBoard as a protocol message. A
  `System.out.println` that is not protocol output must start with `#`. XBoard
  reads such a line as a comment and ignores it.
