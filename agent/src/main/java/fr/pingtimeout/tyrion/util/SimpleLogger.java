/*
 * Copyright (c) 2013-2014, Pierre Laporte
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

package fr.pingtimeout.tyrion.util;

import java.io.PrintStream;

public class SimpleLogger {

    static SimpleLogger log = new SimpleLogger(Level.DEBUG);

    private final Level level;
    private final PrintStream out;

    public SimpleLogger(Level level) {
        this.level = level;
        this.out = System.out;
    }

    public SimpleLogger(Level level, PrintStream printStream) {
        this.level = level;
        this.out = printStream;
    }

    void warnMessage(String message, Object... args) {
        if (level.isWarnEnabled()) {
            printf("WARN ", message, args);
        }
    }

    void infoMessage(String message, Object... args) {
        if (level.isInfoEnabled()) {
            printf("INFO ", message, args);
        }
    }

    void debugMessage(String message, Object... args) {
        if (level.isDebugEnabled()) {
            printf("DEBUG", message, args);
        }
    }

    void debugMessage(Exception e) {
        if (level.isDebugEnabled()) {
            e.printStackTrace(out);
        }
    }

    void traceMessage(String message, Object... args) {
        if (level.isTraceEnabled()) {
            printf("TRACE", message, args);
        }
    }

    private PrintStream printf(String level, String message, Object[] args) {
        return out.printf(level + " - " + message + '\n', args);
    }

    public static void warn(String message, Object... args) {
        log.warnMessage(message, args);
    }

    public static void info(String message, Object... args) {
        log.infoMessage(message, args);
    }

    public static void debug(String message, Object... args) {
        log.debugMessage(message, args);
    }

    public static void debug(Exception e) {
        log.debugMessage(e);
    }

    public static void trace(String message, Object... args) {
        log.traceMessage(message, args);
    }

    static enum Level {
        OFF(0), WARN(1), INFO(2), DEBUG(3), TRACE(4);

        private final int priority;

        Level(int priority) {
            this.priority = priority;
        }

        public boolean isWarnEnabled() {
            return priority >= WARN.priority;
        }

        public boolean isInfoEnabled() {
            return priority >= INFO.priority;
        }

        public boolean isDebugEnabled() {
            return priority >= DEBUG.priority;
        }

        public boolean isTraceEnabled() {
            return priority >= TRACE.priority;
        }
    }
}
