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

package se.dykstrom.ronja.engine.utils;

import org.junit.Test;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class is for testing class {@code PositionUtils} using JUnit.
 *
 * @author Johan Dykstrom
 * @see PositionUtils
 */
public class PositionUtilsTest extends AbstractTestCase {

    @Test
    public void testIsGameOver() throws Exception {
        assertFalse(PositionUtils.isGameOver(FenParser.parse(FEN_START)));
        assertFalse(PositionUtils.isGameOver(FenParser.parse(FEN_END_GAME_0)));
        assertFalse(PositionUtils.isGameOver(FenParser.parse(FEN_DRAW_1_1)));
        assertFalse(PositionUtils.isGameOver(FenParser.parse(FEN_CHECKMATE_1_2)));
        assertTrue(PositionUtils.isGameOver(FenParser.parse(FEN_DRAW_1_2)));
        assertTrue(PositionUtils.isGameOver(FenParser.parse(FEN_CHECKMATE_1_3)));
        assertTrue(PositionUtils.isGameOver(FenParser.parse(FEN_SCHOLARS_MATE)));
        assertTrue(PositionUtils.isGameOver(FenParser.parse(FEN_ONE_BISHOP)));
    }

    @Test
    public void testIsDraw() throws Exception {
        assertFalse(PositionUtils.isDraw(FenParser.parse(FEN_START)));
        assertFalse(PositionUtils.isDraw(FenParser.parse(FEN_CHECKMATE_1_3)));
        assertFalse(PositionUtils.isDraw(FenParser.parse(FEN_DRAW_1_1)));
        assertTrue(PositionUtils.isDraw(FenParser.parse(FEN_DRAW_1_2)));
        assertTrue(PositionUtils.isDraw(FenParser.parse(FEN_ONE_BISHOP)));
    }

    @Test
    public void testIsCheckMate() throws Exception {
        assertFalse(PositionUtils.isCheckMate(FenParser.parse(FEN_START)));
        assertFalse(PositionUtils.isCheckMate(FenParser.parse(FEN_MIDDLE_GAME_0)));
        assertFalse(PositionUtils.isCheckMate(FenParser.parse(FEN_CHECKMATE_1_0)));
        assertFalse(PositionUtils.isCheckMate(FenParser.parse(FEN_CHECKMATE_1_1)));
        assertFalse(PositionUtils.isCheckMate(FenParser.parse(FEN_CHECKMATE_1_2)));
        assertFalse(PositionUtils.isCheckMate(FenParser.parse(FEN_DRAW_1_2)));

        assertTrue(PositionUtils.isCheckMate(FenParser.parse(FEN_SCHOLARS_MATE)));
        assertTrue(PositionUtils.isCheckMate(FenParser.parse(FEN_CHECKMATE_0)));
        assertTrue(PositionUtils.isCheckMate(FenParser.parse(FEN_CHECKMATE_1_3)));
    }
}
