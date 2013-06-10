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

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockInterceptor {

    static Logger LOG = LoggerFactory.getLogger(LockInterceptor.class);
    static Map<Object, Map<Thread, Integer>> USED_LOCKS_COUNTERS = new IdentityHashMap<Object, Map<Thread, Integer>>();


    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void enteredSynchronizedMethod(Object lock) {
        recordSynchronizedAccessOn(lock);
        printDebugMessage("just entered a synchronized method", lock);
    }


    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void leavingSynchronizedMethod(Object lock) {
        printDebugMessage("is leaving a synchronized method", lock);
    }


    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void enteredSynchronizedBlock(Object lock) {
        recordSynchronizedAccessOn(lock);
        printDebugMessage("just entered a synchronized block", lock);
    }


    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void leavingSynchronizedBlock(Object lock) {
        printDebugMessage("is leaving a synchronized block", lock);
    }


    private static void recordSynchronizedAccessOn(Object lock) {
        final Map<Thread, Integer> accessors = getOrCreateLockAccessors(lock);
        incrementOrCreateLockCounter(accessors);
    }


    private static void incrementOrCreateLockCounter(Map<Thread, Integer> accessors) {
        final int numberOfAccessesByCurrentThread;
        if (accessors.containsKey(Thread.currentThread())) {
            numberOfAccessesByCurrentThread = accessors.get(Thread.currentThread()) + 1;
        } else {
            numberOfAccessesByCurrentThread = 1;
        }
        accessors.put(Thread.currentThread(), numberOfAccessesByCurrentThread);
    }


    private static Map<Thread, Integer> getOrCreateLockAccessors(Object lock) {
        final Map<Thread, Integer> accessors;
        if (USED_LOCKS_COUNTERS.containsKey(lock)) {
            accessors = USED_LOCKS_COUNTERS.get(lock);
        } else {
            accessors = new IdentityHashMap<Thread, Integer>();
            USED_LOCKS_COUNTERS.put(lock, accessors);
        }
        return accessors;
    }

    private static void printDebugMessage(String intercepted, Object arg) {
        LOG.debug("Someone {} on {} (type : {})", intercepted, arg, arg.getClass());
        LOG.trace("Stacktrace : ", new Throwable("Here"));
    }
}
