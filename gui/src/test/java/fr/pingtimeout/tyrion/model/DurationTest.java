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

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class DurationTest {
    @Test
    public void intersection_should_not_match_when_times_millis_do_not_intersect() {
        // Given
        Duration between_40_00_and_41_00 = new Duration(40L, 0L, 41L, 0L);
        Duration between_43_00_and_44_00 = new Duration(43L, 0L, 44L, 0L);

        // When
        boolean match = between_43_00_and_44_00.intersectsWithin(between_40_00_and_41_00);
        boolean associativeMatch = between_40_00_and_41_00.intersectsWithin(between_43_00_and_44_00);

        // Then
        assertThat(match).isFalse();
        assertThat(associativeMatch).isFalse();
    }

    @Test
    public void intersection_should_match_when_times_millis_do_intersect() {
        // Given
        Duration between_40_00_and_41_00 = new Duration(40L, 0L, 44L, 0L);
        Duration between_43_00_and_44_00 = new Duration(43L, 0L, 45L, 0L);

        // When
        boolean match = between_40_00_and_41_00.intersectsWithin(between_43_00_and_44_00);
        boolean associativeMatch = between_43_00_and_44_00.intersectsWithin(between_40_00_and_41_00);

        // Then
        assertThat(match).isTrue();
        assertThat(associativeMatch).isTrue();
    }

    @Test
    public void intersection_should_always_match_self() {
        // Given
        Duration t = new Duration(40L, 0L, 44L, 0L);

        // When
        boolean match = t.intersectsWithin(t);

        // Then
        assertThat(match).isTrue();
    }

//    @Test
//    public void intersection_should_not_match_when_delta_is_too_low() {
//        // Given
//        Duration between_40_00_and_41_00 = new Duration(40L, 0L, 41L, 0L);
//        Duration between_43_00_and_44_00 = new Duration(43L, 0L, 44L, 0L);
//
//        // When
//        boolean timesMatch = between_40_00_and_41_00.intersectsWithin(between_43_00_and_44_00, 1, MILLISECONDS);
//
//        // Then
//        assertThat(timesMatch).isFalse();
//    }
//
//    @Test
//    public void intersection_should_match_when_delta_is_high_enough() {
//        // Given
//        Duration between_40_00_and_41_00 = new Duration(40L, 0L, 41L, 0L);
//        Duration between_43_00_and_44_00 = new Duration(43L, 0L, 44L, 0L);
//
//        // When
//        boolean timesMatch = between_40_00_and_41_00.intersectsWithin(between_43_00_and_44_00, 5, MILLISECONDS);
//
//        // Then
//        assertThat(timesMatch).isTrue();
//    }
//
//    @Test
//    public void intersection_should_not_match_when_delta_is_too_low_and_target_preceeds_source() {
//        // Given
//        Duration between_40_00_and_41_00 = new Duration(40L, 0L, 41L, 0L);
//        Duration between_43_00_and_44_00 = new Duration(43L, 0L, 44L, 0L);
//
//        // When
//        boolean timesMatch = between_43_00_and_44_00.intersectsWithin(between_40_00_and_41_00, 1, MILLISECONDS);
//
//        // Then
//        assertThat(timesMatch).isFalse();
//    }
//
//    @Test
//    public void intersection_operation_should_be_associative() {
//        // Given
//        Duration between_40_00_and_41_00 = new Duration(40L, 0L, 41L, 0L);
//        Duration between_43_00_and_44_00 = new Duration(43L, 0L, 44L, 0L);
//
//        // When
//        boolean timesMatch = between_43_00_and_44_00.intersectsWithin(between_40_00_and_41_00, 5, MILLISECONDS);
//
//        // Then
//        assertThat(timesMatch).isTrue();
//    }
//
//    @Test
//    public void any_time_should_always_intersect_with_itself() {
//        // Given
//        Duration time = new Duration(40L, 0L, 41L, 0L);
//
//        // When
//        boolean timesMatch = time.intersectsWithin(time, 0, MILLISECONDS);
//
//        // Then
//        assertThat(timesMatch).isTrue();
//    }

}
