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
import java.util.concurrent.atomic.AtomicBoolean;

public class LockInterceptor {
    private static LockInterceptor INSTANCE = new LockInterceptor(EventsHolderSingleton.INSTANCE, new AtomicBoolean(false));

    private final EventsHolder eventsHolder;
    private final AtomicBoolean enabled;

    LockInterceptor(EventsHolder eventsHolder, AtomicBoolean enabled) {
        this.eventsHolder = eventsHolder;
        this.enabled = enabled;
    }

    // Note : this method is called dynamically
    public void enteredCriticalSection(Object lock) {
        if (enabled.get()) {
            StackTraceElement[] filteredStackTrace = createStackTrace();
            eventsHolder.recordNewEntry(Thread.currentThread(), lock);
        }
    }


    // Note : this method is called dynamically
    public  void leavingCriticalSection(Object lock) {
        if (enabled.get()) {
            StackTraceElement[] filteredStackTrace = createStackTrace();
            eventsHolder.recordNewExit(Thread.currentThread(), lock);
        }
    }


    private static StackTraceElement[] createStackTrace() {
        Throwable exception = new Throwable("");
        StackTraceElement[] stackTrace = exception.getStackTrace();
        return Arrays.copyOfRange(stackTrace, 2, stackTrace.length);
    }

    public static LockInterceptor getInstance() {
        return INSTANCE;
    }
}
