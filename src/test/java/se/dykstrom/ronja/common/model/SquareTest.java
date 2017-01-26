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

import org.junit.Assert;
import org.junit.Test;
import se.dykstrom.ronja.test.AbstractTestCase;

import java.util.*;

import static org.junit.Assert.*;

/**
 * This class is for testing class {@code Square} using JUnit.
 *
 * @author Johan Dykstrom
 * @see Square
 */
public class SquareTest extends AbstractTestCase {

    /**
     * Tests the bitboardToIds method.
     */
    @Test
    public void testBitboardToIds() {
        assertSquareIds(new String[]{}, Square.bitboardToIds(0L));
        assertSquareIds(new String[]{"a1"}, Square.bitboardToIds(Square.A1));
        assertSquareIds(new String[]{"a1", "a2"}, Square.bitboardToIds(Square.A1 | Square.A2));
        assertSquareIds(new String[]{"a1", "a8", "h1", "h8"}, Square.bitboardToIds(Square.A1 | Square.A8 | Square.H1 | Square.H8));
        assertSquareIds(new String[]{"d4", "e4", "d5", "e5"}, Square.bitboardToIds(Square.D4 | Square.E4 | Square.D5 | Square.E5));
    }

    /**
     * Tests the bitboardToIndices method.
     */
    @Test
    public void testBitboardToIndices() {
        assertSquareIndices(new String[]{}, Square.bitboardToIndices(0L));
        assertSquareIndices(new String[]{"a1"}, Square.bitboardToIndices(Square.A1));
        assertSquareIndices(new String[]{"a1", "b1", "b8", "e4"}, Square.bitboardToIndices(Square.A1 | Square.B1 | Square.B8 | Square.E4));
        assertSquareIndices(new String[]{"a1", "a8", "h1", "h8"}, Square.bitboardToIndices(Square.A1 | Square.A8 | Square.H1 | Square.H8));
        assertSquareIndices(new String[]{"g2", "c7", "h8"}, Square.bitboardToIndices(Square.G2 | Square.C7 | Square.H8));
        assertSquareIndices(new String[]{"g2", "c7", "h8", "b2", "c2", "f5"},
                Square.bitboardToIndices(Square.G2 | Square.C7 | Square.H8 | Square.B2 | Square.C2 | Square.F5));
    }

    @Test
	public void testIdToIndex() {
		// Square indices range from 0 to 63
		for (int index = 0; index < 64; index++) {
			assertEquals("Index: " + index + ", ", index, Square.idToIndex(Square.indexToId(index)));
		}

        // Check a subset of the squares
        assertEquals(0, Square.idToIndex(Square.A1));
        assertEquals(1, Square.idToIndex(Square.B1));
        assertEquals(7, Square.idToIndex(Square.H1));
        assertEquals(56, Square.idToIndex(Square.A8));
        assertEquals(63, Square.idToIndex(Square.H8));
	}

    @Test
    public void testIdToName() {
        // Square indices range from 0 to 63
        for (int index = 0; index < 64; index++) {
            long id = Square.indexToId(index);
            assertEquals("Index: " + index + ", ", id, Square.nameToId(Square.idToName(id)));
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
            assertEquals("Index: " + index + ", ", index, Square.nameToIndex(Square.indexToName(index)));
        }

        // Check a subset of the squares
        assertEquals("a1", Square.indexToName(0));
        assertEquals("a8", Square.indexToName(56));
        assertEquals("b1", Square.indexToName(1));
        assertEquals("b8", Square.indexToName(57));
        assertEquals("h1", Square.indexToName(7));
        assertEquals("h7", Square.indexToName(55));
        assertEquals("h8", Square.indexToName(63));
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
        Assert.assertEquals(Square.B1, Square.east(Square.A1));
        Assert.assertEquals(Square.B2, Square.east(Square.A2));
        Assert.assertEquals(Square.B3, Square.east(Square.A3));
        Assert.assertEquals(Square.B4, Square.east(Square.A4));
        Assert.assertEquals(Square.B5, Square.east(Square.A5));
        Assert.assertEquals(Square.B6, Square.east(Square.A6));
        Assert.assertEquals(Square.B7, Square.east(Square.A7));
        Assert.assertEquals(Square.B8, Square.east(Square.A8));

        Assert.assertEquals(Square.H1, Square.east(Square.G1));
        Assert.assertEquals(Square.H2, Square.east(Square.G2));
        Assert.assertEquals(Square.H3, Square.east(Square.G3));
        Assert.assertEquals(Square.H4, Square.east(Square.G4));
        Assert.assertEquals(Square.H5, Square.east(Square.G5));
        Assert.assertEquals(Square.H6, Square.east(Square.G6));
        Assert.assertEquals(Square.H7, Square.east(Square.G7));
        Assert.assertEquals(Square.H8, Square.east(Square.G8));
    }

