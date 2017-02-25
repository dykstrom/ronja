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

import org.junit.Ignore;
import org.junit.Test;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Piece;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This class is for testing the different {@code Finder} classes using JUnit.
 *
 * @author Johan Dykstrom
 * @see AlphaBetaFinder
 */
@Ignore
public class SlowFinderTest extends AbstractTestCase {

    private final Finder finder = new AlphaBetaFinder();

    /**
     * Tests calling findBestMove with positions that result in mate in four moves.
     */
    @Test
    public void testFindBestMove_MateInFour() throws Exception {
        assertEquals(Move.of(Piece.KING, Square.G8, Square.H8, null, false, false),
                findBestMoveWithDepth(FEN_CHECKMATE_2_5, 5));
    }

    /**
     * Tests calling findBestMove with positions that result in draw in four moves.
     */
    @Test
    public void testFindBestMove_DrawInFour() throws Exception {
        assertEquals(Move.of(Piece.KING, Square.A7, Square.B8, null, false, false),
                findBestMoveWithDepth(FEN_DRAW_2_1, 5));
    }

    /**
     * Tests calling findBestMove with positions that result in mate in five moves.
     */
    @Test
    public void testFindBestMove_MateInFive() throws Exception {
        assertEquals(Move.of(Piece.KNIGHT, Square.F7, Square.H6, null, false, false),
                findBestMoveWithDepth(FEN_CHECKMATE_2_4, 5));
    }

    /**
     * Tests calling findBestMove with positions that result in draw in five moves.
     */
    @Test
    public void testFindBestMove_DrawInFive() throws Exception {
        Move actual = findBestMoveWithDepth(FEN_DRAW_2_0, 5);
        assertEquals(Piece.PAWN, actual.getPiece());
        assertEquals(Square.B7, actual.getFrom());
        assertEquals(Square.B8, actual.getTo());
        // Any promotion piece will do as the piece will be taken anyway
    }

    /**
     * Tests calling findBestMove with positions that result in mate in six moves.
     */
    @Test
    public void testFindBestMove_MateInSix() throws Exception {
        assertEquals(Move.of(Piece.KING, Square.H8, Square.G8, null, false, false),
                findBestMoveWithDepth(FEN_CHECKMATE_2_3, 6));
    }

    /**
     * Tests calling findBestMove with some middle game positions with maximum depth 5.
     */
    @Test
    public void testFindBestMove_MiddleGame() throws Exception {
        assertNotNull(findBestMoveWithDepth(FEN_MIDDLE_GAME_0, 5));
        assertNotNull(findBestMoveWithDepth(FEN_MIDDLE_GAME_1, 5));
    }

    /**
     * Tests calling findBestMove with some opening positions with maximum depth 4.
     */
    @Test
    public void testFindBestMove_OpeningFour() throws Exception {
        assertNotNull(findBestMoveWithDepth(FEN_E4_C5_NF3, 4));
        assertNotNull(findBestMoveWithDepth(FEN_E4_E6, 4));
        assertNotNull(findBestMoveWithDepth(FEN_OPENING_1, 4));
        assertNotNull(findBestMoveWithDepth(FEN_OPENING_2, 4));
    }

    /**
     * Tests calling findBestMove with some opening positions with maximum depth 5.
     */
    @Test
    public void testFindBestMove_OpeningFive() throws Exception {
        assertNotNull(findBestMoveWithDepth(FEN_OPENING_1, 5));
        assertNotNull(findBestMoveWithDepth(FEN_OPENING_2, 5));
    }

    /**
     * A test to be used together with a profiler.
     */
    @Test
    public void testFindBestMove_Profiler() throws Exception {
        int waitTime = 5000;
        System.out.println("Waiting " + (waitTime / 1000) + " seconds...");
        Thread.sleep(waitTime);
        System.out.println("Starting test...");
        long start = System.currentTimeMillis();
        assertNotNull(findBestMoveWithDepth(FEN_MIDDLE_GAME_0, 5));
        assertNotNull(findBestMoveWithDepth(FEN_MIDDLE_GAME_1, 5));
        long stop = System.currentTimeMillis();
        System.out.println("Finished after " + ((stop - start) / 1000.0) + " seconds");
    }

    /**
     * Calls findBestMove with the position specified by {@code fen} and the given depth.
     */
    private Move findBestMoveWithDepth(String fen, int maxDepth) throws ParseException {
        return finder.findBestMove(FenParser.parse(fen), maxDepth);
    }
}
