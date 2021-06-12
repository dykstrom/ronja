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

package se.dykstrom.ronja.engine.ui.command;

import org.junit.Before;
import org.junit.Test;
import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.common.model.*;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.engine.time.TimeControl;
import se.dykstrom.ronja.engine.time.TimeData;
import se.dykstrom.ronja.engine.utils.AppConfig;
import se.dykstrom.ronja.test.AbstractTestCase;
import se.dykstrom.ronja.test.ListResponse;
import se.dykstrom.ronja.test.SizeMatcher;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static se.dykstrom.ronja.common.model.Piece.PAWN;
import static se.dykstrom.ronja.common.model.Square.A2_IDX;
import static se.dykstrom.ronja.common.model.Square.A4_IDX;
import static se.dykstrom.ronja.engine.time.TimeControlType.*;
import static se.dykstrom.ronja.test.TestUtils.assertContainsRegex;

/**
 * This class is for testing interface {@code Command} and its implementations using JUnit.
 *
 * @author Johan Dykstrom
 * @see Command
 */
public class CommandTest extends AbstractTestCase {

    private static final TimeControl TC_40_5_0 = new TimeControl(40, 5 * 60 * 1000, 0, CLASSIC);
    private static final TimeControl TC_0_5_30 = new TimeControl(0, 5 * 60 * 1000, 30 * 1000, INCREMENTAL);
    private static final TimeControl TC_0_0_10 = new TimeControl(0, 0, 10 * 1000, SECONDS_PER_MOVE);
    private static final TimeControl TC_0_0_01 = new TimeControl(0, 0, 100, SECONDS_PER_MOVE);

    private static final TimeData TD_40_5_0 = TimeData.from(TC_40_5_0);
    private static final TimeData TD_0_5_30 = TimeData.from(TC_0_5_30);
    private static final TimeData TD_0_0_10 = TimeData.from(TC_0_0_10);
    private static final TimeData TD_0_0_01 = TimeData.from(TC_0_0_01);

    private final Game game = new Game(OpeningBook.DEFAULT);

    @Before
    public void setUp() {
        AppConfig.setGameLogFilename(null);
        game.setTimeControl(TC_0_0_01);
        game.setTimeData(TD_0_0_01);
    }

    // ------------------------------------------------------------------------

    @Test
    public void testBkCommand() {
        ListResponse response = new ListResponse();
        Command command = new BkCommand(null, response, game);
        command.execute();
        assertTrue(response.getList().size() > 1);
        assertContainsRegex("Book moves:", response.getList());
    }

    @Test
    public void testBkCommand_NoMoves() {
        game.makeMove(Move.create(PAWN, A2_IDX, A4_IDX));
        ListResponse response = new ListResponse();
        Command command = new BkCommand(null, response, game);
        command.execute();
        assertTrue(response.getList().size() > 1);
        assertContainsRegex("No book moves", response.getList());
    }

