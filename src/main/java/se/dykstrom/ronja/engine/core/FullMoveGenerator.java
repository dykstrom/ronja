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

import static se.dykstrom.ronja.common.model.Piece.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import se.dykstrom.ronja.common.model.*;

/**
 * A class used to generate all possible pseudo moves for a certain position. A
 * pseudo move is any possible move, including moves that put the moving side's
 * king in check, and moves that capture the other side's king. Pseudo moves
 * that are not legal moves, are removed by getting a very bad score in the
 * Evaluator.
 *
 * @author Johan Dykstrom
 */
public class FullMoveGenerator extends AbstractGenerator implements MoveGenerator {

    /** The current position. */
    private Position position;

    private boolean isWhiteMove;

    private long occupied;

    /** Squares occupied by my own pieces. */
    private long friend;

    /** Squares occupied by the enemy's pieces. */
    private long foe;

    /** Squares attacked by the enemy's pieces. */
    private long attacked;

    /** The 'en passant' target square, or 0 if 'en passant' is not allowed. */
    private long enPassant;

    // ------------------------------------------------------------------------

    /**
     * Sets up internal state.
     */
    public void setup(Position position) {
        this.position = position;

        // Squares occupied by any piece in any color
        occupied = position.white | position.black;

        // The side to move
        isWhiteMove = position.getActiveColor() == Color.WHITE;

        // My pieces, and the enemy's pieces
        if (isWhiteMove) {
            friend = position.white;
            foe = position.black;
            attacked = position.blackAttack;
        } else {
            friend = position.black;
            foe = position.white;
            attacked = position.whiteAttack;
        }

        // The 'en passant' target square, if any
        enPassant = position.getEnPassantSquare();
    }

    // ------------------------------------------------------------------------
    // Methods for finding possible moves:
    // ------------------------------------------------------------------------

    @Override
    public Iterator<Integer> iterator(Position position) {
        return new Itr(position);
    }

    /**
     * Returns the list of all possible pseudo moves for this position, including moves that put my king in check.
     *
     * @param position The position to generate moves for.
     * @return A list of moves found.
     */
    public List<Integer> getMoves(Position position) {
        setup(position);

        List<Integer> moves = new ArrayList<>();

        moves.addAll(getAllBishopMoves());
        moves.addAll(getAllKingMoves());
        moves.addAll(getAllKnightMoves());
        moves.addAll(getAllPawnMoves());
        moves.addAll(getAllQueenMoves());
        moves.addAll(getAllRookMoves());

        return moves;
    }

    /**
     * Returns a list of all squares that a king on the square with index {@code fromIndex} can move to.
     */
    public List<Long> getNormalKingMoves(int fromIndex) {
        // Get all possible to squares
        long to = KING_MOVES[fromIndex];

        // TODO: Would it be better if the lookup in KING_MOVES and KNIGHT_MOVES returned an array of 'to' squares?
        //
        // Implementation suggestion:
        //
        //        long[] tos = KING_MOVES[fromIndex];
        //        List<Long> legalTos = new ArrayList<>(tos.length);
        //        for (int i = 0; i < tos.length; i++) {
        //            if ((tos[i] & ~friend & ~attacked) != 0) {
        //                legalTos.add(tos[i]);
        //            }
        //        }
        //        return legalTos;

        // Filter out those that are legal to move to
        long legal = to & ~friend & ~attacked;

        // Convert to list of to squares
        return Square.bitboardToIds(legal);
    }

