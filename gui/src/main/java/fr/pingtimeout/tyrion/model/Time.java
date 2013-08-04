package fr.pingtimeout.tyrion.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.concurrent.TimeUnit;

public class Time implements Comparable<Time> {


    private final long enterTime;
    private final long exitTime;


    public Time(long enterTime, long exitTime) {
        this.enterTime = enterTime;
        this.exitTime = exitTime;
    }


    public boolean matches(Time that, int delta, TimeUnit unit) {
        boolean thatStartedAfterThis = that.enterTime >= this.exitTime;
        boolean thatEndedBeforeThisExitPlusDelta = that.enterTime <= (this.exitTime + unit.toMillis(delta));

        return thatStartedAfterThis && thatEndedBeforeThisExitPlusDelta;
    }


    @Override
    public int compareTo(Time that) {
        return (int) (this.enterTime - that.enterTime);
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof Time) {
            Time that = (Time) o;

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
