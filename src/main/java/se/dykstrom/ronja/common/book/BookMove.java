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

import java.util.Objects;

import se.dykstrom.ronja.common.parser.CanParser;

/**
 * Represents an opening book move with the actual move, and its weight in the opening book.
 *
 * @author Johan Dykstrom
 */
public class BookMove {
    
    private final int move;
    private final int weight;

    public BookMove(int move, int weight) {
        this.move = move;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return CanParser.format(move) + "/" + weight;
    }
    
    public int getMove() {
        return move;
    }

    public int getWeight() {
        return weight;
    }

    /**
     * Returns a copy of this book move, with the weight altered. This book move remains unchanged.
     *
     * @param weight The new weight.
     * @return A book move, based on this position with the weight altered.
     */
    public BookMove withWeight(int weight) {
        return new BookMove(move, weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookMove that = (BookMove) o;
        return this.move == that.move && this.weight == that.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(move, weight);
    }
}
