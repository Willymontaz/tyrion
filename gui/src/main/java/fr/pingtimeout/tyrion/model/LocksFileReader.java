package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocksFileReader {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    private final Logger logger = Logger.getLogger(getClass().getName());

    private final Map<Accessor, Set<LockAccess>> criticalSectionsPerAccessor;
    private final Map<Target, Set<AccessorAccess>> criticalSectionsPerTarget;


    public LocksFileReader(InputStream inputStream) {
        criticalSectionsPerAccessor = loadFile(inputStream);
        criticalSectionsPerTarget = buildFrom(criticalSectionsPerAccessor);

    }

    private Map<Accessor, Set<LockAccess>> loadFile(InputStream inputStream) {
        final Map<Accessor, Set<LockAccess>> criticalSections = new HashMap<>();
        final Map<Accessor, CriticalSectionEntered> lastEnterInCriticalSection = new HashMap<>();

        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                processLine(line, criticalSections, lastEnterInCriticalSection);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not load file", e);
        }

        return criticalSections;
    }


    private void processLine(String line, Map<Accessor, Set<LockAccess>> criticalSections, Map<Accessor, CriticalSectionEntered> lastEnterInCriticalSection) throws IOException {
        if (line.startsWith("{\"enter")) {
            CriticalSectionEntered event = OBJECT_MAPPER.readValue(line, CriticalSectionEntered.class);
            process(event, lastEnterInCriticalSection);
        } else {
            CriticalSectionExit event = OBJECT_MAPPER.readValue(line, CriticalSectionExit.class);
            process(event, criticalSections, lastEnterInCriticalSection);
        }
    }


    private void process(CriticalSectionExit exit, Map<Accessor, Set<LockAccess>> criticalSections, Map<Accessor, CriticalSectionEntered> lastEnterInCriticalSection) {
        Accessor accessor = exit.getAccessor();
        CriticalSectionEntered lastEnter = lastEnterInCriticalSection.get(accessor);

        if (lastEnter == null) {
            logger.log(Level.FINE, "Got 'exit' without matching 'enter', ignoring event", exit);
        } else {
            LockAccess criticalSection = new LockAccess(lastEnter.getTimestamp(), exit.getTimestamp(), lastEnter.getTarget());
            addCriticalSesion(criticalSection, accessor, criticalSections);
            lastEnterInCriticalSection.remove(accessor);
        }
    }


    private void addCriticalSesion(LockAccess criticalSection, Accessor accessor, Map<Accessor, Set<LockAccess>> criticalSections) {
        if (!criticalSections.containsKey(accessor)) {
            criticalSections.put(accessor, new HashSet<LockAccess>());
        }

        Set<LockAccess> criticalSectionsForAccessor = criticalSections.get(accessor);
        criticalSectionsForAccessor.add(criticalSection);
    }


    private void process(CriticalSectionEntered enter, Map<Accessor, CriticalSectionEntered> lastEnterInCriticalSection) {
        Accessor accessor = enter.getAccessor();
        CriticalSectionEntered lastEnter = lastEnterInCriticalSection.get(accessor);

        if (lastEnter == null) {
            lastEnterInCriticalSection.put(accessor, enter);
        } else {
            logger.log(Level.FINE, "Got two consecutive 'enter', ignoring the first one", enter);
        }
    }


    public Set<LockAccess> getCriticalSectionsForAccessor(Accessor accessor) {
        return criticalSectionsPerAccessor.get(accessor);
    }


    private Map<Target, Set<AccessorAccess>> buildFrom(Map<Accessor, Set<LockAccess>> criticalSectionsPerAccessor) {
        final Map<Target, Set<AccessorAccess>> accessorAccessesPerTarget = new HashMap<>();

        for (Map.Entry<Accessor, Set<LockAccess>> targetSetEntry : criticalSectionsPerAccessor.entrySet()) {
            Accessor accessor = targetSetEntry.getKey();
            Set<LockAccess> accesses = targetSetEntry.getValue();

            for (LockAccess lockAccess : accesses) {
                addAccessorAccess(accessor, lockAccess, accessorAccessesPerTarget);
            }
        }

        return accessorAccessesPerTarget;
    }

    private void addAccessorAccess(Accessor accessor, LockAccess lockAccess, Map<Target, Set<AccessorAccess>> accessorAccessesPerTarget) {
        Access access = lockAccess.getAccess();
        Target target = lockAccess.getTarget();
        AccessorAccess accessorAccess = new AccessorAccess(access, accessor);
        if(!accessorAccessesPerTarget.containsKey(target)) {
            accessorAccessesPerTarget.put(target, new TreeSet<AccessorAccess>());
        }
        Set<AccessorAccess> accessorAccesses = accessorAccessesPerTarget.get(target);
        accessorAccesses.add(accessorAccess);
    }

    public Set<AccessorAccess> getCriticalSectionsForLock(Target lock) {
        return criticalSectionsPerTarget.get(lock);
    }
}