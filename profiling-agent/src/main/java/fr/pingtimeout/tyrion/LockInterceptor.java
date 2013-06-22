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

import fr.pingtimeout.tyrion.data.LockAccesses;
import fr.pingtimeout.tyrion.data.LockFactory;

import java.util.Arrays;

public class LockInterceptor {

    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void enteredSynchronizedMethod(Object lock) {
        StackTraceElement[] filteredStackTrace = createStackTrace();
        recordSynchronizedAccessOn(lock);
        printDebugMessage("just entered a synchronized method", lock, filteredStackTrace);
    }


    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void leavingSynchronizedMethod(Object lock) {
        StackTraceElement[] filteredStackTrace = createStackTrace();
        printDebugMessage("is leaving a synchronized method", lock, filteredStackTrace);
    }


    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void enteredSynchronizedBlock(Object lock) {
        StackTraceElement[] filteredStackTrace = createStackTrace();
        recordSynchronizedAccessOn(lock);
        printDebugMessage("just entered a synchronized block", lock, filteredStackTrace);
    }

    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void leavingSynchronizedBlock(Object lock) {
        StackTraceElement[] filteredStackTrace = createStackTrace();
        printDebugMessage("is leaving a synchronized block", lock, filteredStackTrace);
    }


    private static StackTraceElement[] createStackTrace() {
        Throwable exception = new Throwable("");
        StackTraceElement[] stackTrace = exception.getStackTrace();
        return Arrays.copyOfRange(stackTrace, 2, stackTrace.length);
    }


    private static void recordSynchronizedAccessOn(Object target) {
        LockAccesses lockAccesses = LockFactory.getInstanceFrom(target);
        lockAccesses.addAccessFrom(Thread.currentThread());
    }


    private static void printDebugMessage(String intercepted, Object arg, StackTraceElement[] stacktrace) {
//        LOG.debug("Someone {} on {} (type : {})", intercepted, arg, arg.getClass());
//        LOG.trace("Stacktrace : {}", Arrays.toString(stacktrace));
    }
}
