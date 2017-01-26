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
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class is for testing class {@code Board} using JUnit.
 *
 * @author Johan Dykstrom
 * @see Board
 */
public class BoardTest extends AbstractTestCase {

    @Test
    public void testGetFile() {
        assertEquals(1, Board.getFile(Square.A1));
        assertEquals(1, Board.getFile(Square.A8));
        assertEquals(8, Board.getFile(Square.H1));
        assertEquals(8, Board.getFile(Square.H8));
        assertEquals(4, Board.getFile(Square.D7));
        assertEquals(5, Board.getFile(Square.E2));
    }

    @Test
    public void testGetFileChar() {
        assertEquals('a', Board.getFileChar(Square.A1));
        assertEquals('a', Board.getFileChar(Square.A8));
        assertEquals('h', Board.getFileChar(Square.H1));
        assertEquals('h', Board.getFileChar(Square.H8));
        assertEquals('b', Board.getFileChar(Square.B3));
        assertEquals('g', Board.getFileChar(Square.G7));
    }

    @Test
    public void testGetRank() {
        assertEquals(1, Board.getRank(Square.A1));
        assertEquals(8, Board.getRank(Square.A8));
        assertEquals(1, Board.getRank(Square.H1));
        assertEquals(8, Board.getRank(Square.H8));
        assertEquals(7, Board.getRank(Square.D7));
        assertEquals(2, Board.getRank(Square.E2));
    }

    @Test
    public void testGetSquareId() {
        assertEquals(Square.A1, Board.getSquareId(1, 1));
        assertEquals(Square.A8, Board.getSquareId(1, 8));
        assertEquals(Square.H1, Board.getSquareId(8, 1));
        assertEquals(Square.H8, Board.getSquareId(8, 8));
        assertEquals(Square.E4, Board.getSquareId(5, 4));
        assertEquals(Square.G2, Board.getSquareId(7, 2));
    }

    /**
     * Tests the FILE and RANK constants.
     */
    @SuppressWarnings("ConstantConditions")
    @Test
    public void testFileAndRank() {
        assertTrue((Square.A1 & Board.FILE_A) != 0);
        assertTrue((Square.C3 & Board.FILE_C) != 0);
        assertTrue((Square.H8 & Board.FILE_H) != 0);

        assertTrue((Square.A1 & Board.RANK_1) != 0);
        assertTrue((Square.C3 & Board.RANK_3) != 0);
        assertTrue((Square.H8 & Board.RANK_8) != 0);

        assertEquals(0, Square.A1 & Board.FILE_B);
        assertEquals(0, Square.H8 & Board.RANK_7);
    }

    @Test
    public void testCountPiecesStart() throws Exception {
        Position position = FenParser.parse(FEN_START);

        assertEquals(2, Board.popCount(position.white & position.bishop));
        assertEquals(2, Board.popCount(position.white & position.knight));
        assertEquals(1, Board.popCount(position.white & position.queen));
        assertEquals(8, Board.popCount(position.white & position.pawn));
        assertEquals(2, Board.popCount(position.white & position.rook));

        assertEquals(2, Board.popCount(position.black & position.bishop));
        assertEquals(2, Board.popCount(position.black & position.knight));
        assertEquals(1, Board.popCount(position.black & position.queen));
        assertEquals(8, Board.popCount(position.black & position.pawn));
        assertEquals(2, Board.popCount(position.black & position.rook));
    }

    @Test
    public void testCountPiecesTwoQueens() throws Exception {
        Position position = FenParser.parse(FEN_TWO_QUEENS);

        assertEquals(2, Board.popCount(position.white & position.bishop));
        assertEquals(0, Board.popCount(position.white & position.knight));
        assertEquals(1, Board.popCount(position.white & position.queen));
        assertEquals(5, Board.popCount(position.white & position.pawn));
        assertEquals(0, Board.popCount(position.white & position.rook));

        assertEquals(0, Board.popCount(position.black & position.bishop));
        assertEquals(0, Board.popCount(position.black & position.knight));
        assertEquals(2, Board.popCount(position.black & position.queen));
        assertEquals(3, Board.popCount(position.black & position.pawn));
        assertEquals(1, Board.popCount(position.black & position.rook));
    }
}
