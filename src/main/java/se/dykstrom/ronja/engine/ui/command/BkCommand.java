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

import se.dykstrom.ronja.common.book.BookMove;
import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.SanParser;
import se.dykstrom.ronja.engine.ui.io.Response;

import java.util.Comparator;
import java.util.List;

public class BkCommand extends AbstractCommand {

    public static final String NAME = "bk";

    @SuppressWarnings("WeakerAccess")
    public BkCommand(String args, Response response) {
        super(args, response);
    }

    @Override
    public void execute() {
        OpeningBook book = Game.instance().getBook();
        Position position = Game.instance().getPosition();

        List<BookMove> bookMoves = book.findAllMoves(position);

        // If there are no book moves
        if (bookMoves == null) {
            response.write(" No book moves for this position.");
        } else {
            response.write(" Book moves:");
            bookMoves.stream()
                    .filter(bookMove -> bookMove.getWeight() > 0)
                    .sorted(BOOK_MOVE_COMPARATOR)
                    .map(bookMove -> String.format(" %-6s %3d%%", SanParser.format(bookMove.getMove(), position), bookMove.getWeight()))
                    .forEach(response::write);
        }
        response.write("");
    }

    private static final Comparator<BookMove> BOOK_MOVE_COMPARATOR = (bm1, bm2) -> {
        if (bm1.getWeight() > bm2.getWeight()) {
            return -1;
        } else if (bm1.getWeight() < bm2.getWeight()) {
            return 1;
        } else {
            return 0;
        }
    };
}
