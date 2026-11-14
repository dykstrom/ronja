# Move generation

`FullMoveGenerator` generates pseudo-legal moves. A generated move can leave the
mover's king in check, so the caller makes it with `Position.withMove` and drops
it if `isIllegalCheck()` returns true. The generator castles only if the
castling right is set in `Position` and a rook stands on the corner square.

## Perft

`PerftTest` counts the legal move sequences from the six standard positions on
the chessprogramming wiki, at depths 1 to 4 or 5, and compares them with the
published counts. It runs in `mvn test` and takes about one second. A wrong
count means a chess-rule bug in `FullMoveGenerator`, `Position.withMove` or
`AttackGenerator`. Surefire names a failing row by its number, so look up that
row in the `@CsvSource` to find the position and depth.

`PerftIT` runs in `mvn verify` and takes about 33 s on an Apple Silicon Mac. It
has two tests:

- `testDeepPerft` tests Kiwipete and positions 4 and 5 at depth 5, and position
  3 at depth 6. It also tests position 4 with the colours swapped
  (`FEN_PERFT_4_MIRRORED`), which the wiki lists with the same counts. It leaves
  out the start position and position 6, which have almost no castling, en
  passant or promotion.
- `testVajoletPositions` tests 6,838 positions from long games at depth 3. They
  are in `src/test/resources/perft/vajolet-perft.txt`, copied from
  `tests/perft.txt` in the Vajolet chess engine, which is GPL-3.0 like Ronja.
  Each line holds a FEN without the move clocks, then the counts for depths 1
  to 6. A failure lists every position with a wrong count.

`PerftTest` and `PerftIT` share the counting code in the test class `Perft`.

Perft does not catch a wrong castling right. The generator also needs a rook on
the corner square, so a stale right matters only after a second rook reaches
that square, which takes more plies than these positions reach.
`PositionTest` and `FullMoveGeneratorTest` test castling rights directly.

## Random-game invariants

`RandomGameProperties` checks these invariants at every position of many random
legal games:

- A castling right is set only if the king and that rook stand on their home squares
- The attack bitboards equal a fresh computation by `AttackGenerator`
- `FenParser.format` and then `FenParser.parse` give back the same position
- `CanParser.format` and then `CanParser.parse` give back the same move, for every legal move
- `SanParser.format` and then `SanParser.parse` give back the same move, for pawn moves and castling only
- `Game.unmakeMove` restores the previous position, attack bitboards included
- Each side has one king, and the side that just moved is not in check

Two test helper classes run the games. They use no library:

- `RandomGames` generates and plays a game. A game starts from `FEN_START` or
  one of the perft positions, and a list of 0 to 150 choices picks its moves.
  Each choice, modulo the number of legal moves, picks the next move. The game
  stops early at mate or stalemate.
- `RandomGameRunner` checks one property for a number of games. When the
  property fails, it shrinks the game. It tries an earlier seed position, fewer
  choices, and lower choices, and keeps each change that fails with the same
  exception class. It stops when no change helps, or after 10,000 games. The
  failure shows the seed, the shrunk game in SAN, and the failure of that game.

Two classes check the properties for different numbers of games:

| Class | Runs in | Games per property | Time on an Apple Silicon Mac |
|-------|---------|--------------------|------------------------------|
| `RandomGameInvariantsTest` | `mvn test` | 300 | About 1 s |
| `RandomGameInvariantsIT` | `mvn verify` | 9,500 | About 30 s |

Each run draws a new seed. To repeat the games of a failed run, pass the seed
from the failure as a system property, for example
`mvn test -Dtest=RandomGameInvariantsTest -Dronja.random.seed=<seed>`.
