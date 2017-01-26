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

package se.dykstrom.ronja.engine.core;

import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Position;

import java.util.Iterator;

/**
 * Interface to be implemented by all move generators.
 */
public interface MoveGenerator {
    /**
     * Returns an iterator that can be used to iterate over all possible pseudo moves for the given {@code position}.
     * The moves may be pre-generated, or lazily on demand.
     *
     * @param position The position for which to generate moves.
     * @return The iterator.
     */
    Iterator<Move> iterator(Position position);
}
