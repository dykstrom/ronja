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

import java.util.Iterator;
import java.util.List;

import static se.dykstrom.ronja.common.parser.SanParser.format;

public class MovesCommand extends AbstractCommand {

    public static final String NAME = "moves";

    @SuppressWarnings("WeakerAccess")
    public MovesCommand(String args, Response response, Game game) {
        super(args, response, game);
    }

    @Override
    public void execute() {
        response.write("");
        response.write("      White   Black");

        int moveNumber = game.getStartMoveNumber();
        List<String> formattedMoves = format(game.getStartPosition(), game.getMoves());
        if (!game.getStartPosition().isWhiteMove() && !formattedMoves.isEmpty()) {
            formattedMoves.add(0, "");
        }
        Iterator<String> iterator = formattedMoves.iterator();
        while (iterator.hasNext()) {
            String whiteMove = iterator.next();
            String blackMove = iterator.hasNext() ? iterator.next() : "";
            response.write(String.format("%3d.  %-6s  %s", moveNumber++, whiteMove, blackMove));
        }

        response.write("");
    }
}
