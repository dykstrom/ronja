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
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.common.parser.MoveParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.*;
import static se.dykstrom.ronja.common.model.Piece.BISHOP;
import static se.dykstrom.ronja.common.model.Piece.ROOK;
import static se.dykstrom.ronja.common.model.Square.*;

/**
 * This class is for testing class {@code Position} using JUnit.
 *
 * @author Johan Dykstrom
 * @see Position
 */
public class PositionTest extends AbstractTestCase {

    private Position p0, p1, p2, p3;

	// ------------------------------------------------------------------------

	/**
	 * Tests setting and getting various properties, that should not affect each other.
	 */
    @Test
	public void testSetAndGetProperties() {
		p1 = Position.START;
	    p1 = p1.withActiveColor(Color.WHITE);
	    p1 = p1.withFullMoveNumber(17);
	    p1 = p1.withHalfMoveClock(3);
	    p1 = p1.withEnPassantSquare(0);
        p1 = p1.withKingSideCastlingAllowed(Color.WHITE, true);
        p1 = p1.withKingSideCastlingAllowed(Color.BLACK, true);
        p1 = p1.withQueenSideCastlingAllowed(Color.WHITE, false);
        p1 = p1.withQueenSideCastlingAllowed(Color.BLACK, false);
	    assertEquals(Color.WHITE, p1.getActiveColor());
	    assertEquals(17, p1.getFullMoveNumber());
	    assertEquals(3, p1.getHalfMoveClock());
	    assertEquals(0, p1.getEnPassantSquare());
        assertTrue(p1.isKingSideCastlingAllowed(Color.WHITE));
        assertTrue(p1.isKingSideCastlingAllowed(Color.BLACK));
        assertFalse(p1.isQueenSideCastlingAllowed(Color.WHITE));
        assertFalse(p1.isQueenSideCastlingAllowed(Color.BLACK));

		p1 = Position.START;
        p1 = p1.withActiveColor(Color.BLACK);
        p1 = p1.withFullMoveNumber(51);
        p1 = p1.withHalfMoveClock(0);
        p1 = p1.withEnPassantSquare(Square.F4);
        p1 = p1.withKingSideCastlingAllowed(Color.WHITE, true);
        p1 = p1.withKingSideCastlingAllowed(Color.BLACK, false);
        p1 = p1.withQueenSideCastlingAllowed(Color.WHITE, true);
        p1 = p1.withQueenSideCastlingAllowed(Color.BLACK, false);
        assertEquals(Color.BLACK, p1.getActiveColor());
        assertEquals(51, p1.getFullMoveNumber());
        assertEquals(0, p1.getHalfMoveClock());
        assertEquals(Square.F4, p1.getEnPassantSquare());
        assertTrue(p1.isKingSideCastlingAllowed(Color.WHITE));
        assertFalse(p1.isKingSideCastlingAllowed(Color.BLACK));
        assertTrue(p1.isQueenSideCastlingAllowed(Color.WHITE));
        assertFalse(p1.isQueenSideCastlingAllowed(Color.BLACK));

		p1 = Position.START;
        p1 = p1.withActiveColor(Color.WHITE);
        p1 = p1.withFullMoveNumber(123);
        p1 = p1.withHalfMoveClock(50);
        p1 = p1.withEnPassantSquare(Square.H1);
        p1 = p1.withKingSideCastlingAllowed(Color.WHITE, false);
        p1 = p1.withKingSideCastlingAllowed(Color.BLACK, false);
        p1 = p1.withQueenSideCastlingAllowed(Color.WHITE, false);
        p1 = p1.withQueenSideCastlingAllowed(Color.BLACK, false);
        assertEquals(Color.WHITE, p1.getActiveColor());
        assertEquals(123, p1.getFullMoveNumber());
        assertEquals(50, p1.getHalfMoveClock());
        assertEquals(Square.H1, p1.getEnPassantSquare());
        assertFalse(p1.isKingSideCastlingAllowed(Color.WHITE));
        assertFalse(p1.isKingSideCastlingAllowed(Color.BLACK));
        assertFalse(p1.isQueenSideCastlingAllowed(Color.WHITE));
        assertFalse(p1.isQueenSideCastlingAllowed(Color.BLACK));
	}

