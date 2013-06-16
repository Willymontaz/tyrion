package fr.pingtimeout.tyrion.data;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;


/**
 * This class is used to create LockAccesses objects.
 * <p/>
 * It is deliberately unsafe.
 * Since its getInstanceFrom method is only called from a synchronized section, it will never be called concurrently.
 */
public class LockFactory {
    private final static Map<Object, LockAccesses> EXISTING_LOCKS = new IdentityHashMap<>();

    public static LockAccesses getInstanceFrom(Object target) {
        if (!EXISTING_LOCKS.containsKey(target)) {
            EXISTING_LOCKS.put(target, new LockAccesses(target));
        }

        return EXISTING_LOCKS.get(target);
    }

    public static Collection<LockAccesses> allLocks() {
        return EXISTING_LOCKS.values();
    }
}
