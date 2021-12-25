/*
 * Copyright (C) 2021 Johan Dykstrom
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

import java.util.logging.Logger;

import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.engine.ui.io.Response;

public class RemoveCommand extends AbstractCommand {

    public static final String NAME = "remove";

    private static final Logger TLOG = Logger.getLogger(RemoveCommand.class.getName());

    @SuppressWarnings("WeakerAccess")
    public RemoveCommand(final String args, final Response response, final Game game) {
        super(args, response, game);
    }

    @Override
    public void execute() {
        game.unmakeMove();
        game.unmakeMove();
        TLOG.info(() -> "Retracting latest move pair and expecting user move number " + game.getPosition().getFullMoveNumber());
    }
}
