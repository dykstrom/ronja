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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.dykstrom.ronja.common.model.Square.A1_IDX;
import static se.dykstrom.ronja.common.model.Square.A8_IDX;
import static se.dykstrom.ronja.common.model.Square.B1_IDX;
import static se.dykstrom.ronja.common.model.Square.B8_IDX;
import static se.dykstrom.ronja.common.model.Square.C7_IDX;
import static se.dykstrom.ronja.common.model.Square.E3_IDX;
import static se.dykstrom.ronja.common.model.Square.F4_IDX;
import static se.dykstrom.ronja.common.model.Square.F5_IDX;
import static se.dykstrom.ronja.common.model.Square.H1_IDX;
import static se.dykstrom.ronja.common.model.Square.H7_IDX;
import static se.dykstrom.ronja.common.model.Square.H8_IDX;
import static se.dykstrom.ronja.common.model.Square.SQUARE_IDS;
import static se.dykstrom.ronja.common.model.Square.SQUARE_INDICES;
import static se.dykstrom.ronja.common.model.Square.bitboardToIds;
import static se.dykstrom.ronja.common.model.Square.bitboardToIndices;

/**
 * This class is for testing class {@code Square} using JUnit.
 *
 * @author Johan Dykstrom
 * @see Square
 */
public class SquareTest extends AbstractTestCase {

    @Test
    public void shouldConvertBitboardToIds() {
        assertSquareIds(new String[]{}, bitboardToIds(0L));
        assertSquareIds(new String[]{"a1"}, bitboardToIds(Square.A1));
        assertSquareIds(new String[]{"a1", "a2"}, bitboardToIds(Square.A1 | Square.A2));
        assertSquareIds(new String[]{"a1", "a8", "h1", "h8"}, bitboardToIds(Square.A1 | Square.A8 | Square.H1 | Square.H8));
        assertSquareIds(new String[]{"d4", "e4", "d5", "e5"}, bitboardToIds(Square.D4 | Square.E4 | Square.D5 | Square.E5));
    }

    @Test
    public void testBitboardToIndices() {
        assertSquareIndices(new String[]{},
                bitboardToIndices(0L));
        assertSquareIndices(new String[]{"a1"},
                bitboardToIndices(Square.A1));
        assertSquareIndices(new String[]{"a1", "b1", "b8", "e4"},
                bitboardToIndices(Square.A1 | Square.B1 | Square.B8 | Square.E4));
        assertSquareIndices(new String[]{"a1", "a8", "h1", "h8"},
                bitboardToIndices(Square.A1 | Square.A8 | Square.H1 | Square.H8));
        assertSquareIndices(new String[]{"g2", "c7", "h8"},
                bitboardToIndices(Square.G2 | Square.C7 | Square.H8));
        assertSquareIndices(new String[]{"g2", "c7", "h8", "b2", "c2", "f5"},
                bitboardToIndices(Square.G2 | Square.C7 | Square.H8 | Square.B2 | Square.C2 | Square.F5));
        assertSquareIndices(new String[]{"a1", "a2", "a3", "d1", "d2", "d3", "h8", "h7", "h6", "e8", "e7", "e6"},
                bitboardToIndices(Square.A1 | Square.A2 | Square.A3 | Square.D1 | Square.D2 | Square.D3 |
                                  Square.H8 | Square.H7 | Square.H6 | Square.E8 | Square.E7 | Square.E6));
    }

    @Test
	public void testIdToIndex() {
		// Square indices range from 0 to 63
		for (int index = 0; index < 64; index++) {
			assertEquals(index, Square.idToIndex(Square.indexToId(index)), "Index: " + index + ", ");
		}

        // Check a subset of the squares
        assertEquals(A1_IDX, Square.idToIndex(Square.A1));
        assertEquals(B1_IDX, Square.idToIndex(Square.B1));
        assertEquals(H1_IDX, Square.idToIndex(Square.H1));
        assertEquals(F4_IDX, Square.idToIndex(Square.F4));
        assertEquals(C7_IDX, Square.idToIndex(Square.C7));
        assertEquals(A8_IDX, Square.idToIndex(Square.A8));
        assertEquals(H8_IDX, Square.idToIndex(Square.H8));
	}

