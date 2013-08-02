package fr.pingtimeout.tyrion.model;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class LocksFileReaderTest {
//    Create an InputStream using StringUtils or IOUtils and 4 events :
//
//
//
//

    //    Check that they are converted into 2 LockAccess objects

    @Test
    public void should_transform_events_into_critical_section_by_thread() {
        // Given
        String events = ""
                + "{\"enter\":{\"timestamp\":1374418691243,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":1690956574,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"timestamp\":1374418691244,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":1690956574,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"enter\":{\"timestamp\":1374418691245,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":2112631749,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"exit\":{\"timestamp\":1374418691246,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":2112631749,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"enter\":{\"timestamp\":1374418691246,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":2112631749,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"exit\":{\"timestamp\":1374418691247,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":2112631749,\"className\":\"java.lang.Object\"}}}\n";
        InputStream inputStream = IOUtils.toInputStream(events);

        Accessor accessor1 = new Accessor(1L, "Thread 1");
        Accessor accessor2 = new Accessor(2L, "Thread 2");
        Target lock1 = new Target("java.lang.Class", 1690956574L);
        Target lock2 = new Target("java.lang.Object", 2112631749L);

        LockAccess accessFromThread1ToLock1 = new LockAccess(1374418691243L, 1374418691244L, lock1);
        LockAccess accessFromThread2ToLock2 = new LockAccess(1374418691245L, 1374418691246L, lock2);
        LockAccess accessFromThread1ToLock2 = new LockAccess(1374418691246L, 1374418691247L, lock2);

        // When
        LocksFileReader locksFileReader = new LocksFileReader(inputStream);


        // Then

        Set<LockAccess> criticalSectionsForAccessor1 = locksFileReader.getCriticalSectionsForAccessor(accessor1);
        Set<LockAccess> criticalSectionsForAccessor2 = locksFileReader.getCriticalSectionsForAccessor(accessor2);

        assertThat(criticalSectionsForAccessor1).containsOnly(accessFromThread1ToLock1);
        assertThat(criticalSectionsForAccessor2).containsOnly(accessFromThread2ToLock2, accessFromThread1ToLock2);
    }

    @Test
    public void should_transform_events_into_critical_section_by_lock() {
        // Given
        String events = ""
                + "{\"enter\":{\"timestamp\":1374418691243,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":1690956574,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"exit\":{\"timestamp\":1374418691244,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":1690956574,\"className\":\"java.lang.Class\"}}}\n"
                + "{\"enter\":{\"timestamp\":1374418691245,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":2112631749,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"exit\":{\"timestamp\":1374418691246,\"accessor\":{\"id\":2,\"name\":\"Thread 2\"},\"target\":{\"hashcode\":2112631749,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"enter\":{\"timestamp\":1374418691246,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":2112631749,\"className\":\"java.lang.Object\"}}}\n"
                + "{\"exit\":{\"timestamp\":1374418691247,\"accessor\":{\"id\":1,\"name\":\"Thread 1\"},\"target\":{\"hashcode\":2112631749,\"className\":\"java.lang.Object\"}}}\n";
        InputStream inputStream = IOUtils.toInputStream(events);

        Accessor accessor1 = new Accessor(1L, "Thread 1");
        Accessor accessor2 = new Accessor(2L, "Thread 2");
        Target lock1 = new Target("java.lang.Class", 1690956574L);
        Target lock2 = new Target("java.lang.Object", 2112631749L);

        AccessorAccess accessFromThread1ToLock1 = new AccessorAccess(new Access(1374418691243L, 1374418691244L), accessor1);
        AccessorAccess accessFromThread2ToLock2 = new AccessorAccess(new Access(1374418691245L, 1374418691246L), accessor2);
        AccessorAccess accessFromThread1ToLock2 = new AccessorAccess(new Access(1374418691246L, 1374418691247L), accessor1);

        // When
        LocksFileReader locksFileReader = new LocksFileReader(inputStream);


        // Then

        Set<AccessorAccess> criticalSectionsForLock1 = locksFileReader.getCriticalSectionsForLock(lock1);
        Set<AccessorAccess> criticalSectionsForLock2 = locksFileReader.getCriticalSectionsForLock(lock2);

        assertThat(criticalSectionsForLock1).containsOnly(accessFromThread1ToLock1);
        assertThat(criticalSectionsForLock2).containsOnly(accessFromThread2ToLock2, accessFromThread1ToLock2);
    }


}
