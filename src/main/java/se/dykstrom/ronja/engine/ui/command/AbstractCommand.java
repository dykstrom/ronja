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

/**
 * An abstract base class for all XBoard commands.
 * 
 * @author Johan Dykstrom
 */
public abstract class AbstractCommand implements Command {

    final String args;
    final Response response;
    final Game game;

    AbstractCommand(String args, Response response, Game game) {
        this.args = args;
        this.response = response;
        this.game = game;
    }

    /**
     * Returns the arguments associated with this command, or {@code null} if no arguments.
     */
    public String getArgs() {
        return args;
    }

    /**
     * Default implementation does nothing.
     */
    @Override
    public void execute() { /* Empty */ }
}
