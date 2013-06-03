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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SynchronizedMethodWrapper extends AdviceAdapter {

    static Logger LOG = LoggerFactory.getLogger(SynchronizedMethodWrapper.class);

    private final String methodName;

    protected SynchronizedMethodWrapper(int api, MethodVisitor mv, int access, String name, String desc) {
        super(Opcodes.ASM4, mv, access, name, desc);
        LOG.debug("Instantiating SynchronizedMethodWrapper for method {} {}", name, desc);
        this.methodName = name;
    }

    @Override
    protected void onMethodEnter() {
        LOG.debug("Entering synchronized method {}", methodName);

        mv.visitMethodInsn(INVOKESTATIC,
                "fr/pingtimeout/tyrion/LockInterceptor",
                "enteredSynchronizedMethod", "()V");

        super.onMethodEnter();
    }

    @Override
    protected void onMethodExit(int opcode) {
        LOG.debug("Leaving synchronized method {}", methodName);

        mv.visitMethodInsn(INVOKESTATIC,
                "fr/pingtimeout/tyrion/LockInterceptor",
                "leavingSynchronizedMethod", "()V");

        super.onMethodExit(opcode);
    }
}
