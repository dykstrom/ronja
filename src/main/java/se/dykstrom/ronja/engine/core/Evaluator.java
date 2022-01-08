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

import se.dykstrom.ronja.common.model.Board;
import se.dykstrom.ronja.common.model.Position;

/**
 * A class used to evaluate positions.
 * 
 * @author Johan Dykstrom
 */
public class Evaluator {

    /** Value returned if the side to move is in check, which is illegal. */
    public static final int ILLEGAL_CHECK_VALUE = 2000000;

    /** Value returned if the side to move is checkmated. */
    public static final int CHECK_MATE_VALUE = -1000000;

    /** Value returned if the position is a draw. */
    public static final int DRAW_VALUE = 0;

    private static final int PAWN_VALUE = 1000;

    private static final int BISHOP_VALUE = 3 * PAWN_VALUE;
    private static final int KNIGHT_VALUE = 3 * PAWN_VALUE;
    private static final int ROOK_VALUE   = 5 * PAWN_VALUE;
    private static final int QUEEN_VALUE  = 9 * PAWN_VALUE;

    private static final int BISHOP_PAIR_VALUE = PAWN_VALUE / 2;

    private static final int ATTACKED_SQUARE_VALUE = 10;

    /**
     * Evaluates the given position, and returns a score. The score will be
     * positive if the side to move is in the lead.
     * 
     * @param position The position to evaluate.
     */
    public int evaluate(Position position) {
        // When calculating the score, white is positive, and black is negative
        int score = 0;

        // Calculate value of pieces
        score += calculatePieceValues(position);

        // Calculate value of attacked squares
        score += calculateAttackedSquares(position);

        // If black is to move, negate the score
        return position.isWhiteMove() ? score: -score;
    }

    /**
     * Calculates the value of attacked squares in the given {@code position}.
     */
    public int calculateAttackedSquares(Position position) {
        int noOfAttackedSquaresWhite = Board.popCount(position.whiteAttack);
        int noOfAttackedSquaresBlack = Board.popCount(position.blackAttack);
        return (noOfAttackedSquaresWhite - noOfAttackedSquaresBlack) * ATTACKED_SQUARE_VALUE;
    }

    /**
     * Calculates the total piece values in the given {@code position}.
     */
    public int calculatePieceValues(Position position) {
        int score = 0;

        int noOfBishopsWhite = Board.popCount(position.white & position.bishop);
        score += noOfBishopsWhite * BISHOP_VALUE;
        score += (noOfBishopsWhite > 1) ? BISHOP_PAIR_VALUE : 0;
        score += Board.popCount(position.white & position.knight) * KNIGHT_VALUE;
        score += Board.popCount(position.white & position.queen) * QUEEN_VALUE;
        score += Board.popCount(position.white & position.pawn) * PAWN_VALUE;
        score += Board.popCount(position.white & position.rook) * ROOK_VALUE;

        int noOfBishopsBlack = Board.popCount(position.black & position.bishop);
        score -= noOfBishopsBlack * BISHOP_VALUE;
        score -= (noOfBishopsBlack > 1) ? BISHOP_PAIR_VALUE : 0;
        score -= Board.popCount(position.black & position.knight) * KNIGHT_VALUE;
        score -= Board.popCount(position.black & position.queen) * QUEEN_VALUE;
        score -= Board.popCount(position.black & position.pawn) * PAWN_VALUE;
        score -= Board.popCount(position.black & position.rook) * ROOK_VALUE;

        return score;
    }
}
