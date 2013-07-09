package fr.pingtimeout.tyrion.agent;

import fr.pingtimeout.tyrion.util.EventsHolder;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Mockito.*;

public class LockInterceptorTest {
    @Test
    public void test_entry_in_critical_section_when_enabled() {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        LockInterceptor.eventsHolder = eventsHolder;
        LockInterceptor.enabled.getAndSet(true);
        Object lock = new Object();

        // When
        LockInterceptor.enteredCriticalSection(lock);

        // Then
        verify(eventsHolder, times(1)).recordNewEntry(Thread.currentThread(), lock);
    }

    @Test
    public void test_exit_from_critical_section_when_disabled() {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        LockInterceptor.eventsHolder = eventsHolder;
        LockInterceptor.enabled.getAndSet(false);
        Object lock = new Object();

        // When
        LockInterceptor.leavingCriticalSection(lock);

        // Then
        verify(eventsHolder, never()).recordNewExit(Thread.currentThread(), lock);
    }

    @Test
    public void test_entry_in_critical_section_when_disabled() {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        LockInterceptor.eventsHolder = eventsHolder;
        LockInterceptor.enabled.getAndSet(true);
        Object lock = new Object();

        // When
        LockInterceptor.enteredCriticalSection(lock);

        // Then
        verify(eventsHolder, times(1)).recordNewEntry(Thread.currentThread(), lock);
    }

    @Test
    public void test_exit_from_critical_section_when_enabled() {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        LockInterceptor.eventsHolder = eventsHolder;
        LockInterceptor.enabled.getAndSet(false);
        Object lock = new Object();

        // When
        LockInterceptor.leavingCriticalSection(lock);

        // Then
        verify(eventsHolder, never()).recordNewExit(Thread.currentThread(), lock);
    }
}
