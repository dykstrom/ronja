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

package se.dykstrom.ronja.engine.ui.io;

import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * A {@code Response} object that forwards the response to the given {@code PrintWriter}.
 */
public record PrintWriterResponse(PrintWriter writer) implements Response {

    private static final Logger TLOG = Logger.getLogger(PrintWriterResponse.class.getName());

    @Override
    public void write(final String text) {
        TLOG.finer(() -> "XB <- " + text);
        writer.println(text);
    }
}
