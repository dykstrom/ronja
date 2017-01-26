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
import se.dykstrom.ronja.common.model.Position;

/**
 * This class is for testing class {@code StatefulMoveGenerator} using JUnit.
 *
 * @author Johan Dykstrom
 * @see StatefulMoveGenerator
 */
public class StatefulMoveGeneratorTest extends AbstractMoveGeneratorTestCase {

    private static final MoveGenerator MOVE_GENERATOR = new StatefulMoveGenerator();

    /**
     * Tests that iterating over all moves using the iterator of this class, and getting them
     * using method {@link FullMoveGenerator#getMoves(Position)} of class {@code FullMoveGenerator}
     * yields the same result.
     */
    @Test
    public void testIterator() throws Exception {
        baseTestIterator(MOVE_GENERATOR);
    }
}
