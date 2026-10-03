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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.common.model.Color;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.engine.time.TimeControl;
import se.dykstrom.ronja.engine.utils.AppConfig;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.dykstrom.ronja.engine.time.TimeControlType.INCREMENTAL;

/**
 * This class is for testing class {@code PgnParser} using JUnit.
 *
 * @author Johan Dykstrom
 * @see PgnParser
 */
public class PgnParserTest extends AbstractTestCase {

    private static final LocalDateTime DATE = LocalDateTime.of(2016, 2, 18, 14, 31, 0);

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Game game = new Game(OpeningBook.DEFAULT);

    private void setUpGame(String opponent, String result) throws IllegalMoveException {
        game.setStartTime(DATE);
        game.setEngineColor(Color.WHITE);
        game.setOpponent(opponent);
        game.setResult(result);
        game.setMoves(parseMoves(MOVE_E4_C5_NF3));
    }

    @Test
    public void testFormat() throws Exception {
        String opponent = "GNU Chess";
        String shortResult = "1-0";
        String fullResult = shortResult + " {Time forfeit}";
        setUpGame(opponent, fullResult);

        String contents = PgnParser.format(game);
        assertContains(contents,
                "[Event \"Chess Game\"]",
                "[Result \"" + shortResult + "\"]",
                "[White \"" + AppConfig.getEngineName() + "\"]",
                "[Black \"" + opponent + "\"]",
                "[Date \"" + DF.format(DATE) + "\"]",
                "[Time \"" + TF.format(DATE) + "\"]",
                "[TimeControl \"40/120\"]",
                "1. e4 c5",
                "2. Nf3",
                fullResult);
        assertFalse(contents.contains("[SetUp \"1\"]"), contents);
    }

    @Test
    public void testFormat_NewGame() {
        LocalDateTime now = LocalDateTime.now();
        String contents = PgnParser.format(game);
        assertContains(contents,
                "[Event \"Chess Game\"]",
                "[Result \"*\"]",
                "[White \"\"]",
                "[Black \"" + AppConfig.getEngineName() + "\"]",
                "[Date \"" + DF.format(now) + "\"]",
                "[Time \"" + TF.format(now) + "\"]");
        assertFalse(contents.contains("[SetUp \"1\"]"), contents);
    }

    @Test
    public void testFormat_SetBoard() throws Exception {
        String opponent = "GNU Chess";
        String shortResult = "0-1";
        String fullResult = shortResult + " {Black mates}";

        game.setStartTime(DATE);
        game.setTimeControl(new TimeControl(0, 10 * 1000, 5 * 1000, INCREMENTAL));
        game.setOpponent(opponent);
        game.setEngineColor(Color.WHITE);
        game.setPosition(FenParser.parse(FEN_CHECKMATE_1_1));
        game.makeMove(MoveParser.parse("f4c1", game.getPosition()));
        game.makeMove(MoveParser.parse("a1c1", game.getPosition()));
        game.setResult(fullResult);

        String contents = PgnParser.format(game);
        assertContains(contents,
                "[Event \"Chess Game\"]",
                "[Result \"" + shortResult + "\"]",
                "[White \"" + AppConfig.getEngineName() + "\"]",
                "[Black \"" + opponent + "\"]",
                "[Date \"" + DF.format(DATE) + "\"]",
                "[Time \"" + TF.format(DATE) + "\"]",
                "[TimeControl \"10+5\"]",
                "[SetUp \"1\"]",
                "[FEN \"" + FEN_CHECKMATE_1_1 + "\"]",
                "18. Bc1 Rxc1#",
                fullResult);
    }

    @Test
    public void testFormat_BlackStarts() throws Exception {
        String opponent = "GNU Chess";
        String shortResult = "0-1";
        String fullResult = shortResult + " {Black mates}";

        game.setStartTime(DATE);
        game.setOpponent(opponent);
        game.setEngineColor(Color.BLACK);
        game.setPosition(FenParser.parse(FEN_CHECKMATE_1_2));
        game.makeMove(MoveParser.parse("a1c1", game.getPosition()));
        game.setResult(fullResult);

        String contents = PgnParser.format(game);
        assertContains(contents,
                "[Event \"Chess Game\"]",
                "[Result \"" + shortResult + "\"]",
                "[White \"" + opponent + "\"]",
                "[Black \"" + AppConfig.getEngineName() + "\"]",
                "[Date \"" + DF.format(DATE) + "\"]",
                "[Time \"" + TF.format(DATE) + "\"]",
                "[SetUp \"1\"]",
                "[FEN \"" + FEN_CHECKMATE_1_2 + "\"]",
                "18... Rxc1#",
                fullResult);
    }

    @Test
    public void testGetShortResult() {
        assertEquals("*", PgnParser.getShortResult(game));
        game.setResult("1-0");
        assertEquals("1-0", PgnParser.getShortResult(game));
        game.setResult("0-1");
        assertEquals("0-1", PgnParser.getShortResult(game));
        game.setResult("1/2-1/2");
        assertEquals("1/2-1/2", PgnParser.getShortResult(game));
        game.setResult("1-0 {White mates}");
        assertEquals("1-0", PgnParser.getShortResult(game));
        game.setResult("0-1 {Black mates}");
        assertEquals("0-1", PgnParser.getShortResult(game));
        game.setResult("1/2-1/2 {Draw by repetition}");
        assertEquals("1/2-1/2", PgnParser.getShortResult(game));
    }

    @Test
	public void testEscape() {
        assertEquals("", PgnParser.escape(""));
        assertEquals(" ", PgnParser.escape(" "));
        assertEquals("not escaped", PgnParser.escape("not escaped"));
        assertEquals("with \\\"quotation marks\\\"", PgnParser.escape("with \"quotation marks\""));
        assertEquals("\\\"", PgnParser.escape("\""));
        assertEquals("with \\\\back slashes\\\\", PgnParser.escape("with \\back slashes\\"));
        assertEquals("\\\\", PgnParser.escape("\\"));
	}

    /**
     * Asserts that {@code actual} contains all strings in {@code expected}.
     */
    private static void assertContains(String actual, String... expected) {
        for (String s : expected) {
            assertTrue(actual.contains(s), () -> "Expected to find '" + s + "' in:\n" + actual);
        }
    }
}
