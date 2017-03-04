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

import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.common.parser.CanParser;
import se.dykstrom.ronja.engine.ui.command.Command;
import se.dykstrom.ronja.engine.ui.command.UserMoveCommand;
import se.dykstrom.ronja.engine.ui.io.PrintWriterResponse;

import java.io.*;
import java.util.logging.Logger;

/**
 * A class that parses XBoard commands.
 *
 * @author Johan Dykstrom
 */
public class CommandParser {

    private static final Logger TLOG = Logger.getLogger(CommandParser.class.getName());

    /** Stream to read commands from. */
    private final BufferedReader in;

    /** Response object to write responses to. */
    private final PrintWriterResponse response;

    /** Holds game state. */
    private final Game game;

    public CommandParser(InputStream in, OutputStream out, Game game) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.response = new PrintWriterResponse(new PrintWriter(out, true));
        this.game = game;
    }

    /**
     * Returns the next command.
     *
     * @return The next command read from stdin.
     * @throws IOException If we failed to read from stdin.
     */
    public Command next() throws IOException {
        // Read next line of input from stdin, and split into command and arguments
        String[] array = splitIntoCommandAndArgs(read(in));

        // Check if command is a just a move, e.g. "e2e4"
        if (CanParser.isMove(array[0])) {
            return CommandFactory.create(UserMoveCommand.NAME, array[0], response, game);
        } else {
            return CommandFactory.create(array[0], array[1], response, game);
        }
    }

    /**
     * Splits the given {@code text} in two - the name of the command, and its arguments.
     * The result is returned as a string array of length 2. If there are no arguments,
     * the second element in the array will be {@code null}.
     *
     * @param text The text read from stdin.
     * @return An array of length 2 - command and arguments.
     */
    private String[] splitIntoCommandAndArgs(String text) {
        int index = text.indexOf(" ");
        if (index == -1) {
            return new String[]{text, null};
        } else {
            return new String[]{text.substring(0, index), text.substring(index + 1).trim()};
        }
    }

    /**
     * Reads the next line from stdin while also doing some error handling.
     */
    private String read(BufferedReader in) throws IOException {
        try {
            String line = in.readLine();
            if (line != null) {
                TLOG.finer("XB -> " + line);
                return line.trim();
            } else {
                TLOG.info("BufferedReader.readLine returned null");
                return "quit";
            }
        } catch (IOException ioe) {
            TLOG.severe("Failed to read stdin: " + ioe);
            throw ioe;
        }
    }
}
