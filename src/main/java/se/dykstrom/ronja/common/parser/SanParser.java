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

import se.dykstrom.ronja.common.model.*;
import se.dykstrom.ronja.engine.core.FullMoveGenerator;
import se.dykstrom.ronja.engine.core.MoveGenerator;
import se.dykstrom.ronja.engine.utils.PositionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toSet;
import static se.dykstrom.ronja.common.model.Board.*;

/**
 * A class that can parse and format moves specified in Standard Algebraic Notation (SAN).
 *
 * @author Johan Dykstrom
 */
public class SanParser extends AbstractMoveParser {

    private static final Logger TLOG = Logger.getLogger(SanParser.class.getName());

    private static final MoveGenerator MOVE_GENERATOR = new FullMoveGenerator();

    /**
     * Returns {@code true} if the given string of characters is a syntactically
     * valid chess move in standard algebraic notation.
     *
     * @param move The move to validate.
     * @return True if the given string is a syntactically valid move.
     */
    public static boolean isMove(String move) {
        return move.matches("[BKNRQ][a-h]?[1-8]?[x]?[a-h][1-8][+#]?") ||   // Piece move
               move.matches("([a-h][x])?[a-h][2-7][+#]?") ||            // Pawn move
               move.matches("([a-h][x])?[a-h][18][=][BNRQ][+#]?") ||    // Promotion
               move.matches("O-O(-O)?[+#]?");                           // Castling
    }

    // -----------------------------------------------------------------------
    // Parsing:
    // -----------------------------------------------------------------------

    /**
	 * Parses and validates a move specified in SAN format in the given {@code Position}.
	 *
	 * TODO: - Finding the from square
	 *       - Nbc3 (the case where two pieces can move to the same square)
	 *       - exd5 (pawn captures)
	 *       - En passant captures
	 *       - Verify checks and check mates
	 *
	 * @param move The move in SAN format.
	 * @param position The position when the move is made.
	 * @throws IllegalMoveException If the given string cannot be parsed as a legal move in SAN format.
	 */
    public static Move parse(String move, Position position) throws IllegalMoveException {
		TLOG.finest("move: " + move + " position: \n" + position);
        long from = 0, to = 0;
        Piece promoted = null;

		if (isKingSideCastling(move)) {
            switch (position.getActiveColor()) {
                case WHITE:
                    from = Square.E1;
                    to = Square.G1;
                    break;
                case BLACK:
                    from = Square.E8;
                    to = Square.G8;
                    break;
            }
		}

		else if (isQueenSideCastling(move)) {
            switch (position.getActiveColor()) {
                case WHITE:
                    from = Square.E1;
                    to = Square.C1;
                    break;
                case BLACK:
                    from = Square.E8;
                    to = Square.C8;
                    break;
            }
        }

		// Normal move
		else {
			// True if the move is a capture
			boolean capture = false;

			// Get piece
			if (move.charAt(0) < 'Z') {
				// Remove piece name
				move = move.substring(1);

				// Remove 'x' - not used?
				if (move.startsWith("x")) {
					move = move.substring(1);
					capture = true;
				}
			}

			// Or pawn
			else {
				// Pawn capture
				if (move.contains("x")) {
					// TODO: Save the from file before removing it from move
					move = move.substring(2);
					capture = true;
				}
			}

			// Get to square
            to = Square.nameToId(move.substring(0, 2));

			// If capture, verify that there is a piece to capture
			if (capture) {
				Piece capturedPiece = position.getPiece(to);
				Color capturedColor = position.getColor(to);

				// The to square must contain a piece,
				// it must not be the king,
				// and it must be of the opposite color
				// TODO: En passant captures
				if ((capturedPiece == null) ||
					(capturedPiece == Piece.KING) ||
					(capturedColor == position.getActiveColor())) {
					throw new IllegalMoveException("invalid capture");
				}
			}

			// TODO: Remove '+' - not used?
			if (move.endsWith("+")) {
				move = move.substring(0, move.length() - 1);
				// TODO: Verify that there is a check
			}

			// TODO: Remove '#' - not used?
			if (move.endsWith("#")) {
				move = move.substring(0, move.length() - 1);
				// TODO: Verify that there is a check mate
			}

			// Get from square
			// TODO: Get from square

			// Promotion
			if (move.contains("=")) {
                promoted = getPromotionPiece(position, from, move.charAt(move.indexOf("=") + 1));
			}
		}

        Piece piece = position.getPiece(from);
        boolean isCastling = isCastling(position, from, to);
        boolean isEnPassant = isEnPassant(position, from, to);

        validate(position, from, to, isCastling);

        // Create the new move
        return Move.of(piece, from, to, promoted, isCastling, isEnPassant);
	}

    private static boolean isQueenSideCastling(String move) {
        return move.equals("O-O-O");
    }

    private static boolean isKingSideCastling(String move) {
        return move.equals("O-O");
    }

    // -----------------------------------------------------------------------
    // Formatting:
    // -----------------------------------------------------------------------

