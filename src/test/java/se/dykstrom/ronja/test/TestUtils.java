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

package se.dykstrom.ronja.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Utility functions related to testing.
 *
 * @author Johan Dykstrom
 */
public class TestUtils {

    private TestUtils() { }

    /**
     * Waits for the given {@code supplier} to return {@code true} within the given timeout.
     * The supplier should represent some condition that the user wants to wait for. This
     * could be that a file has been created, that a thread has finished or something else.
     *
     * @param supplier The supplier to wait for.
     * @param timeout The maximum time to wait.
     * @param unit The time unit of the timeout argument.
     * @throws Exception If an exception occurs while waiting.
     */
    public static void waitForSupplier(FailableSupplier<Boolean> supplier, long timeout, TimeUnit unit) throws Exception {
        long start = System.nanoTime();
        long remaining = unit.toNanos(timeout);

        while (!supplier.get() && remaining > 0) {
            Thread.sleep(Math.min(TimeUnit.NANOSECONDS.toMillis(remaining) + 1, 50));
            remaining = unit.toNanos(timeout) - (System.nanoTime() - start);
        }

        assertTrue("Timeout after " + timeout + " " + unit, supplier.get());
    }

    /**
     * Returns {@code true} if the regular expression {@code regex} matches
     * at least one substring in the given list of strings.
     *
     * @param regex The regular expression to match.
     * @param list The list of strings to match in.
     */
    public static boolean containsRegex(String regex, List<String> list) {
        Pattern pattern = Pattern.compile(regex);
        for (String string : list) {
            if (pattern.matcher(string).find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Asserts that the regular expression {@code regex} matches at least one substring in the given list of strings.
     *
     * @param regex The regular expression to match.
     * @param list The list of strings to match in.
     */
    public static void assertContainsRegex(String regex, List<String> list) {
        if (!containsRegex(regex, list)) {
            System.err.println("Expected :" + regex);
            System.err.println("Actual   :" + list);
            fail("Regex '" + regex + "' not found in list");
        }
    }

    /**
     * Creates a temporary opening book file for testing. The file is deleted again when the VM exits.
     *
     * @return A reference to the created file.
     */
    public static File createBookFile() throws IOException {
        File bookFile = File.createTempFile("ronja_", ".xml");
        bookFile.deleteOnExit();

        PrintStream out = new PrintStream(bookFile, "ISO-8859-1");

        out.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
        out.println("<move can='' weight='100' name='Initial position'>");
        out.println("  <move can='e2e4' weight='100'>");
        out.println("    <move can='e7e5' weight='50'>");
        out.println("      <move can='g1f3' weight='100'/>");
        out.println("    </move>");
        out.println("    <move can='e7e6' weight='50' name='French Defense'>");
        out.println("      <move can='d2d4' weight='100'/>");
        out.println("    </move>");
        out.println("  </move>");
        out.println("</move>");

        out.close();

        return bookFile;
    }
}
