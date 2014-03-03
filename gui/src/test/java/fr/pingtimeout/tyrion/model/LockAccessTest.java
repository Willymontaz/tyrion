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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class LockAccessTest {
    @Test
    public void should_sort_events_based_on_entry_time() {
        // Given
        ObjectUnderLock objectUnderLock1 = mock(ObjectUnderLock.class);
        ObjectUnderLock objectUnderLock2 = mock(ObjectUnderLock.class);
        Accessor accessor = mock(Accessor.class);
        Access earlyAccess = new Access(0, 10, accessor, objectUnderLock1);
        Access laterAccess1 = new Access(10, 20, accessor, objectUnderLock1);
        Access laterAccess2 = new Access(10, 30, accessor, objectUnderLock2);

        // When
        int earlyComparedToLater1 = earlyAccess.compareTo(laterAccess1);
        int earlyComparedToLater2 = earlyAccess.compareTo(laterAccess2);
        int later1ComparedToEarly = laterAccess1.compareTo(earlyAccess);
        int later2ComparedToEarly = laterAccess2.compareTo(earlyAccess);
        int laterAccessesCompared = laterAccess1.compareTo(laterAccess2);

        // Then
        assertThat(earlyComparedToLater1).isNegative();
        assertThat(earlyComparedToLater2).isNegative();
        assertThat(later1ComparedToEarly).isPositive();
        assertThat(later2ComparedToEarly).isPositive();
        assertThat(laterAccessesCompared).isZero();
    }

}
