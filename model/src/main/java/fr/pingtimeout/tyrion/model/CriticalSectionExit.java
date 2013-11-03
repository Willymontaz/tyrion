package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CriticalSectionExit extends CriticalSectionEvent {


    public CriticalSectionExit(Thread accessor, Object objectUnderLock) {
        super(accessor, objectUnderLock);
    }


    // Constructor required by Jackson unmashalling process
    @JsonCreator
    protected CriticalSectionExit(
            @JsonProperty("millis") long millis,
            @JsonProperty("nanos") long nanos,
            @JsonProperty("accessor") Accessor accessor,
            @JsonProperty("target") ObjectUnderLock objectUnderLock) {
        super(millis, nanos, accessor, objectUnderLock);
    }
}