    @Test
    public void testHintCommand() {
        ListResponse response = new ListResponse();
        Command command = new HintCommand(null, response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Hint:", response.getList());
    }

    @Test
    public void testForceCommand() {
        Command command = new ForceCommand(null, new ListResponse(), game);
        command.execute();
        assertTrue(game.getForceMode());
    }

    @Test
    public void testNewCommand() {
        Command command = new NewCommand(null, new ListResponse(), game);
        command.execute();
        assertFalse(game.getForceMode());
        assertEquals(Color.BLACK, game.getEngineColor());
        assertEquals(Position.START, game.getPosition());
        assertEquals("*", game.getResult());
        assertNotNull(game.getStartTime());
        assertEquals(TimeData.from(game.getTimeControl()), game.getTimeData());
    }

    @Test
    public void testNameCommand() throws Exception {
        String name = "GNU Chess";
        Command command = new NameCommand(name, new ListResponse(), game);
        command.execute();
        assertEquals(name, game.getOpponent());
    }

    @Test(expected = InvalidCommandException.class)
    public void testNameCommand_NoArgument() throws Exception {
        Command command = new NameCommand(null, new ListResponse(), game);
        command.execute();
    }

    @Test
    public void testLevelCommand_ConventionalClock() throws Exception {
        Command command = new LevelCommand("40 5 0", new ListResponse(), game);
        command.execute();
        assertEquals(TD_40_5_0, game.getTimeData());
    }

    @Test
    public void testLevelCommand_IncrementalClock() throws Exception {
        Command command = new LevelCommand("0 5 30", new ListResponse(), game);
        command.execute();
        assertEquals(TD_0_5_30, game.getTimeData());
    }

    @Test(expected = InvalidCommandException.class)
    public void testLevelCommand_NoArguments() throws Exception {
        Command command = new LevelCommand(null, new ListResponse(), game);
        command.execute();
    }

    @Test
    public void testLevelCommand_InvalidArguments() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new LevelCommand("foo", response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Error \\(invalid number of arguments\\): level foo", response.getList());
    }

    @Test
    public void testStCommand() throws Exception {
        Command command = new StCommand("10", new ListResponse(), game);
        command.execute();
        assertEquals(TD_0_0_10, game.getTimeData());
    }

    @Test(expected = InvalidCommandException.class)
    public void testStCommand_NoArguments() throws Exception {
        Command command = new StCommand(null, new ListResponse(), game);
        command.execute();
    }

    @Test
    public void testStCommand_InvalidArguments() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new StCommand("foo", response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Error \\(invalid time\\): st foo", response.getList());
    }

    @Test
    public void timeCommandShouldSetClock() throws Exception {
        Command command = new TimeCommand("1500", new ListResponse(), game);
        command.execute();
        assertEquals(15_000, game.getTimeData().getRemainingTime());
    }

    @Test
    public void testOtimCommand() throws Exception {
        final var response = new ListResponse();
        Command command = new OtimCommand("100", response, game);
        command.execute();
        assertTrue(response.getList().isEmpty());
    }

    @Test
    public void testGoCommand() {
        game.setTimeControl(TC_40_5_0);
        game.setTimeData(TD_40_5_0);
        ListResponse response = new ListResponse();
        Command command = new GoCommand(null, response, game);
        TimeData timeDataBefore = game.getTimeData();
        command.execute();
        TimeData timeDataAfter = game.getTimeData();
        assertFalse(game.getForceMode());
        assertEquals(game.getPosition().getActiveColor().flip(), game.getEngineColor());
        assertEquals(1, response.getList().size());
        assertContainsRegex("move (e2e4|d2d4)", response.getList());
        // The remaining time has decreased by some amount, and the number of moves left has decreased by one
        assertTrue(timeDataAfter.getRemainingTime() <= timeDataBefore.getRemainingTime());
        assertEquals(timeDataAfter.getNumberOfMoves(), timeDataBefore.getNumberOfMoves() - 1);
    }

    @Test
    public void testGoCommand_ResultingInCheckmate() throws Exception {
        game.setPosition(FenParser.parse(FEN_CHECKMATE_1_2));
        ListResponse response = new ListResponse();
        Command command = new GoCommand(null, response, game);
        command.execute();
        assertThat(response.getList(), both(hasItems("move a1c1", "0-1 {Black mates}")).and(SizeMatcher.hasSize(2)));
    }

    @Test
    public void testGoCommand_WhenInCheckmate() throws Exception {
        game.setPosition(FenParser.parse(FEN_CHECKMATE_1_3));
        ListResponse response = new ListResponse();
        Command command = new GoCommand(null, response, game);
        command.execute();
        assertThat(response.getList(), both(hasItems("Error (checkmate): go")).and(SizeMatcher.hasSize(1)));
    }

    @Test
    public void testGoCommand_ResultingInDraw() throws Exception {
        game.setPosition(FenParser.parse(FEN_DRAW_1_1));
        ListResponse response = new ListResponse();
        Command command = new GoCommand(null, response, game);
        command.execute();
        assertEquals(2, response.getList().size());
        assertEquals("move f3h1", response.getList().get(0));
        assertEquals("1/2-1/2 {Stalemate}", response.getList().get(1));
    }

    @Test
    public void testGoCommand_WhenInDraw() throws Exception {
        game.setPosition(FenParser.parse(FEN_DRAW_1_2));
        ListResponse response = new ListResponse();
        Command command = new GoCommand(null, response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertEquals("Error (draw): go", response.getList().get(0));
    }

    @Test
    public void testPlayOtherCommand() {
        ListResponse response = new ListResponse();
        Command command = new PlayOtherCommand(null, response, game);
        command.execute();
        assertFalse(game.getForceMode());
        assertEquals(game.getPosition().getActiveColor().flip(), game.getEngineColor());
        assertEquals(0, response.getList().size());
    }

    @Test
    public void testPlayOtherCommand_WhenInDraw() throws Exception {
        game.setPosition(FenParser.parse(FEN_DRAW_1_2));
        ListResponse response = new ListResponse();
        Command command = new PlayOtherCommand(null, response, game);
        command.execute();
        assertThat(response.getList(), both(hasItems("Error (draw): playother")).and(SizeMatcher.hasSize(1)));
    }

    @Test
    public void testPlayOtherCommand_WhenInCheckmate() throws Exception {
        game.setPosition(FenParser.parse(FEN_CHECKMATE_1_3));
        ListResponse response = new ListResponse();
        Command command = new PlayOtherCommand(null, response, game);
        command.execute();
        assertThat(response.getList(), both(hasItems("Error (checkmate): playother")).and(SizeMatcher.hasSize(1)));
    }

    @Test
    public void testPingCommand() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new PingCommand("1", response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertEquals("pong 1", response.getList().get(0));
    }

    @Test(expected = InvalidCommandException.class)
    public void testPingCommand_NoArgument() throws Exception {
        Command command = new PingCommand(null, new ListResponse(), game);
        command.execute();
    }

    @Test
    public void testResultCommand() throws Exception {
        String result = "1-0 {White mates}";
        ListResponse response = new ListResponse();
        Command command = new ResultCommand(result, response, game);
        command.execute();
        assertEquals(0, response.getList().size());
        assertEquals(result, game.getResult());
    }

    @Test
    public void testResultCommand_NoFile() throws Exception {
        String result = "1-0 {White mates}";
        ListResponse response = new ListResponse();
        Command command = new ResultCommand(result, response, game);
        command.execute();
        assertEquals(0, response.getList().size());
        assertEquals(result, game.getResult());
        assertNull(AppConfig.getGameLogFilename()); // How can we assert that no file has been written?
    }

    @Test(expected = InvalidCommandException.class)
    public void testResultCommand_NoArgument() throws Exception {
        Command command = new ResultCommand(null, new ListResponse(), game);
        command.execute();
    }

    @Test
    public void testSetBoardCommand() throws Exception {
        game.setForceMode(true);
        ListResponse response = new ListResponse();
        Command command = new SetBoardCommand(FEN_MIDDLE_GAME_0, response, game);
        command.execute();
        assertEquals(0, response.getList().size());
        assertEquals(FenParser.parse(FEN_MIDDLE_GAME_0), game.getPosition());
        assertEquals(FenParser.parse(FEN_MIDDLE_GAME_0), game.getStartPosition());
    }

    @Test
    public void testSetBoardCommand_ParseException() throws Exception {
        game.setForceMode(true);
        ListResponse response = new ListResponse();
        Command command = new SetBoardCommand("foo", response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal position", response.getList());
        assertEquals(Position.START, game.getPosition()); // Position has not changed
        assertEquals(Position.START, game.getStartPosition());
    }

    @Test
    public void testSetBoardCommand_IllegalPosition() throws Exception {
        game.setForceMode(true);
        ListResponse response = new ListResponse();
        Command command = new SetBoardCommand(FEN_ILLEGAL_0, response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal position", response.getList());
        assertEquals(Position.START, game.getPosition()); // Position has not changed
        assertEquals(Position.START, game.getStartPosition());
    }

    @Test
    public void testSetBoardCommand_NotInForceMode() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new SetBoardCommand(FEN_MIDDLE_GAME_0, response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Not in force mode", response.getList());
        assertEquals(Position.START, game.getPosition()); // Position has not changed
        assertEquals(Position.START, game.getStartPosition());
    }

    @Test
    public void testMovesCommand() throws Exception {
        game.setForceMode(true);
        ListResponse response = new ListResponse();
        new UserMoveCommand("e2e4", response, game).execute();
        new UserMoveCommand("d7d5", response, game).execute();
        new UserMoveCommand("e4d5", response, game).execute();
        new UserMoveCommand("d8d5", response, game).execute();
        new UserMoveCommand("b1c3", response, game).execute();
        new MovesCommand(null, response, game).execute();
        assertContainsRegex("1[.]\\s+e4\\s+d5$", response.getList());
        assertContainsRegex("2[.]\\s+exd5\\s+Qxd5$", response.getList());
        assertContainsRegex("3[.]\\s+Nc3\\s+$", response.getList());
    }

    @Test
    public void testMovesCommand_BlackStarts() throws Exception {
        game.setForceMode(true);
        game.setEngineColor(Color.WHITE);
        game.setPosition(FenParser.parse(FEN_CHECKMATE_1_2));
        ListResponse response = new ListResponse();
        new UserMoveCommand("a1c1", response, game).execute();
        new MovesCommand(null, response, game).execute();
        assertContainsRegex("18[.]\\s+Rxc1#$", response.getList());
    }

    @Test
    public void testUserMoveCommand_AsWhite() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("e2e4", response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("move e7(e5|e6)", response.getList());
        Position position = game.getPosition();
        assertTrue(position.equals(FenParser.parse(FEN_E4_E5)) || position.equals(FenParser.parse(FEN_E4_E6)));
    }

    @Test
    public void testUserMoveCommand_AsBlack() throws Exception {
        game.setPosition(FenParser.parse(FEN_CHECKMATE_1_0));
        game.setEngineColor(Color.WHITE);
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("a8a1", response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertEquals("move f4c1", response.getList().get(0)); // The only move to get out of check
        assertEquals(FenParser.parse(FEN_CHECKMATE_1_2), game.getPosition());
    }

    @Test
    public void testUserMoveCommand_WhenInCheckmate() throws Exception {
        game.setPosition(FenParser.parse(FEN_CHECKMATE_1_3));
        game.setEngineColor(Color.BLACK);
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("g1h1", response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertEquals("Error (checkmate): usermove", response.getList().get(0));
    }

    @Test
    public void testUserMoveCommand_ResultingInCheckmate_ForUser() throws Exception {
        game.setPosition(FenParser.parse(FEN_CHECKMATE_1_2));
        game.setEngineColor(Color.WHITE);
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("a1c1", response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertEquals("0-1 {Black mates}", response.getList().get(0));
    }

    @Test
    public void testUserMoveCommand_ResultingInCheckmate_ForEngine() throws Exception {
        game.setPosition(FenParser.parse(FEN_CHECKMATE_2_7));
        game.setEngineColor(Color.WHITE);
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("e8g8", response, game);
        command.execute();
        assertEquals(2, response.getList().size());
        assertEquals("move h6f7", response.getList().get(0));
        assertEquals("1-0 {White mates}", response.getList().get(1));
    }

    @Test
    public void testUserMoveCommand_WhenInDraw() throws Exception {
        game.setPosition(FenParser.parse(FEN_ONE_BISHOP));
        game.setEngineColor(Color.BLACK);
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("e2e1", response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertEquals("Error (draw): usermove", response.getList().get(0));
    }

    @Test
    public void testUserMoveCommand_ResultingInDraw_ForUser() throws Exception {
        game.setPosition(FenParser.parse(FEN_DRAW_1_1));
        game.setEngineColor(Color.BLACK);
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("f3h1", response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertEquals("1/2-1/2 {Stalemate}", response.getList().get(0));
    }

    @Test
    public void testUserMoveCommand_ResultingInDraw_ForEngine() throws Exception {
        game.setPosition(FenParser.parse(FEN_DRAW_1_0));
        game.setEngineColor(Color.WHITE);
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("h4h1", response, game);
        command.execute();
        assertEquals(2, response.getList().size());
        assertEquals("move f3h1", response.getList().get(0));
        assertEquals("1/2-1/2 {Stalemate}", response.getList().get(1));
    }

    @Test
    public void testUserMoveCommand_ForceMode() throws Exception {
        game.setForceMode(true);
        game.setEngineColor(null);

        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("e2e4", response, game);
        command.execute();
        assertEquals(0, response.getList().size());
        assertEquals(FenParser.parse(FEN_E4), game.getPosition());

        response = new ListResponse();
        command = new UserMoveCommand("e7e5", response, game);
        command.execute();
        assertEquals(0, response.getList().size());
        assertEquals(FenParser.parse(FEN_E4_E5), game.getPosition());

        assertTrue(game.getForceMode());
        assertNull(game.getEngineColor());
    }

    @Test
    public void testUserMoveCommand_IllegalMove_SyntaxError() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("foo", response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal move \\(syntax error\\)", response.getList());
    }

    @Test
    public void testUserMoveCommand_IllegalMove_IllegalCheck() throws Exception {
        game.setPosition(FenParser.parse(FEN_CHECKMATE_1_1));
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("g1h1", response, game); // Still in check
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal move \\(in check after move\\)", response.getList());
    }

    @Test
    public void testUserMoveCommand_IllegalMove_NoPiece() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("e4e5", response, game); // No piece on this square
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal move", response.getList());
    }

    @Test
    public void testUserMoveCommand_IllegalMove_IllegalCapture() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("g1e2", response, game); // Cannot capture your own piece
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal move", response.getList());
    }

    @Test
    public void testUserMoveCommand_IllegalMove_KsCastlingNok() throws Exception {
        game.setPosition(FenParser.parse(FEN_WKC_NOK_K));
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("e1g1", response, game); // Castling not available
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal move", response.getList());
    }

    @Test
    public void testUserMoveCommand_IllegalMove_QsCastlingNok() throws Exception {
        game.setPosition(FenParser.parse(FEN_WQC_NOK_K));
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("e1c1", response, game); // Castling not available
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal move", response.getList());
    }

    @Test(expected = InvalidCommandException.class)
    public void testUserMoveCommand_NoArgument() throws Exception {
        Command command = new UserMoveCommand(null, new ListResponse(), game);
        command.execute();
    }
}
