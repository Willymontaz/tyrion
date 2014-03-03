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

package fr.pingtimeout.tyrion.gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pingtimeout.tyrion.model.CriticalSectionEntered;
import fr.pingtimeout.tyrion.model.CriticalSectionEvent;

public class GUI {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        CriticalSectionEntered event = mapper.readValue(
                "{\"enter\":{\"timestamp\":1372770101098,\"accessor\":{\"id\":1,\"name\":\"main\"},\"target\":{\"hashcode\":672184983,\"className\":\"HelloWorld\"}}}",
                CriticalSectionEntered.class);

        System.out.println(event);
    }
}