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

package fr.pingtimeout.tyrion.gui;

import fr.pingtimeout.tyrion.model.*;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Console {
    public static void main(String... args) {
        if (args.length != 1) {
            System.err.println("Missing argument : locks-file to parse");
            System.exit(1);
        }

        File file = new File(args[0]);

        printContendedAccesses(file);
    }

    private static void printContendedAccesses(File file) {
        AccessReport accessReport = new LocksFileReader(file).buildAccessReport();

        Map<Target, Set<Access>> contendedAccesses = accessReport.retrieveAllContendedAccesses(1, TimeUnit.MILLISECONDS);
        for (Map.Entry<Target, Set<Access>> targetContentedAccesses : contendedAccesses.entrySet()) {
            StringBuilder sb = new StringBuilder();

            Target target = targetContentedAccesses.getKey();
            sb.append("Contented accesses on ").append(target).append(" by ");

            Set<Access> accesses = targetContentedAccesses.getValue();
            Set<Accessor> accessors = extractAccessorsFrom(accesses);
            for (Accessor accessor : accessors) {
                sb.append(accessor).append(", ");
//                sb.append(accessor.getName()).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());

            System.out.println(sb);
        }
    }

    private static Set<Accessor> extractAccessorsFrom(Set<Access> accesses) {
        Set<Accessor> result = new HashSet<>();
        for (Access access : accesses) {
            result.add(access.getAccessor());
        }
        return result;
    }
}
