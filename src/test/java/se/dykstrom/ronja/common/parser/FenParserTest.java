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

import org.junit.Assert;
import org.junit.Test;
import se.dykstrom.ronja.common.model.*;
import se.dykstrom.ronja.test.AbstractTestCase;

import java.text.ParseException;

import static org.junit.Assert.*;

/**
 * This class is for testing class {@code FenParser} using JUnit.
 *
 * @author Johan Dykstrom
 * @see FenParser
 */
public class FenParserTest extends AbstractTestCase {

    /**
     * Tests the {@code format} method.
     */
    @Test
    public void testFormat() throws Exception {
        assertEquals(FEN_START,            FenParser.format(Position.of(MOVE_START)));
        assertEquals(FEN_E4,               FenParser.format(Position.of(MOVE_E4)));
        assertEquals(FEN_E4_C5,            FenParser.format(Position.of(MOVE_E4_C5)));
        assertEquals(FEN_E4_C5_KE2,        FenParser.format(Position.of(MOVE_E4_C5_KE2)));
        assertEquals(FEN_E4_C5_NF3,        FenParser.format(Position.of(MOVE_E4_C5_NF3)));
        assertEquals(FEN_E4_C5_NF3_NC6,    FenParser.format(Position.of(MOVE_E4_C5_NF3_NC6)));
        assertEquals(FEN_E4_C5_NF3_NC6_D4, FenParser.format(Position.of(MOVE_E4_C5_NF3_NC6_D4)));
        assertEquals(FEN_SCHOLARS_MATE,    FenParser.format(Position.of(MOVE_SCHOLARS_MATE)));
    }

    /**
     * Tests the {@code parse} method.
     */
    @Test
    public void testParse() throws Exception {
        Assert.assertEquals(Position.of(MOVE_START),            FenParser.parse(FEN_START));
        Assert.assertEquals(Position.of(MOVE_E4),               FenParser.parse(FEN_E4));
        Assert.assertEquals(Position.of(MOVE_E4_C5),            FenParser.parse(FEN_E4_C5));
        Assert.assertEquals(Position.of(MOVE_E4_C5_KE2),        FenParser.parse(FEN_E4_C5_KE2));
        Assert.assertEquals(Position.of(MOVE_E4_C5_NF3),        FenParser.parse(FEN_E4_C5_NF3));
        Assert.assertEquals(Position.of(MOVE_E4_C5_NF3_NC6),    FenParser.parse(FEN_E4_C5_NF3_NC6));
        Assert.assertEquals(Position.of(MOVE_E4_C5_NF3_NC6_D4), FenParser.parse(FEN_E4_C5_NF3_NC6_D4));
        Assert.assertEquals(Position.of(MOVE_SCHOLARS_MATE),    FenParser.parse(FEN_SCHOLARS_MATE));
    }

