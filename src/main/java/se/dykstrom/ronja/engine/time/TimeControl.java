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

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Stores the time control values specified by XBoard using the 'level' or 'st' commands.
 * The fields of this record mean the following:
 *
 * numberOfMoves - The number of moves to make in one time period.
 * baseTime      - The length of the time period in milliseconds.
 * increment     - The time increment per move in milliseconds. For games with a fixed number of seconds per move,
 *                 this is actually not a time increment, but the exact number of seconds available per move.
 * type          - The type of time control: CLASSIC, INCREMENTAL, or SECONDS_PER_MOVE.
 *
 * @author Johan Dykstrom
 */
public record TimeControl(long numberOfMoves, long baseTime, long increment, TimeControlType type) {

    public TimeControl {
        requireNonNull(type);
    }

    /**
     * Returns the PGN representation of this time control.
     */
    public String toPgn() {
        return switch (type) {
            case CLASSIC -> numberOfMoves + "/" + MILLISECONDS.toSeconds(baseTime);
            case INCREMENTAL -> MILLISECONDS.toSeconds(baseTime) + "+" + MILLISECONDS.toSeconds(increment);
            case SECONDS_PER_MOVE -> "" + MILLISECONDS.toSeconds(increment);
        };
    }
}
