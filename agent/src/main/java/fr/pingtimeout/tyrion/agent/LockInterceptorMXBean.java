package fr.pingtimeout.tyrion.agent;

public interface LockInterceptorMXBean {
    boolean isEnabled();

    void setEnabled(boolean newState);
}
