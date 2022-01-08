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

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import se.dykstrom.ronja.common.parser.IllegalMoveException;
import se.dykstrom.ronja.common.parser.MoveParser;
import se.dykstrom.ronja.engine.core.AttackGenerator;

import static se.dykstrom.ronja.common.model.Piece.BISHOP;
import static se.dykstrom.ronja.common.model.Piece.KING;
import static se.dykstrom.ronja.common.model.Piece.KNIGHT;
import static se.dykstrom.ronja.common.model.Piece.PAWN;
import static se.dykstrom.ronja.common.model.Piece.QUEEN;
import static se.dykstrom.ronja.common.model.Piece.ROOK;

/**
 * Represents a chess position, including the move number, the active color,
 * castling rights, and a possible 'en passant' target square.
 * <p>
 * Rules:
 * <p>
 * - A number of longs (bitboards) are used to store the positions.
 * - Bit 00 corresponds to square a1,
 * 01                       b1,
 * 07                       h1,
 * 08                       a2,
 * 56                       a8,
 * 62                       g8,
 * 63                       h8.
 * - The toString() method draws chess boards with white at the bottom.
 * - This means the board will look like this:
 * <p>
 * a8 b8 c8 d8 e8 f8 g8 h8
 * .  .  .  .  .  .  .  .
 * .  .  .  .  .  .  .  .
 * .  .  .  .  .  .  .  .
 * a2 b2 c2 d2 e2 f2 g2 h2
 * a1 b1 c1 d1 e1 f1 g1 h1
 * <p>
 * or:
 * <p>
 * 56 57 58 59 60 61 62 63
 * .  .  .  .  .  .  .  .
 * .  .  .  .  .  .  .  .
 * .  .  .  .  .  .  .  .
 * 08 09 10 11 12 13 14 15
 * 00 01 02 03 04 05 06 07
 *
 * @author Johan Dykstrom
 */
public class Position {

    /**
     * The start position.
     */
    public static final Position START = new Position();

    private static final Logger TLOG = Logger.getLogger(Position.class.getName());

    /**
     * Bit masks for manipulating castling rights.
     */
    private static final long WKS_CASTLING_MASK = 1L;
    private static final long WQS_CASTLING_MASK = 1L << 1L;
    private static final long BKS_CASTLING_MASK = 1L << 2L;
    private static final long BQS_CASTLING_MASK = 1L << 3L;

    /**
     * Bit mask for manipulating active color.
     */
    private static final long ACTIVE_COLOR_MASK = 1L << 4L;

    /**
     * A bitset of flags that define the following data:
     * <p>
     * - Bit 0-1: Castling rights for white (1 = yes, 0 = no)
     * - Bit 2-3: Castling rights for black (1 = yes, 0 = no)
     * - Bit 4:   Active color (1 = white, 0 = black)
     */
    private final long flags;

    /**
     * Bitboards representing the squares occupied by different pieces.
     */
    public final long bishop;
    public final long king;
    public final long knight;
    public final long pawn;
    public final long queen;
    public final long rook;

    /**
     * Bitboards representing the squares occupied by white/black pieces.
     */
    public final long white;
    public final long black;

    /**
     * Bitboards representing the squares attacked by white/black pieces.
     */
    public final long whiteAttack;
    public final long blackAttack;

    /**
     * The ID of the 'en passant' target square, or 0 if 'en passant' is not allowed in this position.
     */
    private final long enPassantSquare;

    /**
     * The number of the full move, starts at 1.
     */
    private final int fullMoveNumber;

    /**
     * The number of half moves since the last pawn advance or capture.
     */
    private final int halfMoveClock;

    /**
     * Used to find attacked squares.
     */
    private static final AttackGenerator ATTACK_GENERATOR = new AttackGenerator();

    public Position(long bishop, long king, long knight, long pawn, long queen, long rook, long white, long black,
                    long whiteAttack, long blackAttack, long enPassantSquare, int fullMoveNumber, int halfMoveClock, long flags) {
        this.bishop = bishop;
        this.king = king;
        this.knight = knight;
        this.pawn = pawn;
        this.queen = queen;
        this.rook = rook;
        this.white = white;
        this.black = black;
        this.whiteAttack = whiteAttack;
        this.blackAttack = blackAttack;
        this.enPassantSquare = enPassantSquare;
        this.fullMoveNumber = fullMoveNumber;
        this.halfMoveClock = halfMoveClock;
        this.flags = flags;
    }

