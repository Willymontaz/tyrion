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

package fr.pingtimeout.lockprofiling;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronizedMethodsTransformer implements ClassFileTransformer {
    static Logger LOG = LoggerFactory.getLogger(SynchronizedMethodsTransformer.class);

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        try {
            return innerTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        } catch (RuntimeException e) {
            LOG.warn("Unable to transform class, returning the class buffer unchanged", e);
            return null;
        }
    }

    private byte[] innerTransform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                  ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        LOG.debug("transform() method called for class {} and classloader {}", className, loader);
        LOG.trace("classfileBuffer = {}", Arrays.toString(classfileBuffer));
        LOG.trace("MonitorEnter={}, MonitorExit={}, Goto={}", ((byte) 194), ((byte) 195), ((byte) 167));

        ClassReader reader = new ClassReader(LockInterceptor.rewriteMonitorEntersAndExits(classfileBuffer));
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        ClassVisitor visitor = new ProfilerClassVisitor(Opcodes.ASM4, writer);
        reader.accept(visitor, 0);
        return writer.toByteArray();
    }
}