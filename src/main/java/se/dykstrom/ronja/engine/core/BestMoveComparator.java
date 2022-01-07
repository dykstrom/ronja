/*
 * Copyright (C) 2022 Johan Dykstrom
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

/**
 * A move comparator that takes into account the best move from the previous
 * search, and orders the moves so the best move is always first.
 */
public record BestMoveComparator(int bestMove) implements MoveComparator {
    @Override
    public int compare(final int x, final int y) {
        if (x == bestMove) {
            return -1;
        } else if (y == bestMove) {
            return 1;
        }
        return Integer.compare(y, x);
    }
}
