package fr.pingtimeout.lockprofiling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockInterceptor {
    static Logger LOG = LoggerFactory.getLogger(LockInterceptor.class);

    public static void enteredSynchronizedMethod() {
        LOG.info("Someone entered a critical section !", new Throwable("Here"));
    }
    public static void leftSynchronizedMethod() {
        LOG.info("Someone left a critical section !", new Throwable("Here"));
    }
}
