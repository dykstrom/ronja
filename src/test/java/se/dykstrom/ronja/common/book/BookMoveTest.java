/*
 * Copyright (C) 2021 Johan Dykstrom
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

package se.dykstrom.ronja.common.book;

import org.junit.Test;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Piece;
import se.dykstrom.ronja.common.model.Square;

import static org.junit.Assert.*;

public class BookMoveTest {

    @Test
    public void shouldCreateMove() {
        final BookMove bookMove = new BookMove(Move.create(Piece.PAWN, Square.E2_IDX, Square.E4_IDX), 100);
        assertEquals("e2e4/100", bookMove.toString());

        final BookMove anotherMove = bookMove.withWeight(50);
        assertNotEquals(bookMove, anotherMove);
        assertEquals("e2e4/50", anotherMove.toString());
    }
}
