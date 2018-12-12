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

/**
 * Contains constants and methods related to the chess board.
 *
 * @author Johan Dykstrom
 */
public class Board {

    /** File constants. */
    public static final long FILE_A = Square.A1 + Square.A2 + Square.A3 + Square.A4 + Square.A5 + Square.A6 + Square.A7 + Square.A8;
    public static final long FILE_B = Square.B1 + Square.B2 + Square.B3 + Square.B4 + Square.B5 + Square.B6 + Square.B7 + Square.B8;
    public static final long FILE_C = Square.C1 + Square.C2 + Square.C3 + Square.C4 + Square.C5 + Square.C6 + Square.C7 + Square.C8;
    public static final long FILE_D = Square.D1 + Square.D2 + Square.D3 + Square.D4 + Square.D5 + Square.D6 + Square.D7 + Square.D8;
    public static final long FILE_E = Square.E1 + Square.E2 + Square.E3 + Square.E4 + Square.E5 + Square.E6 + Square.E7 + Square.E8;
    public static final long FILE_F = Square.F1 + Square.F2 + Square.F3 + Square.F4 + Square.F5 + Square.F6 + Square.F7 + Square.F8;
    public static final long FILE_G = Square.G1 + Square.G2 + Square.G3 + Square.G4 + Square.G5 + Square.G6 + Square.G7 + Square.G8;
    public static final long FILE_H = Square.H1 + Square.H2 + Square.H3 + Square.H4 + Square.H5 + Square.H6 + Square.H7 + Square.H8;

    /** Rank constants. */
    public static final long RANK_1 = Square.A1 + Square.B1 + Square.C1 + Square.D1 + Square.E1 + Square.F1 + Square.G1 + Square.H1;
    public static final long RANK_2 = Square.A2 + Square.B2 + Square.C2 + Square.D2 + Square.E2 + Square.F2 + Square.G2 + Square.H2;
    public static final long RANK_3 = Square.A3 + Square.B3 + Square.C3 + Square.D3 + Square.E3 + Square.F3 + Square.G3 + Square.H3;
    public static final long RANK_4 = Square.A4 + Square.B4 + Square.C4 + Square.D4 + Square.E4 + Square.F4 + Square.G4 + Square.H4;
    public static final long RANK_5 = Square.A5 + Square.B5 + Square.C5 + Square.D5 + Square.E5 + Square.F5 + Square.G5 + Square.H5;
    public static final long RANK_6 = Square.A6 + Square.B6 + Square.C6 + Square.D6 + Square.E6 + Square.F6 + Square.G6 + Square.H6;
    public static final long RANK_7 = Square.A7 + Square.B7 + Square.C7 + Square.D7 + Square.E7 + Square.F7 + Square.G7 + Square.H7;
    public static final long RANK_8 = Square.A8 + Square.B8 + Square.C8 + Square.D8 + Square.E8 + Square.F8 + Square.G8 + Square.H8;

    private static final long[][] RANKS = {
            {Square.A1, Square.B1, Square.C1, Square.D1, Square.E1, Square.F1, Square.G1, Square.H1},
            {Square.A2, Square.B2, Square.C2, Square.D2, Square.E2, Square.F2, Square.G2, Square.H2},
            {Square.A3, Square.B3, Square.C3, Square.D3, Square.E3, Square.F3, Square.G3, Square.H3},
            {Square.A4, Square.B4, Square.C4, Square.D4, Square.E4, Square.F4, Square.G4, Square.H4},
            {Square.A5, Square.B5, Square.C5, Square.D5, Square.E5, Square.F5, Square.G5, Square.H5},
            {Square.A6, Square.B6, Square.C6, Square.D6, Square.E6, Square.F6, Square.G6, Square.H6},
            {Square.A7, Square.B7, Square.C7, Square.D7, Square.E7, Square.F7, Square.G7, Square.H7},
            {Square.A8, Square.B8, Square.C8, Square.D8, Square.E8, Square.F8, Square.G8, Square.H8}
    };
    
    /**
     * Returns the file (a = 1, b = 2, ..., h = 8) of the given square.
     */
    public static int getFile(long id) {
        return (Square.idToIndex(id) % 8) + 1;
    }

    /**
     * Returns the file character (a, b, ..., h) of the given square.
     */
    public static char getFileChar(long id) {
        return "abcdefgh".charAt(getFile(id) - 1);
    }

    /**
     * Returns the rank (1-8) of the given square.
     */
    public static int getRank(long id) {
        return (Square.idToIndex(id) / 8) + 1;
    }

    /**
     * Returns the square ID at the given file and rank.
     */
    public static long getSquareId(int file, int rank) {
        return getSquaresInRank(rank)[file - 1];
    }

    /**
     * Returns an array of all squares that belong to the given rank (1-8).
     */
    public static long[] getSquaresInRank(int rank) {
        return RANKS[rank - 1];
    }

    /**
     * Returns the population count, that is, the number of pieces on the given bitboard.
     */
    public static int popCount(final long bitboard) {
        int count = 0;
        for (long b = bitboard; b != 0; b &= (b - 1)) { count++; }
        return count;
    }
}
