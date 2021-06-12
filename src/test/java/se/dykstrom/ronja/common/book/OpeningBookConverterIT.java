package se.dykstrom.ronja.common.book;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;
import picocli.CommandLine;

import static org.junit.Assert.assertEquals;

public class OpeningBookConverterIT {

    private static final File OPENING_BOOK = new File("target/scripts/book.csv");

    @Test
    public void shouldConvertAndCompareOpeningBook() throws Exception {
        // Given
        final Path path = Files.createTempFile(null, null);
        final File file = path.toFile();
        file.deleteOnExit();

        final String[] args = {
                "-f", OPENING_BOOK.getPath(),
                "-o", path.toString(),
                "-n", "CAN"
        };
        final var openingBookConverter = new OpeningBookConverter();

        // When
        final var exitCode = openingBookConverter.execute(args);

        // Then
        assertEquals(CommandLine.ExitCode.OK, exitCode);
        assertFileEquals(OPENING_BOOK, file);
    }

    @SuppressWarnings("SameParameterValue")
    private void assertFileEquals(final File expectedFile, final File actualFile) throws IOException {
        final List<String> expectedLines = Files.readAllLines(expectedFile.toPath());
        final List<String> actualLines = Files.readAllLines(actualFile.toPath());
        assertEquals(expectedLines, actualLines);
    }
}
