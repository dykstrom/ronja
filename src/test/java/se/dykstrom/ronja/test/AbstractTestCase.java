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

package se.dykstrom.ronja.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import se.dykstrom.ronja.common.model.*;
import se.dykstrom.ronja.common.parser.IllegalMoveException;
import se.dykstrom.ronja.common.parser.MoveParser;

/**
 * An abstract base test case for all {@code se.dykstrom.ronja} test cases.
 *
 * @author Johan Dykstrom
 */
public abstract class AbstractTestCase {

    // Single moves
    protected static final int MOVE_E2E4 = Move.create(Piece.PAWN, Square.E2, Square.E4);
    protected static final int MOVE_D2D4 = Move.create(Piece.PAWN, Square.D2, Square.D4);
    protected static final int MOVE_C2C4 = Move.create(Piece.PAWN, Square.C2, Square.C4);
    protected static final int MOVE_E7E5 = Move.create(Piece.PAWN, Square.E7, Square.E5);
    protected static final int MOVE_E7E6 = Move.create(Piece.PAWN, Square.E7, Square.E6);
    protected static final int MOVE_C7C5 = Move.create(Piece.PAWN, Square.C7, Square.C5);
    protected static final int MOVE_G1F3 = Move.create(Piece.KNIGHT, Square.G1, Square.F3);
    protected static final int MOVE_E1G1 = Move.createCastling(Square.E1, Square.G1);
    protected static final int MOVE_E1C1 = Move.createCastling(Square.E1, Square.C1);
    protected static final int MOVE_E8G8 = Move.createCastling(Square.E8, Square.G8);
    protected static final int MOVE_E8C8 = Move.createCastling(Square.E8, Square.C8);

    // Move sequences
    protected static final String[] MOVE_START            = { };
    protected static final String[] MOVE_E4               = { "e2e4" };
    protected static final String[] MOVE_E4_C5            = { "e2e4", "c7c5" };
    protected static final String[] MOVE_E4_C5_KE2        = { "e2e4", "c7c5", "e1e2" };
    protected static final String[] MOVE_E4_C5_NF3        = { "e2e4", "c7c5", "g1f3" };
    protected static final String[] MOVE_E4_C5_NF3_NC6    = { "e2e4", "c7c5", "g1f3", "b8c6" };
    protected static final String[] MOVE_E4_C5_NF3_NC6_D4 = { "e2e4", "c7c5", "g1f3", "b8c6", "d2d4" };
    protected static final String[] MOVE_E4_E5_NF3        = { "e2e4", "e7e5", "g1f3" };
    protected static final String[] MOVE_E4_E5_D4_D5      = { "e2e4", "e7e5", "d2d4", "d7d5" };
    protected static final String[] MOVE_D4_E5_E4_D5      = { "d2d4", "e7e5", "e2e4", "d7d5" };
    protected static final String[] MOVE_SCHOLARS_MATE    = { "e2e4", "e7e5", "f1c4", "f8c5", "d1h5", "g8f6", "h5f7" };

