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

package se.dykstrom.ronja.common.parser;

import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Position;

/**
 * A class that can parse moves specified either in CAN or SAN. The actual parsing is delegated
 * to one of the classes {@link CanParser} and {@link SanParser}.
 *
 * @author Johan Dykstrom
 */
public class MoveParser extends AbstractMoveParser {

    /**
     * Parses a move given as a string of characters in the context of a given position.
     * The move can be specified in either CAN or SAN format.
     *
     * @param move A string of characters representing the move.
     * @param position The position when the move is made.
     * @throws IllegalMoveException If the given string does not represent a legal move in the given position.
     */
	public static Move parse(String move, Position position) throws IllegalMoveException {
        if (CanParser.isMove(move)) {
            return CanParser.parse(move, position);
        } else if (SanParser.isMove(move)) {
            return SanParser.parse(move, position);
        } else {
            throw new IllegalMoveException("syntax error");
        }
	}
}
