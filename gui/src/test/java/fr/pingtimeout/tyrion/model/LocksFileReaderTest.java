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
    public void should_transform_events_into_critical_section_accessible_by_thread_and_monitor() {
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

        Access accessFromThread1ToLock1 = new Access(new AccessDuration(1374418691243L, 1374418691244L), accessor1, lock1);
        Access accessFromThread2ToLock2 = new Access(new AccessDuration(1374418691245L, 1374418691246L), accessor2, lock2);
        Access accessFromThread1ToLock2 = new Access(new AccessDuration(1374418691246L, 1374418691247L), accessor1, lock2);

        // When
        AccessReport accessReport = new LocksFileReader(inputStream).buildAccessReport();

        Set<Access> thread1CriticalSections = accessReport.getCriticalSectionsForAccessor(accessor1);
        Set<Access> thread2CriticalSections = accessReport.getCriticalSectionsForAccessor(accessor2);
        Set<Access> lock1CriticalSections = accessReport.getCriticalSectionsForLock(lock1);
        Set<Access> lock2CriticalSections = accessReport.getCriticalSectionsForLock(lock2);

        // Then
        assertThat(thread1CriticalSections).containsOnly(accessFromThread1ToLock1, accessFromThread1ToLock2);
        assertThat(thread2CriticalSections).containsOnly(accessFromThread2ToLock2);

        assertThat(lock1CriticalSections).containsOnly(accessFromThread1ToLock1);
        assertThat(lock2CriticalSections).containsOnly(accessFromThread1ToLock2, accessFromThread2ToLock2);
    }
}
