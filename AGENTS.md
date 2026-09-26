# AGENTS.md

## What is this

Ronja is a chess engine written in Java. It speaks the XBoard/WinBoard protocol
over stdin and stdout. You run it under a chess GUI such as XBoard, WinBoard, or
Arena, not directly. It ships with its own opening book, and implements iterative
deepening, alpha-beta pruning, and move ordering. It requires Java 17 or later.

## Stack

| Piece | Choice |
|-------|--------|
| Language | Java 17. CI also builds on 21 |
| Build | Maven. No wrapper in the repo, so builds use the `mvn` on your PATH |
| Dependencies | None at runtime. Every `pom.xml` dependency is `test` scope, so the shipped engine runs on the JDK alone |
| Testing | JUnit 4 with Hamcrest. Awaitility for the protocol tests, picocli for test-side tooling |
| Test split | Surefire runs `*Test`, Failsafe runs `*IT` |
| Interface | The XBoard/WinBoard protocol, over stdin and stdout |
| Data | No database. The opening book is the text file `book.csv` |
| Logging | `java.util.logging`, configured by `ronja.properties` |
| Packaging | maven-assembly-plugin builds a zip and a tar.gz holding the jar, the `ronja` and `ronja.bat` launchers, and `book.csv` |

## Directory index

All Java lives under `src/main/java/se/dykstrom/ronja/`, which `…/` stands for below.

| Path | What's there |
|------|-------------|
| `…/common/` | The chess domain. `model/` (`Board`, `Position`, `Game`, `Move`), `parser/` (FEN, SAN, CAN, PGN), `book/` (opening book) |
| `…/engine/` | The engine that plays. `core/` (search, evaluation, move generation), `time/` (time controls), `ui/` (XBoard front end, `Ronja` main class, one class per command), `utils/` (`AppConfig`, `Version`) |
| `src/main/resources/` | `version.properties`, filtered by Maven so `${project.version}` is replaced at build time |
| `src/main/scripts/` | What ships beside the jar: the `ronja` and `ronja.bat` launchers, `ronja.properties`, `book.csv`. Also Maven-filtered, copied to `target/scripts/` |
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
| All tests | `mvn verify` — adds the `*IT` tests, including `RonjaIT` and `XBoardProtocolIT`, which drive the engine over the protocol |
| Search benchmarks | `SlowFinderTest` is annotated `@Ignore`, so the build skips it. Run it by hand when changing the search |
| Build the release archives | `mvn package` — the assembly plugin writes the zip and tar.gz to `target/` |
| Run the engine | Start `se.dykstrom.ronja.engine.ui.Ronja` from the IDE, or unpack `target/ronja-*-bin.zip` and drive that from XBoard. The launcher needs the jar, `ronja.properties`, and `book.csv` in one directory, which only the unpacked archive gives you |

## Gotchas

- Everything the engine writes to stdout reaches XBoard as a protocol message. A
  `System.out.println` that is not protocol output must start with `#`. XBoard
  reads such a line as a comment and ignores it.