	/**
	 * Tests setting the piece and color on a square.
	 */
    @Test
	public void testWithPieceAndColor() {
		Position position = Position.START;

        position = position.withPieceAndColor(Square.E4, Piece.KNIGHT, Color.BLACK);
        position = position.withPieceAndColor(Square.A8, Piece.QUEEN, Color.WHITE);
        position = position.withPieceAndColor(Square.A1, 0, null);

        assertEquals(Piece.KNIGHT, position.getPiece(Square.E4));
        assertEquals(Color.BLACK, position.getColor(Square.E4));
        assertEquals(Piece.QUEEN, position.getPiece(Square.A8));
        assertEquals(Color.WHITE, position.getColor(Square.A8));
        assertEquals(0, position.getPiece(Square.A1));
        assertNull(position.getColor(Square.A1));
	}

    /**
     * Tests that equal positions are equal.
     */
    @Test
	public void testEquals() throws Exception {
		// White moves
		String e2e4 = "e2e4";
		String d2d4 = "d2d4";

		// Black moves
		String e7e5 = "e7e5";
		String d7d5 = "d7d5";

		// The initial positions are equal
		p1 = Position.START;
		p2 = Position.START;
		assertEquals(p1, p2);
		assertEquals(p2, p1);
		p1 = p1.withMove(MoveParser.parse(e2e4, p1));
		p2 = p2.withMove(MoveParser.parse(d2d4, p2));
        assertNotEquals(p1, p2);
        assertNotEquals(p2, p1);

		p1 = p1.withMove(MoveParser.parse(e7e5, p1));
		p2 = p2.withMove(MoveParser.parse(e7e5, p2));
        assertNotEquals(p1, p2);
        assertNotEquals(p2, p1);

		p1 = p1.withMove(MoveParser.parse(d2d4, p1));
		p2 = p2.withMove(MoveParser.parse(e2e4, p2));

		// The positions look equal, but have different 'en passant' squares
        assertNotEquals(p1, p2);
        assertNotEquals(p2, p1);

        p1 = p1.withMove(MoveParser.parse(d7d5, p1));
        p2 = p2.withMove(MoveParser.parse(d7d5, p2));

        // Now the 'en passant' squares are equal too
		assertEquals(p1, p2);
		assertEquals(p2, p1);
	}

    /**
     * Tests that two positions are not equal if the only difference is castling rights.
     */
    @Test
	public void testEqualsCastlingRights() throws Exception {
		// White moves
		String e2e4 = "e2e4";
		String e1e2 = "e1e2";
		String e2e1 = "e2e1";

		// Black moves
		String e7e5 = "e7e5";
		String e8e7 = "e8e7";
		String e7e8 = "e7e8";

		p1 = Position.START;
		p1 = p1.withMove(MoveParser.parse(e2e4, p1));
		p1 = p1.withMove(MoveParser.parse(e7e5, p1));
		p1 = p1.withMove(MoveParser.parse(e1e2, p1)); // White king forward
		p1 = p1.withMove(MoveParser.parse(e8e7, p1)); // Black king forward
		p1 = p1.withMove(MoveParser.parse(e2e1, p1)); // White king back
		p1 = p1.withMove(MoveParser.parse(e7e8, p1)); // Black king back

		p2 = Position.START;
		p2 = p2.withMove(MoveParser.parse(e2e4, p2));
		p2 = p2.withMove(MoveParser.parse(e7e5, p2));

        assertNotEquals(p1, p2);
        assertNotEquals(p2, p1);
	}

    @Test
    public void testOf() throws Exception {
        // Test that the pieces actually move
        Position position = Position.of(MOVE_E4_E5_NF3);
        assertEquals(Piece.PAWN, position.getPiece(Square.E4));
        assertEquals(Piece.PAWN, position.getPiece(Square.E5));
        assertEquals(Piece.KNIGHT, position.getPiece(Square.F3));
        assertEquals(0, position.getPiece(Square.E2));
        assertEquals(0, position.getPiece(Square.E7));
        assertEquals(0, position.getPiece(Square.G1));

        // Test that making the same moves, but not in the same order, gives the same result
        assertEquals(Position.of(MOVE_E4_E5_D4_D5), Position.of(MOVE_D4_E5_E4_D5));
    }

