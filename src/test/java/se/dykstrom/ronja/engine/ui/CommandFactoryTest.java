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

import org.junit.jupiter.api.Test;
import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.engine.ui.command.*;
import se.dykstrom.ronja.test.ListResponse;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static se.dykstrom.ronja.common.model.Piece.PAWN;
import static se.dykstrom.ronja.common.model.Square.D2_IDX;
import static se.dykstrom.ronja.common.model.Square.D4_IDX;
import static se.dykstrom.ronja.common.model.Square.E2_IDX;
import static se.dykstrom.ronja.common.model.Square.E4_IDX;
import static se.dykstrom.ronja.common.model.Square.E5_IDX;
import static se.dykstrom.ronja.common.model.Square.E7_IDX;
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
    public void testRemoveCommand() {
        final var e2e4 = Move.create(PAWN, E2_IDX, E4_IDX);
        final var e7e5 = Move.create(PAWN, E7_IDX, E5_IDX);
        final var d2d4 = Move.create(PAWN, D2_IDX, D4_IDX);

        game.makeMove(e2e4);
        game.makeMove(e7e5);
        game.makeMove(d2d4);
        assertArrayEquals(new int[]{e2e4, e7e5, d2d4}, game.getMoves());

        Command command = CommandFactory.create(RemoveCommand.NAME, null, response, game);
        command.execute();
        assertEquals(0, response.getList().size());
        assertArrayEquals(new int[]{e2e4}, game.getMoves());
    }

    @Test
    public void testInvalidCommand() {
        Command command = CommandFactory.create("foo", null, response, game);
        assertInstanceOf(InvalidCommand.class, command);
        command.execute();
        assertEquals(1, response.getList().size());
        assertContainsRegex("Error \\(unknown command\\):", response.getList());
    }

    @Test
    public void testAllCommands() {
        assertInstanceOf(AcceptedCommand.class, CommandFactory.create(AcceptedCommand.NAME, "", response, game));
        assertInstanceOf(BkCommand.class, CommandFactory.create(BkCommand.NAME, "", response, game));
        assertInstanceOf(BoardCommand.class, CommandFactory.create(BoardCommand.NAME, "", response, game));
        assertInstanceOf(ComputerCommand.class, CommandFactory.create(ComputerCommand.NAME, "", response, game));
        assertInstanceOf(EasyCommand.class, CommandFactory.create(EasyCommand.NAME, "", response, game));
        assertInstanceOf(ForceCommand.class, CommandFactory.create(ForceCommand.NAME, "", response, game));
        assertInstanceOf(GoCommand.class, CommandFactory.create(GoCommand.NAME, "", response, game));
        assertInstanceOf(HardCommand.class, CommandFactory.create(HardCommand.NAME, "", response, game));
        assertInstanceOf(HelpCommand.class, CommandFactory.create(HelpCommand.NAME, "", response, game));
        assertInstanceOf(HintCommand.class, CommandFactory.create(HintCommand.NAME, "", response, game));
        assertInstanceOf(LevelCommand.class, CommandFactory.create(LevelCommand.NAME, "", response, game));
        assertInstanceOf(MovesCommand.class, CommandFactory.create(MovesCommand.NAME, "", response, game));
        assertInstanceOf(NameCommand.class, CommandFactory.create(NameCommand.NAME, "", response, game));
        assertInstanceOf(NewCommand.class, CommandFactory.create(NewCommand.NAME, "", response, game));
        assertInstanceOf(NoPostCommand.class, CommandFactory.create(NoPostCommand.NAME, "", response, game));
        assertInstanceOf(OtimCommand.class, CommandFactory.create(OtimCommand.NAME, "1", response, game));
        assertInstanceOf(PingCommand.class, CommandFactory.create(PingCommand.NAME, "", response, game));
        assertInstanceOf(PlayOtherCommand.class, CommandFactory.create(PlayOtherCommand.NAME, "", response, game));
        assertInstanceOf(PostCommand.class, CommandFactory.create(PostCommand.NAME, "", response, game));
        assertInstanceOf(ProtoverCommand.class, CommandFactory.create(ProtoverCommand.NAME, "1", response, game));
        assertInstanceOf(QuitCommand.class, CommandFactory.create(QuitCommand.NAME, "", response, game));
        assertInstanceOf(RandomCommand.class, CommandFactory.create(RandomCommand.NAME, "", response, game));
        assertInstanceOf(RejectedCommand.class, CommandFactory.create(RejectedCommand.NAME, "", response, game));
        assertInstanceOf(RemoveCommand.class, CommandFactory.create(RemoveCommand.NAME, "", response, game));
        assertInstanceOf(ResultCommand.class, CommandFactory.create(ResultCommand.NAME, "", response, game));
        assertInstanceOf(SetBoardCommand.class, CommandFactory.create(SetBoardCommand.NAME, "", response, game));
        assertInstanceOf(StCommand.class, CommandFactory.create(StCommand.NAME, "", response, game));
        assertInstanceOf(TimeCommand.class, CommandFactory.create(TimeCommand.NAME, "1", response, game));
        assertInstanceOf(UserMoveCommand.class, CommandFactory.create(UserMoveCommand.NAME, "", response, game));
        assertInstanceOf(XBoardCommand.class, CommandFactory.create(XBoardCommand.NAME, "", response, game));
    }
}
