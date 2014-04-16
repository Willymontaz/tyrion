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

    private final long enterMillis;
    private final long enterNanos;
    private final long exitMillis;
    private final long exitNanos;

    @JsonCreator
    public Duration(@JsonProperty long enterMillis,
                    @JsonProperty long enterNanos,
                    @JsonProperty long exitMillis,
                    @JsonProperty long exitNanos) {
        this.enterMillis = enterMillis;
        this.enterNanos = enterNanos;
        this.exitMillis = exitMillis;
        this.exitNanos = exitNanos;
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
        if (that.enterMillis < this.enterMillis)
            return that.intersectsWithin(this, delta, unit);
        return that.enterMillis <= this.enterMillis + unit.toMillis(delta);

//        return false;

//        boolean startedAtSameMillis = that.enterMillis == this.enterMillis;
//
//        boolean thatStartedAfterThis = that.enterMillis >= this.exitTime;
//        boolean thatEndedBeforeThisExitPlusDelta = that.enterMillis <= (this.exitTime + unit.toMillis(delta));
//
//        return thatStartedAfterThis && thatEndedBeforeThisExitPlusDelta;
    }


    public boolean intersectsWithin(Duration that) {
        return that.enterMillis < this.exitMillis
                && this.enterMillis < that.exitMillis;
//        return false;  //To change body of created methods use File | Settings | File Templates.
    }


    @Override
    public int compareTo(Duration that) {
        return (int) (this.enterMillis - that.enterMillis);
    }
}
