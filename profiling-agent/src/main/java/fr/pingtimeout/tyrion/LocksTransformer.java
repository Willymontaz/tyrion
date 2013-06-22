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

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

class LocksTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            Logger.debug("Trying to transform %s...", className);
            return unsafeTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer.clone());
        } catch (RuntimeException ignored) {
            Logger.warn("Unable to transform class %s, returning the class buffer unchanged. Cause : %s",
                    className, ignored.getMessage());
            Logger.debug("Exception: ", ignored);
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
        interceptAllSynchronizedBlocks(classNode);
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

        if (blocksIntercepted != 0)
            Logger.debug("Intercepted %s synchronized blocks in %s", blocksIntercepted, classNode.name);
    }


    private int interceptAllSynchronizedBlocks(ClassNode classNode, MethodNode methodNode) {
        int numberOfBlocksIntercepted = 0;
        InsnList instructions = methodNode.instructions;
        if (SynchronizedMethodVisitor.isSynchronized(methodNode.access)) {
            Logger.debug("%s::%s is synchronized, nothing to do here", classNode.name, methodNode.name);
        } else {
            Logger.debug("Intercepting all synchronized blocks of %s::%s", classNode.name, methodNode.name);
            numberOfBlocksIntercepted += interceptSynchronizedBlocks(classNode, methodNode, instructions);
        }
        return numberOfBlocksIntercepted;
    }


    private int interceptSynchronizedBlocks(ClassNode classNode, MethodNode methodNode, InsnList instructions) {
        int numberOfBlocksIntercepted = interceptMonitorEnter(classNode, methodNode);
        interceptMonitorExit(classNode, methodNode);
        return numberOfBlocksIntercepted;
    }

    private int interceptMonitorEnter(ClassNode classNode, MethodNode methodNode) {
        Collection<AbstractInsnNode> monitorEnterInsn = extractMonitorEnterInsn(classNode, methodNode);

        for (AbstractInsnNode monitorEnterInsnNode : monitorEnterInsn) {
            // Duplicate lock
//            AbstractInsnNode nodeAfterDup = getNodeAfterDup(monitorEnterInsnNode);
            AbstractInsnNode nodeAfterDup = monitorEnterInsnNode;
            Logger.debug("Inserting DUP before %s", nodeAfterDup);
            methodNode.instructions.insertBefore(nodeAfterDup, new InsnNode(Opcodes.DUP));

            // Add invokestatic as first instruction of critical section
//            AbstractInsnNode nodeAfterInterception = monitorEnterInsnNode;

//            AbstractInsnNode nodeAfterInterception = getNodeAfterDup(monitorEnterInsnNode);
            AbstractInsnNode nodeAfterInterception = monitorEnterInsnNode.getNext();
            Logger.debug("Inserting call to enteredSynchronizedBlock before %s", nodeAfterInterception);
            methodNode.instructions.insertBefore(nodeAfterInterception, new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "fr/pingtimeout/tyrion/LockInterceptor",
                    "enteredSynchronizedBlock", "(Ljava/lang/Object;)V"));
        }

        return monitorEnterInsn.size();
    }

    private void interceptMonitorExit(ClassNode classNode, MethodNode methodNode) {
        Collection<AbstractInsnNode> monitorExitInsn = extractMonitorExitInsn(classNode, methodNode);

        for (AbstractInsnNode monitorExitInsnNode : monitorExitInsn) {
            // Duplicate lock
//            AbstractInsnNode nodeAfterDup = getNodeAfterDup(monitorExitInsnNode);
            AbstractInsnNode nodeAfterDup = monitorExitInsnNode;
            Logger.debug("Inserting DUP before %s", nodeAfterDup);
            methodNode.instructions.insertBefore(nodeAfterDup, new InsnNode(Opcodes.DUP));

            // Add invokestatic as last instruction of critical section
            methodNode.instructions.insertBefore(monitorExitInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "fr/pingtimeout/tyrion/LockInterceptor",
                    "leavingSynchronizedBlock", "(Ljava/lang/Object;)V"));
        }
    }

    private AbstractInsnNode getNodeAfterDup(AbstractInsnNode monitorEnterInsnNode) {
        AbstractInsnNode previousNode = monitorEnterInsnNode.getPrevious();
        Logger.debug("Checking if %s is ASTORE", previousNode);
        while (previousNode.getOpcode() == Opcodes.ASTORE) {
            previousNode = previousNode.getPrevious();
            Logger.debug("Checking if %s is ASTORE", previousNode);
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
                monitorEnterInsn.add(insnNode);
            }
        }
        return monitorEnterInsn;
    }
}