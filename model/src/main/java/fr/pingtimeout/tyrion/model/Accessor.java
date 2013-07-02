package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Accessor {
    private long id;

    private String name;

    Accessor(Thread accessingThread) {
        this.id = accessingThread.getId();
        this.name = accessingThread.getName();
    }


    @JsonCreator
    public Accessor(
            @JsonProperty("id") long id,
            @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return Thread.class.getName() + "@" + id + " : " + name;
    }
}
