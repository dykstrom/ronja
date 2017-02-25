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

import se.dykstrom.ronja.common.model.Position;

/**
 * An abstract base class for all finders.
 */
abstract class AbstractFinder implements Finder {

    /** The number of nodes evaluated. */
    int nodes;

    /** The maximum search depth. */
    private int maxDepth;

    // -----------------------------------------------------------------------

    /**
     * Sets the maximum search depth.
     */
    void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * Returns {@code true} if the given search {@code depth} equals the maximum search depth.
     */
    boolean isMaxDepth(int depth) {
        return depth == maxDepth;
    }

    /**
     * Returns a debug string to display when entering a search method.
     */
    String enter(Position position, int depth) {
        return "-> " + format(depth) + position.getActiveColor();
    }

    /**
     * Returns a debug string to display while executing a search method.
     */
    String stay(Position position, int depth) {
        return "-- " + format(depth) + position.getActiveColor();
    }

    /**
     * Returns a debug string to display when leaving a search method.
     */
    String leave(Position position, int depth) {
        return "<- " + format(depth) + position.getActiveColor();
    }

    /**
     * Formats the given {@code depth} by adding a number of spaces related to the depth to produce a nice indentation.
     */
    private String format(int depth) {
        return String.format("%s%" + (((depth - 1) * 2) + 1) + "s", depth, "");
    }
}
