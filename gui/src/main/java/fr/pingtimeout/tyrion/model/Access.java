package fr.pingtimeout.tyrion.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Access implements Comparable<Access> {


    private final AccessDuration accessDuration;
    private final Accessor accessor;
    private final Target target;


    public Access(long enterTime, long exitTime, Accessor accessor, Target target) {
        this.accessDuration = new AccessDuration(enterTime, exitTime);
        this.accessor = accessor;
        this.target = target;
    }



    @Override
    public int compareTo(Access that) {
        return this.accessDuration.compareTo(that.accessDuration);
    }



    @Override
    public boolean equals(Object o) {
        if (o instanceof Access) {
            Access that = (Access) o;

            return new EqualsBuilder()
                    .append(this.accessDuration, that.accessDuration)
                    .append(this.accessor, that.accessor)
                    .append(this.target, that.target)
                    .build();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.accessDuration)
                .append(this.accessor)
                .append(this.target)
                .build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(this.accessDuration)
                .append(this.accessor)
                .append(this.target)
                .build();
    }


    public AccessDuration getAccessDuration() {
        return accessDuration;
    }

    public Accessor getAccessor() {
        return accessor;
    }

    public Target getTarget() {
        return target;
    }
}
