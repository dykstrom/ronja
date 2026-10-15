# 0002. Block the build on static analysis findings

*2026-10-06*

## Context

Ronja had no static analysis. We added Checkstyle and SpotBugs. A first run found 33 Checkstyle errors, 278 Checkstyle warnings and 25 SpotBugs findings. Nine of the errors came from `CyclomaticComplexity`, which allows at most 10 decision points in a method. Seven of those nine methods are in the search and move generation, where speed matters. We had four ways to handle them:

- refactor all nine methods
- refactor only the methods that split without cost, and suppress the check on the others
- suppress the check on all nine
- raise the limit until all nine pass

A test run showed that splitting `AttackGenerator.getDiagonalAttacks` made `SlowFinderTest` about 5% slower.

## Decision

We will fail `mvn verify` on any Checkstyle error and on any SpotBugs finding that `config/spotbugs/exclude.xml` does not exclude. Checkstyle warnings will not fail the build. They go into an HTML report for review. We will refactor a method that breaks a rule if the split does not slow down the search. If it does, we will suppress the check on that one method and write the reason next to it.

## Consequences

New code cannot add a Checkstyle error or a SpotBugs finding without the build failing. The remaining warnings stay visible in `target/reports/checkstyle.html` until someone decides how to handle them. `Position.withMove`, `AlphaBetaFinder.alphaBeta` and `AttackGenerator.getDiagonalAttacks` keep their complexity, and each carries a suppression. Before splitting a method in `engine/core/` or `Position`, a contributor must run `SlowFinderTest` before and after the change. Every SpotBugs exclusion must give its reason in the exclude file.
