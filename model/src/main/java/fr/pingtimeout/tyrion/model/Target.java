package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Target {

    private String className;

    private long hashcode;


    Target(Object target) {
        this.className = target.getClass().getName();
        this.hashcode = System.identityHashCode(target);
    }


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
}
