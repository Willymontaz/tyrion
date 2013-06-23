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

import java.lang.instrument.Instrumentation;

public class LockProfilingAgent {

    /**
     * JVM hook to statically load the javaagent at startup.
     * <p/>
     * After the Java Virtual Machine (JVM) has initialized, the premain method
     * will be called. Then the real application main method will be called.
     *
     * @param args The agent's arguments, not used
     * @param inst The instrumentation class that will be used
     * @throws Exception
     */
    public static void premain(String args, Instrumentation inst) throws Exception {
        new LockInterceptor();
        String arguments = args == null ? "" : args;
        Logger.info("Tyrion agent starting with arguments '%s'", arguments);

        final String outputFile;
        if (arguments.startsWith("outputFile=")) {
            outputFile = arguments.substring("outputFile=".length());
        } else {
            outputFile = "";
        }

        if (LocksStatisticsCollector.createInstanceAndRegisterAsMXBean(outputFile)) {
            Logger.info("Statistics succesfully registered as JMX bean");
        }

        inst.addTransformer(new LocksTransformer());
    }
}
