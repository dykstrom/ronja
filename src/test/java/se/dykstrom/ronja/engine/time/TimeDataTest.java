/*
 * Copyright (C) 2021 Johan Dykstrom
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

package se.dykstrom.ronja.engine.time;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static se.dykstrom.ronja.engine.time.TimeControlType.CLASSIC;

public class TimeDataTest {

    private static final TimeControl TIME_CONTROL = new TimeControl(40, 5 * 60 * 1000L, 0, CLASSIC);

    @Test
    public void shouldCreateTimeData() {
        final var timeData = TimeData.from(TIME_CONTROL);
        assertEquals(40, timeData.getNumberOfMoves());
        assertEquals(5 * 60 * 1000L, timeData.getRemainingTime());
        assertEquals("40/300", timeData.toString());

        final var anotherTimeData = timeData.withRemainingTime(1000);
        assertNotEquals(timeData, anotherTimeData);
        assertEquals("40/1", anotherTimeData.toString());
    }
}