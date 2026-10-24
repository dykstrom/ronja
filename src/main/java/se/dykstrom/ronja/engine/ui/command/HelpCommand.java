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

public final class HelpCommand extends AbstractCommand {

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
        response.write("computer  = tell the chess engine that the opponent is a computer");
        response.write("force     = turn force mode on");
        response.write("go        = turn force mode off and set the chess engine to");
        response.write("            play the color that is on move");
        response.write("help      = show this help text");
        response.write("hint      = ask the chess engine for a hint");
        response.write("level     = set a time control: moves per time control, base time");
        response.write("            in minutes[:seconds], and increment in seconds");
        response.write("moves     = show the moves made so far");
        response.write("name      = tell the chess engine the name of the opponent");
        response.write("new       = start a new game with the chess engine as black");
        response.write("otim      = report the opponent's remaining time in centiseconds");
        response.write("ping      = ping the chess engine");
        response.write("playother = turn force mode off and set the chess engine to");
        response.write("            play the color that is not on move");
        response.write("protover  = set the protocol version, and list the supported features");
        response.write("quit      = quit program");
        response.write("rejected  = reject a feature that the chess engine offered");
        response.write("remove    = retract latest move pair, and let the user move again");
        response.write("result    = end the game with the given result, e.g. 1-0 {White mates}");
        response.write("setboard  = set current position to the given FEN position");
        response.write("st        = set a time control of a fixed number of seconds per move");
        response.write("time      = set the chess engine's remaining time in centiseconds");
        response.write("undo      = retract latest move");
        response.write("usermove  = submit a move in coordinate algebraic notation");
        response.write("");
    }
}
