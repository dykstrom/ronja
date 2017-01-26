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
import se.dykstrom.ronja.test.AbstractTestCase;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static se.dykstrom.ronja.common.model.Piece.*;
import static se.dykstrom.ronja.common.parser.FenParser.parse;
import static se.dykstrom.ronja.common.parser.SanParser.*;

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
        assertEquals("e4", format(Move.of(PAWN, Square.E2, Square.E4, null, false, false), parse(FEN_START)));
        assertEquals("c5", format(Move.of(PAWN, Square.C7, Square.C5, null, false, false), parse(FEN_E4)));
    }

    @Test
    public void testFormatPawnCapture() throws Exception {
        assertEquals("exd5", format(Move.of(PAWN, Square.E4, Square.D5, null, false, false), parse(FEN_PC_E4D5)));
        // The pawn of d5 can take on e4 or c4
        assertEquals("dxe4", format(Move.of(PAWN, Square.D5, Square.E4, null, false, false), parse(FEN_PC_D5E4_D5C4)));
        assertEquals("dxc4", format(Move.of(PAWN, Square.D5, Square.C4, null, false, false), parse(FEN_PC_D5E4_D5C4)));
        // The pawn on e4, and the pawn on c4, can take on d5
        assertEquals("exd5", format(Move.of(PAWN, Square.E4, Square.D5, null, false, false), parse(FEN_PC_E4D5_C4D5)));
        assertEquals("cxd5", format(Move.of(PAWN, Square.C4, Square.D5, null, false, false), parse(FEN_PC_E4D5_C4D5)));
    }

    @Test
    public void testFormatPawnPromotion() throws Exception {
        assertEquals("exf8=N", format(Move.of(PAWN, Square.E7, Square.F8, KNIGHT, false, false), parse(FEN_WP_E7F8)));
        assertEquals("a1=R", format(Move.of(PAWN, Square.A2, Square.A1, ROOK, false, false), parse(FEN_BP_A2A1)));
    }

    @Test
    public void testFormatPawnEnPassant() throws Exception {
        assertEquals("exd6", format(Move.of(PAWN, Square.E5, Square.D6, null, false, true), parse(FEN_WEP_E5D6)));
        assertEquals("dxc3", format(Move.of(PAWN, Square.D4, Square.C3, null, false, true), parse(FEN_BEP_D4C3)));
    }

    @Test
    public void testFormatPieceMove() throws Exception {
        assertEquals("Nf3", format(Move.of(KNIGHT, Square.G1, Square.F3, null, false, false), parse(FEN_START)));
        assertEquals("Qh6", format(Move.of(QUEEN, Square.H4, Square.H6, null, false, false), parse(FEN_E4_E5_QG4_QH4_NF3)));
        assertEquals("Be7", format(Move.of(BISHOP, Square.F8, Square.E7, null, false, false), parse(FEN_E4_E5_QG4_QH4_NF3)));
        assertEquals("Ke7", format(Move.of(KING, Square.E8, Square.E7, null, false, false), parse(FEN_E4_E5_QG4_QH4_NF3)));
        assertEquals("Rd1", format(Move.of(ROOK, Square.A1, Square.D1, null, false, false), parse(FEN_WQC_OK)));

        assertEquals("Qcf6", format(Move.of(QUEEN, Square.C6, Square.F6, null, false, false), parse(FEN_TWO_QUEENS)));
        assertEquals("Qff6", format(Move.of(QUEEN, Square.F3, Square.F6, null, false, false), parse(FEN_TWO_QUEENS)));
        assertEquals("Nba3", format(Move.of(KNIGHT, Square.B5, Square.A3, null, false, false), parse(FEN_MANY_CAPTURES)));
        assertEquals("Nca3", format(Move.of(KNIGHT, Square.C4, Square.A3, null, false, false), parse(FEN_MANY_CAPTURES)));
    }

    @Test
    public void testFormatPieceCapture() throws Exception {
        assertEquals("Nxe4", format(Move.of(KNIGHT, Square.F6, Square.E4, null, false, false), parse(FEN_MIDDLE_GAME_1)));
        assertEquals("Bxa8", format(Move.of(BISHOP, Square.B7, Square.A8, null, false, false), parse(FEN_MIDDLE_GAME_1)));
        assertEquals("Rxf7", format(Move.of(ROOK, Square.F8, Square.F7, null, false, false), parse(FEN_END_GAME_0)));
        assertEquals("Qxf7", format(Move.of(QUEEN, Square.D7, Square.F7, null, false, false), parse(FEN_END_GAME_0)));
        assertEquals("Kxb8", format(Move.of(KING, Square.A7, Square.B8, null, false, false), parse(FEN_DRAW_2_1)));

        assertEquals("Qcxe4", format(Move.of(QUEEN, Square.C6, Square.E4, null, false, false), parse(FEN_TWO_QUEENS)));
        assertEquals("Qfxe4", format(Move.of(QUEEN, Square.F3, Square.E4, null, false, false), parse(FEN_TWO_QUEENS)));
        assertEquals("N5xd6", format(Move.of(KNIGHT, Square.B5, Square.D6, null, false, false), parse(FEN_MANY_CAPTURES)));
        assertEquals("N7xd6", format(Move.of(KNIGHT, Square.B7, Square.D6, null, false, false), parse(FEN_MANY_CAPTURES)));
        assertEquals("Ncxd6", format(Move.of(KNIGHT, Square.C4, Square.D6, null, false, false), parse(FEN_MANY_CAPTURES)));
    }

    @Test
    public void testFormatCheck() throws Exception {
        // Check by piece move
        assertEquals("Qf1+", format(Move.of(QUEEN, Square.F3, Square.F1, null, false, false), parse(FEN_TWO_QUEENS)));
        assertEquals("Raxd6+", format(Move.of(ROOK, Square.A6, Square.D6, null, false, false), parse(FEN_MANY_CAPTURES)));
        assertEquals("R1xd6+", format(Move.of(ROOK, Square.D1, Square.D6, null, false, false), parse(FEN_MANY_CAPTURES)));
        assertEquals("R8xd6+", format(Move.of(ROOK, Square.D8, Square.D6, null, false, false), parse(FEN_MANY_CAPTURES)));
        assertEquals("Bf4+", format(Move.of(BISHOP, Square.D2, Square.F4, null, false, false), parse(FEN_DRAW_2_2)));

        // Check mate by piece move
        assertEquals("Ba3#", format(Move.of(BISHOP, Square.B4, Square.A3, null, false, false), parse(FEN_CHECKMATE_3_2)));
        assertEquals("Nf7#", format(Move.of(KNIGHT, Square.H6, Square.F7, null, false, false), parse(FEN_CHECKMATE_2_8)));
        assertEquals("Rxc1#", format(Move.of(ROOK, Square.A1, Square.C1, null, false, false), parse(FEN_CHECKMATE_1_2)));

        // Check by pawn move that promotes to queen
        assertEquals("b8=Q+", format(Move.of(PAWN, Square.B7, Square.B8, QUEEN, false, false), parse(FEN_DRAW_2_0)));
        assertEquals("exf8=Q+", format(Move.of(PAWN, Square.E7, Square.F8, QUEEN, false, false), parse(FEN_WP_E7F8)));
    }

    @Test
    public void testFormatCastling() throws Exception {
        assertEquals("O-O", format(Move.of(KING, Square.E1, Square.G1, null, true, false), parse(FEN_WKC_OK)));
        assertEquals("O-O-O", format(Move.of(KING, Square.E1, Square.C1, null, true, false), parse(FEN_WQC_OK)));
        assertEquals("O-O", format(Move.of(KING, Square.E8, Square.G8, null, true, false), parse(FEN_BKC_OK)));
        assertEquals("O-O-O", format(Move.of(KING, Square.E8, Square.C8, null, true, false), parse(FEN_BQC_OK)));
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
        assertEquals(Move.of(KING, Square.E1, Square.G1, null, true, false), SanParser.parse("O-O", parse(FEN_WKC_OK)));
        assertEquals(Move.of(KING, Square.E1, Square.C1, null, true, false), SanParser.parse("O-O-O", parse(FEN_WQC_OK)));
        assertEquals(Move.of(KING, Square.E8, Square.G8, null, true, false), SanParser.parse("O-O", parse(FEN_BKC_OK)));
        assertEquals(Move.of(KING, Square.E8, Square.C8, null, true, false), SanParser.parse("O-O-O", parse(FEN_BQC_OK)));
    }

    // TODO: Add more parsing tests.
}
