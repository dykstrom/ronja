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
import se.dykstrom.ronja.common.parser.CanParser;
import se.dykstrom.ronja.test.AbstractTestCase;
import se.dykstrom.ronja.test.TestUtils;

import static org.junit.Assert.assertNotEquals;
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

    @SuppressWarnings("java:S2925")
    @Test
    public void testFindBestMove_WithTime() throws Exception {
        System.out.println("Warming up...");
        findBestMoveWithDepth(FEN_MIDDLE_GAME_1, 8);
        System.out.println("Starting test...");
        var start = System.nanoTime();
        assertNotEquals(0, findBestMoveWithTime(FEN_MIDDLE_GAME_0, 60_000));
        System.out.printf("Finished step after %7.3f seconds%n", elapsedTime(start));
    }

    public static void main(String[] args) throws Exception {
        final var test = new SlowFinderTest();
        System.out.println("Warming up...");
        test.findBestMoveWithDepth(FEN_MIDDLE_GAME_1, 8);
        System.out.println("Starting test...");
        final var start = System.nanoTime();
        runStep(FEN_MIDDLE_GAME_0, 7, test);
        runStep(FEN_MIDDLE_GAME_1, 8, test);
        runStep(FEN_MIDDLE_GAME_2, 6, test);
        runStep(FEN_MIDDLE_GAME_3, 8, test);
        runStep(FEN_MIDDLE_GAME_4, 8, test);
        runStep(FEN_MIDDLE_GAME_5, 8, test);
        runStep(FEN_OPENING_0, 7, test);
        runStep(FEN_END_GAME_0, 7, test);
        runStep(FEN_NON_QUIET, 7, test);
        runStep(FEN_FORK_1, 8, test);
        System.out.printf("Finished test after %7.3f seconds%n", elapsedTime(start));
    }

    private int findBestMoveWithDepth(final String fen, final int maxDepth) throws ParseException {
        Finder finder = TestUtils.setupFinder(fen);
        return finder.findBestMove(maxDepth);
    }

    private int findBestMoveWithTime(final String fen, final int maxTime) throws ParseException {
        var game = new Game(OpeningBook.DEFAULT);
        game.setPosition(parse(fen));
        var finder = new AlphaBetaFinder(game);
        return finder.findBestMoveWithinTime(maxTime);
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
