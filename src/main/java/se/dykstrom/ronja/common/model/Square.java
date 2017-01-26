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

import java.util.ArrayList;
import java.util.List;

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
    @SuppressWarnings("NumericOverflow")
    public static final long H8 = 1L << 63L;

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

    /**
     * Returns a list of square IDs representing the squares that are occupied in the given bitboard.
     * <p>
     * Example: bitboardToIds(Square.A1 | Square.B1) returns [Square.A1, Square.B1].
     */
    public static List<Long> bitboardToIds(long bitboard) {
        List<Long> result = new ArrayList<>();

        for (long b = bitboard; b != 0;) {
            int index = Long.numberOfTrailingZeros(b);
            long id = 1L << index;
            result.add(id);
            b &= ~id;
        }

        return result;
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
     * Returns a list of square indices representing the squares that are occupied in the given bitboard.
     * This implementation works best for sparsely populated bitboards.
     * <p>
     * Example: bitboardToIndices(Square.A1 | Square.E4) returns [0, 28].
     */
    public static List<Integer> bitboardToIndices(long bitboard) {
        List<Integer> result = new ArrayList<>();

        for (long b = bitboard; b != 0;) {
            int index = Long.numberOfTrailingZeros(b);
            result.add(index);
            b &= ~(1L << index);
        }

        return result;
    }

//  Alternative implementation:
//
//  public static List<Integer> bitboardToIndices(long bitboard) {
//      List<Integer> result = new ArrayList<>();
//
//      int i = 0;
//      for (long b = bitboard; b != 0; b = b >>> 1) {
//          if ((b & 1) != 0) {
//              result.add(i);
//          }
//          i++;
//      }
//
//      return result;
//  }

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
