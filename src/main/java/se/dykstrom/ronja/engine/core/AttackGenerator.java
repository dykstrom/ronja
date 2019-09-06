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

package se.dykstrom.ronja.engine.core;

import se.dykstrom.ronja.common.model.Color;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.model.Square;

import static se.dykstrom.ronja.common.model.Square.*;

/**
 * A class used to find all squares that are attacked in a given position.
 *
 * @author Johan Dykstrom
 */
public class AttackGenerator extends AbstractGenerator {

    /** The current position. */
    private Position position;

    /** True if we are finding squares attacked by the white pieces. */
    private boolean isWhiteAttack;

    /** Squares occupied by my own pieces. */
    private long friend;

    /** Squares occupied by any piece in any color. */
    private long occupied;

    /**
     * 64 squares
     * 256 possible variations of pieces on a rank
     */
    private final long[][] horizontalAttacks = new long[64][256];

    // ------------------------------------------------------------------------

    public AttackGenerator() {
        initHorizontalAttacks();
    }

    /**
     * Initializes the array of cached horizontal attacks.
     */
    private void initHorizontalAttacks() {
        for (int fromIndex = 0; fromIndex < 64; fromIndex++) {
            int rank = fromIndex / 8;
            long fromSquare = indexToId(fromIndex);

            for (int rank_bits = 0; rank_bits < 256; rank_bits++) {
                // Shift the rank bits to the correct rank, and add the square ID of the
                // current square. Now we have one possible setup of occupied (on the
                // current rank), and we can try to slide the piece east and west from
                // the current square, saving the attacked squares.
                occupied = ((long) rank_bits) << (rank * 8) | fromSquare;
                horizontalAttacks[fromIndex][rank_bits] = createCachedHorizontalAttacks(fromSquare);
            }
        }
    }

    /**
     * Sets up internal state.
     */
    public void setup(Color color, Position position) {
        this.position = position;

        // The side to attack
        isWhiteAttack = color == Color.WHITE;

        // My pieces
        friend = isWhiteAttack ? position.white : position.black;

        // Squares occupied by any piece in any color
        occupied = position.white | position.black;
    }

    // ------------------------------------------------------------------------
    // Methods for finding attacked squares:
    // ------------------------------------------------------------------------

