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

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import se.dykstrom.ronja.test.AbstractTestCase;

/**
 * This class is for testing class {@code ArrayUtils} using JUnit.
 *
 * @author Johan Dykstrom
 * @see ArrayUtils
 */
public class ArrayUtilsTest extends AbstractTestCase {

    @Test
    public void shouldConvertEmptyList() {
        assertArrayEquals(new int[0], ArrayUtils.toArray(Collections.emptyList()));
    }

    @Test
    public void shouldConvertList() {
        assertArrayEquals(new int[] {1, 2, 3}, ArrayUtils.toArray(Arrays.asList(1, 2, 3)));
    }
}
