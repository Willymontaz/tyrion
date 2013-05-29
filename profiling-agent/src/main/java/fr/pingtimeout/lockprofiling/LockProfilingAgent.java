package fr.pingtimeout.lockprofiling;

import java.lang.instrument.Instrumentation;

public class LockProfilingAgent {
    private static Instrumentation instrumentation;

    /**
     * JVM hook to statically load the javaagent at startup.
     *
     * After the Java Virtual Machine (JVM) has initialized, the premain method
     * will be called. Then the real application main method will be called.
     *
     * @param args
     * @param inst
     * @throws Exception
     */
    public static void premain(String args, Instrumentation inst) throws Exception {
        System.out.printf("LockProfilingAgent : premain method invoked with args: %s and inst: %s%n", args, inst);

        instrumentation = inst;
        instrumentation.addTransformer(new SynchronizedMethodsTransformer());
    }

    /**
     * JVM hook to dynamically load javaagent at runtime.
     *
     * The agent class may have an agentmain method for use when the agent is
     * started after VM startup.
     *
     * @param args
     * @param inst
     * @throws Exception
     */
    public static void agentmain(String args, Instrumentation inst) throws Exception {
        System.out.printf("LockProfilingAgent : agentmain method invoked with args: %s and inst: %s%n", args, inst);

        instrumentation = inst;
        instrumentation.addTransformer(new SynchronizedMethodsTransformer());
    }

    /**
     * Programmatic hook to dynamically load javaagent at runtime.
     */
    public static void initialize() {
        System.out.printf("LockProfilingAgent : initialize method invoked");

        if (instrumentation == null) {
//            MyJavaAgentLoader.loadAgent();
        }
    }
}
