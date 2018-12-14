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

import org.junit.Test;
import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.test.AbstractTestCase;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.dykstrom.ronja.common.model.Piece.*;
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
        assertEquals(Move.createCapture(ROOK, Square.A1, Square.C1, BISHOP), findBestMoveWithDepth(FEN_CHECKMATE_1_2));
        assertEquals(Move.create(KNIGHT, Square.H6, Square.F7), findBestMoveWithDepth(FEN_CHECKMATE_2_8));
        assertEquals(Move.create(BISHOP, Square.B4, Square.A3), findBestMoveWithDepth(FEN_CHECKMATE_3_2));
    }

    /**
     * Tests calling findBestMove with positions that result in forced draw in one move.
     */
    @Test
    public void testFindBestMove_DrawInOne() throws Exception {
        assertEquals(Move.createCapture(BISHOP, Square.F3, Square.H1, ROOK), findBestMoveWithDepth(FEN_DRAW_1_1));
        assertEquals(Move.createCapture(BISHOP, Square.F4, Square.H2, PAWN), findBestMoveWithDepth(FEN_DRAW_2_4));
    }

    /**
     * Tests calling findBestMove with positions that result in forced checkmate in two moves.
     */
    @Test
    public void testFindBestMove_CheckmateInTwo() throws Exception {
        assertEquals(Move.create(BISHOP, Square.F4, Square.C1), findBestMoveWithDepth(FEN_CHECKMATE_1_1));
        assertEquals(Move.createCapture(ROOK, Square.E8, Square.G8, QUEEN), findBestMoveWithDepth(FEN_CHECKMATE_2_7));

        int actual = findBestMoveWithDepth(FEN_CHECKMATE_3_1);
        int f3d1 = Move.create(QUEEN, Square.F3, Square.D1);
        int c1b2 = Move.create(KING, Square.C1, Square.B2);
        assertTrue(actual == f3d1 || actual == c1b2);
    }

    /**
     * Tests calling findBestMove with positions that result in forced draw in two moves.
     */
    @Test
    public void testFindBestMove_DrawInTwo() throws Exception {
        assertEquals(Move.create(ROOK, Square.H4, Square.H1), findBestMoveWithDepth(FEN_DRAW_1_0));

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
        assertEquals(Move.create(ROOK, Square.A8, Square.A1), findBestMoveWithDepth(FEN_CHECKMATE_1_0));
        assertEquals(Move.create(QUEEN, Square.C4, Square.G8), findBestMoveWithDepth(FEN_CHECKMATE_2_6));
        assertEquals(Move.create(QUEEN, Square.E7, Square.E1), findBestMoveWithDepth(FEN_CHECKMATE_3_0));
    }

    /**
     * Tests calling findBestMove with positions that result in draw in three moves.
     */
    @Test
    public void testFindBestMove_DrawInThree() throws Exception {
        assertEquals(Move.create(ROOK, Square.H4, Square.H1), findBestMoveWithDepth(FEN_DRAW_1_0));
        assertEquals(Move.create(BISHOP, Square.D2, Square.F4), findBestMoveWithDepth(FEN_DRAW_2_2));
    }

    /**
     * Tests calling findBestMove with positions that result in a (executed) fork in three moves.
     */
    @Test
    public void testFindBestMove_ForkInThree() throws Exception {
        assertEquals(Move.createCapture(KNIGHT, Square.B5, Square.C7, PAWN), findBestMoveWithDepth(FEN_FORK_0));
        assertEquals(Move.createCapture(KNIGHT, Square.D4, Square.E6, PAWN), findBestMoveWithDepth(FEN_FORK_1));
    }

    /**
     * Tests calling findBestMoveWithinTime with positions that result in forced checkmate in one move.
     */
    @Test
    public void testFindBestMoveWithinTime_CheckmateInOne() throws Exception {
        assertEquals(Move.createCapture(ROOK, Square.A1, Square.C1, BISHOP), findBestMoveWithTime(FEN_CHECKMATE_1_2, 50));
        assertEquals(Move.create(KNIGHT, Square.H6, Square.F7), findBestMoveWithTime(FEN_CHECKMATE_2_8, 50));
        assertEquals(Move.create(BISHOP, Square.B4, Square.A3), findBestMoveWithTime(FEN_CHECKMATE_3_2, 50));
    }

    @Test
    public void testFindBestMoveWithinTime() throws Exception {
        assertEquals(Move.createCapture(KNIGHT, Square.B5, Square.C7, PAWN), findBestMoveWithTime(FEN_FORK_0, 500));
    }

    // -----------------------------------------------------------------------

    /**
     * Calls findBestMoveWithinTime with the position specified by {@code fen} and the maximum search time.
     */
    private int findBestMoveWithTime(String fen, int maxTime) throws ParseException {
        AlphaBetaFinder finder = setupFinder(fen);
        return finder.findBestMoveWithinTime(parse(fen), maxTime);
    }

    /**
     * Calls findBestMove with the position specified by {@code fen} and the maximum search depth.
     */
    private int findBestMoveWithDepth(String fen) throws ParseException {
        AlphaBetaFinder finder = setupFinder(fen);
        return finder.findBestMove(parse(fen), MAX_DEPTH);
    }

    /**
     * Calls the alphaBeta method with the position specified by {@code fen} and the given depth.
     */
    private int alphaBeta(String fen, int maxDepth) throws ParseException {
        AlphaBetaFinder finder = setupFinder(fen);
        return finder.alphaBeta(parse(fen), 0, maxDepth, AlphaBetaFinder.ALPHA_START, AlphaBetaFinder.BETA_START);
    }

    private AlphaBetaFinder setupFinder(String fen) throws ParseException {
        Game game = new Game(OpeningBook.DEFAULT);
        game.setPosition(parse(fen));
        AlphaBetaFinder finder = new AlphaBetaFinder(game);
        finder.setMaxDepth(MAX_DEPTH);
        return finder;
    }
}