    @Test
    public void testWest() {
        Assert.assertEquals(Square.G1, Square.west(Square.H1));
        Assert.assertEquals(Square.G2, Square.west(Square.H2));
        Assert.assertEquals(Square.G3, Square.west(Square.H3));
        Assert.assertEquals(Square.G4, Square.west(Square.H4));
        Assert.assertEquals(Square.G5, Square.west(Square.H5));
        Assert.assertEquals(Square.G6, Square.west(Square.H6));
        Assert.assertEquals(Square.G7, Square.west(Square.H7));
        Assert.assertEquals(Square.G8, Square.west(Square.H8));

        Assert.assertEquals(Square.A1, Square.west(Square.B1));
        Assert.assertEquals(Square.A2, Square.west(Square.B2));
        Assert.assertEquals(Square.A3, Square.west(Square.B3));
        Assert.assertEquals(Square.A4, Square.west(Square.B4));
        Assert.assertEquals(Square.A5, Square.west(Square.B5));
        Assert.assertEquals(Square.A6, Square.west(Square.B6));
        Assert.assertEquals(Square.A7, Square.west(Square.B7));
        Assert.assertEquals(Square.A8, Square.west(Square.B8));
    }

    @Test
    public void testNorth() {
        Assert.assertEquals(Square.A2, Square.north(Square.A1));
        Assert.assertEquals(Square.B2, Square.north(Square.B1));
        Assert.assertEquals(Square.C2, Square.north(Square.C1));
        Assert.assertEquals(Square.D2, Square.north(Square.D1));
        Assert.assertEquals(Square.E2, Square.north(Square.E1));
        Assert.assertEquals(Square.F2, Square.north(Square.F1));
        Assert.assertEquals(Square.G2, Square.north(Square.G1));
        Assert.assertEquals(Square.H2, Square.north(Square.H1));

        Assert.assertEquals(Square.A8, Square.north(Square.A7));
        Assert.assertEquals(Square.B8, Square.north(Square.B7));
        Assert.assertEquals(Square.C8, Square.north(Square.C7));
        Assert.assertEquals(Square.D8, Square.north(Square.D7));
        Assert.assertEquals(Square.E8, Square.north(Square.E7));
        Assert.assertEquals(Square.F8, Square.north(Square.F7));
        Assert.assertEquals(Square.G8, Square.north(Square.G7));
        Assert.assertEquals(Square.H8, Square.north(Square.H7));
    }

    @Test
    public void testSouth() {
        Assert.assertEquals(Square.A1, Square.south(Square.A2));
        Assert.assertEquals(Square.B1, Square.south(Square.B2));
        Assert.assertEquals(Square.C1, Square.south(Square.C2));
        Assert.assertEquals(Square.D1, Square.south(Square.D2));
        Assert.assertEquals(Square.E1, Square.south(Square.E2));
        Assert.assertEquals(Square.F1, Square.south(Square.F2));
        Assert.assertEquals(Square.G1, Square.south(Square.G2));
        Assert.assertEquals(Square.H1, Square.south(Square.H2));

        Assert.assertEquals(Square.A7, Square.south(Square.A8));
        Assert.assertEquals(Square.B7, Square.south(Square.B8));
        Assert.assertEquals(Square.C7, Square.south(Square.C8));
        Assert.assertEquals(Square.D7, Square.south(Square.D8));
        Assert.assertEquals(Square.E7, Square.south(Square.E8));
        Assert.assertEquals(Square.F7, Square.south(Square.F8));
        Assert.assertEquals(Square.G7, Square.south(Square.G8));
        Assert.assertEquals(Square.H7, Square.south(Square.H8));
    }

