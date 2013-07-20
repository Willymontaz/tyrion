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
 * along with this work; if not, see <http://www.gnu.org/licenses/>.
 */

package fr.pingtimeout.tyrion.agent;

import fr.pingtimeout.tyrion.util.SimpleLogger;

import java.util.*;

public class Configuration {

    private static char ARGUMENT_SEPARATOR = ':';
    private static char VALUES_SEPARATOR = ',';
    private static char NAME_VALUE_SEPARATOR = '=';


    private final Map<Parameter, ParameterValue> parameters = new HashMap<>();


    Configuration(String agentArguments) {
        initializeDefaultValues();
        if (agentArguments != null) {
            safeParse(agentArguments);
        }
    }

    private void initializeDefaultValues() {
        for (Parameter parameter : Parameter.values()) {
            parameters.put(parameter, new ParameterValue(parameter));
        }
    }

    private void safeParse(String agentArguments) {
        try {
            parse(agentArguments);
        } catch (Exception e) {
            SimpleLogger.warn("Could not parse arguments, reverting back to default values. Cause : %s", e.getMessage());
            SimpleLogger.debug(e);
            initializeDefaultValues();
        }
    }

    private void parse(String agentArguments) {
        String[] arguments = agentArguments.split(String.valueOf(ARGUMENT_SEPARATOR));
        for (String argument : arguments) {
            final int equalSign = argument.indexOf(NAME_VALUE_SEPARATOR);
            final String argumentName = argument.substring(0, equalSign);
            final String argumentValue = argument.substring(equalSign + 1, argument.length());

            final Parameter parameter = Parameter.fromName(argumentName);
            final ParameterValue value = new ParameterValue(parameter, argumentValue);

            parameters.put(parameter, value);
        }
    }


    public ParameterValue outputFile() {
        return parameters.get(Parameter.OUTPUT_FILE);
    }

    public ParameterValue excludedThreads() {
        return parameters.get(Parameter.EXCLUDED_THREADS);
    }

    public ParameterValue includedThreads() {
        return parameters.get(Parameter.INCLUDED_THREADS);
    }


    static enum Parameter {
        OUTPUT_FILE("output-file", ""),
        INCLUDED_THREADS("included-threads", ""),
        EXCLUDED_THREADS("excluded-threads", "");


        private final String name;
        private final String defaultValue;


        Parameter(String name, String defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }


        static Parameter fromName(String argument) {
            for (Parameter parameter : values()) {
                if (argument.equals(parameter.name))
                    return parameter;
            }
            throw new IllegalArgumentException("Unknown argument : " + argument);
        }
    }


    static class ParameterValue {

        private final Parameter parameter;
        private final Set<String> values;


        ParameterValue(Parameter parameter) {
            this(parameter, parameter.defaultValue);
        }

        ParameterValue(Parameter parameter, String value) {
            if (value.indexOf(NAME_VALUE_SEPARATOR) != -1) {
                throw new IllegalArgumentException("Invalid character '" + NAME_VALUE_SEPARATOR + "' in " + parameter.name);
            }

            String[] values = value.split(String.valueOf(VALUES_SEPARATOR));

            this.parameter = parameter;
            this.values = new HashSet<>(values.length);
            Collections.addAll(this.values, values);
        }


        public boolean isDefaultValue() {
            // safe-use of identity comparator on array for default value
            return values.size() == 1 && values.contains(parameter.defaultValue);
        }


        public Set<String> getValues() {
            return values;
        }


        @Override
        public String toString() {
            return parameter.name + "=" + values;
        }
    }
}
