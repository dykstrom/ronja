/*
 * Copyright (C) 2021 Johan Dykstrom
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

package se.dykstrom.ronja.common.book;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.CanParser;
import se.dykstrom.ronja.common.parser.IllegalMoveException;
import se.dykstrom.ronja.common.parser.MoveParser;
import se.dykstrom.ronja.common.parser.SanParser;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A class that can be used to convert the opening book between CAN and SAN format.
 */
@SuppressWarnings("unused")
@Command(name = "OpeningBookConverter",
         mixinStandardHelpOptions = true,
         version = "OpeningBookConverter 0.1.0",
         description = "Converts the Ronja opening book to SAN or CAN format.")
public class OpeningBookConverter implements Callable<Integer> {

    public enum Notation { CAN, SAN }

    @Option(names = {"-f"}, description = "Read input from FILENAME.", paramLabel = "FILENAME", required = true)
    private File inputFile;

    @Option(names = {"-o"},
            description = "Write output to FILENAME. If not specified, output will be written to stdout.",
            paramLabel = "FILENAME")
    private File outputFile;

    @Option(names = {"-n"},
            description = "Write output in NOTATION. Must be one of CAN or SAN.",
            paramLabel = "NOTATION",
            required = true)
    private Notation notation;

    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {
        if (!inputFile.canRead()) {
            spec.commandLine().getErr().println("Cannot open input file: " + inputFile);
            return ExitCode.USAGE;
        }

        try {
            convertOpeningBook();
        } catch (IOException | IllegalMoveException | ParseException e) {
            spec.commandLine().getErr().println("Cannot convert opening book: " + e.getMessage());
            return ExitCode.SOFTWARE;
        }

        return ExitCode.OK;
    }

    private void convertOpeningBook() throws IOException, IllegalMoveException, ParseException {
        final List<String> inputLines = Files.readAllLines(inputFile.toPath(), UTF_8);
        final List<String> outputLines = new ArrayList<>();
        for (String inputLine : inputLines) {
            outputLines.add(convertOpeningLine(inputLine));
        }

        if (outputFile == null) {
            outputLines.forEach(spec.commandLine().getOut()::println);
        } else {
            Files.write(outputFile.toPath(), outputLines, UTF_8);
        }
    }

    private String convertOpeningLine(final String line) throws IllegalMoveException, ParseException {
        if (line.isBlank() || line.startsWith("#")) {
            return line;
        } else {
            final var fields = line.split(";", -1);
            if (fields.length != 3) {
                throw new ParseException("Error on line '" + line + "': syntax error", 0);
            }
            try {
                return convertFields(fields);
            } catch (IllegalMoveException e) {
                throw new IllegalMoveException("Error on line '" + line + "': " + e.getMessage());
            }
        }
    }

    private String convertFields(final String[] fields) throws IllegalMoveException {
        final String[] inputMoves = fields[0].strip().split("\\s+");
        final String inputBookMove = fields[1].strip();
        final String inputComment = fields[2].strip();

        Position position = Position.START;

        final List<String> outputMoves = new ArrayList<>();
        for (final String inputMove : inputMoves) {
            if (!inputMove.isBlank()) {
                final int parsedMove = parseMove(inputMove, position);
                final String formattedMove = formatMove(parsedMove, position);
                outputMoves.add(formattedMove);
                position = position.withMove(parsedMove);
            }
        }

        final BookMove parsedBookMove = parseBookMove(inputBookMove, position);
        final String outputBookMove = formatBookMove(parsedBookMove, position);

        return String.join(" ", outputMoves) + ";" + outputBookMove + ";" + inputComment;
    }

    private BookMove parseBookMove(final String inputBookMove, final Position position) throws IllegalMoveException {
        final String[] moveAndWeight = inputBookMove.split(("/"));
        return new BookMove(MoveParser.parse(moveAndWeight[0], position), Integer.parseInt(moveAndWeight[1]));
    }

    private String formatBookMove(final BookMove bookMove, final Position position) {
        return formatMove(bookMove.move(), position) + "/" + bookMove.weight();
    }

    private int parseMove(final String inputMove, final Position position) throws IllegalMoveException {
        return MoveParser.parse(inputMove, position);
    }

    private String formatMove(final int move, final Position position) {
        return notation == Notation.CAN ? CanParser.format(move) : SanParser.format(position, move);
    }

    public int execute(final String[] args) {
        return new CommandLine(this).execute(args);
    }

    public static void main(String[] args) {
        System.exit(new OpeningBookConverter().execute(args));
    }
}
