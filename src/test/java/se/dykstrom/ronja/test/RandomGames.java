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

package se.dykstrom.ronja.test;

import se.dykstrom.ronja.common.model.Position;
import se.dykstrom.ronja.common.parser.FenParser;
import se.dykstrom.ronja.common.parser.SanParser;
import se.dykstrom.ronja.engine.core.FullMoveGenerator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates and plays random legal games. A game starts from one of the seed positions, and a list
 * of choices picks its moves. Each choice, modulo the number of legal moves, picks the next move.
 * The game stops early at mate or stalemate. {@link RandomGameRunner} uses this class to check
 * properties of many games, and to shrink a failing game.
 *
 * @author Johan Dykstrom
 */
public final class RandomGames {

    /**
     * The start position and the perft positions. In each of them, a castling right is set only if its king and
     * rook are at home.
     */
    static final List<String> SEED_FENS = List.of(
            AbstractTestCase.FEN_START,
            AbstractTestCase.FEN_PERFT_KIWIPETE,
            AbstractTestCase.FEN_PERFT_3,
            AbstractTestCase.FEN_PERFT_4,
            AbstractTestCase.FEN_PERFT_5,
            AbstractTestCase.FEN_PERFT_6
    );

    /** The maximum number of plies in a game. Game lengths are spread evenly up to this number. */
    static final int MAX_PLIES = 150;

    /** The largest choice. Choices are taken modulo the number of legal moves, so this only limits the report size. */
    static final int MAX_CHOICE = 255;

    private RandomGames() { }

    /**
     * Returns a random game input: a seed position, and between 0 and {@link #MAX_PLIES} choices.
     */
    static GameInput randomInput(Random random) {
        int seedIndex = random.nextInt(SEED_FENS.size());
        int length = random.nextInt(MAX_PLIES + 1);
        List<Integer> choices = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            choices.add(random.nextInt(MAX_CHOICE + 1));
        }
        return new GameInput(seedIndex, List.copyOf(choices));
    }

    /**
     * Plays the game that {@code input} describes.
     */
    public static RandomGame play(GameInput input) {
        String seedFen = SEED_FENS.get(input.seedIndex());
        FullMoveGenerator generator = new FullMoveGenerator();
        Position position = parse(seedFen);
        List<Integer> moves = new ArrayList<>();
        List<Position> positions = new ArrayList<>();
        List<List<Integer>> legalMoves = new ArrayList<>();

        positions.add(position);
        legalMoves.add(legalMoves(position, generator));
        for (int choice : input.choices()) {
            List<Integer> legal = legalMoves.getLast();
            if (legal.isEmpty()) {
                break;
            }
            int move = legal.get(choice % legal.size());
            position = position.withMove(move);
            moves.add(move);
            positions.add(position);
            legalMoves.add(legalMoves(position, generator));
        }
        return new RandomGame(seedFen, List.copyOf(moves), List.copyOf(positions), List.copyOf(legalMoves));
    }

    /**
     * Returns the legal moves in {@code position}. The generator makes pseudo-legal moves,
     * so this method drops every move that leaves the mover's king in check.
     */
    private static List<Integer> legalMoves(Position position, FullMoveGenerator generator) {
        List<Integer> legal = new ArrayList<>();
        int numberOfMoves = generator.generateMoves(position, 0);
        for (int i = 0; i < numberOfMoves; i++) {
            int move = generator.moves[0][i];
            if (!position.withMove(move).isIllegalCheck()) {
                legal.add(move);
            }
        }
        return List.copyOf(legal);
    }

    private static Position parse(String fen) {
        try {
            return FenParser.parse(fen);
        } catch (ParseException e) {
            throw new IllegalArgumentException("invalid seed FEN: " + fen, e);
        }
    }

    /**
     * The input that describes a random game.
     *
     * @param seedIndex The index of the seed position in {@link #SEED_FENS}.
     * @param choices The choices that pick the moves.
     */
    public record GameInput(int seedIndex, List<Integer> choices) { }

    /**
     * A random legal game.
     *
     * @param seedFen The FEN of the position the game starts from.
     * @param moves The moves played.
     * @param positions Every position of the game, from the seed position to the last one.
     *                  There is one more position than there are moves.
     * @param legalMoves The legal moves in each position, in the same order as {@code positions}.
     */
    public record RandomGame(String seedFen,
                             List<Integer> moves,
                             List<Position> positions,
                             List<List<Integer>> legalMoves) {

        /**
         * Returns the position the game starts from.
         */
        public Position seed() {
            return positions.getFirst();
        }

        /**
         * Returns the seed FEN and the moves in SAN.
         */
        @Override
        public String toString() {
            int[] array = moves.stream().mapToInt(Integer::intValue).toArray();
            return "[" + seedFen + "] " + String.join(" ", SanParser.format(seed(), array));
        }
    }
}
