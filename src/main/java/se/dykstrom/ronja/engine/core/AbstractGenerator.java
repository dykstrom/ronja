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

import se.dykstrom.ronja.common.model.Square;
import se.dykstrom.ronja.common.model.Board;

import static se.dykstrom.ronja.common.model.Square.*;

/**
 * An abstract base class that contains constants that are common to the two generator classes.
 *
 * @author Johan Dykstrom
 */
abstract class AbstractGenerator {

    /** The north border of the chess board (rank 8). */
    static final long NORTH_BORDER = Board.RANK_8;

    /** The east border of the chess board (file H). */
    static final long EAST_BORDER = Board.FILE_H;

    /** Everything except the east border (file H). */
    static final long NOT_EAST_BORDER = ~EAST_BORDER;

    /** The south border of the chess board (rank 1). */
    static final long SOUTH_BORDER = Board.RANK_1;

    /** The west border of the chess board (file A). */
    static final long WEST_BORDER = Board.FILE_A;

    /** Everything except the west border (file A). */
    static final long NOT_WEST_BORDER = ~WEST_BORDER;

    /** All squares a king can move to for each square. */
    static final long[] KING_MOVES = {
        // Rank 1
        Square.A2 + Square.B2 + Square.B1,
        Square.A1 + Square.A2 + Square.B2 + Square.C2 + Square.C1,
        Square.B1 + Square.B2 + Square.C2 + Square.D2 + Square.D1,
        Square.C1 + Square.C2 + Square.D2 + Square.E2 + Square.E1,
        Square.D1 + Square.D2 + Square.E2 + Square.F2 + Square.F1,
        Square.E1 + Square.E2 + Square.F2 + Square.G2 + Square.G1,
        Square.F1 + Square.F2 + Square.G2 + Square.H2 + Square.H1,
        Square.G1 + Square.G2 + Square.H2,

        // Rank 2
        Square.A3 + Square.B3 + Square.B2 + Square.B1 + Square.A1,
        Square.A2 + Square.A3 + Square.B3 + Square.C3 + Square.C2 + Square.C1 + Square.B1 + Square.A1,
        Square.B2 + Square.B3 + Square.C3 + Square.D3 + Square.D2 + Square.D1 + Square.C1 + Square.B1,
        Square.C2 + Square.C3 + Square.D3 + Square.E3 + Square.E2 + Square.E1 + Square.D1 + Square.C1,
        Square.D2 + Square.D3 + Square.E3 + Square.F3 + Square.F2 + Square.F1 + Square.E1 + Square.D1,
        Square.E2 + Square.E3 + Square.F3 + Square.G3 + Square.G2 + Square.G1 + Square.F1 + Square.E1,
        Square.F2 + Square.F3 + Square.G3 + Square.H3 + Square.H2 + Square.H1 + Square.G1 + Square.F1,
        Square.G2 + Square.G3 + Square.H3 + Square.H1 + Square.G1,

        // Rank 3
        Square.A4 + Square.B4 + Square.B3 + Square.B2 + Square.A2,
        Square.A3 + Square.A4 + Square.B4 + Square.C4 + Square.C3 + Square.C2 + Square.B2 + Square.A2,
        Square.B3 + Square.B4 + Square.C4 + Square.D4 + Square.D3 + Square.D2 + Square.C2 + Square.B2,
        Square.C3 + Square.C4 + Square.D4 + Square.E4 + Square.E3 + Square.E2 + Square.D2 + Square.C2,
        Square.D3 + Square.D4 + Square.E4 + Square.F4 + Square.F3 + Square.F2 + Square.E2 + Square.D2,
        Square.E3 + Square.E4 + Square.F4 + Square.G4 + Square.G3 + Square.G2 + Square.F2 + Square.E2,
        Square.F3 + Square.F4 + Square.G4 + Square.H4 + Square.H3 + Square.H2 + Square.G2 + Square.F2,
        Square.G3 + Square.G4 + Square.H4 + Square.H2 + Square.G2,

        // Rank 4
        Square.A5 + Square.B5 + Square.B4 + Square.B3 + Square.A3,
        Square.A4 + Square.A5 + Square.B5 + Square.C5 + Square.C4 + Square.C3 + Square.B3 + Square.A3,
        Square.B4 + Square.B5 + Square.C5 + Square.D5 + Square.D4 + Square.D3 + Square.C3 + Square.B3,
        Square.C4 + Square.C5 + Square.D5 + Square.E5 + Square.E4 + Square.E3 + Square.D3 + Square.C3,
        Square.D4 + Square.D5 + Square.E5 + Square.F5 + Square.F4 + Square.F3 + Square.E3 + Square.D3,
        Square.E4 + Square.E5 + Square.F5 + Square.G5 + Square.G4 + Square.G3 + Square.F3 + Square.E3,
        Square.F4 + Square.F5 + Square.G5 + Square.H5 + Square.H4 + Square.H3 + Square.G3 + Square.F3,
        Square.G4 + Square.G5 + Square.H5 + Square.H3 + Square.G3,

        // Rank 5
        Square.A6 + Square.B6 + Square.B5 + Square.B4 + Square.A4,
        Square.A5 + Square.A6 + Square.B6 + Square.C6 + Square.C5 + Square.C4 + Square.B4 + Square.A4,
        Square.B5 + Square.B6 + Square.C6 + Square.D6 + Square.D5 + Square.D4 + Square.C4 + Square.B4,
        Square.C5 + Square.C6 + Square.D6 + Square.E6 + Square.E5 + Square.E4 + Square.D4 + Square.C4,
        Square.D5 + Square.D6 + Square.E6 + Square.F6 + Square.F5 + Square.F4 + Square.E4 + Square.D4,
        Square.E5 + Square.E6 + Square.F6 + Square.G6 + Square.G5 + Square.G4 + Square.F4 + Square.E4,
        Square.F5 + Square.F6 + Square.G6 + Square.H6 + Square.H5 + Square.H4 + Square.G4 + Square.F4,
        Square.G5 + Square.G6 + Square.H6 + Square.H4 + Square.G4,

        // Rank 6
        Square.A7 + Square.B7 + Square.B6 + Square.B5 + Square.A5,
        Square.A6 + Square.A7 + Square.B7 + Square.C7 + Square.C6 + Square.C5 + Square.B5 + Square.A5,
        Square.B6 + Square.B7 + Square.C7 + Square.D7 + Square.D6 + Square.D5 + Square.C5 + Square.B5,
        Square.C6 + Square.C7 + Square.D7 + Square.E7 + Square.E6 + Square.E5 + Square.D5 + Square.C5,
        Square.D6 + Square.D7 + Square.E7 + Square.F7 + Square.F6 + Square.F5 + Square.E5 + Square.D5,
        Square.E6 + Square.E7 + Square.F7 + Square.G7 + Square.G6 + Square.G5 + Square.F5 + Square.E5,
        Square.F6 + Square.F7 + Square.G7 + Square.H7 + Square.H6 + Square.H5 + Square.G5 + Square.F5,
        Square.G6 + Square.G7 + Square.H7 + Square.H5 + Square.G5,

        // Rank 7
        Square.A8 + Square.B8 + Square.B7 + Square.B6 + Square.A6,
        Square.A7 + Square.A8 + Square.B8 + Square.C8 + Square.C7 + Square.C6 + Square.B6 + Square.A6,
        Square.B7 + Square.B8 + Square.C8 + Square.D8 + Square.D7 + Square.D6 + Square.C6 + Square.B6,
        Square.C7 + Square.C8 + Square.D8 + Square.E8 + Square.E7 + Square.E6 + Square.D6 + Square.C6,
        Square.D7 + Square.D8 + Square.E8 + Square.F8 + Square.F7 + Square.F6 + Square.E6 + Square.D6,
        Square.E7 + Square.E8 + Square.F8 + Square.G8 + Square.G7 + Square.G6 + Square.F6 + Square.E6,
        Square.F7 + Square.F8 + Square.G8 + Square.H8 + Square.H7 + Square.H6 + Square.G6 + Square.F6,
        Square.G7 + Square.G8 + Square.H8 + Square.H6 + Square.G6,

        // Rank 8
        Square.B8 + Square.B7 + Square.A7,
        Square.A8 + Square.C8 + Square.C7 + Square.B7 + Square.A7,
        Square.B8 + Square.D8 + Square.D7 + Square.C7 + Square.B7,
        Square.C8 + Square.E8 + Square.E7 + Square.D7 + Square.C7,
        Square.D8 + Square.F8 + Square.F7 + Square.E7 + Square.D7,
        Square.E8 + Square.G8 + Square.G7 + Square.F7 + Square.E7,
        Square.F8 + Square.H8 + Square.H7 + Square.G7 + Square.F7,
        Square.G8 + Square.H7 + Square.G7
    };

