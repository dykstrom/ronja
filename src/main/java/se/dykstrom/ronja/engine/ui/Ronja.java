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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Logger;

import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.common.book.OpeningBookParser;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.engine.ui.command.Command;
import se.dykstrom.ronja.engine.ui.command.QuitCommand;
import se.dykstrom.ronja.engine.utils.AppConfig;
import se.dykstrom.ronja.engine.utils.Version;

/**
 * The main class, and entry point of the chess engine.
 *
 * The log levels are used like this:
 * 
 * INFO:   Opening book info. Game info.
 * FINE:   User and engine moves. Statistics from searching. Time control.
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

    @SuppressWarnings("java:S106")
    public static void main(String[] args) throws IOException {
        System.out.println("# Ronja version " + Version.instance());

        final var logger = Logger.getLogger(Ronja.class.getName());
        logger.info(() -> "Ronja version: " + Version.instance());
        logger.info(() -> "Java version:  " + System.getProperty("java.version"));

        // Create a game
        final var game = createGame();

        // Create a command parser
        final var parser = new CommandParser(System.in, System.out, game);

        // Start parsing commands
        start(parser);
    }

    /**
     * Loads the opening book from file, and creates a new game.
     */
    private static Game createGame() {
        try {
            return new Game(OpeningBookParser.parse(new File(AppConfig.getBookFilename())));
        } catch (IOException | ParseException e) {
            final var logger = Logger.getLogger(Ronja.class.getName());
            logger.severe("Failed to load opening book. " + e.getMessage());
            return new Game(OpeningBook.DEFAULT);
        }
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
