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

import org.junit.Test;
import se.dykstrom.ronja.common.model.Game;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static se.dykstrom.ronja.engine.time.TimeControlType.CLASSIC;
import static se.dykstrom.ronja.engine.time.TimeControlType.INCREMENTAL;
import static se.dykstrom.ronja.engine.time.TimeControlType.SECONDS_PER_MOVE;
import static se.dykstrom.ronja.engine.time.TimeUtils.*;

/**
 * This class is for testing class {@code TimeUtils} using JUnit.
 *
 * @author Johan Dykstrom
 * @see TimeUtils
 */
public class TimeUtilsTest {

    private static final TimeControl TC_40_5_0 = new TimeControl(40, 5 * 60 * 1000, 0, CLASSIC);
    private static final TimeControl TC_0_30_5 = new TimeControl(0, 30 * 60 * 1000, 5 * 1000, INCREMENTAL);
    private static final TimeControl TC_0_0_10 = new TimeControl(0, 0, 10 * 1000, INCREMENTAL);
    private static final TimeControl TC_10_1_30_0 = new TimeControl(10, 90 * 1000, 0, CLASSIC);
    private static final TimeControl TC_40_2_30_0 = new TimeControl(40, 150 * 1000, 0, CLASSIC);
    private static final TimeControl TC_40_25_0 = new TimeControl(40, 25 * 60 * 1000, 0, CLASSIC);

    private static final TimeControl TC_0_0_30 = new TimeControl(0, 0, 30 * 1000, SECONDS_PER_MOVE);

    private static final long FIVE_MINUTES = 5 * 60 * 1000;
    private static final long TEN_MINUTES = 10 * 60 * 1000;
    private static final long THIRTY_MINUTES = 30 * 60 * 1000;
    private static final long FIVE_SECONDS = 5 * 1000;
    private static final long THIRTY_SECONDS = 30 * 1000;

    @Test
    public void testFormatTime() {
        assertEquals("00:00:01", formatTime(1000));
        assertEquals("00:00:01.001", formatTime(1001));
        assertEquals("00:02:05", formatTime(125000));
        assertEquals("03:14:15.927", formatTime(((3 * 60 + 14) * 60 + 15) * 1000 + 927));
    }

    @Test
    public void testParseLevelText() throws Exception {
        assertEquals(TC_40_5_0, parseLevelText("40 5 0"));
        assertEquals(TC_0_30_5, parseLevelText("0 30 5"));
        assertEquals(TC_0_0_10, parseLevelText("0 0 10"));
        assertEquals(TC_10_1_30_0, parseLevelText("10 1:30 0"));
        assertEquals(TC_40_25_0, parseLevelText("40 25+5 0"));
        assertEquals(TC_40_2_30_0, parseLevelText("40 2:30+5 0"));
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
    public void testUpdateTimeDataAfterMoveClassic() throws Exception {
        Game game = Game.instance();
        int usedTime = 1000;

        game.setTimeControl(TC_40_5_0);
        game.setTimeData(TimeData.from(TC_40_5_0));

        updateTimeDataAfterMove(game, usedTime);

        assertEquals(39, game.getTimeData().getNumberOfMoves());
        assertEquals(FIVE_MINUTES - usedTime, game.getTimeData().getRemainingTime());
    }

    @Test
    public void testUpdateTimeDataAfterMoveClassicPassedTimeControl() throws Exception {
        Game game = Game.instance();
        int usedTime = 1000;

        game.setTimeControl(TC_40_5_0);
        game.setTimeData(TimeData.from(TC_40_5_0).withNumberOfMoves(1));

        updateTimeDataAfterMove(game, usedTime);

        // Expect that there are now 40 new moves to make
        assertEquals(40, game.getTimeData().getNumberOfMoves());
        // Expect to have the original 5 minutes, plus 5 new minutes minus used time
        assertEquals(TEN_MINUTES - usedTime, game.getTimeData().getRemainingTime());
    }

    @Test
    public void testUpdateTimeDataAfterMoveIncremental() throws Exception {
        Game game = Game.instance();
        int usedTime = 1000;

        game.setTimeControl(TC_0_30_5);
        game.setTimeData(TimeData.from(TC_0_30_5));

        updateTimeDataAfterMove(game, usedTime);

        assertEquals(0, game.getTimeData().getNumberOfMoves());
        assertEquals(THIRTY_MINUTES + FIVE_SECONDS - usedTime, game.getTimeData().getRemainingTime());
    }

    @Test
    public void testUpdateTimeDataAfterMoveSecondsPerMove() throws Exception {
        Game game = Game.instance();
        int usedTime = 1000;

        game.setTimeControl(TC_0_0_30);
        game.setTimeData(TimeData.from(TC_0_0_30));

        updateTimeDataAfterMove(game, usedTime);

        assertEquals(0, game.getTimeData().getNumberOfMoves());
        assertEquals(THIRTY_SECONDS, game.getTimeData().getRemainingTime());
    }

    @Test
    public void testCalculateTimeForNextMoveClassic() {
        assertEquals(7500, calculateTimeForNextMove(TC_40_5_0, TimeData.from(TC_40_5_0)));
        assertEquals(9000, calculateTimeForNextMove(TC_10_1_30_0, TimeData.from(TC_10_1_30_0)));
    }

    @Test
    public void testCalculateTimeForNextMoveIncremental() {
        assertEquals(90000, calculateTimeForNextMove(TC_0_30_5, TimeData.from(TC_0_30_5)));
        assertEquals(50, calculateTimeForNextMove(TC_0_30_5, TimeData.from(TC_0_30_5).withRemainingTime(1000)));
    }

    @Test
    public void testCalculateTimeForNextMoveSecondsPerMove() {
        assertEquals(29950, calculateTimeForNextMove(TC_0_0_30, TimeData.from(TC_0_0_30)));
    }
}
