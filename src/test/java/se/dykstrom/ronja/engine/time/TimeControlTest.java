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

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static se.dykstrom.ronja.engine.time.TimeControlType.CLASSIC;
import static se.dykstrom.ronja.engine.time.TimeControlType.INCREMENTAL;
import static se.dykstrom.ronja.engine.time.TimeControlType.SECONDS_PER_MOVE;

public class TimeControlTest {

    @Test
    public void shouldConvertToPgn() {
        final var classic = new TimeControl(30, MINUTES.toMillis(5), 0, CLASSIC);
        assertEquals("30/300", classic.toPgn());
        final var incremental = new TimeControl(0, MINUTES.toMillis(1), 1000, INCREMENTAL);
        assertEquals("60+1", incremental.toPgn());
        final var suddenDeath = new TimeControl(0, 0, SECONDS.toMillis(20), SECONDS_PER_MOVE);
        assertEquals("20", suddenDeath.toPgn());
    }
}
