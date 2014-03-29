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
