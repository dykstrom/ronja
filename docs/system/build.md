# Build checks

`mvn verify` runs three checks besides the tests. The Maven Enforcer Plugin runs first, at the `validate` phase. Checkstyle and SpotBugs run last, at the `verify` phase. All three are set up in `pom.xml`. The Checkstyle rules and the SpotBugs exclusions live in `config/`.

`mvn test` and `mvn package` stop before `verify`, so they run the enforcer but not Checkstyle or SpotBugs.

## Enforcer

`maven-enforcer-plugin` runs these rules:

- `requireMavenVersion [3.9.0,)`
- `requireJavaVersion [25,)`, taken from the `java.version` property
- `banDuplicatePomDependencyVersions`
- `requireProfileIdsExist`
- `requireNoRepositories`
- `dependencyConvergence`

## Checkstyle

The rules are in `config/checkstyle/checkstyle.xml`. They are based on Google Java Style, with 120-column lines and 4-space indentation. The naming checks for lambda, catch, local and pattern variables also accept `_`, the unnamed variable that Java 22 added.

Checkstyle checks main sources only (`includeTestSourceDirectory=false`).

Most checks have warning severity. Warnings do not fail the build. These checks have error severity, and any error fails the build:

- `NeedBraces`
- `OneStatementPerLine`
- `CyclomaticComplexity`, with a maximum of 10
- `EmptyCatchBlock`
- `AvoidStarImport`
- `RedundantImport`
- `UnusedImports`

Two executions run at `verify`:

- `checkstyle-report` writes `target/reports/checkstyle.html`. The report lists every violation, warnings included.
- `checkstyle-check` fails the build on an error-severity violation.

To turn off one check for one method, annotate the method with `@SuppressWarnings("checkstyle:<CheckName>")`. Add a comment that gives the reason. Three hot-path methods suppress `CyclomaticComplexity` this way:

| Method | Reason |
|--------|--------|
| `Position.withMove` | Its switches update six local bitboards. Splitting them would need an array or a holder object for every move made during the search |
| `AlphaBetaFinder.alphaBeta` | It is the core search loop. Eight of its decision points are `if (DEBUG)` trace statements |
| `AttackGenerator.getDiagonalAttacks` | Splitting it into one method per pair of directions made `SlowFinderTest` about 5% slower |

`XBoardCommand` suppresses `AbbreviationAsWordInName` on the class, because XBoard is the name of the protocol.

Before you split a method in `engine/core/` or `Position`, run `SlowFinderTest.main` before and after the change. Compare the total times.

## SpotBugs

`spotbugs-maven-plugin` runs the `check` goal at `verify`. The settings are `effort=Max`, `threshold=Medium` and `includeTests=false`. Any finding that the exclude filter does not match fails the build (`failOnError=true`).

The exclude filter is `config/spotbugs/exclude.xml`. It holds these exclusions:

| Pattern | Scope | Reason |
|---------|-------|--------|
| `CT_CONSTRUCTOR_THROW` | `AbstractCommand` | Its constructor calls `requireNonNull`, and an abstract class cannot be `final`. Every concrete command class is `final`, so SpotBugs does not report them |
| `EI_EXPOSE_REP`, `EI_EXPOSE_REP2` | Whole project | The commands, the search and the XBoard front end share one mutable `Game` and one response writer by reference |
| `UPM_UNCALLED_PRIVATE_METHOD` | `AlphaBetaFinder` | The trace helpers `enter`, `stay` and `leave` are called only inside `if (DEBUG)` blocks. `DEBUG` is a constant `false`, so `javac` removes the calls |
| `MS_MUTABLE_ARRAY` | `Square` | `SQUARE_IDS` and `SQUARE_INDICES` are shared buffers. `bitboardToIds` and `bitboardToIndices` fill them, and the move and attack generators read them |

A new command class must be `final`. If it is not, and its constructor throws `InvalidCommandException`, SpotBugs reports `CT_CONSTRUCTOR_THROW` and the build fails.
