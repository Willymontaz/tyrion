package fr.pingtimeout.tyrion.model;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeTest {
    @Test
    public void should_detect_similar_accesses_based_on_entry_time() {
        // Given
        Time timeAt40 = new Time(40L, 41L);
        Time timeAt43 = new Time(43L, 44L);
        Time timeAt44 = new Time(44L, 45L);


        // When
        boolean timeAt40MatchesTimeAt43 = timeAt40.matches(timeAt43, 2, TimeUnit.MILLISECONDS);
        boolean timeAt40MatchesTimeAt44 = timeAt40.matches(timeAt44, 2, TimeUnit.MILLISECONDS);
        boolean timeAt44MatchesTimeAt40 = timeAt44.matches(timeAt40, 2, TimeUnit.MINUTES);


        // Then
        assertThat(timeAt40MatchesTimeAt43).isTrue();
        assertThat(timeAt40MatchesTimeAt44).isFalse();
        assertThat(timeAt44MatchesTimeAt40).isFalse();
    }

}
