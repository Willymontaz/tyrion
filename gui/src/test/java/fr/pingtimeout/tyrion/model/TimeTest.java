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
