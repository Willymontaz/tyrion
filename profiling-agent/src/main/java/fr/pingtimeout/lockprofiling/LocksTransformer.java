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
import java.util.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocksTransformer implements ClassFileTransformer {
    static Logger LOG = LoggerFactory.getLogger(LocksTransformer.class);

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            return unsafeTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        } catch (RuntimeException ignored) {
            LOG.warn("Unable to transform class {}, returning the class buffer unchanged", className);
            return null;
        }
    }

    private byte[] unsafeTransform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                   ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        LOG.debug("transform() method called for class {} and classloader {}", className, loader);
        LOG.trace("classfileBuffer = {}", Arrays.toString(classfileBuffer));
        LOG.trace("MonitorEnter={}, MonitorExit={}, Goto={}", ((byte) 194), ((byte) 195), ((byte) 167));

        ClassReader reader = new ClassReader(classfileBuffer);
        ClassNode classNode = new ClassNode();
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        ClassVisitor syncMethodsVisitor = new ProfilerClassVisitor(Opcodes.ASM4, writer);

        // Reader -> ClassNode -> ProfilerClassVisitor -> Writer
        reader.accept(classNode, 0);
        interceptAllSynchronizedBlocks(classNode);
        classNode.accept(syncMethodsVisitor);


        return writer.toByteArray();
    }

    private void interceptAllSynchronizedBlocks(ClassNode classNode) {
        @SuppressWarnings("unchecked")
        List<MethodNode> methods = classNode.methods;

        for (MethodNode methodNode : methods) {
            InsnList instructions = methodNode.instructions;
            LOG.debug("interceptAllSynchronizedBlocks for {}::{} that has {} instructions", classNode.name, methodNode.name, instructions.size());
            if (ProfilerClassVisitor.isSynchronized(methodNode.access)) {
                LOG.debug("{}::{} is synchronized", classNode.name, methodNode.name);
            } else {
                Collection<AbstractInsnNode> monitorEnterInsn = new ArrayList<AbstractInsnNode>();
                Collection<AbstractInsnNode> monitorExitInsn = new ArrayList<AbstractInsnNode>();

                @SuppressWarnings("unchecked")
                ListIterator<AbstractInsnNode> iterator = instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode insnNode = iterator.next();
                    if (insnNode.getOpcode() == Opcodes.MONITORENTER) {
                        LOG.debug("Detected MonitorEnter in {}::{}", classNode.name, methodNode.name);
                        monitorEnterInsn.add(insnNode);
                    } else if (insnNode.getOpcode() == Opcodes.MONITOREXIT) {
                        LOG.debug("Detected MonitorExit in {}::{}", classNode.name, methodNode.name);
                        monitorExitInsn.add(insnNode);
                    }
                }

                for (AbstractInsnNode monitorEnterInsnNode : monitorEnterInsn) {
                    // Duplicate lock
                    instructions.insertBefore(monitorEnterInsnNode, new InsnNode(Opcodes.DUP));

                    // Add invokestatic as first instruction of critical section
                    AbstractInsnNode nextInsnNode = monitorEnterInsnNode.getNext();
                    instructions.insertBefore(nextInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "fr/pingtimeout/lockprofiling/LockInterceptor", "enteredSynchronizedBlock", "(Ljava/lang/Object;)V"));
                }

                for (AbstractInsnNode monitorExitInsnNode : monitorExitInsn) {
                    // Duplicate lock
                    instructions.insertBefore(monitorExitInsnNode, new InsnNode(Opcodes.DUP));

                    // Add invokestatic as last instruction of critical section
                    instructions.insertBefore(monitorExitInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "fr/pingtimeout/lockprofiling/LockInterceptor", "leavingSynchronizedBlock", "(Ljava/lang/Object;)V"));
                }
            }
        }
    }
}