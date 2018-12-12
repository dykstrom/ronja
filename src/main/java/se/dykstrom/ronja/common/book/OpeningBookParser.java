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

package se.dykstrom.ronja.common.book;

import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.IllegalMoveException;
import se.dykstrom.ronja.common.parser.MoveParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * A class for parsing the Ronja opening book file. The opening book file is a CSV file
 * containing one line for each opening line. Each line contains three fields separated
 * by semicolons: a number of moves leading to a position, a book move and its weight,
 * and a comment describing the position after the book move.
 *
 * @author Johan Dykstrom
 */
public class OpeningBookParser {

    private static final Logger TLOG = Logger.getLogger(OpeningBookParser.class.getName());

    /**
     * Loads the opening book file.
     *
     * @param file The opening book file.
     * @return The opening book.
     * @throws IOException If the opening book file cannot be read.
     * @throws ParseException If the opening book file cannot be parsed.
     */
    public static OpeningBook parse(File file) throws IOException, ParseException {
        Map<Position, List<BookMove>> positions;

        long start = System.currentTimeMillis();
        try {
            // Read all lines and remove empty lines and comments
            List<String> lines = Files.lines(file.toPath())
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .collect(toList());
            positions = parseLines(lines);
        } catch (IOException e) {
            TLOG.severe("Failed to open file '" + file.getName() + "': " + e);
            throw e;
        }
        long stop = System.currentTimeMillis();
        TLOG.info("Loaded opening book in " + (stop - start) + " ms");

        // Do some counting
        Set<Integer> hashCodes = positions.keySet().stream().map(Position::hashCode).collect(toSet());
        TLOG.info("Unique positions: " + positions.size() + ", unique hash codes: " + hashCodes.size());

        return new OpeningBook(positions);
    }

    /**
     * Parses all non-empty lines that were read from the opening book file.
     *
     * @param lines The opening lines to parse.
     * @return A map of positions and book moves.
     */
    static Map<Position, List<BookMove>> parseLines(List<String> lines) throws ParseException {
        Map<Position, List<BookMove>> positions = new HashMap<>();

        for (String line : lines) {
            String[] fields = line.split(";", -1);
            if (fields.length != 3) {
                throw new ParseException("Syntax error on line '" + line + "'", 0);
            }

            String[] moves = fields[0].trim().split(" ");
            String bookMove = fields[1].trim();
            // Ignore the comment field

            // Create book position from available moves
            Position position;
            try {
                if (moves.length == 1 && moves[0].isBlank()) {
                    position = Position.START;
                } else {
                    position = Position.of(asList(moves));
                }
            } catch (IllegalMoveException e) {
                TLOG.warning("Illegal move in opening line " + Arrays.toString(moves) + ": " + e);
                continue;
            }

            // Get/create the list of possible moves for this position
            List<BookMove> list = positions.computeIfAbsent(position, key -> new ArrayList<>());

            // Create book move and add to list
            try {
                String[] moveAndWeight = bookMove.split(("/"));
                list.add(new BookMove(MoveParser.parse(moveAndWeight[0], position), Integer.parseInt(moveAndWeight[1])));
            } catch (IllegalMoveException e) {
                TLOG.warning("Illegal move in opening line " + Arrays.toString(moves) + ": " + bookMove);
            } catch (NumberFormatException e) {
                TLOG.warning("Illegal weight in opening line " + Arrays.toString(moves) + ": " + bookMove);
            }
        }

        return positions;
    }
}
