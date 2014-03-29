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

package fr.pingtimeout.tyrion.transformation;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.apache.commons.io.IOUtils.readLines;
import static org.apache.commons.lang3.StringUtils.join;
import static org.assertj.core.api.Assertions.assertThat;

public class CriticalSectionInterceptorTest {


    @Test
    public void should_transform_all_synchronized_sections() throws Exception {
        // Given
        Class<?> classUnderTest = TestClassWithSynchronizedSections.class;
        String expectedBytecodes = join(
                readLines(CriticalSectionInterceptorTest.class.getResourceAsStream("expectedBytecodes.txt"))
                , "\n");


        // When
        String testClassBytecodes = extractTransformedBytecodesOf(classUnderTest);


        // Then
        assertThat(testClassBytecodes).isEqualTo(expectedBytecodes);
    }


    private String extractTransformedBytecodesOf(Class<?> classUnderTest) throws IOException {
        String classFilePath = classUnderTest.getName().replace('.', '/') + ".class";

        byte[] originalBytecodes;
        try (InputStream inputStream = classUnderTest.getClassLoader().getResourceAsStream(classFilePath)) {
            originalBytecodes = IOUtils.toByteArray(inputStream);
        }

        StringWriter classBytecodes = new StringWriter();
        CriticalSectionsInterceptor criticalSectionsInterceptor = new CriticalSectionsInterceptor(new PrintWriter(classBytecodes));

        criticalSectionsInterceptor.transform(classUnderTest.getName(), originalBytecodes);

        return classBytecodes.toString();
    }
}
