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

import java.util.List;

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

    // ------------------------------------------------------------------------

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
    public long getAllKingAttacks() {
        return KING_MOVES[Square.idToIndex(position.king & friend)];
    }

    /**
     * Returns a bitboard of all squares attacked by all my knights.
     */
    public long getAllKnightAttacks() {
        List<Integer> fromIndices = Square.bitboardToIndices(position.knight & friend);

        long squares = 0;
        for (Integer fromIndex : fromIndices) {
            squares |= KNIGHT_MOVES[fromIndex];
        }
        return squares;
    }

    /**
     * Returns a bitboard of all squares attacked by all my pawns.
     */
    public long getAllPawnAttacks() {
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
    public long getAllRookAttacks() {
        List<Long> fromSquares = Square.bitboardToIds(position.rook & friend);

        long squares = 0;
        for (Long fromSquare : fromSquares) {
            squares |= getStraightAttacks(fromSquare);
        }
        return squares;
    }

    /**
     * Returns a bitboard of all squares attacked by all my bishops.
     */
    public long getAllBishopAttacks() {
        List<Long> fromSquares = Square.bitboardToIds(position.bishop & friend);

        long squares = 0;
        for (Long fromSquare : fromSquares) {
            squares |= getDiagonalAttacks(fromSquare);
        }
        return squares;
    }

    /**
     * Returns a bitboard of all squares attacked by all my queens.
     */
    public long getAllQueenAttacks() {
        List<Long> fromSquares = Square.bitboardToIds(position.queen & friend);

        long squares = 0;
        for (Long fromSquare : fromSquares) {
            squares |= getStraightAttacks(fromSquare);
            squares |= getDiagonalAttacks(fromSquare);
        }
        return squares;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns a bitboard of squares that a piece on square {@code from} can attack in a straight line.
     *
     * The following things are checked:
     *
     * - The move does not cross a border.
     */
    public long getStraightAttacks(long from) {
        boolean inNorthBorder = (NORTH_BORDER & from) != 0;
        boolean inEastBorder = (EAST_BORDER & from) != 0;
        boolean inSouthBorder = (SOUTH_BORDER & from) != 0;
        boolean inWestBorder = (WEST_BORDER & from) != 0;

        long res = 0;

        long stop;
        long pos;

        // North
        if (!inNorthBorder) {
            stop = NORTH_BORDER | occupied;
            pos = Square.north(from);
            while ((pos & stop) == 0) {
                res |= pos;
                pos = Square.north(pos);
            }
            res |= pos;
        }

        // East
        if (!inEastBorder) {
            stop = EAST_BORDER | occupied;
            pos = Square.east(from);
            while ((pos & stop) == 0) {
                res |= pos;
                pos = Square.east(pos);
            }
            res |= pos;
        }

        // South
        if (!inSouthBorder) {
            stop = SOUTH_BORDER | occupied;
            pos = Square.south(from);
            while ((pos & stop) == 0) {
                res |= pos;
                pos = Square.south(pos);
            }
            res |= pos;
        }

        // West
        if (!inWestBorder) {
            stop = WEST_BORDER | occupied;
            pos = Square.west(from);
            while ((pos & stop) == 0) {
                res |= pos;
                pos = Square.west(pos);
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
    public long getDiagonalAttacks(long from) {
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
