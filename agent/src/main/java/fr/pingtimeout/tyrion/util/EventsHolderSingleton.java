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

import fj.data.List;
import fr.pingtimeout.tyrion.model.CriticalSectionEntered;
import fr.pingtimeout.tyrion.model.CriticalSectionEntering;
import fr.pingtimeout.tyrion.model.CriticalSectionEvent;
import fr.pingtimeout.tyrion.model.CriticalSectionExit;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public enum EventsHolderSingleton implements EventsHolder {
    INSTANCE;

    private Map<Long, AtomicReference<List<CriticalSectionEvent>>> events = new ConcurrentHashMap<>();

    @Override
    public void recordNewEntering(Thread accessor, Object objectUnderLock) {
        add(accessor, new CriticalSectionEntering(accessor, objectUnderLock));
    }

    @Override
    public void recordNewEntry(Thread accessor, Class<?> classUnderLock) {
        add(accessor, new CriticalSectionEntered(accessor, classUnderLock));
    }

    @Override
    public void recordNewExit(Thread accessor, Class<?> classUnderLock) {
        add(accessor, new CriticalSectionExit(accessor, classUnderLock));
    }

    @Override
    public void recordNewEntry(Thread accessor, Object objectUnderLock) {
        add(accessor, new CriticalSectionEntered(accessor, objectUnderLock));
    }

    @Override
    public void recordNewExit(Thread accessor, Object objectUnderLock) {
        add(accessor, new CriticalSectionExit(accessor, objectUnderLock));
    }

    private void add(Thread accessor, CriticalSectionEvent newEvent) {
        AtomicReference<List<CriticalSectionEvent>> accessorEventsRef = safeGet(accessor);
        List<CriticalSectionEvent> currentEventsList;
        List<CriticalSectionEvent> updatedEventsList;

        do {
            currentEventsList = accessorEventsRef.get();
            updatedEventsList = currentEventsList.cons(newEvent);
        } while (!accessorEventsRef.compareAndSet(currentEventsList, updatedEventsList));
    }

    private AtomicReference<List<CriticalSectionEvent>> safeGet(Thread accessor) {
        long accessorId = accessor.getId();
        if (events.get(accessorId) == null) {
            List<CriticalSectionEvent> accessorEvents = List.nil();
            AtomicReference<List<CriticalSectionEvent>> reference = new AtomicReference<>(accessorEvents);
            events.put(accessorId, reference);
        }

        return events.get(accessorId);
    }

    public Set<Long> getThreadIds() {
        return events.keySet();
    }

    public List<CriticalSectionEvent> getAndClearEventsListOf(Long threadId) {
        AtomicReference<List<CriticalSectionEvent>> accessorEventsRef = events.get(threadId);
        List<CriticalSectionEvent> currentEventsList;
        List<CriticalSectionEvent> emptyEventsList = List.nil();

        do {
            currentEventsList = accessorEventsRef.get();
        } while (!accessorEventsRef.compareAndSet(currentEventsList, emptyEventsList));

        return currentEventsList;
    }
}