    /**
     * Creates the initial position.
     */
    private Position() {
        bishop = Square.C1 | Square.F1 | Square.C8 | Square.F8;
        king =   Square.E1 | Square.E8;
        knight = Square.B1 | Square.G1 | Square.B8 | Square.G8;
        pawn =   Square.A2 | Square.B2 | Square.C2 | Square.D2 | Square.E2 | Square.F2 | Square.G2 | Square.H2 |
                 Square.A7 | Square.B7 | Square.C7 | Square.D7 | Square.E7 | Square.F7 | Square.G7 | Square.H7;
        queen =  Square.D1 | Square.D8;
        rook =   Square.A1 | Square.A8 | Square.H1 | Square.H8;
        white =  Square.A1 | Square.B1 | Square.C1 | Square.D1 | Square.E1 | Square.F1 | Square.G1 | Square.H1 |
                 Square.A2 | Square.B2 | Square.C2 | Square.D2 | Square.E2 | Square.F2 | Square.G2 | Square.H2;
        black =  Square.A7 | Square.B7 | Square.C7 | Square.D7 | Square.E7 | Square.F7 | Square.G7 | Square.H7 |
                 Square.A8 | Square.B8 | Square.C8 | Square.D8 | Square.E8 | Square.F8 | Square.G8 | Square.H8;
        whiteAttack = Square.B1 | Square.C1 | Square.D1 | Square.E1 | Square.F1 | Square.G1 |
                Square.A2 | Square.B2 | Square.C2 | Square.D2 | Square.E2 | Square.F2 | Square.G2 | Square.H2 |
                Square.A3 | Square.B3 | Square.C3 | Square.D3 | Square.E3 | Square.F3 | Square.G3 | Square.H3;
        blackAttack = Square.B8 | Square.C8 | Square.D8 | Square.E8 | Square.F8 | Square.G8 |
                Square.A7 | Square.B7 | Square.C7 | Square.D7 | Square.E7 | Square.F7 | Square.G7 | Square.H7 |
                Square.A6 | Square.B6 | Square.C6 | Square.D6 | Square.E6 | Square.F6 | Square.G6 | Square.H6;
        enPassantSquare = 0;
        fullMoveNumber = 1;
        halfMoveClock = 0;
        flags = WKS_CASTLING_MASK | WQS_CASTLING_MASK | BKS_CASTLING_MASK | BQS_CASTLING_MASK | ACTIVE_COLOR_MASK;
    }

    /**
     * Creates a new position by making the given moves. The first move must
     * be a white move, the second must be a black move etc.
     *
     * @param moves The moves to make after setting up the initial position.
     * @throws IllegalMoveException If any of the given moves is illegal.
     */
    public static Position of(String[] moves) throws IllegalMoveException {
        return Position.of(Arrays.asList(moves));
    }

    /**
     * Creates a new position by making the given moves. The first move must
     * be a white move, the second must be a black move etc.
     *
     * @param moves The moves to make after setting up the initial position.
     * @throws IllegalMoveException If any of the given moves is illegal.
     */
    public static Position of(List<String> moves) throws IllegalMoveException {
        Position position = Position.START;
        for (String move : moves) {
            position = position.withMove(MoveParser.parse(move, position));
        }
        return position;
    }