    /**
     * Tests the {@code parse} and {@code format} methods.
     */
    @Test
    public void testParseFormat() throws Exception {
        assertEquals(FEN_START,            FenParser.format(FenParser.parse(FEN_START)));
        assertEquals(FEN_E4,               FenParser.format(FenParser.parse(FEN_E4)));
        assertEquals(FEN_E4_C5,            FenParser.format(FenParser.parse(FEN_E4_C5)));
        assertEquals(FEN_E4_C5_KE2,        FenParser.format(FenParser.parse(FEN_E4_C5_KE2)));
        assertEquals(FEN_E4_C5_NF3,        FenParser.format(FenParser.parse(FEN_E4_C5_NF3)));
        assertEquals(FEN_E4_C5_NF3_NC6,    FenParser.format(FenParser.parse(FEN_E4_C5_NF3_NC6)));
        assertEquals(FEN_E4_C5_NF3_NC6_D4, FenParser.format(FenParser.parse(FEN_E4_C5_NF3_NC6_D4)));
        assertEquals(FEN_SCHOLARS_MATE,    FenParser.format(FenParser.parse(FEN_SCHOLARS_MATE)));
        assertEquals(FEN_QUEEN_IN_CORNER,  FenParser.format(FenParser.parse(FEN_QUEEN_IN_CORNER)));
        assertEquals(FEN_EIGHT_CAPTURES,   FenParser.format(FenParser.parse(FEN_EIGHT_CAPTURES)));
        assertEquals(FEN_MANY_CAPTURES,    FenParser.format(FenParser.parse(FEN_MANY_CAPTURES)));
        assertEquals(FEN_TWO_QUEENS,       FenParser.format(FenParser.parse(FEN_TWO_QUEENS)));
        assertEquals(FEN_ONE_BISHOP,       FenParser.format(FenParser.parse(FEN_ONE_BISHOP)));
        assertEquals(FEN_OPENING_0,        FenParser.format(FenParser.parse(FEN_OPENING_0)));
        assertEquals(FEN_MIDDLE_GAME_0,    FenParser.format(FenParser.parse(FEN_MIDDLE_GAME_0)));
        assertEquals(FEN_MIDDLE_GAME_1,    FenParser.format(FenParser.parse(FEN_MIDDLE_GAME_1)));
        assertEquals(FEN_MIDDLE_GAME_2,    FenParser.format(FenParser.parse(FEN_MIDDLE_GAME_2)));
        assertEquals(FEN_END_GAME_0,       FenParser.format(FenParser.parse(FEN_END_GAME_0)));
        assertEquals(FEN_END_GAME_1,       FenParser.format(FenParser.parse(FEN_END_GAME_1)));
        assertEquals(FEN_END_GAME_2,       FenParser.format(FenParser.parse(FEN_END_GAME_2)));
        assertEquals(FEN_END_GAME_3,       FenParser.format(FenParser.parse(FEN_END_GAME_3)));
        assertEquals(FEN_CHECKMATE_0,      FenParser.format(FenParser.parse(FEN_CHECKMATE_0)));
        assertEquals(FEN_CHECKMATE_1_0,    FenParser.format(FenParser.parse(FEN_CHECKMATE_1_0)));
        assertEquals(FEN_CHECKMATE_2_0,    FenParser.format(FenParser.parse(FEN_CHECKMATE_2_0)));
        assertEquals(FEN_CHECKMATE_3_0,    FenParser.format(FenParser.parse(FEN_CHECKMATE_3_0)));
        assertEquals(FEN_DRAW_2_0,         FenParser.format(FenParser.parse(FEN_DRAW_2_0)));
    }

    /**
     * Tests FEN parsing with an illegal position.
     */
    @Test(expected = ParseException.class)
    public void testIllegal() throws Exception {
        Position position = FenParser.parse(FEN_ILLEGAL_0);
        assertNull(position);
    }

    /**
     * Tests FEN parsing with position {@link #FEN_MIDDLE_GAME_0}.
     */
    @Test
    public void testMiddleGame0() throws Exception {
        Position position = FenParser.parse(FEN_MIDDLE_GAME_0);

        assertEquals(8, position.getFullMoveNumber());
        Assert.assertEquals(Color.BLACK, position.getActiveColor());
        assertEquals(0, position.getEnPassantSquare());
        assertTrue(position.isKingSideCastlingAllowed(Color.BLACK));
        assertTrue(position.isQueenSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isKingSideCastlingAllowed(Color.WHITE));
        assertFalse(position.isQueenSideCastlingAllowed(Color.WHITE));
        Assert.assertEquals(16, Board.popCount(position.white));
        assertEquals(16, Board.popCount(position.black));

        assertPiece(position, Square.D7, Color.BLACK, Piece.KNIGHT);
        assertPiece(position, Square.F6, Color.BLACK, Piece.KNIGHT);
        assertPiece(position, Square.D5, Color.BLACK, Piece.PAWN);
        assertPiece(position, Square.C2, Color.WHITE, Piece.QUEEN);
        assertPiece(position, Square.G5, Color.WHITE, Piece.BISHOP);

        assertEmpty(position, Square.A1, Square.B1, Square.B8, Square.D3, Square.D6, Square.G6);
    }

