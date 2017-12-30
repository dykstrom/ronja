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

/**
 * Utility methods related to sorting.
 *
 * @author Johan Dykstrom
 */
public final class SortUtils {

    /**
     * A comparison function, which imposes a <i>total ordering</i> on some collection of ints.
     */
    @FunctionalInterface
    public interface IntComparator {
        /**
         * Compares its two arguments for order. Returns a negative integer,
         * zero, or a positive integer as the first argument is less than, equal
         * to, or greater than the second.
         *
         * @param x The first integer to compare.
         * @param y The second integer to compare.
         * @return a negative integer, zero, or a positive integer as the
         *         first argument is less than, equal to, or greater than the
         *         second.
         */
        int compare(int x, int y);
    }

    /** An IntComparator that sorts ints in their natural order. */
    public static final IntComparator NATURAL_ORDER_COMPARATOR = Integer::compare;

    private SortUtils() { }

    /**
     * Sorts the first part of the given array into the order specified by the given comparator.
     * The number of elements to actually sort is given by {@code number}, which must be less than
     * or equal to the array size.
     *
     * @implNote The sorting algorithm used is insert sort. This may be changed in future versions.
     *
     * @param array The array to sort.
     * @param number The number of elements to sort in the array.
     * @param comparator The comparator to use when comparing elements.
     */
    public static void sort(int[] array, int number, IntComparator comparator) {
        for (int index = 1; index < number; index++) {
            int data = array[index];
            int dataIndex = index;
            while (dataIndex > 0 && comparator.compare(data, array[dataIndex - 1]) < 0) {
                array[dataIndex] = array[dataIndex - 1];
                dataIndex--;
            }
            array[dataIndex] = data;
        }
    }
}
