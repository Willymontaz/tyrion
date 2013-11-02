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

import static fr.pingtimeout.tyrion.transformation.SynchronizedMethodTransformer.isStatic;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import fr.pingtimeout.tyrion.agent.StaticAccessor;
import fr.pingtimeout.tyrion.util.SimpleLogger;

class SynchronizedMethodProbesInjector extends AdviceAdapter {


    private final String methodName;
    private final String className;

    private final Label startFinally;
    private final Label endFinally;


    protected SynchronizedMethodProbesInjector(int api, MethodVisitor mv, int access, String name, String desc, String className) {
        super(Opcodes.ASM4, mv, access, name, desc);

        this.methodName = name;
        this.className = className.replaceAll("/", ".");

        this.startFinally = new Label();
        this.endFinally = new Label();
    }


    @Override
    protected void onMethodEnter() {
        SimpleLogger.debug("Entering synchronized method %s", methodName);

        pushTargetOnStack();
        mv.visitInsn(DUP);
        mv.visitInsn(DUP);
        popTargetAndRecordTryEnter();
        popTargetAndEnterSynchronizedBlock();
        popTargetAndRecordEnter();
    }

    @Override
    protected void onMethodExit(int opcode) {
        if (opcode != ATHROW) {
            onFinally(opcode);
        }
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        mv.visitTryCatchBlock(this.startFinally, this.endFinally, this.endFinally, null);
        mv.visitLabel(this.endFinally);

        onFinally(ATHROW);

        super.visitMaxs(maxStack, maxLocals);
    }


    private void onFinally(int opcode) {
        SimpleLogger.debug("Leaving synchronized method %s", methodName);

        pushTargetOnStack();
        mv.visitInsn(DUP);
        popTargetAndRecordExit();
        popTargetAndExitSynchronizedBlock();

        mv.visitInsn(opcode);
    }


    private void pushTargetOnStack() {
        if (isStatic(methodAccess)) {
            pushCurrentClassOnStack();
        } else {
            pushThisOnStack();
        }
    }


    private void pushCurrentClassOnStack() {
        mv.visitLdcInsn(className);
        mv.visitMethodInsn(INVOKESTATIC,
                StaticAccessor.CLASS_FQN,
                StaticAccessor.RETRIEVE_CLASS_BY_NAME.getMethodName(),
                StaticAccessor.RETRIEVE_CLASS_BY_NAME.getSignature());
    }


    private void pushThisOnStack() {
        mv.visitVarInsn(ALOAD, 0);
    }


    private void popTargetAndRecordTryEnter() {
        mv.visitMethodInsn(INVOKESTATIC,
                StaticAccessor.CLASS_FQN,
                StaticAccessor.BEFORE_MONITORENTER_ON_OBJECT.getMethodName(),
                StaticAccessor.BEFORE_MONITORENTER_ON_OBJECT.getSignature());
    }


    private void popTargetAndEnterSynchronizedBlock() {
        mv.visitInsn(MONITORENTER);
        mv.visitLabel(startFinally);
    }


    private void popTargetAndRecordEnter() {
        mv.visitMethodInsn(INVOKESTATIC,
                StaticAccessor.CLASS_FQN,
                StaticAccessor.AFTER_MONITORENTER_ON_OBJECT.getMethodName(),
                StaticAccessor.AFTER_MONITORENTER_ON_OBJECT.getSignature());
    }


    private void popTargetAndExitSynchronizedBlock() {
        mv.visitInsn(MONITOREXIT);
    }


    private void popTargetAndRecordExit() {
        mv.visitMethodInsn(INVOKESTATIC,
                StaticAccessor.CLASS_FQN,
                StaticAccessor.BEFORE_MONITOREXIT_ON_OBJECT.getMethodName(),
                StaticAccessor.BEFORE_MONITOREXIT_ON_OBJECT.getSignature());
    }
}
