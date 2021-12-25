/*
 * Copyright (C) 2017 Johan Dykstrom
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
 * This class enumerates all chess pieces, and provides functionality to convert
 * pieces to symbols, and vice versa.
 * 
 * @author Johan Dykstrom
 */
public class Piece {

    public static final int BISHOP = 1;
    public static final int KING   = 2;
    public static final int KNIGHT = 3;
    public static final int PAWN   = 4;
    public static final int QUEEN  = 5;
    public static final int ROOK   = 6;

    /**
     * Returns the symbol for the given piece.
     */
    public static char toSymbol(int piece) {
        return " BKNPQR".charAt(piece);
    }

    /**
     * Returns the symbol for the given piece, taking into account the piece color.
     */
    public static char toSymbol(int piece, Color color) {
        char symbol = toSymbol(piece);
        if (color == Color.WHITE) {
            return symbol;
        } else {
            return Character.toLowerCase(symbol);
        }
    }

    /**
     * Returns the piece that corresponds to the given symbol.
     */
    public static int valueOf(char symbol) {
        return switch (Character.toUpperCase(symbol)) {
            case 'B' -> BISHOP;
            case 'K' -> KING;
            case 'N' -> KNIGHT;
            case 'P' -> PAWN;
            case 'Q' -> QUEEN;
            case 'R' -> ROOK;
            default -> throw new IllegalArgumentException("invalid piece: " + symbol);
        };
    }
}
