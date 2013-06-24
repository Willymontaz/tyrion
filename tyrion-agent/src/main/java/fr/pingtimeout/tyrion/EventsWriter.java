package fr.pingtimeout.tyrion;

import fj.data.List;
import fr.pingtimeout.tyrion.model.CriticalSectionEvent;
import fr.pingtimeout.tyrion.model.EventsHolder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class EventsWriter implements Runnable {

    public static final boolean DO_APPEND = true;
    private final String outputFile;

    public EventsWriter(String outputFile) {
        this.outputFile = outputFile;
    }

    @Override
    public void run() {
        Logger.debug("Writing events to file %s...", outputFile);

        try (FileWriter fileWriter = new FileWriter(outputFile, DO_APPEND);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            writeEvents(bufferedWriter);

        } catch (Exception ignored) {
            Logger.warn("Could not dump locks");
            Logger.debug("Stacktrace : ", ignored);
        }
    }

    private void writeEvents(BufferedWriter writer) throws IOException {
        Set<Long> threadIds = EventsHolder.INSTANCE.getThreadIds();
        for (Long threadId : threadIds) {
            writeEvents(writer, threadId);
        }
    }

    private void writeEvents(BufferedWriter writer, Long threadId) throws IOException {
        List<CriticalSectionEvent> accessorEvents = EventsHolder.INSTANCE.getAndClearEventsListOf(threadId);
        for (CriticalSectionEvent accessorEvent : accessorEvents) {
            writer.write(accessorEvent.toJson());
            writer.write("\n");
        }
    }
}
