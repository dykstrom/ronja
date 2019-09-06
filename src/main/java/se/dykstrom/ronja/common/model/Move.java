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

import static se.dykstrom.ronja.common.model.Square.indexToId;

/**
 * The Move class provides functions for creating and manipulating a single move.
 * The actual move is represented by an integer.
 *
 * The bits of the integer Move type are organized like this:
 *
 * 00-05 - to square
 * 06-11 - from square
 * 12-14 - moved piece
 * 15    - castle flag
 * 16    - en passant flag
 * 17-19 - promoted piece
 * 20-22 - captured piece
 *
 * Note that the bits must be stored in this order, because the move sorting depends
 * on more promising moves like captures being greater than less promising moves.
 *
 * @author Johan Dykstrom
 */
public class Move {

    private static final int PIECE_MASK = 0x07;
    private static final int SQUARE_MASK = 0x3f;
    private static final int CASTLE_MASK = 0x01 << 15;
    private static final int ENPASSANT_MASK = 0x01 << 16;

    private static final int FROM_OFFSET = 6;
    private static final int MOVED_OFFSET = 12;
    private static final int PROMOTED_OFFSET = 17;
    private static final int CAPTURED_OFFSET = 20;

    private static final int CAPTURED_PIECE_MASK = PIECE_MASK << CAPTURED_OFFSET;
    private static final int PROMOTED_PIECE_MASK = PIECE_MASK << PROMOTED_OFFSET;

    private static final int MOVED_KING = Piece.KING << MOVED_OFFSET;
    private static final int MOVED_PAWN = Piece.PAWN << MOVED_OFFSET;

    /**
     * Creates a new move with the given piece moving from square from to square to.
     */
    public static int create(int piece, int from, int to) {
        return (piece << MOVED_OFFSET) | (from << FROM_OFFSET) | to;
    }

    /**
     * Creates a new move with the given piece moving from square from to square to,
     * capturing another piece.
     */
    public static int createCapture(int piece, int from, int to, int captured) {
        return (piece << MOVED_OFFSET) | (from << FROM_OFFSET) | to | (captured << CAPTURED_OFFSET);
    }

    /**
     * Creates a new pawn move from square from to square to, promoting to another piece.
     */
    public static int createPromotion(int from, int to, int promoted) {
        return MOVED_PAWN | (from << FROM_OFFSET) | to | (promoted << PROMOTED_OFFSET);
    }

    /**
     * Creates a new pawn move from square from to square to, capturing another piece, 
     * and promoting to yet another piece.
     */
    public static int createCapturePromotion(int from, int to, int captured, int promoted) {
        return MOVED_PAWN | (from << FROM_OFFSET) | to | (captured << CAPTURED_OFFSET) | (promoted << PROMOTED_OFFSET);
    }

    /**
     * Creates a new 'en passant' move with the pawn moving from square from to square to,
     * capturing the another pawn.
     */
    public static int createEnPassant(int from, int to) {
        return MOVED_PAWN | (from << FROM_OFFSET) | to | (Piece.PAWN << CAPTURED_OFFSET) | ENPASSANT_MASK;
    }

    /**
     * Creates a new castling move with the king moving from square from to square to.
     */
    public static int createCastling(int from, int to) {
        return MOVED_KING | (from << FROM_OFFSET) | to | CASTLE_MASK;
    }

    /**
     * Returns the piece that moved.
     */
    public static int getPiece(int move) {
        return (move >> MOVED_OFFSET) & PIECE_MASK;
    }
    
    /**
     * Returns the ID of the square moved from.
     */
    public static long getFrom(int move) {
        return indexToId((move >> FROM_OFFSET) & SQUARE_MASK);
    }
    
    /**
     * Returns the ID of the square moved to.
     */
    public static long getTo(int move) {
        return indexToId(move & SQUARE_MASK);
    }

    /**
     * Returns the captured piece, or 0 if no piece captured.
     */
    public static int getCaptured(int move) {
        return (move >> CAPTURED_OFFSET) & PIECE_MASK;
    }

    /**
     * Returns the promoted piece, or 0 if no piece promoted.
     */
    public static int getPromoted(int move) {
        return (move >> PROMOTED_OFFSET) & PIECE_MASK;
    }
    
    /**
     * Returns true if the given move is a capture move.
     */
    public static boolean isCapture(int move) {
        return (move & CAPTURED_PIECE_MASK) != 0;
    }
    
    /**
     * Returns true if the given move is a promotion move.
     */
    public static boolean isPromotion(int move) {
        return (move & PROMOTED_PIECE_MASK) != 0;
    }
    
    /**
     * Returns true if the given move is a castling move.
     */
    public static boolean isCastling(int move) {
        return (move & CASTLE_MASK) != 0;
    }
    
    /**
     * Returns true if the given move is an 'en passant' move.
     */
    public static boolean isEnPassant(int move) {
        return (move & ENPASSANT_MASK) != 0;
    }
}
