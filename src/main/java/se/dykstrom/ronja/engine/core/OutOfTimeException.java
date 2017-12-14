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

    private final Integer move;

    OutOfTimeException(Integer move) {
        this.move = move;
    }

    /**
     * Returns the move included with this exception.
     */
    public Integer getMove() {
        return move;
    }
}
