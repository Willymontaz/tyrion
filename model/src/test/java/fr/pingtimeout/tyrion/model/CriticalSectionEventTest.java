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
import java.util.Iterator;
import java.util.TreeSet;

public class CriticalSectionEventTest {
    @Test
    public void sorting_order_should_be_determined_by_millis_first_and_then_nanos() {
        // Given
        CriticalSectionEntered event_at_1_0 = new CriticalSectionEntered(1, 0, mock(Accessor.class), mock(ObjectUnderLock.class));
        CriticalSectionExit event_at_2_0 = new CriticalSectionExit(2, 0, mock(Accessor.class), mock(ObjectUnderLock.class));
        CriticalSectionEntered event_at_2_1 = new CriticalSectionEntered(2, 1, mock(Accessor.class), mock(ObjectUnderLock.class));
        CriticalSectionEntering event_at_2_2 = new CriticalSectionEntering(2, 2, mock(Accessor.class), mock(ObjectUnderLock.class));
        CriticalSectionEntering event_at_3_0 = new CriticalSectionEntering(3, 0, mock(Accessor.class), mock(ObjectUnderLock.class));

        // When
        TreeSet<CriticalSectionEvent> events = new TreeSet<>();
        events.add(event_at_2_0);
        events.add(event_at_2_2);
        events.add(event_at_3_0);
        events.add(event_at_2_1);
        events.add(event_at_1_0);

        // Then
        Iterator<CriticalSectionEvent> eventsIterator = events.iterator();
        assertThat(eventsIterator.next()).isSameAs(event_at_1_0);
        assertThat(eventsIterator.next()).isSameAs(event_at_2_0);
        assertThat(eventsIterator.next()).isSameAs(event_at_2_1);
        assertThat(eventsIterator.next()).isSameAs(event_at_2_2);
        assertThat(eventsIterator.next()).isSameAs(event_at_3_0);
    }


    @Test
    public void sorting_order_should_be_entering_entered_and_then_exit_when_timestamp_is_identical() {
        // Given
        CriticalSectionEntering entering_at_1_0 = new CriticalSectionEntering(1, 0, mock(Accessor.class), mock(ObjectUnderLock.class));
        CriticalSectionEntered entered_at_1_0 = new CriticalSectionEntered(1, 0, mock(Accessor.class), mock(ObjectUnderLock.class));
        CriticalSectionExit exit_at_1_0 = new CriticalSectionExit(1, 0, mock(Accessor.class), mock(ObjectUnderLock.class));

        // When
        TreeSet<CriticalSectionEvent> events = new TreeSet<>();
        events.add(exit_at_1_0);
        events.add(entered_at_1_0);
        events.add(entering_at_1_0) ;

        // Then
        Iterator<CriticalSectionEvent> eventsIterator = events.iterator();
        assertThat(eventsIterator.next()).isSameAs(entering_at_1_0);
        assertThat(eventsIterator.next()).isSameAs(entered_at_1_0);
        assertThat(eventsIterator.next()).isSameAs(exit_at_1_0);
    }
}
