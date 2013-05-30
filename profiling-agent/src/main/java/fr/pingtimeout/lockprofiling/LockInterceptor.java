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

package fr.pingtimeout.lockprofiling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockInterceptor {
    static Logger LOG = LoggerFactory.getLogger(LockInterceptor.class);

    public static void enteredSynchronizedMethod() {
        LOG.info("Someone entered a critical section !", new Throwable("Here"));
    }
    public static void leftSynchronizedMethod() {
        LOG.info("Someone left a critical section !", new Throwable("Here"));
    }
}
