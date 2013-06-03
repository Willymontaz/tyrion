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
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockInterceptor {

    static Logger LOG = LoggerFactory.getLogger(LockInterceptor.class);

    static Map<Object, Map<Thread, Integer>> USED_LOCKS_COUNTERS = new HashMap<Object, Map<Thread, Integer>>();

    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void enteredSynchronizedMethod(Object lock) {
        trace("just entered a synchronized method", lock);

    }

    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void leavingSynchronizedMethod(Object lock) {
        trace("is leaving a synchronized method", lock);
    }

    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void enteredSynchronizedBlock(Object lock) {
        trace("just entered a synchronized block", lock);
    }

    // This method is called dynamically, warnings can be suppressed
    @SuppressWarnings("unused")
    public static void leavingSynchronizedBlock(Object lock) {
        trace("is leaving a synchronized block", lock);
    }

    private static void trace(String intercepted, Object arg) {
        LOG.info("Someone " + intercepted + " !", new Throwable("Here"));
        LOG.info("Additional argument : '{}' of type '{}'", arg, arg.getClass());
        LOG.info("");
    }
}
