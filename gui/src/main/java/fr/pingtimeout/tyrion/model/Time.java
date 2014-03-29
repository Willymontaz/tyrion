/*
 * Copyright (c) 2013-2014, Pierre Laporte
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this work; if not, see <http://www.gnu.org/licenses/>.
 */

package fr.pingtimeout.tyrion.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append(this.enterTime)
                .append(this.exitTime)
                .build();
    }
}
