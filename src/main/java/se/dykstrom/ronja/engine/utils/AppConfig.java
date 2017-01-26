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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Handles all configuration data for the application, and provides getters for different types of data.
 *
 * @author Johan Dykstrom
 */
public class AppConfig {

    /** The environment property for configuration directory. */
    public static final String PROPERTY_CONFIG_DIR = "ronja.config.dir";

    /** The environment property for engine name. */
    public static final String PROPERTY_ENGINE_NAME = "ronja.engine.name";

    /** The environment property for opening book filename. */
    public static final String PROPERTY_BOOK_FILE = "ronja.book.file";

    /** The environment property for game log filename. */
    public static final String PROPERTY_GAME_LOG_FILE = "ronja.game.file";

    /** The environment property for search depth. */
    public static final String PROPERTY_SEARCH_DEPTH = "ronja.search.depth";

    private static final Logger TLOG = Logger.getLogger(AppConfig.class.getName());

    /** The name of the application properties file. */
    private static final String PROPERTIES_FILENAME = "ronja.properties";

    private static String configDirectory;
    private static String engineName;
    private static String bookFilename;
    private static String gameLogFilename;
    private static Integer searchDepth;

    // ------------------------------------------------------------------------
    // Configuration data:
    // ------------------------------------------------------------------------

    /**
     * Sets the configuration directory.
     */
    public static void setConfigDirectory(String configDirectory) {
        AppConfig.configDirectory = configDirectory;
    }

    /**
     * Returns the configuration directory.
     */
    private static String getConfigDirectory() {
        if (configDirectory == null) {
            configDirectory = getStringProperty(PROPERTY_CONFIG_DIR);
        }
        if (configDirectory == null) {
            // We cannot read this one from the config file...
            configDirectory = ".";
        }
        return configDirectory;
    }

    /**
     * Sets the opening book filename.
     */
    public static void setBookFilename(String bookFilename) {
        AppConfig.bookFilename = bookFilename;
    }

    /**
     * Returns the opening book filename.
     */
    public static String getBookFilename() {
        if (bookFilename == null) {
            bookFilename = getStringProperty(PROPERTY_BOOK_FILE);
        }
        if (bookFilename == null) {
            bookFilename = getString(PROPERTY_BOOK_FILE, "book.xml");
        }
        return bookFilename;
    }

    /**
     * Sets the game log filename.
     */
    public static void setGameLogFilename(String gameLogFilename) {
        AppConfig.gameLogFilename = gameLogFilename;
    }

    /**
     * Returns the game log filename, or {@code null} if no game log file has been configured.
     */
    public static String getGameLogFilename() {
        if (gameLogFilename == null) {
            gameLogFilename = getStringProperty(PROPERTY_GAME_LOG_FILE);
        }
        if (gameLogFilename == null) {
            gameLogFilename = getString(PROPERTY_GAME_LOG_FILE, null);
        }
        return gameLogFilename;
    }

    /**
     * Sets the engine name.
     */
    @SuppressWarnings("unused")
    public static void setEngineName(String engineName) {
        AppConfig.engineName = engineName;
    }

    /**
     * Returns the engine name, that is, the name the chess engine presents to XBoard.
     */
    public static String getEngineName() {
        if (engineName == null) {
            engineName = getStringProperty(PROPERTY_ENGINE_NAME);
        }
        if (engineName == null) {
            engineName = getString(PROPERTY_ENGINE_NAME, "Ronja " + Version.instance());
        }
        return engineName;
    }

    /**
     * Sets the search depth.
     */
    public static void setSearchDepth(Integer searchDepth) {
        AppConfig.searchDepth = searchDepth;
    }

    /**
     * Returns the search depth, i.e. the number of plies to search.
     */
    public static Integer getSearchDepth() {
        if (searchDepth == null) {
            searchDepth = getIntegerProperty(PROPERTY_SEARCH_DEPTH);
        }
        if (searchDepth == null) {
            searchDepth = getInteger(PROPERTY_SEARCH_DEPTH, 3);
        }
        return searchDepth;
    }

    // ------------------------------------------------------------------------
    // Data store:
    // ------------------------------------------------------------------------

    /**
     * Returns the specified configuration data, a string, converted to a Java
     * String. If no data is found, a default value is returned.
     *
     * @param name The configuration data name.
     * @param def The default value to use if no data is found.
     */
    private static String getString(String name, String def) {
        String res = getProperties().getProperty(name);
        if (res != null) {
            return res;
        } else {
            TLOG.warning("Missing data for [" + name + "]. Using default value '" + def + "'.");
            return def;
        }
    }

    /**
     * Returns the specified configuration data, an integer, converted to a
     * Java int. If no data is found, a default value is returned.
     *
     * @param name The configuration data name.
     * @param def The default value to use if no data is found.
     */
    private static int getInteger(String name, int def) {
        String res = getProperties().getProperty(name);
        try {
            return Integer.parseInt(res);
        } catch (NumberFormatException nfe) {
            TLOG.warning("Invalid data for [" + name + "]. Not an integer. Using default value '" + def + "'.");
            return def;
        }
    }

    /**
     * Returns a properties object loaded from the application properties file.
     */
    private static Properties getProperties() {
        File file = new File(getConfigDirectory(), PROPERTIES_FILENAME);

        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        } catch (IOException ioe) {
            TLOG.warning("Failed to load properties from file '" + file.getName() + "': " + ioe);
        }
        return properties;
    }

    // ------------------------------------------------------------------------
    // Environment properties:
    // ------------------------------------------------------------------------

    private static Integer getIntegerProperty(String name) {
        String value = System.getProperty(name);
        return (value != null) ? Integer.valueOf(value) : null;
    }

    private static String getStringProperty(String name) {
        return System.getProperty(name);
    }
}
