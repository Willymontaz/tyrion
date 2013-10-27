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

import fr.pingtimeout.tyrion.agent.StaticAccessor;
import fr.pingtimeout.tyrion.util.SimpleLogger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import static fr.pingtimeout.tyrion.transformation.SynchronizedMethodVisitor.isStatic;

class SynchronizedMethodWrapper extends AdviceAdapter {


    private final String methodName;
    private final String className;

    private final Label tryStart;
    private final Label tryEnd;
    private final Label finallyBlock;


    protected SynchronizedMethodWrapper(int api, MethodVisitor mv, int access, String name, String desc, String className) {
        super(Opcodes.ASM4, mv, access, name, desc);

        this.methodName = name;
        this.className = className.replaceAll("/", ".");

        this.tryStart = new Label();
        this.tryEnd = new Label();
        this.finallyBlock = new Label();
    }


    @Override
    protected void onMethodEnter() {
        SimpleLogger.debug("Entering synchronized method %s", methodName);

        mv.visitTryCatchBlock(tryStart, tryEnd, finallyBlock, null);
        mv.visitLabel(tryStart);

        if (isStatic(methodAccess)) {
            putClassOnTheStack();
            recordEnterOnClass();
        } else {
            putThisOnTheStack();
            recordEnterOnObject();
        }

        super.onMethodEnter();
    }


    protected void onMethodExit(int opcode) {
        mv.visitLabel(tryEnd);
        mv.visitLabel(finallyBlock);

        if (isStatic(methodAccess)) {
            putClassOnTheStack();
            recordExitOnClass();
        } else {
            putThisOnTheStack();
            recordExitOnObject();
        }

//        if (opcode != ATHROW) {
//            if (isStatic(methodAccess)) {
//                putClassOnTheStack();
//                recordExitOnClass();
//            } else {
//                putThisOnTheStack();
//                recordExitOnObject();
//            }
//        }
    }

    //    @Override
    protected void onMethodExit_old(int opcode) {
        SimpleLogger.debug("Leaving synchronized method %s", methodName);

        if (isStatic(methodAccess)) {
            putClassOnTheStack();
            recordExitOnClass();
        } else {
            putThisOnTheStack();
            recordExitOnObject();
        }

        super.onMethodExit(opcode);
    }


    private void putClassOnTheStack() {
        mv.visitLdcInsn(className);
        mv.visitMethodInsn(INVOKESTATIC,
                StaticAccessor.CLASS_FQN,
                StaticAccessor.GET_CLASS_BY_NAME.getMethodName(), StaticAccessor.GET_CLASS_BY_NAME.getSignature());
    }


    private void recordEnterOnObject() {
        mv.visitMethodInsn(INVOKESTATIC,
                StaticAccessor.CLASS_FQN,
                StaticAccessor.AFTER_MONITORENTER_ON_OBJECT.getMethodName(),
                StaticAccessor.AFTER_MONITORENTER_ON_OBJECT.getSignature());
    }

    private void recordEnterOnClass() {
        mv.visitMethodInsn(INVOKESTATIC,
                StaticAccessor.CLASS_FQN,
                StaticAccessor.AFTER_MONITORENTER_ON_CLASS.getMethodName(),
                StaticAccessor.AFTER_MONITORENTER_ON_CLASS.getSignature());
    }


    private void putThisOnTheStack() {
        mv.visitVarInsn(ALOAD, 0);
    }


    private void recordExitOnObject() {
        mv.visitMethodInsn(INVOKESTATIC,
                StaticAccessor.CLASS_FQN,
                StaticAccessor.BEFORE_MONITOREXIT_ON_OBJECT.getMethodName(),
                StaticAccessor.BEFORE_MONITOREXIT_ON_OBJECT.getSignature());
    }

    private void recordExitOnClass() {
        mv.visitMethodInsn(INVOKESTATIC,
                StaticAccessor.CLASS_FQN,
                StaticAccessor.BEFORE_MONITOREXIT_ON_CLASS.getMethodName(),
                StaticAccessor.BEFORE_MONITOREXIT_ON_CLASS.getSignature());
    }
}