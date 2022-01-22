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

package se.dykstrom.ronja.common.model;

import java.time.LocalDateTime;
import java.util.Arrays;

import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.engine.time.TimeControl;
import se.dykstrom.ronja.engine.time.TimeData;

import static se.dykstrom.ronja.engine.time.TimeControlType.CLASSIC;
import static se.dykstrom.ronja.engine.time.TimeControlType.SECONDS_PER_MOVE;

/**
 * This class holds state information for the current game.
 *
 * @author Johan Dykstrom
 */
public class Game {

    /** Default time control is 40 moves in 2 minutes. */
    private static final TimeControl TWO_MINUTES = new TimeControl(40, 2 * 60 * 1000L, 0, CLASSIC);

    /** The maximum number of moves in a game. */
    private static final int MAX_MOVES = 500;

    /** True if force mode is on. */
    private boolean force;

    /** The current chess board position. */
    private Position position;

    /** The color the engine is currently playing, or {@code null} if neither color (force mode). */
    private Color engineColor;

    /** All moves made in this game. */
    private final int[] moves = new int[MAX_MOVES];

    /** Index to keep track of the number of stored moves. */
    private int moveIndex;

    /** All historic positions in this game. */
    public final Position[] positions = new Position[MAX_MOVES];

    /** Index to keep track of the number of stored positions. */
    public int positionIndex;

    /** The name of the opponent as set by the "name" command.*/
    private String opponent;

    /** A reference to the opening book used in this game. */
    private final OpeningBook book;

    /** The game result, or {@code null} if the game has not yet ended. */
    private String result;

    /** The date and time the game started. */
    private LocalDateTime startTime;

    /** The position in which the game started. Normally the initial position. */
    private Position startPosition;

    /**
     * The number of the first move in the game. Normally 1, but can be something else
     * if the game has been setup using the setboard command.
     */
    private int startMoveNumber;

    /** Time control set by XBoard. */
    private TimeControl timeControl;

    /** Remaining time and moves for the engine. */
    private TimeData timeData;

    // ------------------------------------------------------------------------

    /**
     * Creates a new game that uses the given opening book.
     */
    public Game(OpeningBook book) {
        this.book = book;
        reset();
    }

    /**
     * Resets game information to start a new game.
     */
    public void reset() {
        setForceMode(false);
        setPosition(Position.START);
        setEngineColor(Color.BLACK);
        setOpponent(null);
        setResult("*");
        setStartTime(LocalDateTime.now());
        setTimeControl(TWO_MINUTES);
        setTimeData(TimeData.from(TWO_MINUTES));
    }

    /**
     * Makes the given move, updates game data, and returns the resulting position.
     */
    public void makeMove(int move) {
        moves[moveIndex++] = move;

        position = position.withMove(move);
        positions[positionIndex++] = position;
    }

    /**
     * Unmakes the last move that was made, and updates game data.
     */
    public void unmakeMove() {
        moveIndex--;

        positionIndex--;
        position = positions[positionIndex - 1];
    }

    /**
     * Updates the time data in the game after a move, taking into account the {@code usedTime},
     * and the type of the time control.
     *
     * @param usedTime The time used for this move in milliseconds.
     */
    public void updateTimeDataAfterMove(final long usedTime) {
        final TimeControl timeControl = getTimeControl();
        if (timeControl.type() == SECONDS_PER_MOVE) {
            setTimeData(TimeData.from(timeControl));
        } else if (timeControl.type() == CLASSIC) {
            final TimeData timeData = getTimeData();
            long remainingTime = Math.max(timeData.remainingTime() - usedTime, 0);
            long numberOfMoves = timeData.numberOfMoves() - 1;
            // If we have reached the time control, reset number of moves and add time
            if (numberOfMoves == 0) {
                numberOfMoves = timeControl.numberOfMoves();
                remainingTime += timeControl.baseTime();
            }
            setTimeData(timeData.withRemainingTime(remainingTime).withNumberOfMoves(numberOfMoves));
        } else { // INCREMENTAL
            final TimeData timeData = getTimeData();
            final long remainingTime = Math.max(timeData.remainingTime() - usedTime + timeControl.increment(), 0);
            setTimeData(timeData.withRemainingTime(remainingTime));
        }
    }

    /**
     * Sets the color the engine plays.
     *
     * @param engineColor The color the engine plays, may be {@code null}.
     */
    public void setEngineColor(Color engineColor) {
        this.engineColor = engineColor;
    }

    /**
     * Returns the color the engine plays, or {@code null} if the engine plays neither color (force mode).
     */
    public Color getEngineColor() {
        return engineColor;
    }

    /**
     * Sets the array of historical moves.
     */
    public void setMoves(int[] moves) {
        System.arraycopy(moves, 0, this.moves, 0, moves.length);
        moveIndex = moves.length;
    }

    /**
     * Returns the array of historical moves.
     */
    public int[] getMoves() {
        return Arrays.copyOf(moves, moveIndex);
    }

    /**
     * Sets the name of the opponent.
     *
     * @param opponent The name of the opponent.
     */
    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    /**
     * Returns the name of the opponent as set by the "name" command.
     */
    public String getOpponent() {
        return opponent;
    }

    /**
     * Sets the current position and the start position of the game to the given position.
     * Also resets the lists of historical positions and moves.
     *
     * @param position The position to set.
     */
    public void setPosition(Position position) {
        this.position = position;
        this.startPosition = position;
        this.startMoveNumber = position.getFullMoveNumber();

        positions[0] = position;
        positionIndex = 1;

        moveIndex = 0;
    }

    /**
     * Returns the current position.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Returns the position in which this game started.
     */
    public Position getStartPosition() {
        return startPosition;
    }

    /**
     * Returns the number of the first move in the game.
     */
    public int getStartMoveNumber() {
        return startMoveNumber;
    }

    /**
     * Turns force mode on or off.
     *
     * @param force True to turn force mode on, false to turn it off.
     */
    public void setForceMode(boolean force) {
        this.force = force;
    }

    /**
     * Returns true if force mode is on.
     */
    public boolean getForceMode() {
        return force;
    }

    /**
     * Returns a reference to the opening book used in this game.
     */
    public OpeningBook getBook() {
        return book;
    }

    /**
     * Sets the result of the game.
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Returns the result of the game.
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the start date/time of the game.
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the start date/time of the game.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the time data.
     */
    public void setTimeData(TimeData timeData) {
        this.timeData = timeData;
    }

    /**
     * Returns the time data.
     */
    public TimeData getTimeData() {
        return timeData;
    }

    /**
     * Sets the time control. This method does not set the time data to match the time control.
     * This must be done by invoking method {@link #setTimeData(TimeData)}.
     */
    public void setTimeControl(TimeControl timeControl) {
        this.timeControl = timeControl;
    }

    /**
     * Returns the time control.
     */
    public TimeControl getTimeControl() {
        return timeControl;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (result.equals("*")) {
            builder.append("Move ").append(position.getFullMoveNumber()).append(", ");
            builder.append(position.getActiveColor()).append(" to move\n");
        } else {
            builder.append("Result ").append(result).append("\n");
        }
        builder.append(position);
        return builder.toString();
    }
}
