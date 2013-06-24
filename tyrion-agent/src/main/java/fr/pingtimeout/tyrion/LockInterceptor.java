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

package fr.pingtimeout.tyrion;

import fr.pingtimeout.tyrion.model.EventsHolder;

import java.util.Arrays;

public class LockInterceptor {

    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void enteredSynchronizedMethod(Object lock) {
        StackTraceElement[] filteredStackTrace = createStackTrace();
        recordSynchronizedAccessOn(lock);
    }


    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void leavingSynchronizedMethod(Object lock) {
        StackTraceElement[] filteredStackTrace = createStackTrace();
        recordSynchronizedExitOn(lock);
    }

    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void enteredSynchronizedBlock(Object lock) {
        StackTraceElement[] filteredStackTrace = createStackTrace();
        recordSynchronizedAccessOn(lock);
    }


    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void leavingSynchronizedBlock(Object lock) {
        StackTraceElement[] filteredStackTrace = createStackTrace();
        recordSynchronizedExitOn(lock);
    }

    private static StackTraceElement[] createStackTrace() {
        Throwable exception = new Throwable("");
        StackTraceElement[] stackTrace = exception.getStackTrace();
        return Arrays.copyOfRange(stackTrace, 2, stackTrace.length);
    }


    private static void recordSynchronizedAccessOn(Object target) {
        EventsHolder.INSTANCE.recordNewEntry(Thread.currentThread(), target);
    }


    private static void recordSynchronizedExitOn(Object target) {
        EventsHolder.INSTANCE.recordNewExit(Thread.currentThread(), target);
    }
}
