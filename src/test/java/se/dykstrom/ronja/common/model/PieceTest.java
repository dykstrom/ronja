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

/**
 * This class is for testing class {@code PieceC} using JUnit.
 *
 * @author Johan Dykstrom
 * @see Piece
 */
public class PieceTest extends AbstractTestCase {

    @Test
    public void shouldGetValueOf() {
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
    public void shouldNotGetValueOf() {
        Piece.valueOf('U');
    }

    @Test
    public void shouldGetSymbol() {
        assertEquals('B', Piece.toSymbol(Piece.BISHOP));
        assertEquals('K', Piece.toSymbol(Piece.KING));
        assertEquals('N', Piece.toSymbol(Piece.KNIGHT));
        assertEquals('P', Piece.toSymbol(Piece.PAWN));
        assertEquals('Q', Piece.toSymbol(Piece.QUEEN));
        assertEquals('R', Piece.toSymbol(Piece.ROOK));
    }

    @Test
    public void shouldGetSymbolForColor() {
        assertEquals('B', Piece.toSymbol(Piece.BISHOP, Color.WHITE));
        assertEquals('K', Piece.toSymbol(Piece.KING, Color.WHITE));
        assertEquals('N', Piece.toSymbol(Piece.KNIGHT, Color.WHITE));
        assertEquals('P', Piece.toSymbol(Piece.PAWN, Color.WHITE));
        assertEquals('Q', Piece.toSymbol(Piece.QUEEN, Color.WHITE));
        assertEquals('R', Piece.toSymbol(Piece.ROOK, Color.WHITE));

        assertEquals('b', Piece.toSymbol(Piece.BISHOP, Color.BLACK));
        assertEquals('k', Piece.toSymbol(Piece.KING, Color.BLACK));
        assertEquals('n', Piece.toSymbol(Piece.KNIGHT, Color.BLACK));
        assertEquals('p', Piece.toSymbol(Piece.PAWN, Color.BLACK));
        assertEquals('q', Piece.toSymbol(Piece.QUEEN, Color.BLACK));
        assertEquals('r', Piece.toSymbol(Piece.ROOK, Color.BLACK));
    }
}
