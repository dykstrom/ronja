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

import se.dykstrom.ronja.common.model.Game;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
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

    private static final Logger TLOG = Logger.getLogger(TimeUtils.class.getName());

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
     * Updates the time data in the {@code game} after a move, taking into account the {@code usedTime},
     * and the type of the time control.
     *
     * @param game The game state.
     * @param usedTime The time used for this move in milliseconds.
     */
    public static void updateTimeDataAfterMove(Game game, long usedTime) {
        TLOG.info("Old time data: " + game.getTimeData());
        TimeControl timeControl = game.getTimeControl();
        if (timeControl.getType() == TimeControlType.SECONDS_PER_MOVE) {
            game.setTimeData(TimeData.from(timeControl));
        } else if (timeControl.getType() == TimeControlType.CLASSIC) {
            TimeData timeData = game.getTimeData();
            long remainingTime = timeData.getRemainingTime() - usedTime;
            long numberOfMoves = timeData.getNumberOfMoves() - 1;
            // If we have reached the time control, reset number of moves and add time
            if (numberOfMoves == 0) {
                numberOfMoves = timeControl.getNumberOfMoves();
                remainingTime += timeControl.getBaseTime();
            }
            game.setTimeData(timeData.withRemainingTime(remainingTime).withNumberOfMoves(numberOfMoves));
        } else { // TimeControlType.INCREMENTAL
            TimeData timeData = game.getTimeData();
            long remainingTime = timeData.getRemainingTime() - usedTime + timeControl.getIncrement();
            game.setTimeData(timeData.withRemainingTime(remainingTime));
        }
        TLOG.info("New time data: " + game.getTimeData());
    }

    /**
     * Calculates the amount of time available in millis for the next move.
     * The calculation is done differently for the different time control types.
     */
    public static long calculateTimeForNextMove(TimeControl timeControl, TimeData timeData) {
        if (timeControl.getType() == TimeControlType.SECONDS_PER_MOVE) {
            // Use all available time
            return timeData.getRemainingTime();
        } else if (timeControl.getType() == TimeControlType.CLASSIC) {
            // Divide remaining time evenly between remaining moves
            return timeData.getRemainingTime() / timeData.getNumberOfMoves();
        } else { // TimeControlType.INCREMENTAL
            // Always assume there are 40 moves left
            return timeData.getRemainingTime() / 40;
        }
    }
}
