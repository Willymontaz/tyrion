package fr.pingtimeout.tyrion.agent;

import fr.pingtimeout.tyrion.util.EventsHolder;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LockInterceptorTest {
    @Test
    public void test_entry_in_critical_section() {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        LockInterceptor.eventsHolder = eventsHolder;
        Object lock = new Object();

        // When
        LockInterceptor.enteredCriticalSection(lock);

        // Then
        verify(eventsHolder, times(1)).recordNewEntry(Thread.currentThread(), lock);
    }

    @Test
    public void test_exit_from_critical_section() {
        // Given
        EventsHolder eventsHolder = mock(EventsHolder.class);
        LockInterceptor.eventsHolder = eventsHolder;
        Object lock = new Object();

        // When
        LockInterceptor.leavingCriticalSection(lock);

        // Then
        verify(eventsHolder, times(1)).recordNewExit(Thread.currentThread(), lock);
    }
}
