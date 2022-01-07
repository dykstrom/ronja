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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.dykstrom.ronja.test.TestUtils;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static se.dykstrom.ronja.test.TestUtils.assertContainsRegex;

/**
 * This class is for integration testing class {@code Ronja}.
 *
 * @author Johan Dykstrom
 * @see Ronja
 */
public class RonjaIT {

    private final InputStream stdin = System.in;
    private final PrintStream stdout = System.out;

    private PrintStream commandStream;
    private BufferedReader responseReader;
    private Future<Void> engineFuture;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Before
    public void setUp() throws Exception {
        PipedInputStream redirectedStdin = new PipedInputStream();
        commandStream = new PrintStream(new PipedOutputStream(redirectedStdin), true, ISO_8859_1);
        System.setIn(redirectedStdin);

        PipedInputStream responseStream = new PipedInputStream();
        responseReader = new BufferedReader(new InputStreamReader(responseStream, ISO_8859_1));
        PrintStream redirectedStdout = new PrintStream(new PipedOutputStream(responseStream), true, ISO_8859_1);
        System.setOut(redirectedStdout);

        engineFuture = executorService.submit(() -> {
            Ronja.main(new String[]{});
            return null;
        });
    }

    @After
    public void tearDown() throws Exception {
        assertNull(engineFuture.get());
        executorService.shutdown();

        // Restore System.in and System.out
        System.setIn(stdin);
        System.setOut(stdout);
    }

    @Test
    public void shouldQuit() throws Exception {
        commandStream.println("quit");
        List<String> list = readAllInput();
        assertContainsRegex("# Ronja", list);
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

    private List<String> readAllInput() throws Exception {
        return TestUtils.readAllInput(responseReader);
    }

    private void discardAllInput() throws Exception {
        assertNotNull(readAllInput());
    }
}
