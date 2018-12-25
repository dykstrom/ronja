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
import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.*;

/**
 * This class is for testing class {@code PositionUtils} using JUnit.
 *
 * @author Johan Dykstrom
 * @see PositionUtils
 */
public class PositionUtilsTest extends AbstractTestCase {

    private final Game game = new Game(OpeningBook.DEFAULT);

    @Test
    public void testIsGameOver() throws Exception {
        assertFalse(PositionUtils.isGameOver(FenParser.parse(FEN_START), game));
        assertFalse(PositionUtils.isGameOver(FenParser.parse(FEN_END_GAME_0), game));
        assertFalse(PositionUtils.isGameOver(FenParser.parse(FEN_DRAW_1_1), game));
        assertFalse(PositionUtils.isGameOver(FenParser.parse(FEN_CHECKMATE_1_2), game));

        assertTrue(PositionUtils.isGameOver(FenParser.parse(FEN_DRAW_1_2), game));
        assertTrue(PositionUtils.isGameOver(FenParser.parse(FEN_CHECKMATE_1_3), game));
        assertTrue(PositionUtils.isGameOver(FenParser.parse(FEN_SCHOLARS_MATE), game));
        assertTrue(PositionUtils.isGameOver(FenParser.parse(FEN_ONE_BISHOP), game));
    }

    @Test
    public void testIsDraw() throws Exception {
        assertFalse(PositionUtils.isDraw(FenParser.parse(FEN_START), game));
        assertFalse(PositionUtils.isDraw(FenParser.parse(FEN_CHECKMATE_1_3), game));
        assertFalse(PositionUtils.isDraw(FenParser.parse(FEN_DRAW_1_1), game));

        assertTrue(PositionUtils.isDraw(FenParser.parse(FEN_DRAW_1_2), game));
        assertTrue(PositionUtils.isDraw(FenParser.parse(FEN_ONE_BISHOP), game));
    }

    @Test
    public void testIsDrawByThreefoldRepetition() {
        // Set up game
        Game game = new Game(OpeningBook.DEFAULT);
        Position position = game.getPosition();

        // Make moves to repeat position
        game.makeMove(MOVE_G1F3);
        assertNull(PositionUtils.getDrawType(game.getPosition(), game));
        game.makeMove(MOVE_G8F6);
        assertNull(PositionUtils.getDrawType(game.getPosition(), game));
        game.makeMove(MOVE_F3G1);
        assertNull(PositionUtils.getDrawType(game.getPosition(), game));
        game.makeMove(MOVE_F6G8);
        assertNull(PositionUtils.getDrawType(game.getPosition(), game));

        game.makeMove(MOVE_G1F3);
        assertNull(PositionUtils.getDrawType(game.getPosition(), game));
        game.makeMove(MOVE_G8F6);
        assertNull(PositionUtils.getDrawType(game.getPosition(), game));
        game.makeMove(MOVE_F3G1);
        assertNull(PositionUtils.getDrawType(game.getPosition(), game));
        game.makeMove(MOVE_F6G8);

        // Check for draw
        assertEquals("Threefold repetition", PositionUtils.getDrawType(position, game));
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
