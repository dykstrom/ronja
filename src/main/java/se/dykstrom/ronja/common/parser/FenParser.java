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

package se.dykstrom.ronja.common.parser;

import static se.dykstrom.ronja.common.model.Piece.KING;
import static se.dykstrom.ronja.common.model.Piece.QUEEN;

import java.text.ParseException;

import se.dykstrom.ronja.common.model.*;
import se.dykstrom.ronja.engine.core.AttackGenerator;

/**
 * A class that can parse and format positions specified in Forsyth-Edwards Notation (FEN).
 *
 * @author Johan Dykstrom
 */
public class FenParser {

    private static final AttackGenerator ATTACK_GENERATOR = new AttackGenerator();

    // -----------------------------------------------------------------------
    // Formatting:
    // -----------------------------------------------------------------------

    /**
     * Formats the given {@code position} in FEN format.
     */
    public static String format(Position position) {
        StringBuilder builder = new StringBuilder();

        // Field 1 - piece placement
        builder.append(formatPieces(position));
        builder.append(" ");

        // Field 2 - active color
        builder.append(formatActiveColor(position));
        builder.append(" ");

        // Field 3 - castling availability
        builder.append(formatCastlingAvailability(position));
        builder.append(" ");

        // Field 4 - 'en passant' target square
        builder.append(formatEnPassantSquare(position));
        builder.append(" ");

        // Field 5 - half move clock
        builder.append(formatHalfMoveClock(position));
        builder.append(" ");

        // Field 6 - full move number
        builder.append(formatFullMoveNumber(position));

        return builder.toString();
    }

    /**
     * Formats the piece placement in the given position.
     */
    private static String formatPieces(Position position) {
        StringBuilder builder = new StringBuilder();

        for (int r = 8; r > 0; r--) {
            builder.append(formatRank(r, position));
            builder.append("/");
        }
        builder.setLength(builder.length() - 1);

        return builder.toString();
    }

    /**
     * Formats rank number {@code r} in the given {@code position}.
     */
    private static String formatRank(int r, Position position) {
        StringBuilder builder = new StringBuilder();

        // Number of consecutive empty squares
        int noOfEmptyInARow = 0;

        for (Long square : Board.getSquaresInRank(r)) {
            int piece = position.getPiece(square);
            Color color = position.getColor(square);
            if (piece == 0) {
                noOfEmptyInARow++;
            } else {
                if (noOfEmptyInARow != 0) {
                    builder.append(noOfEmptyInARow);
                    noOfEmptyInARow = 0;
                }
                builder.append(Piece.toSymbol(piece, color));
            }
        }
        if (noOfEmptyInARow != 0) {
            builder.append(noOfEmptyInARow);
        }

        return builder.toString();
    }

    /**
     * Formats the active color in the given position.
     */
    private static char formatActiveColor(Position position) {
        return position.getActiveColor().getSymbol();
    }

    /**
     * Formats the castling availability for the given position.
     */
    private static String formatCastlingAvailability(Position position) {
        StringBuilder builder = new StringBuilder();

        if (position.isKingSideCastlingAllowed(Color.WHITE)) {
            builder.append("K");
        }
        if (position.isQueenSideCastlingAllowed(Color.WHITE)) {
            builder.append("Q");
        }
        if (position.isKingSideCastlingAllowed(Color.BLACK)) {
            builder.append("k");
        }
        if (position.isQueenSideCastlingAllowed(Color.BLACK)) {
            builder.append("q");
        }
        if (builder.length() == 0) {
            builder.append("-");
        }

        return builder.toString();
    }

    /**
     * Formats the 'en passant' target square for the given position.
     */
    private static String formatEnPassantSquare(Position position) {
        if (position.getEnPassantSquare() != 0) {
            return Square.idToName(position.getEnPassantSquare());
        } else {
            return "-";
        }
    }

    /**
     * Formats the half move clock in the given position.
     */
    private static int formatHalfMoveClock(Position position) {
        return position.getHalfMoveClock();
    }

    /**
     * Formats the full move number in the given position.
     */
    private static int formatFullMoveNumber(Position position) {
        return position.getFullMoveNumber();
    }

    // -----------------------------------------------------------------------
    // Parsing:
    // -----------------------------------------------------------------------

