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

import static org.junit.Assert.assertEquals;
import static se.dykstrom.ronja.common.model.Piece.*;
import static se.dykstrom.ronja.common.model.Square.*;

/**
 * This class is for testing queen moves with the generator classes using JUnit.
 *
 * @author Johan Dykstrom
 * @see AttackGenerator
 * @see FullMoveGenerator
 */
public class GeneratorQueenTest extends AbstractTestCase {

    private static final FullMoveGenerator MOVE_GENERATOR = new FullMoveGenerator();

    private static final AttackGenerator ATTACK_GENERATOR = new AttackGenerator();

    // ------------------------------------------------------------------------

    /**
     * Tests generating queen moves from position {@link #FEN_START}.
     */
    @Test
    public void testPositionStart() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_START), 0);

        // There should be no moves in this position
        MOVE_GENERATOR.generateQueenMoves();
        assertEquals(0, MOVE_GENERATOR.getMoveIndex());
    }

    /**
     * Tests generating queen moves from position {@link #FEN_E4_E5}.
     */
    @Test
    public void testPosition0() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_E4_E5), 0);

        // There should be 4 moves: Qe2, Qf3, Qg4, Qh5
        MOVE_GENERATOR.generateQueenMoves();
        assertEquals(4, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(QUEEN, D1_IDX, E2_IDX),
                             Move.create(QUEEN, D1_IDX, F3_IDX),
                             Move.create(QUEEN, D1_IDX, G4_IDX),
                             Move.create(QUEEN, D1_IDX, H5_IDX));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_E4_E5_BC4}.
     */
    @Test
    public void testPosition1() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_E4_E5_BC4), 0);

        // There should be 4 moves: Qe7, Qf6, Qg5, Qh4
        MOVE_GENERATOR.generateQueenMoves();
        assertEquals(4, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(QUEEN, D8_IDX, E7_IDX),
                             Move.create(QUEEN, D8_IDX, F6_IDX),
                             Move.create(QUEEN, D8_IDX, G5_IDX),
                             Move.create(QUEEN, D8_IDX, H4_IDX));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_E4_E5_QG4_QH4}.
     */
    @Test
    public void testPosition2() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_E4_E5_QG4_QH4), 0);

        // There should be 14 moves, including Qd1, Qg3, Qh3, Qxh4, Qxg7, Qxd7
        MOVE_GENERATOR.generateQueenMoves();
        assertEquals(14, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(QUEEN, G4_IDX, D1_IDX),
                             Move.create(QUEEN, G4_IDX, G3_IDX),
                             Move.create(QUEEN, G4_IDX, H3_IDX),
                             Move.createCapture(QUEEN, G4_IDX, H4_IDX, QUEEN),
                             Move.createCapture(QUEEN, G4_IDX, G7_IDX, PAWN),
                             Move.createCapture(QUEEN, G4_IDX, D7_IDX, PAWN));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_E4_E5_QG4_QH4_NF3}.
     */
    @Test
    public void testPosition3() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_E4_E5_QG4_QH4_NF3), 0);

        // There should be 11 moves, including Qxh2, Qh3, Qxf2, Qg3, Qxg4, Qh6
        MOVE_GENERATOR.generateQueenMoves();
        assertEquals(11, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.createCapture(QUEEN, H4_IDX, H2_IDX, PAWN),
                             Move.create(QUEEN, H4_IDX, H3_IDX),
                             Move.createCapture(QUEEN, H4_IDX, F2_IDX, PAWN),
                             Move.create(QUEEN, H4_IDX, G3_IDX),
                             Move.createCapture(QUEEN, H4_IDX, G4_IDX, QUEEN),
                             Move.create(QUEEN, H4_IDX, H6_IDX));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_QUEEN_IN_CORNER}.
     */
    @Test
    public void testQueenInCorner() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_QUEEN_IN_CORNER), 0);

        // There should be 21 moves, including Qa1, Qg1, Qa8, Qg7, Qh8, Qh2
        MOVE_GENERATOR.generateQueenMoves();
        assertEquals(21, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(QUEEN, H1_IDX, A1_IDX),
                             Move.create(QUEEN, H1_IDX, G1_IDX),
                             Move.create(QUEEN, H1_IDX, A8_IDX),
                             Move.create(QUEEN, H1_IDX, G2_IDX),
                             Move.create(QUEEN, H1_IDX, H8_IDX),
                             Move.create(QUEEN, H1_IDX, H2_IDX));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_EIGHT_CAPTURES}.
     */
    @Test
    public void testEightCaptures() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_EIGHT_CAPTURES), 0);

        // There should be 8 moves: Qxb1, Qxc1, Qxd1+, Qxd2+, Qxd3, Qxc3, Qxb3, Qxb2
        MOVE_GENERATOR.generateQueenMoves();
        assertEquals(8, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.createCapture(QUEEN, C2_IDX, B1_IDX, KNIGHT),
                             Move.createCapture(QUEEN, C2_IDX, C1_IDX, BISHOP),
                             Move.createCapture(QUEEN, C2_IDX, D1_IDX, QUEEN),
                             Move.createCapture(QUEEN, C2_IDX, D2_IDX, PAWN),
                             Move.createCapture(QUEEN, C2_IDX, D3_IDX, KNIGHT),
                             Move.createCapture(QUEEN, C2_IDX, C3_IDX, PAWN),
                             Move.createCapture(QUEEN, C2_IDX, B3_IDX, BISHOP),
                             Move.createCapture(QUEEN, C2_IDX, B2_IDX, PAWN));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_TWO_QUEENS}.
     */
    @Test
    public void testTwoQueens() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_TWO_QUEENS), 0);

        // There should be 38 moves, including Qcf6, Qff6, Qf1#, Qxg3+, Qxe4, Qa8
        MOVE_GENERATOR.generateQueenMoves();
        assertEquals(38, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(QUEEN, C6_IDX, F6_IDX),
                             Move.create(QUEEN, F3_IDX, F6_IDX),
                             Move.create(QUEEN, F3_IDX, F1_IDX),
                             Move.createCapture(QUEEN, F3_IDX, G3_IDX, PAWN),
                             Move.createCapture(QUEEN, C6_IDX, E4_IDX, PAWN),
                             Move.create(QUEEN, C6_IDX, A8_IDX));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_END_GAME_0}.
     */
    @Test
    public void testPositionEndGame0() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_END_GAME_0), 0);

        // There should be 8 moves, including Qc6, Qc8, Qe8, Qxf5, Qd8, Qxf7
        MOVE_GENERATOR.generateQueenMoves();
        assertEquals(8, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(QUEEN, D7_IDX, C6_IDX),
                             Move.create(QUEEN, D7_IDX, C8_IDX),
                             Move.create(QUEEN, D7_IDX, E8_IDX),
                             Move.createCapture(QUEEN, D7_IDX, F5_IDX, PAWN),
                             Move.create(QUEEN, D7_IDX, D8_IDX),
                             Move.createCapture(QUEEN, D7_IDX, F7_IDX, BISHOP));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_CHECKMATE_0}.
     */
    @Test
    public void testCheckMate0() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_CHECKMATE_0), 0);

        // There should be 17 moves, including Qxa2, Qa6, Qc8, Qe7, Qh6, Qh3
        MOVE_GENERATOR.generateQueenMoves();
        assertEquals(17, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.createCapture(QUEEN, E6_IDX, A2_IDX, PAWN),
                             Move.create(QUEEN, E6_IDX, A6_IDX),
                             Move.create(QUEEN, E6_IDX, C8_IDX),
                             Move.create(QUEEN, E6_IDX, E7_IDX),
                             Move.create(QUEEN, E6_IDX, H6_IDX),
                             Move.create(QUEEN, E6_IDX, H3_IDX));
    }

    // ------------------------------------------------------------------------

    /**
     * Tests finding squares attacked by a queen in position {@link #FEN_START}.
     */
    @Test
    public void testAttackPositionStart() throws Exception {
        // WHITE
        ATTACK_GENERATOR.setup(Color.WHITE, FenParser.parse(FEN_START));
        assertEquals(Square.C1 | C2 | Square.D2 | Square.E2 | Square.E1,
                ATTACK_GENERATOR.getAllQueenAttacks());

        // BLACK
        ATTACK_GENERATOR.setup(Color.BLACK, FenParser.parse(FEN_START));
        assertEquals(Square.C8 | Square.C7 | Square.D7 | Square.E7 | Square.E8,
                ATTACK_GENERATOR.getAllQueenAttacks());
    }

    /**
     * Tests finding squares attacked by a queen in position {@link #FEN_MIDDLE_GAME_4}.
     */
    @Test
    public void testAttackPositionMiddleGame4() throws Exception {
        // WHITE
        ATTACK_GENERATOR.setup(Color.WHITE, FenParser.parse(FEN_MIDDLE_GAME_4));
        assertEquals(D3 | E3 | F4 | E2 | E1 | D1 | C1 | A2 | B2 | C2 | A5 | B4 | C3,
                ATTACK_GENERATOR.getAllQueenAttacks());

        // BLACK
        ATTACK_GENERATOR.setup(Color.BLACK, FenParser.parse(FEN_MIDDLE_GAME_4));
        assertEquals(E8 | E7 | F6 | G5 | H4 | D7 | A5 | B6 | C7 | A8 | B8 | C8,
                ATTACK_GENERATOR.getAllQueenAttacks());
    }

    // TODO: Add more test cases for AttackGenerator.
}
