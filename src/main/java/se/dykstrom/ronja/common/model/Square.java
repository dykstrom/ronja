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
 * Contains constants and methods related to the squares on a chess board. A square can be
 * identified in three ways - using a name, an ID, or an index. A square name is a lower case
 * string, e.g. "a1" or "h8". A square ID is a long integer between (1 &lt;&lt; 0) and
 * (1 &lt;&lt; 63). A square index is an integer between 0 and 63. The Square class provides
 * methods for converting between names, IDs, and indices.
 *
 * @author Johan Dykstrom
 */
public class Square {

    /** Square IDs. */
    public static final long A1 = 1L;
    public static final long B1 = 1L << 1L;
    public static final long C1 = 1L << 2L;
    public static final long D1 = 1L << 3L;
    public static final long E1 = 1L << 4L;
    public static final long F1 = 1L << 5L;
    public static final long G1 = 1L << 6L;
    public static final long H1 = 1L << 7L;
    public static final long A2 = 1L << 8L;
    public static final long B2 = 1L << 9L;
    public static final long C2 = 1L << 10L;
    public static final long D2 = 1L << 11L;
    public static final long E2 = 1L << 12L;
    public static final long F2 = 1L << 13L;
    public static final long G2 = 1L << 14L;
    public static final long H2 = 1L << 15L;
    public static final long A3 = 1L << 16L;
    public static final long B3 = 1L << 17L;
    public static final long C3 = 1L << 18L;
    public static final long D3 = 1L << 19L;
    public static final long E3 = 1L << 20L;
    public static final long F3 = 1L << 21L;
    public static final long G3 = 1L << 22L;
    public static final long H3 = 1L << 23L;
    public static final long A4 = 1L << 24L;
    public static final long B4 = 1L << 25L;
    public static final long C4 = 1L << 26L;
    public static final long D4 = 1L << 27L;
    public static final long E4 = 1L << 28L;
    public static final long F4 = 1L << 29L;
    public static final long G4 = 1L << 30L;
    public static final long H4 = 1L << 31L;
    public static final long A5 = 1L << 32L;
    public static final long B5 = 1L << 33L;
    public static final long C5 = 1L << 34L;
    public static final long D5 = 1L << 35L;
    public static final long E5 = 1L << 36L;
    public static final long F5 = 1L << 37L;
    public static final long G5 = 1L << 38L;
    public static final long H5 = 1L << 39L;
    public static final long A6 = 1L << 40L;
    public static final long B6 = 1L << 41L;
    public static final long C6 = 1L << 42L;
    public static final long D6 = 1L << 43L;
    public static final long E6 = 1L << 44L;
    public static final long F6 = 1L << 45L;
    public static final long G6 = 1L << 46L;
    public static final long H6 = 1L << 47L;
    public static final long A7 = 1L << 48L;
    public static final long B7 = 1L << 49L;
    public static final long C7 = 1L << 50L;
    public static final long D7 = 1L << 51L;
    public static final long E7 = 1L << 52L;
    public static final long F7 = 1L << 53L;
    public static final long G7 = 1L << 54L;
    public static final long H7 = 1L << 55L;
    public static final long A8 = 1L << 56L;
    public static final long B8 = 1L << 57L;
    public static final long C8 = 1L << 58L;
    public static final long D8 = 1L << 59L;
    public static final long E8 = 1L << 60L;
    public static final long F8 = 1L << 61L;
    public static final long G8 = 1L << 62L;
    public static final long H8 = 1L << 63L;

