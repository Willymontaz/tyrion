package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Accessor {

    private long id;

    private String name;


    public Accessor(Thread accessingThread) {
        this.id = accessingThread.getId();
        this.name = accessingThread.getName();
    }


    @JsonCreator
    protected Accessor(
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Accessor)) return false;

        Accessor accessor = (Accessor) o;

        if (id != accessor.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
