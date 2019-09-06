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
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.*;
import static se.dykstrom.ronja.common.model.Piece.*;
import static se.dykstrom.ronja.common.model.Square.*;

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
    public void testFormatSimple() {
        assertEquals("a7a5", CanParser.format(Move.create(PAWN, A7_IDX, A5_IDX)));
        assertEquals("d5c4", CanParser.format(Move.createCapture(PAWN, D5_IDX, C4_IDX, QUEEN)));
        assertEquals("f6e4", CanParser.format(Move.create(KNIGHT, F6_IDX, E4_IDX)));
        assertEquals("e7a3", CanParser.format(Move.create(QUEEN, E7_IDX, A3_IDX)));
    }

    @Test
    public void testFormatPromotion() {
        assertEquals("e7f8b", CanParser.format(Move.createPromotion(E7_IDX, F8_IDX, BISHOP)));
        assertEquals("e7f8r", CanParser.format(Move.createPromotion(E7_IDX, F8_IDX, ROOK)));
        assertEquals("a2a1n", CanParser.format(Move.createPromotion(A2_IDX, A1_IDX, KNIGHT)));
        assertEquals("a2a1q", CanParser.format(Move.createPromotion(A2_IDX, A1_IDX, QUEEN)));
    }

    @Test
    public void testFormatCastling() {
        assertEquals("e1g1", CanParser.format(Move.createCastling(E1_IDX, G1_IDX)));
        assertEquals("e1c1", CanParser.format(Move.createCastling(E1_IDX, C1_IDX)));
        assertEquals("e8g8", CanParser.format(Move.createCastling(E8_IDX, G8_IDX)));
        assertEquals("e8c8", CanParser.format(Move.createCastling(E8_IDX, C8_IDX)));
    }

    @Test
    public void testFormatEnPassant() {
        assertEquals("e5d6", CanParser.format(Move.createEnPassant(E5_IDX, D6_IDX)));
        assertEquals("d2d4", CanParser.format(Move.createEnPassant(D2_IDX, D4_IDX)));
        assertEquals("d4c3", CanParser.format(Move.createEnPassant(D4_IDX, C3_IDX)));
        assertEquals("d4d3", CanParser.format(Move.createEnPassant(D4_IDX, D3_IDX)));
    }

    // -----------------------------------------------------------------------
    // Parsing:
    // -----------------------------------------------------------------------

    @Test
    public void testParseSimple() throws Exception {
        Position position = FenParser.parse(FEN_MIDDLE_GAME_0);

        assertEquals(Move.create(PAWN, A7_IDX, A5_IDX), CanParser.parse("a7a5", position));
        assertEquals(Move.create(KNIGHT, F6_IDX, E4_IDX), CanParser.parse("f6e4", position));
        assertEquals(Move.create(QUEEN, E7_IDX, A3_IDX), CanParser.parse("e7a3", position));
        assertEquals(Move.createCapture(PAWN, D5_IDX, C4_IDX, PAWN), CanParser.parse("d5c4", position));
    }

    @Test
    public void testParsePromotion() throws Exception {
        Position position = FenParser.parse(FEN_WP_E7F8);
        assertEquals(Move.createCapturePromotion(E7_IDX, F8_IDX, BISHOP, BISHOP), CanParser.parse("e7f8b", position));
        assertEquals(Move.createCapturePromotion(E7_IDX, F8_IDX, BISHOP, ROOK), CanParser.parse("e7f8r", position));

        position = FenParser.parse(FEN_BP_A2A1);
        assertEquals(Move.createPromotion(A2_IDX, A1_IDX, KNIGHT), CanParser.parse("a2a1n", position));
        assertEquals(Move.createPromotion(A2_IDX, A1_IDX, QUEEN), CanParser.parse("a2a1q", position));
    }

    @Test
    public void testParseCastling() throws Exception {
        Position position = FenParser.parse(FEN_WKC_OK);
        assertEquals(MOVE_E1G1, CanParser.parse("e1g1", position));
        assertEquals(Move.create(KING, E1_IDX, F1_IDX), CanParser.parse("e1f1", position));

        position = FenParser.parse(FEN_BKC_OK);
        assertEquals(MOVE_E8G8, CanParser.parse("e8g8", position));
        assertEquals(Move.create(KING, E8_IDX, E7_IDX), CanParser.parse("e8e7", position));
    }

    @Test
    public void testParseEnPassant() throws Exception {
        Position position = FenParser.parse(FEN_WEP_E5D6);
        assertEquals(Move.createEnPassant(E5_IDX, D6_IDX), CanParser.parse("e5d6", position));
        assertEquals(Move.create(PAWN, D2_IDX, D4_IDX), CanParser.parse("d2d4", position));

        position = FenParser.parse(FEN_BEP_D4C3);
        assertEquals(Move.createEnPassant(D4_IDX, C3_IDX), CanParser.parse("d4c3", position));
        assertEquals(Move.create(PAWN, D4_IDX, D3_IDX), CanParser.parse("d4d3", position));
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
