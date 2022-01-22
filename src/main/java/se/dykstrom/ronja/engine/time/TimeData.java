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

package se.dykstrom.ronja.engine.time;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Keeps track of the current time and number of moves left.
 *
 * @author Johan Dykstrom
 */
public record TimeData(long numberOfMoves, long remainingTime) {

    /**
     * Creates a new time data instance from the given time control. If the time control is
     * {@link TimeControlType#SECONDS_PER_MOVE}, the initial remaining time will be set to
     * the time control increment. Otherwise, the initial remaining time will be set to the
     * time control base time.
     */
    public static TimeData from(TimeControl timeControl) {
        if (timeControl.type() == TimeControlType.SECONDS_PER_MOVE) {
            return new TimeData(0, timeControl.increment());
        } else {
            return new TimeData(timeControl.numberOfMoves(), timeControl.baseTime());
        }
    }

    /**
     * Returns a copy of this time data with remaining time updated.
     *
     * @param remainingTime The new remaining time in milliseconds.
     * @return The updated time data.
     */
    public TimeData withRemainingTime(long remainingTime) {
        return new TimeData(numberOfMoves, remainingTime);
    }

    /**
     * Returns a copy of this time data with number of moves left updated.
     *
     * @param numberOfMoves The new number of moves left.
     * @return The updated time data.
     */
    public TimeData withNumberOfMoves(long numberOfMoves) {
        return new TimeData(numberOfMoves, remainingTime);
    }

    @Override
    public String toString() {
        return numberOfMoves + "/" + MILLISECONDS.toSeconds(remainingTime);
    }
}
