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
import se.dykstrom.ronja.common.model.Move;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.model.Color;
import se.dykstrom.ronja.common.model.Piece;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

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

    private List<Long> squares;

    // ------------------------------------------------------------------------

    /**
     * Tests generating pawn moves from position {@link #FEN_START}.
     */
    @Test
    public void testPositionStart() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_START));

        // There should be 16 possible pawn moves in this position
        List<Move> moves = MOVE_GENERATOR.getAllPawnMoves();
        assertEquals(16, moves.size());
    }

    /**
     * Tests 'en passant' captures for White.
     */
    @Test
    public void testEnPassantWhite() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_WEP_E5D6));

        // There should be no normal pawn moves
        squares = MOVE_GENERATOR.getNormalPawnMoves(Square.E5);
        assertEquals(0, squares.size());

        // There should be one 'en passant' move: exd6 e.p.
        squares = MOVE_GENERATOR.getEnPassantPawnMoves(Square.E5);
        assertEquals(1, squares.size());
        assertEquals(Square.D6, squares.get(0).longValue());
    }

    /**
     * Tests 'en passant' captures for Black.
     */
    @Test
    public void testEnPassantBlack() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_BEP_D4C3));

        // There should be one normal move: d3
        squares = MOVE_GENERATOR.getNormalPawnMoves(Square.D4);
        assertEquals(1, squares.size());
        assertEquals(Square.D3, squares.get(0).longValue());

        // There should be one 'en passant' move: dxc3 e.p.
        squares = MOVE_GENERATOR.getEnPassantPawnMoves(Square.D4);
        assertEquals(1, squares.size());
        assertEquals(Square.C3, squares.get(0).longValue());
    }

	/**
	 * Tests promotion moves for White.
	 */
    @Test
	public void testPromotionWhite() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_WP_D7D8_OR_D7C8));

        // Get all possible pawn moves
        List<Move> allMoves = MOVE_GENERATOR.getAllPawnMoves();
        assertTrue(allMoves.size() > 8);

        // Save the promotion pieces
        Set<Piece> d7d8 = new HashSet<>();
        Set<Piece> d7c8 = new HashSet<>();
        for (Move move : allMoves) {
            if ((move.getFrom() == Square.D7) && (move.getTo() == Square.D8)) {
                assertNotNull(move.getPromoted());
                d7d8.add(move.getPromoted());
            } else if ((move.getFrom() == Square.D7) && (move.getTo() == Square.C8)) {
                assertNotNull(move.getPromoted());
                d7c8.add(move.getPromoted());
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
        MOVE_GENERATOR.setup(FenParser.parse(FEN_BP_A2A1));

        // Get all possible pawn moves
        List<Move> allMoves = MOVE_GENERATOR.getAllPawnMoves();
        assertTrue(allMoves.size() > 4);

        // Save the promotion pieces
        Set<Piece> promotionPieces = new HashSet<>();
        allMoves.stream().filter(move -> (move.getFrom() == Square.A2) && (move.getTo() == Square.A1)).forEach(move -> {
            assertNotNull(move.getPromoted());
            promotionPieces.add(move.getPromoted());
        });

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
