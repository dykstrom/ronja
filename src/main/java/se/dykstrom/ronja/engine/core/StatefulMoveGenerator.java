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

import java.util.Collections;
import java.util.Iterator;

import se.dykstrom.ronja.common.model.Position;

/**
 * A {@link MoveGenerator} that generates moves incrementally, when they are needed,
 * instead of pre-generating all moves.
 */
public class StatefulMoveGenerator implements MoveGenerator {

    @Override
    public Iterator<Integer> iterator(Position position) {
        return new Itr(position);
    }

    // ------------------------------------------------------------------------

    /**
     * An iterator that has an internal state variable to keep track on which type of moves to generate next.
     */
    private class Itr implements Iterator<Integer> {

        private final FullMoveGenerator delegate = new FullMoveGenerator();

        /** State variable used to keep track of which type of move to generate next. */
        private int state = 0;

        /** Internal iterator used to iterate over the move buffer. */
        private Iterator<Integer> iterator;

        Itr(Position position) {
            delegate.setup(position);
            iterator = Collections.<Integer>emptyList().iterator();
        }

        @Override
        public boolean hasNext() {
            if (iterator.hasNext()) {
                return true;
            }
            fillBuffer();
            return iterator.hasNext();
        }

        @Override
        public Integer next() {
            if (iterator.hasNext()) {
                return iterator.next();
            }
            fillBuffer();
            return iterator.next();
        }

        /**
         * Fills the buffer by generating new moves of the type defined by {@code state}.
         */
        private void fillBuffer() {
            while (!iterator.hasNext()) {
                switch (state) {
                    case 0:
                        iterator = delegate.getAllKnightMoves().iterator();
                        break;
                    case 1:
                        iterator = delegate.getAllKingMoves().iterator();
                        break;
                    case 2:
                        iterator = delegate.getAllPawnMoves().iterator();
                        break;
                    case 3:
                        iterator = delegate.getAllBishopMoves().iterator();
                        break;
                    case 4:
                        iterator = delegate.getAllRookMoves().iterator();
                        break;
                    case 5:
                        iterator = delegate.getAllQueenMoves().iterator();
                        break;
                    default:
                        // We have reached the end state - there are no more moves
                        return;
                }
                state++;
            }
        }
    }
}
