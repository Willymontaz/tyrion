package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Target {

    private final String className;

    private final long hashcode;


    public Target(Object target) {
        this.className = target.getClass().getName();
        this.hashcode = System.identityHashCode(target);
    }


    // Constructor and getters required by Jackson unmashalling process
    @JsonCreator
    public Target(
            @JsonProperty("class") String className,
            @JsonProperty("hashcode") long hashcode) {
        this.className = className;
        this.hashcode = hashcode;
    }

    public String getClassName() {
        return className;
    }

    public long getHashcode() {
        return hashcode;
    }


    @Override
    public String toString() {
        return className + "@" + hashcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Target)) return false;

        Target target = (Target) o;

        if (hashcode != target.hashcode) return false;
        if (!className.equals(target.className)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + (int) (hashcode ^ (hashcode >>> 32));
        return result;
    }
}
