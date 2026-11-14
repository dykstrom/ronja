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

import se.dykstrom.ronja.test.RandomGames.GameInput;
import se.dykstrom.ronja.test.RandomGames.RandomGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Checks a property of many random legal games. If the property fails for a game, this class shrinks
 * the game to a shorter or simpler one that fails in the same way, and throws an {@link AssertionError}
 * that shows the shrunk game and the seed.
 * <p>
 * Each run draws a new seed. To repeat the games of an earlier run, set the system property
 * {@value #SEED_PROPERTY} to the seed that the failure printed, for example
 * {@code mvn test -Dtest=RandomGameInvariantsTest -Dronja.random.seed=1234}.
 *
 * @author Johan Dykstrom
 */
public final class RandomGameRunner {

    /** The system property that sets the seed. */
    public static final String SEED_PROPERTY = "ronja.random.seed";

    /** The seed of this run, shared by all properties. Each property mixes in its own name. */
    private static final long SEED = Long.getLong(SEED_PROPERTY, ThreadLocalRandom.current().nextLong());

    /** The maximum number of games to try while shrinking one failure. */
    private static final int MAX_SHRINK_ATTEMPTS = 10_000;

    private RandomGameRunner() { }

    /**
     * A property of a random game. The property fails if it throws an exception or an error.
     */
    @FunctionalInterface
    public interface GameProperty {
        void check(RandomGame game) throws Exception;
    }

    /**
     * Checks {@code property} for {@code tries} random games.
     *
     * @param name The name of the property, used in the failure report and mixed into the seed.
     * @param tries The number of games to check.
     * @param property The property to check.
     * @throws AssertionError If the property fails for a game.
     */
    public static void check(String name, int tries, GameProperty property) {
        Random random = new Random(SEED ^ name.hashCode());
        for (int i = 1; i <= tries; i++) {
            GameInput input = RandomGames.randomInput(random);
            Throwable failure = run(property, input);
            if (failure != null) {
                throw report(name, i, tries, input, failure, property);
            }
        }
    }

    private static AssertionError report(String name,
                                         int tryNumber,
                                         int tries,
                                         GameInput input,
                                         Throwable failure,
                                         GameProperty property) {
        int originalPlies = RandomGames.play(input).moves().size();
        Shrinker shrinker = new Shrinker(property, trimToPlayed(input), failure);
        shrinker.shrink();
        RandomGame shrunk = RandomGames.play(shrinker.input);
        String message = "Property " + name + " failed on game " + tryNumber + " of " + tries + ".\n"
                + "Seed: " + SEED + ". Run again with -D" + SEED_PROPERTY + "=" + SEED + " to repeat these games.\n"
                + "Shrunk game, " + shrunk.moves().size() + " plies (the original had " + originalPlies + ", "
                + shrinker.steps + " shrinking steps):\n"
                + "  " + shrunk + "\n"
                + "Failure: " + shrinker.failure;
        return new AssertionError(message, shrinker.failure);
    }

    /**
     * Runs {@code property} for the game that {@code input} describes.
     * Returns the failure, or {@code null} if the property holds.
     */
    private static Throwable run(GameProperty property, GameInput input) {
        try {
            property.check(RandomGames.play(input));
            return null;
        } catch (Exception | AssertionError e) {
            return e;
        }
    }

    /**
     * Returns the input with the choices that the game did not use removed. A game stops early at mate or stalemate.
     */
    private static GameInput trimToPlayed(GameInput input) {
        int plies = RandomGames.play(input).moves().size();
        return new GameInput(input.seedIndex(), input.choices().subList(0, plies));
    }

    /**
     * Shrinks a failing game input. Each step makes the input simpler: an earlier seed position,
     * fewer choices, or a lower choice. A step counts only if the property still fails with the
     * same exception class. Shrinking stops when no step helps, or after {@link #MAX_SHRINK_ATTEMPTS} games.
     */
    private static final class Shrinker {

        private final GameProperty property;
        private GameInput input;
        private Throwable failure;
        private int attempts;
        private int steps;

        Shrinker(GameProperty property, GameInput input, Throwable failure) {
            this.property = property;
            this.input = input;
            this.failure = failure;
        }

        void shrink() {
            boolean improved = true;
            while (improved && attempts < MAX_SHRINK_ATTEMPTS) {
                improved = tryAll(earlierSeeds()) || tryAll(fewerChoices()) || tryAll(lowerChoices());
            }
        }

        /**
         * Tries each candidate in order, and keeps the first one that fails in the same way.
         * Returns {@code true} if a candidate was kept.
         */
        private boolean tryAll(List<GameInput> candidates) {
            for (GameInput candidate : candidates) {
                if (attempts++ >= MAX_SHRINK_ATTEMPTS) {
                    return false;
                }
                Throwable candidateFailure = run(property, candidate);
                if (candidateFailure != null && candidateFailure.getClass() == failure.getClass()) {
                    input = trimToPlayed(candidate);
                    failure = candidateFailure;
                    steps++;
                    return true;
                }
            }
            return false;
        }

        /** Returns the same choices from each seed position before the current one. */
        private List<GameInput> earlierSeeds() {
            List<GameInput> candidates = new ArrayList<>();
            for (int i = 0; i < input.seedIndex(); i++) {
                candidates.add(new GameInput(i, input.choices()));
            }
            return candidates;
        }

        /** Returns the choices with one chunk removed, for chunk sizes from half the list down to one. */
        private List<GameInput> fewerChoices() {
            List<Integer> choices = input.choices();
            List<GameInput> candidates = new ArrayList<>();
            for (int size = choices.size() / 2; size >= 1; size /= 2) {
                for (int start = choices.size() - size; start >= 0; start -= size) {
                    List<Integer> shorter = new ArrayList<>(choices.subList(0, start));
                    shorter.addAll(choices.subList(start + size, choices.size()));
                    candidates.add(new GameInput(input.seedIndex(), List.copyOf(shorter)));
                }
            }
            if (choices.size() == 1) {
                candidates.add(new GameInput(input.seedIndex(), List.of()));
            }
            return candidates;
        }

        /** Returns the choices with one choice lowered: to 0, to half its value, or by one. */
        private List<GameInput> lowerChoices() {
            List<Integer> choices = input.choices();
            List<GameInput> candidates = new ArrayList<>();
            for (int i = 0; i < choices.size(); i++) {
                int value = choices.get(i);
                for (int lower : new int[]{0, value / 2, value - 1}) {
                    if (lower >= 0 && lower < value) {
                        List<Integer> changed = new ArrayList<>(choices);
                        changed.set(i, lower);
                        candidates.add(new GameInput(input.seedIndex(), List.copyOf(changed)));
                    }
                }
            }
            return candidates;
        }
    }
}