    @Test
    public void testIsCheck() throws Exception {
        assertFalse(FenParser.parse(FEN_START).isCheck(Color.WHITE));
        assertFalse(FenParser.parse(FEN_START).isCheck(Color.BLACK));

        assertFalse(FenParser.parse(FEN_MIDDLE_GAME_0).isCheck(Color.WHITE));
        assertFalse(FenParser.parse(FEN_MIDDLE_GAME_0).isCheck(Color.BLACK));

        assertFalse(FenParser.parse(FEN_SCHOLARS_MATE).isCheck(Color.WHITE));
        assertTrue(FenParser.parse(FEN_SCHOLARS_MATE).isCheck(Color.BLACK));

        assertFalse(FenParser.parse(FEN_CHECKMATE_0).isCheck(Color.WHITE));
        assertTrue(FenParser.parse(FEN_CHECKMATE_0).isCheck(Color.BLACK));

        assertFalse(FenParser.parse(FEN_CHECKMATE_1_0).isCheck(Color.WHITE));
        assertFalse(FenParser.parse(FEN_CHECKMATE_1_0).isCheck(Color.BLACK));

        assertTrue(FenParser.parse(FEN_CHECKMATE_1_1).isCheck(Color.WHITE));
        assertFalse(FenParser.parse(FEN_CHECKMATE_1_1).isCheck(Color.BLACK));

        assertFalse(FenParser.parse(FEN_CHECKMATE_1_2).isCheck(Color.WHITE));
        assertFalse(FenParser.parse(FEN_CHECKMATE_1_2).isCheck(Color.BLACK));

        assertTrue(FenParser.parse(FEN_CHECKMATE_1_3).isCheck(Color.WHITE));
        assertFalse(FenParser.parse(FEN_CHECKMATE_1_3).isCheck(Color.BLACK));
    }

    @Test
    public void testWithMove() throws Exception {
        p0 = FenParser.parse(FEN_START);
        p1 = p0.withMove(MoveParser.parse("a2a4", p0));
        p2 = p1.withMove(MoveParser.parse("g8f6", p1));
        p3 = p2.withMove(MoveParser.parse("a1a3", p2));

        assertPiece(p0, Square.A2, Color.WHITE, Piece.PAWN);
        assertPiece(p0, Square.G8, Color.BLACK, Piece.KNIGHT);
        assertPiece(p0, Square.A1, Color.WHITE, Piece.ROOK);
        assertEmpty(p0, Square.A4, Square.F6, Square.A3);

        assertPiece(p1, Square.A4, Color.WHITE, Piece.PAWN);
        assertPiece(p1, Square.G8, Color.BLACK, Piece.KNIGHT);
        assertPiece(p1, Square.A1, Color.WHITE, Piece.ROOK);
        assertEmpty(p1, Square.A2, Square.F6, Square.A3);

        assertPiece(p2, Square.A4, Color.WHITE, Piece.PAWN);
        assertPiece(p2, Square.F6, Color.BLACK, Piece.KNIGHT);
        assertPiece(p2, Square.A1, Color.WHITE, Piece.ROOK);
        assertEmpty(p2, Square.A2, Square.G8, Square.A3);

        assertPiece(p3, Square.A4, Color.WHITE, Piece.PAWN);
        assertPiece(p3, Square.F6, Color.BLACK, Piece.KNIGHT);
        assertPiece(p3, Square.A3, Color.WHITE, Piece.ROOK);
        assertEmpty(p3, Square.A2, Square.G8, Square.A1);
    }

