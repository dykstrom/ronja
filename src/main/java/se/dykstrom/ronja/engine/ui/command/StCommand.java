/*
 * Copyright (C) 2017 Johan Dykstrom
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
import se.dykstrom.ronja.engine.time.TimeControl;
import se.dykstrom.ronja.engine.time.TimeData;
import se.dykstrom.ronja.engine.ui.io.Response;

import java.text.ParseException;
import java.util.logging.Logger;

import static se.dykstrom.ronja.engine.time.TimeUtils.parseStText;

/**
 * Class that represents the XBoard 'st' command.
 *
 * @author Johan Dykstrom
 */
public class StCommand extends AbstractCommand {

    public static final String NAME = "st";

    private final static Logger TLOG = Logger.getLogger(StCommand.class.getName());

    @SuppressWarnings("WeakerAccess")
    public StCommand(String args, Response response, Game game) throws InvalidCommandException {
        super(args, response, game);
        if (args == null) {
            throw new InvalidCommandException("missing time argument");
        }
    }

    @Override
    public void execute() {
        TLOG.info("Time settings: " + getArgs());
        try {
            TimeControl timeControl = parseStText(getArgs());
            game.setTimeControl(timeControl);
            game.setTimeData(TimeData.from(timeControl));
        } catch (ParseException pe) {
            response.write("Error (" + pe.getMessage() + "): st " + getArgs());
        }
    }
}