    /**
     * Returns a copy of this position, after making the given move. This position remains unchanged.
     *
     * @param move The move to make.
     * @return The position that arises when making the given move in this position.
     */
    public Position withMove(int move) {
        long from = Move.getFrom(move);
        long to = Move.getTo(move);

        int fromPiece = Move.getPiece(move);
        int toPiece = getPiece(to);

        long white = this.white;
        long black = this.black;
        long bishop = this.bishop;
        long king = this.king;
        long knight = this.knight;
        long queen = this.queen;
        long pawn = this.pawn;
        long rook = this.rook;

        // Update white/black bitboards
        if (isWhiteMove()) {
            white = white ^ from | to;
        } else {
            black = black ^ from | to;
        }

        // Move the piece
        switch (fromPiece) {
            case BISHOP:
                bishop = bishop ^ from | to;
                break;
            case KING:
                king = king ^ from | to;
                break;
            case KNIGHT:
                knight = knight ^ from | to;
                break;
            case PAWN:
                pawn = pawn ^ from | to;
                break;
            case QUEEN:
                queen = queen ^ from | to;
                break;
            case ROOK:
                rook = rook ^ from | to;
                break;
        }

        // If there was a piece on the to square (a capture)
        if (toPiece != 0) {
            // Update white/black bitboards to remove the piece that has been captured
            if (isWhiteMove()) {
                black = black ^ to;
            } else {
                white = white ^ to;
            }

            // If the captured piece was not the same as the capturing piece, remove it
            if (toPiece != fromPiece) {
                switch (toPiece) {
                    case BISHOP:
                        bishop = bishop ^ to;
                        break;
                    case KING:
                        king = king ^ to;
                        break;
                    case KNIGHT:
                        knight = knight ^ to;
                        break;
                    case PAWN:
                        pawn = pawn ^ to;
                        break;
                    case QUEEN:
                        queen = queen ^ to;
                        break;
                    case ROOK:
                        rook = rook ^ to;
                        break;
                }
            }
        }

        // Promotion
        if (Move.isPromotion(move)) {
            // Remove the pawn
            pawn = pawn ^ to;

            // Add promotion piece
            switch (Move.getPromoted(move)) {
                case Piece.BISHOP:
                    bishop = bishop | to;
                    break;
                case Piece.KNIGHT:
                    knight = knight | to;
                    break;
                case Piece.QUEEN:
                    queen = queen | to;
                    break;
                case Piece.ROOK:
                    rook = rook | to;
                    break;
                default:
                    TLOG.severe("Invalid promotion piece: " + Move.getPromoted(move));
                    throw new IllegalArgumentException("invalid promotion piece: " + Move.getPromoted(move));
            }
        }

        // Castling
        else if (Move.isCastling(move)) {
            if (to == Square.G1) {          // White king-side castling
                rook = rook ^ Square.H1 | Square.F1;
                white = white ^ Square.H1 | Square.F1;
            } else if (to == Square.C1) {   // White queen-side castling
                rook = rook ^ Square.A1 | Square.D1;
                white = white ^ Square.A1 | Square.D1;
            } else if (to == Square.G8) {   // Black king-side castling
                rook = rook ^ Square.H8 | Square.F8;
                black = black ^ Square.H8 | Square.F8;
            } else if (to == Square.C8) {   // Black queen-side castling
                rook = rook ^ Square.A8 | Square.D8;
                black = black ^ Square.A8 | Square.D8;
            } else {
                TLOG.severe("Invalid castling square: " + Square.idToName(to));
                throw new IllegalArgumentException("invalid castling square: " + Square.idToName(to));
            }
        }

        // En passant
        else if (Move.isEnPassant(move)) {
            // Update white/black bitboards to remove the pawn that has been captured
            // This pawn is not on the 'to' square, but one square closer to the
            // center of the board, so we shift the 'to' square towards the center
            if (isWhiteMove()) {
                black = black ^ Square.south(to);
                pawn = pawn ^ Square.south(to);
            } else {
                white = white ^ Square.north(to);
                pawn = pawn ^ Square.north(to);
            }
        }

        // Update 'en passant' target square
        long enPassantSquare = updateEnPassantSquare(fromPiece, from, to);

        // Increment move number
        int fullMoveNumber = isWhiteMove() ? this.fullMoveNumber : this.fullMoveNumber + 1;

        // Update half move clock
        int halfMoveClock = isPawnMoveOrCapture(fromPiece, toPiece) ? 0 : this.halfMoveClock + 1;

        // Update castling rights
        long flags = updateCastlingRights(from, this.flags);

        // Flip sides
        flags = flags ^ ACTIVE_COLOR_MASK;

        // Create the new position, though we still have the old attack bitboards
        Position position = new Position(bishop, king, knight, pawn, queen, rook, white, black,
                whiteAttack, blackAttack, enPassantSquare, fullMoveNumber, halfMoveClock, flags);

        // Return the new position, including new attack bitboards
        return position.withAttackBitboards(ATTACK_GENERATOR.getAttackedSquares(Color.WHITE, position),
                ATTACK_GENERATOR.getAttackedSquares(Color.BLACK, position));
    }

    /**
     * Returns a copy of this position, with the attack bitboards altered. This position remains unchanged.
     *
     * @param whiteAttack The new attack bitboard for white.
     * @param blackAttack The new attack bitboard for black.
     * @return A position, based on this position with the attack bitboards altered.
     */
    public Position withAttackBitboards(long whiteAttack, long blackAttack) {
        return new Position(bishop, king, knight, pawn, queen, rook, white, black,
                whiteAttack, blackAttack, enPassantSquare, fullMoveNumber, halfMoveClock, flags);
    }

