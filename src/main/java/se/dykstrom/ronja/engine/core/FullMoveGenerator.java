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

import se.dykstrom.ronja.common.model.*;

import java.util.List;

import static se.dykstrom.ronja.common.model.Piece.*;
import static se.dykstrom.ronja.common.model.Square.MAX_SQUARES;
import static se.dykstrom.ronja.common.model.Square.SQUARE_IDS;

/**
 * A class used to generate all possible pseudo moves for a certain position. A
 * pseudo move is any possible move, including moves that put the moving side's
 * king in check, and moves that capture the other side's king. Pseudo moves
 * that are not legal moves will be removed by getting a very bad score in the
 * Evaluator.
 *
 * @author Johan Dykstrom
 */
public class FullMoveGenerator extends AbstractGenerator {

    private static final int MAX_POSITIONS = 50;
    private static final int MAX_MOVES = 200;
    
    public final int[][] moves = new int[MAX_POSITIONS][MAX_MOVES];
    private int positionIndex = 0;
    private int moveIndex = 0;

    /** Local array for storing square IDs. */
    private final long[] squareIds = new long[MAX_SQUARES];

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
    public void setup(Position position, int depth) {
        this.position = position;
        this.positionIndex = depth;
        this.moveIndex = 0;

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

    /**
     * Returns the current move index.
     */
    public int getMoveIndex() {
        return moveIndex;
    }

    /**
     * Generates all possible pseudo moves for this position, including moves that put my king in check,
     * and returns the number of moves generated.
     *
     * @param position The position to generate moves for.
     * @param depth The current depth in the search tree.
     * @return The 'move index' = number of moves generated.
     */
    public int generateMoves(Position position, int depth) {
        setup(position, depth);

        generateBishopMoves();
        generateKingMoves();
        generateKnightMoves();
        generatePawnMoves();
        generateQueenMoves();
        generateRookMoves();

        return moveIndex;
    }

    /**
     * Populates the array {@link Square#SQUARE_IDS} with all squares that a king on the square
     * with index {@code fromIndex} can move to. Returns the number of such squares found.
     */
    private int getNormalKingMoves(int fromIndex) {
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
     * Populates the array {@link Square#SQUARE_IDS} with all squares that a king on the square
     * {@code from} can move to <i>by castling</i>. Returns the number of such squares found.
     *
     * The following things are checked:
     *
     * - Castling is allowed.
     * - The squares that the king moves across are empty and not checked.
     * - The squares that the rook moves across are empty.
     */
    private int getCastlingKingMoves(long from) {
        int count = 0;

        // White castling
        if (from == Square.E1) {
            if (position.isKingSideCastlingAllowed(Color.WHITE) && isRookOnStartSquare(Square.H1) &&
                isPossibleToCastle(Square.F1 | Square.G1, Square.E1 | Square.F1 | Square.G1)) {
                SQUARE_IDS[count++] = Square.G1;
            }
            if (position.isQueenSideCastlingAllowed(Color.WHITE) && isRookOnStartSquare(Square.A1) &&
                isPossibleToCastle(Square.B1 | Square.C1 | Square.D1, Square.C1 | Square.D1 | Square.E1)) {
                SQUARE_IDS[count++] = Square.C1;
            }
        }

        // Black castling
        else if (from == Square.E8) {
            if (position.isKingSideCastlingAllowed(Color.BLACK) && isRookOnStartSquare(Square.H8) &&
                isPossibleToCastle(Square.F8 | Square.G8, Square.E8 | Square.F8 | Square.G8)) {
                SQUARE_IDS[count++] = Square.G8;
            }
            if (position.isQueenSideCastlingAllowed(Color.BLACK) && isRookOnStartSquare(Square.A8) &&
                isPossibleToCastle(Square.B8 | Square.C8 | Square.D8, Square.C8 | Square.D8 | Square.E8)) {
                SQUARE_IDS[count++] = Square.C8;
            }
        }

        return count;
    }

    /**
     * Generates all possible king moves for the side to move in the given position.
     */
    void generateKingMoves() {
        // There is only one king
        long fromSquare = position.king & friend;
        int fromIndex = Square.idToIndex(fromSquare);

        int count = getNormalKingMoves(fromIndex);
        for (int i = 0; i < count; i++) {
            createAndSaveMove(KING, fromSquare, SQUARE_IDS[i]);
        }

        count = getCastlingKingMoves(fromSquare);
        for (int i = 0; i < count; i++) {
            moves[positionIndex][moveIndex++] = Move.createCastling(fromSquare, SQUARE_IDS[i]);
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Populates the array {@link Square#SQUARE_IDS} with all squares that a knight on the square
     * with index {@code fromIndex} can move to. Returns the number of such squares found.
     *
     * The following things are checked:
     *
     * - The destination square is not blocked by a piece of my color.
     * - The move does not cross a border.
     */
    private int getKnightMoves(int fromIndex) {
        // Get all possible 'to' squares as a bitboard
        long to = KNIGHT_MOVES[fromIndex];

        // Filter out those that are legal to move to
        long legal = to & ~friend;

        // Convert to array of 'to' squares
        return Square.bitboardToIds(legal);
    }

    /**
     * Generates all possible knight moves for the side to move in the given position.
     */
    void generateKnightMoves() {
        // Find all my knights
        List<Integer> fromIndices = Square.bitboardToIndices(position.knight & friend);

        // For each knight, find all of its 'to' squares
        for (Integer fromIndex : fromIndices) {
            long fromSquare = Square.indexToId(fromIndex);
            int count = getKnightMoves(fromIndex);
            for (int i = 0; i < count; i++) {
                long toSquare = SQUARE_IDS[i];
                createAndSaveMove(KNIGHT, fromSquare, toSquare);
            }
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Populates the array {@link #squareIds} with all squares that a pawn on the square
     * {@code from} can move to, except 'en passant' moves. Returns the number of such
     * squares found.
     *
     * The following things are checked:
     *
     * - For straight moves: The destination square is not blocked by a piece (of any color).
     * - For captures: The destination square contains an enemy piece.
     * - The origin square is the start position for two square moves.
     * - The move does not cross a border.
     */
    int getNormalPawnMoves(long from) {
        boolean inEastBorder = (EAST_BORDER & from) != 0;
        boolean inWestBorder = (WEST_BORDER & from) != 0;

        // If the pawn is in start position
        boolean inStartPosition = isWhiteMove ? ((from & Board.RANK_2) != 0) : ((from & Board.RANK_7) != 0);

        int count = 0;

        // If white to move
        if (isWhiteMove) {
            // Straight moves
            if (isEmpty(Square.north(from))) {
                squareIds[count++] = Square.north(from);
                if (inStartPosition && isEmpty(Square.northNorth(from))) {
                    squareIds[count++] = Square.northNorth(from);
                }
            }

            // Captures
            if (!inWestBorder) {
                if (isCapture(Square.northWest(from))) {
                    squareIds[count++] = Square.northWest(from);
                }
            }
            if (!inEastBorder) {
                if (isCapture(Square.northEast(from))) {
                    squareIds[count++] = Square.northEast(from);
                }
            }
        }

        // Black to move
        else {
            // Straight moves
            if (isEmpty(Square.south(from))) {
                squareIds[count++] = Square.south(from);
                if (inStartPosition && isEmpty(Square.southSouth(from))) {
                    squareIds[count++] = Square.southSouth(from);
                }
            }

            // Captures
            if (!inWestBorder) {
                if (isCapture(Square.southWest(from))) {
                    squareIds[count++] = Square.southWest(from);
                }
            }
            if (!inEastBorder) {
                if (isCapture(Square.southEast(from))) {
                    squareIds[count++] = Square.southEast(from);
                }
            }
        }

        return count;
    }

    /**
     * Returns the square that a pawn on square {@code from} can move to with an 'en passant' move.
     * If there is no possible 'en passant' square, this method returns 0.
     *
     * The following things are checked:
     *
     * - The destination square is a possible e.p. square.
     */
    long getEnPassantPawnMove(long from) {
        if (isWhiteMove) {
            // If 'en passant' target square is on 6th rank, and pawn is on 5th rank
            if (((enPassant & Board.RANK_6) != 0) && ((from & Board.RANK_5) != 0)) {
                int fromFile = Board.getFile(from);
                int enPassantFile = Board.getFile(enPassant);
                if ((fromFile == (enPassantFile + 1)) || (fromFile == (enPassantFile - 1))) {
                    return enPassant;
                }
            }
        } else {
            // If 'en passant' target square is on 3rd rank, and pawn is on 4th rank
            if (((enPassant & Board.RANK_3) != 0) && ((from & Board.RANK_4) != 0)) {
                int fromFile = Board.getFile(from);
                int enPassantFile = Board.getFile(enPassant);
                if ((fromFile == (enPassantFile + 1)) || (fromFile == (enPassantFile - 1))) {
                    return enPassant;
                }
            }
        }

        return 0;
    }

    /**
     * Generates all possible pawn moves for the side to move in the given position.
     */
    void generatePawnMoves() {
        // Find the squares of all of my pawns
        int fromSquareCount = Square.bitboardToIds(position.pawn & friend);

        // For each pawn, find all of its 'to' squares
        for (int fromSquareIdx = 0; fromSquareIdx < fromSquareCount; fromSquareIdx++) {
            long fromSquare = SQUARE_IDS[fromSquareIdx];

            int toSquareCount = getNormalPawnMoves(fromSquare);
            for (int toSquareIdx = 0; toSquareIdx < toSquareCount; toSquareIdx++) {
                long toSquare = squareIds[toSquareIdx];
                if ((toSquare & (Board.RANK_1 | Board.RANK_8)) != 0) {
                    if (isCapture(toSquare)) {
                        int captured = position.getPiece(toSquare);
                        moves[positionIndex][moveIndex++] = Move.createCapturePromotion(fromSquare, toSquare, captured, BISHOP);
                        moves[positionIndex][moveIndex++] = Move.createCapturePromotion(fromSquare, toSquare, captured, KNIGHT);
                        moves[positionIndex][moveIndex++] = Move.createCapturePromotion(fromSquare, toSquare, captured, QUEEN);
                        moves[positionIndex][moveIndex++] = Move.createCapturePromotion(fromSquare, toSquare, captured, ROOK);
                    } else {
                        moves[positionIndex][moveIndex++] = Move.createPromotion(fromSquare, toSquare, BISHOP);
                        moves[positionIndex][moveIndex++] = Move.createPromotion(fromSquare, toSquare, KNIGHT);
                        moves[positionIndex][moveIndex++] = Move.createPromotion(fromSquare, toSquare, QUEEN);
                        moves[positionIndex][moveIndex++] = Move.createPromotion(fromSquare, toSquare, ROOK);
                    }
                } else {
                    createAndSaveMove(PAWN, fromSquare, toSquare);
                }
            }

            long toSquare = getEnPassantPawnMove(fromSquare);
            if (toSquare != 0) {
                moves[positionIndex][moveIndex++] = Move.createEnPassant(fromSquare, toSquare);
            }
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Generates all possible bishop moves for the side to move in the given position.
     */
    void generateBishopMoves() {
        // Find all my bishops
        int fromSquareCount = Square.bitboardToIds(position.bishop & friend);

        // For each bishop, find all of its 'to' squares
        for (int fromSquareIdx = 0; fromSquareIdx < fromSquareCount; fromSquareIdx++) {
            long fromSquare = SQUARE_IDS[fromSquareIdx];

            int toSquareCount = getDiagonalMoves(fromSquare);
            for (int toSquareIdx = 0; toSquareIdx < toSquareCount; toSquareIdx++) {
                createAndSaveMove(BISHOP, fromSquare, squareIds[toSquareIdx]);
            }
        }
    }

    /**
     * Generates all possible queen moves for the side to move in the given position.
     */
    void generateQueenMoves() {
        // Find all my queens
        int fromSquareCount = Square.bitboardToIds(position.queen & friend);

        // For each queen, find all of its 'to' squares
        for (int fromSquareIdx = 0; fromSquareIdx < fromSquareCount; fromSquareIdx++) {
            long fromSquare = SQUARE_IDS[fromSquareIdx];

            int toSquareCount = getDiagonalMoves(fromSquare);
            for (int toSquareIdx = 0; toSquareIdx < toSquareCount; toSquareIdx++) {
                createAndSaveMove(QUEEN, fromSquare, squareIds[toSquareIdx]);
            }
            toSquareCount = getStraightMoves(fromSquare);
            for (int toSquareIdx = 0; toSquareIdx < toSquareCount; toSquareIdx++) {
                createAndSaveMove(QUEEN, fromSquare, squareIds[toSquareIdx]);
            }
        }
    }

    /**
     * Generates all possible rook moves for the side to move in the given position.
     */
    void generateRookMoves() {
        // Find all my rooks
        int fromSquareCount = Square.bitboardToIds(position.rook & friend);

        // For each rook, find all of its 'to' squares
        for (int fromSquareIdx = 0; fromSquareIdx < fromSquareCount; fromSquareIdx++) {
            long fromSquare = SQUARE_IDS[fromSquareIdx];

            int toSquareCount = getStraightMoves(fromSquare);
            for (int toSquareIdx = 0; toSquareIdx < toSquareCount; toSquareIdx++) {
                createAndSaveMove(ROOK, fromSquare, squareIds[toSquareIdx]);
            }
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Creates a move with the given piece moving from fromSquare to toSquare, possibly
     * making a capture. The created move is added to the {@link #moves} matrix.
     */
    private void createAndSaveMove(int piece, long fromSquare, long toSquare) {
        int move;
        if (isCapture(toSquare)) {
            move = Move.createCapture(piece, fromSquare, toSquare, position.getPiece(toSquare));
        } else {
            move = Move.create(piece, fromSquare, toSquare);
        }
        moves[positionIndex][moveIndex++] = move;
    }

    /**
     * Populates the array {@link #squareIds} with all squares that a piece on square
     * {@code from} can move to straight. Returns the number of such squares found.
     *
     * The following things are checked:
     *
     * - The destination square is not blocked by a piece of my color.
     * - The move does not cross a border.
     */
    private int getStraightMoves(long from) {
        boolean inNorthBorder = (NORTH_BORDER & from) != 0;
        boolean inEastBorder = (EAST_BORDER & from) != 0;
        boolean inSouthBorder = (SOUTH_BORDER & from) != 0;
        boolean inWestBorder = (WEST_BORDER & from) != 0;

        int count = 0;

        long stop;
        long pos;

        // North
        if (!inNorthBorder) {
            stop = NORTH_BORDER | occupied;
            pos = Square.north(from);
            while ((pos & stop) == 0) {
                squareIds[count++] = pos;
                pos = Square.north(pos);
            }
            if (!isBlocked(pos))
                squareIds[count++] = pos;
        }

        // East
        if (!inEastBorder) {
            stop = EAST_BORDER | occupied;
            pos = Square.east(from);
            while ((pos & stop) == 0) {
                squareIds[count++] = pos;
                pos = Square.east(pos);
            }
            if (!isBlocked(pos))
                squareIds[count++] = pos;
        }

        // South
        if (!inSouthBorder) {
            stop = SOUTH_BORDER | occupied;
            pos = Square.south(from);
            while ((pos & stop) == 0) {
                squareIds[count++] = pos;
                pos = Square.south(pos);
            }
            if (!isBlocked(pos))
                squareIds[count++] = pos;
        }

        // West
        if (!inWestBorder) {
            stop = WEST_BORDER | occupied;
            pos = Square.west(from);
            while ((pos & stop) == 0) {
                squareIds[count++] = pos;
                pos = Square.west(pos);
            }
            if (!isBlocked(pos))
                squareIds[count++] = pos;
        }

        return count;
    }

    /**
     * Populates the array {@link #squareIds} with all squares that a piece on square
     * {@code from} can move to diagonally. Returns the number of such squares found.
     *
     * The following things are checked:
     *
     * - The destination square is not blocked by a piece of my color.
     * - The move does not cross a border.
     */
    private int getDiagonalMoves(long from) {
        boolean inNorthBorder = (NORTH_BORDER & from) != 0;
        boolean inEastBorder = (EAST_BORDER & from) != 0;
        boolean inSouthBorder = (SOUTH_BORDER & from) != 0;
        boolean inWestBorder = (WEST_BORDER & from) != 0;

        int count = 0;

        long stop;
        long pos;

        // NE
        if (!inNorthBorder && !inEastBorder) {
            stop = NORTH_BORDER | EAST_BORDER | occupied;
            pos = Square.northEast(from);
            while ((pos & stop) == 0) {
                squareIds[count++] = pos;
                pos = Square.northEast(pos);
            }
            if (!isBlocked(pos)) {
                squareIds[count++] = pos;
            }
        }

        // SE
        if (!inSouthBorder && !inEastBorder) {
            stop = SOUTH_BORDER | EAST_BORDER | occupied;
            pos = Square.southEast(from);
            while ((pos & stop) == 0) {
                squareIds[count++] = pos;
                pos = Square.southEast(pos);
            }
            if (!isBlocked(pos))
                squareIds[count++] = pos;
        }

        // SW
        if (!inSouthBorder && !inWestBorder) {
            stop = SOUTH_BORDER | WEST_BORDER | occupied;
            pos = Square.southWest(from);
            while ((pos & stop) == 0) {
                squareIds[count++] = pos;
                pos = Square.southWest(pos);
            }
            if (!isBlocked(pos))
                squareIds[count++] = pos;
        }

        // NW
        if (!inNorthBorder && !inWestBorder) {
            stop = NORTH_BORDER | WEST_BORDER | occupied;
            pos = Square.northWest(from);
            while ((pos & stop) == 0) {
                squareIds[count++] = pos;
                pos = Square.northWest(pos);
            }
            if (!isBlocked(pos))
                squareIds[count++] = pos;
        }

        return count;
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
     */
    private boolean isRookOnStartSquare(long square) {
        return (position.rook & friend & square) != 0;
    }
}
