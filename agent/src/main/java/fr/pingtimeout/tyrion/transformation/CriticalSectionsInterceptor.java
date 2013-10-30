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

import fr.pingtimeout.tyrion.util.SimpleLogger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class CriticalSectionsInterceptor implements ClassFileTransformer {


    private final PrintWriter traceBytecodeWriter;


    public CriticalSectionsInterceptor() {
        traceBytecodeWriter = null;
    }

    public CriticalSectionsInterceptor(PrintWriter traceBytecodeWriter) {
        this.traceBytecodeWriter = traceBytecodeWriter;
    }


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            SimpleLogger.debug("Trying to transform %s...", className);
            return transform(className, classfileBuffer);
        } catch (RuntimeException ignored) {
            SimpleLogger.warn("Unable to transform class %s, returning the class buffer unchanged. Cause : %s",
                    className, ignored.getMessage());
            SimpleLogger.debug(ignored);
            return classfileBuffer;
        }
    }


    byte[] transform(String className, byte[] classfileBuffer) {
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassNode classNode = new ClassNode();
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        SynchronizedBlockTransformer synchronizedBlockTransformer = new SynchronizedBlockTransformer(classNode);

        final ClassVisitor syncMethodsVisitor;
        if (traceBytecodeWriter != null) {
            ClassVisitor traceClassVisitor = new TraceClassVisitor(writer, traceBytecodeWriter);
            syncMethodsVisitor = new SynchronizedMethodTransformer(Opcodes.ASM4, traceClassVisitor, className);
        } else {
            syncMethodsVisitor = new SynchronizedMethodTransformer(Opcodes.ASM4, writer, className);
        }

        // Reader -> ClassNode -> SynchronizedMethodTransformer -> (TraceClassVisitor ->) Writer
        reader.accept(classNode, 0);
        synchronizedBlockTransformer.interceptAllSynchronizedBlocks();
        classNode.accept(syncMethodsVisitor);

        return writer.toByteArray();
    }
}