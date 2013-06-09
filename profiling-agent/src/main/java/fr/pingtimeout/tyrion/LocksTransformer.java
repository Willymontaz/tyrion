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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
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
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            LOG.debug("Trying to transform {}...", className);
            return unsafeTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer.clone());
        } catch (RuntimeException ignored) {
            LOG.warn("Unable to transform class {}, returning the class buffer unchanged. Cause : {}",
                    className, ignored.getMessage());
            LOG.debug("Exception: ", ignored);
            return classfileBuffer;
        }
    }


    private byte[] unsafeTransform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                   ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassNode classNode = new ClassNode();
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        ClassVisitor traceClassVisitor = new TraceClassVisitor(writer, new PrintWriter(System.out));
//        ClassVisitor syncMethodsVisitor = new SynchronizedMethodVisitor(Opcodes.ASM4, traceClassVisitor, className);
        ClassVisitor syncMethodsVisitor = new SynchronizedMethodVisitor(Opcodes.ASM4, writer, className);

        // Reader -> ClassNode -> SynchronizedMethodVisitor -> (TraceClassVisitor ->) Writer
//        reader.accept(classNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        reader.accept(classNode, 0);
//        interceptAllSynchronizedBlocks(classNode);
        classNode.accept(syncMethodsVisitor);


        return writer.toByteArray();
    }


    private void interceptAllSynchronizedBlocks(ClassNode classNode) {
        @SuppressWarnings("unchecked")
        List<MethodNode> methods = classNode.methods;

        int blocksIntercepted = 0;
        for (MethodNode methodNode : methods) {
            blocksIntercepted += interceptAllSynchronizedBlocks(classNode, methodNode);
        }

        LOG.info("Intercepted {} synchronized blocks in {}", blocksIntercepted, classNode.name);
    }


    private int interceptAllSynchronizedBlocks(ClassNode classNode, MethodNode methodNode) {
        int blocksIntercepted = 0;
        InsnList instructions = methodNode.instructions;
        if (SynchronizedMethodVisitor.isSynchronized(methodNode.access)) {
            LOG.debug("{}::{} is synchronized, nothing to do here", classNode.name, methodNode.name);
        } else {
            LOG.debug("Intercepting all synchronized blocks of {}::{}", classNode.name, methodNode.name);
            blocksIntercepted += interceptSynchronizedBlocks(classNode, methodNode, instructions);
        }
        return blocksIntercepted;
    }


    private int interceptSynchronizedBlocks(ClassNode classNode, MethodNode methodNode, InsnList instructions) {
        int blocksIntercepted = interceptMonitorEnter(classNode, methodNode);
        interceptMonitorExit(classNode, methodNode);
        return blocksIntercepted;
    }

    private int interceptMonitorEnter(ClassNode classNode, MethodNode methodNode) {
        Collection<AbstractInsnNode> monitorEnterInsn = extractMonitorEnterInsn(classNode, methodNode);

        for (AbstractInsnNode monitorEnterInsnNode : monitorEnterInsn) {
            // Duplicate lock
            AbstractInsnNode nodeAfterDup = getNodeAfterDup(monitorEnterInsnNode);
            LOG.debug("Inserting DUP before {}", nodeAfterDup);
            methodNode.instructions.insertBefore(nodeAfterDup, new InsnNode(Opcodes.DUP));

            // Add invokestatic as first instruction of critical section
//            AbstractInsnNode nextInsnNode = monitorEnterInsnNode;

//            AbstractInsnNode nextInsnNode = getNodeAfterDup(monitorEnterInsnNode);
            AbstractInsnNode nextInsnNode = monitorEnterInsnNode.getNext();
            LOG.debug("Inserting call to enteredSynchronizedBlock before {}", nextInsnNode);
            methodNode.instructions.insertBefore(nextInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "fr/pingtimeout/tyrion/LockInterceptor",
                    "enteredSynchronizedBlock", "(Ljava/lang/Object;)V"));
        }

        return monitorEnterInsn.size();
    }

    private void interceptMonitorExit(ClassNode classNode, MethodNode methodNode) {
        Collection<AbstractInsnNode> monitorExitInsn = extractMonitorExitInsn(classNode, methodNode);

        for (AbstractInsnNode monitorExitInsnNode : monitorExitInsn) {
            // Duplicate lock
            AbstractInsnNode nodeAfterDup = getNodeAfterDup(monitorExitInsnNode);
            LOG.debug("Inserting DUP before {}", nodeAfterDup);
            methodNode.instructions.insertBefore(nodeAfterDup, new InsnNode(Opcodes.DUP));

            // Add invokestatic as last instruction of critical section
            methodNode.instructions.insertBefore(monitorExitInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "fr/pingtimeout/tyrion/LockInterceptor",
                    "leavingSynchronizedBlock", "(Ljava/lang/Object;)V"));
        }
    }

    private AbstractInsnNode getNodeAfterDup(AbstractInsnNode monitorEnterInsnNode) {
        AbstractInsnNode previousNode = monitorEnterInsnNode.getPrevious();
        LOG.debug("Checking if {} is ASTORE", previousNode);
        while (previousNode.getOpcode() == Opcodes.ASTORE) {
            previousNode = previousNode.getPrevious();
            LOG.debug("Checking if {} is ASTORE", previousNode);
        }
        return previousNode.getNext();
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
                LOG.trace("Detected {} in {}::{}", instructionAsString, classNode.name, methodNode.name);
                monitorEnterInsn.add(insnNode);
            }
        }
        return monitorEnterInsn;
    }
}