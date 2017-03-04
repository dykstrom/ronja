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

import java.util.logging.Logger;

import static se.dykstrom.ronja.engine.time.TimeUtils.formatTime;

/**
 * Class that represents the XBoard 'time' command.
 *
 * @author Johan Dykstrom
 */
public class TimeCommand extends AbstractCommand {

    public static final String NAME = "time";

    private final static Logger TLOG = Logger.getLogger(TimeCommand.class.getName());

    /** The remaining time in milliseconds, according to XBoard. */
    private final int time;

    @SuppressWarnings("WeakerAccess")
    public TimeCommand(String time, Response response, Game game) throws InvalidCommandException {
        super(time, response, game);

        if (time == null) {
            throw new InvalidCommandException("missing time argument");
        }

        try {
            this.time = 10 * Integer.parseInt(time);
        } catch (NumberFormatException nfe) {
            throw new InvalidCommandException("time not an integer");
        }
    }

    @Override
    public void execute() {
        long engineTime = game.getTimeData().getRemainingTime();
        TLOG.fine("XBoard reports time " + time + " = " + formatTime(time));
        TLOG.fine("Engine reports time " + engineTime + " = " + formatTime(engineTime));
    }
}