    // FEN positions resulting from specific move sequences
    protected static final String FEN_START             = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    protected static final String FEN_E4                = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
    protected static final String FEN_E4_C5             = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2";
    protected static final String FEN_E4_C5_KE2         = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPPKPPP/RNBQ1BNR b kq - 1 2";
    protected static final String FEN_E4_C5_NF3         = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2";
    protected static final String FEN_E4_C5_NF3_NC6     = "r1bqkbnr/pp1ppppp/2n5/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3";
    protected static final String FEN_E4_C5_NF3_NC6_D4  = "r1bqkbnr/pp1ppppp/2n5/2p5/3PP3/5N2/PPP2PPP/RNBQKB1R b KQkq d3 0 3";
    protected static final String FEN_E4_E5             = "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2";
    protected static final String FEN_E4_E5_BC4         = "rnbqkbnr/pppp1ppp/8/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR b KQkq - 1 2";
    protected static final String FEN_E4_E5_BC4_BA3     = "rnbqk1nr/pppp1ppp/8/4p3/2B1P3/b7/PPPP1PPP/RNBQK1NR w KQkq - 2 3";
    protected static final String FEN_E4_E5_BC4_BA3_B4  = "rnbqk1nr/pppp1ppp/8/4p3/1PB1P3/b7/P1PP1PPP/RNBQK1NR b KQkq b3 0 3";
    protected static final String FEN_E4_E5_QG4_QH4     = "rnb1kbnr/pppp1ppp/8/4p3/4P1Qq/8/PPPP1PPP/RNB1KBNR w KQkq - 0 1";
    protected static final String FEN_E4_E5_QG4_QH4_NF3 = "rnb1kbnr/pppp1ppp/8/4p3/4P1Qq/5N2/PPPP1PPP/RNB1KB1R b KQkq - 0 1";
    protected static final String FEN_E4_E6             = "rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2";
    protected static final String FEN_SCHOLARS_MATE     = "rnbqk2r/pppp1Qpp/5n2/2b1p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4";

    // FEN positions that test a specific property
    // Pawn captures
    protected static final String FEN_PC_E4D5          = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 1";
    protected static final String FEN_PC_D5E4_D5C4     = "rnbqkbnr/ppp1pppp/8/3p4/2P1P3/8/PP1P1PPP/RNBQKBNR b KQkq c3 0 1";
    protected static final String FEN_PC_E4D5_C4D5     = "rnb1kbnr/ppp1pppp/3q4/3p4/2P1P3/8/PP1P1PPP/RNBQKBNR w KQkq - 1 2";

    // En passant captures
    protected static final String FEN_WEP_E5D6          = "rnbqkbnr/ppp2ppp/4p3/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3";
    protected static final String FEN_BEP_D4C3          = "rnbqkbnr/ppp1pppp/8/4P3/2Pp4/8/PP1P1PPP/RNBQKBNR b KQkq c3 0 3";

    // Promotion moves
    protected static final String FEN_WP_D7D8_OR_D7C8   = "rnb2bnr/1ppPkppp/5q2/p3p3/3P4/8/PPP2PPP/RNBQKBNR w KQ - 1 6";
    protected static final String FEN_BP_A2A1           = "rnbqkbnr/p1pppppp/8/8/1P6/2N5/pRPPPPPP/2BQKBNR b Kkq - 1 5";
    protected static final String FEN_WP_E7F8           = "rnb1kbnr/3pPppp/1q6/ppp5/8/8/PPP1PPPP/RNBQKBNR w KQkq - 1 5";
    protected static final String FEN_BP_B2A1           = "rnbqkbnr/1ppppppp/8/8/2B1P3/2N2N2/PpPP1PPP/R1BQ1RK1 b kq - 1 5";

