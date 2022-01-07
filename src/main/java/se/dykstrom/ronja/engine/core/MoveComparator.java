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
 * A comparison function, which imposes a <i>total ordering</i> on some collection of moves.
 * This interface does not extend {@link java.util.Comparator} because a move is represented
 * by the primitive type int, and not the boxed type Integer.
 */
@FunctionalInterface
public interface MoveComparator {
    /**
     * Compares its two arguments for order. Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.
     *
     * @param x The first move to compare.
     * @param y The second move to compare.
     * @return a negative integer, zero, or a positive integer as the
     * first argument is less than, equal to, or greater than the
     * second.
     */
    int compare(int x, int y);
}
