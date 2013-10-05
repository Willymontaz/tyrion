package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CriticalSectionExit extends CriticalSectionEvent {


    public CriticalSectionExit(Thread accessor, Object objectUnderLock) {
        super(accessor, objectUnderLock);
    }


    // Constructor and getters required by Jackson unmashalling process
    @JsonCreator
    protected CriticalSectionExit(
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("accessor") Accessor accessor,
            @JsonProperty("objectUnderLock") ObjectUnderLock objectUnderLock) {
        super(timestamp, accessor, objectUnderLock);
    }
}
