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

public class ClassUnderLock extends ObjectUnderLock {

    private final String classLoaderName;
    private final long classLoaderHashcode;

    public ClassUnderLock(Class<?> target) {
        super(target);

        ClassLoader classLoader = target.getClassLoader();
        this.classLoaderName = classLoader.getClass().getName();
        this.classLoaderHashcode = System.identityHashCode(classLoader);
    }

    @JsonCreator
    public ClassUnderLock(
            @JsonProperty("className") String className,
            @JsonProperty("hashcode") long hashcode,
            @JsonProperty("classLoaderName") String classLoaderName,
            @JsonProperty("classLoaderhashcode") long classLoaderHashcode) {
        super(className, hashcode);
        this.classLoaderName = classLoaderName;
        this.classLoaderHashcode = classLoaderHashcode;
    }
}
