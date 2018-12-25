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
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.CanParser;
import se.dykstrom.ronja.common.parser.SanParser;
import se.dykstrom.ronja.engine.core.AlphaBetaFinder;
import se.dykstrom.ronja.engine.time.TimeUtils;
import se.dykstrom.ronja.engine.ui.io.Response;
import se.dykstrom.ronja.engine.utils.PositionUtils;

import java.util.logging.Logger;

/**
 * Abstract base class for all move related XBoard commands.
 *
 * @author Johan Dykstrom
 */
public abstract class AbstractMoveCommand extends AbstractCommand {

    private static final Logger TLOG = Logger.getLogger(AbstractMoveCommand.class.getName());

    AbstractMoveCommand(String args, Response response, Game game) {
        super(args, response, game);
    }

    /**
     * Find and make the engine's move.
     */
    protected void move() {
        // If not in force mode, make a move
        if (!game.getForceMode()) {
            long startTime = System.currentTimeMillis();

            Position position = game.getPosition();

            checkActiveColor(game);

            // Try to find a move in the opening book
            int move = game.getBook().findBestMove(position);

            // If no book move found, use Finder to find best move
            if (move == 0) {
                var availableTime = TimeUtils.calculateTimeForNextMove(game.getTimeControl(), game.getTimeData());
                var finder = new AlphaBetaFinder(game);
                move = finder.findBestMoveWithinTime(position, availableTime);
                TLOG.fine("Engine move: " + formatForLogging(move, position));
            } else {
                TLOG.fine("Engine move: " + formatForLogging(move, position) + " (book)");
            }

            // Make the move
            game.makeMove(move);

            // Reply to XBoard
            response.write("move " + CanParser.format(move));

            // Check game status after move (get new position)
            if (PositionUtils.isGameOver(game.getPosition(), game)) {
                notifyUserGameOverOk();
            }

            long stopTime = System.currentTimeMillis();
            long usedTime = stopTime - startTime + 50; // Add 50 ms as a safety margin
            game.updateTimeDataAfterMove(usedTime);
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
     * Formats the given move for logging.
     */
    String formatForLogging(int move, Position position) {
        return position.getFullMoveNumber() + (position.isWhiteMove() ? ". " : "... ") + SanParser.format(position, move);
    }

    /**
     * Notifies the user that the game is over, and what was the result.
     */
    void notifyUserGameOverOk() {
        String result;
        Position position = game.getPosition();
        if (PositionUtils.isCheckMate(position)) {
            if (position.isWhiteMove()) {
                result = "0-1 {Black mates}";
            } else {
                result = "1-0 {White mates}";
            }
        } else if (PositionUtils.isDraw(position, game)) {
            result = "1/2-1/2 {" + PositionUtils.getDrawType(position, game) + "}";
        } else {
            result = "?";
        }
        response.write(result);
        game.setResult(result);
    }

    /**
     * Notifies the user that the game is over, and that the last command was an error.
     */
    void notifyUserGameOverError(String command) {
        Position position = game.getPosition();
        if (PositionUtils.isCheckMate(position)) {
            response.write("Error (checkmate): " + command);
        } else {
            if (PositionUtils.isDraw(position, game)) {
                response.write("Error (draw): " + command);
            }
        }
    }
}