    // Castling moves
    protected static final String FEN_WKC_OK            = "rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4";
    protected static final String FEN_BKC_OK            = "rnbqk2r/ppp2ppp/3bpn2/8/2B5/5N2/PPPP1PPP/RNBQK2R b KQkq - 3 5";
    protected static final String FEN_WKC_NOK_K         = "rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w - - 8 6";
    protected static final String FEN_WKC_NOK_P         = "rnbqk1nr/pppp1ppp/8/2b1p3/2B1P3/8/PPPP1PPP/RNBQK1NR w KQkq - 6 5";
    protected static final String FEN_WKC_NOK_C         = "rnbqk2r/pppp1ppp/8/4p3/2B1P3/BP2nN2/P1PP1PPP/RN1QK2R w KQkq - 3 6";
    protected static final String FEN_BKC_NOK_C         = "rnbqk2r/pppp1ppp/5n2/4p3/4P3/BP3N2/P1PP1PPP/RN1QKB1R b KQkq - 0 4";
    protected static final String FEN_WQC_OK            = "r3kbnr/pppqpppp/2n1b3/3p4/3P4/2N1B3/PPPQPPPP/R3KBNR w KQkq - 6 5";
    protected static final String FEN_BQC_OK            = "r3kbnr/ppp1qppp/2np4/1B2p3/4P1b1/2N2N2/PPPP1PPP/R1BQR1K1 b kq - 5 6";
    protected static final String FEN_WQC_OK_C_B1       = "r1bq1rk1/pppp1ppp/5n2/4p3/4P3/n1NP1Q2/P1PB1PPP/R3KBNR w KQ - 0 1";
    protected static final String FEN_BQC_OK_C_B8       = "r3kbnr/pp2pppp/2n1b3/2p5/5Q2/2N5/PPPP1PPP/R1B1KBNR b KQkq - 0 1";
    protected static final String FEN_WQC_NOK_K         = "r3kbnr/pppqpppp/2n1b3/3p4/3P4/2N1B3/PPPQPPPP/R3KBNR w - - 10 7";
    protected static final String FEN_WQC_NOK_P         = "rn2kbnr/pppqpppp/4b3/3p4/3P4/4B3/PPPQPPPP/RN2KBNR w KQkq - 8 6";
    protected static final String FEN_WQC_NOK_C_C1      = "rnbq1rk1/pppp1ppp/5n2/4p3/4P3/2NP1Q2/PbPB1PPP/R3KBNR w KQ - 0 1";
    protected static final String FEN_BQC_NOK_C_C8      = "r3kbnr/ppp1pppp/1Nn1b3/8/8/8/PPPP1PPP/R1BQKBNR b KQkq - 0 1";

    // Opening positions
    protected static final String FEN_OPENING_0         = "r2qkbnr/pppb1ppp/2np4/1B2p3/3PP3/5N2/PPP2PPP/RNBQK2R w KQkq - 1 5";
    protected static final String FEN_OPENING_1         = "r1bqkbnr/ppp2ppp/2n5/1B1pp3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq d6 0 4";
    protected static final String FEN_OPENING_2         = "rnbqkb1r/pppp1ppp/5n2/4p3/2P5/2N2N2/PP1PPPPP/R1BQKB1R b KQkq - 3 3";

    // Middle-game positions
    protected static final String FEN_MIDDLE_GAME_0     = "r3kb1r/pbpnqppp/1p2pn2/3p2B1/2PP4/2N1PN2/PPQ2PPP/2KR1B1R b kq - 3 8";
    /* Danielsen-Gunnarsson, 2006 */
    protected static final String FEN_MIDDLE_GAME_1     = "R4rk1/1bq2pbp/2n2np1/1pp1p3/2N1PP2/2P2NPP/1P4B1/2B1QRK1 b - - 0 16";
    /** treeless_druid-volhouder, 2015 */
    protected static final String FEN_MIDDLE_GAME_2     = "rn3qk1/pp4pb/1b3p1p/3p4/4nP2/1P2PNP1/PB1P2BP/2RQ1RK1 b - - 0 1";

    // End-game positions
    protected static final String FEN_END_GAME_0        = "5rn1/2pq1Bk1/3p1b1N/1p3P1Q/1P6/6P1/5P2/4R1K1 b - - 0 36";
    protected static final String FEN_END_GAME_1        = "4k3/8/8/3Bb3/8/8/8/4K3 w - - 4 3";
    protected static final String FEN_END_GAME_2        = "4k3/8/8/4b3/8/8/8/4K2B b - - 5 3";
    protected static final String FEN_END_GAME_3        = "8/8/p7/P1P4p/7P/3pNpk1/1p2b3/4K3 w - - 0 51";

    // A sequence of positions leading to white being back rank checkmated
    protected static final String FEN_CHECKMATE_1_0     = "r7/1p3pkp/2p3p1/5n2/5B2/8/1P3PPP/6K1 b - - 0 17";
    protected static final String FEN_CHECKMATE_1_1     = "8/1p3pkp/2p3p1/5n2/5B2/8/1P3PPP/r5K1 w - - 1 18";
    protected static final String FEN_CHECKMATE_1_2     = "8/1p3pkp/2p3p1/5n2/8/8/1P3PPP/r1B3K1 b - - 2 18";
    protected static final String FEN_CHECKMATE_1_3     = "8/1p3pkp/2p3p1/5n2/8/8/1P3PPP/2r3K1 w - - 0 19";

