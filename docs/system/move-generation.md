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

Perft does not catch a wrong castling right. The generator also needs a rook on
the corner square, so a stale right matters only after a second rook reaches
that square, which takes more plies than these positions reach.
`PositionTest` and `FullMoveGeneratorTest` test castling rights directly.
