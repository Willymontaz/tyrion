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
