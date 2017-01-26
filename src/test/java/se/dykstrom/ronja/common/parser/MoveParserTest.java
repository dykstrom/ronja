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

package se.dykstrom.ronja.common.parser;

import org.junit.Test;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Piece;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.assertEquals;

/**
 * This class is for testing class {@code MoveParser} using JUnit. However, most of the parsing tests
 * are collected in classes {@code CanParserTest} and {@code SanParserTest}.
 *
 * @author Johan Dykstrom
 * @see MoveParser
 */
public class MoveParserTest extends AbstractTestCase {

    @Test
    public void testParseSimple() throws Exception {
        Position position = Position.START;

        // CAN format
        assertEquals(Move.of(Piece.PAWN, Square.E2, Square.E4, null, false, false), MoveParser.parse("e2e4", position));
        assertEquals(Move.of(Piece.KNIGHT, Square.G1, Square.F3, null, false, false), MoveParser.parse("g1f3", position));

        // TODO: Add some SAN tests.
    }
}
