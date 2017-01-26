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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Piece;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class is for testing class {@code AlphaBetaFinder} using JUnit.
 *
 * @author Johan Dykstrom
 * @see AlphaBetaFinder
 */
public class AlphaBetaFinderTest extends AbstractTestCase {

    private static final int MAX_DEPTH = 3;

    private final AlphaBetaFinder finder = new AlphaBetaFinder();

    @Before
    public void setUp() {
        finder.setMaxDepth(MAX_DEPTH);
    }

    /**
     * Tests calling alphaBeta with depth = max depth. No moves are made, the given positions are just evaluated.
     */
    @Test
    public void testAlphaBeta_Evaluate() throws Exception {
        assertTrue(alphaBeta(FenParser.parse(FEN_START), MAX_DEPTH) >= 0);
        assertTrue(alphaBeta(FenParser.parse(FEN_QUEEN_IN_CORNER), MAX_DEPTH) > 0);
        assertTrue(alphaBeta(FenParser.parse(FEN_TWO_QUEENS), MAX_DEPTH) > 0);
        assertTrue(alphaBeta(FenParser.parse(FEN_FORK_0), MAX_DEPTH) < 0);
        assertTrue(alphaBeta(FenParser.parse(FEN_MIDDLE_GAME_2), MAX_DEPTH) < 0);
    }

