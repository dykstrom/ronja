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

import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.IllegalMoveException;
import se.dykstrom.ronja.common.parser.MoveParser;

/**
 * A class for parsing the Ronja opening book file. The opening book file should
 * be an XML file containing a tree of 'move' elements that represent the moves
 * in the different opening lines. The moves are specified in coordinate
 * algebraic notation, and are associated with a weight. A move with weight 0
 * will never be played, but it can still have sub moves in the opening line
 * tree. This is an example of an opening book file:
 *
 * <?xml version="1.0" encoding="ISO-8859-1"?>
 *
 * <move can="" weight="">
 *     <move can="e2e4" weight="100" name="King's Pawn Opening">
 *         <move can="e7e5" weight="100">
 *             <move can="g1f3" weight="100"/>
 *         </move>
 *         <move can="e7e6" weight="100">
 *             <move can="d2d4" weight="100"/>
 *         </move>
 *     </move>
 * </move>
 *
 * @author Johan Dykstrom
 */
public class OpeningBookParser {

    private static final Logger TLOG = Logger.getLogger(OpeningBookParser.class.getName());

    /** Positions and corresponding moves. */
    private static Map<Position, List<BookMove>> positions;

    // ------------------------------------------------------------------------

    /**
     * Loads the opening book file.
     *
     * @param file The opening book file.
     * @return The opening book.
     * @throws ParseException If the opening book file cannot be loaded.
     * @throws ParseException If the opening book file cannot be parsed.
     */
    public static OpeningBook parse(File file) throws IOException, ParseException {
        positions = new HashMap<>();

        long start = System.currentTimeMillis();
        try {
            JAXBContext context = JAXBContext.newInstance(XmlBookMove.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            XmlBookMove topMove = (XmlBookMove) unmarshaller.unmarshal(file);
            for (XmlBookMove move : topMove.getSubMoves()) {
                parseMove(new LinkedList<>(), move);
            }
        } catch (JAXBException e) {
            TLOG.severe("Failed to load file '" + file.getName() + "': " + e);
            if (e.getLinkedException() instanceof IOException) {
                throw (IOException) e.getLinkedException();
            } else {
                throw new ParseException("Failed to load file '" + file.getName() + "'", 0);
            }
        } catch (NumberFormatException e) {
            TLOG.severe("Failed to load file '" + file.getName() + "': " + e);
            throw new ParseException("Failed to load file '" + file.getName() + "'", 0);
        }
        long stop = System.currentTimeMillis();
        TLOG.info("Loaded opening book in " + (stop - start) + " ms");

        // Do some counting
        Set<Integer> hashCodes = positions.keySet().stream().map(Position::hashCode).collect(toSet());
        TLOG.info("Unique positions: " + positions.size() + ", unique hash codes: " + hashCodes.size());

        return new OpeningBook(positions);
    }

    /**
     * Parses a move, adds it to the opening book as a possible move in the
     * current position, and parses all sub moves recursively. If the given
     * move is invalid, this move and all of its sub moves are ignored, but
     * the rest of the opening book is still read.
     *
     * @param moves The list of moves made so far in this opening line.
     * @param xmlMove An XML move read from the opening book file.
     */
    private static void parseMove(LinkedList<String> moves, XmlBookMove xmlMove) {
        // Get attribute values
        String move = xmlMove.getCan();
        int weight = xmlMove.getWeight();

        // Add this move to the opening book
        try {
            add(moves, move, weight);
        } catch (IllegalMoveException ime) {
            TLOG.warning("Illegal move [" + move + ", " + weight + "] in opening line " + moves + ": " + ime);
            return;
        }

        // If we could add this move OK, continue to parse each sub move recursively
        moves.addLast(move);
        for (XmlBookMove subMove : xmlMove.getSubMoves()) {
            parseMove(moves, subMove);
        }
        moves.removeLast();
    }

    /**
     * Adds the supplied move as a possible move in the current position.
     *
     * @param moves The list of moves made so far in this opening line.
     * @param move The move to add, in coordinate algebraic notation.
     * @param weight The weight of the move in this position.
     * @throws IllegalMoveException If any of the moves in the move list, or the new move, is illegal.
     */
    private static void add(List<String> moves, String move, int weight) throws IllegalMoveException {
        // Set up a new position
        Position position = Position.of(moves);

        // Get the list of possible moves for this position
        List<BookMove> list = positions.computeIfAbsent(position, key -> new ArrayList<>());

        // Create the new move and add it to the list
        list.add(new BookMove(MoveParser.parse(move, position), weight));
    }
}
