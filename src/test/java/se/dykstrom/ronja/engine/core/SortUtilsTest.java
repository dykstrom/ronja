/*
 * Copyright (C) 2017 Johan Dykstrom
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

package se.dykstrom.ronja.engine.core;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;
import se.dykstrom.ronja.common.model.Move;

import static org.junit.Assert.assertArrayEquals;
import static se.dykstrom.ronja.common.model.Piece.BISHOP;
import static se.dykstrom.ronja.common.model.Piece.KNIGHT;
import static se.dykstrom.ronja.common.model.Piece.PAWN;
import static se.dykstrom.ronja.common.model.Piece.QUEEN;
import static se.dykstrom.ronja.common.model.Piece.ROOK;
import static se.dykstrom.ronja.common.model.Square.A2_IDX;
import static se.dykstrom.ronja.common.model.Square.A4_IDX;
import static se.dykstrom.ronja.common.model.Square.D2_IDX;
import static se.dykstrom.ronja.common.model.Square.D4_IDX;
import static se.dykstrom.ronja.common.model.Square.D7_IDX;
import static se.dykstrom.ronja.common.model.Square.D8_IDX;
import static se.dykstrom.ronja.common.model.Square.E1_IDX;
import static se.dykstrom.ronja.common.model.Square.E2_IDX;
import static se.dykstrom.ronja.common.model.Square.E4_IDX;
import static se.dykstrom.ronja.common.model.Square.F3_IDX;
import static se.dykstrom.ronja.common.model.Square.G1_IDX;

/**
 * This class is for testing class {@code SortUtils} using JUnit.
 *
 * @author Johan Dykstrom
 * @see SortUtils
 */
public class SortUtilsTest {

    /** A MoveComparator that sorts moves in their natural order. */
    private static final MoveComparator NATURAL_ORDER_COMPARATOR = Integer::compare;

