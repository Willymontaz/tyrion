package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocksFileReader {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    private final Logger logger = Logger.getLogger(getClass().getName());

    private final Map<Accessor, Set<Access>> criticalSectionsPerAccessor;
    private final Map<Target, Set<Access>> criticalSectionsPerTarget;

    public LocksFileReader(InputStream inputStream) {
        criticalSectionsPerAccessor = new HashMap<>();
        criticalSectionsPerTarget = new HashMap<>();

        loadFile(inputStream);
    }


    private void loadFile(InputStream inputStream) {
        final Map<Accessor, CriticalSectionEntered> lastEnterInCriticalSection = new HashMap<>();

        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                processLine(line, lastEnterInCriticalSection);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not load file", e);
        }
    }


    private void processLine(String line, Map<Accessor, CriticalSectionEntered> lastEnterInCriticalSection) throws IOException {
        if (line.startsWith("{\"enter")) {
            CriticalSectionEntered event = OBJECT_MAPPER.readValue(line, CriticalSectionEntered.class);
            process(event, lastEnterInCriticalSection);
        } else {
            CriticalSectionExit event = OBJECT_MAPPER.readValue(line, CriticalSectionExit.class);
            process(event, lastEnterInCriticalSection);
        }
    }


    private void process(CriticalSectionExit exit, Map<Accessor, CriticalSectionEntered> lastEnterInCriticalSection) {
        Accessor accessor = exit.getAccessor();
        CriticalSectionEntered lastEnter = lastEnterInCriticalSection.get(accessor);

        if (lastEnter == null) {
            logger.log(Level.FINE, "Got 'exit' without matching 'enter', ignoring event", exit);
        } else {
            Access criticalSection = new Access(lastEnter.getTimestamp(), exit.getTimestamp(), accessor, lastEnter.getTarget());
            addAccess(criticalSection);
            lastEnterInCriticalSection.remove(accessor);
        }
    }


    private void addAccess(Access access) {
        addAccessForAccessor(access);
        addAccessForTarget(access);
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


    private void process(CriticalSectionEntered enter, Map<Accessor, CriticalSectionEntered> lastEnterInCriticalSection) {
        Accessor accessor = enter.getAccessor();
        CriticalSectionEntered lastEnter = lastEnterInCriticalSection.get(accessor);

        if (lastEnter == null) {
            lastEnterInCriticalSection.put(accessor, enter);
        } else {
            logger.log(Level.FINE, "Got two consecutive 'enter', ignoring the first one", enter);
        }
    }


    public AccessReport buildAccessReport() {
        return new AccessReport(criticalSectionsPerAccessor, criticalSectionsPerTarget);
    }
}