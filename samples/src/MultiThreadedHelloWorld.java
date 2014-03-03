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

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadedHelloWorld {

    Set<String> tokens = Collections.synchronizedSet(new TreeSet<String>());

    public synchronized void append(String token) {
        tokens.add(token);
    }

    public synchronized void printContent() {
        for (String token : tokens) {
            System.out.print(token.substring(1));
        }
        System.out.println();
    }

    public static void main(String... args) throws Exception {
        MultiThreadedHelloWorld helloWorld = new MultiThreadedHelloWorld();

        System.out.println("Press enter to start");
//        System.in.read();

        spawnThreads(1000, helloWorld);
        helloWorld.printContent();

        System.out.println("");
        System.out.println("Press enter...");
//        System.in.read();
    }

    private static void spawnThreads(int nbThreads, final MultiThreadedHelloWorld hello) throws InterruptedException {
        ArrayList<Callable<Void>> runnables = new ArrayList<Callable<Void>>();
        AppendTask appendTask = new AppendTask(hello);
        for (int j = 0; j < nbThreads; j++) {
            runnables.add(appendTask);
        }

        ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
        executor.invokeAll(runnables, 5, TimeUnit.MINUTES);
        executor.shutdownNow();
    }
}

class AppendTask implements Callable<Void> {
    private final static String[] tokens = {
            "1H", "2e", "2l", "2l", "2o"
            , "3 "
            , "4W", "5o", "5r", "6l", "7d", "8 ", "9!"
    };

    private final MultiThreadedHelloWorld helloWorld;
    private final Random random;

    AppendTask(MultiThreadedHelloWorld helloWorld) {
        this.helloWorld = helloWorld;
        random = new SecureRandom();
    }

    @Override
    public Void call() throws Exception {
        for (int i = 0; i < 1000; i++) {
            String token = tokens[random.nextInt(tokens.length)];
            helloWorld.append(token);
        }
        return null;
    }
}