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

    private final Label tryStart;
    private final Label tryEnd;
    private final Label finallyBlock;


    protected SynchronizedMethodProbesInjector(int api, MethodVisitor mv, int access, String name, String desc, String className) {
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
            mv.visitLdcInsn(className);
            mv.visitMethodInsn(INVOKESTATIC,
                    StaticAccessor.CLASS_FQN,
                    StaticAccessor.RETRIEVE_CLASS_BY_NAME.getMethodName(),
                    StaticAccessor.RETRIEVE_CLASS_BY_NAME.getSignature());
            mv.visitInsn(DUP);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESTATIC,
                    StaticAccessor.CLASS_FQN,
                    StaticAccessor.BEFORE_MONITORENTER_ON_CLASS.getMethodName(),
                    StaticAccessor.BEFORE_MONITORENTER_ON_CLASS.getSignature());
            mv.visitInsn(MONITORENTER);
            mv.visitMethodInsn(INVOKESTATIC,
                    StaticAccessor.CLASS_FQN,
                    StaticAccessor.AFTER_MONITORENTER_ON_CLASS.getMethodName(),
                    StaticAccessor.AFTER_MONITORENTER_ON_CLASS.getSignature());
        } else {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(DUP);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESTATIC,
                    StaticAccessor.CLASS_FQN,
                    StaticAccessor.BEFORE_MONITORENTER_ON_OBJECT.getMethodName(),
                    StaticAccessor.BEFORE_MONITORENTER_ON_OBJECT.getSignature());
            mv.visitInsn(MONITORENTER);
            mv.visitMethodInsn(INVOKESTATIC,
                    StaticAccessor.CLASS_FQN,
                    StaticAccessor.AFTER_MONITORENTER_ON_OBJECT.getMethodName(),
                    StaticAccessor.AFTER_MONITORENTER_ON_OBJECT.getSignature());
        }

        super.onMethodEnter();
    }


    @Override
    protected void onMethodExit(int opcode) {
        SimpleLogger.debug("Leaving synchronized method %s", methodName);

        mv.visitLabel(tryEnd);
        mv.visitLabel(finallyBlock);

        if (isStatic(methodAccess)) {
            mv.visitLdcInsn(className);
            mv.visitMethodInsn(INVOKESTATIC,
                    StaticAccessor.CLASS_FQN,
                    StaticAccessor.RETRIEVE_CLASS_BY_NAME.getMethodName(),
                    StaticAccessor.RETRIEVE_CLASS_BY_NAME.getSignature());
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESTATIC,
                    StaticAccessor.CLASS_FQN,
                    StaticAccessor.BEFORE_MONITOREXIT_ON_CLASS.getMethodName(),
                    StaticAccessor.BEFORE_MONITOREXIT_ON_CLASS.getSignature());
            mv.visitInsn(MONITOREXIT);
        } else {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESTATIC,
                    StaticAccessor.CLASS_FQN,
                    StaticAccessor.BEFORE_MONITOREXIT_ON_OBJECT.getMethodName(),
                    StaticAccessor.BEFORE_MONITOREXIT_ON_OBJECT.getSignature());
            mv.visitInsn(MONITOREXIT);
        }

        super.onMethodExit(opcode);
    }


}