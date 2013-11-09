/*
 * Copyright (c) 2013, Pierre Laporte
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this work; if not, see <http://www.gnu.org/licenses/>.
 */

package fr.pingtimeout.tyrion.transformation;

public class TestClassWithSynchronizedSections {
    final Object lock = new Object();

    static synchronized long staticMethodOne() {
        return staticMethodTwo();
    }

    static synchronized long staticMethodTwo() {
        return 1L;
    }

    synchronized long instanceMethodOne() {
        return instanceMethodTwo();
    }

    synchronized long instanceMethodTwo() {
        return 2L;
    }

    long blockOne() {
        synchronized (lock) {
            return blockTwo();
        }
    }

    long blockTwo() {
        synchronized (lock) {
            return 3L;
        }
    }

    synchronized static long staticMethodWithException() {
        throw new RuntimeException("From static method");
    }

    synchronized long instanceMethodWithException() {
        throw new RuntimeException("From instance method");
    }

    long blockWithException() {
        synchronized (lock) {
            throw new RuntimeException("From static block");
        }
    }

}