    @Test
    public void testNorthEast() {
        Assert.assertEquals(Square.B2, Square.northEast(Square.A1));
        Assert.assertEquals(Square.B3, Square.northEast(Square.A2));
        Assert.assertEquals(Square.B4, Square.northEast(Square.A3));
        Assert.assertEquals(Square.B5, Square.northEast(Square.A4));
        Assert.assertEquals(Square.B6, Square.northEast(Square.A5));
        Assert.assertEquals(Square.B7, Square.northEast(Square.A6));
        Assert.assertEquals(Square.B8, Square.northEast(Square.A7));

        Assert.assertEquals(Square.H2, Square.northEast(Square.G1));
        Assert.assertEquals(Square.H3, Square.northEast(Square.G2));
        Assert.assertEquals(Square.H4, Square.northEast(Square.G3));
        Assert.assertEquals(Square.H5, Square.northEast(Square.G4));
        Assert.assertEquals(Square.H6, Square.northEast(Square.G5));
        Assert.assertEquals(Square.H7, Square.northEast(Square.G6));
        Assert.assertEquals(Square.H8, Square.northEast(Square.G7));

        Assert.assertEquals(Square.B2, Square.northEast(Square.A1));
        Assert.assertEquals(Square.C2, Square.northEast(Square.B1));
        Assert.assertEquals(Square.D2, Square.northEast(Square.C1));
        Assert.assertEquals(Square.E2, Square.northEast(Square.D1));
        Assert.assertEquals(Square.F2, Square.northEast(Square.E1));
        Assert.assertEquals(Square.G2, Square.northEast(Square.F1));
        Assert.assertEquals(Square.H2, Square.northEast(Square.G1));

        Assert.assertEquals(Square.B8, Square.northEast(Square.A7));
        Assert.assertEquals(Square.C8, Square.northEast(Square.B7));
        Assert.assertEquals(Square.D8, Square.northEast(Square.C7));
        Assert.assertEquals(Square.E8, Square.northEast(Square.D7));
        Assert.assertEquals(Square.F8, Square.northEast(Square.E7));
        Assert.assertEquals(Square.G8, Square.northEast(Square.F7));
        Assert.assertEquals(Square.H8, Square.northEast(Square.G7));
    }

    @Test
    public void testNorthWest() {
        Assert.assertEquals(Square.A2, Square.northWest(Square.B1));
        Assert.assertEquals(Square.A3, Square.northWest(Square.B2));
        Assert.assertEquals(Square.A4, Square.northWest(Square.B3));
        Assert.assertEquals(Square.A5, Square.northWest(Square.B4));
        Assert.assertEquals(Square.A6, Square.northWest(Square.B5));
        Assert.assertEquals(Square.A7, Square.northWest(Square.B6));
        Assert.assertEquals(Square.A8, Square.northWest(Square.B7));

        Assert.assertEquals(Square.G2, Square.northWest(Square.H1));
        Assert.assertEquals(Square.G3, Square.northWest(Square.H2));
        Assert.assertEquals(Square.G4, Square.northWest(Square.H3));
        Assert.assertEquals(Square.G5, Square.northWest(Square.H4));
        Assert.assertEquals(Square.G6, Square.northWest(Square.H5));
        Assert.assertEquals(Square.G7, Square.northWest(Square.H6));
        Assert.assertEquals(Square.G8, Square.northWest(Square.H7));

        Assert.assertEquals(Square.A2, Square.northWest(Square.B1));
        Assert.assertEquals(Square.B2, Square.northWest(Square.C1));
        Assert.assertEquals(Square.C2, Square.northWest(Square.D1));
        Assert.assertEquals(Square.D2, Square.northWest(Square.E1));
        Assert.assertEquals(Square.E2, Square.northWest(Square.F1));
        Assert.assertEquals(Square.F2, Square.northWest(Square.G1));
        Assert.assertEquals(Square.G2, Square.northWest(Square.H1));

        Assert.assertEquals(Square.A8, Square.northWest(Square.B7));
        Assert.assertEquals(Square.B8, Square.northWest(Square.C7));
        Assert.assertEquals(Square.C8, Square.northWest(Square.D7));
        Assert.assertEquals(Square.D8, Square.northWest(Square.E7));
        Assert.assertEquals(Square.E8, Square.northWest(Square.F7));
        Assert.assertEquals(Square.F8, Square.northWest(Square.G7));
        Assert.assertEquals(Square.G8, Square.northWest(Square.H7));
    }

