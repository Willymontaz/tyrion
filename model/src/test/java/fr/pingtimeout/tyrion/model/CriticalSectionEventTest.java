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

public class CriticalSectionEventTest {
    @Test
    public void should_sort_events_based_on_timestamp_and_type() {
        // Given
        CriticalSectionEntered firstAccess = new CriticalSectionEntered(1, mock(Accessor.class), mock(ObjectUnderLock.class));
        CriticalSectionEntered secondAccess = new CriticalSectionEntered(2, mock(Accessor.class), mock(ObjectUnderLock.class));
        CriticalSectionExit thirdAccessAtSameTimestamp = new CriticalSectionExit(2, mock(Accessor.class), mock(ObjectUnderLock.class));

        // When
        int earlyEnterComparedToLaterEnter = firstAccess.compareTo(secondAccess);
        int earlyExitComparedToLaterExit = secondAccess.compareTo(thirdAccessAtSameTimestamp);

        // Then
        assertThat(earlyEnterComparedToLaterEnter).isNegative();
        assertThat(earlyExitComparedToLaterExit).isNegative();
    }
}
