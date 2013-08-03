package fr.pingtimeout.tyrion.model;

import java.util.Map;
import java.util.Set;

public class AccessReport {


    private final Map<Accessor, Set<Access>> criticalSectionsPerAccessor;
    private final Map<Target, Set<Access>> criticalSectionsPerTarget;


    public AccessReport(Map<Accessor, Set<Access>> criticalSectionsPerAccessor, Map<Target, Set<Access>> criticalSectionsPerTarget) {
        this.criticalSectionsPerAccessor = criticalSectionsPerAccessor;
        this.criticalSectionsPerTarget = criticalSectionsPerTarget;
    }


    public Set<Access> getCriticalSectionsForAccessor(Accessor accessor) {
        return criticalSectionsPerAccessor.get(accessor);
    }


    public Set<Access> getCriticalSectionsForLock(Target lock) {
        return criticalSectionsPerTarget.get(lock);
    }
}
