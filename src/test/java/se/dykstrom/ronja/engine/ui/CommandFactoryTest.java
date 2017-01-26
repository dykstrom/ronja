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

import org.junit.Before;
import org.junit.Test;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.engine.ui.command.*;
import se.dykstrom.ronja.engine.utils.AppConfig;
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

    @Before
    public void setUp() throws Exception {
        AppConfig.setSearchDepth(1);
        Game.instance().reset();
    }

    // ------------------------------------------------------------------------

    @Test
    public void testHintCommand() throws Exception {
        Command command = CommandFactory.create(HintCommand.NAME, null, response);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Hint:", response.getList());
    }

    @Test
    public void testNameCommand() throws Exception {
        String opponent = "GNU Chess";
        Command command = CommandFactory.create(NameCommand.NAME, opponent, response);
        command.execute();
        assertEquals(0, response.getList().size());
        assertEquals(opponent, Game.instance().getOpponent());
    }

    @Test
    public void testInvalidCommand() throws Exception {
        Command command = CommandFactory.create("foo", null, response);
        assertTrue(command instanceof InvalidCommand);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Error \\(unknown command\\):", response.getList());
    }

    @Test
    public void testAllCommands() throws Exception {
        assertTrue(CommandFactory.create(AcceptedCommand.NAME, "", response) instanceof AcceptedCommand);
        assertTrue(CommandFactory.create(BkCommand.NAME, "", response) instanceof BkCommand);
        assertTrue(CommandFactory.create(BoardCommand.NAME, "", response) instanceof BoardCommand);
        assertTrue(CommandFactory.create(ComputerCommand.NAME, "", response) instanceof ComputerCommand);
        assertTrue(CommandFactory.create(EasyCommand.NAME, "", response) instanceof EasyCommand);
        assertTrue(CommandFactory.create(ForceCommand.NAME, "", response) instanceof ForceCommand);
        assertTrue(CommandFactory.create(GoCommand.NAME, "", response) instanceof GoCommand);
        assertTrue(CommandFactory.create(HardCommand.NAME, "", response) instanceof HardCommand);
        assertTrue(CommandFactory.create(HelpCommand.NAME, "", response) instanceof HelpCommand);
        assertTrue(CommandFactory.create(HintCommand.NAME, "", response) instanceof HintCommand);
        assertTrue(CommandFactory.create(LevelCommand.NAME, "", response) instanceof LevelCommand);
        assertTrue(CommandFactory.create(MovesCommand.NAME, "", response) instanceof MovesCommand);
        assertTrue(CommandFactory.create(NameCommand.NAME, "", response) instanceof NameCommand);
        assertTrue(CommandFactory.create(NewCommand.NAME, "", response) instanceof NewCommand);
        assertTrue(CommandFactory.create(NoPostCommand.NAME, "", response) instanceof NoPostCommand);
        assertTrue(CommandFactory.create(PingCommand.NAME, "", response) instanceof PingCommand);
        assertTrue(CommandFactory.create(PlayOtherCommand.NAME, "", response) instanceof PlayOtherCommand);
        assertTrue(CommandFactory.create(PostCommand.NAME, "", response) instanceof PostCommand);
        assertTrue(CommandFactory.create(ProtoverCommand.NAME, "1", response) instanceof ProtoverCommand);
        assertTrue(CommandFactory.create(QuitCommand.NAME, "", response) instanceof QuitCommand);
        assertTrue(CommandFactory.create(RandomCommand.NAME, "", response) instanceof RandomCommand);
        assertTrue(CommandFactory.create(RejectedCommand.NAME, "", response) instanceof RejectedCommand);
        assertTrue(CommandFactory.create(ResultCommand.NAME, "", response) instanceof ResultCommand);
        assertTrue(CommandFactory.create(SetBoardCommand.NAME, "", response) instanceof SetBoardCommand);
        assertTrue(CommandFactory.create(UserMoveCommand.NAME, "", response) instanceof UserMoveCommand);
        assertTrue(CommandFactory.create(XBoardCommand.NAME, "", response) instanceof XBoardCommand);
    }
}
