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
import com.fasterxml.jackson.annotation.JsonCreator;
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

    static TimeSource timeSource = new TimeSource();

    private final Time time;
    private final Accessor accessor;
    private final ObjectUnderLock target;

    public CriticalSectionEvent(Thread accessingThread, Object target) {
        this.time = timeSource.currentTime();
        this.accessor = new Accessor(accessingThread);
        this.target = new ObjectUnderLock(target);
    }

    @JsonCreator
    protected CriticalSectionEvent(
            @JsonProperty("millis") long millis,
            @JsonProperty("nanos") long nanos,
            @JsonProperty("accessor") Accessor accessor,
            @JsonProperty("target") ObjectUnderLock target) {
        this.time = new Time(millis, nanos);
        this.accessor = accessor;
        this.target = target;
    }

    @Override
    public int compareTo(CriticalSectionEvent that) {
        return TimeAndPriorityComparator.INSTANCE.compare(this, that);
    }
}

enum TimeAndPriorityComparator implements Comparator<CriticalSectionEvent> {
    INSTANCE;

    @Override
    public int compare(CriticalSectionEvent a, CriticalSectionEvent b) {
        int timeComparison = compareTime(a, b);
        if (timeComparison == 0) {
            return comparePriority(a, b);
        }
        return timeComparison;
    }

    private int compareTime(CriticalSectionEvent a, CriticalSectionEvent b) {
        return a.getTime().compareTo(b.getTime());
    }

    private int comparePriority(CriticalSectionEvent a, CriticalSectionEvent b) {
        return priorityOf(a) - priorityOf(b);
    }

    private int priorityOf(CriticalSectionEvent event) {
        if (event instanceof CriticalSectionEntering) return 1;
        if (event instanceof CriticalSectionEntered) return 2;
        if (event instanceof CriticalSectionExit) return 3;
        throw new IllegalArgumentException("Unknown type of critical section");
    }

}