    @Test
    public void testIdToName() {
        // Square indices range from 0 to 63
        for (int index = 0; index < 64; index++) {
            long id = Square.indexToId(index);
            assertEquals(id, Square.nameToId(Square.idToName(id)), "Index: " + index + ", ");
        }

        // Check a subset of the squares
        assertEquals("a1", Square.idToName(Square.A1));
        assertEquals("a8", Square.idToName(Square.A8));
        assertEquals("b1", Square.idToName(Square.B1));
        assertEquals("b8", Square.idToName(Square.B8));
        assertEquals("h1", Square.idToName(Square.H1));
        assertEquals("h7", Square.idToName(Square.H7));
        assertEquals("h8", Square.idToName(Square.H8));
    }

    @Test
    public void testIndexToName() {
        // Square indices range from 0 to 63
        for (int index = 0; index < 64; index++) {
            assertEquals(index, Square.nameToIndex(Square.indexToName(index)), "Index: " + index + ", ");
        }

        // Check a subset of the squares
        assertEquals("a1", Square.indexToName(A1_IDX));
        assertEquals("a8", Square.indexToName(A8_IDX));
        assertEquals("b1", Square.indexToName(B1_IDX));
        assertEquals("b8", Square.indexToName(B8_IDX));
        assertEquals("e3", Square.indexToName(E3_IDX));
        assertEquals("f5", Square.indexToName(F5_IDX));
        assertEquals("h1", Square.indexToName(H1_IDX));
        assertEquals("h7", Square.indexToName(H7_IDX));
        assertEquals("h8", Square.indexToName(H8_IDX));
    }

    @Test
	public void testSquareNamesUnique() {
		Set<String> squares = new HashSet<>();

		for (int index = 0; index < 64; index++) {
			squares.add(Square.indexToName(index));
		}
		// Check that there are 64 squares
		assertEquals(64, squares.size());
	}

    @Test
    public void testEast() {
        Assertions.assertEquals(Square.B1, Square.east(Square.A1));
        Assertions.assertEquals(Square.B2, Square.east(Square.A2));
        Assertions.assertEquals(Square.B3, Square.east(Square.A3));
        Assertions.assertEquals(Square.B4, Square.east(Square.A4));
        Assertions.assertEquals(Square.B5, Square.east(Square.A5));
        Assertions.assertEquals(Square.B6, Square.east(Square.A6));
        Assertions.assertEquals(Square.B7, Square.east(Square.A7));
        Assertions.assertEquals(Square.B8, Square.east(Square.A8));

        Assertions.assertEquals(Square.H1, Square.east(Square.G1));
        Assertions.assertEquals(Square.H2, Square.east(Square.G2));
        Assertions.assertEquals(Square.H3, Square.east(Square.G3));
        Assertions.assertEquals(Square.H4, Square.east(Square.G4));
        Assertions.assertEquals(Square.H5, Square.east(Square.G5));
        Assertions.assertEquals(Square.H6, Square.east(Square.G6));
        Assertions.assertEquals(Square.H7, Square.east(Square.G7));
        Assertions.assertEquals(Square.H8, Square.east(Square.G8));
    }

