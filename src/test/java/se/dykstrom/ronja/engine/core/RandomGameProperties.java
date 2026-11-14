/*
 * Copyright (C) 2026 Johan Dykstrom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.dykstrom.ronja.engine.core;

import java.text.ParseException;
import java.util.List;

import org.junit.jupiter.api.Test;
import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.common.model.Color;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Piece;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.parser.CanParser;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.common.parser.IllegalMoveException;
import se.dykstrom.ronja.common.parser.SanParser;
import se.dykstrom.ronja.test.RandomGameRunner;
import se.dykstrom.ronja.test.RandomGames.RandomGame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Invariants that must hold at every position of a random legal game. Each test checks one
 * property with {@link RandomGameRunner}. The subclasses {@link RandomGameInvariantsTest} and
 * {@link RandomGameInvariantsIT} check the properties for different numbers of games.
 *
 * @author Johan Dykstrom
 * @see RandomGameRunner
 */
abstract class RandomGameProperties {

    private final AttackGenerator attackGenerator = new AttackGenerator();

    /**
     * Returns the number of random games to check for each property.
     */
    protected abstract int tries();

    @Test
    void castlingRightRequiresKingAndRookAtHome() {
        RandomGameRunner.check("castlingRightRequiresKingAndRookAtHome", tries(), this::castlingRights);
    }

    @Test
    void attackBitboardsMatchFreshComputation() {
        RandomGameRunner.check("attackBitboardsMatchFreshComputation", tries(), this::attackBitboards);
    }

    @Test
    void fenFormatThenParseGivesSamePosition() {
        RandomGameRunner.check("fenFormatThenParseGivesSamePosition", tries(), this::fenRoundTrip);
    }

    @Test
    void sanFormatThenParseGivesSameMove() {
        RandomGameRunner.check("sanFormatThenParseGivesSameMove", tries(), this::sanRoundTrip);
    }

    @Test
    void canFormatThenParseGivesSameMove() {
        RandomGameRunner.check("canFormatThenParseGivesSameMove", tries(), this::canRoundTrip);
    }

    @Test
    void unmakeMoveRestoresPreviousPosition() {
        RandomGameRunner.check("unmakeMoveRestoresPreviousPosition", tries(), this::unmakeRestores);
    }

    @Test
    void positionsAreLegal() {
        RandomGameRunner.check("positionsAreLegal", tries(), this::legalPositions);
    }

    private void castlingRights(RandomGame game) {
        for (Position p : game.positions()) {
            String fen = FenParser.format(p);
            if (p.isKingSideCastlingAllowed(Color.WHITE)) {
                assertTrue(isAtHome(p.white, p, Square.E1, Square.H1), "K right without king and rook: " + fen);
            }
            if (p.isQueenSideCastlingAllowed(Color.WHITE)) {
                assertTrue(isAtHome(p.white, p, Square.E1, Square.A1), "Q right without king and rook: " + fen);
            }
            if (p.isKingSideCastlingAllowed(Color.BLACK)) {
                assertTrue(isAtHome(p.black, p, Square.E8, Square.H8), "k right without king and rook: " + fen);
            }
            if (p.isQueenSideCastlingAllowed(Color.BLACK)) {
                assertTrue(isAtHome(p.black, p, Square.E8, Square.A8), "q right without king and rook: " + fen);
            }
        }
    }

    private void attackBitboards(RandomGame game) {
        for (Position p : game.positions()) {
            String fen = FenParser.format(p);
            assertEquals(attackGenerator.getAttackedSquares(Color.WHITE, p), p.whiteAttack, "white attacks: " + fen);
            assertEquals(attackGenerator.getAttackedSquares(Color.BLACK, p), p.blackAttack, "black attacks: " + fen);
        }
    }

    private void fenRoundTrip(RandomGame game) throws ParseException {
        for (Position p : game.positions()) {
            assertSamePosition(p, FenParser.parse(FenParser.format(p)));
        }
    }

    // SanParser.parse cannot parse piece moves yet, so this property checks only pawn moves and castling.
    // Remove the filter when SanParser.parse can parse piece moves.
    private void sanRoundTrip(RandomGame game) throws IllegalMoveException {
        List<Position> positions = game.positions();
        for (int i = 0; i < positions.size(); i++) {
            Position p = positions.get(i);
            for (int move : game.legalMoves().get(i)) {
                if (Move.getPiece(move) != Piece.PAWN && !Move.isCastling(move)) {
                    continue;
                }
                String san = SanParser.format(p, move);
                assertEquals(move, SanParser.parse(san, p), san + " in " + FenParser.format(p));
            }
        }
    }

    private void canRoundTrip(RandomGame game) throws IllegalMoveException {
        List<Position> positions = game.positions();
        for (int i = 0; i < positions.size(); i++) {
            Position p = positions.get(i);
            for (int move : game.legalMoves().get(i)) {
                String can = CanParser.format(move);
                assertEquals(move, CanParser.parse(can, p), can + " in " + FenParser.format(p));
            }
        }
    }

    private void unmakeRestores(RandomGame game) {
        List<Position> positions = game.positions();
        Game g = new Game(OpeningBook.DEFAULT);
        g.setPosition(game.seed());
        for (int move : game.moves()) {
            g.makeMove(move);
        }
        assertSamePosition(positions.getLast(), g.getPosition());

        for (int i = positions.size() - 2; i >= 0; i--) {
            g.unmakeMove();
            assertSamePosition(positions.get(i), g.getPosition());
        }
        assertThrows(IllegalStateException.class, g::unmakeMove);
    }

    private void legalPositions(RandomGame game) {
        for (Position p : game.positions()) {
            String fen = FenParser.format(p);
            assertEquals(1, Long.bitCount(p.white & p.king), "white kings: " + fen);
            assertEquals(1, Long.bitCount(p.black & p.king), "black kings: " + fen);
            assertFalse(p.isIllegalCheck(), "side not to move is in check: " + fen);
        }
    }

    /**
     * Returns {@code true} if the {@code color} pieces have a king on {@code kingSquare} and a rook on {@code rookSquare}.
     */
    private static boolean isAtHome(long color, Position p, long kingSquare, long rookSquare) {
        return (color & p.king & kingSquare) != 0 && (color & p.rook & rookSquare) != 0;
    }

    /**
     * Asserts that the positions are equal, attack bitboards included. {@code Position.equals}
     * does not compare the attack bitboards.
     */
    private static void assertSamePosition(Position expected, Position actual) {
        String fen = FenParser.format(expected);
        assertEquals(expected, actual, fen);
        assertEquals(expected.whiteAttack, actual.whiteAttack, "white attacks: " + fen);
        assertEquals(expected.blackAttack, actual.blackAttack, "black attacks: " + fen);
    }
}
