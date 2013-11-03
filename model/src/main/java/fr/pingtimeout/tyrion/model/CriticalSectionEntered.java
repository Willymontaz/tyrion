package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CriticalSectionEntered extends CriticalSectionEvent {


    public CriticalSectionEntered(Thread accessor, Object objectUnderLock) {
        super(accessor, objectUnderLock);
    }


    // Constructor required by Jackson unmashalling process
    @JsonCreator
    protected CriticalSectionEntered(
            @JsonProperty("millis") long millis,
            @JsonProperty("nanos") long nanos,
            @JsonProperty("accessor") Accessor accessor,
            @JsonProperty("target") ObjectUnderLock objectUnderLock) {
        super(millis, nanos, accessor, objectUnderLock);
    }
}
