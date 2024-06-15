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

package se.dykstrom.ronja.engine.time;

import java.text.ParseException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.dykstrom.ronja.engine.time.TimeControlType.CLASSIC;
import static se.dykstrom.ronja.engine.time.TimeControlType.INCREMENTAL;
import static se.dykstrom.ronja.engine.time.TimeControlType.SECONDS_PER_MOVE;
import static se.dykstrom.ronja.engine.time.TimeUtils.calculateTimeForNextMove;
import static se.dykstrom.ronja.engine.time.TimeUtils.formatTime;
import static se.dykstrom.ronja.engine.time.TimeUtils.parseLevelText;
import static se.dykstrom.ronja.engine.time.TimeUtils.parseStText;

/**
 * This class is for testing class {@code TimeUtils} using JUnit.
 *
 * @author Johan Dykstrom
 * @see TimeUtils
 */
public class TimeUtilsTest {

    private static final TimeControl TC_10_01_00 = new TimeControl(10, 60 * 1000, 0, CLASSIC);
    private static final TimeControl TC_40_05_00 = new TimeControl(40, 5 * 60 * 1000, 0, CLASSIC);
    // Incremental time with an increment of 0
    private static final TimeControl TC_0_03_00 = new TimeControl(0, 3 * 60 * 1000, 0, INCREMENTAL);
    private static final TimeControl TC_0_30_05 = new TimeControl(0, 30 * 60 * 1000, 5 * 1000, INCREMENTAL);
    private static final TimeControl TC_0_00_10 = new TimeControl(0, 0, 10 * 1000, INCREMENTAL);
    private static final TimeControl TC_10_1_30_00 = new TimeControl(10, 90 * 1000, 0, CLASSIC);
    private static final TimeControl TC_40_2_30_00 = new TimeControl(40, 150 * 1000, 0, CLASSIC);
    private static final TimeControl TC_40_25_00 = new TimeControl(40, 25 * 60 * 1000, 0, CLASSIC);

    private static final TimeControl TC_0_0_03 = new TimeControl(0, 0, 3 * 1000, SECONDS_PER_MOVE);
    private static final TimeControl TC_0_0_30 = new TimeControl(0, 0, 30 * 1000, SECONDS_PER_MOVE);

    @Test
    public void testFormatTime() {
        assertEquals("00:00:01", formatTime(1000));
        assertEquals("00:00:01.001", formatTime(1001));
        assertEquals("00:02:05", formatTime(125000));
        assertEquals("03:14:15.927", formatTime(((3 * 60 + 14) * 60 + 15) * 1000 + 927));
    }

    @Test
    public void testParseLevelText() throws Exception {
        assertEquals(TC_40_05_00, parseLevelText("40 5 0"));
        assertEquals(TC_10_01_00, parseLevelText("10 0:60 0"));
        assertEquals(TC_0_03_00, parseLevelText("0 3 0"));
        assertEquals(TC_0_30_05, parseLevelText("0 30 5"));
        assertEquals(TC_0_00_10, parseLevelText("0 0 10"));
        assertEquals(TC_10_1_30_00, parseLevelText("10 1:30 0"));
        assertEquals(TC_40_25_00, parseLevelText("40 25+5 0"));
        assertEquals(TC_40_2_30_00, parseLevelText("40 2:30+5 0"));
    }

    @Test(expected = ParseException.class)
    public void testParseLevelText_TwoArguments() throws Exception {
        parseLevelText("40 5");
    }

    @Test(expected = ParseException.class)
    public void testParseLevelText_FourArguments() throws Exception {
        parseLevelText("40 5 0 10");
    }

    @Test
    public void testParseStText() throws Exception {
        assertEquals(TC_0_0_30, parseStText("30"));
        assertEquals(TC_0_0_03, parseStText("3"));
    }

    @Test(expected = ParseException.class)
    public void testParseStText_NoArguments() throws Exception {
        parseStText("");
    }

    @Test(expected = ParseException.class)
    public void testParseStText_TwoArguments() throws Exception {
        parseStText("40 5");
    }

    @Test
    public void testCalculateTimeForNextMoveClassic() {
        assertTrue(calculateTimeForNextMove(TC_40_05_00, TimeData.from(TC_40_05_00)) > 7500);
        assertTrue(calculateTimeForNextMove(TC_10_1_30_00, TimeData.from(TC_10_1_30_00)) > 9000);
    }

    @Test
    public void testCalculateTimeForLastMoveBeforeTimeControl() {
        final var timeData = TimeData.from(TC_10_01_00).withNumberOfMoves(1);
        // The last move before the time control should be allocated less time as a security measure
        assertTrue(calculateTimeForNextMove(TC_10_01_00, timeData) <= timeData.remainingTime() / 2);
    }

    @Test
    public void testCalculateTimeForAllMovesClassic() {
        TimeData timeData = TimeData.from(TC_10_01_00);
        final long totalNumberOfMoves = timeData.numberOfMoves();
        final long totalAvailableTime = timeData.remainingTime();

        long calculatedTime = 0;
        long totalCalculatedTime = 0;
        for (long numberOfMovesLeft = totalNumberOfMoves; numberOfMovesLeft > 0; numberOfMovesLeft--) {
            timeData = timeData.withNumberOfMoves(numberOfMovesLeft).withRemainingTime(timeData.remainingTime() - calculatedTime);
            calculatedTime = calculateTimeForNextMove(TC_10_01_00, timeData);
            totalCalculatedTime += calculatedTime;
        }
        // The total time calculated to all moves must be less than the total available time
        assertTrue(totalCalculatedTime < totalAvailableTime);
    }

    @Test
    public void testCalculateTimeForNextMoveIncremental() {
        TimeData timeData = TimeData.from(TC_0_30_05);
        assertEquals(95000, calculateTimeForNextMove(TC_0_30_05, timeData.withRemainingTime(30 * 60 * 1000 + 5 * 1000)));
        assertEquals(5050, calculateTimeForNextMove(TC_0_30_05, timeData.withRemainingTime(1000 + 5 * 1000)));
    }

    @Test
    public void testCalculateTimeForNextMoveSecondsPerMove() {
        assertEquals(2700, calculateTimeForNextMove(TC_0_0_03, TimeData.from(TC_0_0_03)));
        assertEquals(29500, calculateTimeForNextMove(TC_0_0_30, TimeData.from(TC_0_0_30)));
    }
}
