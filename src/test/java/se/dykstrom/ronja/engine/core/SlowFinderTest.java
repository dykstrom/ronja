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
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;
import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Piece;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.parser.CanParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static se.dykstrom.ronja.common.model.Piece.KING;
import static se.dykstrom.ronja.common.model.Piece.KNIGHT;
import static se.dykstrom.ronja.common.model.Piece.QUEEN;
import static se.dykstrom.ronja.common.model.Square.A7_IDX;
import static se.dykstrom.ronja.common.model.Square.B8_IDX;
import static se.dykstrom.ronja.common.model.Square.F7_IDX;
import static se.dykstrom.ronja.common.model.Square.G8_IDX;
import static se.dykstrom.ronja.common.model.Square.H6_IDX;
import static se.dykstrom.ronja.common.parser.FenParser.parse;

/**
 * This class is for testing the different {@code Finder} classes using JUnit.
 *
 * @author Johan Dykstrom
 * @see AlphaBetaFinder
 */
@Ignore
public class SlowFinderTest extends AbstractTestCase {

    /**
     * Tests calling findBestMove with positions that result in mate in four moves.
     */
    @Test
    public void testFindBestMove_MateInFour() throws Exception {
        assertEquals(Move.create(KING, G8_IDX, G8_IDX), findBestMoveWithDepth(FEN_CHECKMATE_2_5, 5));
    }

    /**
     * Tests calling findBestMove with positions that result in draw in four moves.
     */
    @Test
    public void testFindBestMove_DrawInFour() throws Exception {
        assertEquals(Move.createCapture(KING, A7_IDX, B8_IDX, QUEEN), findBestMoveWithDepth(FEN_DRAW_2_1, 5));
    }

    /**
     * Tests calling findBestMove with positions that result in mate in five moves.
     */
    @Test
    public void testFindBestMove_MateInFive() throws Exception {
        assertEquals(Move.create(KNIGHT, F7_IDX, H6_IDX), findBestMoveWithDepth(FEN_CHECKMATE_2_4, 5));
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
    public void testFindBestMove_MateInSix() throws Exception {
        assertEquals(Move.create(KING, G8_IDX, G8_IDX), findBestMoveWithDepth(FEN_CHECKMATE_2_3, 6));
    }

    /**
     * Tests calling findBestMove with some middle game positions with maximum depth 5.
     */
    @Test
    public void testFindBestMove_MiddleGame() throws Exception {
        assertNotEquals(0, findBestMoveWithDepth(FEN_MIDDLE_GAME_0, 5));
        assertNotEquals(0, findBestMoveWithDepth(FEN_MIDDLE_GAME_1, 5));
    }

    /**
     * Tests calling findBestMove with some opening positions with maximum depth 4.
     */
    @Test
    public void testFindBestMove_OpeningFour() throws Exception {
        assertNotEquals(0, findBestMoveWithDepth(FEN_E4_C5_NF3, 4));
        assertNotEquals(0, findBestMoveWithDepth(FEN_E4_E6, 4));
        assertNotEquals(0, findBestMoveWithDepth(FEN_OPENING_1, 4));
        assertNotEquals(0, findBestMoveWithDepth(FEN_OPENING_2, 4));
    }

    /**
     * Tests calling findBestMove with some opening positions with maximum depth 5.
     */
    @Test
    public void testFindBestMove_OpeningFive() throws Exception {
        assertNotEquals(0, findBestMoveWithDepth(FEN_OPENING_1, 5));
        assertNotEquals(0, findBestMoveWithDepth(FEN_OPENING_2, 5));
    }

    /**
     * A test to be used together with a profiler.
     */
    @SuppressWarnings("java:S2925")
    @Test
    public void testFindBestMove_Profiler() throws Exception {
        var waitTime = 15;
        System.out.println("Warming up...");
        findBestMoveWithDepth(FEN_MIDDLE_GAME_1, 8);
        System.out.printf("Waiting %d seconds...", waitTime);
        Thread.sleep(TimeUnit.SECONDS.toMillis(waitTime));
        System.out.println("Starting test...");
        var start = System.nanoTime();
        assertNotEquals(0, findBestMoveWithDepth(FEN_MIDDLE_GAME_0, 6));
        System.out.printf("Finished step after %7.3f seconds%n", elapsedTime(start));
        assertNotEquals(0, findBestMoveWithDepth(FEN_MIDDLE_GAME_1, 7));
        System.out.printf("Finished step after %7.3f seconds%n", elapsedTime(start));
        assertNotEquals(0, findBestMoveWithDepth(FEN_MIDDLE_GAME_2, 6));
        System.out.printf("Finished step after %7.3f seconds%n", elapsedTime(start));
    }

    /**
     * Calls {@link Finder#findBestMove(int)} with the position specified by {@code fen}
     * and the given {@code maxDepth}.
     */
    private int findBestMoveWithDepth(String fen, int maxDepth) throws ParseException {
        var game = new Game(OpeningBook.DEFAULT);
        game.setPosition(parse(fen));
        var finder = new AlphaBetaFinder(game);
        return finder.findBestMove(maxDepth);
    }

    public static void main(String[] args) throws Exception {
        final var test = new SlowFinderTest();
        System.out.println("Warming up...");
        test.findBestMoveWithDepth(FEN_MIDDLE_GAME_1, 8);
        System.out.println("Starting test...");
        final var start = System.nanoTime();
        runStep(FEN_MIDDLE_GAME_0, 7, test);
        runStep(FEN_MIDDLE_GAME_1, 8, test);
        runStep(FEN_MIDDLE_GAME_2, 7, test);
        runStep(FEN_MIDDLE_GAME_3, 8, test);
        runStep(FEN_MIDDLE_GAME_4, 8, test);
        runStep(FEN_MIDDLE_GAME_5, 8, test);
        System.out.printf("Finished test after %7.3f seconds%n", elapsedTime(start));
    }

    private static void runStep(final String fen, final int maxDepth, final SlowFinderTest test) throws ParseException {
        final var start = System.nanoTime();
        final var move = test.findBestMoveWithDepth(fen, maxDepth);
        System.out.printf("Finished step after %7.3f seconds, best move: %s%n",
                elapsedTime(start), CanParser.format(move));
    }

    private static double elapsedTime(final long startTimeInNanos) {
        return (System.nanoTime() - startTimeInNanos) / 1_000_000_000.0;
    }
}
