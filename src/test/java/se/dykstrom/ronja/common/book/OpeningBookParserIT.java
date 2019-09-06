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

import org.junit.Test;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.CanParser;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;
import se.dykstrom.ronja.test.TestUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.*;
import static se.dykstrom.ronja.test.SizeMatcher.hasSize;

/**
 * This class is for integration testing class {@code OpeningBookParser} using JUnit.
 *
 * @author Johan Dykstrom
 * @see OpeningBookParser
 */
public class OpeningBookParserIT extends AbstractTestCase {

    @Test
    public void shouldParseValidFile() throws Exception {
        OpeningBook book = OpeningBookParser.parse(TestUtils.createBookFile());
        assertEquals(4, book.size());

        List<BookMove> moves = book.findAllMoves(FenParser.parse(FEN_START));
        assertEquals(1, moves.size());
        assertEquals(MOVE_E2E4, moves.get(0).getMove());

        moves = book.findAllMoves(FenParser.parse(FEN_E4));
        assertThat(moves, hasSize(2));
        assertTrue(moves.stream().map(bm -> CanParser.format(bm.getMove())).allMatch(s -> s.matches("^e7(e5|e6)$")));

        // Check some non-existing positions
        assertNull(book.findAllMoves(FenParser.parse(FEN_E4_C5)));
        assertNull(book.findAllMoves(FenParser.parse(FEN_SCHOLARS_MATE)));
        assertNull(book.findAllMoves(FenParser.parse(FEN_END_GAME_0)));
    }

    @Test(expected = ParseException.class)
    public void shouldNotParseSyntaxError() throws Exception {
        File file = File.createTempFile("ronja_syntax_", ".csv");
        file.deleteOnExit();

        try (PrintStream out = new PrintStream(file, StandardCharsets.UTF_8)) {
            out.println("foo");
        }

        OpeningBookParser.parse(file);
    }

    @Test
    public void shouldNotIncludeInvalidMove() throws Exception {
        // Given
        File file = File.createTempFile("ronja_move_", ".csv");
        file.deleteOnExit();

        try (PrintStream out = new PrintStream(file, StandardCharsets.UTF_8)) {
            out.println(";foo/100;Initial position");
        }

        // When
        OpeningBook book = OpeningBookParser.parse(file);

        // Then
        assertEquals(1, book.size());
        assertEquals(0, book.findAllMoves(Position.START).size());
    }

    @Test
    public void shouldNotIncludeInvalidWeight() throws Exception {
        // Given
        File file = File.createTempFile("ronja_weight_", ".csv");
        file.deleteOnExit();

        try (PrintStream out = new PrintStream(file, StandardCharsets.UTF_8)) {
            out.println(";e2e4/foo;Initial position");
        }

        // When
        OpeningBook book = OpeningBookParser.parse(file);

        // Then
        assertEquals(1, book.size());
        assertEquals(0, book.findAllMoves(Position.START).size());
    }

    @Test
    public void shouldParseEmptyFile() throws Exception {
        // Given
        File file = File.createTempFile("ronja_empty_", ".csv");
        file.deleteOnExit();

        // When
        OpeningBook book = OpeningBookParser.parse(file);

        // Then
        assertEquals(0, book.size());
    }

    @Test
    public void shouldParseRealOpeningBook() throws Exception {
        // Given
        File file = Paths.get("target/scripts/book.csv").toFile();
        assertTrue("File not found: " + file.getPath() + ". Current directory: " + Paths.get("").toFile().getAbsolutePath(), file.exists());

        // When
        OpeningBook book = OpeningBookParser.parse(file);

        // Then
        System.out.println("Opening book contains " + book.size() + " positions.");
        assertTrue(book.size() > 0);
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionWhenMissingFile() throws Exception {
        OpeningBook book = OpeningBookParser.parse(new File("does_not_exist.csv"));
        assertNull(book);
    }
}
