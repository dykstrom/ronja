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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import se.dykstrom.ronja.common.model.Color;
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Piece;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class is for testing pawn moves with the generator classes using JUnit.
 *
 * @author Johan Dykstrom
 * @see AttackGenerator
 * @see FullMoveGenerator
 */
public class GeneratorPawnTest extends AbstractTestCase {

    private static final FullMoveGenerator MOVE_GENERATOR = new FullMoveGenerator();

    private static final AttackGenerator ATTACK_GENERATOR = new AttackGenerator();

    // ------------------------------------------------------------------------

    /**
     * Tests generating pawn moves from position {@link #FEN_START}.
     */
    @Test
    public void testPositionStart() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_START), 0);

        // There should be 16 possible pawn moves in this position
        MOVE_GENERATOR.generatePawnMoves();
        assertThat(MOVE_GENERATOR.getMoveIndex(), is(16));
    }

    /**
     * Tests 'en passant' captures for White.
     */
    @Test
    public void testEnPassantWhite() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_WEP_E5D6), 0);

        // There should be no normal pawn moves
        int count = MOVE_GENERATOR.getNormalPawnMoves(Square.E5);
        assertEquals(0, count);

        // There should be one 'en passant' move: exd6 e.p.
        long square = MOVE_GENERATOR.getEnPassantPawnMove(Square.E5);
        assertEquals(Square.D6, square);
    }

    /**
     * Tests 'en passant' captures for Black.
     */
    @Test
    public void testEnPassantBlack() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_BEP_D4C3), 0);

        // There should be one normal move: d3
        int count = MOVE_GENERATOR.getNormalPawnMoves(Square.D4);
        assertEquals(1, count);
        // We cannot check the move, because it is stored in a private field

        // There should be one 'en passant' move: dxc3 e.p.
        long square = MOVE_GENERATOR.getEnPassantPawnMove(Square.D4);
        assertEquals(Square.C3, square);
    }

	/**
	 * Tests promotion moves for White.
	 */
    @Test
	public void testPromotionWhite() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_WP_D7D8_OR_D7C8), 0);

        // Get all possible pawn moves
        MOVE_GENERATOR.generatePawnMoves();
        assertTrue(MOVE_GENERATOR.getMoveIndex() > 8);

        // Save the promotion pieces
        Set<Integer> d7d8 = new HashSet<>();
        Set<Integer> d7c8 = new HashSet<>();
        for (int i = 0; i < MOVE_GENERATOR.getMoveIndex(); i++) {
            int move = MOVE_GENERATOR.moves[0][i];
            if ((Move.getFrom(move) == Square.D7) && (Move.getTo(move) == Square.D8)) {
                d7d8.add(Move.getPromoted(move));
            } else if ((Move.getFrom(move) == Square.D7) && (Move.getTo(move) == Square.C8)) {
                d7c8.add(Move.getPromoted(move));
            }
        }

        // There should be exactly 4 promotion pieces
        assertEquals(4, d7d8.size());
        assertEquals(4, d7c8.size());

        assertTrue(d7d8.contains(Piece.BISHOP));
        assertTrue(d7d8.contains(Piece.KNIGHT));
        assertTrue(d7d8.contains(Piece.ROOK));
        assertTrue(d7d8.contains(Piece.QUEEN));

        assertTrue(d7c8.contains(Piece.BISHOP));
        assertTrue(d7c8.contains(Piece.KNIGHT));
        assertTrue(d7c8.contains(Piece.ROOK));
        assertTrue(d7c8.contains(Piece.QUEEN));
	}

    /**
     * Tests promotion moves for Black.
     */
    @Test
    public void testPromotionBlack() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_BP_A2A1), 0);

        // Get all possible pawn moves
        MOVE_GENERATOR.generatePawnMoves();
        assertTrue(MOVE_GENERATOR.getMoveIndex() > 4);

        // Save the promotion pieces
        Set<Integer> promotionPieces = new HashSet<>();
        for (int i = 0; i < MOVE_GENERATOR.getMoveIndex(); i++) {
            int move = MOVE_GENERATOR.moves[0][i];
            if ((Move.getFrom(move) == Square.A2) && (Move.getTo(move) == Square.A1)) {
                promotionPieces.add(Move.getPromoted(move));
            }
        }

        // There should be exactly 4 promotion pieces
        assertEquals(4, promotionPieces.size());

        assertTrue(promotionPieces.contains(Piece.BISHOP));
        assertTrue(promotionPieces.contains(Piece.KNIGHT));
        assertTrue(promotionPieces.contains(Piece.ROOK));
        assertTrue(promotionPieces.contains(Piece.QUEEN));
    }

    /**
     * Test finding squares attacked by a pawn in position {@link #FEN_START}.
     */
    @Test
    public void testAttackPositionStart() throws Exception {
        // WHITE
        ATTACK_GENERATOR.setup(Color.WHITE, FenParser.parse(FEN_START));
        assertEquals(Square.A3 | Square.B3 | Square.C3 | Square.D3 | Square.E3 | Square.F3 | Square.G3 | Square.H3,
                ATTACK_GENERATOR.getAllPawnAttacks());

        // BLACK
        ATTACK_GENERATOR.setup(Color.BLACK, FenParser.parse(FEN_START));
        assertEquals(Square.A6 | Square.B6 | Square.C6 | Square.D6 | Square.E6 | Square.F6 | Square.G6 | Square.H6,
                ATTACK_GENERATOR.getAllPawnAttacks());
    }

    /**
     * Test finding squares attacked by a pawn in position {@link #FEN_END_GAME_0}.
     */
    @Test
    public void testAttackEndGame0() throws Exception {
        // WHITE
        ATTACK_GENERATOR.setup(Color.WHITE, FenParser.parse(FEN_END_GAME_0));
        assertEquals(Square.A5 | Square.C5 | Square.E3 | Square.G3 | Square.E6 | Square.G6 | Square.F4 | Square.H4,
                ATTACK_GENERATOR.getAllPawnAttacks());

        // BLACK
        ATTACK_GENERATOR.setup(Color.BLACK, FenParser.parse(FEN_END_GAME_0));
        assertEquals(Square.A4 | Square.C4 | Square.B6 | Square.D6 | Square.C5 | Square.E5,
                ATTACK_GENERATOR.getAllPawnAttacks());
    }

    /**
     * Test finding squares attacked by a pawn in position {@link #FEN_END_GAME_3}.
     */
    @Test
    public void testAttackEndGame3() throws Exception {
        // WHITE
        ATTACK_GENERATOR.setup(Color.WHITE, FenParser.parse(FEN_END_GAME_3));
        assertEquals(Square.B6 | Square.D6 | Square.G5,
                ATTACK_GENERATOR.getAllPawnAttacks());

        // BLACK
        ATTACK_GENERATOR.setup(Color.BLACK, FenParser.parse(FEN_END_GAME_3));
        assertEquals(Square.B5 | Square.A1 | Square.C1 | Square.C2 | Square.E2 | Square.G2 | Square.G4,
                ATTACK_GENERATOR.getAllPawnAttacks());
    }
}