    @Test
    public void testWest() {
        Assertions.assertEquals(Square.G1, Square.west(Square.H1));
        Assertions.assertEquals(Square.G2, Square.west(Square.H2));
        Assertions.assertEquals(Square.G3, Square.west(Square.H3));
        Assertions.assertEquals(Square.G4, Square.west(Square.H4));
        Assertions.assertEquals(Square.G5, Square.west(Square.H5));
        Assertions.assertEquals(Square.G6, Square.west(Square.H6));
        Assertions.assertEquals(Square.G7, Square.west(Square.H7));
        Assertions.assertEquals(Square.G8, Square.west(Square.H8));

        Assertions.assertEquals(Square.A1, Square.west(Square.B1));
        Assertions.assertEquals(Square.A2, Square.west(Square.B2));
        Assertions.assertEquals(Square.A3, Square.west(Square.B3));
        Assertions.assertEquals(Square.A4, Square.west(Square.B4));
        Assertions.assertEquals(Square.A5, Square.west(Square.B5));
        Assertions.assertEquals(Square.A6, Square.west(Square.B6));
        Assertions.assertEquals(Square.A7, Square.west(Square.B7));
        Assertions.assertEquals(Square.A8, Square.west(Square.B8));
    }

    @Test
    public void testNorth() {
        Assertions.assertEquals(Square.A2, Square.north(Square.A1));
        Assertions.assertEquals(Square.B2, Square.north(Square.B1));
        Assertions.assertEquals(Square.C2, Square.north(Square.C1));
        Assertions.assertEquals(Square.D2, Square.north(Square.D1));
        Assertions.assertEquals(Square.E2, Square.north(Square.E1));
        Assertions.assertEquals(Square.F2, Square.north(Square.F1));
        Assertions.assertEquals(Square.G2, Square.north(Square.G1));
        Assertions.assertEquals(Square.H2, Square.north(Square.H1));

        Assertions.assertEquals(Square.A8, Square.north(Square.A7));
        Assertions.assertEquals(Square.B8, Square.north(Square.B7));
        Assertions.assertEquals(Square.C8, Square.north(Square.C7));
        Assertions.assertEquals(Square.D8, Square.north(Square.D7));
        Assertions.assertEquals(Square.E8, Square.north(Square.E7));
        Assertions.assertEquals(Square.F8, Square.north(Square.F7));
        Assertions.assertEquals(Square.G8, Square.north(Square.G7));
        Assertions.assertEquals(Square.H8, Square.north(Square.H7));
    }

    @Test
    public void testSouth() {
        Assertions.assertEquals(Square.A1, Square.south(Square.A2));
        Assertions.assertEquals(Square.B1, Square.south(Square.B2));
        Assertions.assertEquals(Square.C1, Square.south(Square.C2));
        Assertions.assertEquals(Square.D1, Square.south(Square.D2));
        Assertions.assertEquals(Square.E1, Square.south(Square.E2));
        Assertions.assertEquals(Square.F1, Square.south(Square.F2));
        Assertions.assertEquals(Square.G1, Square.south(Square.G2));
        Assertions.assertEquals(Square.H1, Square.south(Square.H2));

        Assertions.assertEquals(Square.A7, Square.south(Square.A8));
        Assertions.assertEquals(Square.B7, Square.south(Square.B8));
        Assertions.assertEquals(Square.C7, Square.south(Square.C8));
        Assertions.assertEquals(Square.D7, Square.south(Square.D8));
        Assertions.assertEquals(Square.E7, Square.south(Square.E8));
        Assertions.assertEquals(Square.F7, Square.south(Square.F8));
        Assertions.assertEquals(Square.G7, Square.south(Square.G8));
        Assertions.assertEquals(Square.H7, Square.south(Square.H8));
    }