    @Test
    public void shouldSortEmptyArray() {
        // Given
        int[] array = {};
        int[] expected = {};

        // When
        SortUtils.sort(array, array.length, NATURAL_ORDER_COMPARATOR);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortSingleItemArray() {
        // Given
        int[] array = {1};
        int[] expected = {1};

        // When
        SortUtils.sort(array, array.length, NATURAL_ORDER_COMPARATOR);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortShortArray() {
        // Given
        int[] array = {2, 1};
        int[] expected = {1, 2};

        // When
        SortUtils.sort(array, array.length, NATURAL_ORDER_COMPARATOR);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortArray() {
        // Given
        int[] array = {2, 1, 5, 3, 2};
        int[] expected = {1, 2, 2, 3, 5};

        // When
        SortUtils.sort(array, array.length, NATURAL_ORDER_COMPARATOR);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortArrayDescending() {
        // Given
        int[] array = {2, 1, 5, 3, 2};
        int[] expected = {5, 3, 2, 2, 1};

        // When
        SortUtils.sort(array, array.length);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldLongArray() {
        // Given
        int[] array = new int[100];
        int[] expected = new int[100];

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int number = random.nextInt();
            array[i] = number;
            expected[i] = number;
        }

        // When
        SortUtils.sort(array, array.length, NATURAL_ORDER_COMPARATOR);
        Arrays.sort(expected);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortPartOfArray() {
        // Given
        int[] array = {2, 1, 5, 3, 2};
        int[] expected = {1, 2, 5, 3, 2};

        // When
        SortUtils.sort(array, 3, NATURAL_ORDER_COMPARATOR);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortPartOfArrayDescending() {
        // Given
        int[] array = {2, 1, 5, 3, 2};
        int[] expected = {5, 2, 1, 3, 2};

        // When
        SortUtils.sort(array, 3);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortMovesDescending() {
        // Given
        int knightMove = Move.create(KNIGHT, D2_IDX, D4_IDX);
        int pawnMove = Move.create(PAWN, E2_IDX, E4_IDX);
        int pawnCapture = Move.createCapture(PAWN, E2_IDX, F3_IDX, PAWN);
        int rookCapture = Move.createCapture(PAWN, E2_IDX, F3_IDX, ROOK);
        int knightPromotion = Move.createPromotion(E2_IDX, F3_IDX, KNIGHT);
        int queenPromotion = Move.createPromotion(E2_IDX, F3_IDX, QUEEN);

        int[] array = {pawnMove, pawnCapture, knightMove, queenPromotion, knightPromotion, rookCapture};
        int[] expected = {rookCapture, pawnCapture, queenPromotion, knightPromotion, knightMove, pawnMove};

        // When
        SortUtils.sort(array, array.length);

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortBestMoveFirst() {
        // Given
        int bestMove = Move.create(PAWN, D2_IDX, D4_IDX);
        int captureMove = Move.createCapture(PAWN, E2_IDX, F3_IDX, PAWN);
        int pawnMove = Move.create(PAWN, E2_IDX, E4_IDX);

        int[] array = {pawnMove, captureMove, bestMove};
        int[] expected = {bestMove, captureMove, pawnMove};

        // When
        SortUtils.sort(array, array.length, new BestMoveComparator(bestMove));

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortCapturesCorrect() {
        // Given
        int bestMove = Move.create(PAWN, E2_IDX, E4_IDX);
        int bishopCapture = Move.createCapture(PAWN, D2_IDX, D4_IDX, BISHOP);
        int knightCapture = Move.createCapture(PAWN, D2_IDX, D4_IDX, KNIGHT);
        int pawnCapture = Move.createCapture(PAWN, D2_IDX, D4_IDX, PAWN);
        int pawnMove = Move.create(PAWN, A2_IDX, A4_IDX);
        int queenCapture = Move.createCapture(PAWN, D2_IDX, D4_IDX, QUEEN);
        int rookCapture = Move.createCapture(PAWN, D2_IDX, D4_IDX, ROOK);

        int[] array = {bishopCapture, queenCapture, pawnMove, bestMove, rookCapture, pawnCapture, knightCapture};
        int[] expected = {bestMove, queenCapture, rookCapture, bishopCapture, knightCapture, pawnCapture, pawnMove};

        // When
        SortUtils.sort(array, array.length, new BestMoveComparator(bestMove));

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortPromotionsCorrect() {
        // Given
        int bestMove = Move.create(PAWN, E2_IDX, E4_IDX);
        int bishopPromotion = Move.createPromotion(D7_IDX, D8_IDX, BISHOP);
        int knightPromotion = Move.createPromotion(D7_IDX, D8_IDX, KNIGHT);
        int pawnMove = Move.create(PAWN, A2_IDX, A4_IDX);
        int queenPromotion = Move.createPromotion(D7_IDX, D8_IDX, QUEEN);
        int rookPromotion = Move.createPromotion(D7_IDX, D8_IDX, ROOK);

        int[] array = {bishopPromotion, queenPromotion, pawnMove, bestMove, rookPromotion, knightPromotion};
        int[] expected = {bestMove, queenPromotion, rookPromotion, bishopPromotion, knightPromotion, pawnMove};

        // When
        SortUtils.sort(array, array.length, new BestMoveComparator(bestMove));

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortCapturesBeforePromotions() {
        // Given
        int bestMove = Move.create(PAWN, E2_IDX, E4_IDX);
        int queenPromotion = Move.createPromotion(D7_IDX, D8_IDX, QUEEN);
        int rookPromotion = Move.createPromotion(D7_IDX, D8_IDX, ROOK);
        int pawnMove = Move.create(PAWN, A2_IDX, A4_IDX);
        int queenCapture = Move.createCapture(PAWN, D7_IDX, D8_IDX, QUEEN);
        int rookCapture = Move.createCapture(PAWN, D7_IDX, D8_IDX, ROOK);
        int pawnCapture = Move.createCapture(PAWN, D7_IDX, D8_IDX, PAWN);

        int[] array = {queenPromotion, queenCapture, pawnMove, bestMove, pawnCapture, rookCapture, rookPromotion};
        int[] expected = {bestMove, queenCapture, rookCapture, pawnCapture, queenPromotion, rookPromotion, pawnMove};

        // When
        SortUtils.sort(array, array.length, new BestMoveComparator(bestMove));

        // Then
        assertArrayEquals(expected, array);
    }

    @Test
    public void shouldSortCastlingBeforeOrdinaryMove() {
        // Given
        int bestMove = Move.create(PAWN, E2_IDX, E4_IDX);
        int pawnMove = Move.create(PAWN, A2_IDX, A4_IDX);
        int castlingMove = Move.createCastling(E1_IDX, G1_IDX);

        int[] array = {pawnMove, bestMove, castlingMove};
        int[] expected = {bestMove, castlingMove, pawnMove};

        // When
        SortUtils.sort(array, array.length, new BestMoveComparator(bestMove));

        // Then
        assertArrayEquals(expected, array);
    }
}
