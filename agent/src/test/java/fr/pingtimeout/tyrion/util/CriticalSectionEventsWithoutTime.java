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

package fr.pingtimeout.tyrion.util;

import fr.pingtimeout.tyrion.model.Accessor;
import fr.pingtimeout.tyrion.model.CriticalSectionEvent;
import fr.pingtimeout.tyrion.model.ObjectUnderLock;

import java.util.Comparator;

public class CriticalSectionEventsWithoutTime implements Comparator<CriticalSectionEvent> {
    @Override
    public int compare(CriticalSectionEvent e1, CriticalSectionEvent e2) {
        Accessor e1Accessor = e1.getAccessor();
        Accessor e2Accessor = e2.getAccessor();

        ObjectUnderLock e1ObjectUnderLock = e1.getObjectUnderLock();
        ObjectUnderLock e2ObjectUnderLock = e2.getObjectUnderLock();

        if (e1.getClass().equals(e2.getClass())
                && e1Accessor.equals(e2Accessor)
                && e1ObjectUnderLock.equals(e2ObjectUnderLock))
            return 0;
        return -1;
    }
}
