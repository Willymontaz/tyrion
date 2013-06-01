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

import java.util.Vector;

public class HelloWorld {
    private Object lock = new Object();

    public synchronized void synchronizedMethod() {
        System.out.println("Hello ");
    }

    public void synchronizedBlock() {
        synchronized (lock) {
            System.out.println("World");
        }
    }

    public void externalSynchronizedMethods() {
        Vector<String> stringVector = new Vector<String>();
        StringBuffer stringBuffer = new StringBuffer();
        stringVector.add("K");
        stringVector.add("Thx");
        stringVector.add("Bye");
        for (String s : stringVector) {
            stringBuffer.append(s);
        }
        System.out.println(stringBuffer);
    }

    public static void main(String... args) {
        HelloWorld hello = new HelloWorld();

        hello.synchronizedMethod();
        hello.synchronizedBlock();
        hello.externalSynchronizedMethods();
    }


}
