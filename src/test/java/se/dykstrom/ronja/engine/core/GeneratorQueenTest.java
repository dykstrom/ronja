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
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(QUEEN, Square.D1, Square.E2),
                             Move.create(QUEEN, Square.D1, Square.F3),
                             Move.create(QUEEN, Square.D1, Square.G4),
                             Move.create(QUEEN, Square.D1, Square.H5));
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
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(QUEEN, Square.D8, Square.E7),
                             Move.create(QUEEN, Square.D8, Square.F6),
                             Move.create(QUEEN, Square.D8, Square.G5),
                             Move.create(QUEEN, Square.D8, Square.H4));
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
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(QUEEN, Square.G4, Square.D1),
                             Move.create(QUEEN, Square.G4, Square.G3),
                             Move.create(QUEEN, Square.G4, Square.H3),
                             Move.createCapture(QUEEN, Square.G4, Square.H4, QUEEN),
                             Move.createCapture(QUEEN, Square.G4, Square.G7, PAWN),
                             Move.createCapture(QUEEN, Square.G4, Square.D7, PAWN));
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
        assertGeneratedMoves(MOVE_GENERATOR, Move.createCapture(QUEEN, Square.H4, Square.H2, PAWN),
                             Move.create(QUEEN, Square.H4, Square.H3),
                             Move.createCapture(QUEEN, Square.H4, Square.F2, PAWN),
                             Move.create(QUEEN, Square.H4, Square.G3),
                             Move.createCapture(QUEEN, Square.H4, Square.G4, QUEEN),
                             Move.create(QUEEN, Square.H4, Square.H6));
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
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(QUEEN, Square.H1, Square.A1),
                             Move.create(QUEEN, Square.H1, Square.G1),
                             Move.create(QUEEN, Square.H1, Square.A8),
                             Move.create(QUEEN, Square.H1, Square.G2),
                             Move.create(QUEEN, Square.H1, Square.H8),
                             Move.create(QUEEN, Square.H1, Square.H2));
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
        assertGeneratedMoves(MOVE_GENERATOR, Move.createCapture(QUEEN, Square.C2, Square.B1, KNIGHT),
                             Move.createCapture(QUEEN, Square.C2, Square.C1, BISHOP),
                             Move.createCapture(QUEEN, Square.C2, Square.D1, QUEEN),
                             Move.createCapture(QUEEN, Square.C2, Square.D2, PAWN),
                             Move.createCapture(QUEEN, Square.C2, Square.D3, KNIGHT),
                             Move.createCapture(QUEEN, Square.C2, Square.C3, PAWN),
                             Move.createCapture(QUEEN, Square.C2, Square.B3, BISHOP),
                             Move.createCapture(QUEEN, Square.C2, Square.B2, PAWN));
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
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(QUEEN, Square.C6, Square.F6),
                             Move.create(QUEEN, Square.F3, Square.F6),
                             Move.create(QUEEN, Square.F3, Square.F1),
                             Move.createCapture(QUEEN, Square.F3, Square.G3, PAWN),
                             Move.createCapture(QUEEN, Square.C6, Square.E4, PAWN),
                             Move.create(QUEEN, Square.C6, Square.A8));
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
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(QUEEN, Square.D7, Square.C6),
                             Move.create(QUEEN, Square.D7, Square.C8),
                             Move.create(QUEEN, Square.D7, Square.E8),
                             Move.createCapture(QUEEN, Square.D7, Square.F5, PAWN),
                             Move.create(QUEEN, Square.D7, Square.D8),
                             Move.createCapture(QUEEN, Square.D7, Square.F7, BISHOP));
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
        assertGeneratedMoves(MOVE_GENERATOR, Move.createCapture(QUEEN, Square.E6, Square.A2, PAWN),
                             Move.create(QUEEN, Square.E6, Square.A6),
                             Move.create(QUEEN, Square.E6, Square.C8),
                             Move.create(QUEEN, Square.E6, Square.E7),
                             Move.create(QUEEN, Square.E6, Square.H6),
                             Move.create(QUEEN, Square.E6, Square.H3));
    }

    // ------------------------------------------------------------------------

    /**
     * Tests finding squares attacked by a queen in position {@link #FEN_START}.
     */
    @Test
    public void testAttackPositionStart() throws Exception {
        // WHITE
        ATTACK_GENERATOR.setup(Color.WHITE, FenParser.parse(FEN_START));
        assertEquals(Square.C1 | Square.C2 | Square.D2 | Square.E2 | Square.E1,
                ATTACK_GENERATOR.getAllQueenAttacks());

        // BLACK
        ATTACK_GENERATOR.setup(Color.BLACK, FenParser.parse(FEN_START));
        assertEquals(Square.C8 | Square.C7 | Square.D7 | Square.E7 | Square.E8,
                ATTACK_GENERATOR.getAllQueenAttacks());
    }

    // TODO: Add more test cases for AttackGenerator.
}
