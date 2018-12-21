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

import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.SanParser;
import se.dykstrom.ronja.engine.core.AlphaBetaFinder;
import se.dykstrom.ronja.engine.ui.io.Response;
import se.dykstrom.ronja.engine.utils.PositionUtils;

/**
 * Class that represents the XBoard 'hint' command.
 *
 * @author Johan Dykstrom
 */
public class HintCommand extends AbstractCommand {

    public static final String NAME = "hint";

    @SuppressWarnings("WeakerAccess")
    public HintCommand(String args, Response response, Game game) {
        super(args, response, game);
    }

    @Override
    public void execute() {
        OpeningBook book = game.getBook();
        Position position = game.getPosition();
        if (!PositionUtils.isGameOver(position, game)) {
            int move = book.findBestMove(position);
            if (move == 0) {
                var finder = new AlphaBetaFinder(game);
                move = finder.findBestMove(position, 3); // Limit the search depth in this case
            }
            response.write("Hint: " + SanParser.format(position, move));
        }
    }
}
