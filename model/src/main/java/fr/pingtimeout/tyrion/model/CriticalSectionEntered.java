package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CriticalSectionEntered extends CriticalSectionEvent {


    public CriticalSectionEntered(Thread accessor, Object objectUnderLock) {
        super(accessor, objectUnderLock);
    }


    // Constructor and getters required by Jackson unmashalling process
    @JsonCreator
    protected CriticalSectionEntered(
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("accessor") Accessor accessor,
            @JsonProperty("target") ObjectUnderLock objectUnderLock) {
        super(timestamp, accessor, objectUnderLock);
    }
}
