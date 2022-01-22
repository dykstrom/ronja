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

package se.dykstrom.ronja.engine.core;

/**
 * Thrown when a search must be aborted because we are out of time.
 *
 * @author Johan Dykstrom
 */
class OutOfTimeException extends RuntimeException {

    private final int moveIndex;
    private final int numberOfMoves;
    private final long averageTime;
    private final long remainingTime;

    public OutOfTimeException(final int moveIndex,
                              final int numberOfMoves,
                              final long averageTime,
                              final long remainingTime) {
        this.moveIndex = moveIndex;
        this.numberOfMoves = numberOfMoves;
        this.averageTime = averageTime;
        this.remainingTime = remainingTime;
    }

    public int getMoveIndex() {
        return moveIndex;
    }

    public int getNumberOfMoves() {
        return numberOfMoves;
    }

    public long getAverageTime() {
        return averageTime;
    }

    public long getRemainingTime() {
        return remainingTime;
    }
}
