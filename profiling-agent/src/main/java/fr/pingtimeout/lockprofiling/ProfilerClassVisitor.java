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

import java.util.Arrays;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ProfilerClassVisitor extends ClassVisitor {

    static Logger LOG = LoggerFactory.getLogger(ProfilerClassVisitor.class);

    public ProfilerClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        LOG.trace("Visiting method {} {} {} {} {}",
                accessToString(access), name, desc, signature, Arrays.toString(exceptions));

        final MethodVisitor nextVisitor = super.visitMethod(access, name, desc, signature, exceptions);

        if (isSynchronized(access)) {
            LOG.trace("Found synchronized method {} {} {} {} {}",
                    accessToString(access), name, desc, signature, Arrays.toString(exceptions));
            return new SynchronizedMethodWrapper(api, nextVisitor, access, name, desc);
        }

        return nextVisitor;
    }


    public static boolean isSynchronized(int access) {
        return (access & Opcodes.ACC_SYNCHRONIZED) != 0;
    }


    public static String accessToString(int access) {
        StringBuilder result = new StringBuilder();
        if ((access & Opcodes.ACC_ABSTRACT) != 0) result.append(" abstract");
        if ((access & Opcodes.ACC_BRIDGE) != 0) result.append(" bridge");
        if ((access & Opcodes.ACC_DEPRECATED) != 0) result.append(" deprecated");
        if ((access & Opcodes.ACC_FINAL) != 0) result.append(" final");
        if ((access & Opcodes.ACC_NATIVE) != 0) result.append(" native");
        if ((access & Opcodes.ACC_PRIVATE) != 0) result.append(" private");
        if ((access & Opcodes.ACC_PROTECTED) != 0) result.append(" protected");
        if ((access & Opcodes.ACC_PUBLIC) != 0) result.append(" public");
        if ((access & Opcodes.ACC_STATIC) != 0) result.append(" static");
        if ((access & Opcodes.ACC_STRICT) != 0) result.append(" strict");
        if ((access & Opcodes.ACC_SYNCHRONIZED) != 0) result.append(" synchronized");
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) result.append(" synthetic");
        if ((access & Opcodes.ACC_VARARGS) != 0) result.append(" varargs");
        return result.toString();
    }
}
