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
import org.junit.Test;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.test.AbstractTestCase;

import java.text.ParseException;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
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
    public void testConvertWeightsToPercent() throws Exception {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 100);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 50);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 50);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2);

        List<BookMove> result = OpeningBook.convertWeightsToPercent(bookMoves);
        assertWeights(new int[]{50, 25, 25}, result);
    }

    @Test
    public void testConvertWeightsToPercent_SingleMove() throws Exception {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 30);
        List<BookMove> bookMoves = new ArrayList<>(Collections.singletonList(bookMove0));

        List<BookMove> result = OpeningBook.convertWeightsToPercent(bookMoves);
        assertWeights(new int[]{100}, result);
    }

    @Test
    public void testConvertWeightsToPercent_TotalAlready100() throws Exception {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 30);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 40);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 30);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2);

        List<BookMove> result = OpeningBook.convertWeightsToPercent(bookMoves);
        assertWeights(new int[]{30, 40, 30}, result);
    }

    @Test
    public void testConvertWeightsToPercent_RoundingError() throws Exception {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 100);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 100);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 100);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2);

        List<BookMove> result = OpeningBook.convertWeightsToPercent(bookMoves);
        assertWeights(new int[]{34, 33, 33}, result);
    }

    @Test
    public void testConvertWeightsToPercent_SmallWeights() throws Exception {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 100);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 1);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 1);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2);

        List<BookMove> result = OpeningBook.convertWeightsToPercent(bookMoves);
        assertWeights(new int[]{98, 1, 1}, result);
    }

    @Test
    public void testConvertWeightsToPercent_SomeWeights0() throws Exception {
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
    public void testFindMoveInList() throws Exception {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 30);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 40);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 30);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2);

        Assert.assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 0));
        Assert.assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 29));
        Assert.assertEquals(MOVE_D2D4, OpeningBook.findMoveInList(bookMoves, 30));
        Assert.assertEquals(MOVE_D2D4, OpeningBook.findMoveInList(bookMoves, 69));
        Assert.assertEquals(MOVE_C2C4, OpeningBook.findMoveInList(bookMoves, 70));
        Assert.assertEquals(MOVE_C2C4, OpeningBook.findMoveInList(bookMoves, 99));
    }

    @Test
    public void testFindMoveInList_SingleMove() throws Exception {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 100);
        List<BookMove> bookMoves = Collections.singletonList(bookMove0);

        Assert.assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 0));
        Assert.assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 17));
        Assert.assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 99));
    }

    @Test
    public void testFindMoveInList_AllMoves0() throws Exception {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 0);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 0);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 0);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2);

        assertNull(OpeningBook.findMoveInList(bookMoves, 0));
        assertNull(OpeningBook.findMoveInList(bookMoves, 49));
        assertNull(OpeningBook.findMoveInList(bookMoves, 99));
    }

    @Test
    public void testFindMoveInList_SingleMove0() throws Exception {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 0);
        List<BookMove> bookMoves = Collections.singletonList(bookMove0);

        assertNull(OpeningBook.findMoveInList(bookMoves, 0));
        assertNull(OpeningBook.findMoveInList(bookMoves, 49));
        assertNull(OpeningBook.findMoveInList(bookMoves, 99));
    }

    @Test
    public void testFindMoveInList_SomeMoves0() throws Exception {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 30);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 0);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 40);
        BookMove bookMove3 = new BookMove(MOVE_E7E5, 0);
        BookMove bookMove4 = new BookMove(MOVE_C7C5, 30);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2, bookMove3, bookMove4);

        Assert.assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 0));
        Assert.assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 29));
        Assert.assertEquals(MOVE_C2C4, OpeningBook.findMoveInList(bookMoves, 30));
        Assert.assertEquals(MOVE_C2C4, OpeningBook.findMoveInList(bookMoves, 69));
        Assert.assertEquals(MOVE_C7C5, OpeningBook.findMoveInList(bookMoves, 70));
        Assert.assertEquals(MOVE_C7C5, OpeningBook.findMoveInList(bookMoves, 99));
    }

    @Test
    public void testFindMoveInList_SmallWeights() throws Exception {
        BookMove bookMove0 = new BookMove(MOVE_E2E4, 30);
        BookMove bookMove1 = new BookMove(MOVE_D2D4, 1);
        BookMove bookMove2 = new BookMove(MOVE_C2C4, 1);
        BookMove bookMove3 = new BookMove(MOVE_E7E5, 67);
        BookMove bookMove4 = new BookMove(MOVE_C7C5, 1);
        List<BookMove> bookMoves = Arrays.asList(bookMove0, bookMove1, bookMove2, bookMove3, bookMove4);

        Assert.assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 0));
        Assert.assertEquals(MOVE_E2E4, OpeningBook.findMoveInList(bookMoves, 29));
        Assert.assertEquals(MOVE_D2D4, OpeningBook.findMoveInList(bookMoves, 30));
        Assert.assertEquals(MOVE_C2C4, OpeningBook.findMoveInList(bookMoves, 31));
        Assert.assertEquals(MOVE_E7E5, OpeningBook.findMoveInList(bookMoves, 32));
        Assert.assertEquals(MOVE_E7E5, OpeningBook.findMoveInList(bookMoves, 98));
        Assert.assertEquals(MOVE_C7C5, OpeningBook.findMoveInList(bookMoves, 99));
    }

    @Test
    public void testFindBestMove() throws Exception {
        OpeningBook book = OpeningBook.DEFAULT;
        assertThat(book.findBestMove(parse(FEN_START)), anyOf(equalTo(MOVE_E2E4), equalTo(MOVE_D2D4)));
        assertThat(book.findBestMove(parse(FEN_E4)), equalTo(MOVE_E7E5));
        assertNull(book.findBestMove(parse(FEN_E4_C5)));
        assertNull(book.findBestMove(parse(FEN_SCHOLARS_MATE)));
    }

    @Test
    public void testFindAllMoves() throws Exception {
        OpeningBook book = OpeningBook.DEFAULT;

        assertThat(findAllMoves(book, FEN_START), both(hasItems(MOVE_E2E4, MOVE_D2D4)).and(hasSize(2)));
        assertThat(findAllMoves(book, FEN_E4), both(hasItems(MOVE_E7E5)).and(hasSize(1)));

        assertNull(book.findAllMoves(parse(FEN_E4_C5)));
        assertNull(book.findAllMoves(parse(FEN_SCHOLARS_MATE)));
    }

    /**
     * Finds all book moves in the position identified by {@code fen} and returns those as normal moves.
     */
    private List<Move> findAllMoves(OpeningBook book, String fen) throws ParseException {
        return book.findAllMoves(parse(fen)).stream().map(BookMove::getMove).collect(toList());
    }

    /**
     * Asserts that the weights in the list of book moves equal the weights in {@code expectedWeights}.
     */
    private void assertWeights(int[] expectedWeights, List<BookMove> bookMoves) {
        int[] actualWeights = bookMoves.stream().mapToInt(BookMove::getWeight).toArray();
        assertArrayEquals(expectedWeights, actualWeights);
    }
}
