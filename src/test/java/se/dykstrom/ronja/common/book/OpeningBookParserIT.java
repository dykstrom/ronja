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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import se.dykstrom.ronja.common.parser.CanParser;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;
import se.dykstrom.ronja.test.TestUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This class is for integration testing class {@code OpeningBookParser} using JUnit.
 *
 * @author Johan Dykstrom
 * @see OpeningBookParser
 */
public class OpeningBookParserIT extends AbstractTestCase {

    private static File bookFile;

    @BeforeClass
    public static void setUpClass() throws Exception {
        bookFile = TestUtils.createBookFile();
    }

    // ------------------------------------------------------------------------

    @Test
    public void testOpeningBookParser_ValidFile() throws Exception {
        OpeningBook book = OpeningBookParser.parse(bookFile);
        assertEquals(4, book.size());

        List<BookMove> moves = book.findAllMoves(FenParser.parse(FEN_START));
        assertEquals(1, moves.size());
        Assert.assertEquals("e2e4", CanParser.format(moves.get(0).getMove()));

        moves = book.findAllMoves(FenParser.parse(FEN_E4));
        assertEquals(2, moves.size());
        assertTrue(moves.stream().map(bm -> CanParser.format(bm.getMove())).allMatch(s -> s.matches("^e7(e5|e6)$")));

        // Check some non-existing positions
        assertNull(book.findAllMoves(FenParser.parse(FEN_E4_C5)));
        assertNull(book.findAllMoves(FenParser.parse(FEN_SCHOLARS_MATE)));
        assertNull(book.findAllMoves(FenParser.parse(FEN_END_GAME_0)));
    }

    @Test(expected = ParseException.class)
    public void testOpeningBookParser_InvalidFile_MissingTag() throws Exception {
        File file = File.createTempFile("ronja_tag_", ".xml");
        file.deleteOnExit();

        PrintStream out = new PrintStream(file, "ISO-8859-1");
        out.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
        out.println("<move can='' weight='' name='Initial position'>");
        out.println("  <move can='e2e4' weight='100'>");
        out.println("    <move can='e7e6' weight='100'/>");
        out.println("</move>");
        out.close();

        OpeningBook book = OpeningBookParser.parse(file);
        assertNull(book);
    }

    @Test
    public void testOpeningBookParser_InvalidFile_MissingAttribute() throws Exception {
        File file = File.createTempFile("ronja_attribute_", ".xml");
        file.deleteOnExit();

        PrintStream out = new PrintStream(file, "ISO-8859-1");
        out.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
        out.println("<move can='' weight='' name='Initial position'>");
        out.println("  <move weight='100'>"); // Missing can
        out.println("    <move can='e7e6' weight='100'/>");
        out.println("  </move>");
        out.println("  <move can='d2d4' weight='100'/>");
        out.println("</move>");
        out.close();

        OpeningBook book = OpeningBookParser.parse(file);
        assertEquals(1, book.size()); // Initial position
    }

    @Test
    public void testOpeningBookParser_InvalidFile_InvalidMove() throws Exception {
        File file = File.createTempFile("ronja_move_", ".xml");
        file.deleteOnExit();

        PrintStream out = new PrintStream(file, "ISO-8859-1");
        out.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
        out.println("<move can='' weight='' name='Initial position'>");
        out.println("  <move can='e2e4' weight='100'>");
        out.println("    <move can='e6d6' weight='100'/>"); // Invalid move
        out.println("  </move>");
        out.println("  <move can='d2d4' weight='100'/>");
        out.println("</move>");
        out.close();

        OpeningBook book = OpeningBookParser.parse(file);
        assertEquals(2, book.size()); // Initial position and position after e2e4
    }

    @Test(expected = ParseException.class)
    public void testOpeningBookParser_InvalidFile_InvalidWeight() throws Exception {
        File file = File.createTempFile("ronja_tag_", ".xml");
        file.deleteOnExit();

        PrintStream out = new PrintStream(file, "ISO-8859-1");
        out.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
        out.println("<move can='' weight='' name='Initial position'>");
        out.println("  <move can='e2e4' weight='foo'>"); // Invalid weight
        out.println("    <move can='e7e6' weight='100'/>");
        out.println("  </move>");
        out.println("</move>");
        out.close();

        OpeningBook book = OpeningBookParser.parse(file);
        assertNull(book);
    }

    @Test
    public void testOpeningBookParser_EmptyFile() throws Exception {
        File file = File.createTempFile("ronja_empty_", ".xml");
        file.deleteOnExit();

        PrintStream out = new PrintStream(file, "ISO-8859-1");
        out.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
        out.println("<move can='' weight='' name='Initial position'>");
        out.println("</move>");
        out.close();

        OpeningBook book = OpeningBookParser.parse(file);
        assertEquals(0, book.size());
    }

    @Test(expected = IOException.class)
    public void testOpeningBookParser_NoFile() throws Exception {
        OpeningBook book = OpeningBookParser.parse(new File("foo.xml"));
        assertNull(book);
    }
}