    /**
     * Returns a copy of this position, with the active color altered. This position remains unchanged.
     *
     * @param color The new active color, {@code WHITE} or {@code BLACK}.
     * @return A position, based on this position with the active color altered.
     */
    public Position withActiveColor(Color color) {
        long flags;
        if (color == Color.WHITE) {
            flags = this.flags | ACTIVE_COLOR_MASK;
        } else {
            flags = this.flags & ~ACTIVE_COLOR_MASK;
        }
        return new Position(bishop, king, knight, pawn, queen, rook, white, black,
                whiteAttack, blackAttack, enPassantSquare, fullMoveNumber, halfMoveClock, flags);
    }

    /**
     * Returns the active color, that is, the color to move next: {@code WHITE} or {@code BLACK}.
     *
     * @return The active color.
     */
    public Color getActiveColor() {
        return (isWhiteMove()) ? Color.WHITE : Color.BLACK;
    }

    /**
     * Returns {@code true} if it is white's move, {@code false} otherwise.
     */
    public boolean isWhiteMove() {
        return (flags & ACTIVE_COLOR_MASK) != 0;
    }

    /**
     * Returns {@code true} if this move is a pawn move or a capture, based on the
     * {@code fromPiece} and the {@code toPiece}.
     */
    private boolean isPawnMoveOrCapture(int fromPiece, int toPiece) {
        return (fromPiece == Piece.PAWN) || (toPiece != 0);
    }

    /**
     * Calculates the new 'en passant' square if the given piece is a pawn that has made a two-square move.
     * Otherwise, this method returns 0, which means 'en passant' not allowed.
     */
    private long updateEnPassantSquare(int piece, long from, long to) {
        if (isWhiteMove()) {
            if ((piece == Piece.PAWN) && ((from & Board.RANK_2) != 0) && ((to & Board.RANK_4) != 0)) {
                return Square.north(from);
            } else {
                return 0;
            }
        } else {
            if ((piece == Piece.PAWN) && ((from & Board.RANK_7) != 0) && ((to & Board.RANK_5) != 0)) {
                return Square.south(from);
            } else {
                return 0;
            }
        }
    }

    /**
     * Returns {@code flags} updated with new castling rights, given that a move was made from square {@code from}.
     */
    private long updateCastlingRights(long from, long flags) {
        if ((from & (Square.E1 | Square.H1)) != 0) {
            flags = flags & ~WKS_CASTLING_MASK;
        }
        if ((from & (Square.E1 | Square.A1)) != 0) {
            flags = flags & ~WQS_CASTLING_MASK;
        }
        if ((from & (Square.E8 | Square.H8)) != 0) {
            flags = flags & ~BKS_CASTLING_MASK;
        }
        if ((from & (Square.E8 | Square.A8)) != 0) {
            flags = flags & ~BQS_CASTLING_MASK;
        }
        return flags;
    }

    /**
     * Returns a copy of this position, with the full move number altered. This position remains unchanged.
     *
     * @param fullMoveNumber The new full move number.
     * @return A position, based on this position with the full move number altered.
     */
    public Position withFullMoveNumber(int fullMoveNumber) {
        return new Position(bishop, king, knight, pawn, queen, rook, white, black,
                whiteAttack, blackAttack, enPassantSquare, fullMoveNumber, halfMoveClock, flags);
    }

    /**
     * Returns the number of the full move in progress. This number starts at 1,
     * and is incremented after each black move.
     */
    public int getFullMoveNumber() {
        return fullMoveNumber;
    }

    /**
     * Returns a copy of this position, with the half move clock altered. This position remains unchanged.
     *
     * @param halfMoveClock The new half move clock.
     * @return A position, based on this position with the half move clock altered.
     */
    public Position withHalfMoveClock(int halfMoveClock) {
        return new Position(bishop, king, knight, pawn, queen, rook, white, black,
                whiteAttack, blackAttack, enPassantSquare, fullMoveNumber, halfMoveClock, flags);
    }

