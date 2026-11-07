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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Perft tests for the move generator. Perft counts the legal move sequences from a position to a fixed depth.
 * The expected counts are the published counts for the six standard positions on the chessprogramming wiki.
 * Each position is tested at every depth from 1 up, so the lowest failing depth shows where a count goes wrong.
 *
 * @author Johan Dykstrom
 * @see FullMoveGenerator
 */
public class PerftTest extends AbstractTestCase {

    private final FullMoveGenerator generator = new FullMoveGenerator();

    @ParameterizedTest(name = "{0} at depth {2}")
    @CsvSource({
        "start, "     + FEN_START + ", 1, 20",
        "start, "     + FEN_START + ", 2, 400",
        "start, "     + FEN_START + ", 3, 8902",
        "start, "     + FEN_START + ", 4, 197281",
        "start, "     + FEN_START + ", 5, 4865609",
        "kiwipete, "  + FEN_PERFT_KIWIPETE + ", 1, 48",
        "kiwipete, "  + FEN_PERFT_KIWIPETE + ", 2, 2039",
        "kiwipete, "  + FEN_PERFT_KIWIPETE + ", 3, 97862",
        "kiwipete, "  + FEN_PERFT_KIWIPETE + ", 4, 4085603",
        "position 3, " + FEN_PERFT_3 + ", 1, 14",
        "position 3, " + FEN_PERFT_3 + ", 2, 191",
        "position 3, " + FEN_PERFT_3 + ", 3, 2812",
        "position 3, " + FEN_PERFT_3 + ", 4, 43238",
        "position 3, " + FEN_PERFT_3 + ", 5, 674624",
        "position 4, " + FEN_PERFT_4 + ", 1, 6",
        "position 4, " + FEN_PERFT_4 + ", 2, 264",
        "position 4, " + FEN_PERFT_4 + ", 3, 9467",
        "position 4, " + FEN_PERFT_4 + ", 4, 422333",
        "position 5, " + FEN_PERFT_5 + ", 1, 44",
        "position 5, " + FEN_PERFT_5 + ", 2, 1486",
        "position 5, " + FEN_PERFT_5 + ", 3, 62379",
        "position 5, " + FEN_PERFT_5 + ", 4, 2103487",
        "position 6, " + FEN_PERFT_6 + ", 1, 46",
        "position 6, " + FEN_PERFT_6 + ", 2, 2079",
        "position 6, " + FEN_PERFT_6 + ", 3, 89890",
        "position 6, " + FEN_PERFT_6 + ", 4, 3894594",
    })
    public void testPerft(String name, String fen, int depth, long expectedCount) throws Exception {
        assertEquals(expectedCount, perft(FenParser.parse(fen), depth, 0));
    }

    /**
     * Returns the number of legal move sequences of length {@code depth} from {@code position}.
     * The generator makes pseudo-legal moves, so this method skips every move that leaves the
     * mover's king in check. The generator stores the moves for each ply in its own row,
     * so the recursive call does not overwrite the moves that the loop is still walking.
     *
     * @param position The position to count from.
     * @param depth The number of plies left to count.
     * @param ply The number of plies from the root position, used as the generator's row index.
     */
    private long perft(Position position, int depth, int ply) {
        if (depth == 0) {
            return 1;
        }

        long count = 0;
        int numberOfMoves = generator.generateMoves(position, ply);
        for (int i = 0; i < numberOfMoves; i++) {
            Position next = position.withMove(generator.moves[ply][i]);
            if (!next.isIllegalCheck()) {
                count += perft(next, depth - 1, ply + 1);
            }
        }
        return count;
    }
}
