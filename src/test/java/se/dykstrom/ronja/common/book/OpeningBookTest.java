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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayContaining.hasItemInArray;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static se.dykstrom.ronja.common.parser.FenParser.parse;
import static se.dykstrom.ronja.test.SizeMatcher.hasSize;

/**
 * This class is for testing class {@code OpeningBook} using JUnit.
 *
 * @author Johan Dykstrom
 * @see OpeningBook
 */
public class OpeningBookTest extends AbstractTestCase {

    @Test
    public void testConvertWeightsToPercent() {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 100);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 50);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 50);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2);

        List<BookMove> result = OpeningBook.convertWeightsToPercent(bookMoves);
        assertWeights(new int[]{50, 25, 25}, result);
    }

    @Test
    public void testConvertWeightsToPercent_SingleMove() {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 30);
        List<BookMove> bookMoves = new ArrayList<>(Collections.singletonList(bookMove0));

        List<BookMove> result = OpeningBook.convertWeightsToPercent(bookMoves);
        assertWeights(new int[]{100}, result);
    }

    @Test
    public void testConvertWeightsToPercent_TotalAlready100() {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 30);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 40);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 30);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2);

        List<BookMove> result = OpeningBook.convertWeightsToPercent(bookMoves);
        assertWeights(new int[]{30, 40, 30}, result);
    }

    @Test
    public void testConvertWeightsToPercent_RoundingError() {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 100);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 100);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 100);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2);

        List<BookMove> result = OpeningBook.convertWeightsToPercent(bookMoves);
        assertWeights(new int[]{34, 33, 33}, result);
    }

    @Test
    public void testConvertWeightsToPercent_SmallWeights() {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 100);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 1);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 1);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2);

        List<BookMove> result = OpeningBook.convertWeightsToPercent(bookMoves);
        assertWeights(new int[]{98, 1, 1}, result);
    }

    @Test
    public void testConvertWeightsToPercent_SomeWeights0() {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 0);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 140);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 40);
        BookMove bookMove3 = new BookMove(MOVE_E7E5, 0);
        BookMove bookMove4 = new BookMove(MOVE_C7C5, 20);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2, bookMove3, bookMove4);

        List<BookMove> result = OpeningBook.convertWeightsToPercent(bookMoves);
        assertWeights(new int[]{0, 70, 20, 0, 10}, result);
    }

    @Test
    public void testConvertAllWeightsToPercent() throws Exception {
        BookMove bookMoveE4 = new BookMove(MOVE_E2E4, 100);
        BookMove bookMoveD4 = new BookMove(MOVE_D2D4, 50);
        BookMove bookMoveC4 = new BookMove(MOVE_C2C4, 50);
        BookMove bookMoveE5 = new BookMove(MOVE_E7E5, 120);
        BookMove bookMoveC5 = new BookMove(MOVE_C7C5, 80);
        BookMove bookMoveD4AfterE4C5 = new BookMove(MOVE_D2D4, 70);

        List<BookMove> bookMovesStart = new ArrayList<>(Arrays.asList(bookMoveE4, bookMoveD4, bookMoveC4));
        List<BookMove> bookMovesAfterE4 = new ArrayList<>(Arrays.asList(bookMoveE5, bookMoveC5));
        List<BookMove> bookMovesAfterE4C5 = new ArrayList<>(Collections.singletonList(bookMoveD4AfterE4C5));

        Map<Position, List<BookMove>> book = new HashMap<>();
        book.put(parse(FEN_START), bookMovesStart);
        book.put(parse(FEN_E4), bookMovesAfterE4);
        book.put(parse(FEN_E4_C5), bookMovesAfterE4C5);

        Map<Position, List<BookMove>> result = OpeningBook.convertAllWeightsToPercent(book);
        assertEquals(3, result.size());
        assertWeights(new int[]{50, 25, 25}, result.get(parse(FEN_START)));
        assertWeights(new int[]{60, 40}, result.get(parse(FEN_E4)));
        assertWeights(new int[]{100}, result.get(parse(FEN_E4_C5)));
    }

    @Test
    public void testFindMoveInList() {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 30);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 40);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 30);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2);

        assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 0));
        assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 29));
        assertEquals(MOVE_D2D4, OpeningBook.findMoveInList(bookMoves, 30));
        assertEquals(MOVE_D2D4, OpeningBook.findMoveInList(bookMoves, 69));
        assertEquals(MOVE_C2C4, OpeningBook.findMoveInList(bookMoves, 70));
        assertEquals(MOVE_C2C4, OpeningBook.findMoveInList(bookMoves, 99));
    }

    @Test
    public void testFindMoveInList_SingleMove() {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 100);
        List<BookMove> bookMoves = Collections.singletonList(bookMove0);

        assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 0));
        assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 17));
        assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 99));
    }

    @Test
    public void testFindMoveInList_AllMoves0() {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 0);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 0);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 0);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2);

        assertEquals(0, OpeningBook.findMoveInList(bookMoves, 0));
        assertEquals(0, OpeningBook.findMoveInList(bookMoves, 49));
        assertEquals(0, OpeningBook.findMoveInList(bookMoves, 99));
    }

    @Test
    public void testFindMoveInList_SingleMove0() {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 0);
        List<BookMove> bookMoves = Collections.singletonList(bookMove0);

        assertEquals(0, OpeningBook.findMoveInList(bookMoves, 0));
        assertEquals(0, OpeningBook.findMoveInList(bookMoves, 49));
        assertEquals(0, OpeningBook.findMoveInList(bookMoves, 99));
    }

    @Test
    public void testFindMoveInList_SomeMoves0() {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 30);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 0);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 40);
        BookMove bookMove3 = new BookMove(MOVE_E7E5, 0);
        BookMove bookMove4 = new BookMove(MOVE_C7C5, 30);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2, bookMove3, bookMove4);

        assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 0));
        assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 29));
        assertEquals(MOVE_C2C4, OpeningBook.findMoveInList(bookMoves, 30));
        assertEquals(MOVE_C2C4, OpeningBook.findMoveInList(bookMoves, 69));
        assertEquals(MOVE_C7C5, OpeningBook.findMoveInList(bookMoves, 70));
        assertEquals(MOVE_C7C5, OpeningBook.findMoveInList(bookMoves, 99));
    }

    @Test
    public void testFindMoveInList_SmallWeights() {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 30);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 1);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 1);
        BookMove bookMove3 = new BookMove(MOVE_E7E5, 67);
        BookMove bookMove4 = new BookMove(MOVE_C7C5, 1);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2, bookMove3, bookMove4);

        assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 0));
        assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 29));
        assertEquals(MOVE_D2D4, OpeningBook.findMoveInList(bookMoves, 30));
        assertEquals(MOVE_C2C4, OpeningBook.findMoveInList(bookMoves, 31));
        assertEquals(MOVE_E7E5, OpeningBook.findMoveInList(bookMoves, 32));
        assertEquals(MOVE_E7E5, OpeningBook.findMoveInList(bookMoves, 98));
        assertEquals(MOVE_C7C5, OpeningBook.findMoveInList(bookMoves, 99));
    }

    @Test
    public void testFindBestMove() throws Exception {
        OpeningBook book = OpeningBook.DEFAULT;
        assertThat(book.findBestMove(parse(FEN_START)), anyOf(is(MOVE_E2E4), is(MOVE_D2D4)));
        assertThat(book.findBestMove(parse(FEN_E4)), is(MOVE_E7E5));
        assertEquals(0, book.findBestMove(parse(FEN_E4_C5)));
        assertEquals(0, book.findBestMove(parse(FEN_SCHOLARS_MATE)));
    }

    @Test
    public void testFindAllMoves() throws Exception {
        OpeningBook book = OpeningBook.DEFAULT;

        Integer[] moves = ArrayUtils.toObject(findAllMoves(book, FEN_START));
        assertThat(moves, hasSize(2));
        assertThat(moves, both(hasItemInArray(MOVE_E2E4)).and(hasItemInArray(MOVE_D2D4)));
        moves = ArrayUtils.toObject(findAllMoves(book, FEN_E4));
        assertThat(moves, hasSize(1));
        assertThat(moves, hasItemInArray(MOVE_E7E5));

        assertNull(book.findAllMoves(parse(FEN_E4_C5)));
        assertNull(book.findAllMoves(parse(FEN_SCHOLARS_MATE)));
    }

    /**
     * Finds all book moves in the position identified by {@code fen} and returns those as normal moves.
     */
    private int[] findAllMoves(OpeningBook book, String fen) throws ParseException {
        return book.findAllMoves(parse(fen)).stream().map(BookMove::getMove).mapToInt(move -> move).toArray();
    }

    /**
     * Asserts that the weights in the list of book moves equal the weights in {@code expectedWeights}.
     */
    private void assertWeights(int[] expectedWeights, List<BookMove> bookMoves) {
        int[] actualWeights = bookMoves.stream().mapToInt(BookMove::getWeight).toArray();
        assertArrayEquals(expectedWeights, actualWeights);
    }
}