    /**
     * Returns the number of half moves since the last pawn advance or capture.
     */
    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    /**
     * Returns the color of the piece that occupies the given square,
     * or {@code null} if the square is unoccupied.
     *
     * @return The color of the piece that occupies the given square.
     */
    public Color getColor(long square) {
        if ((square & white) != 0) {
            return Color.WHITE;
        } else if ((square & black) != 0) {
            return Color.BLACK;
        } else {
            return null;
        }
    }

    /**
     * Returns the piece that occupies the specified square, or 0 if the square is unoccupied.
     */
    public int getPiece(long square) {
        if ((square & bishop) != 0) {
            return Piece.BISHOP;
        } else if ((square & king) != 0) {
            return Piece.KING;
        } else if ((square & knight) != 0) {
            return Piece.KNIGHT;
        } else if ((square & pawn) != 0) {
            return Piece.PAWN;
        } else if ((square & queen) != 0) {
            return Piece.QUEEN;
        } else if ((square & rook) != 0) {
            return Piece.ROOK;
        } else {
            return 0;
        }
    }

    /**
     * Returns a copy of this position, with the piece and color of the given square altered.
     * This position remains unchanged.
     *
     * @param square The square to change.
     * @param piece  The piece to place on the square, may be 0.
     * @param color  The color of the piece, may be {@code null}.
     * @return A position, based on this position with the piece and color altered.
     */
    public Position withPieceAndColor(long square, int piece, Color color) {
        // Remove old piece
        long bishop = this.bishop & ~square;
        long king = this.king & ~square;
        long knight = this.knight & ~square;
        long pawn = this.pawn & ~square;
        long queen = this.queen & ~square;
        long rook = this.rook & ~square;

        // Set new piece
        if (piece == Piece.BISHOP) {
            bishop = bishop | square;
        } else if (piece == Piece.KING) {
            king = king | square;
        } else if (piece == Piece.KNIGHT) {
            knight = knight | square;
        } else if (piece == Piece.PAWN) {
            pawn = pawn | square;
        } else if (piece == Piece.QUEEN) {
            queen = queen | square;
        } else if (piece == Piece.ROOK) {
            rook = rook | square;
        }

        // Remove old color
        long white = this.white & ~square;
        long black = this.black & ~square;

        // Set new color
        if (color == Color.WHITE) {
            white = white | square;
        } else if (color == Color.BLACK) {
            black = black | square;
        }

        return new Position(bishop, king, knight, pawn, queen, rook, white, black,
                whiteAttack, blackAttack, enPassantSquare, fullMoveNumber, halfMoveClock, flags);
    }

    /**
     * Returns {@code true} if king-side castling is allowed for the given color.
     */
    public boolean isKingSideCastlingAllowed(Color color) {
        if (color == Color.WHITE) {
            return ((flags & WKS_CASTLING_MASK) != 0);
        } else {
            return ((flags & BKS_CASTLING_MASK) != 0);
        }
    }

    /**
     * Returns {@code true} if queen-side castling is allowed for the given color.
     */
    public boolean isQueenSideCastlingAllowed(Color color) {
        if (color == Color.WHITE) {
            return ((flags & WQS_CASTLING_MASK) != 0);
        } else {
            return ((flags & BQS_CASTLING_MASK) != 0);
        }
    }

    /**
     * Returns a copy of this position, with the king-side castling rights altered. This position remains unchanged.
     *
     * @param color     The color for which to update castling rights.
     * @param isAllowed True if king-side castling is allowed for this color.
     * @return A position, based on this position with the king-side castling rights altered.
     */
    public Position withKingSideCastlingAllowed(Color color, boolean isAllowed) {
        if (color == Color.WHITE) {
            return withCastlingAllowed(WKS_CASTLING_MASK, isAllowed);
        } else {
            return withCastlingAllowed(BKS_CASTLING_MASK, isAllowed);
        }
    }

    /**
     * Returns a copy of this position, with the queen-side castling rights altered. This position remains unchanged.
     *
     * @param color     The color for which to update castling rights.
     * @param isAllowed True if queen-side castling is allowed for this color.
     * @return A position, based on this position with the queen-side castling rights altered.
     */
    public Position withQueenSideCastlingAllowed(Color color, boolean isAllowed) {
        if (color == Color.WHITE) {
            return withCastlingAllowed(WQS_CASTLING_MASK, isAllowed);
        } else {
            return withCastlingAllowed(BQS_CASTLING_MASK, isAllowed);
        }
    }

