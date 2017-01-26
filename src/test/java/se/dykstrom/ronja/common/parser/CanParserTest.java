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
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.model.Piece;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.*;

/**
 * This class is for testing class {@code CanParser} using JUnit.
 *
 * @author Johan Dykstrom
 * @see CanParser
 */
public class CanParserTest extends AbstractTestCase {

    @Test
    public void testIsMove() {
        // Normal moves
        assertTrue("e2e4", CanParser.isMove("e2e4"));
        assertTrue("e4e5", CanParser.isMove("e4e5"));
        assertTrue("e5e4", CanParser.isMove("e5e4"));
        assertTrue("e4e2", CanParser.isMove("e4e2"));
        assertTrue("a1a8", CanParser.isMove("a1a8"));
        assertTrue("h8h1", CanParser.isMove("h8h1"));
        assertTrue("e1g1", CanParser.isMove("e1g1"));

        // Pawn promotions
        assertTrue("e7e8q", CanParser.isMove("e7e8q"));
        assertTrue("h7h8r", CanParser.isMove("h7h8r"));
        assertTrue("b2b1n", CanParser.isMove("b2b1n"));
        assertTrue("a2a1b", CanParser.isMove("a2a1b"));

        // Illegal moves
        assertFalse("i2i4", CanParser.isMove("i2i4"));
        assertFalse("h2i4", CanParser.isMove("h2i4"));
        assertFalse("a0a1", CanParser.isMove("a0a1"));
        assertFalse("a0aa1", CanParser.isMove("a0aa1"));
        assertFalse("aa1", CanParser.isMove("aa1"));
        assertFalse("a2a", CanParser.isMove("a2a"));
        assertFalse("a2a4r", CanParser.isMove("a2a4r"));
        assertFalse("e8e7b", CanParser.isMove("e8e7b"));
        assertFalse("e7e8p", CanParser.isMove("e7e8p"));
        assertFalse("f2f1p", CanParser.isMove("f2f1p"));
    }

    // -----------------------------------------------------------------------
    // Formatting:
    // -----------------------------------------------------------------------

    @Test
    public void testFormatSimple() throws Exception {
        assertEquals("a7a5", CanParser.format(Move.of(Piece.PAWN, Square.A7, Square.A5, null, false, false)));
        assertEquals("d5c4", CanParser.format(Move.of(Piece.PAWN, Square.D5, Square.C4, null, false, false)));
        assertEquals("f6e4", CanParser.format(Move.of(Piece.KNIGHT, Square.F6, Square.E4, null, false, false)));
        assertEquals("e7a3", CanParser.format(Move.of(Piece.QUEEN, Square.E7, Square.A3, null, false, false)));
    }

    @Test
    public void testFormatPromotion() throws Exception {
        assertEquals("e7f8b", CanParser.format(Move.of(Piece.PAWN, Square.E7, Square.F8, Piece.BISHOP, false, false)));
        assertEquals("e7f8r", CanParser.format(Move.of(Piece.PAWN, Square.E7, Square.F8, Piece.ROOK, false, false)));
        assertEquals("a2a1n", CanParser.format(Move.of(Piece.PAWN, Square.A2, Square.A1, Piece.KNIGHT, false, false)));
        assertEquals("a2a1q", CanParser.format(Move.of(Piece.PAWN, Square.A2, Square.A1, Piece.QUEEN, false, false)));
    }

    @Test
    public void testFormatCastling() throws Exception {
        assertEquals("e1g1", CanParser.format(Move.of(Piece.KING, Square.E1, Square.G1, null, true, false)));
        assertEquals("e1f1", CanParser.format(Move.of(Piece.KING, Square.E1, Square.F1, null, false, false)));
        assertEquals("e8g8", CanParser.format(Move.of(Piece.KING, Square.E8, Square.G8, null, true, false)));
        assertEquals("e8e7", CanParser.format(Move.of(Piece.KING, Square.E8, Square.E7, null, false, false)));
    }

    @Test
    public void testFormatEnPassant() throws Exception {
        assertEquals("e5d6", CanParser.format(Move.of(Piece.PAWN, Square.E5, Square.D6, null, false, true)));
        assertEquals("d2d4", CanParser.format(Move.of(Piece.PAWN, Square.D2, Square.D4, null, false, false)));
        assertEquals("d4c3", CanParser.format(Move.of(Piece.PAWN, Square.D4, Square.C3, null, false, true)));
        assertEquals("d4d3", CanParser.format(Move.of(Piece.PAWN, Square.D4, Square.D3, null, false, false)));
    }

    // -----------------------------------------------------------------------
    // Parsing:
    // -----------------------------------------------------------------------

    @Test
    public void testParseSimple() throws Exception {
        Position position = FenParser.parse(FEN_MIDDLE_GAME_0);

        assertEquals(Move.of(Piece.PAWN, Square.A7, Square.A5, null, false, false), CanParser.parse("a7a5", position));
        assertEquals(Move.of(Piece.PAWN, Square.D5, Square.C4, null, false, false), CanParser.parse("d5c4", position));
        assertEquals(Move.of(Piece.KNIGHT, Square.F6, Square.E4, null, false, false), CanParser.parse("f6e4", position));
        assertEquals(Move.of(Piece.QUEEN, Square.E7, Square.A3, null, false, false), CanParser.parse("e7a3", position));
    }

