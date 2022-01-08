/*
 * Copyright (C) 2016 Johan Dykstrom
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

import org.junit.Ignore;
import org.junit.Test;
import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.parser.SanParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.dykstrom.ronja.common.model.Piece.BISHOP;
import static se.dykstrom.ronja.common.model.Piece.KING;
import static se.dykstrom.ronja.common.model.Piece.KNIGHT;
import static se.dykstrom.ronja.common.model.Piece.PAWN;
import static se.dykstrom.ronja.common.model.Piece.QUEEN;
import static se.dykstrom.ronja.common.model.Piece.ROOK;
import static se.dykstrom.ronja.common.model.Square.A1_IDX;
import static se.dykstrom.ronja.common.model.Square.A3_IDX;
import static se.dykstrom.ronja.common.model.Square.A8_IDX;
import static se.dykstrom.ronja.common.model.Square.B2_IDX;
import static se.dykstrom.ronja.common.model.Square.B4_IDX;
import static se.dykstrom.ronja.common.model.Square.B5_IDX;
import static se.dykstrom.ronja.common.model.Square.C1_IDX;
import static se.dykstrom.ronja.common.model.Square.C4_IDX;
import static se.dykstrom.ronja.common.model.Square.C6_IDX;
import static se.dykstrom.ronja.common.model.Square.C7_IDX;
import static se.dykstrom.ronja.common.model.Square.D1_IDX;
import static se.dykstrom.ronja.common.model.Square.D2_IDX;
import static se.dykstrom.ronja.common.model.Square.D4_IDX;
import static se.dykstrom.ronja.common.model.Square.E1_IDX;
import static se.dykstrom.ronja.common.model.Square.E6_IDX;
import static se.dykstrom.ronja.common.model.Square.E7_IDX;
import static se.dykstrom.ronja.common.model.Square.E8_IDX;
import static se.dykstrom.ronja.common.model.Square.F3_IDX;
import static se.dykstrom.ronja.common.model.Square.F4_IDX;
import static se.dykstrom.ronja.common.model.Square.F7_IDX;
import static se.dykstrom.ronja.common.model.Square.G8_IDX;
import static se.dykstrom.ronja.common.model.Square.H1_IDX;
import static se.dykstrom.ronja.common.model.Square.H2_IDX;
import static se.dykstrom.ronja.common.model.Square.H4_IDX;
import static se.dykstrom.ronja.common.model.Square.H6_IDX;
import static se.dykstrom.ronja.common.parser.FenParser.parse;

/**
 * This class is for testing class {@code AlphaBetaFinder} using JUnit.
 *
 * @author Johan Dykstrom
 * @see AlphaBetaFinder
 */
public class AlphaBetaFinderTest extends AbstractTestCase {

    private static final int MAX_DEPTH = 3;

    /**
     * Tests calling alphaBeta with depth = max depth. No moves are made, the given positions are just evaluated.
     */
    @Test
    public void testAlphaBeta_Evaluate() throws Exception {
        assertTrue(alphaBeta(FEN_START, MAX_DEPTH) >= 0);
        assertTrue(alphaBeta(FEN_QUEEN_IN_CORNER, MAX_DEPTH) > 0);
        assertTrue(alphaBeta(FEN_TWO_QUEENS, MAX_DEPTH) > 0);
        assertTrue(alphaBeta(FEN_FORK_0, MAX_DEPTH) < 0);
        assertTrue(alphaBeta(FEN_MIDDLE_GAME_2, MAX_DEPTH) < 0);
    }

