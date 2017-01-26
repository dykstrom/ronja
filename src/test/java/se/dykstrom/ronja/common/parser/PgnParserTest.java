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

import org.junit.Before;
import org.junit.Test;
import se.dykstrom.ronja.common.model.Color;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.engine.utils.AppConfig;
import se.dykstrom.ronja.test.AbstractTestCase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * This class is for testing class {@code PgnParser} using JUnit.
 *
 * @author Johan Dykstrom
 * @see PgnParser
 */
public class PgnParserTest extends AbstractTestCase {

    private static final LocalDateTime DATE = LocalDateTime.of(2016, 2, 18, 14, 31, 0);

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm");

    @Before
    public void setUp() throws Exception {
        Game.instance().reset();
    }

    private void setUpGame(String opponent, String result) throws IllegalMoveException {
        Game game = Game.instance();
        game.setStartTime(DATE);
        game.setEngineColor(Color.WHITE);
        game.setOpponent(opponent);
        game.setResult(result);
        game.setMoves(toMoveList(MOVE_E4_C5_NF3));
    }

    @Test
    public void testFormat() throws Exception {
        String opponent = "GNU Chess";
        String shortResult = "1-0";
        String fullResult = shortResult + " {Time forfeit}";
        setUpGame(opponent, fullResult);

        String contents = PgnParser.format(Game.instance());
        assertThat(contents, both(containsString("[Event \"Chess Game\"]"))
                .and(containsString("[Result \"" + shortResult + "\"]"))
                .and(containsString("[White \"" + AppConfig.getEngineName() + "\"]"))
                .and(containsString("[Black \"" + opponent + "\"]"))
                .and(containsString("[Date \"" + DF.format(DATE) + "\"]"))
                .and(containsString("[Time \"" + TF.format(DATE)))
                .and(containsString("1. e4 c5"))
                .and(containsString("2. Nf3"))
                .and(containsString(fullResult)));
        assertThat(contents, not(containsString("[SetUp \"1\"]")));
    }

    @Test
    public void testFormat_NewGame() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        String contents = PgnParser.format(Game.instance());
        assertThat(contents, both(containsString("[Event \"Chess Game\"]"))
                .and(containsString("[Result \"*\"]"))
                .and(containsString("[White \"\"]"))
                .and(containsString("[Black \"" + AppConfig.getEngineName() + "\"]"))
                .and(containsString("[Date \"" + DF.format(now) + "\"]"))
                .and(containsString("[Time \"" + TF.format(now))));
        assertThat(contents, not(containsString("[SetUp \"1\"]")));
    }

    @Test
    public void testFormat_SetBoard() throws Exception {
        String opponent = "GNU Chess";
        String shortResult = "0-1";
        String fullResult = shortResult + " {Black mates}";

        Game game = Game.instance();
        game.setStartTime(DATE);
        game.setOpponent(opponent);
        game.setEngineColor(Color.WHITE);
        game.setPosition(FenParser.parse(FEN_CHECKMATE_1_1));
        game.makeMove(MoveParser.parse("f4c1", game.getPosition()));
        game.makeMove(MoveParser.parse("a1c1", game.getPosition()));
        game.setResult(fullResult);

        String contents = PgnParser.format(Game.instance());
        assertThat(contents, both(containsString("[Event \"Chess Game\"]"))
                .and(containsString("[Result \"" + shortResult + "\"]"))
                .and(containsString("[White \"" + AppConfig.getEngineName() + "\"]"))
                .and(containsString("[Black \"" + opponent + "\"]"))
                .and(containsString("[Date \"" + DF.format(DATE) + "\"]"))
                .and(containsString("[Time \"" + TF.format(DATE)))
                .and(containsString("[SetUp \"1\"]"))
                .and(containsString("[FEN \"" + FEN_CHECKMATE_1_1 + "\"]"))
                .and(containsString("18. Bc1 Rxc1#"))
                .and(containsString(fullResult)));
    }

    @Test
    public void testFormat_BlackStarts() throws Exception {
        String opponent = "GNU Chess";
        String shortResult = "0-1";
        String fullResult = shortResult + " {Black mates}";

        Game game = Game.instance();
        game.setStartTime(DATE);
        game.setOpponent(opponent);
        game.setEngineColor(Color.BLACK);
        game.setPosition(FenParser.parse(FEN_CHECKMATE_1_2));
        game.makeMove(MoveParser.parse("a1c1", game.getPosition()));
        game.setResult(fullResult);

        String contents = PgnParser.format(Game.instance());
        assertThat(contents, both(containsString("[Event \"Chess Game\"]"))
                .and(containsString("[Result \"" + shortResult + "\"]"))
                .and(containsString("[White \"" + opponent + "\"]"))
                .and(containsString("[Black \"" + AppConfig.getEngineName() + "\"]"))
                .and(containsString("[Date \"" + DF.format(DATE) + "\"]"))
                .and(containsString("[Time \"" + TF.format(DATE)))
                .and(containsString("[SetUp \"1\"]"))
                .and(containsString("[FEN \"" + FEN_CHECKMATE_1_2 + "\"]"))
                .and(containsString("18... Rxc1#"))
                .and(containsString(fullResult)));
    }

    @Test
    public void testGetShortResult() throws Exception {
        Game game = Game.instance();
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
}
