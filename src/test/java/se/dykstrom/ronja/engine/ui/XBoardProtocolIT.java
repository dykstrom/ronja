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

package se.dykstrom.ronja.engine.ui;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se.dykstrom.ronja.engine.utils.AppConfig;
import se.dykstrom.ronja.test.AbstractTestCase;
import se.dykstrom.ronja.test.TestUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.util.regex.Pattern.quote;
import static org.junit.Assert.*;
import static se.dykstrom.ronja.test.TestUtils.*;

/**
 * This class is for integration tests related to the XBoard protocol (Chess Engine Communication Protocol).
 * See: <a href="https://www.gnu.org/software/xboard/engine-intf.html">XBoard protocol</a>.
 *
 * @author Johan Dykstrom
 */
public class XBoardProtocolIT extends AbstractTestCase {

    private static final Logger TLOG = Logger.getLogger(XBoardProtocolIT.class.getName());

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String ENGINE_NAME = "ENGINE_NAME";

    private static final String JAVA_CMD = "java";
    private static final String BOOK_ARG = "-D" + AppConfig.PROPERTY_BOOK_FILE;
    private static final String GAME_LOG_ARG = "-D" + AppConfig.PROPERTY_GAME_LOG_FILE;
    private static final String ENGINE_ARG = "-D" + AppConfig.PROPERTY_ENGINE_NAME;
    private static final String CLASSPATH_ARG = "-classpath";
    private static final String CLASSPATH_VALUE = "target/classes";
    private static final String MAIN_CLASS = "se.dykstrom.ronja.engine.ui.Ronja";

    private static File bookFile;
    private File gameLogFile;

    private Process process;

    private BufferedReader reader;
    private PrintWriter writer;

    @BeforeClass
    public static void setUpClass() throws Exception {
        bookFile = TestUtils.createBookFile();
    }

    @Before
    public void setUp() throws Exception {
        gameLogFile = File.createTempFile("XBoardProtocolIT_", ".pgn");
        gameLogFile.deleteOnExit();

        setUpChessEngine();
    }

    @After
    public void tearDown() throws Exception {
        tearDownChessEngine();

        reader.close();
        writer.close();
    }

    /**
     * Sets up input and output for the chess engine, and starts it in its own thread.
     */
    private void setUpChessEngine() throws Exception {
        ProcessBuilder builder = new ProcessBuilder(getCommandsAndArgs());
        TLOG.finest("Starting engine with command " + builder.command());
        process = builder.redirectErrorStream(true).start();
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        writer = new PrintWriter(process.getOutputStream(), true);

        // Wait for the process to start
        assertProcessStarted(process, 5000, TimeUnit.MILLISECONDS);

        // If we failed to start the process
        if (!process.isAlive()) {
            if (reader.ready()) {
                fail("Failed to start engine: " + readAllInput());
            } else {
                fail("Failed to start engine: " + process.exitValue());
            }
        }

        // Discard initial input from the engine
        discardAllInput();
    }

    /**
     * Tears down the chess engine.
     */
    private void tearDownChessEngine() throws Exception {
        process.waitFor(5000, TimeUnit.MILLISECONDS);
        process.destroy();
    }

    /**
     * Returns an array of command and arguments to create a process of.
     */
    private String[] getCommandsAndArgs() {
        return new String[]{JAVA_CMD,
                BOOK_ARG + "=" + bookFile.getAbsolutePath(),
                GAME_LOG_ARG + "=" + gameLogFile.getAbsolutePath(),
                ENGINE_ARG + "=" + ENGINE_NAME,
                CLASSPATH_ARG,
                CLASSPATH_VALUE,
                MAIN_CLASS};
    }

    /**
     * Asserts that the process represented by {@code process} starts within the given timeout.
     * If the process has already started, this method returns immediately.
     *
     * @param process The process to wait for.
     * @param timeout The maximum wait time.
     * @param unit The time unit of the timeout argument.
     * @throws Exception If an exception occurs while waiting.
     */
    private void assertProcessStarted(Process process, long timeout, TimeUnit unit) throws Exception {
        waitForSupplier(() -> process.getInputStream().available() > 0, timeout, unit);
    }