    /**
     * Parses the given position specified in FEN format, and returns a {@code Position} object.
     */
    public static Position parse(String fen) throws ParseException {
        Position position = Position.START;

        String[] fields = fen.split(" ");
        if (fields.length != 6) {
            throw new ParseException("invalid number of fields (" + fields.length + " != 6)", 0);
        }

        try {
            // Field 1 - piece placement
            position = parsePieces(fields[0], position);

            // Field 2 - active color
            position = parseActiveColor(fields[1], position);

            // Field 3 - castling availability
            position = parseCastlingAvailability(fields[2], position);

            // Field 4 - 'en passant' target square
            position = parseEnPassantSquare(fields[3], position);

            // Field 5 - half move clock
            position = parseHalfMoveClock(fields[4], position);

            // Field 6 - full move number
            position = parseFullMoveNumber(fields[5], position);

            // Update attack bitboards in the parsed position
            position = updateAttackBitboards(position);
        } catch (ParseException pe) {
            throw pe;
        } catch (Exception e) {
            throw new ParseException("failed to parse FEN '" + fen + "': " + e.getMessage(), 0);
        }

        return position;
    }

    /**
     * Parses the piece placement sub string.
     */
    private static Position parsePieces(String s, Position position) throws ParseException {
        String[] ranks = s.split("/");
        if (ranks.length != 8) {
            throw new ParseException("invalid number of ranks (" + ranks.length + " != 8)", 0);
        }

        for (int r = 8; r > 0; r--) {
            position = parseRank(r, ranks[8 - r], position);
        }
        return position;
    }

    /**
     * Parses a single rank in the piece placement sub string.
     *
     * @param r The number of the rank (1-8).
     * @param rank The string to parse for this rank.
     * @param position The position to update with pieces.
     * @return The updated position.
     */
    private static Position parseRank(int r, String rank, Position position) {
        int f = 1;
        for (int i = 0; i < rank.length(); i++) {
            String s = rank.substring(i, i + 1);
            if (s.matches("[BKNPQR]")) {
                position = withPieceAndColor(Piece.valueOf(s.charAt(0)), Color.WHITE, f, r, position);
                f++;
            } else if (s.matches("[bknpqr]")) {
                position = withPieceAndColor(Piece.valueOf(s.charAt(0)), Color.BLACK, f, r, position);
                f++;
            } else {
                int numberOfEmptyInARow = Integer.parseInt(s);
                while (numberOfEmptyInARow-- > 0) {
                    position = withPieceAndColor(0, null, f, r, position);
                    f++;
                }
            }
        }
        return position;
    }

    /**
     * Returns an updated position, with the given {@code piece} and {@code color} at file {@code f} and rank {@code r}.
     */
    private static Position withPieceAndColor(int piece, Color color, int f, int r, Position position) {
        return position.withPieceAndColor(Board.getSquareId(f, r), piece, color);
    }

    /**
     * Parses the active color sub string.
     */
    private static Position parseActiveColor(String s, Position position) {
        return position.withActiveColor(Color.valueOf(s.charAt(0)));
    }

    /**
     * Parses the castling availability sub string.
     */
    private static Position parseCastlingAvailability(String s, Position position) {
        return position.withKingSideCastlingAllowed(Color.WHITE, s.indexOf(Piece.toSymbol(KING, Color.WHITE)) != -1)
                       .withKingSideCastlingAllowed(Color.BLACK, s.indexOf(Piece.toSymbol(KING, Color.BLACK)) != -1)
                       .withQueenSideCastlingAllowed(Color.WHITE, s.indexOf(Piece.toSymbol(QUEEN, Color.WHITE)) != -1)
                       .withQueenSideCastlingAllowed(Color.BLACK, s.indexOf(Piece.toSymbol(QUEEN, Color.BLACK)) != -1);
    }

    /**
     * Parses the 'en passant' target square sub string.
     */
    private static Position parseEnPassantSquare(String s, Position position) {
        if (s.startsWith("-")) {
            return position.withEnPassantSquare(0);
        } else {
            return position.withEnPassantSquare(Square.nameToId(s));
        }
    }

    /**
     * Parses the half move clock sub string.
     */
    private static Position parseHalfMoveClock(String s, Position position) {
        return position.withHalfMoveClock(Integer.parseInt(s));
    }

    /**
     * Parses the full move number sub string.
     */
    private static Position parseFullMoveNumber(String s, Position position) {
        return position.withFullMoveNumber(Integer.parseInt(s));
    }

    /**
     * Returns a position with the attack bitboards updated.
     */
    private static Position updateAttackBitboards(Position position) {
        return position.withAttackBitboards(ATTACK_GENERATOR.getAttackedSquares(Color.WHITE, position),
                                            ATTACK_GENERATOR.getAttackedSquares(Color.BLACK, position));
    }
}
