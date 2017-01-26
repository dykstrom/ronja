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

import se.dykstrom.ronja.common.model.Piece;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.model.Color;
import se.dykstrom.ronja.common.model.Position;

/**
 * An abstract base class for different move parsers.
 *
 * @author Johan Dykstrom
 */
abstract class AbstractMoveParser {

    /**
     * Returns the promotion piece that results from moving a pawn on {@code from} in {@code position}.
     * symbol of the piece is {@code symbol}.
     *
     * @param position The position before the move is made.
     * @param from The from square of the pawn.
     * @param symbol The symbol of the promotion piece.
     * @return The promotion piece.
     * @throws IllegalMoveException If this move does not represent a promotion move by a pawn.
     */
    static Piece getPromotionPiece(Position position, long from, char symbol) throws IllegalMoveException {
        Piece piece = position.getPiece(from);
        if (piece != Piece.PAWN) {
            throw new IllegalMoveException("the piece on square " + Square.idToName(from) + " is a " + piece);
        }
        try {
            return Piece.valueOf(symbol);
        } catch (IllegalArgumentException iae) {
            throw new IllegalMoveException("invalid promotion piece '" + symbol + "'");
        }
    }

    /**
     * Returns {@code true} if the move from {@code from} to {@code to} is a castling move.
     */
    static boolean isCastling(Position position, long from, long to) {
        return (position.getPiece(from) == Piece.KING) &&
                (((from == Square.E1) && (to == Square.G1)) || ((from == Square.E1) && (to == Square.C1)) ||
                 ((from == Square.E8) && (to == Square.G8)) || ((from == Square.E8) && (to == Square.C8)));
    }

    /**
     * Returns {@code true} if the move from {@code from} to {@code to} is an 'en passant' move.
     */
    static boolean isEnPassant(Position position, long from, long to) {
        return (position.getPiece(from) == Piece.PAWN) && (position.getEnPassantSquare() == to);
    }

    /**
     * Validates the move-to-be in the given {@code position}.
     *
     * @throws IllegalMoveException If validation fails.
     */
    static void validate(Position position, long from, long to, boolean isCastling) throws IllegalMoveException {
        Color color = position.getActiveColor();

        if (position.getPiece(from) == null || position.getColor(from) != color) {
            throw new IllegalMoveException("no " + color.toString().toLowerCase() + " piece on " + Square.idToName(from));
        }
        if (color == position.getColor(to)) {
            throw new IllegalMoveException("illegal capture on " + Square.idToName(to));
        }
        if (position.getPiece(to) == Piece.KING) {
            throw new IllegalMoveException("illegal capture on " + Square.idToName(to));
        }
        if (isCastling) {
            if ((to == Square.G1) || (to == Square.G8)) {
                if (!position.isKingSideCastlingAllowed(color)) {
                    throw new IllegalMoveException("king-side castling not allowed");
                }
            } else {
                if (!position.isQueenSideCastlingAllowed(color)) {
                    throw new IllegalMoveException("queen-side castling not allowed");
                }
            }
        }
    }
}