    @Test
    public void testParsePromotion() throws Exception {
        Position position = FenParser.parse(FEN_WP_E7F8);
        assertEquals(Move.of(Piece.PAWN, Square.E7, Square.F8, Piece.BISHOP, false, false), CanParser.parse("e7f8b", position));
        assertEquals(Move.of(Piece.PAWN, Square.E7, Square.F8, Piece.ROOK, false, false), CanParser.parse("e7f8r", position));

        position = FenParser.parse(FEN_BP_A2A1);
        assertEquals(Move.of(Piece.PAWN, Square.A2, Square.A1, Piece.KNIGHT, false, false), CanParser.parse("a2a1n", position));
        assertEquals(Move.of(Piece.PAWN, Square.A2, Square.A1, Piece.QUEEN, false, false), CanParser.parse("a2a1q", position));
    }

    @Test
    public void testParseCastling() throws Exception {
        Position position = FenParser.parse(FEN_WKC_OK);
        assertEquals(Move.of(Piece.KING, Square.E1, Square.G1, null, true, false), CanParser.parse("e1g1", position));
        assertEquals(Move.of(Piece.KING, Square.E1, Square.F1, null, false, false), CanParser.parse("e1f1", position));

        position = FenParser.parse(FEN_BKC_OK);
        assertEquals(Move.of(Piece.KING, Square.E8, Square.G8, null, true, false), CanParser.parse("e8g8", position));
        assertEquals(Move.of(Piece.KING, Square.E8, Square.E7, null, false, false), CanParser.parse("e8e7", position));
    }

    @Test
    public void testParseEnPassant() throws Exception {
        Position position = FenParser.parse(FEN_WEP_E5D6);
        assertEquals(Move.of(Piece.PAWN, Square.E5, Square.D6, null, false, true), CanParser.parse("e5d6", position));
        assertEquals(Move.of(Piece.PAWN, Square.D2, Square.D4, null, false, false), CanParser.parse("d2d4", position));

        position = FenParser.parse(FEN_BEP_D4C3);
        assertEquals(Move.of(Piece.PAWN, Square.D4, Square.C3, null, false, true), CanParser.parse("d4c3", position));
        assertEquals(Move.of(Piece.PAWN, Square.D4, Square.D3, null, false, false), CanParser.parse("d4d3", position));
    }

    @Test(expected = IllegalMoveException.class)
    public void testParseInvalid_NoPiece() throws Exception {
        Position position = FenParser.parse(FEN_START);
        assertNotNull(CanParser.parse("e4e5", position));
    }

    @Test(expected = IllegalMoveException.class)
    public void testParseInvalid_WrongColor() throws Exception {
        Position position = FenParser.parse(FEN_START);
        assertNotNull(CanParser.parse("e5e6", position));
    }

    @Test(expected = IllegalMoveException.class)
    public void testParseInvalid_InvalidCapture() throws Exception {
        Position position = FenParser.parse(FEN_START);
        assertNotNull(CanParser.parse("d1e8", position));
    }

    @Test(expected = IllegalMoveException.class)
    public void testParseInvalid_InvalidCastling() throws Exception {
        Position position = FenParser.parse(FEN_WQC_NOK_K);
        assertNotNull(CanParser.parse("e1c1", position));
    }

    // -----------------------------------------------------------------------
    // Parsing and formatting:
    // -----------------------------------------------------------------------

    @Test
    public void testParseFormat() throws Exception {
        assertEquals("e2e4", CanParser.format(CanParser.parse("e2e4", Position.START)));
        assertEquals("g1f3", CanParser.format(CanParser.parse("g1f3", Position.START)));
        assertEquals("e5d6", CanParser.format(CanParser.parse("e5d6", FenParser.parse(FEN_WEP_E5D6))));
        assertEquals("d4c3", CanParser.format(CanParser.parse("d4c3", FenParser.parse(FEN_BEP_D4C3))));
        assertEquals("e7f8q", CanParser.format(CanParser.parse("e7f8q", FenParser.parse(FEN_WP_E7F8))));
        assertEquals("d7d8b", CanParser.format(CanParser.parse("d7d8b", FenParser.parse(FEN_WP_D7D8_OR_D7C8))));
        assertEquals("b2a1n", CanParser.format(CanParser.parse("b2a1n", FenParser.parse(FEN_BP_B2A1))));
        assertEquals("a2a1r", CanParser.format(CanParser.parse("a2a1r", FenParser.parse(FEN_BP_A2A1))));
        assertEquals("e1g1", CanParser.format(CanParser.parse("e1g1", FenParser.parse(FEN_WKC_OK))));
        assertEquals("e1c1", CanParser.format(CanParser.parse("e1c1", FenParser.parse(FEN_WQC_OK))));
        assertEquals("e8g8", CanParser.format(CanParser.parse("e8g8", FenParser.parse(FEN_BKC_OK))));
        assertEquals("e8c8", CanParser.format(CanParser.parse("e8c8", FenParser.parse(FEN_BQC_OK))));
    }
}
