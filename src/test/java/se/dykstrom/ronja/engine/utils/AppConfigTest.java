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
import se.dykstrom.ronja.test.AbstractTestCase;

import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

/**
 * This class is for testing class {@code AppConfig} using JUnit.
 *
 * @author Johan Dykstrom
 * @see AppConfig
 */
public class AppConfigTest extends AbstractTestCase {

    private static final Integer SETTER_SEARCH_DEPTH = 3;
    private static final Integer PROPERTIES_SEARCH_DEPTH = 5;
    private static final Integer FILE_SEARCH_DEPTH = 7;

    private static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");

    @Before
    public void setUp() throws Exception {
        AppConfig.setConfigDirectory(TEMP_DIRECTORY);
        AppConfig.setSearchDepth(null);
        System.clearProperty(AppConfig.PROPERTY_SEARCH_DEPTH);

        File file = new File(TEMP_DIRECTORY, "ronja.properties");
        file.deleteOnExit();

        PrintStream out = new PrintStream(file, "ISO-8859-1");
        out.println(AppConfig.PROPERTY_SEARCH_DEPTH + "=" + FILE_SEARCH_DEPTH);
        out.close();
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetFromSetter() {
        AppConfig.setSearchDepth(SETTER_SEARCH_DEPTH);
        System.setProperty(AppConfig.PROPERTY_SEARCH_DEPTH, PROPERTIES_SEARCH_DEPTH.toString());
        assertEquals(SETTER_SEARCH_DEPTH, AppConfig.getSearchDepth());
    }

    @Test
    public void testGetFromSystemProperties() {
        System.setProperty(AppConfig.PROPERTY_SEARCH_DEPTH, PROPERTIES_SEARCH_DEPTH.toString());
        assertEquals(PROPERTIES_SEARCH_DEPTH, AppConfig.getSearchDepth());
    }

    @Test
    public void testGetFromDataStore() {
        assertEquals(FILE_SEARCH_DEPTH, AppConfig.getSearchDepth());
    }
}
