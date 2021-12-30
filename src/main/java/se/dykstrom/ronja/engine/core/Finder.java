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

/**
 * Interface to be implemented by all move finders.
 *
 * @author Johan Dykstrom
 */
public interface Finder {
    /**
     * Finds and returns the best move in the given position within the given time.
     *
     * @param maxTime The maximum time to spend in milliseconds.
     * @return The move found.
     */
    int findBestMoveWithinTime(long maxTime);

    /**
     * Finds and returns the best move in the given position. Searching is limited to the given {@code maxDepth}.
     *
     * @param maxDepth The maximum search depth.
     * @return The move found.
     */
    int findBestMove(int maxDepth);
}
