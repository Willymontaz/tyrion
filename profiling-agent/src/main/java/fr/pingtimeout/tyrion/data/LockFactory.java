package fr.pingtimeout.tyrion.data;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

public class LockFactory {
    private final static Map<Object, Lock> EXISTING_LOCKS = new IdentityHashMap<>();

    public static Lock getInstanceFrom(Object target) {
        if (!EXISTING_LOCKS.containsKey(target)) {
            EXISTING_LOCKS.put(target, new Lock(target));
        }

        return EXISTING_LOCKS.get(target);
    }

    public static Collection<Lock> allLocks() {
        return EXISTING_LOCKS.values();
    }
}