    @Test
    public void testWithMoveMiddleGame0() throws Exception {
        p0 = FenParser.parse(FEN_MIDDLE_GAME_0);
        p1 = p0.withMove(MoveParser.parse("h7h6", p0));
        p2 = p1.withMove(MoveParser.parse("g5f4", p1));
        p3 = p2.withMove(MoveParser.parse("e8c8", p2));

        assertPiece(p0, Square.H7, Color.BLACK, Piece.PAWN);
        assertPiece(p0, Square.G5, Color.WHITE, Piece.BISHOP);
        assertPiece(p0, Square.E8, Color.BLACK, Piece.KING);
        assertPiece(p0, Square.A8, Color.BLACK, Piece.ROOK);
        assertEmpty(p0, Square.H6, Square.F4, Square.C8, Square.D8);

        assertPiece(p1, Square.H6, Color.BLACK, Piece.PAWN);
        assertPiece(p1, Square.G5, Color.WHITE, Piece.BISHOP);
        assertPiece(p1, Square.E8, Color.BLACK, Piece.KING);
        assertPiece(p1, Square.A8, Color.BLACK, Piece.ROOK);
        assertEmpty(p1, Square.H7, Square.F4, Square.C8, Square.D8);

        assertPiece(p2, Square.H6, Color.BLACK, Piece.PAWN);
        assertPiece(p2, Square.F4, Color.WHITE, Piece.BISHOP);
        assertPiece(p2, Square.E8, Color.BLACK, Piece.KING);
        assertPiece(p2, Square.A8, Color.BLACK, Piece.ROOK);
        assertEmpty(p2, Square.H7, Square.G5, Square.C8, Square.D8);

        assertPiece(p3, Square.H6, Color.BLACK, Piece.PAWN);
        assertPiece(p3, Square.F4, Color.WHITE, Piece.BISHOP);
        assertPiece(p3, Square.C8, Color.BLACK, Piece.KING);
        assertPiece(p3, Square.D8, Color.BLACK, Piece.ROOK);
        assertEmpty(p3, Square.H7, Square.G5, Square.E8, Square.A8);
    }

    /**
     * Tests White castling moves.
     */
    @Test
    public void testMakeMoveWhiteCastling() throws Exception {
        // White king-side castling
        Position originalPosition = FenParser.parse(FEN_WKC_OK);
        Position castledPosition = originalPosition.withMove(MoveParser.parse("e1g1", originalPosition));

        assertEquals(Piece.KING, castledPosition.getPiece(Square.G1));
        assertEquals(Piece.ROOK, castledPosition.getPiece(Square.F1));
        assertFalse(castledPosition.isKingSideCastlingAllowed(Color.WHITE));
        assertFalse(castledPosition.isQueenSideCastlingAllowed(Color.WHITE));
        assertTrue(castledPosition.isKingSideCastlingAllowed(Color.BLACK));
        assertTrue(castledPosition.isQueenSideCastlingAllowed(Color.BLACK));

        // White queen-side castling
        originalPosition = FenParser.parse(FEN_WQC_OK);
        castledPosition = originalPosition.withMove(MoveParser.parse("e1c1", originalPosition));
        assertEquals(Piece.KING, castledPosition.getPiece(Square.C1));
        assertEquals(Piece.ROOK, castledPosition.getPiece(Square.D1));
        assertFalse(castledPosition.isKingSideCastlingAllowed(Color.WHITE));
        assertFalse(castledPosition.isQueenSideCastlingAllowed(Color.WHITE));
        assertTrue(castledPosition.isKingSideCastlingAllowed(Color.BLACK));
        assertTrue(castledPosition.isQueenSideCastlingAllowed(Color.BLACK));
    }

