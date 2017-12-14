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

package se.dykstrom.ronja.common.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static se.dykstrom.ronja.common.model.Piece.*;

import org.junit.Test;

import se.dykstrom.ronja.test.AbstractTestCase;

/**
 * This class is for testing class {@code Move} using JUnit.
 *
 * @author Johan Dykstrom
 * @see Move
 */
public class MoveTest extends AbstractTestCase {

    @Test
    public void shouldCreateNormalMove() {
        createAndAssertNormalMove(BISHOP, Square.A1, Square.C3);
        createAndAssertNormalMove(KING, Square.E1, Square.F1);
        createAndAssertNormalMove(KNIGHT, Square.G1, Square.F3);
        createAndAssertNormalMove(PAWN, Square.E2, Square.E4);
        createAndAssertNormalMove(QUEEN, Square.D1, Square.D8);
        createAndAssertNormalMove(ROOK, Square.A8, Square.H8);
    }
    
    @Test
    public void shouldCreateCaptureMove() {
        createAndAssertCaptureMove(BISHOP, Square.A1, Square.C3, BISHOP);
        createAndAssertCaptureMove(KING, Square.E8, Square.F8, BISHOP);
        createAndAssertCaptureMove(KNIGHT, Square.G8, Square.F6, PAWN);
        createAndAssertCaptureMove(PAWN, Square.A7, Square.A6, ROOK);
        createAndAssertCaptureMove(QUEEN, Square.D1, Square.D8, KNIGHT);
        createAndAssertCaptureMove(ROOK, Square.A8, Square.A1, QUEEN);
    }
    
    @Test
    public void shouldCreatePromotionMove() {
        createAndAssertPromotionMove(PAWN, Square.E7, Square.E8, ROOK);
        createAndAssertPromotionMove(PAWN, Square.H2, Square.H1, BISHOP);
    }
    
    @Test
    public void shouldCreateCapturePromotionMove() {
        createAndAssertCapturePromotionMove(PAWN, Square.E7, Square.F8, QUEEN, ROOK);
        createAndAssertCapturePromotionMove(PAWN, Square.H2, Square.G1, KNIGHT, BISHOP);
    }
    
    @Test
    public void shouldCreateCastlingMove() {
        createAndAssertCastlingMove(KING, Square.E1, Square.G1);
        createAndAssertCastlingMove(KING, Square.E1, Square.C1);
        createAndAssertCastlingMove(KING, Square.E8, Square.G8);
        createAndAssertCastlingMove(KING, Square.E8, Square.C8);
    }
    
    @Test
    public void shouldCreateEnPassantMove() {
        createAndAssertEnPassantMove(PAWN, Square.E6, Square.F7, PAWN);
        createAndAssertEnPassantMove(PAWN, Square.A3, Square.B2, PAWN);
    }

    private void createAndAssertNormalMove(int piece, long from, long to) {
        int move = Move.create(piece, from, to);
        assertMove(move, piece, from, to, 0, 0, false, false);
    }

    private void createAndAssertCaptureMove(int piece, long from, long to, int captured) {
        int move = Move.createCapture(piece, from, to, captured);
        assertMove(move, piece, from, to, captured, 0, false, false);
    }

    private void createAndAssertPromotionMove(int piece, long from, long to, int promoted) {
        int move = Move.createPromotion(from, to, promoted);
        assertMove(move, piece, from, to, 0, promoted, false, false);
    }

    private void createAndAssertCapturePromotionMove(int piece, long from, long to, int captured, int promoted) {
        int move = Move.createCapturePromotion(from, to, captured, promoted);
        assertMove(move, piece, from, to, captured, promoted, false, false);
    }

    private void createAndAssertCastlingMove(int piece, long from, long to) {
        int move = Move.createCastling(from, to);
        assertMove(move, piece, from, to, 0, 0, true, false);
    }

    private void createAndAssertEnPassantMove(int piece, long from, long to, int captured) {
        int move = Move.createEnPassant(from, to);
        assertMove(move, piece, from, to, captured, 0, false, true);
    }

    private void assertMove(int move, int piece, long from, long to, int captured, int promoted, boolean castling, boolean enPassant) {
        assertThat(Move.getPiece(move), is(piece));
        assertThat(Move.getFrom(move), is(from));
        assertThat(Move.getTo(move), is(to));
        assertThat(Move.getCaptured(move), is(captured));
        assertThat(Move.getPromoted(move), is(promoted));
        assertThat(Move.isCastling(move), is(castling));
        assertThat(Move.isEnPassant(move), is(enPassant));
        assertThat(Move.isPromotion(move), is(promoted != 0));
        assertThat(Move.isCapture(move), is(captured != 0));
    }
}
