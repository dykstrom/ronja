/*
 * Copyright (C) 2026 Johan Dykstrom
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

import java.util.logging.Logger;

/**
 * Class that represents the XBoard 'undo' command. XBoard puts the engine in force mode
 * before sending this command, so the engine does not have to start thinking afterwards.
 *
 * @author Johan Dykstrom
 */
public final class UndoCommand extends AbstractCommand {

    public static final String NAME = "undo";

    private static final Logger TLOG = Logger.getLogger(UndoCommand.class.getName());

    @SuppressWarnings("WeakerAccess")
    public UndoCommand(final String args, final Response response, final Game game) {
        super(args, response, game);
    }

    @Override
    public void execute() {
        if (game.getMoves().length == 0) {
            response.write("Error (no move to undo): " + NAME);
            return;
        }
        game.unmakeMove();
        TLOG.info(() -> "Retracting latest move, " + game.getPosition().getActiveColor() + " to move");
    }
}
