package fr.pingtimeout.lockprofiling;

import java.util.Arrays;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfilerClassVisitor extends ClassVisitor {

    static Logger LOG = LoggerFactory.getLogger(ProfilerClassVisitor.class);

    public ProfilerClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        LOG.trace("Visiting method {} {} {} {} {}", accessToString(access), name, desc, signature, Arrays.toString(exceptions));
        final MethodVisitor nextVisitor = super.visitMethod(access, name, desc, signature, exceptions);

        if (isSynchronized(access)) {
            LOG.trace("Found synchronized method {} {} {} {} {}", accessToString(access), name, desc, signature, Arrays.toString(exceptions));
            return new SynchronizedMethodWrapper(api, nextVisitor, access, name, desc);
        }

        return nextVisitor;
    }

    private boolean isSynchronized(int access) {
        return (access & Opcodes.ACC_SYNCHRONIZED) != 0;
    }

    private String accessToString(int access) {
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
