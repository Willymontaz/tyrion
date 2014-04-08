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
import fr.pingtimeout.tyrion.util.HashCodeSource;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@ToString
public class ObjectUnderLock {

    static HashCodeSource hashCodeSource = new HashCodeSource();

    private final String className;

    private final long hashcode;

    public ObjectUnderLock(Object target) {
        this.className = target.getClass().getName();
        this.hashcode = hashCodeSource.hashCodeOf(target);
    }

    @JsonCreator
    public ObjectUnderLock(
            @JsonProperty("class") String className,
            @JsonProperty("hashcode") long hashcode) {
        this.className = className;
        this.hashcode = hashcode;
    }
}
