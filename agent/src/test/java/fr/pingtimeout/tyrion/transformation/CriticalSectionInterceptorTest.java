/*
 * Copyright (c) 2013, Pierre Laporte
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
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.apache.commons.io.IOUtils.readLines;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.apache.commons.lang3.StringUtils.join;
import static org.assertj.core.api.Assertions.assertThat;

public class CriticalSectionInterceptorTest {


    @Test
    public void should_transform_all_synchronized_sections() throws Exception {
        // Given
        Class<?> classUnderTest = TestClassWithSynchronizedSections.class;
        String expectedOriginalBytecodes = join(
                readLines(CriticalSectionInterceptorTest.class.getResourceAsStream("originalBytecodes.txt"))
                , "\n");
        String expectedTransformedBytecodes = join(
                readLines(CriticalSectionInterceptorTest.class.getResourceAsStream("expectedBytecodes.txt"))
                , "\n");


        // When
        String originalBytecodes = extractOriginalBytecodesOf(classUnderTest);
        String transformedBytecodes = extractTransformedBytecodesOf(classUnderTest);


        // Then
        assertThat(originalBytecodes).isEqualTo(expectedOriginalBytecodes);
        assertThat(transformedBytecodes).isEqualTo(expectedTransformedBytecodes);

        /**
         * The test of the origial bytecodes is there for :
         * - Making sure that the original class file is not modified
         * - Otherwise, locate if a problem is in the original class or the transformer
         * - Have a precise idea of the original bytecodes
         * - Be able to compare the original bytecodes and the transformed bytecodes
         */
    }

    private String extractOriginalBytecodesOf(Class<?> classUnderTest) throws IOException {
        byte[] bytecodes = retrieveClassBytecodes(classUnderTest);
        return convertBytecodesToString(bytecodes);
    }

    private String convertBytecodesToString(byte[] originalBytecodes) {
        StringWriter classBytecodes = new StringWriter();
        ClassReader reader = new ClassReader(originalBytecodes);
        ClassVisitor syncMethodsVisitor = new TraceClassVisitor(new ClassWriter(0), new PrintWriter(classBytecodes));
        reader.accept(syncMethodsVisitor, 0);
        return classBytecodes.toString();
    }

    private byte[] retrieveClassBytecodes(Class<?> classUnderTest) throws IOException {
        String classFilePath = classUnderTest.getName().replace('.', '/') + ".class";

        byte[] originalBytecodes;
        try (InputStream inputStream = classUnderTest.getClassLoader().getResourceAsStream(classFilePath)) {
            originalBytecodes = IOUtils.toByteArray(inputStream);
        }
        return originalBytecodes;
    }


    private String extractTransformedBytecodesOf(Class<?> classUnderTest) throws IOException {
        byte[] transformedBytecodes = new CriticalSectionsInterceptor().transform(
                classUnderTest.getName(),
                retrieveClassBytecodes(classUnderTest));

        return convertBytecodesToString(transformedBytecodes);
    }
}