    /**
     * Returns a copy of this position, with the castling rights defined by {@code mask} altered.
     * This position remains unchanged.
     *
     * @param mask      Castling mask used to update the flags attribute.
     * @param isAllowed True if castling is allowed for this color.
     * @return A position, based on this position with the castling rights altered.
     */
    private Position withCastlingAllowed(long mask, boolean isAllowed) {
        long flags = isAllowed ? this.flags | mask : this.flags & ~mask;
        return new Position(bishop, king, knight, pawn, queen, rook, white, black,
                whiteAttack, blackAttack, enPassantSquare, fullMoveNumber, halfMoveClock, flags);
    }

    /**
     * Returns a copy of this position, with the 'en passant' square altered. This position remains unchanged.
     *
     * @param enPassantSquare The new 'en passant' square.
     * @return A position, based on this position with the 'en passant' square altered.
     */
    public Position withEnPassantSquare(long enPassantSquare) {
        return new Position(bishop, king, knight, pawn, queen, rook, white, black,
                whiteAttack, blackAttack, enPassantSquare, fullMoveNumber, halfMoveClock, flags);
    }

    /**
     * Returns the 'en passant' target square, or 0 if 'en passant' is not allowed
     * in this position. The 'en passant' target square is the square the pawn can
     * move to when doing an 'en passant' capture. For example, if the last pawn
     * move was e2-e4, the 'en passant' target square will be e3.
     */
    public long getEnPassantSquare() {
        return enPassantSquare;
    }

    /**
     * Returns {@code true} if side {@code color} is in check in this position.
     */
    public boolean isCheck(Color color) {
        if (color == Color.WHITE) {
            return (white & king & blackAttack) != 0;
        } else {
            return (black & king & whiteAttack) != 0;
        }
    }

    /**
     * Returns {@code true} if the player that just moved is in check in this position.
     */
    public boolean isIllegalCheck() {
        return isCheck(isWhiteMove() ? Color.BLACK : Color.WHITE);
    }

    @Override
    public int hashCode() {
        return (int) (((white) ^
                (black) ^
                (bishop << 1) ^
                (king << 2) ^
                (knight << 3) ^
                (pawn << 4) ^
                (queen << 5) ^
                (rook << 6)) >> 32);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Position that = (Position) obj;
        return ((this.flags == that.flags) &&
                (this.fullMoveNumber == that.fullMoveNumber) &&
                (this.halfMoveClock == that.halfMoveClock) &&
                (this.enPassantSquare == that.enPassantSquare) &&
                (this.white == that.white) &&
                (this.black == that.black) &&
                (this.bishop == that.bishop) &&
                (this.king == that.king) &&
                (this.knight == that.knight) &&
                (this.pawn == that.pawn) &&
                (this.queen == that.queen) &&
                (this.rook == that.rook));
    }

    /**
     * Returns {@code true} if this position is equal to the given position if the full move number
     * and half move clock are ignored.
     *
     * @param that The position to compare with.
     * @return True if the positions are equal.
     */
    public boolean equalTo(Position that) {
        return ((this.flags == that.flags) &&
                (this.enPassantSquare == that.enPassantSquare) &&
                (this.white == that.white) &&
                (this.black == that.black) &&
                (this.bishop == that.bishop) &&
                (this.king == that.king) &&
                (this.knight == that.knight) &&
                (this.pawn == that.pawn) &&
                (this.queen == that.queen) &&
                (this.rook == that.rook));
    }

    @Override
    public String toString() {
        StringBuilder[] ranks = new StringBuilder[8];

        // For each rank
        for (int r = 0; r < 8; r++) {
            ranks[r] = new StringBuilder();
            // For each file
            for (int f = 0; f < 8; f++) {
                long square = Board.getSquareId(f + 1, r + 1);

                // Get piece and color
                int piece = getPiece(square);
                Color color = getColor(square);
                if (color != null) {
                    ranks[r].append(Piece.toSymbol(piece, color)).append(" ");
                } else {
                    ranks[r].append(". ");
                }
            }
        }

        // Turn board upside down, and add a border
        StringBuilder builder = new StringBuilder();
        builder.append("  -----------------\n");
        for (int r = 7; r >= 0; r--) {
            builder.append(r + 1).append("| ").append(ranks[r].toString()).append("|\n");
        }
        builder.append("  -----------------\n");
        builder.append("   a b c d e f g h");
        return builder.toString();
    }
}
