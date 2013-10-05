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

package fr.pingtimeout.tyrion.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class SerializationTest {
    @Test
    public void should_produce_a_valid_format() throws Exception {
        // Given
        ObjectMapper jsonMapper = new ObjectMapper();

        // When
        CriticalSectionEntering enteringEvt = new CriticalSectionEntering(Thread.currentThread(), new Object());
        String serializedEntry = jsonMapper.writeValueAsString(enteringEvt);

        // Then
        System.out.println(serializedEntry);
    }
}
