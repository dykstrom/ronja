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

package se.dykstrom.ronja.engine.time;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Long.parseLong;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static se.dykstrom.ronja.engine.time.TimeControlType.CLASSIC;
import static se.dykstrom.ronja.engine.time.TimeControlType.INCREMENTAL;
import static se.dykstrom.ronja.engine.time.TimeControlType.SECONDS_PER_MOVE;

/**
 * Utility methods related to time.
 *
 * @author Johan Dykstrom
 */
public final class TimeUtils {

    private static final int MOVES_INDEX = 0;
    private static final int BASE_TIME_INDEX = 1;
    private static final int INCREMENT_INDEX = 2;

    // Base time is given as MIN or MIN:SEC, possibly followed by other characters that can be ignored for now
    private static final Pattern BASE_TIME_PATTERN = Pattern.compile("([0-9]+)(:[0-9]+)?.*");

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_TIME;

    private TimeUtils() { }

    /**
     * Formats the time given in milliseconds as HH:mm:ss.SSS.
     */
    public static String formatTime(final long millis) {
        return TIME_FORMATTER.format(LocalTime.ofNanoOfDay(MILLISECONDS.toNanos(millis)));
    }

    /**
     * Parses the given text from the 'st' command, and returns a {@code TimeControl} object.
     */
    public static TimeControl parseStText(final String text) throws ParseException {
        try {
            final long time = SECONDS.toMillis(parseLong(text.strip()));
            return new TimeControl(0, 0, time, SECONDS_PER_MOVE);
        } catch (NumberFormatException nfe) {
            throw new ParseException("invalid time", 0);
        }
    }

    /**
     * Parses the given text from the 'level' command, and returns a {@code TimeControl} object.
     */
    public static TimeControl parseLevelText(final String text) throws ParseException {
        final String[] parts = text.split(" ");
        if (parts.length != 3) {
            throw new ParseException("invalid number of arguments: " + parts.length, 0);
        }

        // Parse number of moves
        final String numberOfMovesStr = parts[MOVES_INDEX].trim();
        final long numberOfMoves;
        try {
            numberOfMoves = parseLong(numberOfMovesStr);
        } catch (NumberFormatException e) {
            throw new ParseException("invalid number of moves: " + numberOfMovesStr, 0);
        }

        // Parse base time
        final String baseTimeStr = parts[BASE_TIME_INDEX].trim();
        long baseTime;
        final Matcher matcher = BASE_TIME_PATTERN.matcher(baseTimeStr);
        if (matcher.matches()) {
            baseTime = getMinutesAsMillis(matcher);
            if (matcher.group(2) != null) {
                baseTime += getSecondsAsMillis(matcher);
            }
        } else {
            throw new ParseException("invalid base time: " + baseTimeStr, 0);
        }

        // Parse time increment
        long increment;
        try {
            increment = getIncrementAsMillis(parts);
        } catch (NumberFormatException nfe) {
            throw new ParseException("invalid time increment", 0);
        }
        return new TimeControl(numberOfMoves, baseTime, increment, (increment == 0) ? CLASSIC : INCREMENTAL);
    }

    private static long getIncrementAsMillis(final String[] parts) {
        return SECONDS.toMillis(parseLong(parts[INCREMENT_INDEX].trim()));
    }

    private static long getSecondsAsMillis(final Matcher matcher) {
        return SECONDS.toMillis(parseLong(matcher.group(2).substring(1)));
    }

    private static long getMinutesAsMillis(final Matcher matcher) {
        return MINUTES.toMillis(parseLong(matcher.group(1)));
    }

    /**
     * Calculates the amount of time available in millis for the next move.
     * The calculation is done differently for the different time control types.
     */
    public static long calculateTimeForNextMove(final TimeControl timeControl, final TimeData timeData) {
        if (timeControl.type() == TimeControlType.SECONDS_PER_MOVE) {
            // Use all available time minus a safety margin
            // The safety margin is 10% of the time up to 500 ms
            long margin = Math.min(timeData.remainingTime() / 10, 500);
            return timeData.remainingTime() - margin;
        } else if (timeControl.type() == TimeControlType.CLASSIC) {
            // If this is the last move before the time control, we need a safety margin
            if (timeData.numberOfMoves() == 1) {
                return timeData.remainingTime() / 2;
            }
            // Divide the remaining time between remaining moves,
            // but allocate more time to early moves
            double partOfMovesLeft = 1.0 * timeData.numberOfMoves() / timeControl.numberOfMoves();
            double factor = 0.2 * partOfMovesLeft + 0.9;
            long evenlyDividedTime = timeData.remainingTime() / timeData.numberOfMoves();
            return (long) (evenlyDividedTime * factor);
        } else { // TimeControlType.INCREMENTAL
            // Remove increment that was added after last move to get remaining base time
            long baseTime = timeData.remainingTime() - timeControl.increment();
            // Use a certain part of the base time, and add increment to that
            return baseTime / 20 + timeControl.increment();
        }
    }

    /**
     * Estimates the time it will take to find the best move at the next search depth,
     * by looking at the previous search times.
     *
     * @param searchTimes A list of search times in millis for previous search depths.
     * @return The estimated time in millis to find the best move next time.
     */
    public static long estimateTimeForNextDepth(List<Long> searchTimes) {
        return searchTimes.get(searchTimes.size() - 1) * 3;
    }
}