    /**
     * Formats the given list of moves in SAN format in the context of the given start position.
     *
     * @param moves The list of moves to format.
     * @param startPosition The position of the first move.
     * @return The formatted moves.
     */
    public static List<String> format(List<Move> moves, Position startPosition) {
        List<String> formattedMoves = new ArrayList<>(moves.size());
        Position position = startPosition;
        for (Move move : moves) {
            formattedMoves.add(format(move, position));
            position = position.withMove(move);
        }
        return formattedMoves;
    }

    /**
     * Formats the given move in SAN format in the context of the given position.
     *
     * @param move The move to format.
     * @param position The current position.
     * @return The formatted move.
     */
    public static String format(Move move, Position position) {
        if (move.isCastling()) {
            return formatCastlingMove(move);
        } else if (move.getPiece() == Piece.PAWN) {
            return formatPawnMove(move, position);
        } else {
            return formatPieceMove(move, position);
        }
    }

    private static String formatCastlingMove(Move move) {
        if (move.getTo() == Square.G1 || move.getTo() == Square.G8) {
            return "O-O";
        } else {
            return "O-O-O";
        }
    }

    private static String formatPawnMove(Move move, Position position) {
        String fromFile = formatFromFile(move, position);
        String capture = formatCapture(move, position);
        String toSquare = formatToSquare(move);
        String promotion = formatPromotion(move);
        String check = formatCheck(move, position);
        return fromFile + capture + toSquare + promotion + check;
    }

    private static String formatPieceMove(Move move, Position position) {
        String piece = Character.toString(move.getPiece().getSymbol());
        String fromFileOrRank = fileOrRank(move, position);
        String capture = formatCapture(move, position);
        String toSquare = formatToSquare(move);
        String check = formatCheck(move, position);
        return piece + fromFileOrRank + capture + toSquare + check;
    }

    /**
     * Formats the file or rank specifier for a piece move. This is used when more than one piece of this type
     * can move to the to square.
     */
    private static String fileOrRank(Move move, Position position) {
        // Find all squares that a piece of this type could have started from to end up at this to square
        Set<Long> allFromSquares = getAllFromSquares(move.getPiece(), move.getTo(), position);

        // If there is more than one legal from square, that is, more than one piece that could move to this to square,
        // we must specify the file or rank of the real from square
        if (allFromSquares.size() > 1) {
            long fromSquare = move.getFrom();
            if (isFileUnique(fromSquare, allFromSquares)) {
                return Character.toString(getFileChar(fromSquare));
            } else if (isRankUnique(fromSquare, allFromSquares)) {
                return Integer.toString(getRank(fromSquare));
            } else {
                return Character.toString(getFileChar(fromSquare)) + Integer.toString(getRank(fromSquare));
            }
        } else {
            return "";
        }
    }

    /**
     * Returns all possible squares, from which a piece of the given type could move to the given to square,
     * in this position.
     *
     * @param piece The piece to move.
     * @param toSquare The square to move to.
     * @param position The current position.
     * @return All possible from squares.
     */
    static Set<Long> getAllFromSquares(Piece piece, long toSquare, Position position) {
        Iterable<Move> iterable = () -> MOVE_GENERATOR.iterator(position);
        return StreamSupport.stream(iterable.spliterator(), false)
                .filter(move -> move.getPiece() == piece)
                .filter(move -> move.getTo() == toSquare)
                .map(Move::getFrom)
                .collect(toSet());
    }

    /**
     * Returns {@code true} if the file of {@code fromSquare} is unique within all from squares.
     */
    static boolean isFileUnique(Long fromSquare, Set<Long> allFromSquares) {
        int fromFile = getFile(fromSquare);
        return allFromSquares.stream()
                .filter(square -> !square.equals(fromSquare))
                .map(Board::getFile)
                .noneMatch(file -> file == fromFile);
    }

    /**
     * Returns {@code true} if the rank of {@code fromSquare} is unique within all from squares.
     */
    static boolean isRankUnique(Long fromSquare, Set<Long> allFromSquares) {
        int fromRank = getRank(fromSquare);
        return allFromSquares.stream()
                .filter(square -> !square.equals(fromSquare))
                .map(Board::getRank)
                .noneMatch(rank -> rank == fromRank);
    }

    /**
     * Formats the from file for a pawn move. Returns the from file for capture moves,
     * or an empty string for normal moves.
     */
    private static String formatFromFile(Move move, Position position) {
        return isCapture(move, position) ? Character.toString(getFileChar(move.getFrom())) : "";
    }

    private static String formatCapture(Move move, Position position) {
        return isCapture(move, position) ? "x" : "";
    }

    private static String formatToSquare(Move move) {
        return Square.idToName(move.getTo());
    }

    private static String formatPromotion(Move move) {
        if (move.isPromotion()) {
            return "=" + Character.toString(move.getPromoted().getSymbol());
        } else {
            return "";
        }
    }

    private static String formatCheck(Move move, Position position) {
        Position newPosition = position.withMove(move);
        if (PositionUtils.isCheckMate(newPosition)) {
            return "#";
        } else if (newPosition.isCheck(newPosition.getActiveColor())) {
            return "+";
        }
        return "";
    }

    /**
     * Returns {@code true} if the given move is a capture.
     */
    private static boolean isCapture(Move move, Position position) {
        return position.getPiece(move.getTo()) != null || move.isEnPassant();
    }
}
