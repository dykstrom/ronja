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

import java.util.logging.Logger;

import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.engine.ui.io.Response;

import static se.dykstrom.ronja.engine.time.TimeUtils.formatTime;

/**
 * Class that represents the XBoard 'time' command. When this command executes,
 * it updates the internal chess clock with the time from XBoard.
 *
 * @author Johan Dykstrom
 */
public class TimeCommand extends AbstractCommand {

    public static final String NAME = "time";

    private static final Logger TLOG = Logger.getLogger(TimeCommand.class.getName());

    /** The remaining time in milliseconds, according to XBoard. */
    private final int time;

    @SuppressWarnings("WeakerAccess")
    public TimeCommand(final String time, final Response response, final Game game) throws InvalidCommandException {
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
        final long engineTime = game.getTimeData().getRemainingTime();
        TLOG.fine(() -> "XBoard reports time " + time + " = " + formatTime(time));
        TLOG.fine(() -> "Engine reports time " + engineTime + " = " + formatTime(engineTime));
        game.setTimeData(game.getTimeData().withRemainingTime(time));
    }
}