    /** All 'to' square indices a king can move to for each 'from' square index. */
    static final int[][] KING_SQUARES = {
        // Rank 1
        {A2_IDX, B2_IDX, B1_IDX},
        {A1_IDX, A2_IDX, B2_IDX, C2_IDX, C1_IDX},
        {B1_IDX, B2_IDX, C2_IDX, D2_IDX, D1_IDX},
        {C1_IDX, C2_IDX, D2_IDX, E2_IDX, E1_IDX},
        {D1_IDX, D2_IDX, E2_IDX, F2_IDX, F1_IDX},
        {E1_IDX, E2_IDX, F2_IDX, G2_IDX, G1_IDX},
        {F1_IDX, F2_IDX, G2_IDX, H2_IDX, H1_IDX},
        {G1_IDX, G2_IDX, H2_IDX},

        // Rank 2
        {A3_IDX, B3_IDX, B2_IDX, B1_IDX, A1_IDX},
        {A2_IDX, A3_IDX, B3_IDX, C3_IDX, C2_IDX, C1_IDX, B1_IDX, A1_IDX},
        {B2_IDX, B3_IDX, C3_IDX, D3_IDX, D2_IDX, D1_IDX, C1_IDX, B1_IDX},
        {C2_IDX, C3_IDX, D3_IDX, E3_IDX, E2_IDX, E1_IDX, D1_IDX, C1_IDX},
        {D2_IDX, D3_IDX, E3_IDX, F3_IDX, F2_IDX, F1_IDX, E1_IDX, D1_IDX},
        {E2_IDX, E3_IDX, F3_IDX, G3_IDX, G2_IDX, G1_IDX, F1_IDX, E1_IDX},
        {F2_IDX, F3_IDX, G3_IDX, H3_IDX, H2_IDX, H1_IDX, G1_IDX, F1_IDX},
        {G2_IDX, G3_IDX, H3_IDX, H1_IDX, G1_IDX},

        // Rank 3
        {A4_IDX, B4_IDX, B3_IDX, B2_IDX, A2_IDX},
        {A3_IDX, A4_IDX, B4_IDX, C4_IDX, C3_IDX, C2_IDX, B2_IDX, A2_IDX},
        {B3_IDX, B4_IDX, C4_IDX, D4_IDX, D3_IDX, D2_IDX, C2_IDX, B2_IDX},
        {C3_IDX, C4_IDX, D4_IDX, E4_IDX, E3_IDX, E2_IDX, D2_IDX, C2_IDX},
        {D3_IDX, D4_IDX, E4_IDX, F4_IDX, F3_IDX, F2_IDX, E2_IDX, D2_IDX},
        {E3_IDX, E4_IDX, F4_IDX, G4_IDX, G3_IDX, G2_IDX, F2_IDX, E2_IDX},
        {F3_IDX, F4_IDX, G4_IDX, H4_IDX, H3_IDX, H2_IDX, G2_IDX, F2_IDX},
        {G3_IDX, G4_IDX, H4_IDX, H2_IDX, G2_IDX},

        // Rank 4
        {A5_IDX, B5_IDX, B4_IDX, B3_IDX, A3_IDX},
        {A4_IDX, A5_IDX, B5_IDX, C5_IDX, C4_IDX, C3_IDX, B3_IDX, A3_IDX},
        {B4_IDX, B5_IDX, C5_IDX, D5_IDX, D4_IDX, D3_IDX, C3_IDX, B3_IDX},
        {C4_IDX, C5_IDX, D5_IDX, E5_IDX, E4_IDX, E3_IDX, D3_IDX, C3_IDX},
        {D4_IDX, D5_IDX, E5_IDX, F5_IDX, F4_IDX, F3_IDX, E3_IDX, D3_IDX},
        {E4_IDX, E5_IDX, F5_IDX, G5_IDX, G4_IDX, G3_IDX, F3_IDX, E3_IDX},
        {F4_IDX, F5_IDX, G5_IDX, H5_IDX, H4_IDX, H3_IDX, G3_IDX, F3_IDX},
        {G4_IDX, G5_IDX, H5_IDX, H3_IDX, G3_IDX},

        // Rank 5
        {A6_IDX, B6_IDX, B5_IDX, B4_IDX, A4_IDX},
        {A5_IDX, A6_IDX, B6_IDX, C6_IDX, C5_IDX, C4_IDX, B4_IDX, A4_IDX},
        {B5_IDX, B6_IDX, C6_IDX, D6_IDX, D5_IDX, D4_IDX, C4_IDX, B4_IDX},
        {C5_IDX, C6_IDX, D6_IDX, E6_IDX, E5_IDX, E4_IDX, D4_IDX, C4_IDX},
        {D5_IDX, D6_IDX, E6_IDX, F6_IDX, F5_IDX, F4_IDX, E4_IDX, D4_IDX},
        {E5_IDX, E6_IDX, F6_IDX, G6_IDX, G5_IDX, G4_IDX, F4_IDX, E4_IDX},
        {F5_IDX, F6_IDX, G6_IDX, H6_IDX, H5_IDX, H4_IDX, G4_IDX, F4_IDX},
        {G5_IDX, G6_IDX, H6_IDX, H4_IDX, G4_IDX},

        // Rank 6
        {A7_IDX, B7_IDX, B6_IDX, B5_IDX, A5_IDX},
        {A6_IDX, A7_IDX, B7_IDX, C7_IDX, C6_IDX, C5_IDX, B5_IDX, A5_IDX},
        {B6_IDX, B7_IDX, C7_IDX, D7_IDX, D6_IDX, D5_IDX, C5_IDX, B5_IDX},
        {C6_IDX, C7_IDX, D7_IDX, E7_IDX, E6_IDX, E5_IDX, D5_IDX, C5_IDX},
        {D6_IDX, D7_IDX, E7_IDX, F7_IDX, F6_IDX, F5_IDX, E5_IDX, D5_IDX},
        {E6_IDX, E7_IDX, F7_IDX, G7_IDX, G6_IDX, G5_IDX, F5_IDX, E5_IDX},
        {F6_IDX, F7_IDX, G7_IDX, H7_IDX, H6_IDX, H5_IDX, G5_IDX, F5_IDX},
        {G6_IDX, G7_IDX, H7_IDX, H5_IDX, G5_IDX},

        // Rank 7
        {A8_IDX, B8_IDX, B7_IDX, B6_IDX, A6_IDX},
        {A7_IDX, A8_IDX, B8_IDX, C8_IDX, C7_IDX, C6_IDX, B6_IDX, A6_IDX},
        {B7_IDX, B8_IDX, C8_IDX, D8_IDX, D7_IDX, D6_IDX, C6_IDX, B6_IDX},
        {C7_IDX, C8_IDX, D8_IDX, E8_IDX, E7_IDX, E6_IDX, D6_IDX, C6_IDX},
        {D7_IDX, D8_IDX, E8_IDX, F8_IDX, F7_IDX, F6_IDX, E6_IDX, D6_IDX},
        {E7_IDX, E8_IDX, F8_IDX, G8_IDX, G7_IDX, G6_IDX, F6_IDX, E6_IDX},
        {F7_IDX, F8_IDX, G8_IDX, H8_IDX, H7_IDX, H6_IDX, G6_IDX, F6_IDX},
        {G7_IDX, G8_IDX, H8_IDX, H6_IDX, G6_IDX},

        // Rank 8
        {B8_IDX, B7_IDX, A7_IDX},
        {A8_IDX, C8_IDX, C7_IDX, B7_IDX, A7_IDX},
        {B8_IDX, D8_IDX, D7_IDX, C7_IDX, B7_IDX},
        {C8_IDX, E8_IDX, E7_IDX, D7_IDX, C7_IDX},
        {D8_IDX, F8_IDX, F7_IDX, E7_IDX, D7_IDX},
        {E8_IDX, G8_IDX, G7_IDX, F7_IDX, E7_IDX},
        {F8_IDX, H8_IDX, H7_IDX, G7_IDX, F7_IDX},
        {G8_IDX, H7_IDX, G7_IDX}
    };

