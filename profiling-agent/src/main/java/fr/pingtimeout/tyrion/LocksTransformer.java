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

package fr.pingtimeout.tyrion;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.TraceClassVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LocksTransformer implements ClassFileTransformer {

    static Logger LOG = LoggerFactory.getLogger(LocksTransformer.class);


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {
        try {
            return unsafeTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        } catch (RuntimeException ignored) {
            LOG.warn("Unable to transform class {}, returning the class buffer unchanged. Cause : {}",
                    className, ignored.getMessage());
            LOG.warn("Exception: ", ignored);
            return null;
        }
    }


    private byte[] unsafeTransform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                   ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        LOG.debug("transform() method called for class {} and classloader {}", className, loader);

        ClassReader reader = new ClassReader(classfileBuffer);
        ClassNode classNode = new ClassNode();
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        ClassVisitor traceClassVisitor = new TraceClassVisitor(writer, new PrintWriter(System.out));
//        ClassVisitor syncMethodsVisitor = new SynchronizedMethodVisitor(Opcodes.ASM4, traceClassVisitor, className);
        ClassVisitor syncMethodsVisitor = new SynchronizedMethodVisitor(Opcodes.ASM4, writer, className);

        // Reader -> ClassNode -> SynchronizedMethodVisitor -> (TraceClassVisitor ->) Writer
        reader.accept(classNode, 0);
        interceptAllSynchronizedBlocks(classNode);
        classNode.accept(syncMethodsVisitor);


        return writer.toByteArray();
    }


    private void interceptAllSynchronizedBlocks(ClassNode classNode) {
        @SuppressWarnings("unchecked")
        List<MethodNode> methods = classNode.methods;

        for (MethodNode methodNode : methods) {
            interceptAllSynchronizedBlocks(classNode, methodNode);
        }
    }


    private void interceptAllSynchronizedBlocks(ClassNode classNode, MethodNode methodNode) {
        InsnList instructions = methodNode.instructions;
        LOG.debug("interceptAllSynchronizedBlocks for {}::{} that has {} instructions",
                classNode.name, methodNode.name, instructions.size());
        if (SynchronizedMethodVisitor.isSynchronized(methodNode.access)) {
            LOG.debug("{}::{} is synchronized, nothing to do here", classNode.name, methodNode.name);
        } else {
            interceptSynchronizedBlocks(classNode, methodNode, instructions);
        }
    }


    private void interceptSynchronizedBlocks(ClassNode classNode, MethodNode methodNode, InsnList instructions) {
        interceptMonitorEnter(classNode, methodNode);
        interceptMonitorExit(classNode, methodNode);
    }

    private void interceptMonitorEnter(ClassNode classNode, MethodNode methodNode) {
        Collection<AbstractInsnNode> monitorEnterInsn = extractMonitorEnterInsn(classNode, methodNode);

        for (AbstractInsnNode monitorEnterInsnNode : monitorEnterInsn) {
            // Duplicate lock
            methodNode.instructions.insertBefore(monitorEnterInsnNode, new InsnNode(Opcodes.DUP));

            // Add invokestatic as first instruction of critical section
            AbstractInsnNode nextInsnNode = monitorEnterInsnNode.getNext();
            methodNode.instructions.insertBefore(nextInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "fr/pingtimeout/tyrion/LockInterceptor",
                    "enteredSynchronizedBlock", "(Ljava/lang/Object;)V"));
        }
    }

    private void interceptMonitorExit(ClassNode classNode, MethodNode methodNode) {
        Collection<AbstractInsnNode> monitorExitInsn = extractMonitorExitInsn(classNode, methodNode);

        for (AbstractInsnNode monitorExitInsnNode : monitorExitInsn) {
            // Duplicate lock
            methodNode.instructions.insertBefore(monitorExitInsnNode, new InsnNode(Opcodes.DUP));

            // Add invokestatic as last instruction of critical section
            methodNode.instructions.insertBefore(monitorExitInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "fr/pingtimeout/tyrion/LockInterceptor",
                    "leavingSynchronizedBlock", "(Ljava/lang/Object;)V"));
        }
    }

    private Collection<AbstractInsnNode> extractMonitorEnterInsn(ClassNode classNode, MethodNode methodNode) {
        return extractInstructions(classNode, methodNode, Opcodes.MONITORENTER, "MonitorEnter");
    }

    private Collection<AbstractInsnNode> extractMonitorExitInsn(ClassNode classNode, MethodNode methodNode) {
        return extractInstructions(classNode, methodNode, Opcodes.MONITOREXIT, "MonitorExit");
    }

    @SuppressWarnings("unchecked")
    private Collection<AbstractInsnNode> extractInstructions(ClassNode classNode, MethodNode methodNode,
                                                             int instructionToExtract, String instructionAsString) {
        Collection<AbstractInsnNode> monitorEnterInsn = new ArrayList<AbstractInsnNode>();
        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode insnNode = iterator.next();
            if (insnNode.getOpcode() == instructionToExtract) {
                LOG.debug("Detected {} in {}::{}", instructionAsString, classNode.name, methodNode.name);
                monitorEnterInsn.add(insnNode);
            }
        }
        return monitorEnterInsn;
    }
}