    /**
     * Returns a list of all squares that a king on square {@code from} can move to by castling.
     *
     * The following things are checked:
     *
     * - Castling is allowed.
     * - The squares that the king moves across are empty and not checked.
     * - The squares that the rook moves across are empty.
     */
    public List<Long> getCastlingKingMoves(long from) {
        List<Long> res = new ArrayList<>();

        // White castling
        if (from == Square.E1) {
            if (position.isKingSideCastlingAllowed(Color.WHITE) && isRookOnStartSquare(Square.H1) &&
                isPossibleToCastle(Square.F1 | Square.G1, Square.E1 | Square.F1 | Square.G1)) {
                res.add(Square.G1);
            }
            if (position.isQueenSideCastlingAllowed(Color.WHITE) && isRookOnStartSquare(Square.A1) &&
                isPossibleToCastle(Square.B1 | Square.C1 | Square.D1, Square.C1 | Square.D1 | Square.E1)) {
                res.add(Square.C1);
            }
        }

        // Black castling
        else if (from == Square.E8) {
            if (position.isKingSideCastlingAllowed(Color.BLACK) && isRookOnStartSquare(Square.H8) &&
                isPossibleToCastle(Square.F8 | Square.G8, Square.E8 | Square.F8 | Square.G8)) {
                res.add(Square.G8);
            }
            if (position.isQueenSideCastlingAllowed(Color.BLACK) && isRookOnStartSquare(Square.A8) &&
                isPossibleToCastle(Square.B8 | Square.C8 | Square.D8, Square.C8 | Square.D8 | Square.E8)) {
                res.add(Square.C8);
            }
        }

        return res;
    }

