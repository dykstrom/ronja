/*
 * Copyright (C) 2026 Johan Dykstrom
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Deep perft tests for the move generator, too slow for {@link PerftTest}. Only {@code mvn verify} runs them.
 * <p>
 * {@link #testDeepPerft} tests the standard positions from the chessprogramming wiki one ply deeper than
 * {@code PerftTest}. It leaves out the start position and position 6, which have almost no castling,
 * en passant or promotion.
 * <p>
 * {@link #testVajoletPositions} tests 6,838 positions from long games at depth 3. The positions and counts
 * come from {@code tests/perft.txt} in the Vajolet chess engine (<a href="https://github.com/elcabesa/vajolet">Vajolet</a>),
 * which is GPL-3.0 like Ronja. Each line holds a FEN without the move clocks, followed by the counts for
 * depths 1 to 6.
 *
 * @author Johan Dykstrom
 * @see FullMoveGenerator
 */
public class PerftIT extends AbstractTestCase {

    private static final String VAJOLET_FILE = "/perft/vajolet-perft.txt";

    private static final int VAJOLET_DEPTH = 3;

    private final Perft perft = new Perft();

    @ParameterizedTest(name = "{0} at depth {2}")
    @CsvSource({
        "kiwipete, "            + FEN_PERFT_KIWIPETE   + ", 5, 193690690",
        "position 3, "          + FEN_PERFT_3          + ", 6, 11030083",
        "position 4, "          + FEN_PERFT_4          + ", 5, 15833292",
        "position 4 mirrored, " + FEN_PERFT_4_MIRRORED + ", 5, 15833292",
        "position 5, "          + FEN_PERFT_5          + ", 5, 89941194",
    })
    public void testDeepPerft(String name, String fen, int depth, long expectedCount) throws Exception {
        assertEquals(expectedCount, perft.count(FenParser.parse(fen), depth));
    }

    @Test
    public void testVajoletPositions() throws Exception {
        List<String> lines = readVajoletFile();
        List<String> mismatches = new ArrayList<>();
        for (String line : lines) {
            String[] fields = line.split(",");
            long expectedCount = Long.parseLong(fields[VAJOLET_DEPTH]);
            long actualCount = perft.count(FenParser.parse(fields[0] + " 0 1"), VAJOLET_DEPTH);
            if (actualCount != expectedCount) {
                mismatches.add(fields[0] + ": expected " + expectedCount + ", was " + actualCount);
            }
        }
        assertEquals(6838, lines.size(), "positions read");
        assertEquals(List.of(), mismatches, "positions with a wrong count at depth " + VAJOLET_DEPTH);
    }

    private static List<String> readVajoletFile() throws IOException {
        try (InputStream stream = Objects.requireNonNull(PerftIT.class.getResourceAsStream(VAJOLET_FILE), VAJOLET_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return reader.lines().filter(line -> !line.isBlank()).toList();
        }
    }
}
