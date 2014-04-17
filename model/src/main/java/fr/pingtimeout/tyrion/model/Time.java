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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class Time implements Comparable<Time> {

    private final long millis;
    private final long nanos;

    @JsonCreator
    public Time(@JsonProperty("millis") long millis, @JsonProperty("nanos") long nanos) {
        this.millis = millis;
        this.nanos = nanos;
    }

    public boolean isStrinctlyBefore(Time that) {
        return this.compareTo(that) < 0;
    }

    public boolean isBefore(Time that) {
        return this.compareTo(that) <= 0;
    }

    @Override
    public int compareTo(Time that) {
        if (this.millis == that.millis) {
            return ((Long) this.nanos).compareTo(that.nanos);
        }
        return ((Long) this.millis).compareTo(that.millis);
    }
}
