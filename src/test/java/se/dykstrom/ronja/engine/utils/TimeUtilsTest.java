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

package se.dykstrom.ronja.engine.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static se.dykstrom.ronja.engine.utils.TimeUtils.formatTime;

/**
 * This class is for testing class {@code TimeUtils} using JUnit.
 *
 * @author Johan Dykstrom
 * @see TimeUtils
 */
public class TimeUtilsTest {

    @Test
    public void testFormatTime() {
        assertEquals("00:00:01", formatTime(1000));
        assertEquals("00:00:01.001", formatTime(1001));
        assertEquals("00:02:05", formatTime(125000));
        assertEquals("03:14:15.927", formatTime(((3 * 60 + 14) * 60 + 15) * 1000 + 927));
    }
}
