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

package se.dykstrom.ronja.engine.ui;

import org.junit.Test;
import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.engine.ui.command.*;
import se.dykstrom.ronja.test.ListResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.dykstrom.ronja.test.TestUtils.assertContainsRegex;

/**
 * This class is for testing class {@code CommandFactory} using JUnit.
 *
 * @author Johan Dykstrom
 * @see CommandFactory
 */
public class CommandFactoryTest {

    private final ListResponse response = new ListResponse();

    private final Game game = new Game(OpeningBook.DEFAULT);

    // ------------------------------------------------------------------------

    @Test
    public void testHintCommand() {
        Command command = CommandFactory.create(HintCommand.NAME, null, response, game);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Hint:", response.getList());
    }

    @Test
    public void testNameCommand() {
        String opponent = "GNU Chess";
        Command command = CommandFactory.create(NameCommand.NAME, opponent, response, game);
        command.execute();
        assertEquals(0, response.getList().size());
        assertEquals(opponent, game.getOpponent());
    }

    @Test
    public void testInvalidCommand() {
        Command command = CommandFactory.create("foo", null, response, game);
        assertTrue(command instanceof InvalidCommand);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Error \\(unknown command\\):", response.getList());
    }

    @Test
    public void testAllCommands() {
        assertTrue(CommandFactory.create(AcceptedCommand.NAME, "", response, game) instanceof AcceptedCommand);
        assertTrue(CommandFactory.create(BkCommand.NAME, "", response, game) instanceof BkCommand);
        assertTrue(CommandFactory.create(BoardCommand.NAME, "", response, game) instanceof BoardCommand);
        assertTrue(CommandFactory.create(ComputerCommand.NAME, "", response, game) instanceof ComputerCommand);
        assertTrue(CommandFactory.create(EasyCommand.NAME, "", response, game) instanceof EasyCommand);
        assertTrue(CommandFactory.create(ForceCommand.NAME, "", response, game) instanceof ForceCommand);
        assertTrue(CommandFactory.create(GoCommand.NAME, "", response, game) instanceof GoCommand);
        assertTrue(CommandFactory.create(HardCommand.NAME, "", response, game) instanceof HardCommand);
        assertTrue(CommandFactory.create(HelpCommand.NAME, "", response, game) instanceof HelpCommand);
        assertTrue(CommandFactory.create(HintCommand.NAME, "", response, game) instanceof HintCommand);
        assertTrue(CommandFactory.create(LevelCommand.NAME, "", response, game) instanceof LevelCommand);
        assertTrue(CommandFactory.create(MovesCommand.NAME, "", response, game) instanceof MovesCommand);
        assertTrue(CommandFactory.create(NameCommand.NAME, "", response, game) instanceof NameCommand);
        assertTrue(CommandFactory.create(NewCommand.NAME, "", response, game) instanceof NewCommand);
        assertTrue(CommandFactory.create(NoPostCommand.NAME, "", response, game) instanceof NoPostCommand);
        assertTrue(CommandFactory.create(OtimCommand.NAME, "1", response, game) instanceof OtimCommand);
        assertTrue(CommandFactory.create(PingCommand.NAME, "", response, game) instanceof PingCommand);
        assertTrue(CommandFactory.create(PlayOtherCommand.NAME, "", response, game) instanceof PlayOtherCommand);
        assertTrue(CommandFactory.create(PostCommand.NAME, "", response, game) instanceof PostCommand);
        assertTrue(CommandFactory.create(ProtoverCommand.NAME, "1", response, game) instanceof ProtoverCommand);
        assertTrue(CommandFactory.create(QuitCommand.NAME, "", response, game) instanceof QuitCommand);
        assertTrue(CommandFactory.create(RandomCommand.NAME, "", response, game) instanceof RandomCommand);
        assertTrue(CommandFactory.create(RejectedCommand.NAME, "", response, game) instanceof RejectedCommand);
        assertTrue(CommandFactory.create(ResultCommand.NAME, "", response, game) instanceof ResultCommand);
        assertTrue(CommandFactory.create(SetBoardCommand.NAME, "", response, game) instanceof SetBoardCommand);
        assertTrue(CommandFactory.create(StCommand.NAME, "", response, game) instanceof StCommand);
        assertTrue(CommandFactory.create(TimeCommand.NAME, "1", response, game) instanceof TimeCommand);
        assertTrue(CommandFactory.create(UserMoveCommand.NAME, "", response, game) instanceof UserMoveCommand);
        assertTrue(CommandFactory.create(XBoardCommand.NAME, "", response, game) instanceof XBoardCommand);
    }
}
