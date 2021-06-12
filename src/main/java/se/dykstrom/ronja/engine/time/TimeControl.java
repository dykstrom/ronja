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

import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Stores the time control values specified by XBoard using the 'level' or 'st' commands.
 *
 * @author Johan Dykstrom
 */
public class TimeControl {

    /** The number of moves to make in one time period. */
    private final long numberOfMoves;

    /** The length of the time period in milliseconds. */
    private final long baseTime;

    /**
     * The time increment per move in milliseconds. For games with a fixed number of seconds per move,
     * this is actually not a time increment, but the exact number of seconds available per move.
     */
    private final long increment;

    /** The type of time control: CLASSIC, INCREMENTAL, or SECONDS_PER_MOVE. */
    private final TimeControlType type;

    public TimeControl(final long numberOfMoves, final long baseTime, final long increment, final TimeControlType type) {
        this.numberOfMoves = numberOfMoves;
        this.baseTime = baseTime;
        this.increment = increment;
        this.type = requireNonNull(type);
    }

    @Override
    public String toString() {
        return "[" + numberOfMoves + " " + baseTime + " " + increment + " " + type + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeControl that = (TimeControl) o;
        return numberOfMoves == that.numberOfMoves && baseTime == that.baseTime && increment == that.increment && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfMoves, baseTime, increment, type);
    }

    public long getNumberOfMoves() {
        return numberOfMoves;
    }

    public long getBaseTime() {
        return baseTime;
    }

    public long getIncrement() {
        return increment;
    }

    public TimeControlType getType() {
        return type;
    }

    /**
     * Returns the PGN representation of this time control.
     */
    public String toPgn() {
        switch (type) {
            case CLASSIC:
                return numberOfMoves + "/" + MILLISECONDS.toSeconds(baseTime);
            case INCREMENTAL:
                return MILLISECONDS.toSeconds(baseTime) + "+" + MILLISECONDS.toSeconds(increment);
            case SECONDS_PER_MOVE:
                return "" + MILLISECONDS.toSeconds(increment);
            default:
                throw new IllegalStateException("unknown time control type: " + type);
        }
    }
}
