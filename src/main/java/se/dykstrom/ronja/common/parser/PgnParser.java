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

package se.dykstrom.ronja.common.parser;

import se.dykstrom.ronja.common.model.Color;
import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.engine.utils.AppConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that can parse and format files specified in Portable Game Notation (PGN).
 *
 * @author Johan Dykstrom
 */
public final class PgnParser {

    private static final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm");

    private static final String BACKSLASH_REPLACEMENT = Matcher.quoteReplacement("\\\\");
    private static final String QUOTE_REPLACEMENT = Matcher.quoteReplacement("\\\"");

    /** The maximum line length of a line of moves. */
    private static final int MAX_LINE_LENGTH = 80;

    /** Platform specific end-of-line string. */
    private static final String EOL = System.lineSeparator();

    /** A regex pattern that matches the result string sent by XBoard. */
    private static final Pattern RESULT_PATTERN = Pattern.compile("^([012/-]+) \\{.*}$");

    /**
     * Formats the given {@code game} as a PGN string.
     *
     * @param game The game to format.
     * @return The game contents as a PGN formatted string.
     */
    public static String format(Game game) {
        return getHeaders(game) + EOL + getMoves(game) + EOL + getResult(game) + EOL;
    }

    /**
     * Returns a string containing all the PGN format headers.
     */
    private static String getHeaders(Game game) {
        StringBuilder builder = new StringBuilder();
        builder.append(getHeader("Event", getEvent())).append(EOL);
        builder.append(getHeader("Site", getSite())).append(EOL);
        builder.append(getHeader("Round", getRound())).append(EOL);
        builder.append(getHeader("White", getWhite(game))).append(EOL);
        builder.append(getHeader("Black", getBlack(game))).append(EOL);
        builder.append(getHeader("Result", getShortResult(game))).append(EOL);
        builder.append(getHeader("Date", getDate(game))).append(EOL);
        builder.append(getHeader("Time", getTime(game))).append(EOL);
        if (!game.getStartPosition().equals(Position.START)) {
            builder.append(getHeader("SetUp", "1")).append(EOL);
            builder.append(getHeader("FEN", getFen(game))).append(EOL);
        }
        return builder.toString();
    }

    private static String getHeader(String key, String value) {
        return "[" + key + " \"" + escape(value) + "\"]";
    }

    /**
     * Returns a string containing all the moves in the game formatted in SAN format.
     */
    private static String getMoves(Game game) {
        StringBuilder text = new StringBuilder();
        StringBuilder line = new StringBuilder();

        int moveNumber = game.getStartMoveNumber();
        Iterator<String> iterator = SanParser.format(game.getStartPosition(), game.getMoves()).iterator();
        // If the game was setup, and the first move was by black, we need some special formatting
        if (!game.getStartPosition().isWhiteMove() && iterator.hasNext()) {
            line.append(String.format("%d... %s ", moveNumber++, iterator.next()));
        }
        while (iterator.hasNext()) {
            String whiteMove = iterator.next();
            String blackMove = iterator.hasNext() ? iterator.next() : "";
            String formattedMoves = String.format("%d. %s %s ", moveNumber++, whiteMove, blackMove);

            if (isLineTooLong(line, formattedMoves)) {
                text.append(line).append(EOL);
                line = new StringBuilder();
            }
            line.append(formattedMoves);
        }
        text.append(line);

        return text.toString();
    }

    /**
     * Returns {@code true} if the line formed by appending {@code pair} to {@code line}
     * will be longer than the maximum line length.
     */
    private static boolean isLineTooLong(StringBuilder line, String pair) {
        return line.length() + pair.length() > MAX_LINE_LENGTH;
    }

    /**
     * Returns a full result string, for example "1-0 {White mates}". If the result is "*",
     * which means unfinished, an empty string is returned instead.
     */
    private static String getResult(Game game) {
        return game.getResult().equals("*") ? "" : game.getResult();
    }

    /**
     * Returns a short result string, for example "1-0".
     *
     * @param game The game to get the result from.
     * @return The result.
     */
    static String getShortResult(Game game) {
        String result = game.getResult();
        Matcher matcher = RESULT_PATTERN.matcher(result);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return result;
        }
    }

    /**
     * Returns the name of the black player.
     */
    private static String getBlack(Game game) {
        if (Color.BLACK == game.getEngineColor()) {
            return AppConfig.getEngineName();
        } else if (Color.WHITE == game.getEngineColor()) {
            return game.getOpponent();
        } else {
            return null;
        }
    }

    /**
     * Returns the name of the white player.
     */
    private static String getWhite(Game game) {
        if (Color.WHITE == game.getEngineColor()) {
            return AppConfig.getEngineName();
        } else if (Color.BLACK == game.getEngineColor()) {
            return game.getOpponent();
        } else {
            return null;
        }
    }

    private static String getDate(Game game) {
        return DF.format(game.getStartTime());
    }

    private static String getTime(Game game) {
        return TF.format(game.getStartTime());
    }

    private static String getFen(Game game) {
        return FenParser.format(game.getStartPosition());
    }

    private static String getSite() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignore) {
            return "<unknown>";
        }
    }

    @SuppressWarnings("SameReturnValue")
    private static String getRound() {
        return "*";
    }

    @SuppressWarnings("SameReturnValue")
    private static String getEvent() {
        return "Chess Game";
    }

    /**
     * Escapes the string {@code s} as described in the PGN file specification.
     *
     * @param s The string to escape.
     * @return The escaped string.
     */
    static String escape(String s) {
        return (s == null) ? "" : s.replaceAll("\\\\", BACKSLASH_REPLACEMENT).replaceAll("\"", QUOTE_REPLACEMENT);
    }
}
