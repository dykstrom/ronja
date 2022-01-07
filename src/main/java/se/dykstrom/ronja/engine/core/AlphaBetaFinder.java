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

package se.dykstrom.ronja.engine.core;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.SanParser;
import se.dykstrom.ronja.engine.time.TimeUtils;
import se.dykstrom.ronja.engine.utils.PositionUtils;

import static se.dykstrom.ronja.engine.time.TimeUtils.formatTime;

/**
 * This class implements the {@link Finder} interface using the alpha-beta
 * search algorithm. For an explanation of the alpha-beta algorithm, see
 * <a href="https://en.wikipedia.org/wiki/Alpha-beta_pruning">Wikipedia</a>.
 *
 * @author Johan Dykstrom
 */
@SuppressWarnings("java:S2629")
public class AlphaBetaFinder extends AbstractFinder {

    static final int ALPHA_START = -3_000_000;
    static final int BETA_START = 3_000_000;

    private static final Logger TLOG = Logger.getLogger(AlphaBetaFinder.class.getName());

    /** True if debugging this class. */
    private static final boolean DEBUG = false;

    private static final NumberFormat NF = NumberFormat.getIntegerInstance();

    static {
        NF.setGroupingUsed(false);
    }

    /** Used to evaluate positions. */
    private final Evaluator evaluator = new Evaluator();

    /** Used to generate moves. */
    private final FullMoveGenerator fullMoveGenerator = new FullMoveGenerator();

    /** The current game. */
    private final Game game;

    public AlphaBetaFinder(Game game) {
        this.game = game;
    }

    @Override
    public int findBestMoveWithinTime(final long maxTime) {
        TLOG.fine(() -> "Available time " + maxTime + " = " + formatTime(maxTime));
        List<Long> searchTimes = new ArrayList<>();

        // Reset statistics
        nodes = 0;
        long startTime = System.currentTimeMillis();
        long remainingTime = maxTime;
        long estimatedTime;
        int bestMove = 0;
        int maxDepth = 0;

        // Find all possible moves
        int numberOfMoves = fullMoveGenerator.generateMoves(game.getPosition(), 0);

        try {
            do {
                maxDepth++;
                long startTimeForDepth = System.currentTimeMillis();
                bestMove = findBestMove(maxDepth, numberOfMoves, remainingTime);
                if (DEBUG) TLOG.fine("Best move at depth " + maxDepth + " is " + formatMove(game.getPosition(), bestMove));
                searchTimes.add(System.currentTimeMillis() - startTimeForDepth);
                long usedTime = System.currentTimeMillis() - startTime;
                remainingTime = maxTime - usedTime;
                estimatedTime = TimeUtils.estimateTimeForNextDepth(searchTimes);
                if (DEBUG) TLOG.fine("Estimated time = " + estimatedTime + ", remaining time = " + remainingTime);
                sort(fullMoveGenerator, 0, numberOfMoves, bestMove);
            } while (estimatedTime < remainingTime);
        } catch (OutOfTimeException exception) {
            if (DEBUG) TLOG.fine("Aborted search with max depth " + maxDepth + " because time is up");
        }
        if (DEBUG) TLOG.fine(() -> "Search times: " + searchTimes);

        long stopTime = System.currentTimeMillis();
        final int finalMaxDepth = maxDepth;
        TLOG.fine(() -> "Evaluated " + nodes + " nodes (depth " + finalMaxDepth + ") in " + (stopTime - startTime) +
                        " ms = " + NF.format(nodes / ((stopTime - startTime) / 1000.0)) + " nps");
        return bestMove;
    }

    @Override
    public int findBestMove(final int maxDepth) {
        int numberOfMoves = fullMoveGenerator.generateMoves(game.getPosition(), 0);
        return findBestMove(maxDepth, numberOfMoves, 50_000);
    }

    /**
     * Finds the best move in the given position, searching in the given list of
     * moves. Searching is limited to the given max depth and the given max time.
     */
    private int findBestMove(int maxDepth, int numberOfMoves, long maxTime) {
        setMaxDepth(maxDepth);
        if (DEBUG) TLOG.finest(enter(game.getPosition(), 0));

        long startTime = System.currentTimeMillis();
        int bestMove = 0;
        int alpha = ALPHA_START;
        int beta = BETA_START;

        // Search all moves
        for (int moveIndex = 0; moveIndex < numberOfMoves; moveIndex++) {
            // Abort search if we realize we won't finish in time
            abortSearchIfOutOfTime(numberOfMoves, moveIndex, startTime, maxTime);

            int move = fullMoveGenerator.moves[0][moveIndex];

            // Make the move
            game.makeMove(move);

            // Calculate the score for the move by searching deeper
            int score = -alphaBeta(move, 1, -beta, -alpha);

            // Unmake the move again
            game.unmakeMove();

            // No beta cut-off needed here

            // If this move is the best yet
            if (score > alpha) {
                if (DEBUG) TLOG.finest(stay(game.getPosition(), 0) + ", score = " + score + ", new best move = " +
                                       formatMove(game.getPosition(), move));
                bestMove = move;
                alpha = score;
            }
        }

        if (DEBUG) TLOG.finest(leave(game.getPosition(), 0) + ", score = " + alpha + ", best move = " +
                               formatMove(game.getPosition(), bestMove));
        final int finalBestMove = bestMove;
        final int finalAlpha = alpha;
        TLOG.fine(() -> "Returning best move " + formatMove(game.getPosition(), finalBestMove) + " with score " + finalAlpha + " for max depth " + maxDepth);
        return bestMove;
    }