    public static final int A1_IDX = 0;
    public static final int B1_IDX = 1;
    public static final int C1_IDX = 2;
    public static final int D1_IDX = 3;
    public static final int E1_IDX = 4;
    public static final int F1_IDX = 5;
    public static final int G1_IDX = 6;
    public static final int H1_IDX = 7;
    public static final int A2_IDX = 8;
    public static final int B2_IDX = 9;
    public static final int C2_IDX = 10;
    public static final int D2_IDX = 11;
    public static final int E2_IDX = 12;
    public static final int F2_IDX = 13;
    public static final int G2_IDX = 14;
    public static final int H2_IDX = 15;
    public static final int A3_IDX = 16;
    public static final int B3_IDX = 17;
    public static final int C3_IDX = 18;
    public static final int D3_IDX = 19;
    public static final int E3_IDX = 20;
    public static final int F3_IDX = 21;
    public static final int G3_IDX = 22;
    public static final int H3_IDX = 23;
    public static final int A4_IDX = 24;
    public static final int B4_IDX = 25;
    public static final int C4_IDX = 26;
    public static final int D4_IDX = 27;
    public static final int E4_IDX = 28;
    public static final int F4_IDX = 29;
    public static final int G4_IDX = 30;
    public static final int H4_IDX = 31;
    public static final int A5_IDX = 32;
    public static final int B5_IDX = 33;
    public static final int C5_IDX = 34;
    public static final int D5_IDX = 35;
    public static final int E5_IDX = 36;
    public static final int F5_IDX = 37;
    public static final int G5_IDX = 38;
    public static final int H5_IDX = 39;
    public static final int A6_IDX = 40;
    public static final int B6_IDX = 41;
    public static final int C6_IDX = 42;
    public static final int D6_IDX = 43;
    public static final int E6_IDX = 44;
    public static final int F6_IDX = 45;
    public static final int G6_IDX = 46;
    public static final int H6_IDX = 47;
    public static final int A7_IDX = 48;
    public static final int B7_IDX = 49;
    public static final int C7_IDX = 50;
    public static final int D7_IDX = 51;
    public static final int E7_IDX = 52;
    public static final int F7_IDX = 53;
    public static final int G7_IDX = 54;
    public static final int H7_IDX = 55;
    public static final int A8_IDX = 56;
    public static final int B8_IDX = 57;
    public static final int C8_IDX = 58;
    public static final int D8_IDX = 59;
    public static final int E8_IDX = 60;
    public static final int F8_IDX = 61;
    public static final int G8_IDX = 62;
    public static final int H8_IDX = 63;

    private static final String SQUARES =
            "a1b1c1d1e1f1g1h1" +
            "a2b2c2d2e2f2g2h2" +
            "a3b3c3d3e3f3g3h3" +
            "a4b4c4d4e4f4g4h4" +
            "a5b5c5d5e5f5g5h5" +
            "a6b6c6d6e6f6g6h6" +
            "a7b7c7d7e7f7g7h7" +
            "a8b8c8d8e8f8g8h8";

    // ------------------------------------------------------------------------

    public static final int MAX_SQUARES = 64;

    public static final long[] SQUARE_IDS = new long[MAX_SQUARES];
    public static final int[] SQUARE_INDICES = new int[MAX_SQUARES];

    private Square() { }

    /**
     * Stores the square IDs of the occupied squares in the given bitboard in the array {@link #SQUARE_IDS},
     * and returns the number of square IDs found in the bitboard.
     *
     * Example: bitboardToIds(Square.A1 | Square.B1) will store [Square.A1, Square.B1] in the array, and return 2.
     *
     * @param bitboard The bitboard to split into square IDs.
     * @return The number of square IDs found in the bitboard.
     */
    public static int bitboardToIds(long bitboard) {
        int count = 0;

        for (long b = bitboard; b != 0;) {
            int index = Long.numberOfTrailingZeros(b);
            long id = 1L << index;
            SQUARE_IDS[count++] = id;
            b &= ~id;
        }

        return count;
    }

    // ------------------------------------------------------------------------
    // Methods for converting between IDs and names:
    // ------------------------------------------------------------------------

    /**
     * Returns the name of the square with the specified ID.
     *
     * @param id The long ID of the specified square.
     * @return The name of the specified square.
     */
    public static String idToName(long id) {
        return indexToName(idToIndex(id));
    }

