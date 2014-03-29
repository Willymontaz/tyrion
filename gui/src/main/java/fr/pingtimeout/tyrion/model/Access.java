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

public class Access implements Comparable<Access> {


    private final Time time;
    private final Accessor accessor;
    private final ObjectUnderLock objectUnderLock;


    public Access(long enterTime, long exitTime, Accessor accessor, ObjectUnderLock objectUnderLock) {
        this.time = new Time(enterTime, exitTime);
        this.accessor = accessor;
        this.objectUnderLock = objectUnderLock;
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
                    .append(this.objectUnderLock, that.objectUnderLock)
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
                .append(this.objectUnderLock)
                .build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append(this.time)
                .append(this.accessor)
                .append(this.objectUnderLock)
                .build();
    }


    public Time getTime() {
        return time;
    }

    public Accessor getAccessor() {
        return accessor;
    }

    public ObjectUnderLock getObjectUnderLock() {
        return objectUnderLock;
    }

    public boolean matches(Access that, int delta, TimeUnit unit) {
        return this.time.matches(that.time, delta, unit);
    }

    public boolean isAccessedBy(Accessor accessor) {
        return this.accessor.equals(accessor);
    }
}
