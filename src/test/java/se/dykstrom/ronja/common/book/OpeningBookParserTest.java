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

import org.junit.Test;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

/**
 * Tests class {@code OpeningBookParser}.
 *
 * @author Johan Dykstrom
 * @see OpeningBookParser
 */
public class OpeningBookParserTest extends AbstractTestCase {

    private static final String START_E4 = ";e2e4/100;King's Pawn Opening";
    private static final String START_D4 = ";d2d4/50;Queen's Pawn Opening";
    private static final String START_E4_E5 = "e2e4;e7e5/100;King's Pawn Game";
    private static final String START_E4_E5_NF3 = "e2e4 e7e5;g1f3/100;";
    private static final String START_E4_E6 = "e2e4;e7e6/100;French Defense";
    private static final String LINE_WITHOUT_COMMENT = ";e2e4/100;";
    private static final String LINE_WITH_SYNTAX_ERROR = "Syntax Error";

    @Test
    public void shouldParseEmptyFile() throws Exception {
        // When
        Map<Position, List<BookMove>> positions = OpeningBookParser.parseLines(emptyList());

        // Then
        assertEquals(0, positions.size());
    }

    @Test
    public void shouldParseInitialPosition() throws Exception {
        // Given
        List<BookMove> expectedMoves = asList(new BookMove(MOVE_E2E4, 100), new BookMove(MOVE_D2D4, 50));

        // When
        Map<Position, List<BookMove>> positions = OpeningBookParser.parseLines(asList(START_E4, START_D4));

        // Then
        assertEquals(1, positions.size());
        assertEquals(expectedMoves, positions.get(FenParser.parse(FEN_START)));
    }

    @Test
    public void shouldParseAfterE4() throws Exception {
        // Given
        List<BookMove> expectedMoves = asList(new BookMove(MOVE_E7E5, 100), new BookMove(MOVE_E7E6, 100));

        // When
        Map<Position, List<BookMove>> positions = OpeningBookParser.parseLines(asList(START_E4_E5, START_E4_E6));

        // Then
        assertEquals(1, positions.size());
        assertEquals(expectedMoves, positions.get(FenParser.parse(FEN_E4)));
    }

    @Test
    public void shouldParseAfterE4E5() throws Exception {
        // Given
        List<BookMove> expectedMoves = singletonList(new BookMove(MOVE_G1F3, 100));

        // When
        Map<Position, List<BookMove>> positions = OpeningBookParser.parseLines(singletonList(START_E4_E5_NF3));

        // Then
        assertEquals(1, positions.size());
        assertEquals(expectedMoves, positions.get(FenParser.parse(FEN_E4_E5)));
    }

    @Test
    public void shouldParseLineWithoutComment() throws Exception {
        // Given
        List<BookMove> expectedMoves = singletonList(new BookMove(MOVE_E2E4, 100));

        // When
        Map<Position, List<BookMove>> positions = OpeningBookParser.parseLines(singletonList(LINE_WITHOUT_COMMENT));

        // Then
        assertEquals(1, positions.size());
        assertEquals(expectedMoves, positions.get(FenParser.parse(FEN_START)));
    }

    @Test(expected = ParseException.class)
    public void shouldNotParseSyntaxError() throws Exception {
        OpeningBookParser.parseLines(singletonList(LINE_WITH_SYNTAX_ERROR));
    }
}
