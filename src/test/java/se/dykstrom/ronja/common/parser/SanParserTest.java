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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.test.AbstractTestCase;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static se.dykstrom.ronja.common.model.Piece.BISHOP;
import static se.dykstrom.ronja.common.model.Piece.KING;
import static se.dykstrom.ronja.common.model.Piece.KNIGHT;
import static se.dykstrom.ronja.common.model.Piece.PAWN;
import static se.dykstrom.ronja.common.model.Piece.QUEEN;
import static se.dykstrom.ronja.common.model.Piece.ROOK;
import static se.dykstrom.ronja.common.model.Square.A1_IDX;
import static se.dykstrom.ronja.common.model.Square.A2_IDX;
import static se.dykstrom.ronja.common.model.Square.A3_IDX;
import static se.dykstrom.ronja.common.model.Square.A6_IDX;
import static se.dykstrom.ronja.common.model.Square.A7_IDX;
import static se.dykstrom.ronja.common.model.Square.A8_IDX;
import static se.dykstrom.ronja.common.model.Square.B4_IDX;
import static se.dykstrom.ronja.common.model.Square.B5_IDX;
import static se.dykstrom.ronja.common.model.Square.B7_IDX;
import static se.dykstrom.ronja.common.model.Square.B8_IDX;
import static se.dykstrom.ronja.common.model.Square.C1_IDX;
import static se.dykstrom.ronja.common.model.Square.C3_IDX;
import static se.dykstrom.ronja.common.model.Square.C4_IDX;
import static se.dykstrom.ronja.common.model.Square.C5_IDX;
import static se.dykstrom.ronja.common.model.Square.C6_IDX;
import static se.dykstrom.ronja.common.model.Square.C7_IDX;
import static se.dykstrom.ronja.common.model.Square.C8_IDX;
import static se.dykstrom.ronja.common.model.Square.D1_IDX;
import static se.dykstrom.ronja.common.model.Square.D2_IDX;
import static se.dykstrom.ronja.common.model.Square.D4_IDX;
import static se.dykstrom.ronja.common.model.Square.D5_IDX;
import static se.dykstrom.ronja.common.model.Square.D6_IDX;
import static se.dykstrom.ronja.common.model.Square.D7_IDX;
import static se.dykstrom.ronja.common.model.Square.D8_IDX;
import static se.dykstrom.ronja.common.model.Square.E1_IDX;
import static se.dykstrom.ronja.common.model.Square.E2_IDX;
import static se.dykstrom.ronja.common.model.Square.E4_IDX;
import static se.dykstrom.ronja.common.model.Square.E5_IDX;
import static se.dykstrom.ronja.common.model.Square.E7_IDX;
import static se.dykstrom.ronja.common.model.Square.E8_IDX;
import static se.dykstrom.ronja.common.model.Square.F1_IDX;
import static se.dykstrom.ronja.common.model.Square.F3_IDX;
import static se.dykstrom.ronja.common.model.Square.F4_IDX;
import static se.dykstrom.ronja.common.model.Square.F6_IDX;
import static se.dykstrom.ronja.common.model.Square.F7_IDX;
import static se.dykstrom.ronja.common.model.Square.F8_IDX;
import static se.dykstrom.ronja.common.model.Square.G1_IDX;
import static se.dykstrom.ronja.common.model.Square.G8_IDX;
import static se.dykstrom.ronja.common.model.Square.H4_IDX;
import static se.dykstrom.ronja.common.model.Square.H6_IDX;
import static se.dykstrom.ronja.common.parser.FenParser.parse;
import static se.dykstrom.ronja.common.parser.SanParser.format;
import static se.dykstrom.ronja.common.parser.SanParser.getAllFromSquares;
import static se.dykstrom.ronja.common.parser.SanParser.isFileUnique;
import static se.dykstrom.ronja.common.parser.SanParser.isMove;
import static se.dykstrom.ronja.common.parser.SanParser.isRankUnique;

/**
 * This class is for testing class {@code SanParser} using JUnit.
 *
 * @author Johan Dykstrom
 * @see SanParser
 */
public class SanParserTest extends AbstractTestCase {

