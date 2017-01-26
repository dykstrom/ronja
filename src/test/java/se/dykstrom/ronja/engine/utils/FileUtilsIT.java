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

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static se.dykstrom.ronja.test.TestUtils.assertContainsRegex;

/**
 * This class is for integration testing class {@code FileUtils} using JUnit.
 *
 * @author Johan Dykstrom
 * @see FileUtils
 */
public class FileUtilsIT {

    private File file;

    @Before
    public void setUp() throws Exception {
        file = Files.createTempFile("FileUtilsIT_", ".txt").toFile();
        file.deleteOnExit();
    }

    @Test
    public void testWrite() throws Exception {
        String text = "some text";

        // Write file
        FileUtils.write(text, file, false);

        // Read file
        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(1, lines.size());
        assertContainsRegex("^" + text + "$", lines);
    }

    @Test
    public void testWrite_Append() throws Exception {
        String text1 = "some other text";
        String text2 = "line two";

        // Write first text
        FileUtils.write(text1, file, false);

        // Append second text
        FileUtils.write(text2, file, true);

        // Read file
        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(2, lines.size());
        assertContainsRegex("^" + text1 + "$", lines);
        assertContainsRegex("^" + text2 + "$", lines);
    }
}