    /**
     * Tests FEN parsing with position {@link #FEN_END_GAME_0}.
     */
    @Test
    public void testEndGame0() throws Exception {
        Position position = FenParser.parse(FEN_END_GAME_0);

        assertEquals(36, position.getFullMoveNumber());
        assertEquals(Color.BLACK, position.getActiveColor());
        assertEquals(0, position.getEnPassantSquare());
        assertFalse(position.isKingSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isQueenSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isKingSideCastlingAllowed(Color.WHITE));
        assertFalse(position.isQueenSideCastlingAllowed(Color.WHITE));
        assertEquals(9, Board.popCount(position.white));
        assertEquals(8, Board.popCount(position.black));

        assertPiece(position, Square.F7, Color.WHITE, Piece.BISHOP);
        assertPiece(position, Square.H5, Color.WHITE, Piece.QUEEN);
        assertPiece(position, Square.F5, Color.WHITE, Piece.PAWN);

        assertPiece(position, Square.G7, Color.BLACK, Piece.KING);
        assertPiece(position, Square.F8, Color.BLACK, Piece.ROOK);
        assertPiece(position, Square.B5, Color.BLACK, Piece.PAWN);

        assertEmpty(position, Square.A2, Square.A8, Square.D1, Square.E7, Square.G6, Square.H8);
    }

    /**
     * Tests FEN parsing with position {@link #FEN_CHECKMATE_0}.
     */
    @Test
    public void testCheckMate0() throws Exception {
        Position position = FenParser.parse(FEN_CHECKMATE_0);

        assertEquals(17, position.getFullMoveNumber());
        assertEquals(Color.BLACK, position.getActiveColor());
        assertEquals(0, position.getEnPassantSquare());
        assertTrue(position.isKingSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isQueenSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isKingSideCastlingAllowed(Color.WHITE));
        assertFalse(position.isQueenSideCastlingAllowed(Color.WHITE));
        assertEquals(10, Board.popCount(position.white));
        assertEquals(10, Board.popCount(position.black));

        assertPiece(position, Square.D8, Color.WHITE, Piece.ROOK);
        assertPiece(position, Square.G5, Color.WHITE, Piece.BISHOP);
        assertPiece(position, Square.C1, Color.WHITE, Piece.KING);

        assertPiece(position, Square.E8, Color.BLACK, Piece.KING);
        assertPiece(position, Square.E6, Color.BLACK, Piece.QUEEN);
        assertPiece(position, Square.H8, Color.BLACK, Piece.ROOK);

        assertEmpty(position, Square.B1, Square.C8, Square.E3, Square.E7, Square.H1, Square.H3);
    }

    /**
     * Tests FEN parsing with position {@link #FEN_CHECKMATE_1_3}.
     */
    @Test
    public void testCheckMate13() throws Exception {
        Position position = FenParser.parse(FEN_CHECKMATE_1_3);

        assertEquals(19, position.getFullMoveNumber());
        assertEquals(Color.WHITE, position.getActiveColor());
        assertEquals(0, position.getEnPassantSquare());
        assertFalse(position.isKingSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isQueenSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isKingSideCastlingAllowed(Color.WHITE));
        assertFalse(position.isQueenSideCastlingAllowed(Color.WHITE));
        assertEquals(5, Board.popCount(position.white));
        assertEquals(8, Board.popCount(position.black));

        assertPiece(position, Square.B2, Color.WHITE, Piece.PAWN);
        assertPiece(position, Square.F2, Color.WHITE, Piece.PAWN);
        assertPiece(position, Square.G1, Color.WHITE, Piece.KING);

        assertPiece(position, Square.G7, Color.BLACK, Piece.KING);
        assertPiece(position, Square.F5, Color.BLACK, Piece.KNIGHT);
        assertPiece(position, Square.C1, Color.BLACK, Piece.ROOK);

        assertEmpty(position, Square.B1, Square.C8, Square.E3, Square.E7, Square.F6, Square.G5);
    }

