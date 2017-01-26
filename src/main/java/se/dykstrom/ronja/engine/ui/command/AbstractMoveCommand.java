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

import se.dykstrom.ronja.common.model.Color;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.CanParser;
import se.dykstrom.ronja.common.parser.IllegalMoveException;
import se.dykstrom.ronja.engine.core.AlphaBetaFinder;
import se.dykstrom.ronja.engine.core.Finder;
import se.dykstrom.ronja.engine.ui.io.Response;
import se.dykstrom.ronja.engine.utils.AppConfig;
import se.dykstrom.ronja.engine.utils.PositionUtils;

import java.util.logging.Logger;

public abstract class AbstractMoveCommand extends AbstractCommand {

    private static final Logger TLOG = Logger.getLogger(AbstractMoveCommand.class.getName());

    private static final Finder FINDER = new AlphaBetaFinder();

    AbstractMoveCommand(String args, Response response) {
        super(args, response);
    }

    /**
     * Find and make the engine's move.
     */
    protected void move() {
        Game game = Game.instance();

        // If not in force mode, make a move
        if (!game.getForceMode()) {
            checkActiveColor(game);

            // Try to find a move in the opening book
            Move move = game.getBook().findBestMove(game.getPosition());

            // If no book move found, use Finder to find best move
            if (move == null) {
                move = FINDER.findBestMove(game.getPosition(), AppConfig.getSearchDepth());
                TLOG.fine("Engine move: " + prefix(game.getPosition()) + move);
            } else {
                TLOG.fine("Engine move: " + prefix(game.getPosition()) + move + " (book)");
            }

            // Make the move
            try {
                game.makeMove(move);
            } catch (IllegalMoveException ime) {
                TLOG.severe("Engine made an illegal move (" + CanParser.format(move) + "): " + ime.getMessage());
            }

            // Reply to XBoard
            response.write("move " + CanParser.format(move));

            // Check game status after move
            if (PositionUtils.isGameOver(game.getPosition())) {
                notifyUserGameOverOk();
            }
        }
    }

    /**
     * Checks that the active color matches the engine color.
     */
    private void checkActiveColor(Game game) {
        Color activeColor = game.getPosition().getActiveColor();
        Color engineColor = game.getEngineColor();
        if (activeColor != engineColor) {
            String msg = "Active color " + activeColor + " does not match engine color " + engineColor;
            TLOG.severe(msg);
            TLOG.severe("Current position: " + game);
            throw new IllegalStateException(msg);
        }
    }

    /**
     * Returns a short prefix string, including the move number, to use for tracing.
     */
    protected static String prefix(Position position) {
        return position.getFullMoveNumber() + ((position.getActiveColor() == Color.WHITE) ? ". " : "... ");
    }

    /**
     * Notifies the user that the game is over, and what was the result.
     */
    protected void notifyUserGameOverOk() {
        String result;
        Position position = Game.instance().getPosition();
        if (PositionUtils.isCheckMate(position)) {
            if (position.getActiveColor() == Color.WHITE) {
                result = "0-1 {Black mates}";
            } else {
                result = "1-0 {White mates}";
            }
        } else if (PositionUtils.isDraw(position)) {
            result = "1/2-1/2 {" + PositionUtils.getDrawType(position) + "}";
        } else {
            result = "?";
        }
        response.write(result);
        Game.instance().setResult(result);
    }

    /**
     * Notifies the user that the game is over, and that the last command was an error.
     */
    protected void notifyUserGameOverError(String command) {
        Position position = Game.instance().getPosition();
        if (PositionUtils.isCheckMate(position)) {
            response.write("Error (checkmate): " + command);
        } else if (PositionUtils.isDraw(position)) {
            response.write("Error (draw): " + command);
        }
    }
}
