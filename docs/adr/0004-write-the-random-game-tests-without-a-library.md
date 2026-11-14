# 0004. Write the random-game tests without a library

*2026-11-14*

## Context

Ronja's unit tests cover most lines and branches, but chess-rule bugs still
slipped through, such as a castling right that stayed set after its rook was
captured. Such a bug shows up only after a particular sequence of moves. We
wanted tests that play many random legal games, check invariants at every
position, and shrink a failing game to a short one. We considered:

- jqwik, which runs on the JUnit Platform and shrinks failing inputs. From
  version 1.10.1 it prints a message on every run that asks AI agents not to
  use it, and Ronja is developed with AI agents.
- vavr-test, which is maintained but does not shrink
- QuickTheories and junit-quickcheck, which shrink, but have had no release
  since 2019 and 2020
- our own test helpers, with `java.util.Random` and a small shrinking loop

## Decision

We will generate, check and shrink random games with our own test helpers,
`RandomGames` and `RandomGameRunner`, and add no property-testing library.
Each run draws a new seed. A fast class runs in `mvn test`, and a class that
checks many more games runs in `mvn verify`.

## Consequences

The build gets no new dependency. A rule bug fails the build with a short game
in SAN. When we undid the fix for the castling bug described above, the runner
shrank a failing game from 146 plies to 2. We maintain the generation and
shrinking code ourselves, and it is simpler than a library's. Because the seed
changes, a build can fail on a game that no earlier build tried. That failure
is a real bug, and `-Dronja.random.seed` repeats it. Later tests that play
random games can reuse both helpers.
