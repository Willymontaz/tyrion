package fr.pingtimeout.lockprofiling;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

public class SynchronizedMethodsTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        try {
            return innerTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        } catch (RuntimeException e) {
            System.err.println("SynchronizedMethodsTransformer: unable to transform class");
            e.printStackTrace();
            return null;
        }
    }

    private byte[] innerTransform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                  ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        System.out.printf("SynchronizedMethodsTransformer: transform method called for class %s%n", className);
//        System.out.printf("SynchronizedMethodsTransformer: classBeingRedefined = %s%n", classBeingRedefined);
//        System.out.printf("SynchronizedMethodsTransformer: classfileBuffer = %s%n", classfileBuffer);

        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        TraceClassVisitor cw = new TraceClassVisitor(new PrintWriter(System.out));
        ClassVisitor profiler = new ProfilerClassVisitor(Opcodes.ASM4, writer);
        reader.accept(profiler, 0);
//        reader.accept(cw, 0);
        return writer.toByteArray();

//        if (classBeingRedefined != null) {
//            for (Method method : classBeingRedefined.getMethods()) {
//                System.out.printf("SynchronizedMethodsTransformer: inspecting %s%n", method);
//                if (Modifier.isSynchronized(method.getModifiers())) {
//                    System.out.printf("SynchronizedMethodsTransformer: method %s is synchronized%n", method);
//                }
//            }
//        }
//
//        return null;
    }

//    public byte[] transform(ClassLoader loader, String fullyQualifiedClassName, Class<?> classBeingRedefined,
//                            ProtectionDomain protectionDomain, byte[] classofileBuffer) throws IllegalClassFormatException {
//        String className = fullyQualifiedClassName.replaceAll(".*/", "");
//        String package=fullyQualifiedClassName.replaceAll("/[a-zA-Z$0-9_]*$", "");
//        System.out.printf("Class: %s in: %sn", className,package);
//        return null;
//    }
}