    /**
     * Tests FEN parsing with position {@link #FEN_END_GAME_3}.
     */
    @Test
    public void testEndGame3() throws Exception {
        Position position = FenParser.parse(FEN_END_GAME_3);

        assertEquals(51, position.getFullMoveNumber());
        assertEquals(Color.WHITE, position.getActiveColor());
        assertEquals(0, position.getEnPassantSquare());
        assertFalse(position.isKingSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isQueenSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isKingSideCastlingAllowed(Color.WHITE));
        assertFalse(position.isQueenSideCastlingAllowed(Color.WHITE));
        assertEquals(5, Board.popCount(position.white));
        assertEquals(7, Board.popCount(position.black));

        assertPiece(position, Square.E3, Color.WHITE, Piece.KNIGHT);
        assertPiece(position, Square.A5, Color.WHITE, Piece.PAWN);
        assertPiece(position, Square.C5, Color.WHITE, Piece.PAWN);

        assertPiece(position, Square.E2, Color.BLACK, Piece.BISHOP);
        assertPiece(position, Square.B2, Color.BLACK, Piece.PAWN);
        assertPiece(position, Square.D3, Color.BLACK, Piece.PAWN);

        assertEmpty(position, Square.C2, Square.D2, Square.F2, Square.H1, Square.H8, Square.E8);
    }

    /**
     * Tests FEN parsing with position {@link #FEN_CAPTURE_IN_CORNER}.
     */
    @Test
    public void testCaptureInCorner() throws Exception {
        Position position = FenParser.parse(FEN_CAPTURE_IN_CORNER);

        assertEquals(7, position.getFullMoveNumber());
        assertEquals(Color.WHITE, position.getActiveColor());
        assertEquals(0, position.getEnPassantSquare());
        assertTrue(position.isKingSideCastlingAllowed(Color.BLACK));
        assertTrue(position.isQueenSideCastlingAllowed(Color.BLACK));
        assertTrue(position.isKingSideCastlingAllowed(Color.WHITE));
        assertTrue(position.isQueenSideCastlingAllowed(Color.WHITE));
        assertEquals(16, Board.popCount(position.white));
        assertEquals(15, Board.popCount(position.black));

        assertPiece(position, Square.C6, Color.WHITE, Piece.BISHOP);
        assertPiece(position, Square.D4, Color.WHITE, Piece.PAWN);
        assertPiece(position, Square.D2, Color.WHITE, Piece.QUEEN);

        assertPiece(position, Square.A8, Color.BLACK, Piece.ROOK);
        assertPiece(position, Square.B5, Color.BLACK, Piece.PAWN);
        assertPiece(position, Square.H8, Color.BLACK, Piece.ROOK);

        assertEmpty(position, Square.D1, Square.E2, Square.H4, Square.A6, Square.B8, Square.D5);
    }

    /**
     * Tests FEN parsing with position {@link #FEN_QUEEN_IN_CORNER}.
     */
    @Test
    public void testQueenInCorner() throws Exception {
        Position position = FenParser.parse(FEN_QUEEN_IN_CORNER);

        assertEquals(1, position.getFullMoveNumber());
        assertEquals(Color.WHITE, position.getActiveColor());
        assertEquals(0, position.getEnPassantSquare());
        assertFalse(position.isKingSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isQueenSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isKingSideCastlingAllowed(Color.WHITE));
        assertFalse(position.isQueenSideCastlingAllowed(Color.WHITE));
        assertEquals(2, Board.popCount(position.white));
        assertEquals(1, Board.popCount(position.black));

        assertPiece(position, Square.A4, Color.WHITE, Piece.KING);
        assertPiece(position, Square.H1, Color.WHITE, Piece.QUEEN);
        assertPiece(position, Square.E8, Color.BLACK, Piece.KING);

        assertEmpty(position, Square.D1, Square.E2, Square.H4, Square.A6, Square.B8, Square.D5);
    }

