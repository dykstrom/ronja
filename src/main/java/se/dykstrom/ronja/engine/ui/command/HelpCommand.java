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

import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.engine.ui.io.Response;

public class HelpCommand extends AbstractCommand {

    public static final String NAME = "help";

    @SuppressWarnings("WeakerAccess")
    public HelpCommand(String args, Response response, Game game) {
        super(args, response, game);
    }

    @Override
    public void execute() {
        response.write("");
        response.write("Available commands");
        response.write("------------------");
        response.write("");
        response.write("bk        = show book moves for current position");
        response.write("board     = show current position");
        response.write("force     = turn force mode on");
        response.write("go        = turn force mode off and set the chess engine to");
        response.write("            play the color that is on move");
        response.write("help      = show this help text");
        response.write("hint      = ask the chess engine for a hint");
        response.write("moves     = show the moves made so far");
        response.write("new       = start a new game with the chess engine as black");
        response.write("ping      = ping the chess engine");
        response.write("playother = turn force mode off and set the chess engine to");
        response.write("            play the color that is not on move");
        response.write("quit      = quit program");
        response.write("setboard  = set current position to the given FEN position");
        response.write("usermove  = submit a move in coordinate algebraic notation");
        response.write("");
    }
}
