package fr.pingtimeout.tyrion.agent;

public class LockInterceptorStaticAccessor {

    public static final String CLASS_FQN = LockInterceptorStaticAccessor.class.getName().replace('.', '/');

    public static final String ENTERING_METHOD_NAME = "enteringCriticalSection";
    public static final String ENTER_METHOD_NAME = "enteredCriticalSection";
    public static final String EXIT_METHOD_NAME = "leavingCriticalSection";
    public static final String ENTER_EXIT_METHOD_SIGNATURE = "(Ljava/lang/Object;)V";

    public static final String CLASS_FORNAME_METHOD_NAME = "classForName";
    public static final String CLASS_FORNAME_METHOD_SIGNATURE = "(Ljava/lang/String;)Ljava/lang/Class;";


    public static LockInterceptor lockInterceptor = LockInterceptor.getInstance();

    public static void enteringCriticalSection(Object lock) {
        lockInterceptor.enteringCriticalSection(lock);
    }

    public static void enteredCriticalSection(Object lock) {
        lockInterceptor.enteredCriticalSection(lock);
    }

    public static void leavingCriticalSection(Object lock) {
        lockInterceptor.leavingCriticalSection(lock);
    }

    public static Class<?> classForName(String className) {
        return lockInterceptor.classForName(className);
    }
}
