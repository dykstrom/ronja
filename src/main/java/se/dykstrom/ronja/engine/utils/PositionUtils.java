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

package se.dykstrom.ronja.engine.utils;

import se.dykstrom.ronja.common.model.Board;
import se.dykstrom.ronja.common.model.Color;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.engine.core.FullMoveGenerator;

/**
 * Utility methods related to class {@link Position}.
 *
 * @author Johan Dykstrom
 */
public final class PositionUtils {

    private static final FullMoveGenerator MOVE_GENERATOR = new FullMoveGenerator();

    private PositionUtils() { }

    /**
     * Returns {@code true} if the given position is legal, that is, the board
     * contains exactly one white king and one black king.
     */
    public static boolean isLegal(Position position) {
        return (Board.popCount(position.king & position.white) == 1) && (Board.popCount(position.king & position.black) == 1);
    }

    /**
     * Returns {@code true} if the game is over, that is, if the given position is draw or checkmate.
     */
    public static boolean isGameOver(Position position) {
        return isCheckMate(position) || isDraw(position);
    }

    /**
     * Returns {@code true} if the side to move is checkmated in the given position.
     */
    public static boolean isCheckMate(Position position) {
        Color color = position.getActiveColor();

        // If player is not in check, it is not checkmate
        if (!position.isCheck(color)) {
            return false;
        }

        // If we can find a move out of check, it is not checkmate
        int numberOfMoves = MOVE_GENERATOR.generateMoves(position, 0);
        for (int moveIndex = 0; moveIndex < numberOfMoves; moveIndex++) {
            int move = MOVE_GENERATOR.moves[0][moveIndex];
            if (!position.withMove(move).isCheck(color)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns {@code true} if the given position is a draw.
     */
    public static boolean isDraw(Position position) {
        return getDrawType(position) != null;
    }

    /**
     * Returns a string describing the type of draw in the given position, or {@code null}
     * if the given position is not a draw at all.
     */
    public static String getDrawType(Position position) {
        if (isDrawByFiftyMoveRule(position)) {
            return "Fifty move rule";
        }

        if (isDrawByLackOfMatingMaterial(position)) {
            return "Insufficient mating material";
        }

        if (isDrawByThreefoldRepetition(position)) {
            return "Threefold repetition";
        }

        if (isDrawByStalemate(position)) {
            return "Stalemate";
        }

        return null;
    }

    /**
     * Returns {@code true} if the given position is a draw by stalemate.
     */
    private static boolean isDrawByStalemate(Position position) {
        Color color = position.getActiveColor();

        // If player is in check, it is not stalemate
        if (position.isCheck(color)) {
            return false;
        }

        // If we find a move that is not check, it is not stalemate
        int numberOfMoves = MOVE_GENERATOR.generateMoves(position, 0);
        for (int moveIndex = 0; moveIndex < numberOfMoves; moveIndex++) {
            int move = MOVE_GENERATOR.moves[0][moveIndex];
            if (!position.withMove(move).isCheck(color)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the given position is a draw by threefold repetition of position.
     */
    private static boolean isDrawByThreefoldRepetition(Position position) {
        return false;
    }

    /**
     * Returns {@code true} if the given position is a draw by lack of mating material.
     */
    private static boolean isDrawByLackOfMatingMaterial(Position position) {
        if (position.pawn == 0 && position.queen == 0 && position.rook == 0) {
            if (position.knight == 0 && Board.popCount(position.bishop) <= 1) {
                return true;
            }
            if (position.bishop == 0 && Board.popCount(position.knight) <= 1) {
                return true;
            }
            // TODO: Same colored bishops.
        }
        return false;
    }

    /**
     * Returns {@code true} if the given position is a draw by the fifty move rule.
     */
    private static boolean isDrawByFiftyMoveRule(Position position) {
        return position.getHalfMoveClock() >= 100;
    }
}
