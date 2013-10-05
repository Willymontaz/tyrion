package fr.pingtimeout.tyrion.agent;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StaticAccessorTest {
    @Test
    public void should_delegate_entering_critical_section_to_LockInterceptor() {
        // Given
        LockInterceptor mockedLockInterceptor = mock(LockInterceptor.class);
        StaticAccessor.lockInterceptor = mockedLockInterceptor;
        Object mockedTarget = mock(Object.class);

        // When
        StaticAccessor.enteringCriticalSection(mockedTarget);

        // Then
        verify(mockedLockInterceptor).enteringCriticalSection(mockedTarget);
    }

    @Test
    public void should_delegate_entered_critical_section_to_LockInterceptor() {
        // Given
        LockInterceptor mockedLockInterceptor = mock(LockInterceptor.class);
        StaticAccessor.lockInterceptor = mockedLockInterceptor;
        Object mockedTarget = mock(Object.class);

        // When
        StaticAccessor.enteredCriticalSection(mockedTarget);

        // Then
        verify(mockedLockInterceptor).enteredCriticalSection(mockedTarget);
    }

    @Test
    public void should_delegate_leaving_critical_section_to_LockInterceptor() {
        // Given
        LockInterceptor mockedLockInterceptor = mock(LockInterceptor.class);
        StaticAccessor.lockInterceptor = mockedLockInterceptor;
        Object mockedTarget = mock(Object.class);

        // When
        StaticAccessor.leavingCriticalSection(mockedTarget);

        // Then
        verify(mockedLockInterceptor).leavingCriticalSection(mockedTarget);
    }

    @Test
    public void should_delegate_class_forName_to_LockInterceptor() {
        // Given
        LockInterceptor mockedLockInterceptor = mock(LockInterceptor.class);
        StaticAccessor.lockInterceptor = mockedLockInterceptor;
        String className = "java.lang.String";

        // When
        StaticAccessor.classForName(className);

        // Then
        verify(mockedLockInterceptor).classForName(className);
    }
}
