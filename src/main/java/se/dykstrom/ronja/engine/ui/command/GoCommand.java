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
import se.dykstrom.ronja.engine.utils.PositionUtils;

import java.util.logging.Logger;

public class GoCommand extends AbstractMoveCommand {

    public static final String NAME = "go";

    private final static Logger TLOG = Logger.getLogger(GoCommand.class.getName());

    public GoCommand(String args, Response response) {
        super(args, response);
    }

    @Override
    public void execute() {
        Game game = Game.instance();
        if (PositionUtils.isGameOver(game.getPosition())) {
            notifyUserGameOverError(NAME);
        } else {
            game.setForceMode(false);
            game.setEngineColor(game.getPosition().getActiveColor());
            TLOG.info("Leaving force mode, engine plays " + game.getEngineColor().toString().toLowerCase());
            move();
        }
    }
}
