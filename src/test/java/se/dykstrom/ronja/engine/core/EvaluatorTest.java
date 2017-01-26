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
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.*;

/**
 * This class is for testing class {@code Evaluator} using JUnit.
 *
 * @author Johan Dykstrom
 * @see Evaluator
 */
public class EvaluatorTest extends AbstractTestCase {

    private final Evaluator evaluator = new Evaluator();

    // ------------------------------------------------------------------------

    @Test
    public void testEvaluate() throws Exception {
        int score = evaluator.evaluate(FenParser.parse(FEN_START));
        assertTrue(score >= 0);
    }

    @Test
    public void testCalculatePieceValues() throws Exception {
        assertEquals(0, evaluator.calculatePieceValues(FenParser.parse(FEN_START)));
        assertEquals(-5500, evaluator.calculatePieceValues(FenParser.parse(FEN_TWO_QUEENS)));
        assertEquals(-2000, evaluator.calculatePieceValues(FenParser.parse(FEN_END_GAME_3)));
    }

    @Test
    public void testCalculateAttackedSquares() throws Exception {
        assertEquals(0, evaluator.calculateAttackedSquares(FenParser.parse(FEN_START)));
        assertEquals(-200, evaluator.calculateAttackedSquares(FenParser.parse(FEN_END_GAME_3)));
    }
}
