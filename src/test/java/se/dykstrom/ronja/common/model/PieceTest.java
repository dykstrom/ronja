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

package se.dykstrom.ronja.common.model;

import org.junit.Test;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This class is for testing class {@code Piece} using JUnit.
 *
 * @author Johan Dykstrom
 * @see Piece
 */
public class PieceTest extends AbstractTestCase {

    @Test
    public void testValueOf() {
        assertEquals(Piece.BISHOP, Piece.valueOf('B'));
        assertEquals(Piece.KING, Piece.valueOf('K'));
        assertEquals(Piece.KNIGHT, Piece.valueOf('N'));
        assertEquals(Piece.PAWN, Piece.valueOf('P'));
        assertEquals(Piece.QUEEN, Piece.valueOf('Q'));
        assertEquals(Piece.ROOK, Piece.valueOf('R'));

        assertEquals(Piece.BISHOP, Piece.valueOf('b'));
        assertEquals(Piece.KING, Piece.valueOf('k'));
        assertEquals(Piece.KNIGHT, Piece.valueOf('n'));
        assertEquals(Piece.PAWN, Piece.valueOf('p'));
        assertEquals(Piece.QUEEN, Piece.valueOf('q'));
        assertEquals(Piece.ROOK, Piece.valueOf('r'));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfFailure() {
        assertNull(Piece.valueOf('U'));
    }

    @Test
    public void testGetSymbol() {
        assertEquals('B', Piece.BISHOP.getSymbol());
        assertEquals('K', Piece.KING.getSymbol());
        assertEquals('N', Piece.KNIGHT.getSymbol());
        assertEquals('P', Piece.PAWN.getSymbol());
        assertEquals('Q', Piece.QUEEN.getSymbol());
        assertEquals('R', Piece.ROOK.getSymbol());
    }

    @Test
    public void testGetSymbolForColor() {
        assertEquals('B', Piece.BISHOP.getSymbol(Color.WHITE));
        assertEquals('K', Piece.KING.getSymbol(Color.WHITE));
        assertEquals('N', Piece.KNIGHT.getSymbol(Color.WHITE));
        assertEquals('P', Piece.PAWN.getSymbol(Color.WHITE));
        assertEquals('Q', Piece.QUEEN.getSymbol(Color.WHITE));
        assertEquals('R', Piece.ROOK.getSymbol(Color.WHITE));

        assertEquals('b', Piece.BISHOP.getSymbol(Color.BLACK));
        assertEquals('k', Piece.KING.getSymbol(Color.BLACK));
        assertEquals('n', Piece.KNIGHT.getSymbol(Color.BLACK));
        assertEquals('p', Piece.PAWN.getSymbol(Color.BLACK));
        assertEquals('q', Piece.QUEEN.getSymbol(Color.BLACK));
        assertEquals('r', Piece.ROOK.getSymbol(Color.BLACK));
    }
}
