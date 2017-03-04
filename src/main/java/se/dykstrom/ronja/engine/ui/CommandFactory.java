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

package se.dykstrom.ronja.engine.ui;

import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.engine.ui.command.*;
import se.dykstrom.ronja.engine.ui.io.Response;
import se.dykstrom.ronja.engine.utils.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory class used to create {@link Command} objects.
 *
 * @author Johan Dykstrom
 */
class CommandFactory {

    /** Maps command name to command class. */
    private static final Map<String, Class<? extends Command>> COMMANDS = new HashMap<>();

    static {
        COMMANDS.put(AcceptedCommand.NAME, AcceptedCommand.class);
        COMMANDS.put(BkCommand.NAME, BkCommand.class);
        COMMANDS.put(BoardCommand.NAME, BoardCommand.class);
        COMMANDS.put(ComputerCommand.NAME, ComputerCommand.class);
        COMMANDS.put(EasyCommand.NAME, EasyCommand.class);
        COMMANDS.put(ForceCommand.NAME, ForceCommand.class);
        COMMANDS.put(GoCommand.NAME, GoCommand.class);
        COMMANDS.put(HardCommand.NAME, HardCommand.class);
        COMMANDS.put(HelpCommand.NAME, HelpCommand.class);
        COMMANDS.put(HintCommand.NAME, HintCommand.class);
        COMMANDS.put(LevelCommand.NAME, LevelCommand.class);
        COMMANDS.put(MovesCommand.NAME, MovesCommand.class);
        COMMANDS.put(NameCommand.NAME, NameCommand.class);
        COMMANDS.put(NewCommand.NAME, NewCommand.class);
        COMMANDS.put(NoPostCommand.NAME, NoPostCommand.class);
        COMMANDS.put(OtimCommand.NAME, OtimCommand.class);
        COMMANDS.put(PingCommand.NAME, PingCommand.class);
        COMMANDS.put(PlayOtherCommand.NAME, PlayOtherCommand.class);
        COMMANDS.put(PostCommand.NAME, PostCommand.class);
        COMMANDS.put(ProtoverCommand.NAME, ProtoverCommand.class);
        COMMANDS.put(QuitCommand.NAME, QuitCommand.class);
        COMMANDS.put(RandomCommand.NAME, RandomCommand.class);
        COMMANDS.put(RejectedCommand.NAME, RejectedCommand.class);
        COMMANDS.put(ResultCommand.NAME, ResultCommand.class);
        COMMANDS.put(SetBoardCommand.NAME, SetBoardCommand.class);
        COMMANDS.put(StCommand.NAME, StCommand.class);
        COMMANDS.put(TimeCommand.NAME, TimeCommand.class);
        COMMANDS.put(UserMoveCommand.NAME, UserMoveCommand.class);
        COMMANDS.put(XBoardCommand.NAME, XBoardCommand.class);
    }

    /**
     * Creates a new command using {@code name} to look up the correct command class. If {@code name} is
     * not the name of a known command, this method returns an instance of {@code InvalidCommand} instead.
     * If the command constructor throws an {@code InvalidCommandException}, this method also returns an
     * instance of {@code InvalidCommand}.
     *
     * @param name The name of the command.
     * @param args The command arguments (optional).
     * @param response The command response object (optional).
     * @param game The game state.
     * @return The created command.
     */
    static Command create(String name, String args, Response response, Game game) {
        Class<? extends Command> clazz = COMMANDS.get(name);
        if (clazz == null) {
            return new InvalidCommand(name, response, "unknown command", game);
        }
        try {
            Constructor<? extends Command> constructor = ClassUtils.getConstructorOrFail(clazz, String.class, Response.class, Game.class);
            return ClassUtils.invokeConstructorOrFail(constructor, args, response, game);
        } catch (Exception e) {
            if (e.getCause() instanceof InvalidCommandException) {
                return new InvalidCommand(name, response, e.getCause().getMessage(), game);
            } else {
                throw e;
            }
        }
    }
}
