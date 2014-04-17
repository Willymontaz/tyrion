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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@EqualsAndHashCode
@Getter
@ToString
public class Access implements Comparable<Access> {

    private final Duration duration;
    private final Accessor accessor;
    private final ObjectUnderLock objectUnderLock;

    public Access(CriticalSectionEntered enterTime, CriticalSectionExit exitTime) {
        if (!enterTime.getAccessor().equals(exitTime.getAccessor())) {
            throw new IllegalArgumentException("Events do not reference the same accessor");
        }
        if (!enterTime.getTarget().equals(exitTime.getTarget())) {
            throw new IllegalArgumentException("Events do not reference the same lock");
        }

        this.duration = new Duration(enterTime.getTime(), exitTime.getTime());
        this.accessor = enterTime.getAccessor();
        this.objectUnderLock = exitTime.getTarget();
    }

    @Override
    public int compareTo(Access that) {
        return this.duration.compareTo(that.duration);
    }

    static class Builder {
        private long enterMillis;
        private long enterNanos;
        private long exitMillis;
        private long exitNanos;
        private Accessor accessor;
        private ObjectUnderLock objectUnderLock;

        public Builder enterAt(long enterMillis, long enterNanos) {
            this.enterMillis = enterMillis;
            this.enterNanos = enterNanos;
            return this;
        }

        public Builder enterAt(long enterMillis) {
            return enterAt(enterMillis, 0);
        }

        public Builder exitAt(long exitMillis, long exitNanos) {
            this.exitMillis = exitMillis;
            this.exitNanos = exitNanos;
            return this;
        }

        public Builder exitAt(long exitMillis) {
            return exitAt(exitMillis, 0);
        }

        public Builder by(long threadId, String threadName) {
            return by(new Accessor(threadId, threadName));
        }

        public Builder by(Accessor accessor) {
            this.accessor = accessor;
            return this;
        }

        public Builder on(Object o) {
            return on(new ObjectUnderLock(o));
        }

        public Builder on(ObjectUnderLock objectUnderLock) {
            this.objectUnderLock = objectUnderLock;
            return this;
        }

        public Access build() {
            CriticalSectionEntered enter = new CriticalSectionEntered(enterMillis, enterNanos, accessor, objectUnderLock);
            CriticalSectionExit exit = new CriticalSectionExit(exitMillis, exitNanos, accessor, objectUnderLock);
            return new Access(enter, exit);
        }
    }
}
