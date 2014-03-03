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

package fr.pingtimeout.tyrion.agent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class FullAgentIT {
    @Test
    public void should_attach_to_java_application_and_profile_synchronized_locks() throws IOException {
        // Given
        String runAgentOnTestClass = String.format("java -javaagent:%s=output-file=%s -cp %s %s%n",
                new File("target/tyrion-agent-jar-with-dependencies.jar").getAbsolutePath(),
                new File("target/" + getClass().getSimpleName() + ".locks.txt").getAbsolutePath(),
                new File("target/test-classes/").getAbsolutePath(),
                TestClassWithSynchronized.class.getName()
        );


        // When
        Process javaProcess = Runtime.getRuntime().exec(runAgentOnTestClass);
        String output = flushStream(javaProcess.getInputStream());
        String error = flushStream(javaProcess.getErrorStream());

        // Then
//        Assertions.assertThat(error).isEmpty();
        Assertions.assertThat(output).contains("Test completed. Now please check the output file.");
    }

    private String flushStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader stdout = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = stdout.readLine()) != null) {
                output.append(line);
                output.append('\n');
            }
        }
        return output.toString();
    }
}
