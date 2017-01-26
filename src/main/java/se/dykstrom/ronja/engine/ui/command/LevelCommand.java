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

import se.dykstrom.ronja.engine.ui.io.Response;

import java.util.logging.Logger;

public class LevelCommand extends AbstractCommand {

    public static final String NAME = "level";

    private final static Logger TLOG = Logger.getLogger(LevelCommand.class.getName());

    public LevelCommand(String args, Response response) throws InvalidCommandException {
        super(args, response);
        if (args == null) {
            throw new InvalidCommandException("missing args", NAME);
        }
    }

    @Override
    public void execute() {
        TLOG.info("Time settings: " + getArgs());
    }
}
