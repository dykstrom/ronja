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
import se.dykstrom.ronja.test.AbstractTestCase;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This class is for testing class {@code CommandParser} using JUnit.
 *
 * @author Johan Dykstrom
 * @see CommandParser
 */
public class CommandParserTest extends AbstractTestCase {

    private final Game game = new Game(OpeningBook.DEFAULT);

    @Test
    public void testQuit() throws Exception {
        doTest("quit", new Class[]{QuitCommand.class}, new String[]{null});
    }

    @Test
    public void testPlayOther() throws Exception {
        doTest("playother", new Class[]{PlayOtherCommand.class}, new String[]{null});
    }

    @Test
    public void testPing() throws Exception {
        doTest("ping 0", new Class[]{PingCommand.class}, new String[]{"0"});
    }

    @Test
    public void testName() throws Exception {
        doTest("name GNU Chess", new Class[]{NameCommand.class}, new String[]{"GNU Chess"});
    }

    @Test
    public void testResult() throws Exception {
        doTest("result 1-0 {White mates}", new Class[]{ResultCommand.class}, new String[]{"1-0 {White mates}"});
    }

    @Test
    public void testUserMove() throws Exception {
        doTest("usermove e2e4", new Class[]{UserMoveCommand.class}, new String[]{"e2e4"});
    }

    @Test
    public void testMoveWithoutCommand() throws Exception {
        doTest("f2f4", new Class[]{UserMoveCommand.class}, new String[]{"f2f4"});
    }

    @Test
    public void testSetBoard() throws Exception {
        doTest("setboard fen", new Class[]{SetBoardCommand.class}, new String[]{"fen"});
    }

    @Test
    public void testInvalid() throws Exception {
        doTest("foo", new Class[]{InvalidCommand.class}, new String[]{"foo"});
    }

    @Test
    public void testMultipleCommands() throws Exception {
        doTest("xboard\nprotover 1\nnew\nrandom",
               new Class[]{XBoardCommand.class, ProtoverCommand.class, NewCommand.class, RandomCommand.class},
               new String[]{null, "1", null, null});
    }

    /**
     * Tests that parsing the given string of {@code commands} results in a
     * stream of command objects matching the given {@code classes} and
     * {@code args}.
     */
    private void doTest(String commands, Class<?>[] classes, String[] args) throws Exception {
        assertEquals(classes.length, args.length);

        InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.ISO_8859_1));
        CommandParser commandParser = new CommandParser(in, System.out, game);

        for (int i = 0; i < classes.length; i++) {
            AbstractCommand command = (AbstractCommand) commandParser.next();
            assertNotNull(command);
            assertEquals(classes[i], command.getClass());
            assertEquals(args[i], command.getArgs());
        }
    }
}
