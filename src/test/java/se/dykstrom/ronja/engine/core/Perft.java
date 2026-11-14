/*
 * Copyright (C) 2026 Johan Dykstrom
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

import se.dykstrom.ronja.common.model.Position;

/**
 * Counts the legal move sequences from a position to a fixed depth. {@link PerftTest} and {@link PerftIT} use it.
 *
 * @author Johan Dykstrom
 */
public final class Perft {

    private final FullMoveGenerator generator = new FullMoveGenerator();

    /**
     * Returns the number of legal move sequences of length {@code depth} from {@code position}.
     */
    long count(Position position, int depth) {
        return count(position, depth, 0);
    }

    /**
     * Returns the number of legal move sequences of length {@code depth} from {@code position}.
     * The generator makes pseudo-legal moves, so this method skips every move that leaves the
     * mover's king in check. The generator stores the moves for each ply in its own row,
     * so the recursive call does not overwrite the moves that the loop is still walking.
     *
     * @param position The position to count from.
     * @param depth The number of plies left to count.
     * @param ply The number of plies from the root position, used as the generator's row index.
     */
    private long count(Position position, int depth, int ply) {
        if (depth == 0) {
            return 1;
        }

        long count = 0;
        int numberOfMoves = generator.generateMoves(position, ply);
        for (int i = 0; i < numberOfMoves; i++) {
            Position next = position.withMove(generator.moves[ply][i]);
            if (!next.isIllegalCheck()) {
                count += count(next, depth - 1, ply + 1);
            }
        }
        return count;
    }
}
