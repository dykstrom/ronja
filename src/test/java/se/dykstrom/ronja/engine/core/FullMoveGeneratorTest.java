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
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.CanParser;
import se.dykstrom.ronja.common.parser.FenParser;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static se.dykstrom.ronja.test.SizeMatcher.hasSize;

/**
 * This class is for testing class {@code FullMoveGenerator} using JUnit.
 *
 * @author Johan Dykstrom
 * @see FullMoveGenerator
 */
public class FullMoveGeneratorTest extends AbstractMoveGeneratorTestCase {

    private static final FullMoveGenerator MOVE_GENERATOR = new FullMoveGenerator();

	// ------------------------------------------------------------------------

    /**
     * Test generating moves from position {@link #FEN_START}.
     */
    @Test
    public void testPositionStart() throws Exception {
        List<Move> moves = MOVE_GENERATOR.getMoves(FenParser.parse(FEN_START));
        assertThat(moves, hasSize(20));
    }

    /**
     * Test generating moves from position {@link #FEN_MIDDLE_GAME_2}.
     */
    @Test
    public void testPositionMiddleGame2() throws Exception {
        List<Move> moves = MOVE_GENERATOR.getMoves(FenParser.parse(FEN_MIDDLE_GAME_2));
        // a7a6, a7a5, d5d4, f6f5, g7g6, g7g5, h6h5 = 7
        // Nb8a6, Nb8c6, Nb8d7, Ne4d6, Ne4g5, Ne4g3, Ne4f2, Ne4d2, Ne4c3, Ne4c5 = 10
        // Bb6a5, Bb6c7, Bb6d8, Bb6c5, Bb6d4, Bb6e3, Bh7g6, Bh7f5 = 8
        // Qf8f7, Qf8c8, Qf8d8, Qf8e8, Qf8e7, Qf8d6, Qf8c5, Qf8b4, Qf8a3 = 9
        // Kg8f7, Kg8h8 = 2
        assertThat(moves, hasSize(36));
    }

    /**
     * Test generating moves from position {@link #FEN_CHECKMATE_1_1}.
     */
    @Test
    public void testPositionCheckmate11() throws Exception {
        List<Move> moves = MOVE_GENERATOR.getMoves(FenParser.parse(FEN_CHECKMATE_1_1));
        // b2b3, b2b4, f2f3, g2g3, g2g4, h2h3, h2h4 = 7
        // Bf4b8, Bf4c7, Bf4d6, Bf4e5, Bf4g3, Bf4c1, Bf4d2, Bf4e3, Bf4g5, Bf4h6 = 10
        // Kg1h1 = 1 (h1 is not attacked in the original position because the king is in the way)
        assertThat(moves, hasSize(18));
        assertMoves(new String[]{"b2b3", "b2b4", "f2f3", "g2g3", "g2g4", "h2h3", "h2h4",
                "f4b8", "f4c7", "f4d6", "f4e5", "f4g3", "f4c1", "f4d2", "f4e3", "f4g5", "f4h6",
                "g1h1"}, moves);
    }

    /**
     * Test generating moves from position {@link #FEN_DRAW_2_0}.
     */
    @Test
    public void testPositionDraw20() throws Exception {
        List<Move> moves = MOVE_GENERATOR.getMoves(FenParser.parse(FEN_DRAW_2_0));
        // b7b8b, b7b8n, b7b8r, b7b8q = 4
        // Bd2c1, Bd2c3, Bd2b4, Bd2a5, Bd2e1, Bd2e3, Bd2f4, Bd2g5, Bd2h6 = 9
        // Ke4d3, Ke4d4, Ke4d5, Ke4e5, Ke4f5, Ke4f4, Ke4f3, Ke4e3 = 8
        assertThat(moves, hasSize(21));
        assertMoves(new String[]{"b7b8b", "b7b8n", "b7b8r", "b7b8q",
                "d2c1", "d2c3", "d2b4", "d2a5", "d2e1", "d2e3", "d2f4", "d2g5", "d2h6",
                "e4d3", "e4d4", "e4d5", "e4e5", "e4f5", "e4f4", "e4f3", "e4e3"}, moves);
    }

    /**
     * Test generating moves from position {@link #FEN_BQC_OK}.
     */
    @Test
    public void testPosition_BQC_OK() throws Exception {
        List<Move> moves = MOVE_GENERATOR.getMoves(FenParser.parse(FEN_BQC_OK));
        // a7a6, a7a5, b7b6, d6d5, f7f6, f7f5, g7g6, g7g5, h7h6, h7h5 = 10
        // Nc6b8, Nc6d8, Nc6a5, Nc6b4, Nc6d4, Ng8f6, Ng8h6 = 7
        // Bg4f3, Bg4h3, Bg4h5, Bg4f5, Bg4e6, Bg4d7, Bg4c8 = 7
        // Ra8b8, Ra8c8, Ra8d8 = 3
        // Qe7d7, Qe7d8, Qe7e6, Qe7f6, Qe7g5, Qe7h4 = 6
        // Ke8d7, Ke8d8, Ke8c8 = 3
        assertThat(moves, hasSize(36));
    }

    /**
     * Tests that iterating over all moves using the iterator of this class, and getting them
     * using method {@link FullMoveGenerator#getMoves(Position)} of class {@code FullMoveGenerator}
     * yields the same result.
     */
    @Test
    public void testIterator() throws Exception {
        baseTestIterator(MOVE_GENERATOR);
    }

    /**
     * Asserts that the list of actual moves contains the same moves as the array of expected moves.
     *
     * @param expectedMoves An array of expected moves, in CAN format.
     * @param actualMoves The list of actual moves to check.
     */
    private void assertMoves(String[] expectedMoves, List<Move> actualMoves) {
        Arrays.sort(expectedMoves);
        assertArrayEquals(expectedMoves, actualMoves.stream().map(CanParser::format).sorted().toArray());
    }
}
