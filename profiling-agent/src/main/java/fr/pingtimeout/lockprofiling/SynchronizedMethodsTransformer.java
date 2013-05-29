package fr.pingtimeout.lockprofiling;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronizedMethodsTransformer implements ClassFileTransformer {
    static Logger LOG = LoggerFactory.getLogger(SynchronizedMethodsTransformer.class);

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        try {
            return innerTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        } catch (RuntimeException e) {
            LOG.warn("Unable to transform class, returning the class buffer unchanged", e);
            return null;
        }
    }

    private byte[] innerTransform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                  ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        LOG.debug("transform() method called for class {} and classloader {}", className, loader);

        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        ClassVisitor visitor = new ProfilerClassVisitor(Opcodes.ASM4, writer);
        reader.accept(visitor, 0);
        return writer.toByteArray();

    }
}