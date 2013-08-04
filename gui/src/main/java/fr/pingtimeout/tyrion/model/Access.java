package fr.pingtimeout.tyrion.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.concurrent.TimeUnit;

public class Access implements Comparable<Access> {


    private final Time time;
    private final Accessor accessor;
    private final Target target;


    public Access(long enterTime, long exitTime, Accessor accessor, Target target) {
        this.time = new Time(enterTime, exitTime);
        this.accessor = accessor;
        this.target = target;
    }



    @Override
    public int compareTo(Access that) {
        return this.time.compareTo(that.time);
    }



    @Override
    public boolean equals(Object o) {
        if (o instanceof Access) {
            Access that = (Access) o;

            return new EqualsBuilder()
                    .append(this.time, that.time)
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
                .append(this.time)
                .append(this.accessor)
                .append(this.target)
                .build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append(this.time)
                .append(this.accessor)
                .append(this.target)
                .build();
    }


    public Time getTime() {
        return time;
    }

    public Accessor getAccessor() {
        return accessor;
    }

    public Target getTarget() {
        return target;
    }

    public boolean matches(Access that, int delta, TimeUnit unit) {
        return this.time.matches(that.time, delta, unit);
    }

    public boolean isAccessedBy(Accessor accessor) {
        return this.accessor.equals(accessor);
    }
}
