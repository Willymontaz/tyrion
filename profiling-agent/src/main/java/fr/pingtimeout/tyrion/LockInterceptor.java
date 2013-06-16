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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.pingtimeout.tyrion.data.LockAccesses;
import fr.pingtimeout.tyrion.data.LockFactory;

public class LockInterceptor {

    static Logger LOG = LoggerFactory.getLogger(LockInterceptor.class);


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


    private static void recordSynchronizedAccessOn(Object target) {
        LockAccesses lockAccesses = LockFactory.getInstanceFrom(target);
        lockAccesses.addAccessFrom(Thread.currentThread());
    }


    private static void printDebugMessage(String intercepted, Object arg) {
        LOG.debug("Someone {} on {} (type : {})", intercepted, arg, arg.getClass());
        LOG.trace("Stacktrace : ", new Throwable("Here"));
    }
}
