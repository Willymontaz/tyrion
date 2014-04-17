package fr.pingtimeout.tyrion.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class AccessTest {
    @Test
    public void accesses_should_be_sorted_by_enter_time() {
        // When
        Access firstAccess = new Access.Builder()
                .by(1, "Thread-1")
                .enterAt(10).exitAt(11)
                .on(new Object())
                .build();
        Access secondAccess = new Access.Builder()
                .by(2, "Thread-2")
                .enterAt(20).exitAt(21)
                .on(new Object())
                .build();

        // Then
        Assertions.assertThat(firstAccess.compareTo(secondAccess)).isNegative();
        Assertions.assertThat(secondAccess.compareTo(firstAccess)).isPositive();
        Assertions.assertThat(firstAccess.compareTo(firstAccess)).isZero();
    }
}
