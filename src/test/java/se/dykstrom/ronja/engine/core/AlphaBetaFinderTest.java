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
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Piece;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.test.AbstractTestCase;
import se.dykstrom.ronja.test.TestUtils;

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
import static se.dykstrom.ronja.common.model.Square.A7_IDX;
import static se.dykstrom.ronja.common.model.Square.A8_IDX;
import static se.dykstrom.ronja.common.model.Square.B2_IDX;
import static se.dykstrom.ronja.common.model.Square.B4_IDX;
import static se.dykstrom.ronja.common.model.Square.B5_IDX;
import static se.dykstrom.ronja.common.model.Square.B8_IDX;
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
import static se.dykstrom.ronja.common.model.Square.H8_IDX;
import static se.dykstrom.ronja.test.TestUtils.assertFindMoveAtDepth;

/**
 * This class is for testing class {@code AlphaBetaFinder} using JUnit.
 *
 * @author Johan Dykstrom
 * @see AlphaBetaFinder
 */
public class AlphaBetaFinderTest extends AbstractTestCase {

    @Test
    public void testAlphaBeta_Evaluate() throws Exception {
        assertTrue(alphaBeta(FEN_START, 3) >= 0);
        assertTrue(alphaBeta(FEN_QUEEN_IN_CORNER, 3) > 0);
        assertTrue(alphaBeta(FEN_TWO_QUEENS, 3) > 0);
        assertTrue(alphaBeta(FEN_FORK_0, 3) < 0);
        assertTrue(alphaBeta(FEN_MIDDLE_GAME_2, 3) < 0);
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
        assertEquals(-Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_1_2, 2));
        assertEquals(-Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_2_8, 2));
        assertEquals(-Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_3_2, 2));
    }

    /**
     * Tests calling alphaBeta with positions that result in draw in one move.
     */
    @Test
    public void testAlphaBeta_DrawInOne() throws Exception {
        assertEquals(Evaluator.DRAW_VALUE, alphaBeta(FEN_DRAW_1_1, 2));
        // FEN_DRAW_2_4 is a draw in one move but without QS, alphaBeta cannot see that.
        // Be5 gives the bishop better mobility, so that move is chosen instead.
        assertEquals(Evaluator.DRAW_VALUE, alphaBeta(FEN_DRAW_2_4, 3));
    }

    /**
     * Tests calling alphaBeta with positions that result in forced checkmate in two moves.
     */
    @Test
    public void testAlphaBeta_CheckmateInTwo() throws Exception {
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_1_1, 3));
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_2_7, 3));
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FEN_CHECKMATE_3_1, 3));
    }

    /**
     * Tests calling alphaBeta with positions that result in forced draw in two moves.
     */
    @Test
    public void testAlphaBeta_DrawInTwo() throws Exception {
        assertEquals(Evaluator.DRAW_VALUE, alphaBeta(FEN_DRAW_1_0, 3));
        assertEquals(Evaluator.DRAW_VALUE, alphaBeta(FEN_DRAW_2_4, 3));
    }

    /**
     * Tests calling findBestMove with positions that result in forced checkmate in one move.
     */
    @Test
    public void testFindBestMove_CheckmateInOne() throws Exception {
        assertFindMoveAtDepth(Move.createCapture(ROOK, A1_IDX, C1_IDX, BISHOP), FEN_CHECKMATE_1_2, 1);
        assertFindMoveAtDepth(Move.create(KNIGHT, H6_IDX, F7_IDX), FEN_CHECKMATE_2_8, 1);
        assertFindMoveAtDepth(Move.create(BISHOP, B4_IDX, A3_IDX), FEN_CHECKMATE_3_2, 1);
    }

    /**
     * Tests calling findBestMove with positions that result in draw in one move.
     */
    @Test
    public void testFindBestMove_DrawInOne() throws Exception {
        assertFindMoveAtDepth(Move.createCapture(BISHOP, F3_IDX, H1_IDX, ROOK), FEN_DRAW_1_1, 1);
        // FEN_DRAW_2_4 is a draw in one move but without QS, alphaBeta cannot see that.
        // Be5 gives the bishop better mobility, so that move is chosen instead.
        assertFindMoveAtDepth(Move.createCapture(BISHOP, F4_IDX, H2_IDX, PAWN), FEN_DRAW_2_4, 2);
    }

    /**
     * Tests calling findBestMove with positions that result in forced checkmate in two moves.
     */
    @Test
    public void testFindBestMove_CheckmateInTwo() throws Exception {
        assertFindMoveAtDepth(Move.create(BISHOP, F4_IDX, C1_IDX), FEN_CHECKMATE_1_1, 2);
        assertFindMoveAtDepth(Move.createCapture(ROOK, E8_IDX, G8_IDX, QUEEN), FEN_CHECKMATE_2_7, 2);

        int actual = findBestMoveWithDepth(FEN_CHECKMATE_3_1, 2);
        int f3d1 = Move.create(QUEEN, F3_IDX, D1_IDX);
        int c1b2 = Move.create(KING, C1_IDX, B2_IDX);
        assertTrue(actual == f3d1 || actual == c1b2);
    }

    /**
     * Tests calling findBestMove with positions that result in forced draw in two moves.
     */
    @Test
    public void testFindBestMove_DrawInTwo() throws Exception {
        assertFindMoveAtDepth(Move.create(ROOK, H4_IDX, H1_IDX), FEN_DRAW_1_0, 2);

        int actual = findBestMoveWithDepth(FEN_DRAW_2_3, 2);
        assertEquals(KING, Move.getPiece(actual));
        assertEquals(Square.B8, Move.getFrom(actual));
        // We don't care about the to-square since there are some alternatives
    }

    /**
     * Tests calling findBestMove with positions that result in mate in three moves.
     */
    @Test
    public void testFindBestMove_CheckmateInThree() throws Exception {
        assertFindMoveAtDepth(Move.create(ROOK, A8_IDX, A1_IDX), FEN_CHECKMATE_1_0, 3);
        assertFindMoveAtDepth(Move.create(QUEEN, C4_IDX, G8_IDX), FEN_CHECKMATE_2_6, 3);
        assertFindMoveAtDepth(Move.create(QUEEN, E7_IDX, E1_IDX), FEN_CHECKMATE_3_0, 3);
    }

    /**
     * Tests calling findBestMove with positions that result in draw in three moves.
     */
    @Test
    public void testFindBestMove_DrawInThree() throws Exception {
        assertFindMoveAtDepth(Move.create(ROOK, H4_IDX, H1_IDX), FEN_DRAW_1_0, 3);
        assertFindMoveAtDepth(Move.create(BISHOP, D2_IDX, F4_IDX), FEN_DRAW_2_2, 3);
    }

    /**
     * Tests calling findBestMove with positions that result in a (executed) fork in three moves.
     */
    @Test
    public void testFindBestMove_ForkInThree() throws Exception {
        assertFindMoveAtDepth(Move.createCapture(KNIGHT, B5_IDX, C7_IDX, PAWN), FEN_FORK_0, 3);
        assertFindMoveAtDepth(Move.createCapture(KNIGHT, D4_IDX, E6_IDX, PAWN), FEN_FORK_1, 3);
    }

    /**
     * Tests calling findBestMove with positions that result in mate in four moves.
     */
    @Test
    public void testFindBestMove_CheckmateInFour() throws Exception {
        assertFindMoveAtDepth(Move.create(KING, G8_IDX, H8_IDX), FEN_CHECKMATE_2_5, 4);
    }

    /**
     * Tests calling findBestMove with positions that result in draw in four moves.
     */
    @Test
    public void testFindBestMove_DrawInFour() throws Exception {
        assertFindMoveAtDepth(Move.createCapture(KING, A7_IDX, B8_IDX, QUEEN), FEN_DRAW_2_1, 4);
    }

    /**
     * Tests calling findBestMove with positions that result in mate in five moves.
     */
    @Test
    public void testFindBestMove_CheckmateInFive() throws Exception {
        assertFindMoveAtDepth(Move.create(KNIGHT, F7_IDX, H6_IDX), FEN_CHECKMATE_2_4, 5);
    }

    /**
     * Tests calling findBestMove with positions that result in draw in five moves.
     */
    @Test
    public void testFindBestMove_DrawInFive() throws Exception {
        int actual = findBestMoveWithDepth(FEN_DRAW_2_0, 5);
        assertEquals(Piece.PAWN, Move.getPiece(actual));
        assertEquals(Square.B7, Move.getFrom(actual));
        assertEquals(Square.B8, Move.getTo(actual));
        // Any promotion piece will do as the piece will be taken anyway
    }

    /**
     * Tests calling findBestMove with positions that result in mate in six moves.
     */
    @Test
    public void testFindBestMove_CheckmateInSix() throws Exception {
        assertFindMoveAtDepth(Move.create(KING, H8_IDX, G8_IDX), FEN_CHECKMATE_2_3, 6);
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
        assertFindMoveAtDepth(Move.create(KNIGHT, B4_IDX, C6_IDX), FEN_NON_QUIET, 1);
        assertFindMoveAtDepth(Move.createCapture(BISHOP, F4_IDX, H2_IDX, PAWN), FEN_DRAW_2_4, 1);
    }

    @Ignore("Quiescence search not implemented")
    @Test
    public void shouldFindBestMoveInNonQuietPositionAtMaxDepth2() throws Exception {
        assertFindMoveAtDepth(Move.create(KNIGHT, B4_IDX, C6_IDX), FEN_NON_QUIET, 2);
    }

    /**
     * Calls findBestMoveWithinTime with the position specified by {@code fen} and the maximum search time.
     */
    private int findBestMoveWithTime(final String fen, final int maxTime) throws ParseException {
        AlphaBetaFinder finder = TestUtils.setupFinder(fen);
        return finder.findBestMoveWithinTime(maxTime);
    }

    /**
     * Calls findBestMove with the position specified by {@code fen} and the maximum search depth.
     */
    private int findBestMoveWithDepth(final String fen, final int depth) throws ParseException {
        AlphaBetaFinder finder = TestUtils.setupFinder(fen);
        return finder.findBestMove(depth);
    }

    /**
     * Calls the alphaBeta method with the position specified by {@code fen} and the given depth.
     */
    private int alphaBeta(final String fen, final int depth) throws ParseException {
        AlphaBetaFinder finder = TestUtils.setupFinder(fen);
        finder.setMaxDepth(depth);
        return finder.alphaBeta(depth - 1, AlphaBetaFinder.ALPHA_START, AlphaBetaFinder.BETA_START);
    }
}