    // A sequence of positions leading to black being checkmated in a smothered mate (Timman-Short, 1990)
    protected static final String FEN_CHECKMATE_2_0     = "4r1k1/2pRP1pp/2p5/p4pN1/5Qn1/q5P1/P3PP1P/6K1 w - - 0 26";
    protected static final String FEN_CHECKMATE_2_1     = "4r1k1/2pRP1pp/2p5/p4pN1/2Q3n1/q5P1/P3PP1P/6K1 b - - 1 26";
    protected static final String FEN_CHECKMATE_2_2     = "4r2k/2pRP1pp/2p5/p4pN1/2Q3n1/q5P1/P3PP1P/6K1 w - - 2 27";
    protected static final String FEN_CHECKMATE_2_3     = "4r2k/2pRPNpp/2p5/p4p2/2Q3n1/q5P1/P3PP1P/6K1 b - - 3 27";
    protected static final String FEN_CHECKMATE_2_4     = "4r1k1/2pRPNpp/2p5/p4p2/2Q3n1/q5P1/P3PP1P/6K1 w - - 4 28";
    protected static final String FEN_CHECKMATE_2_5     = "4r1k1/2pRP1pp/2p4N/p4p2/2Q3n1/q5P1/P3PP1P/6K1 b - - 5 28";
    protected static final String FEN_CHECKMATE_2_6     = "4r2k/2pRP1pp/2p4N/p4p2/2Q3n1/q5P1/P3PP1P/6K1 w - - 6 29";
    protected static final String FEN_CHECKMATE_2_7     = "4r1Qk/2pRP1pp/2p4N/p4p2/6n1/q5P1/P3PP1P/6K1 b - - 7 29";
    protected static final String FEN_CHECKMATE_2_8     = "6rk/2pRP1pp/2p4N/p4p2/6n1/q5P1/P3PP1P/6K1 w - - 8 30";
    protected static final String FEN_CHECKMATE_2_9     = "6rk/2pRPNpp/2p5/p4p2/6n1/q5P1/P3PP1P/6K1 b - - 9 30";

    // A sequence of positions leading to white being checkmated (ChessFree-Dykstrom, 2015)
    protected static final String FEN_CHECKMATE_3_0     = "r4rk1/pp2qp1p/2n3p1/6R1/1b3B1P/1P3Q2/b1P2PP1/2K2BNR b - - 0 15";
    protected static final String FEN_CHECKMATE_3_1     = "r4rk1/pp3p1p/2n3p1/6R1/1b3B1P/1P3Q2/b1P2PP1/2K1qBNR w - - 1 16";
    protected static final String FEN_CHECKMATE_3_2     = "r4rk1/pp3p1p/2n3p1/6R1/1b3B1P/1P6/b1P2PP1/2KQqBNR b - - 2 16";
    protected static final String FEN_CHECKMATE_3_3     = "r4rk1/pp3p1p/2n3p1/6R1/5B1P/bP6/b1P2PP1/2KQqBNR w - - 3 17";

    // A sequence of moves leading to a forced draw
    protected static final String FEN_DRAW_1_0          = "8/k1P5/p7/P7/7r/1R3B2/PP2PP2/K7 b - - 0 1";
    protected static final String FEN_DRAW_1_1          = "8/k1P5/p7/P7/8/1R3B2/PP2PP2/K6r w - - 1 2";
    protected static final String FEN_DRAW_1_2          = "8/k1P5/p7/P7/8/1R6/PP2PP2/K6B b - - 0 2";

