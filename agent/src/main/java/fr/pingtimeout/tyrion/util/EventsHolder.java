/*
 * Copyright (c) 2013-2014, Pierre Laporte
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

package fr.pingtimeout.tyrion.util;

public interface EventsHolder {
    void recordNewEntry(Thread accessor, Class<?> classUnderLock);

    void recordNewExit(Thread accessor, Class<?> classUnderLock);

    void recordNewEntry(Thread accessor, Object objectUnderLock);

    void recordNewExit(Thread accessor, Object objectUnderLock);

    void recordNewEntering(Thread accessor, Object objectUnderLock);
}
