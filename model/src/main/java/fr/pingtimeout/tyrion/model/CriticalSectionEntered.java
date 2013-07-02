package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CriticalSectionEntered extends CriticalSectionEvent {


    public CriticalSectionEntered(Thread accessor, Object objectUnderLock) {
        super(accessor, objectUnderLock);
    }


    @JsonCreator
    protected CriticalSectionEntered (
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("accessor") Accessor accessor,
            @JsonProperty("target") Target target) {
        super(timestamp, accessor, target);
    }


    @Override
    String getType() {
        return "enter";
    }
}