    /**
     * Returns a bitboard representing the squares attacked by {@code color} in
     * the given {@code position}.
     */
    public long getAttackedSquares(Color color, Position position) {
        setup(color, position);

        long squares = 0;

        squares |= getAllBishopAttacks();
        squares |= getAllKingAttacks();
        squares |= getAllKnightAttacks();
        squares |= getAllPawnAttacks();
        squares |= getAllQueenAttacks();
        squares |= getAllRookAttacks();

        return squares;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns a bitboard of all squares attacked by my king.
     */
    long getAllKingAttacks() {
        return KING_MOVES[Square.idToIndex(position.king & friend)];
    }

    /**
     * Returns a bitboard of all squares attacked by all my knights.
     */
    long getAllKnightAttacks() {
        int numberOfKnights = Square.bitboardToIndices(position.knight & friend);

        long squares = 0;
        for (int knightIndex = 0; knightIndex < numberOfKnights; knightIndex++) {
            int fromIndex = SQUARE_INDICES[knightIndex];
            squares |= KNIGHT_MOVES[fromIndex];
        }
        return squares;
    }

    /**
     * Returns a bitboard of all squares attacked by all my pawns.
     */
    long getAllPawnAttacks() {
        long squares = position.pawn & friend;

        if (isWhiteAttack) {
            return Square.northWest(squares & NOT_WEST_BORDER) | Square.northEast(squares & NOT_EAST_BORDER);
        } else {
            return Square.southWest(squares & NOT_WEST_BORDER) | Square.southEast(squares & NOT_EAST_BORDER);
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Returns a bitboard of all squares attacked by all my rooks.
     */
    long getAllRookAttacks() {
        int count = Square.bitboardToIndices(position.rook & friend);

        long squares = 0;
        for (int i = 0; i < count; i++) {
            int fromIndex = SQUARE_INDICES[i];
            long fromSquare = indexToId(fromIndex);
            squares |= getCachedHorizontalAttacks(fromIndex);
            squares |= getVerticalAttacks(fromSquare);
        }
        return squares;
    }

    /**
     * Returns a bitboard of all squares attacked by all my bishops.
     */
    long getAllBishopAttacks() {
        int count = Square.bitboardToIds(position.bishop & friend);

        long squares = 0;
        for (int i = 0; i < count; i++) {
            squares |= getDiagonalAttacks(SQUARE_IDS[i]);
        }
        return squares;
    }

    /**
     * Returns a bitboard of all squares attacked by all my queens.
     */
    long getAllQueenAttacks() {
        int count = Square.bitboardToIndices(position.queen & friend);

        long squares = 0;
        for (int i = 0; i < count; i++) {
            int fromIndex = SQUARE_INDICES[i];
            long fromSquare = indexToId(fromIndex);
            squares |= getCachedHorizontalAttacks(fromIndex);
            squares |= getVerticalAttacks(fromSquare);
            squares |= getDiagonalAttacks(fromSquare);
        }
        return squares;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns a bitboard of squares that a sliding piece on square {@code fromIndex}
     * can attack horizontally.
     */
    private long getCachedHorizontalAttacks(int fromIndex) {
        int rank = fromIndex / 8;   // Board.getRank() returns rank 1-8
        int rank_bits = (int) (occupied >> (rank * 8)) & 0b11111111;
        return horizontalAttacks[fromIndex][rank_bits];
    }

    /**
     * Creates a bitboard of squares that a sliding piece on square {@code from} can attack
     * horizontally. This method assumes that bitboard {@link #occupied} has been initialized
     * correctly, like it would when actually playing the game.
     */
    private long createCachedHorizontalAttacks(long from) {
        boolean inEastBorder = (EAST_BORDER & from) != 0;
        boolean inWestBorder = (WEST_BORDER & from) != 0;

        long res = 0;

        // East
        if (!inEastBorder) {
            long stop = EAST_BORDER | occupied;
            long pos = east(from);
            while ((pos & stop) == 0) {
                res |= pos;
                pos = east(pos);
            }
            res |= pos;
        }

        // West
        if (!inWestBorder) {
            long stop = WEST_BORDER | occupied;
            long pos = west(from);
            while ((pos & stop) == 0) {
                res |= pos;
                pos = west(pos);
            }
            res |= pos;
        }

        return res;
    }

    /**
     * Returns a bitboard of squares that a sliding piece on square {@code from}
     * can attack vertically.
     */
    private long getVerticalAttacks(long from) {
        boolean inNorthBorder = (NORTH_BORDER & from) != 0;
        boolean inSouthBorder = (SOUTH_BORDER & from) != 0;

        long res = 0;

        // North
        if (!inNorthBorder) {
            long stop = NORTH_BORDER | occupied;
            long pos = north(from);
            while ((pos & stop) == 0) {
                res |= pos;
                pos = north(pos);
            }
            res |= pos;
        }

        // South
        if (!inSouthBorder) {
            long stop = SOUTH_BORDER | occupied;
            long pos = south(from);
            while ((pos & stop) == 0) {
                res |= pos;
                pos = south(pos);
            }
            res |= pos;
        }

        return res;
    }

    /**
     * Returns a bitboard of squares that a piece on square {@code from} can attack diagonally.
     *
     * The following things are checked:
     *
     * - The move does not cross a border.
     */
    private long getDiagonalAttacks(long from) {
        boolean inNorthBorder = (NORTH_BORDER & from) != 0;
        boolean inEastBorder = (EAST_BORDER & from) != 0;
        boolean inSouthBorder = (SOUTH_BORDER & from) != 0;
        boolean inWestBorder = (WEST_BORDER & from) != 0;

        long res = 0;

        long stop;
        long pos;

        // NE
        if (!inNorthBorder && !inEastBorder) {
            stop = NORTH_BORDER | EAST_BORDER | occupied;
            pos = Square.northEast(from);
            while ((pos & stop) == 0) {
                res |= pos;
                pos = Square.northEast(pos);
            }
            res |= pos;
        }

        // SE
        if (!inSouthBorder && !inEastBorder) {
            stop = SOUTH_BORDER | EAST_BORDER | occupied;
            pos = Square.southEast(from);
            while ((pos & stop) == 0) {
                res |= pos;
                pos = Square.southEast(pos);
            }
            res |= pos;
        }

        // SW
        if (!inSouthBorder && !inWestBorder) {
            stop = SOUTH_BORDER | WEST_BORDER | occupied;
            pos = Square.southWest(from);
            while ((pos & stop) == 0) {
                res |= pos;
                pos = Square.southWest(pos);
            }
            res |= pos;
        }

        // NW
        if (!inNorthBorder && !inWestBorder) {
            stop = NORTH_BORDER | WEST_BORDER | occupied;
            pos = Square.northWest(from);
            while ((pos & stop) == 0) {
                res |= pos;
                pos = Square.northWest(pos);
            }
            res |= pos;
        }

        return res;
    }
}
