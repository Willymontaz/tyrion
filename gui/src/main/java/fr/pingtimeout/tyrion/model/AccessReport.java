package fr.pingtimeout.tyrion.model;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AccessReport {


    private final Map<Accessor, Set<Access>> criticalSectionsPerAccessor;
    private final Map<Target, Set<Access>> criticalSectionsPerTarget;


    public AccessReport(Set<Access> criticalSections) {
        criticalSectionsPerAccessor = new HashMap<>();
        criticalSectionsPerTarget = new HashMap<>();

        for (Access access : criticalSections) {
            addAccessForAccessor(access);
            addAccessForTarget(access);
        }
    }


    private void addAccessForAccessor(Access access) {
        Accessor accessor = access.getAccessor();

        if (!criticalSectionsPerAccessor.containsKey(accessor)) {
            criticalSectionsPerAccessor.put(accessor, new TreeSet<Access>());
        }

        Set<Access> criticalSectionsForAccessor = criticalSectionsPerAccessor.get(accessor);
        criticalSectionsForAccessor.add(access);
    }

    private void addAccessForTarget(Access access) {
        Target target = access.getTarget();

        if (!criticalSectionsPerTarget.containsKey(target)) {
            criticalSectionsPerTarget.put(target, new TreeSet<Access>());
        }

        Set<Access> criticalSectionsForTarget = criticalSectionsPerTarget.get(target);
        criticalSectionsForTarget.add(access);
    }


    public Set<Access> retrieveCriticalSectionsFor(Accessor accessor) {
        return criticalSectionsPerAccessor.get(accessor);
    }

    public Set<Access> retrieveCriticalSectionsFor(Target lock) {
        return criticalSectionsPerTarget.get(lock);
    }

    public int countDifferentAccessorsFor(Target lock) {
        Set<Access> accesses = retrieveCriticalSectionsFor(lock);
        Set<Accessor> accessors = new HashSet<>();
        for (Access access : accesses) {
            accessors.add(access.getAccessor());
        }

        return accessors.size();
    }

    public Set<Access> retrieveFrequentAccesses(Target lock, int delta, TimeUnit unit) {
        Set<Access> accesses = retrieveCriticalSectionsFor(lock);
        Set<Access> frequentAccesses = new TreeSet<>();

        if (accesses.size() > 1) {
            Iterator<Access> iterator = accesses.iterator();
            Access lastAccess = iterator.next();
            while (iterator.hasNext()) {
                Access access = iterator.next();

                boolean matchesLast = lastAccess.matches(access, delta, unit);
                if(matchesLast) {
                    frequentAccesses.add(lastAccess);
                    frequentAccesses.add(access);
                }
                lastAccess = access;
            }
        }

        return frequentAccesses;
    }

    public Set<Access> retrieveContendedAccesses(Target lock, int delta, TimeUnit unit) {
        Set<Access> frequentAccesses = retrieveFrequentAccesses(lock, delta, unit);
        Set<Access> contendedAccesses = new TreeSet<>();

        if(frequentAccesses.size() > 1) {
            Iterator<Access> iterator = frequentAccesses.iterator();
            Access lastAccess = iterator.next();
            while (iterator.hasNext()) {
                Access access = iterator.next();

                if(!lastAccess.isAccessedBy(access.getAccessor())) {
                    contendedAccesses.add(lastAccess);
                    contendedAccesses.add(access);
                }
                lastAccess = access;
            }
        }

        return contendedAccesses;
    }
}
