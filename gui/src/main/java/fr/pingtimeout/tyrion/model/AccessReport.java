package fr.pingtimeout.tyrion.model;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AccessReport {


    private final Map<Accessor, Set<Access>> criticalSectionsPerAccessor;
    private final Map<ObjectUnderLock, Set<Access>> criticalSectionsPerTarget;


    public AccessReport(Set<Access> criticalSections) {
        criticalSectionsPerAccessor = new HashMap<>();
        criticalSectionsPerTarget = new HashMap<>();

        for (Access access : criticalSections) {
            indexAccessByAccessor(access);
            indexAccessByTarget(access);
        }
    }


    private void indexAccessByAccessor(Access access) {
        Accessor accessor = access.getAccessor();

        if (!criticalSectionsPerAccessor.containsKey(accessor)) {
            criticalSectionsPerAccessor.put(accessor, new TreeSet<Access>());
        }

        Set<Access> criticalSectionsForAccessor = criticalSectionsPerAccessor.get(accessor);
        criticalSectionsForAccessor.add(access);
    }

    private void indexAccessByTarget(Access access) {
        ObjectUnderLock objectUnderLock = access.getObjectUnderLock();

        if (!criticalSectionsPerTarget.containsKey(objectUnderLock)) {
            criticalSectionsPerTarget.put(objectUnderLock, new TreeSet<Access>());
        }

        Set<Access> criticalSectionsForTarget = criticalSectionsPerTarget.get(objectUnderLock);
        criticalSectionsForTarget.add(access);
    }


    public Set<Access> retrieveCriticalSectionsFor(Accessor accessor) {
        return criticalSectionsPerAccessor.get(accessor);
    }

    public Set<Access> retrieveCriticalSectionsFor(ObjectUnderLock lock) {
        return criticalSectionsPerTarget.get(lock);
    }


    public int countDifferentAccessorsFor(ObjectUnderLock lock) {
        Set<Access> accesses = retrieveCriticalSectionsFor(lock);
        Set<Accessor> accessors = new HashSet<>();
        for (Access access : accesses) {
            accessors.add(access.getAccessor());
        }

        return accessors.size();
    }


    public Set<Access> retrieveFrequentAccesses(ObjectUnderLock lock, int delta, TimeUnit unit) {
        Set<Access> accesses = retrieveCriticalSectionsFor(lock);

        if (accesses.size() > 1) {
            return safeRetrieveConsecutiveAccessesWithin(delta, unit, accesses);
        } else {
            return new HashSet<>();
        }
    }

    private Set<Access> safeRetrieveConsecutiveAccessesWithin(int delta, TimeUnit unit, Set<Access> accesses) {
        Set<Access> frequentAccesses = new TreeSet<>();
        Iterator<Access> iterator = accesses.iterator();

        Access lastAccess = iterator.next();
        while (iterator.hasNext()) {
            Access access = iterator.next();

            boolean accessesWithinSameTimeFraction = lastAccess.matches(access, delta, unit);
            if (accessesWithinSameTimeFraction) {
                frequentAccesses.add(lastAccess);
                frequentAccesses.add(access);
            }
            lastAccess = access;
        }

        return frequentAccesses;
    }


    public Set<Access> retrieveContendedAccesses(ObjectUnderLock lock, int delta, TimeUnit unit) {
        Set<Access> frequentAccesses = retrieveFrequentAccesses(lock, delta, unit);

        if (frequentAccesses.size() > 1) {
            return safeRetrieveConsecutiveAccessesByDifferentAccessors(frequentAccesses);
        } else {
            return new HashSet<>();
        }
    }

    private Set<Access> safeRetrieveConsecutiveAccessesByDifferentAccessors(Set<Access> frequentAccesses) {
        Set<Access> contendedAccesses = new TreeSet<>();
        Iterator<Access> iterator = frequentAccesses.iterator();

        Access lastAccess = iterator.next();
        while (iterator.hasNext()) {
            Access access = iterator.next();

            boolean accessesByDifferentThreads = !lastAccess.isAccessedBy(access.getAccessor());
            if (accessesByDifferentThreads) {
                contendedAccesses.add(lastAccess);
                contendedAccesses.add(access);
            }
            lastAccess = access;
        }
        return contendedAccesses;
    }


    public Map<ObjectUnderLock, Set<Access>> retrieveAllContendedAccesses(int delta, TimeUnit unit) {
        final Map<ObjectUnderLock, Set<Access>> contendedAccessesPerLock = new HashMap<>();

        for (ObjectUnderLock lock : criticalSectionsPerTarget.keySet()) {
            Set<Access> contendedAccesses = retrieveContendedAccesses(lock, delta, unit);
            if (contendedAccesses.size() > 0) {
                contendedAccessesPerLock.put(lock, contendedAccesses);
            }
        }

        return contendedAccessesPerLock;
    }
}