    @Test
	public void testIsMove() {
        // Simple piece moves
        assertTrue("Nf3", isMove("Nf3"));
        assertTrue("Bh1", isMove("Bh1"));
        assertTrue("Rf8", isMove("Rf8"));
        assertTrue("Rf8+", isMove("Rf8+"));
        assertTrue("Qa8", isMove("Qa8"));
        assertTrue("Qa8#", isMove("Qa8#"));
        assertTrue("Ka1", isMove("Ka1"));

        // Complex piece moves
        assertTrue("Nbc3", isMove("Nbc3"));
        assertTrue("Bah1", isMove("Bah1"));
        assertTrue("R1f8", isMove("R1f8"));
        assertTrue("Q5d6", isMove("Q5d6"));
        assertTrue("Qd5d6", isMove("Qd5d6"));

        // Complex piece moves with captures
        assertTrue("Ngxf3", isMove("Ngxf3"));
        assertTrue("Bfxh1", isMove("Bfxh1"));
        assertTrue("Raxf8", isMove("Raxf8"));
        assertTrue("R7xf8+", isMove("R7xf8+"));
        assertTrue("Q6xa8", isMove("Q6xa8"));
        assertTrue("Q8xa8#", isMove("Q8xa8#"));

        // Simple pawn moves
        assertTrue("e4", isMove("e4"));
        assertTrue("a7", isMove("a7"));
        assertTrue("b2+", isMove("b2+"));
        assertTrue("g7#", isMove("g7#"));

        // Pawn moves with captures
        assertTrue("exd4", isMove("exd4"));
        assertTrue("axb2", isMove("axb2"));
        assertTrue("bxc6+", isMove("bxc6+"));
        assertTrue("hxg5#", isMove("hxg5#"));

        // Pawn promotions
        assertTrue("e8=Q", isMove("e8=Q"));
        assertTrue("c8=N", isMove("c8=N"));
        assertTrue("a1=R", isMove("a1=R"));
        assertTrue("h1=B", isMove("h1=B"));
        assertTrue("g1=B+", isMove("g1=B+"));
        assertTrue("axb8=N#", isMove("axb8=N#"));

        // Castling
        assertTrue("O-O", isMove("O-O"));
        assertTrue("O-O-O", isMove("O-O-O"));
        assertTrue("O-O+", isMove("O-O+"));
        assertTrue("O-O-O#", isMove("O-O-O#"));

        // Illegal moves
        assertFalse("Nf9", isMove("Nf9"));
        assertFalse("Bh11", isMove("Bh11"));
        assertFalse("ka8", isMove("ka8"));
        assertFalse("Sa8", isMove("Sa8"));
        assertFalse("Qi8", isMove("Qi8"));

        assertFalse("Q9g8", isMove("Q9g8"));
        assertFalse("Nfff5", isMove("Nfff5"));
        assertFalse("N1ff5", isMove("N1ff5"));

        assertFalse("R9xf8+", isMove("R9xf8+"));
        assertFalse("Qkxa8", isMove("Qkxa8"));
        assertFalse("Q8xp8#", isMove("Q8xp8#"));

        assertFalse("i7", isMove("i7"));
        assertFalse("h9", isMove("h9"));
        assertFalse("f10+", isMove("f10+"));
        assertFalse("a0#", isMove("a0#"));

        assertFalse("zxd4", isMove("zxd4"));
        assertFalse("axb9#", isMove("axb9#"));

        assertFalse("e6=Q", isMove("e6=Q"));
        assertFalse("c2=N", isMove("c2=N"));
        assertFalse("c1=P", isMove("c1=P"));
        assertFalse("g8=K", isMove("g8=K"));
        assertFalse("e8=Q##", isMove("e8=Q##"));

        assertFalse("O", isMove("O"));
        assertFalse("O+", isMove("O+"));
        assertFalse("O-O-O-O", isMove("O-O-O-O"));
	}

    // -----------------------------------------------------------------------
    // Formatting:
    // -----------------------------------------------------------------------

    @Test
    public void testFormatPawnMove() throws Exception {
        assertEquals("e4", format(parse(FEN_START), Move.create(PAWN, E2_IDX, E4_IDX)));
        assertEquals("c5", format(parse(FEN_E4), Move.create(PAWN, C7_IDX, C5_IDX)));
    }