    /** All squares a knight can move to for each square. */
    static final long[] KNIGHT_MOVES = {
        // Rank 1
        Square.B3 + Square.C2,
        Square.A3 + Square.C3 + Square.D2,
        Square.A2 + Square.B3 + Square.D3 + Square.E2,
        Square.B2 + Square.C3 + Square.E3 + Square.F2,
        Square.C2 + Square.D3 + Square.F3 + Square.G2,
        Square.D2 + Square.E3 + Square.G3 + Square.H2,
        Square.E2 + Square.F3 + Square.H3,
        Square.F2 + Square.G3,

        // Rank 2
        Square.B4 + Square.C3 + Square.C1,
        Square.A4 + Square.C4 + Square.D3 + Square.D1,
        Square.A1 + Square.A3 + Square.B4 + Square.D4 + Square.E3 + Square.E1,
        Square.B1 + Square.B3 + Square.C4 + Square.E4 + Square.F3 + Square.F1,
        Square.C1 + Square.C3 + Square.D4 + Square.F4 + Square.G3 + Square.G1,
        Square.D1 + Square.D3 + Square.E4 + Square.G4 + Square.H3 + Square.H1,
        Square.E1 + Square.E3 + Square.F4 + Square.H4,
        Square.F1 + Square.F3 + Square.G4,

        // Rank 3
        Square.B5 + Square.C4 + Square.C2 + Square.B1,
        Square.A1 + Square.A5 + Square.C5 + Square.D4 + Square.D2 + Square.C1,
        Square.B1 + Square.A2 + Square.A4 + Square.B5 + Square.D5 + Square.E4 + Square.E2 + Square.D1,
        Square.C1 + Square.B2 + Square.B4 + Square.C5 + Square.E5 + Square.F4 + Square.F2 + Square.E1,
        Square.D1 + Square.C2 + Square.C4 + Square.D5 + Square.F5 + Square.G4 + Square.G2 + Square.F1,
        Square.E1 + Square.D2 + Square.D4 + Square.E5 + Square.G5 + Square.H4 + Square.H2 + Square.G1,
        Square.F1 + Square.E2 + Square.E4 + Square.F5 + Square.H5 + Square.H1,
        Square.G1 + Square.F2 + Square.F4 + Square.G5,

        // Rank 4
        Square.B6 + Square.C5 + Square.C3 + Square.B2,
        Square.A2 + Square.A6 + Square.C6 + Square.D5 + Square.D3 + Square.C2,
        Square.B2 + Square.A3 + Square.A5 + Square.B6 + Square.D6 + Square.E5 + Square.E3 + Square.D2,
        Square.C2 + Square.B3 + Square.B5 + Square.C6 + Square.E6 + Square.F5 + Square.F3 + Square.E2,
        Square.D2 + Square.C3 + Square.C5 + Square.D6 + Square.F6 + Square.G5 + Square.G3 + Square.F2,
        Square.E2 + Square.D3 + Square.D5 + Square.E6 + Square.G6 + Square.H5 + Square.H3 + Square.G2,
        Square.F2 + Square.E3 + Square.E5 + Square.F6 + Square.H6 + Square.H2,
        Square.G2 + Square.F3 + Square.F5 + Square.G6,

        // Rank 5
        Square.B7 + Square.C6 + Square.C4 + Square.B3,
        Square.A3 + Square.A7 + Square.C7 + Square.D6 + Square.D4 + Square.C3,
        Square.B3 + Square.A4 + Square.A6 + Square.B7 + Square.D7 + Square.E6 + Square.E4 + Square.D3,
        Square.C3 + Square.B4 + Square.B6 + Square.C7 + Square.E7 + Square.F6 + Square.F4 + Square.E3,
        Square.D3 + Square.C4 + Square.C6 + Square.D7 + Square.F7 + Square.G6 + Square.G4 + Square.F3,
        Square.E3 + Square.D4 + Square.D6 + Square.E7 + Square.G7 + Square.H6 + Square.H4 + Square.G3,
        Square.F3 + Square.E4 + Square.E6 + Square.F7 + Square.H7 + Square.H3,
        Square.G3 + Square.F4 + Square.F6 + Square.G7,

        // Rank 6
        Square.B8 + Square.C7 + Square.C5 + Square.B4,
        Square.A4 + Square.A8 + Square.C8 + Square.D7 + Square.D5 + Square.C4,
        Square.B4 + Square.A5 + Square.A7 + Square.B8 + Square.D8 + Square.E7 + Square.E5 + Square.D4,
        Square.C4 + Square.B5 + Square.B7 + Square.C8 + Square.E8 + Square.F7 + Square.F5 + Square.E4,
        Square.D4 + Square.C5 + Square.C7 + Square.D8 + Square.F8 + Square.G7 + Square.G5 + Square.F4,
        Square.E4 + Square.D5 + Square.D7 + Square.E8 + Square.G8 + Square.H7 + Square.H5 + Square.G4,
        Square.F4 + Square.E5 + Square.E7 + Square.F8 + Square.H8 + Square.H4,
        Square.G4 + Square.F5 + Square.F7 + Square.G8,

        // Rank 7
        Square.C8 + Square.C6 + Square.B5,
        Square.D8 + Square.D6 + Square.C5 + Square.A5,
        Square.B5 + Square.A6 + Square.A8 + Square.E8 + Square.E6 + Square.D5,
        Square.C5 + Square.B6 + Square.B8 + Square.F8 + Square.F6 + Square.E5,
        Square.D5 + Square.C6 + Square.C8 + Square.G8 + Square.G6 + Square.F5,
        Square.E5 + Square.D6 + Square.D8 + Square.H8 + Square.H6 + Square.G5,
        Square.F5 + Square.E6 + Square.E8 + Square.H5,
        Square.G5 + Square.F6 + Square.F8,

        // Rank 8
        Square.C7 + Square.B6,
        Square.A6 + Square.D7 + Square.C6,
        Square.B6 + Square.A7 + Square.E7 + Square.D6,
        Square.C6 + Square.B7 + Square.F7 + Square.E6,
        Square.D6 + Square.C7 + Square.G7 + Square.F6,
        Square.E6 + Square.D7 + Square.H7 + Square.G6,
        Square.F6 + Square.E7 + Square.H6,
        Square.G6 + Square.F7
    };

