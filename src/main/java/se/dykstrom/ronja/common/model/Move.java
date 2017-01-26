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

import se.dykstrom.ronja.engine.utils.FastList;

import java.util.List;

/**
 * This class represents a single move in a chess game.
 *
 * @author Johan Dykstrom
 */
public class Move {

    /** The piece that is moved in this move. */
    private final Piece piece;

    /** The piece the pawn is promoted to if this is a promotion move. */
    private final Piece promoted;

    /** The ID of the 'from' square. */
    private final long from;

    /** The ID of the 'to' square. */
    private final long to;

    /** True if this is a castling move. */
    private final boolean isCastling;

    /** True if this is an 'en passant' move. */
    private final boolean isEnPassant;

    private Move(Piece piece, long from, long to, Piece promoted, boolean isCastling, boolean isEnPassant) {
        this.piece = piece;
        this.from = from;
        this.to = to;
        this.promoted = promoted;
        this.isCastling = isCastling;
        this.isEnPassant = isEnPassant;
    }

    /**
     * Creates a new {@code Move} of the given arguments.
     *
     * @param piece The piece to move.
     * @param from The 'from' square.
     * @param to The 'to' square.
     * @param promoted The piece that the pawn is promoted to, or {@code null} if no promotion.
     * @param isCastling True if this is a castling move.
     * @param isEnPassant True if this is an 'en passant' move.
     * @return The created move.
     */
    public static Move of(Piece piece, long from, long to, Piece promoted, boolean isCastling, boolean isEnPassant) {
        return new Move(piece, from, to, promoted, isCastling, isEnPassant);
    }

    /**
     * Creates a number of new {@code Move}s of the given arguments. All moves start at square {@code from},
     * but they end at different 'to' squares, as defined by list {@code tos}.
     *
     * @param piece The piece to move.
     * @param from The 'from' square.
     * @param tos A list of 'to' squares.
     * @param promoted If this is a promotion move, this is the piece that the pawn is promoted to. Otherwise {@code null}.
     * @param isCastling True if this is a castling move.
     * @param isEnPassant True if this is an 'en passant' move.
     * @return A list of created moves.
     */
    public static List<Move> of(Piece piece, long from, List<Long> tos, Piece promoted, boolean isCastling, boolean isEnPassant) {
        int size = tos.size();
        Move[] moves = new Move[size];
        for (int i = 0; i < size; i++) {
            moves[i] = Move.of(piece, from, tos.get(i), promoted, isCastling, isEnPassant);
        }
        return new FastList<>(moves);
    }

    /**
	 * Returns the piece moved in this move. If this move is a isCastling move,
	 * {@code Piece.King} will be returned.
	 */
	public Piece getPiece() {
		return piece;
	}

	/**
	 * Returns the ID of the 'from' square.
	 */
	public long getFrom() {
		return from;
	}

	/**
	 * Returns the ID of the 'to' square.
	 */
	public long getTo() {
		return to;
	}

	/**
	 * If this is a promotion move, return the piece that the pawn was promoted
	 * to, otherwise return {@code null}.
	 */
	public Piece getPromoted() {
		return promoted;
	}

	/**
	 * Returns true if this is a promotion move, and false otherwise.
	 */
	public boolean isPromotion() {
		return (promoted != null);
	}

	/**
	 * Returns true if this is a castling move, and false otherwise.
	 */
	public boolean isCastling() {
		return isCastling;
	}

	/**
	 * Returns true if this is an 'en passant' move, and false otherwise.
	 */
	public boolean isEnPassant() {
		return isEnPassant;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("[");
		builder.append(getPiece());
		builder.append(" ");
		builder.append(Square.idToName(from));
		builder.append(Square.idToName(to));
		if (isCastling()) {
			builder.append(", castling");
		} else if (isPromotion()) {
			builder.append(", promotion -> ").append(promoted);
		} else if (isEnPassant()) {
			builder.append(", en passant");
		}
		builder.append("]");

		return builder.toString();
	}

    @Override
    public int hashCode() {
        int result = piece.hashCode();
        result = 31 * result + (promoted != null ? promoted.hashCode() : 0);
        result = 31 * result + (int) (from ^ (from >>> 32));
        result = 31 * result + (int) (to ^ (to >>> 32));
        result = 31 * result + (isCastling ? 1 : 0);
        result = 31 * result + (isEnPassant ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            Move that = (Move) obj;
            return ((this.from == that.from) &&
                    (this.to == that.to) &&
                    (this.piece == that.piece) &&
                    (this.promoted == that.promoted) &&
                    (this.isCastling == that.isCastling) &&
                    (this.isEnPassant == that.isEnPassant));
        } else {
            return false;
        }
    }
}
