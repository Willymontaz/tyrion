package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectUnderLock {

    private final String className;

    private final long hashcode;


    public ObjectUnderLock(Object target) {
        this.className = target.getClass().getName();
        this.hashcode = System.identityHashCode(target);
    }


    // Constructor and getters required by Jackson unmashalling process
    @JsonCreator
    public ObjectUnderLock(
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
        if (!(o instanceof ObjectUnderLock)) return false;

        ObjectUnderLock objectUnderLock = (ObjectUnderLock) o;

        if (hashcode != objectUnderLock.hashcode) return false;
        if (!className.equals(objectUnderLock.className)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + (int) (hashcode ^ (hashcode >>> 32));
        return result;
    }
}