    @Test
    public void testNorthEast() {
        Assertions.assertEquals(Square.B2, Square.northEast(Square.A1));
        Assertions.assertEquals(Square.B3, Square.northEast(Square.A2));
        Assertions.assertEquals(Square.B4, Square.northEast(Square.A3));
        Assertions.assertEquals(Square.B5, Square.northEast(Square.A4));
        Assertions.assertEquals(Square.B6, Square.northEast(Square.A5));
        Assertions.assertEquals(Square.B7, Square.northEast(Square.A6));
        Assertions.assertEquals(Square.B8, Square.northEast(Square.A7));

        Assertions.assertEquals(Square.H2, Square.northEast(Square.G1));
        Assertions.assertEquals(Square.H3, Square.northEast(Square.G2));
        Assertions.assertEquals(Square.H4, Square.northEast(Square.G3));
        Assertions.assertEquals(Square.H5, Square.northEast(Square.G4));
        Assertions.assertEquals(Square.H6, Square.northEast(Square.G5));
        Assertions.assertEquals(Square.H7, Square.northEast(Square.G6));
        Assertions.assertEquals(Square.H8, Square.northEast(Square.G7));

        Assertions.assertEquals(Square.B2, Square.northEast(Square.A1));
        Assertions.assertEquals(Square.C2, Square.northEast(Square.B1));
        Assertions.assertEquals(Square.D2, Square.northEast(Square.C1));
        Assertions.assertEquals(Square.E2, Square.northEast(Square.D1));
        Assertions.assertEquals(Square.F2, Square.northEast(Square.E1));
        Assertions.assertEquals(Square.G2, Square.northEast(Square.F1));
        Assertions.assertEquals(Square.H2, Square.northEast(Square.G1));

        Assertions.assertEquals(Square.B8, Square.northEast(Square.A7));
        Assertions.assertEquals(Square.C8, Square.northEast(Square.B7));
        Assertions.assertEquals(Square.D8, Square.northEast(Square.C7));
        Assertions.assertEquals(Square.E8, Square.northEast(Square.D7));
        Assertions.assertEquals(Square.F8, Square.northEast(Square.E7));
        Assertions.assertEquals(Square.G8, Square.northEast(Square.F7));
        Assertions.assertEquals(Square.H8, Square.northEast(Square.G7));
    }

    @Test
    public void testNorthWest() {
        Assertions.assertEquals(Square.A2, Square.northWest(Square.B1));
        Assertions.assertEquals(Square.A3, Square.northWest(Square.B2));
        Assertions.assertEquals(Square.A4, Square.northWest(Square.B3));
        Assertions.assertEquals(Square.A5, Square.northWest(Square.B4));
        Assertions.assertEquals(Square.A6, Square.northWest(Square.B5));
        Assertions.assertEquals(Square.A7, Square.northWest(Square.B6));
        Assertions.assertEquals(Square.A8, Square.northWest(Square.B7));

        Assertions.assertEquals(Square.G2, Square.northWest(Square.H1));
        Assertions.assertEquals(Square.G3, Square.northWest(Square.H2));
        Assertions.assertEquals(Square.G4, Square.northWest(Square.H3));
        Assertions.assertEquals(Square.G5, Square.northWest(Square.H4));
        Assertions.assertEquals(Square.G6, Square.northWest(Square.H5));
        Assertions.assertEquals(Square.G7, Square.northWest(Square.H6));
        Assertions.assertEquals(Square.G8, Square.northWest(Square.H7));

        Assertions.assertEquals(Square.A2, Square.northWest(Square.B1));
        Assertions.assertEquals(Square.B2, Square.northWest(Square.C1));
        Assertions.assertEquals(Square.C2, Square.northWest(Square.D1));
        Assertions.assertEquals(Square.D2, Square.northWest(Square.E1));
        Assertions.assertEquals(Square.E2, Square.northWest(Square.F1));
        Assertions.assertEquals(Square.F2, Square.northWest(Square.G1));
        Assertions.assertEquals(Square.G2, Square.northWest(Square.H1));

        Assertions.assertEquals(Square.A8, Square.northWest(Square.B7));
        Assertions.assertEquals(Square.B8, Square.northWest(Square.C7));
        Assertions.assertEquals(Square.C8, Square.northWest(Square.D7));
        Assertions.assertEquals(Square.D8, Square.northWest(Square.E7));
        Assertions.assertEquals(Square.E8, Square.northWest(Square.F7));
        Assertions.assertEquals(Square.F8, Square.northWest(Square.G7));
        Assertions.assertEquals(Square.G8, Square.northWest(Square.H7));
    }

