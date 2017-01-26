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
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.engine.ui.io.Response;
import se.dykstrom.ronja.engine.utils.PositionUtils;

import java.text.ParseException;
import java.util.ArrayList;

public class SetBoardCommand extends AbstractCommand {

    public static final String NAME = "setboard";

    public SetBoardCommand(String fen, Response response) throws InvalidCommandException {
        super(fen, response);
        if (fen == null) {
            throw new InvalidCommandException("missing position", NAME);
        }
    }

    @Override
    public void execute() {
        try {
            Position position = FenParser.parse(getArgs());
            if (!PositionUtils.isLegal(position)) {
                response.write("tellusererror Illegal position");
            } else if (!Game.instance().getForceMode()) {
                response.write("tellusererror Not in force mode");
            } else {
                Game.instance().setPosition(position);
                Game.instance().setMoves(new ArrayList<>());
            }
        } catch (ParseException e) {
            response.write("tellusererror Illegal position");
        }
    }
}
