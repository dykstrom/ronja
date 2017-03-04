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
import se.dykstrom.ronja.common.parser.PgnParser;
import se.dykstrom.ronja.engine.ui.io.Response;
import se.dykstrom.ronja.engine.utils.AppConfig;
import se.dykstrom.ronja.engine.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class ResultCommand extends AbstractCommand {

    public static final String NAME = "result";

    private final static Logger TLOG = Logger.getLogger(ResultCommand.class.getName());

    @SuppressWarnings("WeakerAccess")
    public ResultCommand(String result, Response response, Game game) throws InvalidCommandException {
        super(result, response, game);
        if (result == null) {
            throw new InvalidCommandException("missing result");
        }
    }

    @Override
    public void execute() {
        TLOG.info("Game over: " + getArgs());
        game.setResult(getArgs());
        String filename = AppConfig.getGameLogFilename();
        if (filename != null) {
            try {
                FileUtils.write(PgnParser.format(game), new File(filename), true);
            } catch (IOException ioe) {
                TLOG.warning("Failed to write to game log file: " + ioe.getMessage());
            }
        }
    }
}
