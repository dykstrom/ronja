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

import java.util.Objects;
import java.util.logging.Logger;

public class NewCommand extends AbstractCommand {

    public static final String NAME = "new";

    private final static Logger TLOG = Logger.getLogger(NewCommand.class.getName());

    @SuppressWarnings("WeakerAccess")
    public NewCommand(String args, Response response, Game game) {
        super(args, response, game);
    }

    @Override
    public void execute() {
        game.reset();
        TLOG.info("New game, engine plays " + Objects.toString(game.getEngineColor()).toLowerCase());
    }
}
