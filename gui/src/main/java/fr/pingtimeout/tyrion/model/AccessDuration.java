package fr.pingtimeout.tyrion.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AccessDuration implements Comparable<AccessDuration> {


    private final long enterTime;
    private final long exitTime;


    public AccessDuration(long enterTime, long exitTime) {
        this.enterTime = enterTime;
        this.exitTime = exitTime;
    }


    @Override
    public int compareTo(AccessDuration that) {
        return (int) (this.enterTime - that.enterTime);
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof AccessDuration) {
            AccessDuration that = (AccessDuration) o;

            return new EqualsBuilder()
                    .append(this.enterTime, that.enterTime)
                    .append(this.exitTime, that.exitTime)
                    .build();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.enterTime)
                .append(this.exitTime)
                .build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(this.enterTime)
                .append(this.exitTime)
                .build();
    }
}
