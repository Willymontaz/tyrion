package fr.pingtimeout.lockprofiling;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronizedMethodWrapper extends AdviceAdapter {
    static Logger LOG = LoggerFactory.getLogger(SynchronizedMethodWrapper.class);

    private final String methodName;

    /**
     * Creates a new {@link org.objectweb.asm.commons.AdviceAdapter}.
     *
     * @param api    the ASM API version implemented by this visitor. Must be one
     *               of {@link org.objectweb.asm.Opcodes#ASM4}.
     * @param mv     the method visitor to which this adapter delegates calls.
     * @param access the method's access flags (see {@link org.objectweb.asm.Opcodes}).
     * @param name   the method's name.
     * @param desc   the method's descriptor (see {@link org.objectweb.asm.Type Type}).
     */
    protected SynchronizedMethodWrapper(int api, MethodVisitor mv, int access, String name, String desc) {
        super(Opcodes.ASM4, mv, access, name, desc);
        LOG.debug("Instantiating SynchronizedMethodWrapper for method {} {}", name, desc);
        this.methodName = name;
    }

    @Override
    protected void onMethodEnter() {
        LOG.debug("Entering synchronized method {}", methodName);

        mv.visitMethodInsn(INVOKESTATIC, "fr/pingtimeout/lockprofiling/LockInterceptor", "enteredSynchronizedMethod", "()V");

        super.onMethodEnter();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onMethodExit(int opcode) {
        LOG.debug("Leaving synchronized method {}", methodName);

        mv.visitMethodInsn(INVOKESTATIC, "fr/pingtimeout/lockprofiling/LockInterceptor", "leftSynchronizedMethod", "()V");

        super.onMethodExit(opcode);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
