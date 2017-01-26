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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.common.model.*;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.engine.utils.AppConfig;
import se.dykstrom.ronja.test.AbstractTestCase;
import se.dykstrom.ronja.test.ListResponse;
import se.dykstrom.ronja.test.SizeMatcher;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;
import static se.dykstrom.ronja.test.TestUtils.assertContainsRegex;

/**
 * This class is for testing interface {@code Command} and its implementations using JUnit.
 *
 * @author Johan Dykstrom
 * @see Command
 */
public class CommandTest extends AbstractTestCase {

    @Before
    public void setUp() throws Exception {
        AppConfig.setSearchDepth(1);
        AppConfig.setGameLogFilename(null);
        Game.instance().reset();
        Game.instance().setBook(OpeningBook.DEFAULT);
    }

    // ------------------------------------------------------------------------

    @Test
    public void testBkCommand() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new BkCommand(null, response);
        command.execute();
        assertTrue(response.getList().size() > 1);
        assertContainsRegex("Book moves:", response.getList());
    }

    @Test
    public void testBkCommand_NoMoves() throws Exception {
        Game.instance().makeMove(Move.of(Piece.PAWN, Square.A2, Square.A4, null, false, false));
        ListResponse response = new ListResponse();
        Command command = new BkCommand(null, response);
        command.execute();
        assertTrue(response.getList().size() > 1);
        assertContainsRegex("No book moves", response.getList());
    }

    @Test
    public void testHintCommand() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new HintCommand(null, response);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Hint:", response.getList());
    }

    @Test
    public void testForceCommand() throws Exception {
        Command command = new ForceCommand(null, null);
        command.execute();
        assertTrue(Game.instance().getForceMode());
    }

    @Test
    public void testNewCommand() throws Exception {
        Command command = new NewCommand(null, null);
        command.execute();
        assertFalse(Game.instance().getForceMode());
        Assert.assertEquals(Color.BLACK, Game.instance().getEngineColor());
        assertEquals(Position.START, Game.instance().getPosition());
        assertEquals("*", Game.instance().getResult());
        assertNotNull(Game.instance().getStartTime());
    }

    @Test
    public void testNameCommand() throws Exception {
        String name = "GNU Chess";
        Command command = new NameCommand(name, null);
        command.execute();
        assertEquals(name, Game.instance().getOpponent());
    }

    @Test(expected = InvalidCommandException.class)
    public void testNameCommand_NoArgument() throws Exception {
        Command command = new NameCommand(null, new ListResponse());
        command.execute();
    }

    @Test
    public void testGoCommand() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new GoCommand(null, response);
        command.execute();
        assertFalse(Game.instance().getForceMode());
        assertEquals(Game.instance().getPosition().getActiveColor().flip(), Game.instance().getEngineColor());
        assertEquals(1, response.getList().size());
        assertContainsRegex("move (e2e4|d2d4)", response.getList());
    }

    @Test
    public void testGoCommand_ResultingInCheckmate() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_CHECKMATE_1_2));
        ListResponse response = new ListResponse();
        Command command = new GoCommand(null, response);
        command.execute();
        assertThat(response.getList(), both(hasItems("move a1c1", "0-1 {Black mates}")).and(SizeMatcher.hasSize(2)));
    }

    @Test
    public void testGoCommand_WhenInCheckmate() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_CHECKMATE_1_3));
        ListResponse response = new ListResponse();
        Command command = new GoCommand(null, response);
        command.execute();
        assertThat(response.getList(), both(hasItems("Error (checkmate): go")).and(SizeMatcher.hasSize(1)));
    }

    @Test
    public void testGoCommand_ResultingInDraw() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_DRAW_1_1));
        ListResponse response = new ListResponse();
        Command command = new GoCommand(null, response);
        command.execute();
        assertEquals(2, response.getList().size());
        assertEquals("move f3h1", response.getList().get(0));
        assertEquals("1/2-1/2 {Stalemate}", response.getList().get(1));
    }

    @Test
    public void testGoCommand_WhenInDraw() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_DRAW_1_2));
        ListResponse response = new ListResponse();
        Command command = new GoCommand(null, response);
        command.execute();
        assertEquals(1, response.getList().size());
        assertEquals("Error (draw): go", response.getList().get(0));
    }

    @Test
    public void testPlayOtherCommand() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new PlayOtherCommand(null, response);
        command.execute();
        assertFalse(Game.instance().getForceMode());
        assertEquals(Game.instance().getPosition().getActiveColor().flip(), Game.instance().getEngineColor());
        assertEquals(0, response.getList().size());
    }

    @Test
    public void testPlayOtherCommand_WhenInDraw() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_DRAW_1_2));
        ListResponse response = new ListResponse();
        Command command = new PlayOtherCommand(null, response);
        command.execute();
        assertThat(response.getList(), both(hasItems("Error (draw): playother")).and(SizeMatcher.hasSize(1)));
    }

    @Test
    public void testPlayOtherCommand_WhenInCheckmate() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_CHECKMATE_1_3));
        ListResponse response = new ListResponse();
        Command command = new PlayOtherCommand(null, response);
        command.execute();
        assertThat(response.getList(), both(hasItems("Error (checkmate): playother")).and(SizeMatcher.hasSize(1)));
    }

    @Test
    public void testPingCommand() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new PingCommand("1", response);
        command.execute();
        assertEquals(1, response.getList().size());
        assertEquals("pong 1", response.getList().get(0));
    }

    @Test(expected = InvalidCommandException.class)
    public void testPingCommand_NoArgument() throws Exception {
        Command command = new PingCommand(null, new ListResponse());
        command.execute();
    }

    @Test
    public void testResultCommand() throws Exception {
        String result = "1-0 {White mates}";
        ListResponse response = new ListResponse();
        Command command = new ResultCommand(result, response);
        command.execute();
        assertEquals(0, response.getList().size());
        assertEquals(result, Game.instance().getResult());
    }

    @Test
    public void testResultCommand_NoFile() throws Exception {
        String result = "1-0 {White mates}";
        ListResponse response = new ListResponse();
        Command command = new ResultCommand(result, response);
        command.execute();
        assertEquals(0, response.getList().size());
        assertEquals(result, Game.instance().getResult());
        assertNull(AppConfig.getGameLogFilename()); // How can we assert that no file has been written?
    }

    @Test(expected = InvalidCommandException.class)
    public void testResultCommand_NoArgument() throws Exception {
        Command command = new ResultCommand(null, new ListResponse());
        command.execute();
    }

    @Test
    public void testSetBoardCommand() throws Exception {
        Game.instance().setForceMode(true);
        ListResponse response = new ListResponse();
        Command command = new SetBoardCommand(FEN_MIDDLE_GAME_0, response);
        command.execute();
        assertEquals(0, response.getList().size());
        assertEquals(FenParser.parse(FEN_MIDDLE_GAME_0), Game.instance().getPosition());
        assertEquals(FenParser.parse(FEN_MIDDLE_GAME_0), Game.instance().getStartPosition());
    }

    @Test
    public void testSetBoardCommand_ParseException() throws Exception {
        Game.instance().setForceMode(true);
        ListResponse response = new ListResponse();
        Command command = new SetBoardCommand("foo", response);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal position", response.getList());
        assertEquals(Position.START, Game.instance().getPosition()); // Position has not changed
        assertEquals(Position.START, Game.instance().getStartPosition());
    }

    @Test
    public void testSetBoardCommand_IllegalPosition() throws Exception {
        Game.instance().setForceMode(true);
        ListResponse response = new ListResponse();
        Command command = new SetBoardCommand(FEN_ILLEGAL_0, response);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal position", response.getList());
        assertEquals(Position.START, Game.instance().getPosition()); // Position has not changed
        assertEquals(Position.START, Game.instance().getStartPosition());
    }

    @Test
    public void testSetBoardCommand_NotInForceMode() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new SetBoardCommand(FEN_MIDDLE_GAME_0, response);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Not in force mode", response.getList());
        assertEquals(Position.START, Game.instance().getPosition()); // Position has not changed
        assertEquals(Position.START, Game.instance().getStartPosition());
    }

    @Test
    public void testMovesCommand() throws Exception {
        Game.instance().setForceMode(true);
        ListResponse response = new ListResponse();
        new UserMoveCommand("e2e4", response).execute();
        new UserMoveCommand("d7d5", response).execute();
        new UserMoveCommand("e4d5", response).execute();
        new UserMoveCommand("d8d5", response).execute();
        new UserMoveCommand("b1c3", response).execute();
        new MovesCommand(null, response).execute();
        assertContainsRegex("1[.]\\s+e4\\s+d5$", response.getList());
        assertContainsRegex("2[.]\\s+exd5\\s+Qxd5$", response.getList());
        assertContainsRegex("3[.]\\s+Nc3\\s+$", response.getList());
    }

    @Test
    public void testMovesCommand_BlackStarts() throws Exception {
        Game game = Game.instance();
        game.setForceMode(true);
        game.setEngineColor(Color.WHITE);
        game.setPosition(FenParser.parse(FEN_CHECKMATE_1_2));
        ListResponse response = new ListResponse();
        new UserMoveCommand("a1c1", response).execute();
        new MovesCommand(null, response).execute();
        assertContainsRegex("18[.]\\s+Rxc1#$", response.getList());
    }

    @Test
    public void testUserMoveCommand_AsWhite() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("e2e4", response);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("move e7(e5|e6)", response.getList());
        Position position = Game.instance().getPosition();
        assertTrue(position.equals(FenParser.parse(FEN_E4_E5)) || position.equals(FenParser.parse(FEN_E4_E6)));
    }

    @Test
    public void testUserMoveCommand_AsBlack() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_CHECKMATE_1_0));
        Game.instance().setEngineColor(Color.WHITE);
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("a8a1", response);
        command.execute();
        assertEquals(1, response.getList().size());
        assertEquals("move f4c1", response.getList().get(0)); // The only move to get out of check
        Assert.assertEquals(FenParser.parse(FEN_CHECKMATE_1_2), Game.instance().getPosition());
    }

    @Test
    public void testUserMoveCommand_WhenInCheckmate() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_CHECKMATE_1_3));
        Game.instance().setEngineColor(Color.BLACK);
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("g1h1", response);
        command.execute();
        assertEquals(1, response.getList().size());
        assertEquals("Error (checkmate): usermove", response.getList().get(0));
    }

    @Test
    public void testUserMoveCommand_ResultingInCheckmate_ForUser() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_CHECKMATE_1_2));
        Game.instance().setEngineColor(Color.WHITE);
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("a1c1", response);
        command.execute();
        assertEquals(1, response.getList().size());
        assertEquals("0-1 {Black mates}", response.getList().get(0));
    }

    @Test
    public void testUserMoveCommand_ResultingInCheckmate_ForEngine() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_CHECKMATE_2_7));
        Game.instance().setEngineColor(Color.WHITE);
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("e8g8", response);
        command.execute();
        assertEquals(2, response.getList().size());
        assertEquals("move h6f7", response.getList().get(0));
        assertEquals("1-0 {White mates}", response.getList().get(1));
    }

    @Test
    public void testUserMoveCommand_WhenInDraw() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_ONE_BISHOP));
        Game.instance().setEngineColor(Color.BLACK);
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("e2e1", response);
        command.execute();
        assertEquals(1, response.getList().size());
        assertEquals("Error (draw): usermove", response.getList().get(0));
    }

    @Test
    public void testUserMoveCommand_ResultingInDraw_ForUser() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_DRAW_1_1));
        Game.instance().setEngineColor(Color.BLACK);
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("f3h1", response);
        command.execute();
        assertEquals(1, response.getList().size());
        assertEquals("1/2-1/2 {Stalemate}", response.getList().get(0));
    }

    @Test
    public void testUserMoveCommand_ResultingInDraw_ForEngine() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_DRAW_1_0));
        Game.instance().setEngineColor(Color.WHITE);
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("h4h1", response);
        command.execute();
        assertEquals(2, response.getList().size());
        assertEquals("move f3h1", response.getList().get(0));
        assertEquals("1/2-1/2 {Stalemate}", response.getList().get(1));
    }

    @Test
    public void testUserMoveCommand_ForceMode() throws Exception {
        Game.instance().setForceMode(true);
        Game.instance().setEngineColor(null);

        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("e2e4", response);
        command.execute();
        assertEquals(0, response.getList().size());
        Assert.assertEquals(FenParser.parse(FEN_E4), Game.instance().getPosition());

        response = new ListResponse();
        command = new UserMoveCommand("e7e5", response);
        command.execute();
        assertEquals(0, response.getList().size());
        Assert.assertEquals(FenParser.parse(FEN_E4_E5), Game.instance().getPosition());

        assertTrue(Game.instance().getForceMode());
        assertNull(Game.instance().getEngineColor());
    }

    @Test
    public void testUserMoveCommand_IllegalMove_SyntaxError() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("foo", response);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal move \\(syntax error\\)", response.getList());
    }

    @Test
    public void testUserMoveCommand_IllegalMove_IllegalCheck() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_CHECKMATE_1_1));
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("g1h1", response); // Still in check
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal move \\(in check after move\\)", response.getList());
    }

    @Test
    public void testUserMoveCommand_IllegalMove_NoPiece() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("e4e5", response); // No piece on this square
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal move", response.getList());
    }

    @Test
    public void testUserMoveCommand_IllegalMove_IllegalCapture() throws Exception {
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("g1e2", response); // Cannot capture your own piece
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal move", response.getList());
    }

    @Test
    public void testUserMoveCommand_IllegalMove_KsCastlingNok() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_WKC_NOK_K));
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("e1g1", response); // Castling not available
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal move", response.getList());
    }

    @Test
    public void testUserMoveCommand_IllegalMove_QsCastlingNok() throws Exception {
        Game.instance().setPosition(FenParser.parse(FEN_WQC_NOK_K));
        ListResponse response = new ListResponse();
        Command command = new UserMoveCommand("e1c1", response); // Castling not available
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Illegal move", response.getList());
    }

    @Test(expected = InvalidCommandException.class)
    public void testUserMoveCommand_NoArgument() throws Exception {
        Command command = new UserMoveCommand(null, new ListResponse());
        command.execute();
    }
}