    @Test
    public void testSouthEast() {
        Assertions.assertEquals(Square.B1, Square.southEast(Square.A2));
        Assertions.assertEquals(Square.B2, Square.southEast(Square.A3));
        Assertions.assertEquals(Square.B3, Square.southEast(Square.A4));
        Assertions.assertEquals(Square.B4, Square.southEast(Square.A5));
        Assertions.assertEquals(Square.B5, Square.southEast(Square.A6));
        Assertions.assertEquals(Square.B6, Square.southEast(Square.A7));
        Assertions.assertEquals(Square.B7, Square.southEast(Square.A8));

        Assertions.assertEquals(Square.H1, Square.southEast(Square.G2));
        Assertions.assertEquals(Square.H2, Square.southEast(Square.G3));
        Assertions.assertEquals(Square.H3, Square.southEast(Square.G4));
        Assertions.assertEquals(Square.H4, Square.southEast(Square.G5));
        Assertions.assertEquals(Square.H5, Square.southEast(Square.G6));
        Assertions.assertEquals(Square.H6, Square.southEast(Square.G7));
        Assertions.assertEquals(Square.H7, Square.southEast(Square.G8));

        Assertions.assertEquals(Square.B1, Square.southEast(Square.A2));
        Assertions.assertEquals(Square.C1, Square.southEast(Square.B2));
        Assertions.assertEquals(Square.D1, Square.southEast(Square.C2));
        Assertions.assertEquals(Square.E1, Square.southEast(Square.D2));
        Assertions.assertEquals(Square.F1, Square.southEast(Square.E2));
        Assertions.assertEquals(Square.G1, Square.southEast(Square.F2));
        Assertions.assertEquals(Square.H1, Square.southEast(Square.G2));

        Assertions.assertEquals(Square.B7, Square.southEast(Square.A8));
        Assertions.assertEquals(Square.C7, Square.southEast(Square.B8));
        Assertions.assertEquals(Square.D7, Square.southEast(Square.C8));
        Assertions.assertEquals(Square.E7, Square.southEast(Square.D8));
        Assertions.assertEquals(Square.F7, Square.southEast(Square.E8));
        Assertions.assertEquals(Square.G7, Square.southEast(Square.F8));
        Assertions.assertEquals(Square.H7, Square.southEast(Square.G8));
    }

    @Test
    public void testSouthWest() {
        Assertions.assertEquals(Square.A1, Square.southWest(Square.B2));
        Assertions.assertEquals(Square.A2, Square.southWest(Square.B3));
        Assertions.assertEquals(Square.A3, Square.southWest(Square.B4));
        Assertions.assertEquals(Square.A4, Square.southWest(Square.B5));
        Assertions.assertEquals(Square.A5, Square.southWest(Square.B6));
        Assertions.assertEquals(Square.A6, Square.southWest(Square.B7));
        Assertions.assertEquals(Square.A7, Square.southWest(Square.B8));

        Assertions.assertEquals(Square.G1, Square.southWest(Square.H2));
        Assertions.assertEquals(Square.G2, Square.southWest(Square.H3));
        Assertions.assertEquals(Square.G3, Square.southWest(Square.H4));
        Assertions.assertEquals(Square.G4, Square.southWest(Square.H5));
        Assertions.assertEquals(Square.G5, Square.southWest(Square.H6));
        Assertions.assertEquals(Square.G6, Square.southWest(Square.H7));
        Assertions.assertEquals(Square.G7, Square.southWest(Square.H8));

        Assertions.assertEquals(Square.A1, Square.southWest(Square.B2));
        Assertions.assertEquals(Square.B1, Square.southWest(Square.C2));
        Assertions.assertEquals(Square.C1, Square.southWest(Square.D2));
        Assertions.assertEquals(Square.D1, Square.southWest(Square.E2));
        Assertions.assertEquals(Square.E1, Square.southWest(Square.F2));
        Assertions.assertEquals(Square.F1, Square.southWest(Square.G2));
        Assertions.assertEquals(Square.G1, Square.southWest(Square.H2));

        Assertions.assertEquals(Square.A7, Square.southWest(Square.B8));
        Assertions.assertEquals(Square.B7, Square.southWest(Square.C8));
        Assertions.assertEquals(Square.C7, Square.southWest(Square.D8));
        Assertions.assertEquals(Square.D7, Square.southWest(Square.E8));
        Assertions.assertEquals(Square.E7, Square.southWest(Square.F8));
        Assertions.assertEquals(Square.F7, Square.southWest(Square.G8));
        Assertions.assertEquals(Square.G7, Square.southWest(Square.H8));
    }

