/*
 * Copyright (c) 2013-2014, Pierre Laporte
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this work; if not, see <http://www.gnu.org/licenses/>.
 */

package fr.pingtimeout.tyrion.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import fj.data.List;
import fr.pingtimeout.tyrion.model.CriticalSectionEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class EventsWriter implements Runnable {

    public static final boolean DO_APPEND = true;

    public static final String THREAD_NAME = "TyrionLocksWriter";

    private final ObjectMapper jsonMapper;

    private final String outputFile;


    public EventsWriter(String outputFile) {
        this.outputFile = outputFile;
        jsonMapper = new ObjectMapper();
    }


    @Override
    public void run() {
        SimpleLogger.debug("Writing events to file %s...", outputFile);

        try (FileWriter fileWriter = new FileWriter(outputFile, DO_APPEND);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            writeEvents(bufferedWriter);

        } catch (Exception ignored) {
            SimpleLogger.warn("Could not dump locks");
            SimpleLogger.debug(ignored);
        }
    }


    private void writeEvents(BufferedWriter writer) throws IOException {
        Set<Long> threadIds = EventsHolderSingleton.INSTANCE.getThreadIds();
        for (Long threadId : threadIds) {
            writeEvents(writer, threadId);
        }
    }


    private void writeEvents(BufferedWriter writer, Long threadId) throws IOException {
        List<CriticalSectionEvent> accessorEvents = EventsHolderSingleton.INSTANCE.getAndClearEventsListOf(threadId);

        // Careful ! This list is a Cons-List with items being head-appended
        // Iterate through the list in the reverse order
        for (int i = accessorEvents.length() - 1; i >= 0; i--) {
            CriticalSectionEvent accessorEvent = accessorEvents.index(i);
            writer.write(jsonMapper.writeValueAsString(accessorEvent));
            writer.write("\n");
        }
    }
}