    @Test
    public void testSouthEast() {
        Assert.assertEquals(Square.B1, Square.southEast(Square.A2));
        Assert.assertEquals(Square.B2, Square.southEast(Square.A3));
        Assert.assertEquals(Square.B3, Square.southEast(Square.A4));
        Assert.assertEquals(Square.B4, Square.southEast(Square.A5));
        Assert.assertEquals(Square.B5, Square.southEast(Square.A6));
        Assert.assertEquals(Square.B6, Square.southEast(Square.A7));
        Assert.assertEquals(Square.B7, Square.southEast(Square.A8));

        Assert.assertEquals(Square.H1, Square.southEast(Square.G2));
        Assert.assertEquals(Square.H2, Square.southEast(Square.G3));
        Assert.assertEquals(Square.H3, Square.southEast(Square.G4));
        Assert.assertEquals(Square.H4, Square.southEast(Square.G5));
        Assert.assertEquals(Square.H5, Square.southEast(Square.G6));
        Assert.assertEquals(Square.H6, Square.southEast(Square.G7));
        Assert.assertEquals(Square.H7, Square.southEast(Square.G8));

        Assert.assertEquals(Square.B1, Square.southEast(Square.A2));
        Assert.assertEquals(Square.C1, Square.southEast(Square.B2));
        Assert.assertEquals(Square.D1, Square.southEast(Square.C2));
        Assert.assertEquals(Square.E1, Square.southEast(Square.D2));
        Assert.assertEquals(Square.F1, Square.southEast(Square.E2));
        Assert.assertEquals(Square.G1, Square.southEast(Square.F2));
        Assert.assertEquals(Square.H1, Square.southEast(Square.G2));

        Assert.assertEquals(Square.B7, Square.southEast(Square.A8));
        Assert.assertEquals(Square.C7, Square.southEast(Square.B8));
        Assert.assertEquals(Square.D7, Square.southEast(Square.C8));
        Assert.assertEquals(Square.E7, Square.southEast(Square.D8));
        Assert.assertEquals(Square.F7, Square.southEast(Square.E8));
        Assert.assertEquals(Square.G7, Square.southEast(Square.F8));
        Assert.assertEquals(Square.H7, Square.southEast(Square.G8));
    }

