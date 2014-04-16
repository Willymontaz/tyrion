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

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pingtimeout.tyrion.util.HashCodeSource;
import fr.pingtimeout.tyrion.util.TimeSource;
import org.junit.Test;

import static java.lang.Thread.currentThread;
import static org.assertj.core.api.Assertions.assertThat;

public class SerializationTest {

    @Test
    public void output_format_of_entering_events_should_be_json_with_all_needed_information() throws Exception {
        // Given
        ObjectMapper jsonMapper = new ObjectMapper();
        CriticalSectionEvent.timeSource = new ConstantTimeSource(42, 1);
        ObjectUnderLock.hashCodeSource = new ConstantHashCodeSource(1337);

        // When
        Object target = new Object();
        String entering = jsonMapper.writeValueAsString(new CriticalSectionEntering(currentThread(), target));

        // Then
        assertThat(entering).isEqualTo(
                "{'entering':{'accessor':{'id':1,'name':'main'},'target':{'hashcode':1337,'className':'java.lang.Object'},'time':{'millis':42,'nanos':1}}}"
                        .replace('\'', '"')
        );
    }


    @Test
    public void output_format_of_entered_events_should_be_json_with_all_needed_information() throws Exception {
        // Given
        ObjectMapper jsonMapper = new ObjectMapper();
        CriticalSectionEvent.timeSource = new ConstantTimeSource(43, 2);
        ObjectUnderLock.hashCodeSource = new ConstantHashCodeSource(1338);

        // When
        Object target = new Object();
        String entered = jsonMapper.writeValueAsString(new CriticalSectionEntered(currentThread(), target));

        // Then
        assertThat(entered).isEqualTo(
                "{'enter':{'accessor':{'id':1,'name':'main'},'target':{'hashcode':1338,'className':'java.lang.Object'},'time':{'millis':43,'nanos':2}}}"
                        .replace('\'', '"')
        );
    }


    @Test
    public void output_format_of_exit_events_should_be_json_with_all_needed_information() throws Exception {
        // Given
        ObjectMapper jsonMapper = new ObjectMapper();
        CriticalSectionEvent.timeSource = new ConstantTimeSource(44, 3);
        ObjectUnderLock.hashCodeSource = new ConstantHashCodeSource(1339);

        // When
        Object target = new Object();
        String exit = jsonMapper.writeValueAsString(new CriticalSectionExit(currentThread(), target));

        // Then
        assertThat(exit).isEqualTo(
                "{'exit':{'accessor':{'id':1,'name':'main'},'target':{'hashcode':1339,'className':'java.lang.Object'},'time':{'millis':44,'nanos':3}}}"
                        .replace('\'', '"')
        );
    }
}


class ConstantTimeSource extends TimeSource {

    private final int millis;
    private final int nanos;

    ConstantTimeSource(int millis, int nanos) {
        this.millis = millis;
        this.nanos = nanos;
    }

    @Override
    public long currentTimeMillis() {
        return millis;
    }

    @Override
    public long currentTimeNanos() {
        return nanos;
    }
}


class ConstantHashCodeSource extends HashCodeSource {

    private int hashcode;

    ConstantHashCodeSource(int hashcode) {
        this.hashcode = hashcode;
    }

    @Override
    public long hashCodeOf(Object o) {
        return hashcode;
    }
}