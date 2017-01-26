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

package se.dykstrom.ronja.engine.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Utility methods related to class manipulation.
 *
 * @author Johan Dykstrom
 */
public final class ClassUtils {

    private ClassUtils() { }

    /**
     * Finds and returns the matching constructor in class {@code clazz}, or fails with a
     * {@code RuntimeException} if the constructor cannot be found.
     *
     * @param clazz The class of the constructor.
     * @param argTypes The argument types of the constructor.
     * @return The constructor found.
     */
    public static <T> Constructor<T> getConstructorOrFail(Class<T> clazz, Class<?>... argTypes) {
        try {
            return clazz.getConstructor(argTypes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get constructor '" + clazz.getSimpleName() + "' with args " + Arrays.asList(argTypes), e);
        }
    }

    /**
     * Invokes the given constructor with optional arguments {@code args}, returning
     * the new instance created by {@code constructor}. If {@code constructor} cannot
     * be invoked, this method fails with a {@code RuntimeException}.
     *
     * @param constructor The constructor to invoke.
     * @param args The constructor arguments.
     * @return The new instance created by calling the constructor.
     */
    public static <T> T invokeConstructorOrFail(Constructor<T> constructor, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Failed to call constructor '" + constructor.getDeclaringClass().getSimpleName() + "': " + e.getCause().getMessage(), e.getCause());
        } catch (Exception e) {
            throw new RuntimeException("Failed to call constructor '" + constructor.getDeclaringClass().getSimpleName() + "': " + e.getMessage(), e);
        }
    }
}
