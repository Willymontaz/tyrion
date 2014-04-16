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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class LocksFileReaderTest {


    @Test
    public void should_transform_events_into_accesses_accessible_by_thread() {
        // Given
        String events = ""
                + "{\"enter\":{\"millis\":100,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"millis\":101,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"enter\":{\"millis\":102,\"nanos\":0,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":5,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"exit\":{\"millis\":103,\"nanos\":0,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":5,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"enter\":{\"millis\":103,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":5,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"exit\":{\"millis\":107,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":5,\"className\":\"java.lang.Object\"}}}\n";
        InputStream inputStream = IOUtils.toInputStream(events);

        Accessor accessor1 = new Accessor(1L, "Thread 1");
        Accessor accessor2 = new Accessor(2L, "Thread 2");
        ObjectUnderLock lock1 = new ObjectUnderLock("java.lang.Class", 4L);
        ObjectUnderLock lock2 = new ObjectUnderLock("java.lang.Object", 5L);

        Access accessFromThread1ToLock1 = new Access.Builder().enterAt(100).exitAt(101L).by(accessor1).on(lock1).build();
        Access accessFromThread2ToLock2 = new Access.Builder().enterAt(102).exitAt(103).by(accessor2).on(lock2).build();
        Access accessFromThread1ToLock2 = new Access.Builder().enterAt(103).exitAt(107).by(accessor1).on(lock2).build();


        // When
        AccessReport accessReport = new LocksFileReader(inputStream).buildAccessReport();

        Set<Access> thread1CriticalSections = accessReport.retrieveCriticalSectionsFor(accessor1);
        Set<Access> thread2CriticalSections = accessReport.retrieveCriticalSectionsFor(accessor2);


        // Then
        assertThat(thread1CriticalSections).containsOnly(accessFromThread1ToLock1, accessFromThread1ToLock2);
        assertThat(thread2CriticalSections).containsOnly(accessFromThread2ToLock2);
    }


    @Test
    public void should_transform_events_into_accesses_accessible_by_monitor() {
        // Given
        String events = ""
                + "{\"enter\":{\"millis\":100,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"millis\":101,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"enter\":{\"millis\":102,\"nanos\":0,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":5,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"exit\":{\"millis\":103,\"nanos\":0,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":5,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"enter\":{\"millis\":103,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":5,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"exit\":{\"millis\":107,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":5,\"className\":\"java.lang.Object\"}}}\n";
        InputStream inputStream = IOUtils.toInputStream(events);

        Accessor accessor1 = new Accessor(1L, "Thread 1");
        Accessor accessor2 = new Accessor(2L, "Thread 2");
        ObjectUnderLock lock1 = new ObjectUnderLock("java.lang.Class", 4L);
        ObjectUnderLock lock2 = new ObjectUnderLock("java.lang.Object", 5L);

        Access accessFromThread1ToLock1 = new Access.Builder().enterAt(100).exitAt(101).by(accessor1).on(lock1).build();
        Access accessFromThread2ToLock2 = new Access.Builder().enterAt(102).exitAt(103).by(accessor2).on(lock2).build();
        Access accessFromThread1ToLock2 = new Access.Builder().enterAt(103).exitAt(107).by(accessor1).on(lock2).build();


        // When
        AccessReport accessReport = new LocksFileReader(inputStream).buildAccessReport();

        Set<Access> lock1CriticalSections = accessReport.retrieveCriticalSectionsFor(lock1);
        Set<Access> lock2CriticalSections = accessReport.retrieveCriticalSectionsFor(lock2);


        // Then
        assertThat(lock1CriticalSections).containsOnly(accessFromThread1ToLock1);
        assertThat(lock2CriticalSections).containsOnly(accessFromThread1ToLock2, accessFromThread2ToLock2);
    }


    @Test
    public void should_count_accesses_by_monitor() {
        // Given
        String events = ""
                + "{\"enter\":{\"millis\":100,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"millis\":101,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"enter\":{\"millis\":102,\"nanos\":0,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":5,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"exit\":{\"millis\":103,\"nanos\":0,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":5,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"enter\":{\"millis\":103,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":5,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"exit\":{\"millis\":107,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":5,\"className\":\"java.lang.Object\"}}}\n";
        InputStream inputStream = IOUtils.toInputStream(events);

        ObjectUnderLock lock1 = new ObjectUnderLock("java.lang.Class", 4L);
        ObjectUnderLock lock2 = new ObjectUnderLock("java.lang.Object", 5L);


        // When
        AccessReport accessReport = new LocksFileReader(inputStream).buildAccessReport();

        int accessorsOfLock1 = accessReport.countDifferentAccessorsFor(lock1);
        int accessorsOfLock2 = accessReport.countDifferentAccessorsFor(lock2);


        // Then
        assertThat(accessorsOfLock1).isEqualTo(1);
        assertThat(accessorsOfLock2).isEqualTo(2);
    }


    @Test
    public void should_detect_frequent_accesses_with_margin() {
        // Given
        String events = ""
                + "{\"enter\":{\"millis\":95,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"millis\":96,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"enter\":{\"millis\":100,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"millis\":101,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"enter\":{\"millis\":102,\"nanos\":0,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"millis\":103,\"nanos\":0,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"enter\":{\"millis\":108,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"millis\":110,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n";
        InputStream inputStream = IOUtils.toInputStream(events);

        ObjectUnderLock lock1 = new ObjectUnderLock("java.lang.Class", 4L);

        Access accessFromThread1At95 = new Access.Builder().enterAt(95).exitAt(96).by(1L, "Thread 1").on(lock1).build();
        Access accessFromThread1At100 = new Access.Builder().enterAt(100).exitAt(101).by(1L, "Thread 1").on(lock1).build();
        Access accessFromThread2At102 = new Access.Builder().enterAt(102).exitAt(103).by(2L, "Thread 2").on(lock1).build();


        // When
        AccessReport accessReport = new LocksFileReader(inputStream).buildAccessReport();

        Set<Access> frequentAccesses = accessReport.retrieveFrequentAccesses(lock1, 4, MILLISECONDS);


        // Then
        assertThat(frequentAccesses).containsOnly(accessFromThread1At95, accessFromThread1At100, accessFromThread2At102);
    }


    @Test
    public void should_detect_contended_accesses_with_margin() {
        // Given
        String events = ""
                + "{\"enter\":{\"millis\":95,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"millis\":96,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"enter\":{\"millis\":100,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"millis\":101,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"enter\":{\"millis\":102,\"nanos\":0,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"millis\":103,\"nanos\":0,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"enter\":{\"millis\":108,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"millis\":110,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n";
        InputStream inputStream = IOUtils.toInputStream(events);

        Accessor accessor1 = new Accessor(1L, "Thread 1");
        Accessor accessor2 = new Accessor(2L, "Thread 2");
        ObjectUnderLock lock1 = new ObjectUnderLock("java.lang.Class", 4L);

        Access accessFromThread1At100 = new Access.Builder().enterAt(100).exitAt(101).by(accessor1).on(lock1).build();
        Access accessFromThread2At102 = new Access.Builder().enterAt(102).exitAt(103).by(accessor2).on(lock1).build();


        // When
        AccessReport accessReport = new LocksFileReader(inputStream).buildAccessReport();

        Set<Access> contentedAccesses = accessReport.retrieveContendedAccesses(lock1, 4, MILLISECONDS);


        // Then
        assertThat(contentedAccesses).containsOnly(accessFromThread1At100, accessFromThread2At102);
    }


    @Test
    public void should_detect_contended_accesses_on_all_locks() {
        // Given
        String events = ""
                + "{\"enter\":{\"millis\":100,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"millis\":101,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"enter\":{\"millis\":102,\"nanos\":0,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"millis\":103,\"nanos\":0,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n"

                + "{\"enter\":{\"millis\":110,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":6,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"exit\":{\"millis\":111,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":6,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"enter\":{\"millis\":112,\"nanos\":0,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":6,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"exit\":{\"millis\":113,\"nanos\":0,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":6,\"className\":\"java.lang.Object\"}}}\n"

                + "{\"enter\":{\"millis\":121,\"nanos\":0,\"accessor\":{\"id\":3,\"name\":\"Thread 3\"},\"target\":{\"hashcode\":7,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"exit\":{\"millis\":122,\"nanos\":0,\"accessor\":{\"id\":3,\"name\":\"Thread 3\"},\"target\":{\"hashcode\":7,\"className\":\"java.lang.Object\"}}}\n";
        InputStream inputStream = IOUtils.toInputStream(events);

        Accessor accessor1 = new Accessor(1L, "Thread 1");
        Accessor accessor2 = new Accessor(2L, "Thread 2");
        Accessor accessor3 = new Accessor(3L, "Thread 3");
        ObjectUnderLock lock1 = new ObjectUnderLock("java.lang.Class", 4L);
        ObjectUnderLock lock2 = new ObjectUnderLock("java.lang.Object", 6L);
        ObjectUnderLock lock3 = new ObjectUnderLock("java.lang.Object", 7L);

        Access accessOfLock1FromThread1At100 = new Access.Builder().enterAt(100).exitAt(101).by(accessor1).on(lock1).build();
        Access accessOfLock1FromThread2At102 = new Access.Builder().enterAt(102).exitAt(103).by(accessor2).on(lock1).build();
        Access accessOfLock2FromThread1At110 = new Access.Builder().enterAt(110).exitAt(111).by(accessor1).on(lock2).build();
        Access accessOfLock2FromThread2At112 = new Access.Builder().enterAt(112).exitAt(113).by(accessor2).on(lock2).build();


        // When
        AccessReport accessReport = new LocksFileReader(inputStream).buildAccessReport();
        Map<ObjectUnderLock, Set<Access>> contentedAccesses = accessReport.retrieveAllContendedAccesses(1, MILLISECONDS);


        // Then
        assertThat(contentedAccesses.get(lock1)).containsOnly(accessOfLock1FromThread1At100, accessOfLock1FromThread2At102);
        assertThat(contentedAccesses.get(lock2)).containsOnly(accessOfLock2FromThread1At110, accessOfLock2FromThread2At112);
        assertThat(contentedAccesses).hasSize(2);
    }

    @Test
    public void should_ignore_entering_event_for_now() {
        // TODO: this test is going to be removed once we will implement handling for entering event
        // Given
        String events = ""
                + "{\"entering\":{\"millis\":100,\"nanos\":0,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":4,\"className\":\"java.lang.Class\"}}}\n";
        InputStream inputStream = IOUtils.toInputStream(events);

        // When
        new LocksFileReader(inputStream).buildAccessReport();

        // Then
        // should not throw an exception
    }


}
