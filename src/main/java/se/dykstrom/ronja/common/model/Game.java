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

import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.common.book.OpeningBookParser;
import se.dykstrom.ronja.common.parser.IllegalMoveException;
import se.dykstrom.ronja.engine.utils.AppConfig;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class holds state information for the current game. Only one game can be played at a time,
 * so this is a singleton class.
 *
 * @author Johan Dykstrom
 */
public class Game {
    
    private static final Logger TLOG = Logger.getLogger(Game.class.getName());

    /** The singleton instance of this class. */
    private static final class Holder {
        static final Game INSTANCE = new Game();
    }

    /** True if force mode is on. */
    private boolean force;

    /** The current chess board position. */
    private Position position;

    /** The color the engine is currently playing, or {@code null} if neither color (force mode). */
    private Color engineColor;

    /** All moves made in this game. */
    private List<Move> moves;

    /** The name of the opponent as set by the "name" command.*/
    private String opponent;

    /** A reference to the opening book used in this game. */
    private OpeningBook book;

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

    // ------------------------------------------------------------------------

    private Game() {
        try {
            book = OpeningBookParser.parse(new File(AppConfig.getBookFilename()));
        } catch (IOException | ParseException e) {
            TLOG.severe("Failed to load opening book. " + e.getMessage());
            book = OpeningBook.DEFAULT;
        }
        reset();
    }

    /**
     * Returns the singleton instance of this class.
     */
    @SuppressWarnings("SameReturnValue")
    public static Game instance() {
        return Holder.INSTANCE;
    }

    /**
     * Resets game information to start a new game.
     */
    public void reset() {
        setForceMode(false);
        setPosition(Position.START);
        setEngineColor(Color.BLACK);
        setMoves(new ArrayList<>());
        setOpponent(null);
        setResult("*");
        setStartTime(LocalDateTime.now());
    }

    /**
     * Makes the given move, and updates game data accordingly.
     */
    public void makeMove(Move move) throws IllegalMoveException {
        Position newPosition = position.withMove(move);

        // If the user is in check after his move
        if (newPosition.isIllegalCheck()) {
            throw new IllegalMoveException("in check after move");
        }

        position = newPosition;
        moves.add(move);
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
     * Sets the list of moves.
     */
    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    /**
     * Returns the list of moves made so far in this game.
     */
    public List<Move> getMoves() {
        return moves;
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
     * Sets the current position. Also sets the start position of the game to the given position.
     *
     * @param position The position to set.
     */
    public void setPosition(Position position) {
        this.position = position;
        this.startPosition = position;
        this.startMoveNumber = position.getFullMoveNumber();
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
     * Sets the opening book.
     */
    public void setBook(OpeningBook book) {
        this.book = book;
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
