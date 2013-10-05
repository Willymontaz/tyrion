package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CriticalSectionEntering.class, name = "entering"),
        @JsonSubTypes.Type(value = CriticalSectionEntered.class, name = "enter"),
        @JsonSubTypes.Type(value = CriticalSectionExit.class, name = "exit")
})
public abstract class CriticalSectionEvent implements Comparable<CriticalSectionEvent> {

    private final long timestamp;

    private final Accessor accessor;

    @JsonProperty("target")
    private final ObjectUnderLock objectUnderLock;


    public CriticalSectionEvent(Thread accessingThread, Object objectUnderLock) {
        this.timestamp = System.currentTimeMillis();
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