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
import se.dykstrom.ronja.common.model.Piece;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    private List<Move> moves;

    // ------------------------------------------------------------------------

    /**
     * Tests generating queen moves from position {@link #FEN_START}.
     */
    @Test
    public void testPositionStart() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_START));

        // There should be no moves in this position
        moves = MOVE_GENERATOR.getAllQueenMoves();
        assertEquals(0, moves.size());
    }

    /**
     * Tests generating queen moves from position {@link #FEN_E4_E5}.
     */
    @Test
    public void testPosition0() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_E4_E5));

        // There should be 4 moves: Qe2, Qf3, Qg4, Qh5
        moves = MOVE_GENERATOR.getAllQueenMoves();
        assertEquals(4, moves.size());
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.D1, Square.E2, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.D1, Square.F3, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.D1, Square.G4, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.D1, Square.H5, null, false, false)));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_E4_E5_BC4}.
     */
    @Test
    public void testPosition1() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_E4_E5_BC4));

        // There should be 4 moves: Qe7, Qf6, Qg5, Qh4
        moves = MOVE_GENERATOR.getAllQueenMoves();
        assertEquals(4, moves.size());
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.D8, Square.E7, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.D8, Square.F6, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.D8, Square.G5, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.D8, Square.H4, null, false, false)));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_E4_E5_QG4_QH4}.
     */
    @Test
    public void testPosition2() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_E4_E5_QG4_QH4));

        // There should be 14 moves, including Qd1, Qg3, Qh3, Qxh4, Qxg7, Qxd7
        moves = MOVE_GENERATOR.getAllQueenMoves();
        assertEquals(14, moves.size());
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.G4, Square.D1, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.G4, Square.G3, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.G4, Square.H3, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.G4, Square.H4, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.G4, Square.G7, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.G4, Square.D7, null, false, false)));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_E4_E5_QG4_QH4_NF3}.
     */
    @Test
    public void testPosition3() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_E4_E5_QG4_QH4_NF3));

        // There should be 11 moves, including Qxh2, Qh3, Qxf2, Qg3, Qxg4, Qh6
        moves = MOVE_GENERATOR.getAllQueenMoves();
        assertEquals(11, moves.size());
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.H4, Square.H2, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.H4, Square.H3, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.H4, Square.F2, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.H4, Square.G3, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.H4, Square.G4, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.H4, Square.H6, null, false, false)));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_QUEEN_IN_CORNER}.
     */
    @Test
    public void testQueenInCorner() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_QUEEN_IN_CORNER));

        // There should be 21 moves, including Qa1, Qg1, Qa8, Qg7, Qh8, Qh2
        moves = MOVE_GENERATOR.getAllQueenMoves();
        assertEquals(21, moves.size());
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.H1, Square.A1, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.H1, Square.G1, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.H1, Square.A8, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.H1, Square.G2, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.H1, Square.H8, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.H1, Square.H2, null, false, false)));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_EIGHT_CAPTURES}.
     */
    @Test
    public void testEightCaptures() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_EIGHT_CAPTURES));

        // There should be 8 moves: Qxb1, Qxc1, Qxd1+, Qxd2+, Qxd3, Qxc3, Qxb3, Qxb2
        moves = MOVE_GENERATOR.getAllQueenMoves();
        assertEquals(8, moves.size());
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.C2, Square.B1, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.C2, Square.C1, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.C2, Square.D1, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.C2, Square.D2, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.C2, Square.D3, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.C2, Square.C3, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.C2, Square.B3, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.C2, Square.B2, null, false, false)));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_TWO_QUEENS}.
     */
    @Test
    public void testTwoQueens() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_TWO_QUEENS));

        // There should be 38 moves, including Qcf6, Qff6, Qf1#, Qxg3+, Qxe4, Qa8
        moves = MOVE_GENERATOR.getAllQueenMoves();
        assertEquals(38, moves.size());
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.C6, Square.F6, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.F3, Square.F6, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.F3, Square.F1, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.F3, Square.G3, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.C6, Square.E4, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.C6, Square.A8, null, false, false)));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_END_GAME_0}.
     */
    @Test
    public void testPositionEndGame0() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_END_GAME_0));

        // There should be 8 moves, including Qc6, Qc8, Qe8, Qxf5, Qd8, Qxf7
        moves = MOVE_GENERATOR.getAllQueenMoves();
        assertEquals(8, moves.size());
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.D7, Square.C6, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.D7, Square.C8, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.D7, Square.E8, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.D7, Square.F5, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.D7, Square.D8, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.D7, Square.F7, null, false, false)));
    }

    /**
     * Tests generating queen moves from position {@link #FEN_CHECKMATE_0}.
     */
    @Test
    public void testCheckMate0() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_CHECKMATE_0));

        // There should be 17 moves, including Qxa2, Qa6, Qc8, Qe7, Qh6, Qh3
        moves = MOVE_GENERATOR.getAllQueenMoves();
        assertEquals(17, moves.size());
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.E6, Square.A2, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.E6, Square.A6, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.E6, Square.C8, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.E6, Square.E7, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.E6, Square.H6, null, false, false)));
        assertTrue(moves.contains(Move.of(Piece.QUEEN, Square.E6, Square.H3, null, false, false)));
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
