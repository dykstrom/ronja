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
 * Enumerates the possible colors of a chess piece.
 *
 * @author Johan Dykstrom
 */
public enum Color {

    BLACK('b'),
    WHITE('w');

    private final char symbol;

    Color(char symbol) {
        this.symbol = symbol;
    }

    public final char getSymbol() {
        return symbol;
    }

    /**
     * Returns the {@code Color} corresponding to the given symbol.
     *
     * @param symbol The symbol of the color ('b' or 'w').
     * @return The color.
     * @throws IllegalArgumentException If the symbol cannot be interpreted as a color.
     */
    public static Color valueOf(char symbol) throws IllegalArgumentException {
        return switch (symbol) {
            case 'b' -> BLACK;
            case 'w' -> WHITE;
            default -> throw new IllegalArgumentException("invalid color: " + symbol);
        };
    }

    /**
     * Flips the color, that is, for WHITE returns BLACK and the other way around.
     */
    public Color flip() {
        return (this == WHITE) ? BLACK : WHITE;
    }
}
