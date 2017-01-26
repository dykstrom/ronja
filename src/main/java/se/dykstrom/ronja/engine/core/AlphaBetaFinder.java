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

import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.engine.utils.PositionUtils;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * This class implements the {@link Finder} interface using the alpha-beta search algorithm. For an explanation of
 * the alpha-beta algorithm, see <a href="https://en.wikipedia.org/wiki/Alpha-beta_pruning">Wikipedia</a>.
 *
 * @author Johan Dykstrom
 */
public class AlphaBetaFinder extends AbstractFinder {

    public static final int ALPHA_START = -3000000;

    public static final int BETA_START = 3000000;

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
    private final MoveGenerator moveGenerator = new StatefulMoveGenerator();

    @Override
    public Move findBestMove(Position position, int maxDepth) {
        setMaxDepth(maxDepth);
        if (DEBUG) TLOG.finest(enter(position, 0));

        // Reset statistics
        this.nodes = 0;
        long start = System.currentTimeMillis();

        Move bestMove = null;
        int alpha = ALPHA_START;
        int beta = BETA_START;

        // For all possible moves
        Iterator<Move> iterator = moveGenerator.iterator(position);
        while (iterator.hasNext()) {
            Move move = iterator.next();

            // Make the move
            Position next = position.withMove(move);

            // Calculate the score for the move by searching deeper
            int score = -alphaBeta(next, move, 1, -beta, -alpha);

            // No beta cut-off needed here

            // If this move is the best yet
            if (score > alpha) {
                if (DEBUG) TLOG.finest(stay(position, 0) + ", score = " + score + ", new best move = " + move);
                bestMove = move;
                alpha = score;
            }
        }
        long stop = System.currentTimeMillis();

        if (DEBUG) TLOG.finest(leave(position, 0) + ", score = " + alpha + ", best move = " + bestMove);
        TLOG.fine("Evaluated " + nodes + " nodes (max depth " + maxDepth + ") in " +
                ((stop - start) / 1000.0) + " seconds = " + NF.format(nodes / ((stop - start) / 1000.0)) + " nps");

        return bestMove;
    }

    /**
     * Returns the score of the given position. The score will be positive if the side to move is in the lead.
     * The values {@code alpha} and {@code beta} are search results from already searched branches in the tree.
     *
     * @param position The position to calculate the score for.
     * @param lastMove The last move made that led to this position.
     * @param depth The current search depth.
     * @param alpha The score of the best move found so far in any branch of the tree.
     * @param beta The score of the best move for our opponent found so far in any branch of the tree.
     */
    public int alphaBeta(Position position, Move lastMove, int depth, int alpha, int beta) {
        if (DEBUG) TLOG.finest(enter(position, depth) + ", after " + lastMove + ", alpha = " + alpha + ", beta = " + beta);

        // Check that we do not pass by an end-of-game position
        if (position.isIllegalCheck()) {
            if (DEBUG) TLOG.finest(leave(position, depth) + ", score = " + Evaluator.ILLEGAL_CHECK_VALUE);
            return Evaluator.ILLEGAL_CHECK_VALUE;
        }
        if (PositionUtils.isCheckMate(position)) {
            if (DEBUG) TLOG.finest(leave(position, depth) + ", score = " + Evaluator.CHECK_MATE_VALUE);
            return Evaluator.CHECK_MATE_VALUE;
        }
        if (PositionUtils.isDraw(position)) {
            if (DEBUG) TLOG.finest(leave(position, depth) + ", score = " + Evaluator.DRAW_VALUE);
            return Evaluator.DRAW_VALUE;
        }

        // If we have reached a leaf node, evaluate the position
        if (isMaxDepth(depth)) {
            int score = evaluator.evaluate(position);
            nodes++;
            if (DEBUG) TLOG.finest(leave(position, depth) + ", score = " + score);
            return score;
        }

        Move bestMove = null;

        // For all possible moves
        Iterator<Move> iterator = moveGenerator.iterator(position);
        while (iterator.hasNext()) {
            Move move = iterator.next();

            // Make the move
            Position next = position.withMove(move);

            // Calculate the score for the move by searching deeper
            int score = -alphaBeta(next, move, depth + 1, -beta, -alpha);

            // If the score is too good, we cut off the search tree here, because the opponent will not select this branch
            if (score >= beta) {
                if (DEBUG) TLOG.finest(leave(position, depth) + ", score = " + beta + " (beta cut-off for score " + score + ")");
                return beta;
            }

            // If this move is the best yet
            if (score > alpha) {
                bestMove = move;
                alpha = score;
            }
        }

        if (DEBUG) TLOG.finest(leave(position, depth) + ", score = " + alpha + ", best move = " + bestMove);
        return alpha;
    }
}
