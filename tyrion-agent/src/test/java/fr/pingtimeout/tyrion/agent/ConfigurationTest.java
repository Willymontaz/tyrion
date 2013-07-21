/*
 * Copyright (c) 2013, Pierre Laporte
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
 * along with this work; if not, see <http;//www.gnu.org/licenses/>.
 */

package fr.pingtimeout.tyrion.agent;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationTest {

    @Test
    public void should_return_default_values() {
        // Given
        String agentArguments = null;

        // When
        final Configuration configuration = new Configuration(agentArguments);

        // Then
        assertDefaultConfiguration(configuration);
    }


    @Test
    public void should_return_the_given_values_for_valid_parameters() {
        // Given
        final String outputFileParam = "output-file=/tmp/output-file";
        final String includedThreadsParam = "included-threads=main,gc";
        final String excludedThreadsParam = "excluded-threads=RMI TCP Connection(1)-127.0.0.1,RMI TCP Connection(2)-127.0.0.1";

        final Configuration configuration = new Configuration(outputFileParam
                + ";" + includedThreadsParam
                + ";" + excludedThreadsParam);

        // When
        Configuration.ParameterValue outputFileValue = configuration.outputFile();
        Configuration.ParameterValue excludedThreadsValue = configuration.excludedThreads();
        Configuration.ParameterValue includedThreadsValue = configuration.includedThreads();

        // Then
        assertThat(outputFileValue.isDefaultValue()).isFalse();
        assertThat(excludedThreadsValue.isDefaultValue()).isFalse();
        assertThat(includedThreadsValue.isDefaultValue()).isFalse();
        assertThat(outputFileValue.getValues()).containsOnly("/tmp/output-file");
        assertThat(includedThreadsValue.getValues()).containsOnly("main", "gc");
        assertThat(excludedThreadsValue.getValues()).containsOnly("RMI TCP Connection(1)-127.0.0.1", "RMI TCP Connection(2)-127.0.0.1");
    }


    @Test
    public void should_revert_to_default_values_if_invalid_parameters() {
        // Given
        String argumentsWithTooManyEquals = "output-file=/tmp/output-file"
                + ";" + "included-threads=main="
                + ";" + "excluded-threads=RMI===";
        String argumentsWithTooManyColons = "output-file=/tmp/output-file"
                + ";" + "included-threads=;;;"
                + ";" + "excluded-threads=";
        String argumentsWithUnknownParam = "foo=bar";

        // When
        final Configuration configuration1 = new Configuration(argumentsWithTooManyEquals);
        final Configuration configuration2 = new Configuration(argumentsWithTooManyColons);
        final Configuration configuration3 = new Configuration(argumentsWithUnknownParam);

        // Then
        assertDefaultConfiguration(configuration1);
        assertDefaultConfiguration(configuration2);
        assertDefaultConfiguration(configuration3);
    }


    private void assertDefaultConfiguration(Configuration configuration) {
        assertThat(configuration.outputFile().isDefaultValue());
        assertThat(configuration.excludedThreads().isDefaultValue());
        assertThat(configuration.includedThreads().isDefaultValue());
        assertThat(configuration.outputFile().getValues()).containsOnly("");
        assertThat(configuration.includedThreads().getValues()).containsOnly("");
        assertThat(configuration.excludedThreads().getValues()).containsOnly("");
    }

}
