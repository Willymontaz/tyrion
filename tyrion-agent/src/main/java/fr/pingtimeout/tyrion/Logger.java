package fr.pingtimeout.tyrion;

import java.io.PrintStream;

public class Logger {
    public static final boolean WARN = true;
    public static final boolean INFO = true;
    public static final boolean DEBUG = false;
    private static final boolean TRACE = false;

    public static void warn(String message, Object... args) {
        if (WARN)
            printf("WARN", message, args);
    }

    public static void info(String message, Object... args) {
        if (INFO)
            printf("INFO", message, args);
    }

    public static void debug(String message, Object... args) {
        if (DEBUG)
            printf("DEBUG", message, args);
    }

    public static void debug(Exception e) {
        if (DEBUG)
            e.printStackTrace();
    }

    public static void trace(String message, Object... args) {
        if (TRACE)
            printf("TRACE", message, args);
    }

    private static PrintStream printf(String level, String message, Object[] args) {
        return System.out.printf(level + " - " + message + '\n', args);
    }
}