    /**
     * Tests calling alphaBeta with positions that are already checkmate.
     */
    @Test
    public void testAlphaBeta_CheckmateInZero() throws Exception {
        Assert.assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FenParser.parse(FEN_SCHOLARS_MATE), 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FenParser.parse(FEN_CHECKMATE_0), 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FenParser.parse(FEN_CHECKMATE_1_3), 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FenParser.parse(FEN_CHECKMATE_2_9), 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FenParser.parse(FEN_CHECKMATE_3_3), 1));
    }

    /**
     * Tests calling alphaBeta with positions that are already draw.
     */
    @Test
    public void testAlphaBeta_DrawInZero() throws Exception {
        assertEquals(Evaluator.DRAW_VALUE, alphaBeta(FenParser.parse(FEN_DRAW_1_2), 1));
        assertEquals(Evaluator.DRAW_VALUE, alphaBeta(FenParser.parse(FEN_DRAW_2_5), 1));
    }

    /**
     * Tests calling alphaBeta with positions that result in forced checkmate in one move.
     */
    @Test
    public void testAlphaBeta_CheckmateInOne() throws Exception {
        assertEquals(-Evaluator.CHECK_MATE_VALUE, alphaBeta(FenParser.parse(FEN_CHECKMATE_1_2), 1));
        assertEquals(-Evaluator.CHECK_MATE_VALUE, alphaBeta(FenParser.parse(FEN_CHECKMATE_2_8), 1));
        assertEquals(-Evaluator.CHECK_MATE_VALUE, alphaBeta(FenParser.parse(FEN_CHECKMATE_3_2), 1));
    }

    /**
     * Tests calling alphaBeta with positions that result in forced draw in one move.
     */
    @Test
    public void testAlphaBeta_DrawInOne() throws Exception {
        assertEquals(Evaluator.DRAW_VALUE, alphaBeta(FenParser.parse(FEN_DRAW_1_1), 1));
        assertEquals(Evaluator.DRAW_VALUE, alphaBeta(FenParser.parse(FEN_DRAW_2_4), 1));
    }

    /**
     * Tests calling alphaBeta with positions that result in forced checkmate in two moves.
     */
    @Test
    public void testAlphaBeta_CheckmateInTwo() throws Exception {
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FenParser.parse(FEN_CHECKMATE_1_1), 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FenParser.parse(FEN_CHECKMATE_2_7), 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, alphaBeta(FenParser.parse(FEN_CHECKMATE_3_1), 1));
    }

    /**
     * Tests calling alphaBeta with positions that result in forced draw in two moves.
     */
    @Test
    public void testAlphaBeta_DrawInTwo() throws Exception {
        assertEquals(Evaluator.DRAW_VALUE, alphaBeta(FenParser.parse(FEN_DRAW_1_0), 1));
        // We cannot test with FEN_DRAW_2_3 because the maximum search depth of 3 is not enough
        // for the engine to see that the draw is forced
    }

    /**
     * Tests calling findBestMove with positions that result in forced checkmate in one move.
     */
    @Test
    public void testFindBestMove_CheckmateInOne() throws Exception {
        Assert.assertEquals(Move.of(Piece.ROOK, Square.A1, Square.C1, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_CHECKMATE_1_2), MAX_DEPTH));
        Assert.assertEquals(Move.of(Piece.KNIGHT, Square.H6, Square.F7, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_CHECKMATE_2_8), MAX_DEPTH));
        Assert.assertEquals(Move.of(Piece.BISHOP, Square.B4, Square.A3, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_CHECKMATE_3_2), MAX_DEPTH));
    }

    /**
     * Tests calling findBestMove with positions that result in forced draw in one move.
     */
    @Test
    public void testFindBestMove_DrawInOne() throws Exception {
        Assert.assertEquals(Move.of(Piece.BISHOP, Square.F3, Square.H1, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_DRAW_1_1), MAX_DEPTH));
        Assert.assertEquals(Move.of(Piece.BISHOP, Square.F4, Square.H2, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_DRAW_2_4), MAX_DEPTH));
    }

    /**
     * Tests calling findBestMove with positions that result in forced checkmate in two moves.
     */
    @Test
    public void testFindBestMove_CheckmateInTwo() throws Exception {
        Assert.assertEquals(Move.of(Piece.BISHOP, Square.F4, Square.C1, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_CHECKMATE_1_1), MAX_DEPTH));
        Assert.assertEquals(Move.of(Piece.ROOK, Square.E8, Square.G8, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_CHECKMATE_2_7), MAX_DEPTH));

        Move actual = finder.findBestMove(FenParser.parse(FEN_CHECKMATE_3_1), MAX_DEPTH);
        Move f3d1 = Move.of(Piece.QUEEN, Square.F3, Square.D1, null, false, false);
        Move c1b2 = Move.of(Piece.KING, Square.C1, Square.B2, null, false, false);
        assertTrue(actual.equals(f3d1) || actual.equals(c1b2));
    }

    /**
     * Tests calling findBestMove with positions that result in forced draw in two moves.
     */
    @Test
    public void testFindBestMove_DrawInTwo() throws Exception {
        Assert.assertEquals(Move.of(Piece.ROOK, Square.H4, Square.H1, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_DRAW_1_0), MAX_DEPTH));
        Move actual = finder.findBestMove(FenParser.parse(FEN_DRAW_2_3), MAX_DEPTH);
        assertEquals(Piece.KING, actual.getPiece());
        assertEquals(Square.B8, actual.getFrom());
    }

    /**
     * Tests calling findBestMove with positions that result in mate in three moves.
     */
    @Test
    public void testFindBestMove_MateInThree() throws Exception {
        Assert.assertEquals(Move.of(Piece.ROOK, Square.A8, Square.A1, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_CHECKMATE_1_0), MAX_DEPTH));
        Assert.assertEquals(Move.of(Piece.QUEEN, Square.C4, Square.G8, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_CHECKMATE_2_6), MAX_DEPTH));
        Assert.assertEquals(Move.of(Piece.QUEEN, Square.E7, Square.E1, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_CHECKMATE_3_0), MAX_DEPTH));
    }

    /**
     * Tests calling findBestMove with positions that result in draw in three moves.
     */
    @Test
    public void testFindBestMove_DrawInThree() throws Exception {
        Assert.assertEquals(Move.of(Piece.ROOK, Square.H4, Square.H1, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_DRAW_1_0), MAX_DEPTH));
        Assert.assertEquals(Move.of(Piece.BISHOP, Square.D2, Square.F4, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_DRAW_2_2), MAX_DEPTH));
    }

    /**
     * Tests calling findBestMove with positions that result in a (executed) fork in three moves.
     */
    @Test
    public void testFindBestMove_ForkInThree() throws Exception {
        Assert.assertEquals(Move.of(Piece.KNIGHT, Square.B5, Square.C7, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_FORK_0), MAX_DEPTH));
        Assert.assertEquals(Move.of(Piece.KNIGHT, Square.D4, Square.E6, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_FORK_1), MAX_DEPTH));
    }

    // -----------------------------------------------------------------------

    /**
     * Calls the alphaBeta method with the given position and depth, and initial values for alpha and beta.
     */
    private int alphaBeta(Position position, int depth) {
        return finder.alphaBeta(position, null, depth, AlphaBetaFinder.ALPHA_START, AlphaBetaFinder.BETA_START);
    }
}