    // Another sequence of moves leading to a draw
    protected static final String FEN_DRAW_2_0          = "8/kP6/8/8/4K3/8/3B3p/8 w - - 0 1";
    protected static final String FEN_DRAW_2_1          = "1Q6/k7/8/8/4K3/8/3B3p/8 b - - 0 1";
    protected static final String FEN_DRAW_2_2          = "1k6/8/8/8/4K3/8/3B3p/8 w - - 0 2";
    protected static final String FEN_DRAW_2_3          = "1k6/8/8/8/4KB2/8/7p/8 b - - 1 2";
    protected static final String FEN_DRAW_2_4          = "8/1k6/8/8/4KB2/8/7p/8 w - - 1 3";
    protected static final String FEN_DRAW_2_5          = "8/1k6/8/8/4K3/8/7B/8 b - - 0 3";

    // Other positions
    /** A white bishop captures in the corner. */
    protected static final String FEN_CAPTURE_IN_CORNER = "r2qkbnr/p1pb1ppp/2Bp4/1p2p3/3PP3/5N2/PPPQ1PPP/RNB1K2R w KQkq - 0 7";
    /** A white queen placed in the corner. */
    protected static final String FEN_QUEEN_IN_CORNER   = "4k3/8/8/8/K7/8/8/7Q w - - 0 1";
    /** A black queen surrounded by 8 enemy pieces. */
    protected static final String FEN_EIGHT_CAPTURES    = "rnb1kbnr/pppp1ppp/4p3/8/8/1BPN2P1/PPqPPP1P/RNBQK2R b KQkq - 0 1";
    /** Many captures for white. */
    protected static final String FEN_MANY_CAPTURES     = "3R4/pN6/R2r1k1p/1N4p1/2N5/8/PP5Q/1K1R4 w - - 0 1";
    /** Two black queens with some squares are reachable by both queens. */
    protected static final String FEN_TWO_QUEENS        = "5r1k/4Q2p/2q5/p5B1/1pBPP3/5qP1/1P5P/6K1 b - - 0 1";
    /** Just one bishop and the kings. */
    protected static final String FEN_ONE_BISHOP        = "8/8/8/8/4k3/4b3/4K3/8 w - - 0 1";
    /** Black is check mated. Moves can be generated, but will be rejected by the Evaluator. */
    protected static final String FEN_CHECKMATE_0       = "1n1Rkb1r/p4ppp/4q3/4p1B1/4P3/8/PPP2PPP/2K5 b k - 1 17";
    /** An empty board. */
    protected static final String FEN_ILLEGAL_0         = "8/8/8/8/8/8/8/8 w KQkq - 0 1";
    /** Possible fork for white. */
    protected static final String FEN_FORK_0            = "r1b1k1nr/ppppbppp/2n5/1N6/4q3/2P5/PP1PBPPP/R1BQK2R w KQkq - 0 1";
    protected static final String FEN_FORK_1            = "rn1q1rk1/pp4pb/1bp1pp1p/3p4/2PNnP2/1P2PNP1/PB1P2BP/R2Q1RK1 w - - 0 1";

    /**
     * Asserts that all given {@code squares} are empty in the given {@code position}.
     */
    protected static void assertEmpty(Position position, long... squares) {
        for (long square : squares) {
            assertEquals(0,  position.getPiece(square));
            assertNull(position.getColor(square));
        }
    }

    /**
     * Asserts that {@code square} contains a piece of type {@code piece} and {@code color} in the given {@code position}.
     */
    protected static void assertPiece(Position position, long square, Color color, int piece) {
        assertEquals(piece, position.getPiece(square));
        assertEquals(color, position.getColor(square));
    }

    /**
     * Converts an array of moves in string format to a list of real moves.
     * This method assumes that the moves are made from the start position.
     *
     * @param moves An array move moves in string format to convert.
     * @return The list of converted moves.
     * @throws IllegalMoveException If there was an illegal move.
     */
    protected static List<Integer> toMoveList(String[] moves) throws IllegalMoveException {
        Position position = Position.START;
        List<Integer> result = new ArrayList<>(moves.length);
        for (String move : moves) {
            result.add(MoveParser.parse(move, position));
            position = position.withMove(result.get(result.size() - 1));
        }
        return result;
    }
}
