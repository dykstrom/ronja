/*
 * Copyright (C) 2018 Johan Dykstrom
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

import org.junit.Before;
import org.junit.Test;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.IllegalMoveException;
import se.dykstrom.ronja.common.parser.MoveParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;

/**
 * A JUnit test case that is used to convert the opening book from XML format to CSV format,
 * and test that the conversion was successful.
 *
 * @author Johan Dykstrom
 * @see OpeningBookParser
 */
public class OpeningBookConversionIT extends AbstractTestCase {

    private static final Logger TLOG = Logger.getLogger(OpeningBookConversionIT.class.getName());

    private static final String ORIGINAL_FILE_NAME = "target/scripts/book.xml";
    private static final String CONVERTED_FILE_NAME = "target/scripts/book.csv";

    private File originalFile;
    private File convertedFile;

    private final List<String> convertedLines = new ArrayList<>();

    @Before
    public void setUp() {
        originalFile = new File(ORIGINAL_FILE_NAME);
        assertTrue(originalFile.exists());
        convertedFile = new File(CONVERTED_FILE_NAME);
    }

    @Test
    public void shouldConvertFile() throws Exception {
        // Read original opening book
        System.out.println("Reading " + originalFile + "...");
        Map<Position, List<BookMove>> originalBook = parseXmlFile(originalFile);
        assertFalse(originalBook.isEmpty());

        // Write opening book in CSV format
        System.out.println("Writing " + convertedFile + "...");
        writeConvertedBook(convertedLines, convertedFile);

        // Read converted opening book
        System.out.println("Reading " + convertedFile + "...");
        Map<Position, List<BookMove>> convertedBook = readConvertedBook(convertedFile);
        assertFalse(convertedBook.isEmpty());

        // Compare opening books
        System.out.println("Converting...");
        assertEquals(originalBook, convertedBook);
    }

    private void writeConvertedBook(List<String> convertedLines, File convertedFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(convertedFile)) {
            convertedLines.forEach(writer::println);
        }
    }

    private Map<Position, List<BookMove>> readConvertedBook(File convertedFile) throws Exception {
        return OpeningBookParser.parseLines(Files.readAllLines(convertedFile.toPath()));
    }

    // -----------------------------------------------------------------------

    /** Positions and corresponding moves. */
    private Map<Position, List<BookMove>> positions;

    /**
     * Loads the original XML opening book file.
     *
     * @param file The opening book file.
     * @return A hash map of positions and moves.
     * @throws IOException If the opening book file cannot be read.
     * @throws ParseException If the opening book file cannot be parsed.
     */
    private Map<Position, List<BookMove>> parseXmlFile(File file) throws IOException, ParseException {
        positions = new HashMap<>();

        long start = System.currentTimeMillis();
        try {
            JAXBContext context = JAXBContext.newInstance(XmlBookMove.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            XmlBookMove topMove = (XmlBookMove) unmarshaller.unmarshal(file);
            for (XmlBookMove move : topMove.getSubMoves()) {
                parseMove(new LinkedList<>(), move);
            }
        } catch (JAXBException | NumberFormatException e) {
            TLOG.severe("Failed to load file '" + file.getName() + "': " + e);
            throw new ParseException("Failed to load file '" + file.getName() + "'", 0);
        } catch (IllegalArgumentException e) {
            TLOG.severe("Failed to load file '" + file.getName() + "': " + e);
            throw new IOException("File not found: " + file.getName(), e);
        }
        long stop = System.currentTimeMillis();
        TLOG.info("Loaded opening book in " + (stop - start) + " ms");

        // Do some counting
        Set<Integer> hashCodes = positions.keySet().stream().map(Position::hashCode).collect(toSet());
        TLOG.info("Unique positions: " + positions.size() + ", unique hash codes: " + hashCodes.size());

        return positions;
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
    private void parseMove(LinkedList<String> moves, XmlBookMove xmlMove) {
        // Get attribute values
        String move = xmlMove.getCan();
        int weight = xmlMove.getWeight();
        String name = xmlMove.getName() != null ? xmlMove.getName() : "";

        // Add this move to the opening book
        try {
            add(moves, move, weight);
            String line = String.join(" ", moves) + ";" + move + "/" + weight + ";" + name;
            convertedLines.add(line);
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
    private void add(List<String> moves, String move, int weight) throws IllegalMoveException {
        // Set up a new position
        Position position = Position.of(moves);

        // Get the list of possible moves for this position
        List<BookMove> list = positions.computeIfAbsent(position, key -> new ArrayList<>());

        // Create the new move and add it to the list
        list.add(new BookMove(MoveParser.parse(move, position), weight));
    }
}
