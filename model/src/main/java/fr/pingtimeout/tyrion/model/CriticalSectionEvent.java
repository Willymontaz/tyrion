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

package fr.pingtimeout.tyrion.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.pingtimeout.tyrion.util.TimeSource;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CriticalSectionEntering.class, name = "entering"),
        @JsonSubTypes.Type(value = CriticalSectionEntered.class, name = "enter"),
        @JsonSubTypes.Type(value = CriticalSectionExit.class, name = "exit")
})
@EqualsAndHashCode
@Getter
@ToString
public abstract class CriticalSectionEvent implements Comparable<CriticalSectionEvent> {

    private static class TimeAndPriorityComparator implements Comparator<CriticalSectionEvent> {
        private final TimeComparator timeComparator = new TimeComparator();
        private final PriorityComparator priorityComparator = new PriorityComparator();

        @Override
        public int compare(CriticalSectionEvent e1, CriticalSectionEvent e2) {
            int timeComparison = timeComparator.compare(e1, e2);
            if (timeComparison == 0) {
                return priorityComparator.compare(e1, e2);
            }
            return timeComparison;
        }
    }

    private static class TimeComparator implements Comparator<CriticalSectionEvent> {
        @Override
        public int compare(CriticalSectionEvent e1, CriticalSectionEvent e2) {
            if (e1.millis == e2.millis) {
                return ((Long) e1.nanos).compareTo(e2.nanos);
            }
            return ((Long) e1.millis).compareTo(e2.millis);
        }
    }

    private static class PriorityComparator implements Comparator<CriticalSectionEvent> {
        private final static Map<Class<? extends CriticalSectionEvent>, Integer> priorities = new HashMap<Class<? extends CriticalSectionEvent>, Integer>() {{
            put(CriticalSectionEntering.class, 1);
            put(CriticalSectionEntered.class, 2);
            put(CriticalSectionExit.class, 3);
        }};

        @Override
        public int compare(CriticalSectionEvent e1, CriticalSectionEvent e2) {
            Integer e1Priority = priorities.get(e1.getClass());
            Integer e2Priority = priorities.get(e2.getClass());
            return e1Priority.compareTo(e2Priority);
        }
    }

    static TimeSource timeSource = new TimeSource();

    private static final TimeAndPriorityComparator timeAndPriorityComparator = new TimeAndPriorityComparator();

    private final long millis;
    private final long nanos;
    private final Accessor accessor;
    private final ObjectUnderLock target;

    public CriticalSectionEvent(Thread accessingThread, Object target) {
        this.millis = timeSource.currentTimeMillis();
        this.nanos = timeSource.currentTimeNanos();
        this.accessor = new Accessor(accessingThread);
        this.target = new ObjectUnderLock(target);
    }

    @JsonCreator
    protected CriticalSectionEvent(
            @JsonProperty("millis") long millis,
            @JsonProperty("nanos") long nanos,
            @JsonProperty("accessor") Accessor accessor,
            @JsonProperty("target") ObjectUnderLock target) {
        this.millis = millis;
        this.nanos = nanos;
        this.accessor = accessor;
        this.target = target;
    }

    @Override
    public int compareTo(CriticalSectionEvent that) {
        return timeAndPriorityComparator.compare(this, that);
    }
}