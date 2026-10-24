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

package se.dykstrom.ronja.engine.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.dykstrom.ronja.test.TestUtils;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static se.dykstrom.ronja.test.TestUtils.assertContainsRegex;

/**
 * Integration tests for the unpacked platform archive. They start the engine through
 * the launcher, the way a chess GUI does, and check that it runs on the bundled runtime.
 *
 * @author Johan Dykstrom
 */
public class LauncherIT {

    private static final String JAVA_HOME_PREFIX = "# Java home ";
    private static final String LOG_FOLDER_PREFIX = "# Log folder ";

    private static final boolean WINDOWS = System.getProperty("os.name").startsWith("Windows");

    private Path imageDir;
    private Path homeDir;
    private Path stateDir;

    private Process process;

    private BufferedReader reader;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        imageDir = Path.of(System.getProperty("ronja.image.dir")).toRealPath();

        final var builder = WINDOWS
                ? new ProcessBuilder("cmd.exe", "/c", imageDir.resolve("ronja.bat").toString())
                : new ProcessBuilder(imageDir.resolve("ronja").toString());
        // The launcher must not depend on a Java installed on the machine
        builder.environment().remove("JAVA_HOME");

        // Give the engine a home folder of its own, so the test can find the files it writes
        homeDir = Files.createTempDirectory("LauncherIT_");
        if (WINDOWS) {
            builder.environment().put("LOCALAPPDATA", homeDir.toString());
            stateDir = homeDir.resolve("Ronja");
        } else {
            builder.environment().put("HOME", homeDir.toString());
            builder.environment().remove("XDG_STATE_HOME");
            stateDir = homeDir.resolve(".local").resolve("state").resolve("ronja");
        }

        process = builder.directory(imageDir.toFile()).redirectErrorStream(true).start();
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        writer = new PrintWriter(process.getOutputStream(), true);
    }

    @AfterEach
    public void tearDown() throws Exception {
        // On Windows, destroying the process stops only cmd.exe, so ask the engine to quit first
        writer.println("quit");
        process.waitFor(10, SECONDS);
        process.destroyForcibly();
        reader.close();
        writer.close();

        try (final var paths = Files.walk(homeDir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
        }
    }

    @Test
    public void shouldRunOnBundledRuntime() throws Exception {
        final List<String> list = TestUtils.readAllInput(reader);
        final var javaHome = getValue(JAVA_HOME_PREFIX, list);
        assertEquals(imageDir.resolve("runtime").toRealPath(), Path.of(javaHome).toRealPath());
    }

    @Test
    public void shouldAnswerInitSequenceAndQuit() throws Exception {
        TestUtils.readAllInput(reader);
        writer.println("xboard");
        writer.println("protover 2");
        final List<String> list = TestUtils.readAllInput(reader);
        assertContainsRegex("feature done=1", list);
        writer.println("quit");
        assertTrue(process.waitFor(10, SECONDS), "The engine did not quit");
    }

    @Test
    public void shouldLogToFolderPerUser() throws Exception {
        // The rejected command logs a warning, and the unique feature name finds it in the log
        final var feature = "feature" + UUID.randomUUID();
        final var logFolder = getValue(LOG_FOLDER_PREFIX, TestUtils.readAllInput(reader));
        assertEquals(stateDir.toRealPath(), Path.of(logFolder).toRealPath());
        writer.println("xboard");
        writer.println("rejected " + feature);
        writer.println("quit");
        assertTrue(process.waitFor(10, SECONDS), "The engine did not quit");

        final var log = Files.readString(stateDir.resolve("ronja.log"));
        assertTrue(log.contains(feature), "No warning in log: " + log);
        // The launcher runs outside the install folder, so the book must still be found there
        assertFalse(log.contains("Failed to load opening book"), "Book not loaded: " + log);

        final var installLog = imageDir.resolve("ronja.log");
        assertFalse(Files.exists(installLog) && Files.readString(installLog).contains(feature),
                "Engine logged to the install folder");
    }

    @Test
    public void shouldBundleOnlyRequiredModules() throws Exception {
        final List<String> lines = Files.readAllLines(imageDir.resolve("runtime").resolve("release"));
        assertContainsRegex("^MODULES=\"java.base java.logging\"$", lines);
    }

    /**
     * Returns the rest of the first line in {@code list} that starts with {@code prefix}.
     */
    private static String getValue(final String prefix, final List<String> list) {
        return list.stream()
                .filter(line -> line.startsWith(prefix))
                .map(line -> line.substring(prefix.length()))
                .findFirst()
                .orElseGet(() -> fail("No line starting with '" + prefix + "' in engine output: " + list));
    }
}
