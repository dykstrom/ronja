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

import org.junit.Test;
import se.dykstrom.ronja.common.model.Move;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static se.dykstrom.ronja.common.model.Piece.PAWN;
import static se.dykstrom.ronja.common.model.Square.*;
import static se.dykstrom.ronja.engine.core.SortUtils.NATURAL_ORDER_COMPARATOR;

/**
 * This class is for testing class {@code SortUtils} using JUnit.
 *
 * @author Johan Dykstrom
 * @see SortUtils
 */
public class SortUtilsTest {

    private static final SortUtils.IntComparator TEST_COMPARATOR = (x, y) -> {
        if (Move.isCapture(x) && !Move.isCapture(y)) {
            return -1;
        }
        if (!Move.isCapture(x) && Move.isCapture(y)) {
            return 1;
        }
        return 0;
    };

    @Test
    public void shouldSortEmptyArray() {
        // Given
        int[] array = {};
        int[] expected = {};

        // When
        SortUtils.sort(array, array.length, NATURAL_ORDER_COMPARATOR);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortSingleItemArray() {
        // Given
        int[] array = {1};
        int[] expected = {1};

        // When
        SortUtils.sort(array, array.length, NATURAL_ORDER_COMPARATOR);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortShortArray() {
        // Given
        int[] array = {2, 1};
        int[] expected = {1, 2};

        // When
        SortUtils.sort(array, array.length, NATURAL_ORDER_COMPARATOR);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortArray() {
        // Given
        int[] array = {2, 1, 5, 3, 2};
        int[] expected = {1, 2, 2, 3, 5};

        // When
        SortUtils.sort(array, array.length, NATURAL_ORDER_COMPARATOR);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortArrayDescending() {
        // Given
        int[] array = {2, 1, 5, 3, 2};
        int[] expected = {5, 3, 2, 2, 1};

        // When
        SortUtils.sort(array, array.length);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldLongArray() {
        // Given
        int[] array = new int[100];
        int[] expected = new int[100];

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int number = random.nextInt();
            array[i] = number;
            expected[i] = number;
        }

        // When
        SortUtils.sort(array, array.length, NATURAL_ORDER_COMPARATOR);
        Arrays.sort(expected);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortPartOfArray() {
        // Given
        int[] array = {2, 1, 5, 3, 2};
        int[] expected = {1, 2, 5, 3, 2};

        // When
        SortUtils.sort(array, 3, NATURAL_ORDER_COMPARATOR);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortPartOfArrayDescending() {
        // Given
        int[] array = {2, 1, 5, 3, 2};
        int[] expected = {5, 2, 1, 3, 2};

        // When
        SortUtils.sort(array, 3);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortArrayUsingMoveComparator() {
        // Given
        int move0 = Move.create(PAWN, E2_IDX, E4_IDX);
        int move1 = Move.createCapture(PAWN, E2_IDX, F3_IDX, PAWN);
        int move2 = Move.create(PAWN, D2_IDX, D4_IDX);

        int[] array = {move0, move1, move2};
        int[] expected = {move1, move0, move2};

        // When
        SortUtils.sort(array, array.length, TEST_COMPARATOR);

        // Then
        assertArrayEquals(expected, array);
    }
}