    @Test
    public void testSouthWest() {
        Assert.assertEquals(Square.A1, Square.southWest(Square.B2));
        Assert.assertEquals(Square.A2, Square.southWest(Square.B3));
        Assert.assertEquals(Square.A3, Square.southWest(Square.B4));
        Assert.assertEquals(Square.A4, Square.southWest(Square.B5));
        Assert.assertEquals(Square.A5, Square.southWest(Square.B6));
        Assert.assertEquals(Square.A6, Square.southWest(Square.B7));
        Assert.assertEquals(Square.A7, Square.southWest(Square.B8));

        Assert.assertEquals(Square.G1, Square.southWest(Square.H2));
        Assert.assertEquals(Square.G2, Square.southWest(Square.H3));
        Assert.assertEquals(Square.G3, Square.southWest(Square.H4));
        Assert.assertEquals(Square.G4, Square.southWest(Square.H5));
        Assert.assertEquals(Square.G5, Square.southWest(Square.H6));
        Assert.assertEquals(Square.G6, Square.southWest(Square.H7));
        Assert.assertEquals(Square.G7, Square.southWest(Square.H8));

        Assert.assertEquals(Square.A1, Square.southWest(Square.B2));
        Assert.assertEquals(Square.B1, Square.southWest(Square.C2));
        Assert.assertEquals(Square.C1, Square.southWest(Square.D2));
        Assert.assertEquals(Square.D1, Square.southWest(Square.E2));
        Assert.assertEquals(Square.E1, Square.southWest(Square.F2));
        Assert.assertEquals(Square.F1, Square.southWest(Square.G2));
        Assert.assertEquals(Square.G1, Square.southWest(Square.H2));

        Assert.assertEquals(Square.A7, Square.southWest(Square.B8));
        Assert.assertEquals(Square.B7, Square.southWest(Square.C8));
        Assert.assertEquals(Square.C7, Square.southWest(Square.D8));
        Assert.assertEquals(Square.D7, Square.southWest(Square.E8));
        Assert.assertEquals(Square.E7, Square.southWest(Square.F8));
        Assert.assertEquals(Square.F7, Square.southWest(Square.G8));
        Assert.assertEquals(Square.G7, Square.southWest(Square.H8));
    }

    @Test
    public void testNorthNorth() {
        Assert.assertEquals(Square.A4, Square.northNorth(Square.A2));
        Assert.assertEquals(Square.B4, Square.northNorth(Square.B2));
        Assert.assertEquals(Square.C4, Square.northNorth(Square.C2));
        Assert.assertEquals(Square.D4, Square.northNorth(Square.D2));
        Assert.assertEquals(Square.E4, Square.northNorth(Square.E2));
        Assert.assertEquals(Square.F4, Square.northNorth(Square.F2));
        Assert.assertEquals(Square.G4, Square.northNorth(Square.G2));
        Assert.assertEquals(Square.H4, Square.northNorth(Square.H2));
    }

    @Test
    public void testSouthSouth() {
        Assert.assertEquals(Square.A5, Square.southSouth(Square.A7));
        Assert.assertEquals(Square.B5, Square.southSouth(Square.B7));
        Assert.assertEquals(Square.C5, Square.southSouth(Square.C7));
        Assert.assertEquals(Square.D5, Square.southSouth(Square.D7));
        Assert.assertEquals(Square.E5, Square.southSouth(Square.E7));
        Assert.assertEquals(Square.F5, Square.southSouth(Square.F7));
        Assert.assertEquals(Square.G5, Square.southSouth(Square.G7));
        Assert.assertEquals(Square.H5, Square.southSouth(Square.H7));
    }

    // -----------------------------------------------------------------------

    /**
     * Asserts that the squares defined by the names in {@code expectedNames} are the same as
     * the squares defined by the indices in {@code actualIndices}.
     *
     * @param expectedNames An array of the expected square names, e.g. ["a1", "e4"].
     * @param actualIndices A list of the actual square indices, e.g. [0, 28].
     */
    private void assertSquareIndices(String[] expectedNames, List<Integer> actualIndices) {
        int[] expected = Arrays.stream(expectedNames).mapToInt(Square::nameToIndex).sorted().toArray();
        int[] actual = actualIndices.stream().mapToInt(n -> n).sorted().toArray();
        assertArrayEquals(expected, actual);
    }

    /**
     * Asserts that the squares defined by the names in {@code expectedNames} are the same as
     * the squares defined by the IDs in {@code actualIds}.
     *
     * @param expectedNames An array of the expected square names, e.g. ["a1", "e4"].
     * @param actualIds A list of the actual square IDs, e.g. [Square.A1, Square.E4].
     */
    private static void assertSquareIds(String[] expectedNames, List<Long> actualIds) {
        long[] expected = Arrays.stream(expectedNames).mapToLong(Square::nameToId).sorted().toArray();
        long[] actual = actualIds.stream().mapToLong(n -> n).sorted().toArray();
        assertArrayEquals(expected, actual);
    }
}
