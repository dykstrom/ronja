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

import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Piece;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.model.Position;

/**
 * A class that can parse and format moves specified in Coordinate Algebraic Notation (CAN).
 *
 * @author Johan Dykstrom
 */
public class CanParser extends AbstractMoveParser {

    /**
     * Returns {@code true} if the given string of characters is a syntactically
     * valid chess move in coordinate algebraic notation.
     *
     * @param move The move to validate.
     * @return True if the given string is a syntactically valid move.
     */
    public static boolean isMove(String move) {
        return move.matches("[a-h][1-8][a-h][1-8]") ||     // Normal move
               move.matches("[a-h][7][a-h][8][qrbn]") ||   // White promotion
               move.matches("[a-h][2][a-h][1][qrbn]");     // Black promotion
    }

	/**
	 * Parses and validates a move specified in CAN format in the given {@code position}.
     *
	 * @param move The move in CAN format.
	 * @param position The position when the move is made.
	 * @throws IllegalMoveException If the given string cannot be parsed as a legal move in CAN format.
	 */
    public static Move parse(String move, Position position) throws IllegalMoveException {
        long from = Square.nameToId(move.substring(0, 2));
        long to = Square.nameToId(move.substring(2, 4));
        Piece piece = position.getPiece(from);
        Piece promoted = (move.length() == 5) ? getPromotionPiece(position, from, move.charAt(4)) : null;
        boolean isCastling = isCastling(position, from, to);
        boolean isEnPassant = isEnPassant(position, from, to);

        validate(position, from, to, isCastling);

        return Move.of(piece, from, to, promoted, isCastling, isEnPassant);
	}

    /**
     * Formats the given move in CAN format.
     *
     * @param move The move to format.
     * @return The formatted move.
     */
    public static String format(Move move) {
        StringBuilder builder = new StringBuilder();

        builder.append(Square.idToName(move.getFrom()));
        builder.append(Square.idToName(move.getTo()));
        if (move.isPromotion()) {
            builder.append(move.getPromoted().getSymbol());
        }

        return builder.toString().toLowerCase();
    }
}