    @Test
    public void testNorthNorth() {
        Assertions.assertEquals(Square.A4, Square.northNorth(Square.A2));
        Assertions.assertEquals(Square.B4, Square.northNorth(Square.B2));
        Assertions.assertEquals(Square.C4, Square.northNorth(Square.C2));
        Assertions.assertEquals(Square.D4, Square.northNorth(Square.D2));
        Assertions.assertEquals(Square.E4, Square.northNorth(Square.E2));
        Assertions.assertEquals(Square.F4, Square.northNorth(Square.F2));
        Assertions.assertEquals(Square.G4, Square.northNorth(Square.G2));
        Assertions.assertEquals(Square.H4, Square.northNorth(Square.H2));
    }

    @Test
    public void testSouthSouth() {
        Assertions.assertEquals(Square.A5, Square.southSouth(Square.A7));
        Assertions.assertEquals(Square.B5, Square.southSouth(Square.B7));
        Assertions.assertEquals(Square.C5, Square.southSouth(Square.C7));
        Assertions.assertEquals(Square.D5, Square.southSouth(Square.D7));
        Assertions.assertEquals(Square.E5, Square.southSouth(Square.E7));
        Assertions.assertEquals(Square.F5, Square.southSouth(Square.F7));
        Assertions.assertEquals(Square.G5, Square.southSouth(Square.G7));
        Assertions.assertEquals(Square.H5, Square.southSouth(Square.H7));
    }

    // -----------------------------------------------------------------------

    /**
     * Asserts that the squares defined by the names in {@code expectedNames} are the same as
     * the squares defined by the indices in {@link Square#SQUARE_INDICES}.
     *
     * @param expectedNames An array of the expected square names, e.g. ["a1", "e4"].
     * @param numberOfIndices The number of indices in SQUARE_INDICES to check.
     */
    private void assertSquareIndices(String[] expectedNames, int numberOfIndices) {
        assertEquals(expectedNames.length, numberOfIndices);
        int[] expected = Arrays.stream(expectedNames).mapToInt(Square::nameToIndex).sorted().toArray();
        int[] actual = Arrays.stream(Arrays.copyOf(SQUARE_INDICES, numberOfIndices)).sorted().toArray();
        assertArrayEquals(expected, actual);
    }

    /**
     * Asserts that the squares defined by the names in {@code expectedNames} are the same as
     * the squares defined by the IDs in {@link Square#SQUARE_IDS}.
     *
     * @param expectedNames An array of the expected square names, e.g. ["a1", "e4"].
     * @param numberOfIds The number of IDs in SQUARE_IDS to check.
     */
    private static void assertSquareIds(String[] expectedNames, int numberOfIds) {
        assertEquals(expectedNames.length, numberOfIds);
        long[] expected = Arrays.stream(expectedNames).mapToLong(Square::nameToId).sorted().toArray();
        long[] actual = Arrays.stream(Arrays.copyOf(SQUARE_IDS, numberOfIds)).sorted().toArray();
        assertArrayEquals(expected, actual);
    }
}
