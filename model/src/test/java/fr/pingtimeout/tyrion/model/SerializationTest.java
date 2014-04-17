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

import static java.lang.Thread.currentThread;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import fr.pingtimeout.tyrion.util.HashCodeSource;
import fr.pingtimeout.tyrion.util.TimeSource;

public class SerializationTest {

    @Test
    public void output_format_of_entering_events_should_be_json_with_all_needed_information() throws Exception {
        // Given
        CriticalSectionEvent.timeSource = new ConstantTimeSource(new Time(42, 1));
        ObjectUnderLock.hashCodeSource = new ConstantHashCodeSource(1337);

        // When
        Object target = new Object();
        String serializedEvent = new CriticalSectionEntering(currentThread(), target).serializeToJsonString();

        // Then
        assertThat(serializedEvent).isEqualTo(
                "{'entering':{'accessor':{'id':1,'name':'main'},'target':{'hashcode':1337,'className':'java.lang.Object'},'time':{'millis':42,'nanos':1}}}"
                        .replace('\'', '"')
        );
    }


    @Test
    public void output_format_of_entered_events_should_be_json_with_all_needed_information() throws Exception {
        // Given
        CriticalSectionEvent.timeSource = new ConstantTimeSource(new Time(43, 2));
        ObjectUnderLock.hashCodeSource = new ConstantHashCodeSource(1338);

        // When
        Object target = new Object();
        String serializedEvent = new CriticalSectionEntered(currentThread(), target).serializeToJsonString();

        // Then
        assertThat(serializedEvent).isEqualTo(
                "{'enter':{'accessor':{'id':1,'name':'main'},'target':{'hashcode':1338,'className':'java.lang.Object'},'time':{'millis':43,'nanos':2}}}"
                        .replace('\'', '"')
        );
    }


    @Test
    public void output_format_of_exit_events_should_be_json_with_all_needed_information() throws Exception {
        // Given
        CriticalSectionEvent.timeSource = new ConstantTimeSource(new Time(44, 3));
        ObjectUnderLock.hashCodeSource = new ConstantHashCodeSource(1339);

        // When
        Object target = new Object();
        String serializedEvent = new CriticalSectionExit(currentThread(), target).serializeToJsonString();

        // Then
        assertThat(serializedEvent).isEqualTo(
                "{'exit':{'accessor':{'id':1,'name':'main'},'target':{'hashcode':1339,'className':'java.lang.Object'},'time':{'millis':44,'nanos':3}}}"
                        .replace('\'', '"')
        );
    }

    @Test
    public void foo() throws Exception {
        // Given
        long millis = 8_839_064_868_652_493_482L;
        long nanos = 8_454_757_700_450_211_157L;
        int hashcode = 1339;
        long threadId = currentThread().getId();
        String threadName = "main";

        CriticalSectionEvent.timeSource = new ConstantTimeSource(new Time(millis, nanos));
        ObjectUnderLock.hashCodeSource = new ConstantHashCodeSource(hashcode);

        // When
        byte[] serializedEvent = new CriticalSectionExit(currentThread(), new Object()).serializeToRawString();

        // Then
        assertThat(buildLongFromByteArray(serializedEvent, 0)).isEqualTo(millis);
        assertThat(buildLongFromByteArray(serializedEvent, 8)).isEqualTo(nanos);
        assertThat(buildCharFromByteArray(serializedEvent, 16)).isEqualTo('x');

    }

    private long buildLongFromByteArray(byte[] serializedEvent, int startOffset) {
        long value = 0;
        for (int i = startOffset; i < startOffset + 8; i++) {
            value = (value << 8) + (serializedEvent[i] & 0xff);
        }
        return value;
    }

    private char buildCharFromByteArray(byte[] serializedEvent, int startOffset) {
        char value = 0;
        for (int i = startOffset; i < startOffset + 2; i++) {
            value = (char) ((value << 8) + (serializedEvent[i] & 0xff));
        }
        return value;
    }

    private int buildIntFromByteArray(byte[] serializedEvent, int startOffset) {
        int value = 0;
        for (int i = startOffset; i < startOffset + 4; i++) {
            value = (value << 8) + (serializedEvent[i] & 0xff);
        }
        return value;
    }
}


class ConstantTimeSource extends TimeSource {
    private final Time time;

    public ConstantTimeSource(Time time) {
        this.time = time;
    }

    @Override
    public Time currentTime() {
        return time;
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