    /**
     * Returns the ID of the specified square.
     *
     * @param square The name of the specified square.
     * @return The long ID of the specified square.
     */
    public static long nameToId(String square) {
        return indexToId(nameToIndex(square));
    }

    // ------------------------------------------------------------------------
    // Methods for converting between IDs and indices:
    // ------------------------------------------------------------------------

    /**
     * Stores the square indices of the occupied squares in the given bitboard in the array {@link #SQUARE_INDICES},
     * and returns the number of square indices found in the bitboard. This implementation works best for sparsely
     * populated bitboards.
     * <p>
     * Example: bitboardToIndices(Square.A1 | Square.E4) returns 2,
     * and stores values 0 and 28 in {@link #SQUARE_INDICES}.
     *
     * @param bitboard The bitboard to split into square indices.
     * @return The number of square indices found in the bitboard.
     */
    public static int bitboardToIndices(long bitboard) {
        int count = 0;

        for (long b = bitboard; b != 0;) {
            int index = Long.numberOfTrailingZeros(b);
            SQUARE_INDICES[count++] = index;
            b &= ~(1L << index);
        }

        return count;
    }

    /**
     * Returns the index of the square with the specified ID.
     * <p>
     * Example: idToIndex(Square.E4) returns 28.
     *
     * @param id The long ID of the square.
     * @return The integer index of the specified square.
     */
    public static int idToIndex(long id) {
        return Long.numberOfTrailingZeros(id);
    }

    /**
     * Returns the ID of the square with the specified index.
     *
     * @param index The integer index of the square.
     * @return The long ID of the specified square.
     */
    public static long indexToId(int index) {
        return 1L << index;
    }

    // ------------------------------------------------------------------------
    // Methods for converting between indices and names:
    // ------------------------------------------------------------------------

    /**
     * Returns the name of the specified square. The square index is an integer between 0 and 63.
     *
     * @param index The integer index of the square.
     * @return The name of the specified square.
     */
    public static String indexToName(int index) {
        return SQUARES.substring(index * 2, (index * 2) + 2);
    }

    /**
     * Returns the index of the specified square. The square name is a lower case string between "a1" and "h8".
     *
     * @param name The name of the square.
     * @return The integer index of the specified square.
     */
    public static int nameToIndex(String name) {
        return SQUARES.indexOf(name) / 2;
    }

    // ------------------------------------------------------------------------
    // Methods for moving from one square to another:
    // ------------------------------------------------------------------------

    /**
     * Returns the square west of the given {@code square}.
     * <p>
     * Note that we need to use "logical shift" instead of "arithmetic shift".
     * We are dealing with unsigned longs and do not want "sign-extension" when
     * shifting. This does not work for square H8.
     */
    public static long west(long square) {
        return square >>> 1;
    }

    /**
     * Returns the square south of the given {@code square}.
     */
    public static long south(long square) {
        return square >>> 8;
    }

    /**
     * Returns the square east of the given {@code square}.
     */
    public static long east(long square) {
        return square << 1;
    }

    /**
     * Returns the square north of the given {@code square}.
     */
    public static long north(long square) {
        return square << 8;
    }

    /**
     * Returns the square northeast of the given {@code square}.
     */
    public static long northEast(long square) {
        return square << 9;
    }

    /**
     * Returns the square southeast of the given {@code square}.
     */
    public static long southEast(long square) {
        return square >>> 7;
    }

    /**
     * Returns the square southwest of the given {@code square}.
     */
    public static long southWest(long square) {
        return square >>> 9;
    }

    /**
     * Returns the square northwest of the given {@code square}.
     */
    public static long northWest(long square) {
        return square << 7;
    }

    /**
     * Returns the square two squares north of the given {@code square}.
     */
    public static long northNorth(long square) {
        return square << 16;
    }

    /**
     * Returns the square two squares south of the given {@code square}.
     */
    public static long southSouth(long square) {
        return square >>> 16;
    }
}
