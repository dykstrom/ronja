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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.IllegalMoveException;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static se.dykstrom.ronja.common.model.Piece.PAWN;
import static se.dykstrom.ronja.common.model.Square.D2_IDX;
import static se.dykstrom.ronja.common.model.Square.D4_IDX;
import static se.dykstrom.ronja.common.model.Square.D5_IDX;
import static se.dykstrom.ronja.common.model.Square.D7_IDX;
import static se.dykstrom.ronja.common.model.Square.E2_IDX;
import static se.dykstrom.ronja.common.model.Square.E4_IDX;
import static se.dykstrom.ronja.common.model.Square.E5_IDX;
import static se.dykstrom.ronja.common.model.Square.E7_IDX;

/**
 * A chess game opening book, that reads its opening moves from a file that is
 * specified when creating the opening book. Each chess board position that can
 * occur in the different opening lines is stored together with the known moves
 * for that position, and is used when the user requests a move for that
 * certain position.
 *
 * @author Johan Dykstrom
 */
public class OpeningBook {

    /** A small opening book to use if the opening book file cannot be loaded. */
    public static final OpeningBook DEFAULT = new OpeningBook();

    /** Map with board positions. */
    private final Map<Position, List<BookMove>> positions;

    /** Used when there are several possible moves in one position. */
    private final Random random = new Random();

    private OpeningBook() {
        positions = new HashMap<>();

        // Add some simple moves to the empty opening book
        int e2e4 = Move.create(PAWN, E2_IDX, E4_IDX);
        int e7e5 = Move.create(PAWN, E7_IDX, E5_IDX);
        int d2d4 = Move.create(PAWN, D2_IDX, D4_IDX);
        int d7d5 = Move.create(PAWN, D7_IDX, D5_IDX);
        try {
            positions.put(Position.START, Arrays.asList(new BookMove(e2e4, 50), new BookMove(d2d4, 50)));
            positions.put(Position.of(new String[]{"e2e4"}), singletonList(new BookMove(e7e5, 100)));
            positions.put(Position.of(new String[]{"d2d4"}), singletonList(new BookMove(d7d5, 100)));
        } catch (IllegalMoveException e) {
            // Ignore
        }
    }

    /**
     * Creates a new opening book from the map of positions and moves.
     *
     * @param positions A map that maps positions to lists of possible moves.
     */
    public OpeningBook(Map<Position, List<BookMove>> positions) {
        this.positions = convertAllWeightsToPercent(positions);
    }

    /**
     * returns the size of the opening book, that is, the number of unique positions.
     */
    public int size() {
        return positions.size();
    }

    /**
     * Returns a new map of positions, where all weights in all positions have been converted to percent.
     * The given map of positions remains unchanged.
     *
     * @param positions The original map of positions.
     * @return A new map of positions with all weights converted to percent.
     */
    public static Map<Position, List<BookMove>> convertAllWeightsToPercent(Map<Position, List<BookMove>> positions) {
        return positions.entrySet()
                        .stream()
                        .collect(toMap(Map.Entry::getKey, e -> convertWeightsToPercent(e.getValue())));
    }

    /**
     * Returns a new list of moves with all weights converted to percent.
     * The given list of moves remains unchanged.
     */
    public static List<BookMove> convertWeightsToPercent(final List<BookMove> bookMoves) {
        final int totalWeight = getTotalWeight(bookMoves);
        if (totalWeight > 0 && totalWeight != 100) {
            final List<BookMove> convertedMoves = convertUsingTotalWeight(bookMoves, totalWeight);
            final int newTotalWeight = getTotalWeight(convertedMoves);
            if (newTotalWeight != 100) {
                return fixRoundingError(convertedMoves, 100 - newTotalWeight);
            } else {
                return convertedMoves;
            }
        } else {
            return bookMoves;
        }
    }

    /**
     * Fixes any rounding error that occurred while converting the weights to percent.
     * Return a new list of book moves, where the first book move with non-zero weight
     * has been updated to fix the rounding error. The given list of moves is not changed.
     *
     * @param bookMoves List of book moves to update.
     * @param roundingError The rounding error.
     * @return The list of book moves with the rounding error fixed.
     */
    private static List<BookMove> fixRoundingError(final List<BookMove> bookMoves, final int roundingError) {
        boolean hasAdjusted = false;
        final List<BookMove> fixedBookMoves = new ArrayList<>();
        for (BookMove bookMove : bookMoves) {
            if (!hasAdjusted && bookMove.weight() > 0) {
                fixedBookMoves.add(bookMove.withWeight(bookMove.weight() + roundingError));
                hasAdjusted = true;
            } else {
                fixedBookMoves.add(bookMove);
            }
        }
        return fixedBookMoves;
    }

    /**
     * Returns a new list of moves with all weights converted to percent using the given total weight.
     */
    private static List<BookMove> convertUsingTotalWeight(List<BookMove> bookMoves, int totalWeight) {
        return bookMoves.stream()
                        .map(bm -> bm.withWeight(Math.round(100F * bm.weight() / totalWeight)))
                        .toList();
    }

    /**
     * Returns the total weight of all moves in the list.
     */
    private static int getTotalWeight(List<BookMove> bookMoves) {
        return bookMoves.stream().mapToInt(BookMove::weight).sum();
    }

    /**
     * Returns the "best" book move for the given position. All possible moves for the
     * given position are looked up, and one of them is selected randomly according to
     * their percent weights. If no move is found, this method returns {@code null}.
     *
     * @param position A chess game position.
     * @return One of the possible moves found, or {@code null} if no move was found.
     */
    public int findBestMove(Position position) {
        List<BookMove> moves = positions.get(position);

        // If no book moves are known for this position
        if (moves == null) {
            return 0;
        }

        // Make a random decision on which move to make
        return findMoveInList(moves, random.nextInt(100));
    }

    /**
     * Finds the move in the list of moves that corresponds to the given value. The argument {@code value}
     * should be an integer between 0 and 99 (inclusive). The list of moves is scanned from start to end to
     * find the move that corresponds to the value. For example, if the list contains moves with weights
     * [60, 20, 20] and value is 65, the second move will be returned.
     *
     * @param moves The list of moves to chose from.
     * @param value A value between 0 and 99 (inclusive).
     * @return The chosen move, or {@code null} if no move was found.
     */
    public static int findMoveInList(final List<BookMove> moves, final int value) {
        if (moves.isEmpty()) {
            return 0;
        } else if (value < moves.get(0).weight()) {
            return moves.get(0).move();
        } else {
            return findMoveInList(moves.subList(1, moves.size()), value - moves.get(0).weight());
        }
    }

    /**
     * Returns a list of all moves and their weights for the given position.
     * The list contains a number of pairs, where the key is the weight, and
     * the value is the actual move.
     *
     * @param position A chess game position.
     * @return The list of book moves found for this position, or {@code null} if no moves were found.
     */
    public List<BookMove> findAllMoves(Position position) {
        return positions.get(position);
    }
}
