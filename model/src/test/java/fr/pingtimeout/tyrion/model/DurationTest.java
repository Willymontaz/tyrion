package fr.pingtimeout.tyrion.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class DurationTest {
    @Test
    public void durations_should_be_sorted_by_the_enter_millis_first_and_then_the_enter_nanos() {
        // When
        Duration from_10_0_to_12_0 = new Duration(10, 0, 12, 0);
        Duration from_11_0_to_12_0 = new Duration(11, 0, 12, 0);
        Duration from_11_1_to_12_0 = new Duration(11, 1, 12, 0);

        // Then
        assertThat(from_10_0_to_12_0.compareTo(from_11_0_to_12_0)).isNegative();
        assertThat(from_11_0_to_12_0.compareTo(from_10_0_to_12_0)).isPositive();

        assertThat(from_10_0_to_12_0.compareTo(from_11_1_to_12_0)).isNegative();
        assertThat(from_11_1_to_12_0.compareTo(from_10_0_to_12_0)).isPositive();

        assertThat(from_11_0_to_12_0.compareTo(from_11_1_to_12_0)).isNegative();
        assertThat(from_11_1_to_12_0.compareTo(from_11_0_to_12_0)).isPositive();

        assertThat(from_10_0_to_12_0.compareTo(from_10_0_to_12_0)).isZero();
    }

    @Test
    public void durations_with_same_timestamps_should_intersect() {
        // Given
        Duration duration = new Duration(11, 0, 12, 0);

        // When
        boolean intersects = duration.intersectsWithin(duration);

        // Then
        assertThat(intersects).isTrue();
    }

    @Test
    public void durations_with_milliseconds_level_intersection_should_intersect() {
        // When
        Duration from_10_to_13 = new Duration(new Time(10, 0), new Time(13, 0));
        Duration from_11_to_12 = new Duration(new Time(11, 0), new Time(12, 0));
        Duration from_13_to_14 = new Duration(new Time(13, 0), new Time(14, 0));

        // Then
        assertThat(from_10_to_13.intersectsWithin(from_11_to_12)).isTrue();
        assertThat(from_11_to_12.intersectsWithin(from_10_to_13)).isTrue();

        assertThat(from_11_to_12.intersectsWithin(from_13_to_14)).isFalse();
        assertThat(from_13_to_14.intersectsWithin(from_11_to_12)).isFalse();

        assertThat(from_10_to_13.intersectsWithin(from_13_to_14)).isFalse();
        assertThat(from_13_to_14.intersectsWithin(from_10_to_13)).isFalse();
    }
}
