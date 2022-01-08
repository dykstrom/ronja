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

import org.junit.Test;
import se.dykstrom.ronja.common.model.Color;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static se.dykstrom.ronja.common.model.Piece.ROOK;
import static se.dykstrom.ronja.common.model.Square.*;

/**
 * This class is for testing rook moves with the generator classes using JUnit.
 *
 * @author Johan Dykstrom
 * @see AttackGenerator
 * @see FullMoveGenerator
 */
public class GeneratorRookTest extends AbstractTestCase {

    private static final FullMoveGenerator MOVE_GENERATOR = new FullMoveGenerator();

    private static final AttackGenerator ATTACK_GENERATOR = new AttackGenerator();

    /**
     * Tests generating rook moves in position {@link #FEN_START}.
     */
    @Test
    public void testPositionStart() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_START), 0);

        // There should be no moves in this position
        MOVE_GENERATOR.generateRookMoves();
        assertThat(MOVE_GENERATOR.getMoveIndex(), is(0));
    }

    /**
     * Tests generating rook moves from position {@link #FEN_MIDDLE_GAME_0}.
     */
    @Test
    public void testPosition0() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_MIDDLE_GAME_0), 0);

        // There should be 4 moves: Rb8, Rc8, Rd8, Rg8
        MOVE_GENERATOR.generateRookMoves();
        assertEquals(4, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR,
                Move.create(ROOK, A8_IDX, B8_IDX),
                Move.create(ROOK, A8_IDX, C8_IDX),
                Move.create(ROOK, A8_IDX, D8_IDX),
                Move.create(ROOK, H8_IDX, G8_IDX)
        );
    }

    /**
     * Tests generating rook moves from position {@link #FEN_MIDDLE_GAME_3}.
     */
    @Test
    public void testPosition1() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_MIDDLE_GAME_3), 0);

        // There should be 11 moves: Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Rxa8, Rb1, Rc1, Re2, Rf1
        MOVE_GENERATOR.generateRookMoves();
        assertEquals(11, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR,
                Move.create(ROOK, A1_IDX, A2_IDX),
                Move.create(ROOK, A1_IDX, A3_IDX),
                Move.create(ROOK, A1_IDX, A4_IDX),
                Move.create(ROOK, A1_IDX, A5_IDX),
                Move.create(ROOK, A1_IDX, A6_IDX),
                Move.create(ROOK, A1_IDX, A7_IDX),
                Move.createCapture(ROOK, A1_IDX, A8_IDX, ROOK),
                Move.create(ROOK, A1_IDX, A2_IDX),
                Move.create(ROOK, A1_IDX, A2_IDX),
                Move.create(ROOK, E1_IDX, E2_IDX),
                Move.create(ROOK, E1_IDX, F1_IDX)
        );
    }

    /**
     * Tests generating rook moves from position {@link #FEN_CHECKMATE_3_2}.
     */
    @Test
    public void testPosition2() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_CHECKMATE_3_2), 0);

        // There should be 8 moves: Rab8, Rac8, Rad8, Rae8, Rfb8, Rfc8, Rfd8, Rfe8
        MOVE_GENERATOR.generateRookMoves();
        assertEquals(8, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR,
                Move.create(ROOK, A8_IDX, B8_IDX),
                Move.create(ROOK, A8_IDX, C8_IDX),
                Move.create(ROOK, A8_IDX, D8_IDX),
                Move.create(ROOK, A8_IDX, E8_IDX),
                Move.create(ROOK, F8_IDX, B8_IDX),
                Move.create(ROOK, F8_IDX, C8_IDX),
                Move.create(ROOK, F8_IDX, D8_IDX),
                Move.create(ROOK, F8_IDX, E8_IDX)
        );
    }

    // ------------------------------------------------------------------------

    /**
     * Tests finding squares attacked by a rook in position {@link #FEN_START}.
     */
    @Test
    public void testAttackPositionStart() throws Exception {
        // WHITE
        ATTACK_GENERATOR.setup(Color.WHITE, FenParser.parse(FEN_START));
        assertEquals(Square.A2 | Square.B1 | Square.G1 | Square.H2,
                ATTACK_GENERATOR.getAllRookAttacks());

        // BLACK
        ATTACK_GENERATOR.setup(Color.BLACK, FenParser.parse(FEN_START));
        assertEquals(Square.A7 | Square.B8 | Square.G8 | Square.H7,
                ATTACK_GENERATOR.getAllRookAttacks());
    }

    /**
     * Tests finding squares attacked by a rook in position {@link #FEN_MIDDLE_GAME_0}.
     */
    @Test
    public void testAttackPositionMiddleGame0() throws Exception {
        // WHITE
        ATTACK_GENERATOR.setup(Color.WHITE, FenParser.parse(FEN_MIDDLE_GAME_0));
        assertEquals(Square.C1 | Square.E1 | Square.F1 | Square.D2 | Square.D3 | Square.D4 |
                        Square.F1 | Square.G1 | Square.H2,
                ATTACK_GENERATOR.getAllRookAttacks());

        // BLACK
        ATTACK_GENERATOR.setup(Color.BLACK, FenParser.parse(FEN_MIDDLE_GAME_0));
        assertEquals(Square.A7 | Square.B8 | Square.C8 | Square.D8 | Square.E8 |
                        Square.F8 | Square.G8 | Square.H7,
                ATTACK_GENERATOR.getAllRookAttacks());
    }

    /**
     * Tests finding squares attacked by a rook in position {@link #FEN_END_GAME_0}.
     */
    @Test
    public void testAttackPositionEndGame0() throws Exception {
        // WHITE
        ATTACK_GENERATOR.setup(Color.WHITE, FenParser.parse(FEN_END_GAME_0));
        assertEquals(Square.A1 | Square.B1 | Square.C1 | Square.D1 | Square.F1 | Square.G1 |
                        Square.E2 | Square.E3 | Square.E4 | Square.E5 | Square.E6 | Square.E7 | Square.E8,
                ATTACK_GENERATOR.getAllRookAttacks());

        // BLACK
        ATTACK_GENERATOR.setup(Color.BLACK, FenParser.parse(FEN_END_GAME_0));
        assertEquals(Square.A8 | Square.B8 | Square.C8 | Square.D8 | Square.E8 | Square.G8 | Square.F7,
                ATTACK_GENERATOR.getAllRookAttacks());
    }
}