    @Test
    public void testFormatPawnCapture() throws Exception {
        assertEquals("exd5", format(parse(FEN_PC_E4D5), Move.createCapture(PAWN, E4_IDX, D5_IDX, PAWN)));
        // The pawn of d5 can take on e4 or c4
        assertEquals("dxe4", format(parse(FEN_PC_D5E4_D5C4), Move.createCapture(PAWN, D5_IDX, E4_IDX, PAWN)));
        assertEquals("dxc4", format(parse(FEN_PC_D5E4_D5C4), Move.createCapture(PAWN, D5_IDX, C4_IDX, PAWN)));
        // The pawn on e4, and the pawn on c4, can take on d5
        assertEquals("exd5", format(parse(FEN_PC_E4D5_C4D5), Move.createCapture(PAWN, E4_IDX, D5_IDX, PAWN)));
        assertEquals("cxd5", format(parse(FEN_PC_E4D5_C4D5), Move.createCapture(PAWN, C4_IDX, D5_IDX, PAWN)));
    }

    @Test
    public void testFormatPawnPromotion() throws Exception {
        assertEquals("exf8=N", format(parse(FEN_WP_E7F8), Move.createCapturePromotion(E7_IDX, F8_IDX, BISHOP, KNIGHT)));
        assertEquals("a1=R", format(parse(FEN_BP_A2A1), Move.createPromotion(A2_IDX, A1_IDX, ROOK)));
    }

    @Test
    public void testFormatPawnEnPassant() throws Exception {
        assertEquals("exd6", format(parse(FEN_WEP_E5D6), Move.createEnPassant(E5_IDX, D6_IDX)));
        assertEquals("dxc3", format(parse(FEN_BEP_D4C3), Move.createEnPassant(D4_IDX, C3_IDX)));
    }

    @Test
    public void testFormatPieceMove() throws Exception {
        assertEquals("Nf3", format(parse(FEN_START), Move.create(KNIGHT, G1_IDX, F3_IDX)));
        assertEquals("Qh6", format(parse(FEN_E4_E5_QG4_QH4_NF3), Move.create(QUEEN, H4_IDX, H6_IDX)));
        assertEquals("Be7", format(parse(FEN_E4_E5_QG4_QH4_NF3), Move.create(BISHOP, F8_IDX, E7_IDX)));
        assertEquals("Ke7", format(parse(FEN_E4_E5_QG4_QH4_NF3), Move.create(KING, E8_IDX, E7_IDX)));
        assertEquals("Rd1", format(parse(FEN_WQC_OK), Move.create(ROOK, A1_IDX, D1_IDX)));

        assertEquals("Qcf6", format(parse(FEN_TWO_QUEENS), Move.create(QUEEN, C6_IDX, F6_IDX)));
        assertEquals("Qff6", format(parse(FEN_TWO_QUEENS), Move.create(QUEEN, F3_IDX, F6_IDX)));
        assertEquals("Nba3", format(parse(FEN_MANY_CAPTURES), Move.create(KNIGHT, B5_IDX, A3_IDX)));
        assertEquals("Nca3", format(parse(FEN_MANY_CAPTURES), Move.create(KNIGHT, C4_IDX, A3_IDX)));
    }

    @Test
    public void testFormatPieceCapture() throws Exception {
        assertEquals("Nxe4", format(parse(FEN_MIDDLE_GAME_1), Move.createCapture(KNIGHT, F6_IDX, E4_IDX, PAWN)));
        assertEquals("Bxa8", format(parse(FEN_MIDDLE_GAME_1), Move.createCapture(BISHOP, B7_IDX, A8_IDX, ROOK)));
        assertEquals("Rxf7", format(parse(FEN_END_GAME_0), Move.createCapture(ROOK, F8_IDX, F7_IDX, BISHOP)));
        assertEquals("Qxf7", format(parse(FEN_END_GAME_0), Move.createCapture(QUEEN, D7_IDX, F7_IDX, BISHOP)));
        assertEquals("Kxb8", format(parse(FEN_DRAW_2_1), Move.createCapture(KING, A7_IDX, B8_IDX, QUEEN)));
        assertEquals("Qcxe4", format(parse(FEN_TWO_QUEENS), Move.createCapture(QUEEN, C6_IDX, E4_IDX, PAWN)));
        assertEquals("Qfxe4", format(parse(FEN_TWO_QUEENS), Move.createCapture(QUEEN, F3_IDX, E4_IDX, PAWN)));
        assertEquals("N5xd6", format(parse(FEN_MANY_CAPTURES), Move.createCapture(KNIGHT, B5_IDX, D6_IDX, ROOK)));
        assertEquals("Ncxd6", format(parse(FEN_MANY_CAPTURES), Move.createCapture(KNIGHT, C4_IDX, D6_IDX, ROOK)));
        assertEquals("N7xd6", format(parse(FEN_MANY_CAPTURES), Move.createCapture(KNIGHT, B7_IDX, D6_IDX, ROOK)));
    }

