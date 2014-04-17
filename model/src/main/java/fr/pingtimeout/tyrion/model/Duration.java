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

import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@EqualsAndHashCode
@Getter
@ToString
public class Duration implements Comparable<Duration> {

    private final Time enterTime;
    private final Time exitTime;

    @JsonCreator
    public Duration(@JsonProperty long enterMillis,
                    @JsonProperty long enterNanos,
                    @JsonProperty long exitMillis,
                    @JsonProperty long exitNanos) {
        this.enterTime = new Time(enterMillis, enterNanos);
        this.exitTime = new Time(exitMillis, exitNanos);
    }

    public Duration(Time enter, Time exit) {
        this(enter.getMillis(), enter.getNanos(), exit.getMillis(), exit.getNanos());
    }


    /**
     * Checks if the current Duration instance intersects with a given Duration, considering a possible delta between those two events.
     * <p/>
     * This methods checks that the following scenario :
     * <pre>
     *     [--this--]   [--that--]
     *               [-∆-]
     * -------------------------------------> t
     * </pre>
     *
     * @param that
     * @param delta
     * @param unit
     * @return
     */
    public boolean intersectsWithin(Duration that, int delta, TimeUnit unit) {
        // Here there is all possible combinations (this being before, interleaved with, during or after that)
        if(that.getEnterTime().isStrinctlyBefore(this.getEnterTime())) {
            // Flip parameters so that that is always after this
            return that.intersectsWithin(this, delta, unit);
        }

        // Here the only combinations remaining are : that (y) starts after-or-exactly-at this (x) beggining
        // x--x y----y
        // x-yx---y
        // yx--xy
        return that.getExitTime().isBefore(this.getExitTime());
    }


    public boolean intersectsWithin(Duration that) {
        return  intersectsWithin(that, 0, TimeUnit.MILLISECONDS);
    }


    @Override
    public int compareTo(Duration that) {
        return this.getEnterTime().compareTo(that.getEnterTime());
    }
}
