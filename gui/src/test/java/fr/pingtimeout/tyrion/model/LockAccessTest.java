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
    public void should_sort_events_based_on_entry_time_millis_first() {
        // Given
        ObjectUnderLock lock1 = mock(ObjectUnderLock.class);
        ObjectUnderLock lock2 = mock(ObjectUnderLock.class);
        Accessor accessor = mock(Accessor.class);

        // When
        Access firstAccess = new Access.Builder().enterAt(0,0).exitAt(10,0).by(accessor).on(lock1).build();
        Access secondAccess = new Access.Builder().enterAt(5,0).exitAt(10,0).by(accessor).on(lock2).build();
        Access thirdAccess = new Access.Builder().enterAt(10,123).exitAt(20,0).by(accessor).on(lock1).build();

        // Then
        assertThat(firstAccess.compareTo(thirdAccess)).isNegative();
        assertThat(firstAccess.compareTo(secondAccess)).isNegative();

        assertThat(secondAccess.compareTo(firstAccess)).isPositive();
        assertThat(secondAccess.compareTo(thirdAccess)).isNegative();

        assertThat(thirdAccess.compareTo(firstAccess)).isPositive();
        assertThat(thirdAccess.compareTo(secondAccess)).isPositive();
    }

}
