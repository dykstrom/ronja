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
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.IllegalMoveException;
import se.dykstrom.ronja.common.parser.MoveParser;
import se.dykstrom.ronja.engine.ui.io.Response;
import se.dykstrom.ronja.engine.utils.PositionUtils;

import java.util.logging.Logger;

public class UserMoveCommand extends AbstractMoveCommand {

    public static final String NAME = "usermove";

    private static final Logger TLOG = Logger.getLogger(UserMoveCommand.class.getName());

    @SuppressWarnings("WeakerAccess")
    public UserMoveCommand(String move, Response response, Game game) throws InvalidCommandException {
        super(move, response, game);
        if (move == null) {
            throw new InvalidCommandException("missing move argument");
        }
    }

    @Override
    public void execute() {
        Position position = game.getPosition();

        if (PositionUtils.isGameOver(position, game)) {
            notifyUserGameOverError(NAME);
        } else {
            try {
                int move = MoveParser.parse(getArgs(), position);
                TLOG.fine("User move: " + formatForLogging(move, position));

                // Make the user's move
                game.makeMove(move);

                // If the user is in check after his move
                if (game.getPosition().isIllegalCheck()) {
                    game.unmakeMove();
                    throw new IllegalMoveException("in check after move");
                }

                // If game is over notify user, otherwise make engine's move (in the new position)
                if (PositionUtils.isGameOver(game.getPosition(), game)) {
                    notifyUserGameOverOk();
                } else {
                    move();
                }
            } catch (IllegalMoveException ime) {
                response.write("Illegal move (" + ime.getMessage() + "): " + getArgs());
            }
        }
    }
}
