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
 * Utility methods related to sorting.
 *
 * @author Johan Dykstrom
 */
public final class SortUtils {

    private SortUtils() { }

    /**
     * Sorts the first part of the given array into the order specified by the given comparator.
     * The number of elements to actually sort is given by {@code number}, which must be less than
     * or equal to the array size.
     *
     * @implNote The sorting algorithm used is insert sort.
     *
     * @param array The array to sort.
     * @param number The number of elements to sort in the array.
     * @param comparator The comparator to use when comparing elements.
     */
    public static void sort(int[] array, int number, MoveComparator comparator) {
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

    /**
     * Sorts the first part of the given array into descending order. The number of elements to
     * actually sort is given by {@code number}, which must be less than or equal to the array size.
     *
     * The moves in the array should be sorted in descending order, because of how the bits of
     * a move are arranged. See class {@link Move}.
     *
     * @implNote The sorting algorithm used is insert sort.
     *
     * @param array The array to sort.
     * @param number The number of elements to sort in the array.
     * @see Move
     */
    public static void sort(int[] array, int number) {
        for (int index = 1; index < number; index++) {
            int data = array[index];
            int dataIndex = index;
            while (dataIndex > 0 && data > array[dataIndex - 1]) {
                array[dataIndex] = array[dataIndex - 1];
                dataIndex--;
            }
            array[dataIndex] = data;
        }
    }
}