    /**
     * Aborts the search by throwing an exception if we realize we won't be able
     * to finish in time. We calculate the average time it has taken to search a
     * move, and if this time exceeds the time left, we abort the search.
     *
     * @param numberOfMoves The number of moves generated.
     * @param moveIndex The index of the move we will search next.
     * @param startTime The time we started to search.
     * @param maxTime The maximum time to use for the search.
     */
    private void abortSearchIfOutOfTime(int numberOfMoves, int moveIndex, long startTime, long maxTime) {
        long usedTime = System.currentTimeMillis() - startTime;
        long remainingTime = maxTime - usedTime;
        long averageTime = (moveIndex == 0) ? 0 : usedTime / moveIndex;
        if (averageTime > remainingTime) {
            TLOG.fine(() -> "Aborting search at move " + moveIndex + "/" + numberOfMoves +
                            ", average time = " + averageTime + ", remaining time = " + remainingTime);
            throw new OutOfTimeException();
        }
    }

    /**
     * Returns the score of the given position. The score will be positive if
     * the side to move is in the lead. The values {@code alpha} and
     * {@code beta} are search results from already searched branches in the
     * tree.
     *
     * @param lastMove The last move made that led to this position.
     * @param depth The current search depth.
     * @param alpha The score of the best move found so far in any branch of the tree.
     * @param beta The score of the best move for our opponent found so far in any branch of the tree.
     */
    int alphaBeta(int lastMove, int depth, int alpha, int beta) {
        if (DEBUG) TLOG.finest(enter(game.getPosition(), depth) + ", after " + lastMove + ", alpha = " + alpha + ", beta = " + beta);

        // Check that we do not pass by an end-of-game position
        if (game.getPosition().isIllegalCheck()) {
            if (DEBUG) TLOG.finest(leave(game.getPosition(), depth) + ", score = " + Evaluator.ILLEGAL_CHECK_VALUE);
            return Evaluator.ILLEGAL_CHECK_VALUE;
        }
        if (PositionUtils.isCheckMate(game.getPosition())) {
            if (DEBUG) TLOG.finest(leave(game.getPosition(), depth) + ", score = " + Evaluator.CHECK_MATE_VALUE);
            return Evaluator.CHECK_MATE_VALUE;
        }
        if (PositionUtils.isDraw(game.getPosition(), game)) {
            if (DEBUG) TLOG.finest(leave(game.getPosition(), depth) + ", score = " + Evaluator.DRAW_VALUE);
            return Evaluator.DRAW_VALUE;
        }

        // If we have reached a leaf node, evaluate the position
        if (isMaxDepth(depth)) {
            int score = evaluator.evaluate(game.getPosition());
            nodes++;
            if (DEBUG) TLOG.finest(leave(game.getPosition(), depth) + ", score = " + score);
            return score;
        }

        int bestMove = 0;

        // For all possible moves
        int numberOfMoves = fullMoveGenerator.generateMoves(game.getPosition(), depth);
        sort(fullMoveGenerator, depth, numberOfMoves);

        for (int moveIndex = 0; moveIndex < numberOfMoves; moveIndex++) {
            int move = fullMoveGenerator.moves[depth][moveIndex];

            // Make the move
            game.makeMove(move);

            // Calculate the score for the move by searching deeper
            int score = -alphaBeta(move, depth + 1, -beta, -alpha);

            // Unmake the move again
            game.unmakeMove();

            // If the score is too good, we cut off the search tree here,
            // because the opponent will not select this branch
            if (score >= beta) {
                if (DEBUG) TLOG.finest(leave(game.getPosition(), depth) + ", score = " + beta + " (beta cut-off for score " + score + ")");
                return beta;
            }

            // If this move is the best yet
            if (score > alpha) {
                bestMove = move;
                alpha = score;
            }
        }

        if (DEBUG) TLOG.finest(leave(game.getPosition(), depth) + ", score = " + alpha + ", best move = " +
                               formatMove(game.getPosition(), bestMove));
        return alpha;
    }

    /**
     * Sorts the moves on the given depth, taking into account the previous best move.
     */
    private void sort(FullMoveGenerator moveGenerator, int depth, int numberOfMoves, int bestMove) {
        SortUtils.sort(moveGenerator.moves[depth], numberOfMoves, new BestMoveComparator(bestMove));
    }

    /**
     * Sorts the moves on the given depth.
     */
    private void sort(FullMoveGenerator moveGenerator, int depth, int numberOfMoves) {
        SortUtils.sort(moveGenerator.moves[depth], numberOfMoves);
    }

    /**
     * Formats the given move.
     */
    private String formatMove(Position position, int move) {
        return SanParser.format(position, move);
    }
}