    @Test
    public void testFormatCheck() throws Exception {
        // Check by piece move
        assertEquals("Qf1+", format(parse(FEN_TWO_QUEENS), Move.create(QUEEN, F3_IDX, F1_IDX)));
        assertEquals("Raxd6+", format(parse(FEN_MANY_CAPTURES), Move.createCapture(ROOK, A6_IDX, D6_IDX, ROOK)));
        assertEquals("R1xd6+", format(parse(FEN_MANY_CAPTURES), Move.createCapture(ROOK, D1_IDX, D6_IDX, ROOK)));
        assertEquals("R8xd6+", format(parse(FEN_MANY_CAPTURES), Move.createCapture(ROOK, D8_IDX, D6_IDX, ROOK)));
        assertEquals("Bf4+", format(parse(FEN_DRAW_2_2), Move.create(BISHOP, D2_IDX, F4_IDX)));

        // Check mate by piece move
        assertEquals("Ba3#", format(parse(FEN_CHECKMATE_3_2), Move.create(BISHOP, B4_IDX, A3_IDX)));
        assertEquals("Nf7#", format(parse(FEN_CHECKMATE_2_8), Move.create(KNIGHT, H6_IDX, F7_IDX)));
        assertEquals("Rxc1#", format(parse(FEN_CHECKMATE_1_2), Move.createCapture(ROOK, A1_IDX, C1_IDX, BISHOP)));

        // Check by pawn move that promotes to queen
        assertEquals("b8=Q+", format(parse(FEN_DRAW_2_0), Move.createPromotion(B7_IDX, B8_IDX, QUEEN)));
        assertEquals("exf8=Q+", format(parse(FEN_WP_E7F8), Move.createCapturePromotion(E7_IDX, F8_IDX, BISHOP, QUEEN)));
    }

    @Test
    public void testFormatCastling() throws Exception {
        assertEquals("O-O", format(parse(FEN_WKC_OK), Move.createCastling(E1_IDX, G1_IDX)));
        assertEquals("O-O-O", format(parse(FEN_WQC_OK), Move.createCastling(E1_IDX, C1_IDX)));
        assertEquals("O-O", format(parse(FEN_BKC_OK), Move.createCastling(E8_IDX, G8_IDX)));
        assertEquals("O-O-O", format(parse(FEN_BQC_OK), Move.createCastling(E8_IDX, C8_IDX)));
    }

    // -----------------------------------------------------------------------
    // Formatting utility methods:
    // -----------------------------------------------------------------------

    @Test
    public void testGetAllFromSquares() throws Exception {
        assertEquals(asSet(), getAllFromSquares(ROOK, Square.F3, parse(FEN_START)));
        assertEquals(asSet(Square.G1), getAllFromSquares(KNIGHT, Square.F3, parse(FEN_START)));
        assertEquals(asSet(Square.C6, Square.F3), getAllFromSquares(QUEEN, Square.F6, parse(FEN_TWO_QUEENS)));
        assertEquals(asSet(Square.C6, Square.F3), getAllFromSquares(QUEEN, Square.E4, parse(FEN_TWO_QUEENS)));
        assertEquals(asSet(Square.A6, Square.D1, Square.D8), getAllFromSquares(ROOK, Square.D6, parse(FEN_MANY_CAPTURES)));
        assertEquals(asSet(Square.B5, Square.B7, Square.C4), getAllFromSquares(KNIGHT, Square.D6, parse(FEN_MANY_CAPTURES)));
    }

