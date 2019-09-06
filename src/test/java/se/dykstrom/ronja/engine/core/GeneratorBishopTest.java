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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static se.dykstrom.ronja.common.model.Piece.*;
import static se.dykstrom.ronja.common.model.Square.*;

/**
 * This class is for testing bishop moves with the generator classes using JUnit.
 *
 * @author Johan Dykstrom
 * @see AttackGenerator
 * @see FullMoveGenerator
 */
public class GeneratorBishopTest extends AbstractTestCase {

    private static final FullMoveGenerator MOVE_GENERATOR = new FullMoveGenerator();

    private static final AttackGenerator ATTACK_GENERATOR = new AttackGenerator();

    // ------------------------------------------------------------------------

    /**
     * Tests generating bishop moves from position {@link #FEN_START}.
     */
    @Test
    public void testPositionStart() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_START), 0);

        // There should be no moves in this position
        MOVE_GENERATOR.generateBishopMoves();
        assertThat(MOVE_GENERATOR.getMoveIndex(), is(0));
    }

    /**
     * Tests generating bishop moves from position {@link #FEN_E4_E5}.
     */
    @Test
    public void testPosition0() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_E4_E5), 0);

        // There should be 5 moves: Be2, Bd3, Bc4, Bb5, Ba6
        MOVE_GENERATOR.generateBishopMoves();
        assertThat(MOVE_GENERATOR.getMoveIndex(), is(5));
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(BISHOP, F1_IDX, E2_IDX),
                                             Move.create(BISHOP, F1_IDX, D3_IDX),
                                             Move.create(BISHOP, F1_IDX, C4_IDX),
                                             Move.create(BISHOP, F1_IDX, B5_IDX),
                                             Move.create(BISHOP, F1_IDX, A6_IDX));
    }

    /**
     * Tests generating bishop moves from position {@link #FEN_E4_E5_BC4}.
     */
    @Test
    public void testPosition1() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_E4_E5_BC4), 0);

        // There should be 5 moves: Be7, Bd6, Bc5, Bb4, Ba3
        MOVE_GENERATOR.generateBishopMoves();
        assertThat(MOVE_GENERATOR.getMoveIndex(), is(5));
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(BISHOP, F8_IDX, E7_IDX),
                                             Move.create(BISHOP, F8_IDX, D6_IDX),
                                             Move.create(BISHOP, F8_IDX, C5_IDX),
                                             Move.create(BISHOP, F8_IDX, B4_IDX),
                                             Move.create(BISHOP, F8_IDX, A3_IDX));
    }

    /**
     * Tests generating bishop moves from position {@link #FEN_E4_E5_BC4_BA3}.
     */
    @Test
    public void testPosition2() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_E4_E5_BC4_BA3), 0);

        // There should be 9 moves: Ba6, Bb5, Bb3, Bd5, Be6, Bxf7, Bd3, Be2, Bf1
        MOVE_GENERATOR.generateBishopMoves();
        assertThat(MOVE_GENERATOR.getMoveIndex(), is(9));
    }

    /**
     * Tests generating bishop moves from position {@link #FEN_E4_E5_BC4_BA3_B4}.
     */
    @Test
    public void testPosition3() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_E4_E5_BC4_BA3_B4), 0);

        // There should be 3 moves: Bxb4, Bb2, Bxc1
        MOVE_GENERATOR.generateBishopMoves();
        assertThat(MOVE_GENERATOR.getMoveIndex(), is(3));
        assertGeneratedMoves(MOVE_GENERATOR, Move.createCapture(BISHOP, A3_IDX, B4_IDX, PAWN),
                                             Move.create(BISHOP, A3_IDX, B2_IDX),
                                             Move.createCapture(BISHOP, A3_IDX, C1_IDX, BISHOP));
    }

    /**
     * Tests generating bishop moves from position {@link #FEN_END_GAME_1}.
     */
    @Test
    public void testPositionEndGame1() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_END_GAME_1), 0);

        // There should be 13 moves: Ba8, Bb7, Bc6, Ba2, Bb3, Bc4, Be6, Bf7, Bg8, Be4, Bf3, Bg2, Bh1
        MOVE_GENERATOR.generateBishopMoves();
        assertThat(MOVE_GENERATOR.getMoveIndex(), is(13));
    }

    /**
     * Tests generating bishop moves from position {@link #FEN_END_GAME_2}.
     */
    @Test
    public void testPositionEndGame2() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_END_GAME_2), 0);

        // There should be 13 moves: Bb8, Bc7, Bd6, Ba1, Bb2, Bc3, Bd4, Bf6, Bg7, Bh8, Bf4, Bg3, Bh2
        MOVE_GENERATOR.generateBishopMoves();
        assertThat(MOVE_GENERATOR.getMoveIndex(), is(13));
    }

    /**
     * Tests generating bishop moves from position {@link #FEN_OPENING_0}.
     */
    @Test
    public void testOpening0() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_OPENING_0), 0);

        // There should be 12 moves: Ba6, Ba4, Bxc6, Bc4, Bd3, Be2, Bf1, Bd2, Be3, Bf4, Bg5, Bh6
        MOVE_GENERATOR.generateBishopMoves();
        assertThat(MOVE_GENERATOR.getMoveIndex(), is(12));
    }

    /**
     * Tests generating bishop moves from position {@link #FEN_CAPTURE_IN_CORNER}.
     */
    @Test
    public void testCaptureInCorner() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_CAPTURE_IN_CORNER), 0);

        // There should be 5 moves: Bxa8, Bb7, Bxb5, Bxd7, Bd5
        MOVE_GENERATOR.generateBishopMoves();
        assertThat(MOVE_GENERATOR.getMoveIndex(), is(5));
        assertGeneratedMoves(MOVE_GENERATOR, Move.createCapture(BISHOP, C6_IDX, A8_IDX, ROOK),
                                             Move.create(BISHOP, C6_IDX, B7_IDX),
                                             Move.createCapture(BISHOP, C6_IDX, B5_IDX, PAWN),
                                             Move.createCapture(BISHOP, C6_IDX, D7_IDX, BISHOP),
                                             Move.create(BISHOP, C6_IDX, D5_IDX));
    }

    // ------------------------------------------------------------------------

    /**
     * Tests finding squares attacked by a bishop in position {@link #FEN_START}.
     */
    @Test
    public void testAttackPositionStart() throws Exception {
        // WHITE
        ATTACK_GENERATOR.setup(Color.WHITE, FenParser.parse(FEN_START));
        assertEquals(Square.B2 | Square.D2 | Square.E2 | Square.G2,
                ATTACK_GENERATOR.getAllBishopAttacks());

        // BLACK
        ATTACK_GENERATOR.setup(Color.BLACK, FenParser.parse(FEN_START));
        assertEquals(Square.B7 | Square.D7 | Square.E7 | Square.G7,
                ATTACK_GENERATOR.getAllBishopAttacks());
    }

    /**
     * Tests finding squares attacked by a bishop in position {@link #FEN_MIDDLE_GAME_0}.
     */
    @Test
    public void testAttackPositionMiddleGame0() throws Exception {
        // WHITE
        ATTACK_GENERATOR.setup(Color.WHITE, FenParser.parse(FEN_MIDDLE_GAME_0));
        assertEquals(Square.E2 | Square.D3 | Square.C4 | Square.G2 |
                        Square.F4 | Square.E3 | Square.F6 | Square.H6 | Square.H4,
                ATTACK_GENERATOR.getAllBishopAttacks());

        // BLACK
        ATTACK_GENERATOR.setup(Color.BLACK, FenParser.parse(FEN_MIDDLE_GAME_0));
        assertEquals(Square.A6 | Square.A8 | Square.C8 | Square.C6 | Square.D5 |
                        Square.E7 | Square.G7,
                ATTACK_GENERATOR.getAllBishopAttacks());
    }
}