    /**
     * Tests calling alphaBeta with positions that are already checkmate.
     */
    @Test
    public void testAlphaBeta_CheckmateInZero() throws Exception {
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_SCHOLARS_MATE, 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_0, 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_1_3, 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_2_9, 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_3_3, 1));
    }

    /**
     * Tests calling alphaBeta with positions that are already draw.
     */
    @Test
    public void testAlphaBeta_DrawInZero() throws Exception {
        assertEquals(Evaluator.DRAW_VALUE, alphaBeta(FEN_DRAW_1_2, 1));
        assertEquals(Evaluator.DRAW_VALUE, alphaBeta(FEN_DRAW_2_5, 1));
    }

    /**
     * Tests calling alphaBeta with positions that result in forced checkmate in one move.
     */
    @Test
    public void testAlphaBeta_CheckmateInOne() throws Exception {
        assertEquals(-Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_1_2, 1));
        assertEquals(-Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_2_8, 1));
        assertEquals(-Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_3_2, 1));
    }

    /**
     * Tests calling alphaBeta with positions that result in forced draw in one move.
     */
    @Test
    public void testAlphaBeta_DrawInOne() throws Exception {
        assertEquals(Evaluator.DRAW_VALUE, alphaBeta(FEN_DRAW_1_1, 1));
        assertEquals(Evaluator.DRAW_VALUE, alphaBeta(FEN_DRAW_2_4, 1));
    }

    /**
     * Tests calling alphaBeta with positions that result in forced checkmate in two moves.
     */
    @Test
    public void testAlphaBeta_CheckmateInTwo() throws Exception {
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_1_1, 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_2_7, 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_3_1, 1));
    }

    /**
     * Tests calling alphaBeta with positions that result in forced draw in two moves.
     */
    @Test
    public void testAlphaBeta_DrawInTwo() throws Exception {
        assertEquals(Evaluator.DRAW_VALUE, alphaBeta(FEN_DRAW_1_0, 1));
        // We cannot test with FEN_DRAW_2_3 because the maximum search depth of 3 is not enough
        // for the engine to see that the draw is forced
    }

    /**
     * Tests calling findBestMove with positions that result in forced checkmate in one move.
     */
    @Test
    public void testFindBestMove_CheckmateInOne() throws Exception {
        assertEquals(Move.createCapture(ROOK, A1_IDX, C1_IDX, BISHOP), findBestMoveWithDepth(FEN_CHECKMATE_1_2));
        assertEquals(Move.create(KNIGHT, H6_IDX, F7_IDX), findBestMoveWithDepth(FEN_CHECKMATE_2_8));
        assertEquals(Move.create(BISHOP, B4_IDX, A3_IDX), findBestMoveWithDepth(FEN_CHECKMATE_3_2));
    }

    /**
     * Tests calling findBestMove with positions that result in forced draw in one move.
     */
    @Test
    public void testFindBestMove_DrawInOne() throws Exception {
        assertEquals(Move.createCapture(BISHOP, F3_IDX, H1_IDX, ROOK), findBestMoveWithDepth(FEN_DRAW_1_1));
        assertEquals(Move.createCapture(BISHOP, F4_IDX, H2_IDX, PAWN), findBestMoveWithDepth(FEN_DRAW_2_4));
    }

    /**
     * Tests calling findBestMove with positions that result in forced checkmate in two moves.
     */
    @Test
    public void testFindBestMove_CheckmateInTwo() throws Exception {
        assertEquals(Move.create(BISHOP, F4_IDX, C1_IDX), findBestMoveWithDepth(FEN_CHECKMATE_1_1));
        assertEquals(Move.createCapture(ROOK, E8_IDX, G8_IDX, QUEEN), findBestMoveWithDepth(FEN_CHECKMATE_2_7));

        int actual = findBestMoveWithDepth(FEN_CHECKMATE_3_1);
        int f3d1 = Move.create(QUEEN, F3_IDX, D1_IDX);
        int c1b2 = Move.create(KING, C1_IDX, B2_IDX);
        assertTrue(actual == f3d1 || actual == c1b2);
    }

    /**
     * Tests calling findBestMove with positions that result in forced draw in two moves.
     */
    @Test
    public void testFindBestMove_DrawInTwo() throws Exception {
        assertEquals(Move.create(ROOK, H4_IDX, H1_IDX), findBestMoveWithDepth(FEN_DRAW_1_0));

        int actual = findBestMoveWithDepth(FEN_DRAW_2_3);
        assertEquals(KING, Move.getPiece(actual));
        assertEquals(Square.B8, Move.getFrom(actual));
        // We don't care about the to-square since there are some alternatives
    }

    /**
     * Tests calling findBestMove with positions that result in mate in three moves.
     */
    @Test
    public void testFindBestMove_MateInThree() throws Exception {
        assertEquals(Move.create(ROOK, A8_IDX, A1_IDX), findBestMoveWithDepth(FEN_CHECKMATE_1_0));
        assertEquals(Move.create(QUEEN, C4_IDX, G8_IDX), findBestMoveWithDepth(FEN_CHECKMATE_2_6));
        assertEquals(Move.create(QUEEN, E7_IDX, E1_IDX), findBestMoveWithDepth(FEN_CHECKMATE_3_0));
    }

    /**
     * Tests calling findBestMove with positions that result in draw in three moves.
     */
    @Test
    public void testFindBestMove_DrawInThree() throws Exception {
        assertEquals(Move.create(ROOK, H4_IDX, H1_IDX), findBestMoveWithDepth(FEN_DRAW_1_0));
        assertEquals(Move.create(BISHOP, D2_IDX, F4_IDX), findBestMoveWithDepth(FEN_DRAW_2_2));
    }

    /**
     * Tests calling findBestMove with positions that result in a (executed) fork in three moves.
     */
    @Test
    public void testFindBestMove_ForkInThree() throws Exception {
        assertEquals(Move.createCapture(KNIGHT, B5_IDX, C7_IDX, PAWN), findBestMoveWithDepth(FEN_FORK_0));
        assertEquals(Move.createCapture(KNIGHT, D4_IDX, E6_IDX, PAWN), findBestMoveWithDepth(FEN_FORK_1));
    }

    /**
     * Tests calling findBestMoveWithinTime with positions that result in forced checkmate in one move.
     */
    @Test
    public void shouldFindBestMoveWithinTime_CheckmateInOne() throws Exception {
        assertEquals(Move.createCapture(ROOK, A1_IDX, C1_IDX, BISHOP), findBestMoveWithTime(FEN_CHECKMATE_1_2, 50));
        assertEquals(Move.create(KNIGHT, H6_IDX, F7_IDX), findBestMoveWithTime(FEN_CHECKMATE_2_8, 50));
        assertEquals(Move.create(BISHOP, B4_IDX, A3_IDX), findBestMoveWithTime(FEN_CHECKMATE_3_2, 50));
    }

    @Test
    public void shouldFindBestMoveWithinTime() throws Exception {
        assertEquals(Move.createCapture(KNIGHT, B5_IDX, C7_IDX, PAWN), findBestMoveWithTime(FEN_FORK_0, 500));
    }

    @Ignore("Quiescence search not implemented")
    @Test
    public void shouldFindBestMoveInNonQuietPositionAtMaxDepth1() throws Exception {
        findBestMoveAtMaxDepth(Move.create(KNIGHT, B4_IDX, C6_IDX), FEN_NON_QUIET, 1);
    }

    @Ignore("Quiescence search not implemented")
    @Test
    public void shouldFindBestMoveInNonQuietPositionAtMaxDepth2() throws Exception {
        findBestMoveAtMaxDepth(Move.create(KNIGHT, B4_IDX, C6_IDX), FEN_NON_QUIET, 2);
    }

    private void findBestMoveAtMaxDepth(final int expectedMove, final String fen, final int maxDepth) throws Exception {
        final var position = parse(fen);
        final var finder = setupFinder(fen);
        final var actualMove = finder.findBestMove(maxDepth);
        final var message = String.format(
                "Expected %s, got %s",
                SanParser.format(position, expectedMove),
                SanParser.format(position, actualMove)
        );
        assertEquals(message, expectedMove, actualMove);
    }

    // -----------------------------------------------------------------------

    /**
     * Calls findBestMoveWithinTime with the position specified by {@code fen} and the maximum search time.
     */
    private int findBestMoveWithTime(String fen, int maxTime) throws ParseException {
        AlphaBetaFinder finder = setupFinder(fen);
        return finder.findBestMoveWithinTime(maxTime);
    }

    /**
     * Calls findBestMove with the position specified by {@code fen} and the maximum search depth.
     */
    private int findBestMoveWithDepth(String fen) throws ParseException {
        AlphaBetaFinder finder = setupFinder(fen);
        return finder.findBestMove(MAX_DEPTH);
    }

    /**
     * Calls the alphaBeta method with the position specified by {@code fen} and the given depth.
     */
    private int alphaBeta(String fen, int maxDepth) throws ParseException {
        AlphaBetaFinder finder = setupFinder(fen);
        return finder.alphaBeta(0, maxDepth, AlphaBetaFinder.ALPHA_START, AlphaBetaFinder.BETA_START);
    }

    private AlphaBetaFinder setupFinder(final String fen) throws ParseException {
        final var game = new Game(OpeningBook.DEFAULT);
        game.setPosition(parse(fen));
        final var finder = new AlphaBetaFinder(game);
        finder.maxDepth = MAX_DEPTH;
        return finder;
    }
}
