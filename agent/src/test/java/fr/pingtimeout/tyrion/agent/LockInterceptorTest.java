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

package fr.pingtimeout.tyrion.agent;

import fr.pingtimeout.tyrion.util.EventsHolder;
import fr.pingtimeout.tyrion.util.EventsWriter;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Mockito.*;

public class LockInterceptorTest {
    @Test
    public void test_entering_critical_section_when_enabled() {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        LockInterceptor lockInterceptor = new LockInterceptor(eventsHolder, new AtomicBoolean(true));
        Object lock = new Object();

        // When
        lockInterceptor.enteringCriticalSection(lock);

        // Then
        verify(eventsHolder, times(1)).recordNewEntering(Thread.currentThread(), lock);
    }

    @Test
    public void test_entry_in_critical_section_when_enabled() {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        LockInterceptor lockInterceptor = new LockInterceptor(eventsHolder, new AtomicBoolean(true));
        Object lock = new Object();

        // When
        lockInterceptor.enteredCriticalSection(lock);

        // Then
        verify(eventsHolder, times(1)).recordNewEntry(Thread.currentThread(), lock);
    }

    @Test
    public void test_exit_from_critical_section_when_enabled() {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        LockInterceptor lockInterceptor = new LockInterceptor(eventsHolder, new AtomicBoolean(true));
        Object lock = new Object();

        // When
        lockInterceptor.leavingCriticalSection(lock);

        // Then
        verify(eventsHolder, times(1)).recordNewExit(Thread.currentThread(), lock);
    }

    @Test
    public void test_entering_critical_section_when_disabled() {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        LockInterceptor lockInterceptor = new LockInterceptor(eventsHolder, new AtomicBoolean(false));
        Object lock = new Object();

        // When
        lockInterceptor.enteringCriticalSection(lock);

        // Then
        verifyZeroInteractions(eventsHolder);
    }

    @Test
    public void test_entry_in_critical_section_when_disabled() {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        LockInterceptor lockInterceptor = new LockInterceptor(eventsHolder, new AtomicBoolean(false));
        Object lock = new Object();

        // When
        lockInterceptor.enteredCriticalSection(lock);

        // Then
        verifyZeroInteractions(eventsHolder);
    }

    @Test
    public void test_exit_from_critical_section_when_disabled() {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        LockInterceptor lockInterceptor = new LockInterceptor(eventsHolder, new AtomicBoolean(false));
        Object lock = new Object();

        // When
        lockInterceptor.leavingCriticalSection(lock);

        // Then
        verifyZeroInteractions(eventsHolder);
    }

    @Test
    public void should_retrieve_a_class_by_its_name_and_classloader() {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        LockInterceptor lockInterceptor = new LockInterceptor(eventsHolder, new AtomicBoolean(true));

        // When
        Class<?> result = lockInterceptor.classForName("java.lang.String");

        // Then
        Assertions.assertThat(result).isEqualTo(String.class);
    }

    @Test
    public void test_entry_in_critical_section_from_excluded_thread() throws InterruptedException {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        final LockInterceptor lockInterceptor = new LockInterceptor(eventsHolder, new AtomicBoolean(true));
        final Object lock = new Object();
        lockInterceptor.addExcludedThread(EventsWriter.THREAD_NAME);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                lockInterceptor.enteredCriticalSection(lock);
            }
        };
        Thread eventsWriterThread = new Thread(runnable, EventsWriter.THREAD_NAME);

        // When
        eventsWriterThread.start();
        eventsWriterThread.join(TimeUnit.SECONDS.toMillis(1));

        // Then
        verifyZeroInteractions(eventsHolder);
    }

    @Test
    public void test_exit_from_critical_section_from_excluded_thread() throws InterruptedException {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        final LockInterceptor lockInterceptor = new LockInterceptor(eventsHolder, new AtomicBoolean(true));
        final Object lock = new Object();
        lockInterceptor.addExcludedThread(EventsWriter.THREAD_NAME);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                lockInterceptor.leavingCriticalSection(lock);
            }
        };
        Thread eventsWriterThread = new Thread(runnable, EventsWriter.THREAD_NAME);

        // When
        eventsWriterThread.start();
        eventsWriterThread.join(TimeUnit.SECONDS.toMillis(1));

        // Then
        verifyZeroInteractions(eventsHolder);
    }
}
