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

public class HelloWorld {
    private Object lock = new Object();

    public synchronized void synchronizedMethod() {
        System.out.print("Hello ");
    }

    public static synchronized void staticSynchronizedMethod() {
        System.out.println("!!");
    }

    public void synchronizedBlock() {
        synchronized (lock) {
            System.out.print("World");
            innerSynchronizedBlock();
        }
    }

    public void innerSynchronizedBlock() {
        synchronized (lock) {
            System.out.print(" !!");
            throw new RuntimeException("Foo");
        }
    }

    public static void main(String... args) {
        HelloWorld hello = new HelloWorld();

        hello.synchronizedMethod();
        hello.synchronizedBlock();
        staticSynchronizedMethod();
    }
}
