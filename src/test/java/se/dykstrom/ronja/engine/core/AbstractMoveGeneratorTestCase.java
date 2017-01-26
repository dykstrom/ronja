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

import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * An abstract base class for classes that test the different move generators.
 */
public class AbstractMoveGeneratorTestCase extends AbstractTestCase {

    private static final FullMoveGenerator FULL_MOVE_GENERATOR = new FullMoveGenerator();

    /**
     * Base test for testing move generation using an iterator. This method takes a
     * testee as argument and tests this with several different positions.
     *
     * @param testee The move generator to test.
     */
    protected void baseTestIterator(MoveGenerator testee) throws Exception {
        testIteratorForPosition(testee, FenParser.parse(FEN_START));
        testIteratorForPosition(testee, FenParser.parse(FEN_OPENING_0));
        testIteratorForPosition(testee, FenParser.parse(FEN_MIDDLE_GAME_0));
        testIteratorForPosition(testee, FenParser.parse(FEN_END_GAME_3));
        testIteratorForPosition(testee, FenParser.parse(FEN_WEP_E5D6));
        testIteratorForPosition(testee, FenParser.parse(FEN_BEP_D4C3));
        testIteratorForPosition(testee, FenParser.parse(FEN_WP_D7D8_OR_D7C8));
        testIteratorForPosition(testee, FenParser.parse(FEN_BP_A2A1));
        testIteratorForPosition(testee, FenParser.parse(FEN_WKC_OK));
        testIteratorForPosition(testee, FenParser.parse(FEN_WKC_NOK_C));
        testIteratorForPosition(testee, FenParser.parse(FEN_WKC_NOK_K));
        testIteratorForPosition(testee, FenParser.parse(FEN_WKC_NOK_P));
        testIteratorForPosition(testee, FenParser.parse(FEN_BKC_OK));
        testIteratorForPosition(testee, FenParser.parse(FEN_BKC_NOK_C));
        testIteratorForPosition(testee, FenParser.parse(FEN_WQC_OK));
        testIteratorForPosition(testee, FenParser.parse(FEN_WQC_OK_C_B1));
        testIteratorForPosition(testee, FenParser.parse(FEN_WQC_NOK_C_C1));
        testIteratorForPosition(testee, FenParser.parse(FEN_WQC_NOK_K));
        testIteratorForPosition(testee, FenParser.parse(FEN_WQC_NOK_P));
        testIteratorForPosition(testee, FenParser.parse(FEN_BQC_OK));
        testIteratorForPosition(testee, FenParser.parse(FEN_BQC_OK_C_B8));
        testIteratorForPosition(testee, FenParser.parse(FEN_BQC_NOK_C_C8));
    }

    /**
     * Gets all moves for the given position, both using method {@link FullMoveGenerator#getMoves(Position)},
     * and using an iterator, and compares the result.
     *
     * @param testee The move generator to test.
     * @param position The position to test with.
     */
    protected void testIteratorForPosition(MoveGenerator testee, Position position) {
        Set<Move> moves = new HashSet<>();
        Iterator<Move> iterator = testee.iterator(position);
        while (iterator.hasNext()) {
            moves.add(iterator.next());
        }
        assertThat(moves, is(new HashSet<>(FULL_MOVE_GENERATOR.getMoves(position))));
    }
}
