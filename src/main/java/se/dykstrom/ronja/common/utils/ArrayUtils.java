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

package se.dykstrom.ronja.common.utils;

import java.util.List;

/**
 * Utility methods related to arrays.
 *
 * @author Johan Dykstrom
 */
public final class ArrayUtils {

    private ArrayUtils() { }

    /**
     * Converts the given list of Integer objects to an array of ints.
     */
    public static int[] toArray(List<Integer> list) {
        return list.stream().mapToInt(element -> element).toArray();
    }
}
