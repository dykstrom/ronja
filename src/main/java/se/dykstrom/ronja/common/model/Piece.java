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
 * An enum class that enumerates all chess pieces, including pawns. The method
 * {@link #valueOf(char)} can be used to convert a symbol to a {@code Piece}.
 *
 * @author Johan Dykstrom
 */
public enum Piece {

    BISHOP('B'),
    KING('K'),
    KNIGHT('N'),
    PAWN('P'),
    QUEEN('Q'),
    ROOK('R');

    /** The piece symbol, e.g. 'R' for Rook. */
    private final char symbol;

    /**
     * Creates a new piece enum value with the given symbol.
     */
    Piece(char symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol of this piece, that is one of BKNPQR.
     */
    public final char getSymbol() {
        return symbol;
    }

    /**
     * Returns the symbol of this piece for the given {@code color}, that is,
     * one of BKNPQR for White and one of bknpqr for Black.
     */
    public final char getSymbol(Color color) {
        if (color == Color.WHITE) {
            return symbol;
        } else {
            return Character.toLowerCase(symbol);
        }
    }

	/**
	 * Returns the {@code Piece} corresponding to the given symbol.
	 *
	 * @param symbol The symbol of the piece.
	 * @return The piece.
     * @throws IllegalArgumentException If the symbol cannot be interpreted as a piece.
	 */
	public static Piece valueOf(char symbol) throws IllegalArgumentException {
		switch (symbol) {
        case 'b':
        case 'B':
			return BISHOP;
        case 'k':
        case 'K':
			return KING;
        case 'n':
        case 'N':
			return KNIGHT;
        case 'p':
        case 'P':
			return PAWN;
        case 'q':
        case 'Q':
			return QUEEN;
        case 'r':
        case 'R':
			return ROOK;
		default:
			throw new IllegalArgumentException("invalid piece: " + symbol);
		}
	}
}
