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

package se.dykstrom.ronja.engine.utils;

import org.junit.Test;
import se.dykstrom.ronja.common.model.Position;

import static org.junit.Assert.assertTrue;

public class UtilsTest {

    @Test
    public void shouldConvertBitboardToString() {
        final String board = Utils.toString(
                Position.START.rook & Position.START.black,
                Position.START.bishop & Position.START.white
        );
        assertTrue(board.startsWith("10000001  00000000\n"));
        assertTrue(board.endsWith("00000000  00100100\n"));
    }
}
