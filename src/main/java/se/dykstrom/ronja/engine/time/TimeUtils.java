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
import static se.dykstrom.ronja.engine.time.TimeControlType.*;

/**
 * Utility methods related to time.
 *
 * @author Johan Dykstrom
 */
public final class TimeUtils {

    private static final long NANOS_PER_MILLI = 1_000_000L;
    private static final long MILLIS_PER_SECOND = 1_000L;
    private static final long MILLIS_PER_MINUTE = 60_000L;

    private static final int MOVES_INDEX = 0;
    private static final int BASE_TIME_INDEX = 1;
    private static final int INCREMENT_INDEX = 2;

    // Base time is given as MIN or MIN:SEC, possibly followed by other characters that can be ignored for now
    private static final Pattern BASE_TIME_PATTERN = Pattern.compile("([0-9]+)(:[0-9]+)?.*");

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_TIME;

    private TimeUtils() { }

    /**
     * Formats the time given in milliseconds as HH:mm:ss.SSS.
     */
    public static String formatTime(long millis) {
        return FORMATTER.format(LocalTime.ofNanoOfDay(millis * NANOS_PER_MILLI));
    }

    /**
     * Parses the given text from the 'st' command, and returns a {@code TimeControl} object.
     */
    public static TimeControl parseStText(String text) throws ParseException {
        long time;
        try {
            time = parseLong(text.trim()) * MILLIS_PER_SECOND;
        } catch (NumberFormatException nfe) {
            throw new ParseException("invalid time", 0);
        }
        return new TimeControl(0, 0, time, SECONDS_PER_MOVE);
    }

    /**
     * Parses the given text from the 'level' command, and returns a {@code TimeControl} object.
     */
    public static TimeControl parseLevelText(String text) throws ParseException {
        String[] parts = text.split(" ");
        if (parts.length != 3) {
            throw new ParseException("invalid number of arguments", 0);
        }

        // Parse number of moves
        long numberOfMoves;
        try {
            numberOfMoves = parseLong(parts[MOVES_INDEX].trim());
        } catch (NumberFormatException nfe) {
            throw new ParseException("invalid number of moves", 0);
        }

        // Parse base time
        long baseTime;
        Matcher matcher = BASE_TIME_PATTERN.matcher(parts[BASE_TIME_INDEX].trim());
        if (matcher.matches()) {
            baseTime = getMinutesAsMillis(matcher);
            if (matcher.group(2) != null) {
                baseTime += getSecondsAsMillis(matcher);
            }
        } else {
            throw new ParseException("invalid base time", 0);
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

    private static long getIncrementAsMillis(String[] parts) {
        return parseLong(parts[INCREMENT_INDEX].trim()) * MILLIS_PER_SECOND;
    }

    private static long getSecondsAsMillis(Matcher matcher) {
        return parseLong(matcher.group(2).substring(1)) * MILLIS_PER_SECOND;
    }

    private static long getMinutesAsMillis(Matcher matcher) {
        return parseLong(matcher.group(1)) * MILLIS_PER_MINUTE;
    }

    /**
     * Calculates the amount of time available in millis for the next move.
     * The calculation is done differently for the different time control types.
     */
    public static long calculateTimeForNextMove(TimeControl timeControl, TimeData timeData) {
        if (timeControl.getType() == TimeControlType.SECONDS_PER_MOVE) {
            // Use all available time minus a safety margin
            // The safety margin is 10% of the time up to 500 ms
            long margin = Math.min(timeData.getRemainingTime() / 10, 500);
            return timeData.getRemainingTime() - margin;
        } else if (timeControl.getType() == TimeControlType.CLASSIC) {
            // Divide the remaining time between remaining moves,
            // but allocate more time to moves early in the game
            double partOfMovesLeft = 1.0 * timeData.getNumberOfMoves() / timeControl.getNumberOfMoves();
            double factor = 0.6 * partOfMovesLeft + 0.7;
            long evenlyDividedTime = timeData.getRemainingTime() / timeData.getNumberOfMoves();
            return (long) (evenlyDividedTime * factor);
        } else { // TimeControlType.INCREMENTAL
            // Remove increment that was added after last move to get remaining base time
            long baseTime = timeData.getRemainingTime() - timeControl.getIncrement();
            // Use a certain part of the base time, and add increment to that
            return baseTime / 20 + timeControl.getIncrement();
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
