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

package se.dykstrom.ronja.engine.ui.command;

import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.engine.ui.io.Response;
import se.dykstrom.ronja.engine.utils.AppConfig;

/**
 * Class that represents the XBoard 'protover' command.
 *
 * @author Johan Dykstrom
 */
public class ProtoverCommand extends AbstractCommand {

    public static final String NAME = "protover";

    /** Protocol version. */
	private final int version;

    @SuppressWarnings("WeakerAccess")
    public ProtoverCommand(String version, Response response, Game game) throws InvalidCommandException {
        super(version, response, game);

        if (version == null) {
            throw new InvalidCommandException("missing version");
        }

        try {
            this.version = Integer.parseInt(version);
        } catch (NumberFormatException nfe) {
            throw new InvalidCommandException("version not an integer");
        }
    }

    @Override
    public void execute() {
        if (version >= 2) {
            response.write("feature analyze=0");
            response.write("feature colors=0");
            response.write("feature myname=\"" + AppConfig.getEngineName() + "\"");
            response.write("feature name=1");
            response.write("feature ping=1");
            response.write("feature playother=1");
            response.write("feature setboard=1");
            response.write("feature sigint=0");
            response.write("feature sigterm=0");
            response.write("feature usermove=1");
            response.write("feature variants=\"normal\"");
            response.write("feature done=1");
        }
    }
}
