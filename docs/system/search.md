# Search and evaluation

`AlphaBetaFinder` implements `Finder` using alpha-beta search with iterative
deepening. `findBestMoveWithinTime` searches depth 1 first, then deepens while
`TimeUtils.estimateTimeForNextDepth` predicts the next depth still fits in the
time left. That estimate is the last depth's time multiplied by three. A search
that runs past the limit throws `OutOfTimeException`, and the finder keeps the best
move from the last completed depth. `FullMoveGenerator` generates the moves once
and reuses them across depths.

`Move` encodes a move as a single `int`. The bit order is deliberate: the captured
piece occupies the highest bits, so sorting the raw integers puts captures before
quiet moves. `Evaluator.evaluate` returns `CHECK_MATE_VALUE` for checkmate,
otherwise it sums material and attacked squares. A pawn is worth 1000, a bishop
pair adds 500, and each attacked square counts 10.

## The search bounds

`AlphaBetaFinder` starts each search at `ALPHA_START` and `BETA_START`, which are
minus and plus 3,000,000. The largest magnitude `Evaluator` returns is
`ILLEGAL_CHECK_VALUE`, at 2,000,000. The rule that ties the two together is in
[architecture/search.md](../architecture/search.md). Read it before changing an
evaluation constant.

## No quiescence search

The finder evaluates the position at the depth limit, whatever is happening on the
board. There is no quiescence search, so a capture sequence that crosses the limit
is scored mid-exchange. This is a known gap, not an oversight.

## Measuring a search change

`SlowFinderTest` is annotated `@Ignore`, so the build skips it. Run it by hand
after changing the search. Its assertions only check that a move was found, so
they do not catch a worse move. The measurement comes from its `main` method,
which searches ten fixed positions through `runStep` and prints the seconds taken
and the move chosen for each. Compare that output against your previous run:
faster is better, and a changed move is worth understanding before you keep it.