    @Test
    public void testIsFileUnique() {
        assertTrue(isFileUnique(Square.E4, asSet(Square.E4)));
        assertTrue(isFileUnique(Square.E4, asSet(Square.A1, Square.E4, Square.A4, Square.G5)));
        assertTrue(isFileUnique(Square.G8, asSet(Square.B8, Square.A4, Square.H8, Square.G8)));

        assertFalse(isFileUnique(Square.A1, asSet(Square.A1, Square.A2)));
        assertFalse(isFileUnique(Square.A1, asSet(Square.E4, Square.A7, Square.A5, Square.A1)));
        assertFalse(isFileUnique(Square.F7, asSet(Square.A6, Square.A7, Square.F6, Square.F7)));
    }

    @Test
    public void testIsRankUnique() {
        assertTrue(isRankUnique(Square.E4, asSet(Square.E4)));
        assertTrue(isRankUnique(Square.E4, asSet(Square.A1, Square.E4, Square.E7, Square.G5)));
        assertTrue(isRankUnique(Square.G8, asSet(Square.B1, Square.A4, Square.G5, Square.G8)));

        assertFalse(isRankUnique(Square.A1, asSet(Square.A1, Square.B1)));
        assertFalse(isRankUnique(Square.A1, asSet(Square.E4, Square.E1, Square.F1, Square.A1)));
        assertFalse(isRankUnique(Square.F7, asSet(Square.A6, Square.A7, Square.F6, Square.F7)));
    }

    @SafeVarargs
    private static <T> Set<T> asSet(T... values) {
        return new HashSet<>(asList(values));
    }

    // -----------------------------------------------------------------------
    // Parsing:
    // -----------------------------------------------------------------------

    @Test
    public void testParseCastling() throws Exception {
        assertEquals(MOVE_E1G1, SanParser.parse("O-O", parse(FEN_WKC_OK)));
        assertEquals(MOVE_E1C1, SanParser.parse("O-O-O", parse(FEN_WQC_OK)));
        assertEquals(MOVE_E8G8, SanParser.parse("O-O", parse(FEN_BKC_OK)));
        assertEquals(MOVE_E8C8, SanParser.parse("O-O-O", parse(FEN_BQC_OK)));
    }

    @Test
    public void shouldParseSimplePawnMoves() throws Exception {
        assertEquals(MOVE_E2E4, SanParser.parse("e4", parse(FEN_START)));
        assertEquals(MOVE_D2D4, SanParser.parse("d4", parse(FEN_START)));
        assertEquals(MOVE_E7E5, SanParser.parse("e5", parse(FEN_E4)));
        assertEquals(MOVE_E7E6, SanParser.parse("e6", parse(FEN_E4)));
    }

    @Test
    public void shouldParsePawnCaptures() throws Exception {
        assertEquals(MOVE_E4D5, SanParser.parse("exd5", parse(FEN_PC_E4D5)));
        assertEquals(MOVE_D5E4, SanParser.parse("dxe4", parse(FEN_PC_D5E4_D5C4)));
        assertEquals(MOVE_D5C4, SanParser.parse("dxc4", parse(FEN_PC_D5E4_D5C4)));
    }

    @Test
    public void shouldParseEnPassantCaptures() throws Exception {
        assertEquals(MOVE_E5D6, SanParser.parse("exd6", parse(FEN_WEP_E5D6)));
        assertEquals(MOVE_D4C3, SanParser.parse("dxc3", parse(FEN_BEP_D4C3)));
    }

    @Test
    public void shouldNotParseInvalidPawnMoves() {
        IllegalMoveException e = assertThrows(IllegalMoveException.class, () -> SanParser.parse("e4", parse(FEN_E4_C5)));
        assertEquals("illegal pawn move", e.getMessage());

        e = assertThrows(IllegalMoveException.class, () -> SanParser.parse("a7", parse(FEN_E4_C5)));
        assertEquals("illegal pawn move", e.getMessage());

        e = assertThrows(IllegalMoveException.class, () -> SanParser.parse("dxe5", parse(FEN_E4_C5)));
        assertEquals("illegal pawn move, no pawn on d4", e.getMessage());

        e = assertThrows(IllegalMoveException.class, () -> SanParser.parse("exd5", parse(FEN_E4_C5)));
        assertEquals("illegal capture", e.getMessage());
    }

    // TODO: Add more parsing tests.
}
