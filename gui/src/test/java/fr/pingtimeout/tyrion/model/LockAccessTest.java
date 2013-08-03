package fr.pingtimeout.tyrion.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class LockAccessTest {
    @Test
    public void should_sort_events_based_on_entry_time() {
        // Given
        Target target1 = mock(Target.class);
        Target target2 = mock(Target.class);
        Accessor accessor = mock(Accessor.class);
        Access earlyAccess = new Access(0, 10, accessor, target1);
        Access laterAccess1 = new Access(10, 20, accessor, target1);
        Access laterAccess2 = new Access(10, 30, accessor, target2);

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
