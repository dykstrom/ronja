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

import se.dykstrom.ronja.engine.ui.command.Command;
import se.dykstrom.ronja.engine.utils.Version;
import se.dykstrom.ronja.engine.ui.command.QuitCommand;

import java.io.IOException;

/**
 * The main class, and entry point of the chess engine.
 *
 * The log levels are used like this:
 * 
 * INFO:   Opening book info. Game info.
 * FINE:   User moves. Engine moves. Statistics from searching.
 * FINER:  XBoard input and output.
 * FINEST: Debug info from searching, move generation, and evaluation.
 *
 * @author Johan Dykstrom
 */
public class Ronja {

    static {
        if (System.getProperty("java.util.logging.config.file") == null) {
            System.setProperty("java.util.logging.config.file", "ronja.properties");
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("# Ronja version " + Version.instance());

        // Create a command parser
        CommandParser parser = new CommandParser(System.in, System.out);

        // Start parsing commands
        start(parser);
    }

    /**
     * Starts parsing and executing commands using the given {@code parser}.
     */
    private static void start(CommandParser parser) throws IOException {
        Command command = parser.next();
        while (!(command instanceof QuitCommand)) {
            command.execute();
            command = parser.next();
        }
        command.execute();
    }
}
