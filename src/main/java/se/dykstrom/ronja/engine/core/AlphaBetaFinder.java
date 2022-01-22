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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import se.dykstrom.ronja.common.model.Game;
import se.dykstrom.ronja.common.parser.SanParser;
import se.dykstrom.ronja.engine.time.TimeUtils;
import se.dykstrom.ronja.engine.utils.PositionUtils;

import static se.dykstrom.ronja.engine.core.Evaluator.CHECK_MATE_VALUE;
import static se.dykstrom.ronja.engine.core.Evaluator.DRAW_VALUE;
import static se.dykstrom.ronja.engine.core.Evaluator.ILLEGAL_CHECK_VALUE;
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

    /** Used to evaluate positions. */
    private final Evaluator evaluator = new Evaluator();

    /** Used to generate moves. */
    private final FullMoveGenerator fullMoveGenerator = new FullMoveGenerator();

    /** The current game. */
    private final Game game;

    /** The number of nodes evaluated. */
    private int nodes;

    /** The maximum search depth. */
    private int maxDepth;

    public AlphaBetaFinder(final Game game) {
        this.game = game;
    }

    public void setMaxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public int findBestMoveWithinTime(final long maxTime) {
        TLOG.fine(() -> "Available time " + maxTime + " = " + formatTime(maxTime));
        List<Long> searchTimes = new ArrayList<>();

        // Reset statistics
        nodes = 0;
        maxDepth = 1;
        long startTime = System.currentTimeMillis();
        long remainingTime = maxTime;
        long estimatedTime = 0;
        int bestMove = 0;

        // Generate moves once for all depths
        int numberOfMoves = fullMoveGenerator.generateMoves(game.getPosition(), 0);

        try {
            while (estimatedTime < remainingTime) {
                long startTimeForDepth = System.currentTimeMillis();

                sort(0, numberOfMoves, bestMove);
                bestMove = findBestMove(numberOfMoves, maxDepth, remainingTime);
                if (DEBUG) TLOG.fine("Best move at depth " + maxDepth + " is " + format(bestMove));

                searchTimes.add(System.currentTimeMillis() - startTimeForDepth);
                long usedTime = System.currentTimeMillis() - startTime;
                remainingTime = maxTime - usedTime;
                estimatedTime = TimeUtils.estimateTimeForNextDepth(searchTimes);
                if (DEBUG) TLOG.fine("Estimated time = " + estimatedTime + ", remaining time = " + remainingTime);
                maxDepth++;
                if (maxDepth >= FullMoveGenerator.MAX_POSITIONS) {
                    TLOG.warning(() -> "Exceeding maximum allowed depth " + maxDepth);
                    break;
                }
            }
        } catch (OutOfTimeException e) {
            TLOG.fine(() -> String.format("Aborting search at move %d/%d for depth %d, average time = %d, remaining time = %d",
                    e.getMoveIndex(), e.getNumberOfMoves(), maxDepth, e.getAverageTime(), e.getRemainingTime()));
        } catch (EndOfGameException e) {
            TLOG.fine(() -> String.format("Aborting search at depth %d because end-of-game reached, move = %s, score = %d",
                    maxDepth, format(e.getBestMove()), e.getScore()));
            bestMove = e.getBestMove();
            if (DEBUG) TLOG.fine("Best move at depth " + maxDepth + " is " + format(bestMove));
            maxDepth++;
        }
        if (DEBUG) TLOG.fine(() -> "Search times: " + searchTimes);

        final long stopTime = System.currentTimeMillis();
        final long elapsedTime = stopTime - startTime;
        TLOG.fine(() -> "Evaluated " + nodes + " nodes (depth " + (maxDepth - 1) + ") in " + elapsedTime +
                        " ms = " + Math.round(nodes / (elapsedTime / 1000.0)) + " nps");
        return bestMove;
    }

    @Override
    public int findBestMove(final int depth) {
        final int numberOfMoves = fullMoveGenerator.generateMoves(game.getPosition(), 0);
        sort(0, numberOfMoves);

        try {
            setMaxDepth(depth);
            return findBestMove(numberOfMoves, depth, 60_000);
        } catch (EndOfGameException e) {
            return e.getBestMove();
        }
    }

    /**
     * Finds the best move in the given position. Searching is limited to the given depth,
     * and the given maximum time. The number of moves that were generated by the caller is
     * passed as a parameter to this method.
     */
    private int findBestMove(final int numberOfMoves, final int depth, final long maxTime) {
        if (DEBUG) TLOG.finest(enter(depth));

        long startTime = System.currentTimeMillis();
        int alpha = ALPHA_START;
        int beta = BETA_START;
        int bestMove = 0;

        // For all possible moves
        for (int moveIndex = 0; moveIndex < numberOfMoves; moveIndex++) {
            // Abort search if we realize we won't finish in time
            abortSearchIfOutOfTime(moveIndex, numberOfMoves, startTime, maxTime);

            // Always read position index 0 in this case, because moves were generated by caller
            int move = fullMoveGenerator.moves[0][moveIndex];
            if (DEBUG) TLOG.finest(stay(depth) + ", trying " + format(move));

            // Make the move
            game.makeMove(move);

            // Calculate the score for the move by searching deeper
            int score = -alphaBeta(depth - 1, -beta, -alpha);

            // Unmake the move again
            game.unmakeMove();

            // No beta cut-off needed here

            // If this move is the best yet
            if (score > alpha) {
                if (DEBUG) TLOG.finest(stay(depth, score) + ", new best move = " + format(move));
                bestMove = move;
                alpha = score;
            }
        }

        if (DEBUG) TLOG.finest(leave(depth, alpha) + ", final best move = " + format(bestMove));
        final int finalBestMove = bestMove;
        final int finalAlpha = alpha;
        TLOG.fine(() -> "Returning best move " + format(finalBestMove) + " with score " + finalAlpha + " for depth " + depth);
        abortSearchIfEndOfGame(finalBestMove, finalAlpha);
        return bestMove;
    }

    /**
     * Aborts the search by throwing an exception if we have reached an end-of-game position.
     *
     * @param bestMove The move that lead to the end-of-game position.
     * @param score The score of the position.
     */
    private void abortSearchIfEndOfGame(final int bestMove, final int score) {
        if (Math.abs(score) >= Math.abs(CHECK_MATE_VALUE)) {
            throw new EndOfGameException(bestMove, score);
        }
    }

    /**
     * Aborts the search by throwing an exception if we realize we won't be able
     * to finish in time. We calculate the average time it has taken to search a
     * move, and if this time exceeds the time left, we abort the search.
     *
     * @param moveIndex The index of the move we will search next.
     * @param numberOfMoves The total number of moves.
     * @param startTime The time we started to search.
     * @param maxTime The maximum time to use for the entire search, not only for the current move.
     */
    private void abortSearchIfOutOfTime(final int moveIndex,
                                        final int numberOfMoves,
                                        final long startTime,
                                        final long maxTime) {
        final long usedTime = System.currentTimeMillis() - startTime;
        final long remainingTime = maxTime - usedTime;
        final long averageTimePerMove = (moveIndex == 0) ? 0 : usedTime / moveIndex;
        if (averageTimePerMove > remainingTime) {
            throw new OutOfTimeException(moveIndex, numberOfMoves, averageTimePerMove, remainingTime);
        }
    }

    /**
     * Returns the score of the given position. The score will be positive if
     * the side to move is in the lead. The values {@code alpha} and {@code beta}
     * are search results from already searched branches in the tree.
     *
     * @param depth The current search depth.
     * @param alpha The score of the best move found so far in any branch of the tree.
     * @param beta The score of the best move for our opponent found so far in any branch of the tree.
     */
    int alphaBeta(final int depth, int alpha, final int beta) {
        if (DEBUG) TLOG.finest(enter(depth) + ", alpha = " + alpha + ", beta = " + beta);

        // Check that we do not pass by an end-of-game position
        if (game.getPosition().isIllegalCheck()) {
            if (DEBUG) TLOG.finest(leave(depth, ILLEGAL_CHECK_VALUE));
            return ILLEGAL_CHECK_VALUE;
        }
        if (PositionUtils.isDraw(game.getPosition(), game)) {
            if (DEBUG) TLOG.finest(leave(depth, DRAW_VALUE));
            return DRAW_VALUE;
        }

        // If we have reached a leaf node, evaluate the position
        if (depth == 0) {
            int score = evaluator.evaluate(game.getPosition());
            nodes++;
            if (DEBUG) TLOG.finest(leave(depth, score));
            return score;
        }

        int bestMove = 0;

        int numberOfMoves = fullMoveGenerator.generateMoves(game.getPosition(), depth);
        sort(depth, numberOfMoves);

        // For all possible moves
        for (int moveIndex = 0; moveIndex < numberOfMoves; moveIndex++) {
            int move = fullMoveGenerator.moves[depth][moveIndex];
            if (DEBUG) TLOG.finest(stay(depth) + ", trying " + format(move));

            // Make the move
            game.makeMove(move);

            // Calculate the score for the move by searching deeper
            int score = -alphaBeta(depth - 1, -beta, -alpha);

            // Unmake the move again
            game.unmakeMove();

            // If the score is too good, we cut off the search tree here,
            // because the opponent will not select this branch
            if (score >= beta) {
                if (DEBUG) TLOG.finest(leave(depth, beta) + " (beta cut-off for score " + score + ")");
                return beta;
            }

            // If this move is the best yet
            if (score > alpha) {
                if (DEBUG) TLOG.finest(stay(depth, score) + ", new best move = " + format(move));
                bestMove = move;
                alpha = score;
            }
        }

        if (DEBUG) TLOG.finest(leave(depth, alpha) + ", final best move = " + format(bestMove));
        return alpha;
    }

    /**
     * Sorts the moves on the given depth, taking into account the previous best move.
     */
    private void sort(int depth, int numberOfMoves, int bestMove) {
        SortUtils.sort(fullMoveGenerator.moves[depth], numberOfMoves, new BestMoveComparator(bestMove));
    }

    /**
     * Sorts the moves on the given depth.
     */
    private void sort(int depth, int numberOfMoves) {
        SortUtils.sort(fullMoveGenerator.moves[depth], numberOfMoves);
    }

    private String enter(final int depth) {
        return enter(game.getPosition(), maxDepth - depth);
    }

    private String stay(final int depth) {
        return stay(game.getPosition(), maxDepth - depth);
    }

    private String stay(final int depth, final int score) {
        return stay(game.getPosition(), maxDepth - depth) + ", score = " + score;
    }

    private String leave(final int depth, final int score) {
        return leave(game.getPosition(), maxDepth - depth) + ", score = " + score;
    }

    private String format(final int move) {
        return SanParser.format(game.getPosition(), move);
    }
}
