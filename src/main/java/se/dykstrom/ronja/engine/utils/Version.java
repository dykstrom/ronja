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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides the current version of the chess engine.
 *
 * @author Johan Dykstrom
 */
public final class Version {

    private String version = "<no version>";

    /**
     * The singleton instance of this class.
     */
    private static final class Holder {
        static final Version INSTANCE = new Version();
    }

    /**
     * Returns the singleton {@code Version} instance.
     *
     * @return The singleton {@code Version} instance.
     */
    @SuppressWarnings("SameReturnValue")
    public static Version instance() {
        return Holder.INSTANCE;
    }

    private Version() {
        final var url = Version.class.getResource("/version.properties");
        if (url != null) {
            try (InputStream in = url.openStream()) {
                final var properties = new Properties();
                properties.load(in);
                version = properties.getProperty("ronja.version");
            } catch (IOException ignore) {
                // Ignore
            }
        }
    }

    @Override
    public String toString() {
        return version;
    }
}
