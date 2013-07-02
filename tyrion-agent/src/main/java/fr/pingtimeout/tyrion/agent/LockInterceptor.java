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

package fr.pingtimeout.tyrion.agent;

import fr.pingtimeout.tyrion.util.EventsHolder;
import fr.pingtimeout.tyrion.util.EventsHolderSingleton;

import java.util.Arrays;

public class LockInterceptor {
    public static final String CLASS_FQN = LockInterceptor.class.getName().replace('.', '/');
    public static final String ENTER_METHOD_NAME = "enteredCriticalSection";
    public static final String EXIT_METHOD_NAME = "leavingCriticalSection";
    public static final String ENTER_EXIT_METHOD_SIGNATURE = "(Ljava/lang/Object;)V";

    static EventsHolder eventsHolder = EventsHolderSingleton.INSTANCE;

    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void enteredCriticalSection(Object lock) {
        StackTraceElement[] filteredStackTrace = createStackTrace();
        recordSynchronizedAccessOn(lock);
    }


    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void leavingCriticalSection(Object lock) {
        StackTraceElement[] filteredStackTrace = createStackTrace();
        recordSynchronizedExitOn(lock);
    }


    private static StackTraceElement[] createStackTrace() {
        Throwable exception = new Throwable("");
        StackTraceElement[] stackTrace = exception.getStackTrace();
        return Arrays.copyOfRange(stackTrace, 2, stackTrace.length);
    }


    private static void recordSynchronizedAccessOn(Object target) {
        eventsHolder.recordNewEntry(Thread.currentThread(), target);
    }


    private static void recordSynchronizedExitOn(Object target) {
        eventsHolder.recordNewExit(Thread.currentThread(), target);
    }
}