    // ------------------------------------------------------------------------

    /**
     * Tests sending only the quit command, which should be the simplest possible sequence of commands.
     */
    @Test
    public void testQuit() {
        writer.println("quit");
    }

    /**
     * Tests sending the init sequence usually sent by XBoard.
     */
    @Test
    public void testInitSequence() throws Exception {
        writer.println("xboard");
        writer.println("protover 2");
        List<String> list = readAllInput();
        assertContainsRegex("feature myname=\"" + ENGINE_NAME + "\"", list);
        assertContainsRegex("feature done=1", list);
        writer.println("new");
        writer.println("quit");
    }

    /**
     * Tests the ping command.
     */
    @Test
    public void testPing() throws Exception {
        writer.println("xboard");
        writer.println("ping 17");
        List<String> list = readAllInput();
        assertContainsRegex("pong 17", list);
        writer.println("quit");
    }

    /**
     * Tests the result command.
     */
    @Test
    public void testResult() throws Exception {
        writer.println("xboard");
        writer.println("new");
        writer.println("result 1/2-1/2 {Stalemate}");
        assertGameLogFileWritten(1000, TimeUnit.MILLISECONDS);
        writer.println("quit");
    }

    /**
     * Tests playing a complete game using the force command.
     */
    @Test
    public void testCompleteGameUsingForce() throws Exception {
        String shortResult = "0-1";
        String fullResult = shortResult + " {Black mates}";
        String opponent = "Some chess engine";
        List<String> list;

        writer.println("xboard");
        writer.println("protover 2");

        list = readAllInput();
        // Verify that "playother" command is enabled
        assertContainsRegex("feature playother=1", list);

        writer.println("new");
        writer.println("name " + opponent);
        writer.println("computer");
        writer.println("force");
        writer.println("usermove f2f3");
        writer.println("usermove e7e5");
        // Make engine play black (it is now white's turn)
        writer.println("playother");
        // Make the blunder move g4??
        writer.println("usermove g2g4");

        list = readAllInput();
        // Verify that the engine finds the fool's mate Qh4#
        assertContainsRegex("move d8h4", list);
        assertContainsRegex(quote(fullResult), list);

        writer.println("result " + fullResult);
        writer.println("force");

        // Verify that game log file was written
        assertGameLogFileWritten(1000, TimeUnit.MILLISECONDS);

        // Read game log file
        List<String> lines = Files.readAllLines(gameLogFile.toPath());
        assertContainsRegex(quote("[Result \"" + shortResult + "\"]"), lines);
        assertContainsRegex(quote("[White \"" + opponent + "\"]"), lines);
        assertContainsRegex(quote("[Black \"" + ENGINE_NAME + "\"]"), lines);
        assertContainsRegex(quote("[Date \"" + DF.format(LocalDateTime.now()) + "\"]"), lines);
        assertContainsRegex(quote("1. f3 e5"), lines);
        assertContainsRegex(quote("2. g4 Qh4#"), lines);
        assertContainsRegex(quote(fullResult), lines);
    }

    /**
     * Tests playing a partial game using the setboard command.
     */
    @Test
    public void testPartialGameUsingSetBoard() throws Exception {
        String shortResult = "0-1";
        String fullResult = shortResult + " {Black mates}";
        String opponent = "Some chess engine";
        List<String> list;

        writer.println("xboard");
        writer.println("protover 2");
        discardAllInput();
        writer.println("new");
        writer.println("name " + opponent);
        writer.println("computer");
        writer.println("force");
        writer.println("setboard " + FEN_CHECKMATE_1_1);

        // Make engine play white
        writer.println("go");
        list = readAllInput();
        // Verify that the engine finds the only move Bc1
        assertContainsRegex("move f4c1", list);

        // Checkmate engine
        writer.println("usermove a1c1");
        list = readAllInput();
        // Verify that the engine sees the checkmate
        assertContainsRegex(quote(fullResult), list);

        writer.println("result " + fullResult);
        writer.println("force");

        // Verify that game log file was written
        assertGameLogFileWritten(1000, TimeUnit.MILLISECONDS);

        // Read game log file
        List<String> lines = Files.readAllLines(gameLogFile.toPath());
        assertContainsRegex(quote("[Result \"" + shortResult + "\"]"), lines);
        assertContainsRegex(quote("[White \"" + ENGINE_NAME + "\"]"), lines);
        assertContainsRegex(quote("[Black \"" + opponent + "\"]"), lines);
        assertContainsRegex(quote("[Date \"" + DF.format(LocalDateTime.now()) + "\"]"), lines);
        assertContainsRegex(quote("[SetUp \"1\""), lines);
        assertContainsRegex(quote("[FEN \"" + FEN_CHECKMATE_1_1 + "\"]"), lines);
        assertContainsRegex(quote("18. Bc1 Rxc1#"), lines);
        assertContainsRegex(quote(fullResult), lines);
    }

