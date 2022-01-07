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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.fail;

/**
 * Utility functions related to testing.
 *
 * @author Johan Dykstrom
 */
public class TestUtils {

    private TestUtils() { }

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
     * Reads all lines of input that is available from the given {@code reader},
     * and returns this as a list of strings. This method assumes that some input
     * will be available, and will fail the test if that is not the case. It will
     * also fail the test if the input read contains an exception.
     *
     * @param reader The reader to read input from.
     * @return The lines of input read from the reader.
     */
    public static List<String> readAllInput(final BufferedReader reader) throws Exception {
        List<String> list = new ArrayList<>();

        // Assume there will be some input
        await().atMost(5, SECONDS).until(reader::ready);

        while (reader.ready()) {
            list.add(reader.readLine());
            Thread.sleep(100);
        }

        // Fail if we discover an exception
        if (containsRegex("Exception", list)) {
            fail("Engine exception: " + list);
        }

        return list;
    }

    /**
     * Creates a temporary opening book file for testing. The file is deleted again when the VM exits.
     *
     * @return A reference to the created file.
     */
    public static File createBookFile() throws IOException {
        File bookFile = File.createTempFile("ronja_", ".csv");
        bookFile.deleteOnExit();

        try (PrintStream out = new PrintStream(bookFile, StandardCharsets.UTF_8)) {
            out.println(";e2e4/100;King's Pawn Opening");
            out.println("e2e4;e7e5/50;King's Pawn Game");
            out.println("e2e4 e7e5;g1f3/100;");
            out.println("e2e4;e7e6/50;French Defense");
            out.println("e2e4 e7e6;d2d4/100;");
        }

        return bookFile;
    }
}
