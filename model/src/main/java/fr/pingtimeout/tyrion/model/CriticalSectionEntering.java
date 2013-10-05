/*
 * Copyright (c) 2013, Lukasz Celeban
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

public class CriticalSectionEntering extends CriticalSectionEvent {


    public CriticalSectionEntering(Thread accessor, Object objectUnderLock) {
        super(accessor, objectUnderLock);
    }


    // Constructor and getters required by Jackson unmashalling process
    @JsonCreator
    protected CriticalSectionEntering(
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("accessor") Accessor accessor,
            @JsonProperty("objectUnderLock") ObjectUnderLock objectUnderLock) {
        super(timestamp, accessor, objectUnderLock);
    }
}