    /**
     * Tests Black castling moves.
     */
    @Test
    public void testMakeMoveBlackCastling() throws Exception {
        // Black king-side castling
        Position originalPosition = FenParser.parse(FEN_BKC_OK);
        Position castledPosition = originalPosition.withMove(MoveParser.parse("e8g8", originalPosition));
        assertEquals(Piece.KING, castledPosition.getPiece(Square.G8));
        assertEquals(Piece.ROOK, castledPosition.getPiece(Square.F8));
        assertTrue(castledPosition.isKingSideCastlingAllowed(Color.WHITE));
        assertTrue(castledPosition.isQueenSideCastlingAllowed(Color.WHITE));
        assertFalse(castledPosition.isKingSideCastlingAllowed(Color.BLACK));
        assertFalse(castledPosition.isQueenSideCastlingAllowed(Color.BLACK));

        // Black queen-side castling
        originalPosition = FenParser.parse(FEN_BQC_OK);
        castledPosition = originalPosition.withMove(MoveParser.parse("e8c8", originalPosition));
        assertEquals(Piece.KING, castledPosition.getPiece(Square.C8));
        assertEquals(Piece.ROOK, castledPosition.getPiece(Square.D8));
        assertFalse(castledPosition.isKingSideCastlingAllowed(Color.WHITE)); // White has also castled
        assertFalse(castledPosition.isQueenSideCastlingAllowed(Color.WHITE));
        assertFalse(castledPosition.isKingSideCastlingAllowed(Color.BLACK));
        assertFalse(castledPosition.isQueenSideCastlingAllowed(Color.BLACK));
    }

    /**
     * Tests White 'en passant' moves.
     */
    @Test
    public void testMakeMoveWhiteEnPassant() throws Exception {
        Position originalPosition = FenParser.parse(FEN_WEP_E5D6);
        assertEquals(Square.D6, originalPosition.getEnPassantSquare());

        // Make the 'en passant' move
        Position resultPosition = originalPosition.withMove(Move.createEnPassant(E5_IDX, D6_IDX));

        // Verify that the white pawn is on d6
        assertEquals(Piece.PAWN, resultPosition.getPiece(Square.D6));
        assertEquals(Color.WHITE, resultPosition.getColor(Square.D6));

        // Verify that the black pawn is _not_ on d5
        assertEquals(0, resultPosition.getPiece(Square.D5));
        assertNull(resultPosition.getColor(Square.D5));
    }

    /**
     * Tests Black 'en passant' moves.
     */
    @Test
    public void testMakeMoveBlackEnPassant() throws Exception {
        Position originalPosition = FenParser.parse(FEN_BEP_D4C3);
        assertEquals(Square.C3, originalPosition.getEnPassantSquare());

        // Make the 'en passant' move
        Position resultPosition = originalPosition.withMove(Move.createEnPassant(D4_IDX, C3_IDX));

        // Verify that the black pawn is on c3
        assertEquals(Piece.PAWN, resultPosition.getPiece(Square.C3));
        assertEquals(Color.BLACK, resultPosition.getColor(Square.C3));

        // Verify that the white pawn is _not_ on c4
        assertEquals(0, resultPosition.getPiece(Square.C4));
        assertNull(resultPosition.getColor(Square.C4));
    }

    /**
     * Tests White promotion moves.
     */
    @Test
    public void testMakeMoveWhitePromotion() throws Exception {
        Position originalPosition = FenParser.parse(FEN_WP_E7F8);

        // Make the promotion move
        Position resultPosition = originalPosition.withMove(Move.createPromotion(E7_IDX, F8_IDX, BISHOP));

        // Verify that the white bishop is on f8
        assertEquals(Piece.BISHOP, resultPosition.getPiece(Square.F8));
        assertEquals(Color.WHITE,  resultPosition.getColor(Square.F8));

        // Verify that the white pawn is _not_ on e7
        assertEquals(0, resultPosition.getPiece(Square.E7));
        assertNull(resultPosition.getColor(Square.E7));
    }

    /**
     * Tests Black promotion moves.
     */
    @Test
    public void testMakeMoveBlackPromotion() throws Exception {
        Position originalPosition = FenParser.parse(FEN_BP_A2A1);

        // Make the promotion move
        Position resultPosition = originalPosition.withMove(Move.createPromotion(A2_IDX, A1_IDX, ROOK));

        // Verify that the black rook is on a1
        assertEquals(Piece.ROOK,  resultPosition.getPiece(Square.A1));
        assertEquals(Color.BLACK, resultPosition.getColor(Square.A1));

        // Verify that the black pawn is _not_ on a2
        assertEquals(0, resultPosition.getPiece(Square.A2));
        assertNull(resultPosition.getColor(Square.A2));
    }
}
