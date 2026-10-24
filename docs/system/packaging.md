# Packaging

`mvn package` builds the archive for the platform that runs the build. The
choice to bundle a runtime is in [ADR 0003](../adr/0003-bundle-a-java-runtime-in-each-release-archive.md).

## Platform profiles

`pom.xml` has one profile per platform, activated by `os.name` or OS family and
`os.arch`. The profile id is the platform name, such as `macos-arm64`. Each
profile sets four properties that `src/assembly/bin.xml` reads:

| Property | Use |
|----------|-----|
| `platform` | The assembly id, so the archive is `ronja-<version>-<platform>` |
| `archive.format` | `zip` on Windows, `tar.gz` elsewhere |
| `launcher` | `ronja.bat` on Windows, `ronja` elsewhere. Only that launcher ships |
| `launcher.line.ending` | `windows` or `unix` |

The `platform` value in each entry of the CI matrix in
`.github/workflows/maven.yml` must equal the profile id. The upload step looks
for `target/ronja-*-<platform>.*` and fails the job if nothing matches.

## The runtime

The exec-maven-plugin execution `jlink` writes the runtime to `target/runtime`
at `package`. jlink refuses to write to a folder that exists, so the
maven-clean-plugin execution `clean-runtime` deletes it at `prepare-package`.
The runtime holds only the modules in the `--add-modules` argument. If engine
code starts to use another JDK module, add it there and to the `MODULES` check
in `LauncherIT`. `jdeps --print-module-deps` on the jar lists the modules the
code needs.

## Files the engine reads and writes

The launchers do not run the engine in the install folder, because a user may
not be allowed to write there. They create a folder per user and start Java in
it: `${XDG_STATE_HOME:-$HOME/.local/state}/ronja` on Linux and macOS, and
`%LOCALAPPDATA%\Ronja` on Windows. They pass absolute install paths for the
runtime, the jar, `java.util.logging.config.file`, and `ronja.config.dir`.

A relative path for a file the engine writes, such as `ronja.log` or the game
log, therefore resolves against the per-user folder. A relative path for a file
the engine reads must resolve against `ronja.config.dir` instead, the way
`AppConfig.getBookFilename` resolves `ronja.book.file`.

## LauncherIT

The assembly also writes an unpacked copy in the `dir` format. Failsafe passes
its path to `LauncherIT` as the system property `ronja.image.dir`. `LauncherIT`
starts the launcher with `JAVA_HOME` removed, and with `HOME`, or `LOCALAPPDATA`
on Windows, set to a temp folder. It reads the `# Java home` line that
`Ronja.main` prints at startup to check that the bundled runtime ran. It also
reads the `# Log folder` line, which `Ronja.main` prints from `user.dir`, and
checks that it names the per-user folder. Both lines must stay. It sends
`rejected <unique name>`, which `RejectedCommand` logs as a warning, to check
that `ronja.log` lands in the per-user folder.

## Releases

`mvn release:prepare` runs `mvn verify`, makes the two release commits, and
tags the release as `v<version>`. It pushes them over SSH, to the
`developerConnection` URL in `pom.xml`. A pushed `v*` tag makes CI build all
six platforms. Then the `release` job creates a draft GitHub release with the
six archives, titled with the version.

The `release` job runs only if all six builds pass. If one build fails for a
reason outside the tagged code, such as a flaky test, re-run only that job with
`gh run rerun <run-id> --failed`.

The `release` job writes the notes with `--generate-notes`, which lists only
merged pull requests. Most changes go straight to master, so the generated
notes miss them. A maintainer must rewrite the notes to list the changes users
see, and then publish the draft by hand.
