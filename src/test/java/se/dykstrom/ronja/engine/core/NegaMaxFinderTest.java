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

import org.junit.Before;
import org.junit.Test;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Piece;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class is for testing class {@code NegaMaxFinder} using JUnit.
 *
 * @author Johan Dykstrom
 * @see NegaMaxFinder
 */
public class NegaMaxFinderTest extends AbstractTestCase {

    private static final int MAX_DEPTH = 3;

    private final NegaMaxFinder finder = new NegaMaxFinder();

    @Before
    public void setUp() {
        finder.setMaxDepth(MAX_DEPTH);
    }

    /**
     * Tests calling negaMax with depth = max depth. No moves are made, the given positions are just evaluated.
     */
    @Test
    public void testNegaMax_Evaluate() throws Exception {
        assertTrue(finder.negaMax(FenParser.parse(FEN_START), null, MAX_DEPTH) >= 0);
        assertTrue(finder.negaMax(FenParser.parse(FEN_QUEEN_IN_CORNER), null, MAX_DEPTH) > 0);
        assertTrue(finder.negaMax(FenParser.parse(FEN_TWO_QUEENS), null, MAX_DEPTH) > 0);
        assertTrue(finder.negaMax(FenParser.parse(FEN_FORK_0), null, MAX_DEPTH) < 0);
        assertTrue(finder.negaMax(FenParser.parse(FEN_MIDDLE_GAME_2), null, MAX_DEPTH) < 0);
    }

    /**
     * Tests calling negaMax with positions that are already checkmate.
     */
    @Test
    public void testNegaMax_CheckmateInZero() throws Exception {
        assertEquals(Evaluator.CHECK_MATE_VALUE, finder.negaMax(FenParser.parse(FEN_SCHOLARS_MATE), null, 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, finder.negaMax(FenParser.parse(FEN_CHECKMATE_0), null, 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, finder.negaMax(FenParser.parse(FEN_CHECKMATE_1_3), null, 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, finder.negaMax(FenParser.parse(FEN_CHECKMATE_2_9), null, 1));
    }

    /**
     * Tests calling negaMax with positions that are already draw.
     */
    @Test
    public void testNegaMax_DrawInZero() throws Exception {
        assertEquals(Evaluator.DRAW_VALUE, finder.negaMax(FenParser.parse(FEN_DRAW_1_2), null, 1));
    }

    /**
     * Tests calling negaMax with positions that result in forced checkmate in one move.
     */
    @Test
    public void testNegaMax_CheckmateInOne() throws Exception {
        assertEquals(-Evaluator.CHECK_MATE_VALUE, finder.negaMax(FenParser.parse(FEN_CHECKMATE_1_2), null, 1));
        assertEquals(-Evaluator.CHECK_MATE_VALUE, finder.negaMax(FenParser.parse(FEN_CHECKMATE_2_8), null, 1));
    }

    /**
     * Tests calling negaMax with positions that result in forced draw in one move.
     */
    @Test
    public void testNegaMax_DrawInOne() throws Exception {
        assertEquals(Evaluator.DRAW_VALUE, finder.negaMax(FenParser.parse(FEN_DRAW_1_1), null, 1));
    }

    /**
     * Tests calling negaMax with positions that result in forced checkmate in two moves.
     */
    @Test
    public void testNegaMax_CheckmateInTwo() throws Exception {
        assertEquals(Evaluator.CHECK_MATE_VALUE, finder.negaMax(FenParser.parse(FEN_CHECKMATE_1_1), null, 1));
        assertEquals(Evaluator.CHECK_MATE_VALUE, finder.negaMax(FenParser.parse(FEN_CHECKMATE_2_7), null, 1));
    }

    /**
     * Tests calling negaMax with positions that result in forced draw in two moves.
     */
    @Test
    public void testNegaMax_DrawInTwo() throws Exception {
        assertEquals(Evaluator.DRAW_VALUE, finder.negaMax(FenParser.parse(FEN_DRAW_1_0), null, 1));
    }

    /**
     * Tests calling findBestMove with positions that result in forced checkmate in one move.
     */
    @Test
    public void testFindBestMove_CheckmateInOne() throws Exception {
        assertEquals(Move.of(Piece.ROOK, Square.A1, Square.C1, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_CHECKMATE_1_2), MAX_DEPTH));
        assertEquals(Move.of(Piece.KNIGHT, Square.H6, Square.F7, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_CHECKMATE_2_8), MAX_DEPTH));
    }

    /**
     * Tests calling findBestMove with positions that result in forced draw in one move.
     */
    @Test
    public void testFindBestMove_DrawInOne() throws Exception {
        assertEquals(Move.of(Piece.BISHOP, Square.F3, Square.H1, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_DRAW_1_1), MAX_DEPTH));
    }

    /**
     * Tests calling findBestMove with positions that result in forced checkmate in two moves.
     */
    @Test
    public void testFindBestMove_CheckmateInTwo() throws Exception {
        assertEquals(Move.of(Piece.BISHOP, Square.F4, Square.C1, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_CHECKMATE_1_1), MAX_DEPTH));
        assertEquals(Move.of(Piece.ROOK, Square.E8, Square.G8, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_CHECKMATE_2_7), MAX_DEPTH));
    }

    /**
     * Tests calling findBestMove with positions that result in forced draw in two moves.
     */
    @Test
    public void testFindBestMove_DrawInTwo() throws Exception {
        assertEquals(Move.of(Piece.ROOK, Square.H4, Square.H1, null, false, false),
                finder.findBestMove(FenParser.parse(FEN_DRAW_1_0), MAX_DEPTH));
    }
}
