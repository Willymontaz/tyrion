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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.pingtimeout.tyrion.util.HashCodeSource;
import fr.pingtimeout.tyrion.util.TimeSource;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CriticalSectionEntering.class, name = "entering"),
        @JsonSubTypes.Type(value = CriticalSectionEntered.class, name = "enter"),
        @JsonSubTypes.Type(value = CriticalSectionExit.class, name = "exit")
})
public abstract class CriticalSectionEvent implements Comparable<CriticalSectionEvent> {

    static TimeSource timeSource = new TimeSource();

    private final long timestamp;

    private final Accessor accessor;

    @JsonProperty("target")
    private final ObjectUnderLock objectUnderLock;


    public CriticalSectionEvent(Thread accessingThread, Object objectUnderLock) {
        this.timestamp = timeSource.currentTimeMillis();
        this.accessor = new Accessor(accessingThread);
        this.objectUnderLock = new ObjectUnderLock(objectUnderLock);
    }


    @Override
    public int compareTo(CriticalSectionEvent that) {
        long timestampComparison = this.timestamp - that.timestamp;
        if (timestampComparison == 0) {
            int thisPriority = this instanceof CriticalSectionEntered ? 0 : 1;
            int thatPriority = that instanceof CriticalSectionEntered ? 0 : 1;
            return thisPriority - thatPriority;
        } else {
            return (int) timestampComparison;
        }
    }


    // Constructor and getters required by Jackson unmashalling process
    @JsonCreator
    protected CriticalSectionEvent(
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("accessor") Accessor accessor,
            @JsonProperty("objectUnderLock") ObjectUnderLock objectUnderLock) {
        this.timestamp = timestamp;
        this.accessor = accessor;
        this.objectUnderLock = objectUnderLock;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public Accessor getAccessor() {
        return accessor;
    }

    public ObjectUnderLock getObjectUnderLock() {
        return objectUnderLock;
    }

    @Override
    public String toString() {
        return "CriticalSectionEvent{" +
                "timestamp=" + timestamp +
                ", accessor=" + accessor +
                ", objectUnderLock=" + objectUnderLock +
                '}';
    }
}