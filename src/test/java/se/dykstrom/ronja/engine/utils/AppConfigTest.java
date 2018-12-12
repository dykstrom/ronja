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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;

import se.dykstrom.ronja.test.AbstractTestCase;

/**
 * This class is for testing class {@code AppConfig} using JUnit.
 *
 * @author Johan Dykstrom
 * @see AppConfig
 */
public class AppConfigTest extends AbstractTestCase {

    private static final String SETTER_ENGINE_NAME = "setter";
    private static final String PROPERTIES_ENGINE_NAME = "properties";
    private static final String FILE_ENGINE_NAME = "file";

    private static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");

    @Before
    public void setUp() throws Exception {
        AppConfig.setConfigDirectory(TEMP_DIRECTORY);
        AppConfig.setEngineName(null);
        System.clearProperty(AppConfig.PROPERTY_ENGINE_NAME);

        File file = new File(TEMP_DIRECTORY, "ronja.properties");
        file.deleteOnExit();

        try (PrintStream out = new PrintStream(file, StandardCharsets.ISO_8859_1)) {
            out.println(AppConfig.PROPERTY_ENGINE_NAME + "=" + FILE_ENGINE_NAME);
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetFromSetter() {
        AppConfig.setEngineName(SETTER_ENGINE_NAME);
        System.setProperty(AppConfig.PROPERTY_ENGINE_NAME, PROPERTIES_ENGINE_NAME);
        assertEquals(SETTER_ENGINE_NAME, AppConfig.getEngineName());
    }

    @Test
    public void testGetFromSystemProperties() {
        System.setProperty(AppConfig.PROPERTY_ENGINE_NAME, PROPERTIES_ENGINE_NAME);
        assertEquals(PROPERTIES_ENGINE_NAME, AppConfig.getEngineName());
    }

    @Test
    public void testGetFromDataStore() {
        assertEquals(FILE_ENGINE_NAME, AppConfig.getEngineName());
    }
}
