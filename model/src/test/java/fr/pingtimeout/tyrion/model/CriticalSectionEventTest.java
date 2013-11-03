package fr.pingtimeout.tyrion.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import java.util.Iterator;
import java.util.TreeSet;

public class CriticalSectionEventTest {
    @Test
    public void should_sort_events_based_on_type_when_timestamp_is_identical() {
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


    @Test
    public void should_sort_events_based_on_millis_first_and_nanos_if_same_millis() {
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
        events.add(event_at_2_1);
        events.add(event_at_3_0);
        events.add(event_at_1_0);

        // Then
        Iterator<CriticalSectionEvent> eventsIterator = events.iterator();
        assertThat(eventsIterator.next()).isSameAs(event_at_1_0);
        assertThat(eventsIterator.next()).isSameAs(event_at_2_0);
        assertThat(eventsIterator.next()).isSameAs(event_at_2_1);
        assertThat(eventsIterator.next()).isSameAs(event_at_2_2);
        assertThat(eventsIterator.next()).isSameAs(event_at_3_0);
    }
}
