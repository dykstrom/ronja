/*
 * Copyright (C) 2017 Johan Dykstrom
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

import org.junit.Test;
import se.dykstrom.ronja.common.book.OpeningBook;
import se.dykstrom.ronja.engine.time.TimeControl;
import se.dykstrom.ronja.engine.time.TimeData;
import se.dykstrom.ronja.test.AbstractTestCase;

import static org.junit.Assert.assertEquals;
import static se.dykstrom.ronja.engine.time.TimeControlType.*;

/**
 * This class is for testing class {@code Game} using JUnit.
 *
 * @author Johan Dykstrom
 * @see Game
 */
public class GameTest extends AbstractTestCase {

    private static final TimeControl TC_40_5_0 = new TimeControl(40, 5 * 60 * 1000, 0, CLASSIC);
    private static final TimeControl TC_0_30_5 = new TimeControl(0, 30 * 60 * 1000, 5 * 1000, INCREMENTAL);
    private static final TimeControl TC_0_0_30 = new TimeControl(0, 0, 30 * 1000, SECONDS_PER_MOVE);

    private static final long FIVE_MINUTES = 5 * 60 * 1000;
    private static final long TEN_MINUTES = 10 * 60 * 1000;
    private static final long THIRTY_MINUTES = 30 * 60 * 1000;
    private static final long FIVE_SECONDS = 5 * 1000;
    private static final long THIRTY_SECONDS = 30 * 1000;

    private final Game game = new Game(OpeningBook.DEFAULT);

    @Test
    public void testUpdateTimeDataAfterMoveClassic() {
        int usedTime = 1000;

        game.setTimeControl(TC_40_5_0);
        game.setTimeData(TimeData.from(TC_40_5_0));

        game.updateTimeDataAfterMove(usedTime);

        assertEquals(39, game.getTimeData().numberOfMoves());
        assertEquals(FIVE_MINUTES - usedTime, game.getTimeData().remainingTime());
    }

    @Test
    public void testUpdateTimeDataAfterMoveClassicPassedTimeControl() {
        int usedTime = 1000;

        game.setTimeControl(TC_40_5_0);
        game.setTimeData(TimeData.from(TC_40_5_0).withNumberOfMoves(1));

        game.updateTimeDataAfterMove(usedTime);

        // Expect that there are now 40 new moves to make
        assertEquals(40, game.getTimeData().numberOfMoves());
        // Expect to have the original 5 minutes, plus 5 new minutes minus used time
        assertEquals(TEN_MINUTES - usedTime, game.getTimeData().remainingTime());
    }

    @Test
    public void testUpdateTimeDataAfterMoveIncremental() {
        int usedTime = 1000;

        game.setTimeControl(TC_0_30_5);
        game.setTimeData(TimeData.from(TC_0_30_5));

        game.updateTimeDataAfterMove(usedTime);

        assertEquals(0, game.getTimeData().numberOfMoves());
        assertEquals(THIRTY_MINUTES + FIVE_SECONDS - usedTime, game.getTimeData().remainingTime());
    }

    @Test
    public void testUpdateTimeDataAfterMoveSecondsPerMove() {
        int usedTime = 1000;

        game.setTimeControl(TC_0_0_30);
        game.setTimeData(TimeData.from(TC_0_0_30));

        game.updateTimeDataAfterMove(usedTime);

        assertEquals(0, game.getTimeData().numberOfMoves());
        assertEquals(THIRTY_SECONDS, game.getTimeData().remainingTime());
    }
}
