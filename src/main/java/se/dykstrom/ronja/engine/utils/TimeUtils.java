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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility methods related to time.
 *
 * @author Johan Dykstrom
 */
public final class TimeUtils {

    private static final long NANOS_PER_MILLI = 1_000_000L;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_TIME;

    private TimeUtils() { }

    /**
     * Formats the time given in milliseconds as HH:mm:ss.SSS.
     */
    public static String formatTime(long millis) {
        return FORMATTER.format(LocalTime.ofNanoOfDay(millis * NANOS_PER_MILLI));
    }
}
