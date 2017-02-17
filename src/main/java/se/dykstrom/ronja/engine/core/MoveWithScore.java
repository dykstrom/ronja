/*
 * Copyright (C) 2017 Johan Dykstrom
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

import se.dykstrom.ronja.common.model.Move;

/**
 * Represents a move and the score it was evaluated to.
 *
 * @author Johan Dykstrom
 */
public class MoveWithScore {

    private final Move move;
    private final int score;

    private MoveWithScore(Move move, int score) {
        this.move = move;
        this.score = score;
    }

    /**
     * Creates a new move with score of the given move and score.
     */
    public static MoveWithScore of(Move move, int score) {
        return new MoveWithScore(move, score);
    }

    public Move getMove() {
        return move;
    }

    public int getScore() {
        return score;
    }
}