    /**
     * Returns a list of all possible king moves for the side to move in the given position.
     */
    public List<Integer> getAllKingMoves() {
        List<Integer> moves = new ArrayList<>();

        // There is only one king
        long fromSquare = position.king & friend;
        int fromIndex = Square.idToIndex(fromSquare);

        List<Long> toSquares = getNormalKingMoves(fromIndex);
        for (Long toSquare : toSquares) {
            if (isCapture(toSquare)) {
                moves.add(Move.createCapture(KING, fromSquare, toSquare, position.getPiece(toSquare)));
            } else {
                moves.add(Move.create(KING, fromSquare, toSquare));
            }
        }

        toSquares = getCastlingKingMoves(fromSquare);
        for (Long toSquare : toSquares) {
            moves.add(Move.createCastling(fromSquare, toSquare));
        }

        return moves;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns a list of all squares that a knight on the square with index {@code fromIndex} can move to.
     *
     * The following things are checked:
     *
     * - The destination square is not blocked by a piece of my color.
     * - The move does not cross a border.
     */
    public List<Long> getKnightMoves(int fromIndex) {
        // Get all possible 'to' squares as a bitboard
        long to = KNIGHT_MOVES[fromIndex];

        // Filter out those that are legal to move to
        long legal = to & ~friend;

        // Convert to list of 'to' squares
        return Square.bitboardToIds(legal);
    }

    /**
     * Returns a list of all possible knight moves for the side to move in the given position.
     */
    public List<Integer> getAllKnightMoves() {
        List<Integer> moves = new ArrayList<>();

        // Find all my knights
        List<Integer> fromIndices = Square.bitboardToIndices(position.knight & friend);

        // For each knight, find all of its 'to' squares
        for (Integer fromIndex : fromIndices) {
            long fromSquare = Square.indexToId(fromIndex);
            List<Long> toSquares = getKnightMoves(fromIndex);
            for (Long toSquare : toSquares) {
                if (isCapture(toSquare)) {
                    moves.add(Move.createCapture(KNIGHT, fromSquare, toSquare, position.getPiece(toSquare)));
                } else {
                    moves.add(Move.create(KNIGHT, fromSquare, toSquare));
                }
            }
        }

        return moves;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns a list of all squares that a pawn on square {@code from} can move to,
     * except 'en passant' moves.
     *
     * The following things are checked:
     *
     * - For straight moves: The destination square is not blocked by a piece (of any color).
     * - For captures: The destination square contains an enemy piece.
     * - The origin square is the start position for two square moves.
     * - The move does not cross a border.
     */
    public List<Long> getNormalPawnMoves(long from) {
        boolean inEastBorder = (EAST_BORDER & from) != 0;
        boolean inWestBorder = (WEST_BORDER & from) != 0;

        // If the pawn is in start position
        boolean inStartPosition = isWhiteMove ? ((from & Board.RANK_2) != 0) : ((from & Board.RANK_7) != 0);

        List<Long> res = new ArrayList<>();

        // If white to move
        if (isWhiteMove) {
            // Straight moves
            if (isEmpty(Square.north(from))) {
                res.add(Square.north(from));
                if (inStartPosition && isEmpty(Square.northNorth(from))) {
                    res.add(Square.northNorth(from));
                }
            }

            // Captures
            if (!inWestBorder) {
                if (isCapture(Square.northWest(from))) {
                    res.add(Square.northWest(from));
                }
            }
            if (!inEastBorder) {
                if (isCapture(Square.northEast(from))) {
                    res.add(Square.northEast(from));
                }
            }
        }

        // Black to move
        else {
            // Straight moves
            if (isEmpty(Square.south(from))) {
                res.add(Square.south(from));
                if (inStartPosition && isEmpty(Square.southSouth(from))) {
                    res.add(Square.southSouth(from));
                }
            }

            // Captures
            if (!inWestBorder) {
                if (isCapture(Square.southWest(from))) {
                    res.add(Square.southWest(from));
                }
            }
            if (!inEastBorder) {
                if (isCapture(Square.southEast(from))) {
                    res.add(Square.southEast(from));
                }
            }
        }

        return res;
    }

    /**
     * Returns a list of all squares that a pawn on square {@code from} can move to with an 'en passant' move.
     * This list either contains one move, or no moves at all.
     *
     * The following things are checked:
     *
     * - The destination square is a possible e.p. square.
     */
    public List<Long> getEnPassantPawnMoves(long from) {
        if (isWhiteMove) {
            // If 'en passant' target square is on 6th rank, and pawn is on 5th rank
            if (((enPassant & Board.RANK_6) != 0) && ((from & Board.RANK_5) != 0)) {
                int fromFile = Board.getFile(from);
                int enPassantFile = Board.getFile(enPassant);
                if ((fromFile == (enPassantFile + 1)) || (fromFile == (enPassantFile - 1))) {
                    return Collections.singletonList(enPassant);
                }
            }
        } else {
            // If 'en passant' target square is on 3rd rank, and pawn is on 4th rank
            if (((enPassant & Board.RANK_3) != 0) && ((from & Board.RANK_4) != 0)) {
                int fromFile = Board.getFile(from);
                int enPassantFile = Board.getFile(enPassant);
                if ((fromFile == (enPassantFile + 1)) || (fromFile == (enPassantFile - 1))) {
                    return Collections.singletonList(enPassant);
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * Returns a list of all possible pawn moves for the side to move in the given position.
     */
    public List<Integer> getAllPawnMoves() {
        List<Integer> moves = new ArrayList<>();

        // Find all my pawns
        List<Long> fromSquares = Square.bitboardToIds(position.pawn & friend);

        // For each pawn, find all of its 'to' squares
        for (Long fromSquare : fromSquares) {
            List<Long> toSquares = getNormalPawnMoves(fromSquare);
            for (Long toSquare : toSquares) {
                if ((toSquare & (Board.RANK_1 | Board.RANK_8)) != 0) {
                    if (isCapture(toSquare)) {
                        int captured = position.getPiece(toSquare);
                        moves.add(Move.createCapturePromotion(fromSquare, toSquare, captured, BISHOP));
                        moves.add(Move.createCapturePromotion(fromSquare, toSquare, captured, KNIGHT));
                        moves.add(Move.createCapturePromotion(fromSquare, toSquare, captured, QUEEN));
                        moves.add(Move.createCapturePromotion(fromSquare, toSquare, captured, ROOK));
                    } else {
                        moves.add(Move.createPromotion(fromSquare, toSquare, BISHOP));
                        moves.add(Move.createPromotion(fromSquare, toSquare, KNIGHT));
                        moves.add(Move.createPromotion(fromSquare, toSquare, QUEEN));
                        moves.add(Move.createPromotion(fromSquare, toSquare, ROOK));
                    }
                } else {
                    if (isCapture(toSquare)) {
                        moves.add(Move.createCapture(PAWN, fromSquare, toSquare, position.getPiece(toSquare)));
                    } else {
                        moves.add(Move.create(PAWN, fromSquare, toSquare));
                    }
                }
            }

            toSquares = getEnPassantPawnMoves(fromSquare);
            for (Long toSquare : toSquares) {
                moves.add(Move.createEnPassant(fromSquare, toSquare));
            }
        }

        return moves;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns a list of all possible bishop moves for the side to move in the given position.
     */
    public List<Integer> getAllBishopMoves() {
        List<Integer> moves = new ArrayList<>();

        // Find all my bishops
        List<Long> fromSquares = Square.bitboardToIds(position.bishop & friend);

        // For each bishop, find all of its 'to' squares
        for (Long fromSquare : fromSquares) {
            List<Long> toSquares = getDiagonalMoves(fromSquare);
            for (Long toSquare : toSquares) {
                if (isCapture(toSquare)) {
                    moves.add(Move.createCapture(BISHOP, fromSquare, toSquare, position.getPiece(toSquare)));
                } else {
                    moves.add(Move.create(BISHOP, fromSquare, toSquare));
                }
            }
        }

        return moves;
    }

    /**
     * Returns a list of all possible queen moves for the side to move in the given position.
     */
    public List<Integer> getAllQueenMoves() {
        List<Integer> moves = new ArrayList<>();

        // Find all my queens
        List<Long> fromSquares = Square.bitboardToIds(position.queen & friend);

        // For each queen, find all of its 'to' squares
        for (Long fromSquare : fromSquares) {
            List<Long> toSquares = getStraightMoves(fromSquare);
            toSquares.addAll(getDiagonalMoves(fromSquare));
            for (Long toSquare : toSquares) {
                if (isCapture(toSquare)) {
                    moves.add(Move.createCapture(QUEEN, fromSquare, toSquare, position.getPiece(toSquare)));
                } else {
                    moves.add(Move.create(QUEEN, fromSquare, toSquare));
                }
            }
        }

        return moves;
    }

    /**
     * Returns a list of all possible rook moves for the side to move in the given position.
     */
    public List<Integer> getAllRookMoves() {
        List<Integer> moves = new ArrayList<>();

        // Find all my rooks
        List<Long> fromSquares = Square.bitboardToIds(position.rook & friend);

        // For each rook, find all of its 'to' squares
        for (Long fromSquare : fromSquares) {
            List<Long> toSquares = getStraightMoves(fromSquare);
            for (Long toSquare : toSquares) {
                if (isCapture(toSquare)) {
                    moves.add(Move.createCapture(ROOK, fromSquare, toSquare, position.getPiece(toSquare)));
                } else {
                    moves.add(Move.create(ROOK, fromSquare, toSquare));
                }
            }
        }

        return moves;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the list of all squares that a piece on square {@code from} can move to in a straight line.
     *
     * The following things are checked:
     *
     * - The destination square is not blocked by a piece of my color.
     * - The move does not cross a border.
     */
    public List<Long> getStraightMoves(long from) {
        boolean inNorthBorder = (NORTH_BORDER & from) != 0;
        boolean inEastBorder = (EAST_BORDER & from) != 0;
        boolean inSouthBorder = (SOUTH_BORDER & from) != 0;
        boolean inWestBorder = (WEST_BORDER & from) != 0;

        List<Long> res = new ArrayList<>();

        long stop;
        long pos;

        // North
        if (!inNorthBorder) {
            stop = NORTH_BORDER | occupied;
            pos = Square.north(from);
            while ((pos & stop) == 0) {
                res.add(pos);
                pos = Square.north(pos);
            }
            if (!isBlocked(pos))
                res.add(pos);
        }

        // East
        if (!inEastBorder) {
            stop = EAST_BORDER | occupied;
            pos = Square.east(from);
            while ((pos & stop) == 0) {
                res.add(pos);
                pos = Square.east(pos);
            }
            if (!isBlocked(pos))
                res.add(pos);
        }

        // South
        if (!inSouthBorder) {
            stop = SOUTH_BORDER | occupied;
            pos = Square.south(from);
            while ((pos & stop) == 0) {
                res.add(pos);
                pos = Square.south(pos);
            }
            if (!isBlocked(pos))
                res.add(pos);
        }

        // West
        if (!inWestBorder) {
            stop = WEST_BORDER | occupied;
            pos = Square.west(from);
            while ((pos & stop) == 0) {
                res.add(pos);
                pos = Square.west(pos);
            }
            if (!isBlocked(pos))
                res.add(pos);
        }

        return res;
    }

    /**
     * Returns the list of all squares that a piece on square {@code from} can move to diagonally.
     *
     * The following things are checked:
     *
     * - The destination square is not blocked by a piece of my color.
     * - The move does not cross a border.
     */
    public List<Long> getDiagonalMoves(long from) {
        boolean inNorthBorder = (NORTH_BORDER & from) != 0;
        boolean inEastBorder = (EAST_BORDER & from) != 0;
        boolean inSouthBorder = (SOUTH_BORDER & from) != 0;
        boolean inWestBorder = (WEST_BORDER & from) != 0;

        List<Long> res = new ArrayList<>();

        long stop;
        long pos;

        // NE
        if (!inNorthBorder && !inEastBorder) {
            stop = NORTH_BORDER | EAST_BORDER | occupied;
            pos = Square.northEast(from);
            while ((pos & stop) == 0) {
                res.add(pos);
                pos = Square.northEast(pos);
            }
            if (!isBlocked(pos)) {
                res.add(pos);
            }
        }

        // SE
        if (!inSouthBorder && !inEastBorder) {
            stop = SOUTH_BORDER | EAST_BORDER | occupied;
            pos = Square.southEast(from);
            while ((pos & stop) == 0) {
                res.add(pos);
                pos = Square.southEast(pos);
            }
            if (!isBlocked(pos))
                res.add(pos);
        }

        // SW
        if (!inSouthBorder && !inWestBorder) {
            stop = SOUTH_BORDER | WEST_BORDER | occupied;
            pos = Square.southWest(from);
            while ((pos & stop) == 0) {
                res.add(pos);
                pos = Square.southWest(pos);
            }
            if (!isBlocked(pos))
                res.add(pos);
        }

        // NW
        if (!inNorthBorder && !inWestBorder) {
            stop = NORTH_BORDER | WEST_BORDER | occupied;
            pos = Square.northWest(from);
            while ((pos & stop) == 0) {
                res.add(pos);
                pos = Square.northWest(pos);
            }
            if (!isBlocked(pos))
                res.add(pos);
        }

        return res;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns {@code true} if {@code square} is blocked, that is, is occupied by one of my own pieces.
     */
    private boolean isBlocked(long square) {
        return (square & friend) != 0;
    }

    /**
     * Returns {@code true} if moving to {@code square} is a capture, that is,
     * if {@code square} is occupied by one of the opponent's pieces.
     */
    private boolean isCapture(long square) {
        return (square & foe) != 0;
    }

    /**
     * Returns {@code true} if all squares in {@code bitboard} are empty.
     */
    private boolean isEmpty(long bitboard) {
        return (bitboard & occupied) == 0;
    }

    /**
     * Returns {@code true} if any of the squares in {@code bitboard} is attacked by an enemy piece.
     */
    private boolean isAttacked(long bitboard) {
        return (bitboard & attacked) != 0;
    }

    /**
     * Returns {@code true} if it is possible to castle. Castling is possible if all squares
     * in {@code emptySquares} are empty and all squares in {@code safeSquares} are safe,
     * that is, they are not attacked.
     */
    private boolean isPossibleToCastle(long emptySquares, long safeSquares) {
        return isEmpty(emptySquares) && !isAttacked(safeSquares);
    }

    /**
     * Returns {@code true} if the rook is still on its start square, given by {@code square}.
     *
     * TODO: Move this method to class Position and call it from the isXSideCastlingAllowed methods.
     */
    private boolean isRookOnStartSquare(long square) {
        return (position.rook & friend & square) != 0;
    }

    // ------------------------------------------------------------------------

    /**
     * A move iterator that pre-generates the full list of moves when created.
     */
    private class Itr implements Iterator<Integer> {

        /** Internal iterator used to iterate over all pre-generated moves. */
        private final Iterator<Integer> iterator;

        public Itr(Position position) {
            this.iterator = getMoves(position).iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Integer next() {
            return iterator.next();
        }
    }
}