    /**
     * Tests FEN parsing with position {@link #FEN_TWO_QUEENS}.
     */
    @Test
    public void testTwoQueens() throws Exception {
        Position position = FenParser.parse(FEN_TWO_QUEENS);

        assertEquals(1, position.getFullMoveNumber());
        assertEquals(Color.BLACK, position.getActiveColor());
        assertEquals(0, position.getEnPassantSquare());
        assertFalse(position.isKingSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isQueenSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isKingSideCastlingAllowed(Color.WHITE));
        assertFalse(position.isQueenSideCastlingAllowed(Color.WHITE));
        assertEquals(9, Board.popCount(position.white));
        assertEquals(7, Board.popCount(position.black));

        assertPiece(position, Square.E7, Color.WHITE, Piece.QUEEN);
        assertPiece(position, Square.G1, Color.WHITE, Piece.KING);
        assertPiece(position, Square.H2, Color.WHITE, Piece.PAWN);

        assertPiece(position, Square.C6, Color.BLACK, Piece.QUEEN);
        assertPiece(position, Square.F3, Color.BLACK, Piece.QUEEN);
        assertPiece(position, Square.H8, Color.BLACK, Piece.KING);

        assertEmpty(position, Square.C1, Square.E1, Square.E5, Square.G7, Square.G2, Square.H1);
    }

    /**
     * Tests FEN parsing with position {@link #FEN_ONE_BISHOP}.
     */
    @Test
    public void testOneBishop() throws Exception {
        Position position = FenParser.parse(FEN_ONE_BISHOP);

        assertEquals(1, position.getFullMoveNumber());
        assertEquals(Color.WHITE, position.getActiveColor());
        assertEquals(0, position.getEnPassantSquare());
        assertFalse(position.isKingSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isQueenSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isKingSideCastlingAllowed(Color.WHITE));
        assertFalse(position.isQueenSideCastlingAllowed(Color.WHITE));
        assertEquals(1, Board.popCount(position.white));
        assertEquals(2, Board.popCount(position.black));

        assertPiece(position, Square.E2, Color.WHITE, Piece.KING);

        assertPiece(position, Square.E3, Color.BLACK, Piece.BISHOP);
        assertPiece(position, Square.E4, Color.BLACK, Piece.KING);

        assertEmpty(position, Square.E1, Square.E5, Square.D2, Square.F2, Square.D3, Square.F3, Square.D4, Square.F4);
    }

    /**
     * Tests FEN parsing with position {@link #FEN_MANY_CAPTURES}.
     */
    @Test
    public void testManyCaptures() throws Exception {
        Position position = FenParser.parse(FEN_MANY_CAPTURES);

        assertEquals(1, position.getFullMoveNumber());
        assertEquals(Color.WHITE, position.getActiveColor());
        assertEquals(0, position.getEnPassantSquare());
        assertFalse(position.isKingSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isQueenSideCastlingAllowed(Color.BLACK));
        assertFalse(position.isKingSideCastlingAllowed(Color.WHITE));
        assertFalse(position.isQueenSideCastlingAllowed(Color.WHITE));
        assertEquals(10, Board.popCount(position.white));
        assertEquals(5, Board.popCount(position.black));

        assertPiece(position, Square.A6, Color.WHITE, Piece.ROOK);
        assertPiece(position, Square.D1, Color.WHITE, Piece.ROOK);
        assertPiece(position, Square.D8, Color.WHITE, Piece.ROOK);
        assertPiece(position, Square.B5, Color.WHITE, Piece.KNIGHT);
        assertPiece(position, Square.B7, Color.WHITE, Piece.KNIGHT);
        assertPiece(position, Square.C4, Color.WHITE, Piece.KNIGHT);
        assertPiece(position, Square.B1, Color.WHITE, Piece.KING);

        assertPiece(position, Square.D6, Color.BLACK, Piece.ROOK);
        assertPiece(position, Square.H6, Color.BLACK, Piece.PAWN);
        assertPiece(position, Square.F6, Color.BLACK, Piece.KING);

        assertEmpty(position, Square.A1, Square.C1, Square.C5, Square.E4, Square.F5, Square.G6, Square.H1, Square.H8);
    }
}
