/*
 * Copyright (C) 2018 Johan Dykstrom
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
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static se.dykstrom.ronja.test.TestUtils.assertContainsRegex;
import static se.dykstrom.ronja.test.TestUtils.containsRegex;

/**
 * This class is for integration testing class {@code Ronja}.
 *
 * @author Johan Dykstrom
 * @see Ronja
 */
public class RonjaIT {

    private final InputStream stdin = System.in;
    private final PrintStream stdout = System.out;

    private Thread thread;
    private PrintStream commandStream;
    private PipedInputStream responseStream;

    @Before
    public void setUp() throws Exception {
        PipedInputStream redirectedStdin = new PipedInputStream();
        commandStream = new PrintStream(new PipedOutputStream(redirectedStdin), true, StandardCharsets.ISO_8859_1);
        System.setIn(redirectedStdin);

        responseStream = new PipedInputStream();
        PrintStream redirectedStdout = new PrintStream(new PipedOutputStream(responseStream), true, StandardCharsets.ISO_8859_1);
        System.setOut(redirectedStdout);

        thread = new Thread(new Engine());
        thread.start();
    }

    @After
    public void tearDown() throws Exception {
        thread.join(1000);

        // Restore System.in and System.out
        System.setIn(stdin);
        System.setOut(stdout);
    }

    @Test
    public void shouldQuit() {
        commandStream.println("quit");
    }

    @Test
    public void testInitSequence() throws Exception {
        commandStream.println("xboard");
        commandStream.println("protover 2");
        List<String> list = readAllInput();
        assertContainsRegex("feature done=1", list);
        commandStream.println("new");
        commandStream.println("quit");
    }

    @Test
    public void shouldAnswerPong() throws Exception {
        commandStream.println("xboard");
        commandStream.println("ping 17");
        List<String> list = readAllInput();
        assertContainsRegex("pong 17", list);
        commandStream.println("quit");
    }

    @Test
    public void shouldPlaySingleMove() throws Exception {
        commandStream.println("xboard");
        commandStream.println("protover 2");
        discardAllInput();
        commandStream.println("new");
        commandStream.println("usermove e2e4");
        List<String> list = readAllInput();
        assertContainsRegex("move e7(e5|e6)", list);
        commandStream.println("quit");
    }

    /**
     * Reads all lines of input that is available from {@code stdin}, and returns this as a list of strings.
     */
    private List<String> readAllInput() throws Exception {
        List<String> list = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.ISO_8859_1));

        // Assume there will be some input
        while (!reader.ready()) {
            Thread.sleep(100);
        }
        while (reader.ready()) {
            list.add(reader.readLine());
            Thread.sleep(100);
        }

        // Fail if we discover an exception
        if (containsRegex("Exception", list)) {
            fail("Engine exception: " + list);
        }

        return list;
    }

    /**
     * Reads all lines of input that is available from {@code stdin}, and throws them away.
     */
    private void discardAllInput() throws Exception {
        assertNotNull(readAllInput());
    }

    private static class Engine implements Runnable {
        @Override
        public void run() {
            try {
                Ronja.main(new String[]{});
            } catch (IOException e) {
                fail("Caught exception: " + e);
            }
        }
    }
}
