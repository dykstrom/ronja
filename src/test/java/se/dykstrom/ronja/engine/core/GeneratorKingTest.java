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
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.common.parser.MoveParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.assertEquals;
import static se.dykstrom.ronja.common.model.Piece.KING;

/**
 * This class is for testing king moves with the generator classes using JUnit.
 *
 * @author Johan Dykstrom
 * @see AttackGenerator
 * @see FullMoveGenerator
 */
public class GeneratorKingTest extends AbstractTestCase {

    private static final AttackGenerator ATTACK_GENERATOR = new AttackGenerator();

    private static final FullMoveGenerator MOVE_GENERATOR = new FullMoveGenerator();

    private Position position;

	// ------------------------------------------------------------------------

    /**
     * Test generating king moves from position {@link #FEN_START}.
     */
    @Test
    public void testPositionStart() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_START), 0);

        // There should be no moves in this position
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(0, MOVE_GENERATOR.getMoveIndex());
    }

    /**
     * Test king-side castling when it is OK to castle for white.
     */
    @Test
    public void testKSCastlingWhiteOk() throws Exception {
        // WHITE
        MOVE_GENERATOR.setup(FenParser.parse(FEN_WKC_OK), 0);

        // There should be three moves
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(3, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E1, Square.E2), // Ke2
                                             Move.create(KING, Square.E1, Square.F1), // Kf1
                                             Move.createCastling(Square.E1, Square.G1));  // O-O
    }

    /**
     * Test king-side castling when it is OK to castle for black.
     */
    @Test
    public void testKSCastlingBlackOk() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_BKC_OK), 0);

        // There should be four moves
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(4, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E8, Square.D7), // Kd7
                                             Move.create(KING, Square.E8, Square.E7), // Ke7
                                             Move.create(KING, Square.E8, Square.F8), // Kf8
                                             Move.createCastling(Square.E8, Square.G8));  // O-O
    }

    /**
     * Test king-side castling when it is not OK to castle because the king (or rook) has moved.
     */
    @Test
	public void testKSCastlingNokKing() throws Exception {
        // WHITE
        MOVE_GENERATOR.setup(position = FenParser.parse(FEN_WKC_NOK_K), 0);

		// There should only be two moves
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(2, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E1, Square.E2), // Ke2
                                             Move.create(KING, Square.E1, Square.F1)); // Kf1

        // BLACK
        // TODO: Create position FEN_BKC_NOK_K for this.
        MOVE_GENERATOR.setup(position.withMove(MoveParser.parse("e1f1", position)), 0);

		// There should only be two moves
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(2, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E8, Square.E7), // Ke7
                                             Move.create(KING, Square.E8, Square.F8)); // Kf8
    }

    // TODO: Also test that it is NOK to castle when the rook has been taken.

    /**
     * Test king-side castling when it is not OK to castle because a piece is in the way.
     */
    @Test
	public void testKSCastlingNokPiece() throws Exception {
        // WHITE
        MOVE_GENERATOR.setup(position = FenParser.parse(FEN_WKC_NOK_P), 0);

		// There should only be two moves
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(2, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E1, Square.E2), // Ke2
                                             Move.create(KING, Square.E1, Square.F1)); // Kf1

        // BLACK
        // TODO: Create position FEN_BKC_NOK_P for this.
        MOVE_GENERATOR.setup(position.withMove(MoveParser.parse("e1f1", position)), 0);

		// There should only be two moves
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(2, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E8, Square.E7), // Ke7
                                             Move.create(KING, Square.E8, Square.F8)); // Kf8
    }

    /**
     * Test king-side castling when it is not OK to castle because a square in the middle is checked.
     */
    @Test
    public void testKSCastlingWhiteNokCheck() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_WKC_NOK_C), 0);

        // There should be one move: Ke2, but not Kf1 or O-O (e1g1)
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(1, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E1, Square.E2)); // Ke2
    }

    // TODO: Also test that it is NOK to castle when the king is checked.

    /**
     * Test king-side castling when it is not OK to castle because a square in the middle is checked.
     */
    @Test
    public void testKSCastlingBlackNokCheck() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_BKC_NOK_C), 0);

        // There should be no moves: Ke7, Kf8, and O-O (e8g8) all leave the king in check
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(0, MOVE_GENERATOR.getMoveIndex());
    }

    /**
     * Test queen-side castling when it is OK to castle for white.
     */
    @Test
    public void testQSCastlingWhiteOk() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_WQC_OK), 0);

        // There should be two moves
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(2, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E1, Square.D1), // Kd1
                                             Move.createCastling(Square.E1, Square.C1));  // O-O-O
    }

    /**
     * Test queen-side castling when it is OK to castle for black.
     */
    @Test
    public void testQSCastlingBlackOk() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_BQC_OK), 0);

        // There should be three moves
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(3, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E8, Square.D7), // Kd7
                                             Move.create(KING, Square.E8, Square.D8), // Kd8
                                             Move.createCastling(Square.E8, Square.C8));  // O-O-O
    }

    /**
     * Test queen-side castling when is OK to castle for White, even though square B1 is checked.
     */
    @Test
    public void testQSCastlingWhiteOkCheck() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_WQC_OK_C_B1), 0);

        // There should be three moves: Kd1 and Ke2, and O-O-O (e1c1)
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(3, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E1, Square.D1), // Kd1
                                             Move.create(KING, Square.E1, Square.E2), // Ke2
                                             Move.createCastling(Square.E1, Square.C1)); // O-O-O
    }

    /**
     * Test queen-side castling when is OK to castle for Black, even though square B8 is checked.
     */
    @Test
    public void testQSCastlingBlackOkCheck() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_BQC_OK_C_B8), 0);

        // There should be three moves: Kd7 and Kd8, and O-O-O (e8c8)
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(3, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E8, Square.D7), // Kd7
                                             Move.create(KING, Square.E8, Square.D8), // Kd8
                                             Move.createCastling(Square.E8, Square.C8)); // O-O-O
    }

    /**
     * Test queen-side castling when it is not OK to castle because the king (or rook) has moved.
     */
    @Test
	public void testQSCastlingNokKing() throws Exception {
        // WHITE
        MOVE_GENERATOR.setup(position = FenParser.parse(FEN_WQC_NOK_K), 0);

		// There should only be one move
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(1, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E1, Square.D1)); // Kd1

        // BLACK
        // TODO: Create position FEN_BQC_NOK_K for this.
        MOVE_GENERATOR.setup(position.withMove(MoveParser.parse("e1d1", position)), 0);

		// There should only be one move
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(1, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E8, Square.D8)); // Kd8
    }

    /**
     * Test queen-side castling when it is not OK to castle because a piece is in the way.
     */
    @Test
	public void testQSCastlingNokPiece() throws Exception {
        // WHITE
        MOVE_GENERATOR.setup(position = FenParser.parse(FEN_WQC_NOK_P), 0);

		// There should only be one move
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(1, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E1, Square.D1)); // Kd1

        // BLACK
        // TODO: Create position FEN_BQC_NOK_P for this.
        MOVE_GENERATOR.setup(position.withMove(MoveParser.parse("e1d1", position)), 0);

		// There should only be one move
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(1, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E8, Square.D8)); // Kd8
    }

    /**
     * Test queen-side castling when is not OK to castle because a square in
     * the middle is checked.
     */
    @Test
    public void testQSCastlingWhiteNokCheck() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_WQC_NOK_C_C1), 0);

        // There should only be two moves: Kd1 and Ke2, but not O-O-O (e1c1)
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(2, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E1, Square.D1), // Kd1
                                             Move.create(KING, Square.E1, Square.E2)); // Ke2
    }

    /**
     * Test queen-side castling when is not OK to castle because a square in
     * the middle is checked.
     */
    @Test
    public void testQSCastlingBlackNokCheck() throws Exception {
        MOVE_GENERATOR.setup(FenParser.parse(FEN_BQC_NOK_C_C8), 0);

        // There should only be one move: Kd8, but not Kd7 or O-O-O (e8c8)
        MOVE_GENERATOR.generateKingMoves();
        assertEquals(1, MOVE_GENERATOR.getMoveIndex());
        assertGeneratedMoves(MOVE_GENERATOR, Move.create(KING, Square.E8, Square.D8)); // Kd8
    }

    // -----------------------------------------------------------------------

    /**
     * Tests finding squares attacked by the king in position {@link #FEN_START}.
     */
    @Test
    public void testAttackPositionStart() throws Exception {
        // WHITE
        ATTACK_GENERATOR.setup(Color.WHITE, FenParser.parse(FEN_START));
        assertEquals(Square.D1 | Square.D2 | Square.E2 | Square.F2 | Square.F1,
                ATTACK_GENERATOR.getAllKingAttacks());

        // BLACK
        ATTACK_GENERATOR.setup(Color.BLACK, FenParser.parse(FEN_START));
        assertEquals(Square.D8 | Square.D7 | Square.E7 | Square.F7 | Square.F8,
                ATTACK_GENERATOR.getAllKingAttacks());
    }

    /**
     * Test finding squares attacked by the king in position {@link #FEN_END_GAME_0}.
     */
    @Test
    public void testAttackEndGame0() throws Exception {
        // WHITE
        ATTACK_GENERATOR.setup(Color.WHITE, FenParser.parse(FEN_END_GAME_0));
        assertEquals(Square.F1 | Square.F2 | Square.G2 | Square.H2 | Square.H1,
                ATTACK_GENERATOR.getAllKingAttacks());

        // BLACK
        ATTACK_GENERATOR.setup(Color.BLACK, FenParser.parse(FEN_END_GAME_0));
        assertEquals(Square.F6 | Square.F7 | Square.F8 | Square.G8 | Square.H8 | Square.H7 | Square.H6 | Square.G6,
                ATTACK_GENERATOR.getAllKingAttacks());
    }
}
