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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.dykstrom.ronja.common.model.Piece.*;

import org.junit.Test;

import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.test.AbstractTestCase;

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
        assertEquals("a7a5", CanParser.format(Move.create(PAWN, Square.A7, Square.A5)));
        assertEquals("d5c4", CanParser.format(Move.createCapture(PAWN, Square.D5, Square.C4, QUEEN)));
        assertEquals("f6e4", CanParser.format(Move.create(KNIGHT, Square.F6, Square.E4)));
        assertEquals("e7a3", CanParser.format(Move.create(QUEEN, Square.E7, Square.A3)));
    }

    @Test
    public void testFormatPromotion() throws Exception {
        assertEquals("e7f8b", CanParser.format(Move.createPromotion(Square.E7, Square.F8, BISHOP)));
        assertEquals("e7f8r", CanParser.format(Move.createPromotion(Square.E7, Square.F8, ROOK)));
        assertEquals("a2a1n", CanParser.format(Move.createPromotion(Square.A2, Square.A1, KNIGHT)));
        assertEquals("a2a1q", CanParser.format(Move.createPromotion(Square.A2, Square.A1, QUEEN)));
    }

    @Test
    public void testFormatCastling() throws Exception {
        assertEquals("e1g1", CanParser.format(Move.createCastling(Square.E1, Square.G1)));
        assertEquals("e1f1", CanParser.format(Move.createCastling(Square.E1, Square.F1)));
        assertEquals("e8g8", CanParser.format(Move.createCastling(Square.E8, Square.G8)));
        assertEquals("e8e7", CanParser.format(Move.createCastling(Square.E8, Square.E7)));
    }

    @Test
    public void testFormatEnPassant() throws Exception {
        assertEquals("e5d6", CanParser.format(Move.createEnPassant(Square.E5, Square.D6)));
        assertEquals("d2d4", CanParser.format(Move.createEnPassant(Square.D2, Square.D4)));
        assertEquals("d4c3", CanParser.format(Move.createEnPassant(Square.D4, Square.C3)));
        assertEquals("d4d3", CanParser.format(Move.createEnPassant(Square.D4, Square.D3)));
    }

    // -----------------------------------------------------------------------
    // Parsing:
    // -----------------------------------------------------------------------

    @Test
    public void testParseSimple() throws Exception {
        Position position = FenParser.parse(FEN_MIDDLE_GAME_0);

        assertEquals(Move.create(PAWN, Square.A7, Square.A5), CanParser.parse("a7a5", position));
        assertEquals(Move.create(KNIGHT, Square.F6, Square.E4), CanParser.parse("f6e4", position));
        assertEquals(Move.create(QUEEN, Square.E7, Square.A3), CanParser.parse("e7a3", position));
        assertEquals(Move.createCapture(PAWN, Square.D5, Square.C4, PAWN), CanParser.parse("d5c4", position));
    }

    @Test
    public void testParsePromotion() throws Exception {
        Position position = FenParser.parse(FEN_WP_E7F8);
        // TODO: Verify that this really is a capture.
        assertEquals(Move.createCapturePromotion(Square.E7, Square.F8, BISHOP, BISHOP), CanParser.parse("e7f8b", position));
        assertEquals(Move.createCapturePromotion(Square.E7, Square.F8, BISHOP, ROOK), CanParser.parse("e7f8r", position));

        position = FenParser.parse(FEN_BP_A2A1);
        assertEquals(Move.createPromotion(Square.A2, Square.A1, KNIGHT), CanParser.parse("a2a1n", position));
        assertEquals(Move.createPromotion(Square.A2, Square.A1, QUEEN), CanParser.parse("a2a1q", position));
    }

    @Test
    public void testParseCastling() throws Exception {
        Position position = FenParser.parse(FEN_WKC_OK);
        assertEquals(MOVE_E1G1, CanParser.parse("e1g1", position));
        assertEquals(Move.create(KING, Square.E1, Square.F1), CanParser.parse("e1f1", position));

        position = FenParser.parse(FEN_BKC_OK);
        assertEquals(MOVE_E8G8, CanParser.parse("e8g8", position));
        assertEquals(Move.create(KING, Square.E8, Square.E7), CanParser.parse("e8e7", position));
    }

    @Test
    public void testParseEnPassant() throws Exception {
        Position position = FenParser.parse(FEN_WEP_E5D6);
        assertEquals(Move.createEnPassant(Square.E5, Square.D6), CanParser.parse("e5d6", position));
        assertEquals(Move.create(PAWN, Square.D2, Square.D4), CanParser.parse("d2d4", position));

        position = FenParser.parse(FEN_BEP_D4C3);
        assertEquals(Move.createEnPassant(Square.D4, Square.C3), CanParser.parse("d4c3", position));
        assertEquals(Move.create(PAWN, Square.D4, Square.D3), CanParser.parse("d4d3", position));
    }

    @Test(expected = IllegalMoveException.class)
    public void testParseInvalid_NoPiece() throws Exception {
        Position position = FenParser.parse(FEN_START);
        CanParser.parse("e4e5", position);
    }

    @Test(expected = IllegalMoveException.class)
    public void testParseInvalid_WrongColor() throws Exception {
        Position position = FenParser.parse(FEN_START);
        CanParser.parse("e5e6", position);
    }

    @Test(expected = IllegalMoveException.class)
    public void testParseInvalid_InvalidCapture() throws Exception {
        Position position = FenParser.parse(FEN_START);
        CanParser.parse("d1e8", position);
    }

    @Test(expected = IllegalMoveException.class)
    public void testParseInvalid_InvalidCastling() throws Exception {
        Position position = FenParser.parse(FEN_WQC_NOK_K);
        CanParser.parse("e1c1", position);
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
