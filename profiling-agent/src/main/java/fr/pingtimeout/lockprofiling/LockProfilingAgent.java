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

import java.lang.instrument.Instrumentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockProfilingAgent {

    static Logger LOG = LoggerFactory.getLogger(LockProfilingAgent.class);

    private static Instrumentation instrumentation;

    /**
     * JVM hook to statically load the javaagent at startup.
     * <p/>
     * After the Java Virtual Machine (JVM) has initialized, the premain method
     * will be called. Then the real application main method will be called.
     *
     * @param args
     * @param inst
     * @throws Exception
     */
    public static void premain(String args, Instrumentation inst) throws Exception {
        LOG.debug("premain() method invoked with args: {} and inst: {}", args, inst);

        instrumentation = inst;
        instrumentation.addTransformer(new SynchronizedMethodsTransformer());
    }

    /**
     * JVM hook to dynamically load javaagent at runtime.
     * <p/>
     * The agent class may have an agentmain method for use when the agent is
     * started after VM startup.
     *
     * @param args
     * @param inst
     * @throws Exception
     */
    public static void agentmain(String args, Instrumentation inst) throws Exception {
        LOG.debug("agentmain() method invoked with args: {} and inst: {}", args, inst);

        instrumentation = inst;
        instrumentation.addTransformer(new SynchronizedMethodsTransformer());
    }

    /**
     * Programmatic hook to dynamically load javaagent at runtime.
     */
    public static void initialize() {
        LOG.debug("initialize() method invoked");

        if (instrumentation == null) {
//            MyJavaAgentLoader.loadAgent();
        }
    }
}