    /**
     * Tests the bk command.
     */
    @Test
    public void testBk() throws Exception {
        writer.println("xboard");
        writer.println("bk");
        List<String> list = readAllInput();
        assertContainsRegex("Book moves:", list);
        assertContainsRegex("e4\\s+100%", list);
        writer.println("quit");
    }

    /**
     * Tests the hint command.
     */
    @Test
    public void testHint() throws Exception {
        writer.println("xboard");
        writer.println("protover 2");
        writer.println("force");
        discardAllInput();
        writer.println("setboard " + FEN_CHECKMATE_1_1);
        writer.println("playother");
        writer.println("hint");
        List<String> list = readAllInput();
        assertContainsRegex("Hint: Bc1", list);
        writer.println("quit");
    }

    /**
     * Tests the hint command with a book move.
     */
    @Test
    public void testHintWithBookMove() throws Exception {
        writer.println("xboard");
        writer.println("hint");
        List<String> list = readAllInput();
        assertContainsRegex("Hint: e4", list);
        writer.println("quit");
    }

    /**
     * Tests playing a single move.
     */
    @Test
    public void testSingleMove() throws Exception {
        writer.println("xboard");
        writer.println("protover 2");
        discardAllInput();
        writer.println("new");
        writer.println("usermove e2e4");
        List<String> list = readAllInput();
        assertContainsRegex("move e7(e5|e6)", list);
        writer.println("quit");
    }

    /**
     * Tests the go command.
     */
    @Test
    public void testGo() throws Exception {
        writer.println("xboard");
        writer.println("protover 2");
        discardAllInput();
        writer.println("new");
        writer.println("go");
        List<String> list = readAllInput();
        assertContainsRegex("move e2e4", list);
        writer.println("quit");
    }

    /**
     * Tests playing multiple moves.
     */
    @Test
    public void testMultipleMoves() throws Exception {
        writer.println("xboard");
        writer.println("protover 2");
        discardAllInput();
        writer.println("new");
        writer.println("go");
        List<String> list = readAllInput();
        assertContainsRegex("move e2e4", list);
        writer.println("usermove e7e5");
        list = readAllInput();
        assertContainsRegex("move g1f3", list);
        writer.println("usermove f1c4");
        list = readAllInput();
        assertContainsRegex("move", list); // We don't know the move here
        writer.println("quit");
    }

    /**
     * Tests setting up the board.
     */
    @Test
    public void testSetBoard() throws Exception {
        writer.println("xboard");
        writer.println("protover 2");
        writer.println("force");
        discardAllInput();
        writer.println("setboard " + FEN_CHECKMATE_1_1);
        writer.println("go");
        List<String> list = readAllInput();
        assertEquals(1, list.size());
        assertEquals("move f4c1", list.get(0)); // The only move
        writer.println("quit");
    }

    /**
     * Tests setting up the board with an illegal position.
     */
    @Test
    public void testSetBoardIllegalPosition() throws Exception {
        writer.println("xboard");
        writer.println("protover 2");
        writer.println("force");
        discardAllInput();
        writer.println("setboard " + FEN_ILLEGAL_0);
        List<String> list = readAllInput();
        assertEquals(1, list.size());
        assertContainsRegex("Illegal position", list);
        writer.println("quit");
    }

