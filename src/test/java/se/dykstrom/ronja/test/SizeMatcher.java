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

package se.dykstrom.ronja.test;

import java.lang.reflect.Array;
import java.util.stream.StreamSupport;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * A matcher that matches on the size of a collection or array. The type of the elements does not matter.
 *
 * @author Johan Dykstrom
 */
public class SizeMatcher<T> extends BaseMatcher<T> {

    private final long size;

    private SizeMatcher(long size) {
        this.size = size;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a collection or array of size " + size);
    }

    @Override
    public boolean matches(Object obj) {
        if (obj instanceof Iterable<?>) {
            Iterable<?> iterable = (Iterable<?>) obj;
            return StreamSupport.stream(iterable.spliterator(), false).count() == this.size;
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == this.size;
        }
        return false;
    }

    /**
     * Returns a matcher that matches every collection or array of the given size.
     *
     * @param size The size to match.
     * @return A matcher that matches every collection or array of the given size.
     */
    public static <T> Matcher<T> hasSize(long size) {
        return new SizeMatcher<>(size);
    }
}
