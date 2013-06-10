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

package fr.pingtimeout.tyrion.data;

import java.util.IdentityHashMap;
import java.util.Map;

public class Lock {

    public static final int EXPECTED_CONTENTION = 4;

    private final Object target;

    private final Map<Thread, Access> accessors = new IdentityHashMap<>(EXPECTED_CONTENTION);


    Lock(Object lock) {
        this.target = lock;
    }


    public void addAccessFrom(Thread accessor) {
        Access newAccess;
        if (!accessors.containsKey(accessor)) {
            newAccess = new Access();
        } else {
            newAccess = accessors.get(accessor).addOneAccess();
        }
        accessors.put(accessor, newAccess);
    }


    public String toString() {
        return target + " is accessed by " + accessors;
    }
}
