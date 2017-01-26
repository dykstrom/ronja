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

import static org.junit.Assert.*;

/**
 * This class is for testing class {@code Move} using JUnit.
 *
 * @author Johan Dykstrom
 * @see Move
 */
public class MoveTest extends AbstractTestCase {

    @Test
    public void testOf() throws Exception {
        Move move = Move.of(Piece.KING, Square.E1, Square.F1, null, false, false);
        assertEquals(Square.E1, move.getFrom());
        assertEquals(Square.F1, move.getTo());
        assertEquals(Piece.KING, move.getPiece());
        assertNull(move.getPromoted());
        assertFalse(move.isCastling());
        assertFalse(move.isEnPassant());
    }

    @Test
    public void testOfCastling() throws Exception {
        Move move = Move.of(Piece.KING, Square.E1, Square.G1, null, true, false);
        assertEquals(Square.E1, move.getFrom());
        assertEquals(Square.G1, move.getTo());
        assertEquals(Piece.KING, move.getPiece());
        assertNull(move.getPromoted());
        assertTrue(move.isCastling());
        assertFalse(move.isEnPassant());
    }

    @Test
    public void testOfPromotion() throws Exception {
        Move move = Move.of(Piece.PAWN, Square.E7, Square.F8, Piece.QUEEN, false, false);
        assertEquals(Square.E7, move.getFrom());
        assertEquals(Square.F8, move.getTo());
        assertEquals(Piece.PAWN, move.getPiece());
        assertEquals(Piece.QUEEN, move.getPromoted());
        assertFalse(move.isCastling());
        assertFalse(move.isEnPassant());
    }

    @Test
    public void testOfEnPassant() throws Exception {
        Move move = Move.of(Piece.PAWN, Square.E5, Square.D6, null, false, true);
        assertEquals(Square.E5, move.getFrom());
        assertEquals(Square.D6, move.getTo());
        assertEquals(Piece.PAWN, move.getPiece());
        assertNull(move.getPromoted());
        assertFalse(move.isCastling());
        assertTrue(move.isEnPassant());
    }

	/**
	 * Tests the {@code equals} method.
	 */
    @Test
	public void testEquals() throws Exception {
        // Equal normal moves
	    Move m0 = Move.of(Piece.PAWN, Square.A2, Square.A4, null, false, false);
	    Move m1 = Move.of(Piece.PAWN, Square.A2, Square.A4, null, false, false);
	    assertTrue(m0.equals(m1));

        // Different in from and to square
        m0 = Move.of(Piece.PAWN, Square.A2, Square.A4, null, false, false);
        m1 = Move.of(Piece.PAWN, Square.B2, Square.B4, null, false, false);
	    assertFalse(m0.equals(m1));

        // Equal promotion moves
        m0 = Move.of(Piece.PAWN, Square.E7, Square.F8, Piece.QUEEN, false, false);
        m1 = Move.of(Piece.PAWN, Square.E7, Square.F8, Piece.QUEEN, false, false);
        assertTrue(m0.equals(m1));

        // Differ only in promotion piece
        m0 = Move.of(Piece.PAWN, Square.E7, Square.F8, Piece.QUEEN, false, false);
        m1 = Move.of(Piece.PAWN, Square.E7, Square.F8, Piece.ROOK, false, false);
        assertFalse(m0.equals(m1));

        // Equal castling moves
        m0 = Move.of(Piece.KING, Square.E1, Square.G1, null, true, false);
        m1 = Move.of(Piece.KING, Square.E1, Square.G1, null, true, false);
        assertTrue(m0.equals(m1));

        // Differ only in castling
        m0 = Move.of(Piece.KING, Square.E1, Square.G1, null, false, false);
        m1 = Move.of(Piece.KING, Square.E1, Square.G1, null, true, false);
        assertFalse(m0.equals(m1));

        // Equal 'en passant' moves
        m0 = Move.of(Piece.PAWN, Square.E5, Square.D6, null, false, true);
        m1 = Move.of(Piece.PAWN, Square.E5, Square.D6, null, false, true);
        assertTrue(m0.equals(m1));

        // Differ only in 'en passant'
        m0 = Move.of(Piece.PAWN, Square.E5, Square.D6, null, false, true);
        m1 = Move.of(Piece.PAWN, Square.E5, Square.D6, null, false, false);
        assertFalse(m0.equals(m1));
	}
}
