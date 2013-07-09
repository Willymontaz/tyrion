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

package fr.pingtimeout.tyrion.util;

import fr.pingtimeout.tyrion.model.Accessor;
import fr.pingtimeout.tyrion.model.CriticalSectionEvent;
import fr.pingtimeout.tyrion.model.Target;

import java.util.Comparator;

public class CriticalSectionEventsWithoutTimeComparator implements Comparator<CriticalSectionEvent> {
    @Override
    public int compare(CriticalSectionEvent e1, CriticalSectionEvent e2) {
        Accessor e1Accessor = e1.getAccessor();
        Accessor e2Accessor = e2.getAccessor();

        Target e1Target = e1.getTarget();
        Target e2Target = e2.getTarget();

        if (e1.getClass().equals(e2.getClass())
                && e1Accessor.equals(e2Accessor)
                && e1Target.equals(e2Target))
            return 0;
        return -1;
    }
}
