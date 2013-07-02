package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CriticalSectionExit extends CriticalSectionEvent {


    public CriticalSectionExit(Thread accessor, Object objectUnderLock) {
        super(accessor, objectUnderLock);
    }


    @JsonCreator
    protected CriticalSectionExit (
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("accessor") Accessor accessor,
            @JsonProperty("target") Target target) {
        super(timestamp, accessor, target);
    }

    @Override
    String getType() {
        return "exit";
    }
}