    /**
     * Tests checkmate by the engine.
     */
    @Test
    public void testEngineCheckmates() throws Exception {
        writer.println("xboard");
        writer.println("protover 2");
        writer.println("force");
        discardAllInput();
        writer.println("setboard " + FEN_CHECKMATE_1_2);
        writer.println("go");
        List<String> list = readAllInput();
        assertEquals(2, list.size());
        assertEquals("move a1c1", list.get(0));
        assertEquals("0-1 {Black mates}", list.get(1));
        writer.println("quit");
    }

    /**
     * Tests checkmate by the user.
     */
    @Test
    public void testUserCheckmates() throws Exception {
        writer.println("xboard");
        writer.println("protover 2");
        writer.println("force");
        discardAllInput();
        writer.println("setboard " + FEN_CHECKMATE_1_2);
        writer.println("usermove a1c1");
        List<String> list = readAllInput();
        assertEquals(1, list.size());
        assertEquals("0-1 {Black mates}", list.get(0));
        writer.println("quit");
    }

    /**
     * Tests moving when in checkmate.
     */
    @Test
    public void testWhenInCheckmate() throws Exception {
        writer.println("xboard");
        writer.println("protover 2");
        writer.println("force");
        discardAllInput();
        writer.println("setboard " + FEN_CHECKMATE_1_3);
        writer.println("usermove h2h3");
        List<String> list = readAllInput();
        assertEquals(1, list.size());
        assertEquals("Error (checkmate): usermove", list.get(0));
        writer.println("quit");
    }

    /**
     * Tests draw by the engine.
     */
    @Test
    public void testEngineDraws() throws Exception {
        writer.println("xboard");
        writer.println("protover 2");
        writer.println("force");
        discardAllInput();
        writer.println("setboard " + FEN_DRAW_1_0);
        writer.println("usermove h4h1");
        writer.println("go");
        List<String> list = readAllInput();
        assertEquals(2, list.size());
        assertEquals("move f3h1", list.get(0));
        assertEquals("1/2-1/2 {Stalemate}", list.get(1));
        writer.println("quit");
    }

    /**
     * Tests draw by the user.
     */
    @Test
    public void testUserDraws() throws Exception {
        writer.println("xboard");
        writer.println("protover 2");
        writer.println("force");
        discardAllInput();
        writer.println("setboard " + FEN_DRAW_1_1);
        writer.println("usermove f3h1");
        List<String> list = readAllInput();
        assertEquals(1, list.size());
        assertEquals("1/2-1/2 {Stalemate}", list.get(0));
        writer.println("quit");
    }

    /**
     * Tests moving when in draw.
     */
    @Test
    public void testWhenInDraw() throws Exception {
        writer.println("xboard");
        writer.println("protover 2");
        writer.println("force");
        discardAllInput();
        writer.println("setboard " + FEN_DRAW_1_2);
        writer.println("usermove a7a8");
        List<String> list = readAllInput();
        assertEquals(1, list.size());
        assertEquals("Error (draw): usermove", list.get(0));
        writer.println("quit");
    }

    // ------------------------------------------------------------------------

    /**
     * Reads all lines of input that is available from the {@code reader}, and returns this as a list of strings.
     */
    private List<String> readAllInput() throws Exception {
        List<String> list = new ArrayList<>();

        // Assume there will be some input
        while (!reader.ready() && process.isAlive()) {
            Thread.sleep(10);
        }
        while (reader.ready()) {
            Thread.sleep(20);
            list.add(reader.readLine());
        }

        // Fail if we discover an exception
        if (containsRegex("Exception", list)) {
            fail("Engine exception: " + list);
        }

        return list;
    }

    /**
     * Reads all lines of input that is available from the {@code reader}, and throws them away.
     */
    private void discardAllInput() throws Exception {
        assertNotNull(readAllInput());
    }

    /**
     * Asserts that a game log file is written by the engine within the given timeout.
     */
    private void assertGameLogFileWritten(long timeout, TimeUnit unit) throws Exception {
        waitForSupplier(() -> Files.size(gameLogFile.toPath()) > 0, timeout, unit);
    }
}
