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

package se.dykstrom.ronja.engine.utils;

import se.dykstrom.ronja.common.model.Board;

/**
 * Miscellaneous utility functions.
 *
 * @author Johan Dykstrom
 */
@SuppressWarnings("unused")
public final class Utils {

    private Utils() { }

    /**
     * Returns a string representation of the given bitboards. The bitboards
     * are placed beside each other in the resulting string.
     *
     * @param bitboards An array of bitboards to convert to a string.
     * @return A string representation of the bitboards.
     */
    public static String toString(final long... bitboards) {
        final var ranks = new StringBuilder[8];

        // For each rank
        for (var r = 0; r < 8; r++) {
            ranks[r] = new StringBuilder();
            // For each bitboard
            for (final long bitboard : bitboards) {
                // For each file
                for (var f = 0; f < 8; f++) {
                    final long square = Board.getSquareId(f + 1, r + 1);
                    if ((square & bitboard) != 0) {
                        ranks[r].append("1");
                    } else {
                        ranks[r].append("0");
                    }
                }
                ranks[r].append("  ");
            }
        }

        // Turn board upside down
        final var builder = new StringBuilder();
        for (var r = 7; r >= 0; r--) {
            builder.append(ranks[r].toString().strip()).append("\n");
        }
        return builder.toString();
    }
}
