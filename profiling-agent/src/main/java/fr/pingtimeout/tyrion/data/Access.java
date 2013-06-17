/*
 * Copyright (c) 2013, Pierre Laporte
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

package fr.pingtimeout.tyrion.data;

public class Access {

    private final int numberOfAccesses;


    public Access() {
        numberOfAccesses = 1;
    }


    private Access(int numberOfAccesses) {
        this.numberOfAccesses = numberOfAccesses;
    }


    public Access addOneAccess() {
        return new Access(numberOfAccesses + 1);
    }


    public int getNumberOfAccesses() {
        return numberOfAccesses;
    }

    @Override
    public String toString() {
        return numberOfAccesses + " times";
    }
}