    /** All 'to' square indices a knight can move to for each 'from' square index. */
    static final int[][] KNIGHT_SQUARES = {
            // Rank 1
            {B3_IDX, C2_IDX},
            {A3_IDX, C3_IDX, D2_IDX},
            {A2_IDX, B3_IDX, D3_IDX, E2_IDX},
            {B2_IDX, C3_IDX, E3_IDX, F2_IDX},
            {C2_IDX, D3_IDX, F3_IDX, G2_IDX},
            {D2_IDX, E3_IDX, G3_IDX, H2_IDX},
            {E2_IDX, F3_IDX, H3_IDX},
            {F2_IDX, G3_IDX},

            // Rank 2
            {B4_IDX, C3_IDX, C1_IDX},
            {A4_IDX, C4_IDX, D3_IDX, D1_IDX},
            {A1_IDX, A3_IDX, B4_IDX, D4_IDX, E3_IDX, E1_IDX},
            {B1_IDX, B3_IDX, C4_IDX, E4_IDX, F3_IDX, F1_IDX},
            {C1_IDX, C3_IDX, D4_IDX, F4_IDX, G3_IDX, G1_IDX},
            {D1_IDX, D3_IDX, E4_IDX, G4_IDX, H3_IDX, H1_IDX},
            {E1_IDX, E3_IDX, F4_IDX, H4_IDX},
            {F1_IDX, F3_IDX, G4_IDX},

            // Rank 3
            {B5_IDX, C4_IDX, C2_IDX, B1_IDX},
            {A1_IDX, A5_IDX, C5_IDX, D4_IDX, D2_IDX, C1_IDX},
            {B1_IDX, A2_IDX, A4_IDX, B5_IDX, D5_IDX, E4_IDX, E2_IDX, D1_IDX},
            {C1_IDX, B2_IDX, B4_IDX, C5_IDX, E5_IDX, F4_IDX, F2_IDX, E1_IDX},
            {D1_IDX, C2_IDX, C4_IDX, D5_IDX, F5_IDX, G4_IDX, G2_IDX, F1_IDX},
            {E1_IDX, D2_IDX, D4_IDX, E5_IDX, G5_IDX, H4_IDX, H2_IDX, G1_IDX},
            {F1_IDX, E2_IDX, E4_IDX, F5_IDX, H5_IDX, H1_IDX},
            {G1_IDX, F2_IDX, F4_IDX, G5_IDX},

            // Rank 4
            {B6_IDX, C5_IDX, C3_IDX, B2_IDX},
            {A2_IDX, A6_IDX, C6_IDX, D5_IDX, D3_IDX, C2_IDX},
            {B2_IDX, A3_IDX, A5_IDX, B6_IDX, D6_IDX, E5_IDX, E3_IDX, D2_IDX},
            {C2_IDX, B3_IDX, B5_IDX, C6_IDX, E6_IDX, F5_IDX, F3_IDX, E2_IDX},
            {D2_IDX, C3_IDX, C5_IDX, D6_IDX, F6_IDX, G5_IDX, G3_IDX, F2_IDX},
            {E2_IDX, D3_IDX, D5_IDX, E6_IDX, G6_IDX, H5_IDX, H3_IDX, G2_IDX},
            {F2_IDX, E3_IDX, E5_IDX, F6_IDX, H6_IDX, H2_IDX},
            {G2_IDX, F3_IDX, F5_IDX, G6_IDX},

            // Rank 5
            {B7_IDX, C6_IDX, C4_IDX, B3_IDX},
            {A3_IDX, A7_IDX, C7_IDX, D6_IDX, D4_IDX, C3_IDX},
            {B3_IDX, A4_IDX, A6_IDX, B7_IDX, D7_IDX, E6_IDX, E4_IDX, D3_IDX},
            {C3_IDX, B4_IDX, B6_IDX, C7_IDX, E7_IDX, F6_IDX, F4_IDX, E3_IDX},
            {D3_IDX, C4_IDX, C6_IDX, D7_IDX, F7_IDX, G6_IDX, G4_IDX, F3_IDX},
            {E3_IDX, D4_IDX, D6_IDX, E7_IDX, G7_IDX, H6_IDX, H4_IDX, G3_IDX},
            {F3_IDX, E4_IDX, E6_IDX, F7_IDX, H7_IDX, H3_IDX},
            {G3_IDX, F4_IDX, F6_IDX, G7_IDX},

            // Rank 6
            {B8_IDX, C7_IDX, C5_IDX, B4_IDX},
            {A4_IDX, A8_IDX, C8_IDX, D7_IDX, D5_IDX, C4_IDX},
            {B4_IDX, A5_IDX, A7_IDX, B8_IDX, D8_IDX, E7_IDX, E5_IDX, D4_IDX},
            {C4_IDX, B5_IDX, B7_IDX, C8_IDX, E8_IDX, F7_IDX, F5_IDX, E4_IDX},
            {D4_IDX, C5_IDX, C7_IDX, D8_IDX, F8_IDX, G7_IDX, G5_IDX, F4_IDX},
            {E4_IDX, D5_IDX, D7_IDX, E8_IDX, G8_IDX, H7_IDX, H5_IDX, G4_IDX},
            {F4_IDX, E5_IDX, E7_IDX, F8_IDX, H8_IDX, H4_IDX},
            {G4_IDX, F5_IDX, F7_IDX, G8_IDX},

            // Rank 7
            {C8_IDX, C6_IDX, B5_IDX},
            {D8_IDX, D6_IDX, C5_IDX, A5_IDX},
            {B5_IDX, A6_IDX, A8_IDX, E8_IDX, E6_IDX, D5_IDX},
            {C5_IDX, B6_IDX, B8_IDX, F8_IDX, F6_IDX, E5_IDX},
            {D5_IDX, C6_IDX, C8_IDX, G8_IDX, G6_IDX, F5_IDX},
            {E5_IDX, D6_IDX, D8_IDX, H8_IDX, H6_IDX, G5_IDX},
            {F5_IDX, E6_IDX, E8_IDX, H5_IDX},
            {G5_IDX, F6_IDX, F8_IDX},

            // Rank 8
            {C7_IDX, B6_IDX},
            {A6_IDX, D7_IDX, C6_IDX},
            {B6_IDX, A7_IDX, E7_IDX, D6_IDX},
            {C6_IDX, B7_IDX, F7_IDX, E6_IDX},
            {D6_IDX, C7_IDX, G7_IDX, F6_IDX},
            {E6_IDX, D7_IDX, H7_IDX, G6_IDX},
            {F6_IDX, E7_IDX, H6_IDX},
            {G6_IDX, F7_IDX}
    };
}
