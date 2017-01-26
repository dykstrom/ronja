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

package se.dykstrom.ronja.engine.utils;

import java.io.*;
import java.util.logging.Logger;

/**
 * Utility methods related to files.
 *
 * @author Johan Dykstrom
 */
public final class FileUtils {

    private static final Logger TLOG = Logger.getLogger(FileUtils.class.getName());

    private FileUtils() { }

    /**
     * Writes {@code text} to the given {@code file}. If the flag {@code append} is {@code true},
     * the new text will be appended to the end of an exiting file rather than overwriting it.
     *
     * @param text The text string to write.
     * @param file The file to write to.
     * @param append True if text should be appended to the end of the file.
     * @throws IOException If writing failed.
     */
    public static void write(String text, File file, boolean append) throws IOException {
        PrintWriter out;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file, append)), true);
        } catch (IOException ioe) {
            TLOG.warning("Failed to open game log file '" + file.getName() + "': " + ioe);
            throw ioe;
        }

        TLOG.finest("Writing to game log file '" + file.getName() + "'");
        out.println(text);
        out.close();

        // PrintWriter does not throw exceptions, but we can check for errors
        if (out.checkError()) {
            TLOG.warning("An error occurred while writing to game log file '" + file.getName() + "'");
            throw new IOException("Unknown I/O error: " + file.getName());
        }
    }
}
