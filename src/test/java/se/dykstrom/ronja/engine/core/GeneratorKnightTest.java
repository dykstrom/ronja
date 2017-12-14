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

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static se.dykstrom.ronja.common.model.Piece.KNIGHT;
import static se.dykstrom.ronja.test.SizeMatcher.hasSize;

import java.util.List;

import org.junit.Test;

import se.dykstrom.ronja.common.model.Color;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

/**
 * This class is for testing knight moves with the generator classes using JUnit.
 *
 * @author Johan Dykstrom
 * @see AttackGenerator
 * @see FullMoveGenerator
 */
public class GeneratorKnightTest extends AbstractTestCase {

    private static final AttackGenerator ATTACK_GENERATOR = new AttackGenerator();

    private static final FullMoveGenerator MOVE_GENERATOR = new FullMoveGenerator();

	// ------------------------------------------------------------------------

    /**
     * Test generating knight moves from position {@link #FEN_START}.
     */
    @Test
    public void testPositionStart() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_START));

        List<Integer> moves = MOVE_GENERATOR.getAllKnightMoves();
        assertThat(moves, hasSize(4));
        assertThat(moves, hasItems(Move.create(KNIGHT, Square.B1, Square.A3),
                                   Move.create(KNIGHT, Square.B1, Square.C3),
                                   Move.create(KNIGHT, Square.G1, Square.F3),
                                   Move.create(KNIGHT, Square.G1, Square.H3)));
    }

    // TODO: Add more tests for class MoveGenerator.

    // -----------------------------------------------------------------------

    /**
     * Tests finding squares attacked by a knight in position {@link #FEN_START}.
     */
    @Test
    public void testAttackPositionStart() throws Exception {
        // WHITE
        ATTACK_GENERATOR.setup(Color.WHITE, FenParser.parse(FEN_START));
        assertEquals(Square.A3 | Square.C3 | Square.D2 | Square.E2 | Square.F3 | Square.H3,
                ATTACK_GENERATOR.getAllKnightAttacks());

        // BLACK
        ATTACK_GENERATOR.setup(Color.BLACK, FenParser.parse(FEN_START));
        assertEquals(Square.A6 | Square.C6 | Square.D7 | Square.E7 | Square.F6 | Square.H6,
                ATTACK_GENERATOR.getAllKnightAttacks());
    }

    // TODO: Add more tests for class AttackGenerator.

}
