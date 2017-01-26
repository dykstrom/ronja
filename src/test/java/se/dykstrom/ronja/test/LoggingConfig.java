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

import java.util.logging.*;

/**
 * Configuration class for java.util.logging to use for testing.
 *
 * @author Johan Dykstrom
 */
public class LoggingConfig {

    public LoggingConfig() {
        try {
            System.setProperty("java.util.logging.SimpleFormatter.format",
                    "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$-7s [%2$s] %5$s %6$s%n");

            FileHandler fileHandler = new FileHandler("target/test.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            Logger se = Logger.getLogger("se");
            se.setLevel(Level.FINE);
            se.addHandler(fileHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
