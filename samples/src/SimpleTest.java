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

import java.util.concurrent.CountDownLatch;

public class SimpleTest {

    static class ClassWithLocks {
        final Object lock = new Object();

        static synchronized long staticMethodOne() {
            return staticMethodTwo();
        }

        static synchronized long staticMethodTwo() {
            return 1L;
        }

        synchronized long instanceMethodOne() {
            return instanceMethodTwo();
        }

        synchronized long instanceMethodTwo() {
            return 2L;
        }

        long blockOne() {
            synchronized (lock) {
                return blockTwo();
            }
        }

        long blockTwo() {
            synchronized (lock) {
                return 3L;
            }
        }

        synchronized static long staticMethodWithException() {
            throw new RuntimeException("From static method");
        }

        synchronized long instanceMethodWithException() {
            throw new RuntimeException("From instance method");
        }

        long blockWithException() {
            synchronized (lock) {
                throw new RuntimeException("From static block");
            }
        }

    }

    public static void main(String... args) throws Exception {
        System.out.println("This test will create :");
        System.out.println("- Test-Ok-St-Meth - 1 thread that accesses 2 synchronized static methods (reentrant) and returns normally");
        System.out.println("- Test-Ok-Meth    - 1 thread that accesses 2 synchronized instance methods (reentrant) and returns normally");
        System.out.println("- Test-Ok-Block   - 1 thread that accesses 2 synchronized blocks (reentrant) and returns normally");
        System.out.println("- Test-Ex-St-Meth - 1 thread that accesses 1 synchronized static method and throw an Exception");
        System.out.println("- Test-Ex-Meth    - 1 thread that accesses 1 synchronized instance method and throw an Exception");
        System.out.println("- Test-Ex-Block   - 1 thread that accesses 1 synchronized block and throw an Exception");
        System.out.println("");
        System.out.println("Press enter to start");
        System.in.read();

        CountDownLatch countDownLatch = new CountDownLatch(6);
        spawnTestOkStMeth(countDownLatch);
        spawnTestOkMeth(countDownLatch);
        spawnTestOkBlock(countDownLatch);
        spawnTestExStMeth(countDownLatch);
        spawnTestExMeth(countDownLatch);
        spawnTestExBlock(countDownLatch);

        countDownLatch.await();
        System.out.println("");
        System.out.println("Test successfully completed. Now please check the output file.");
        System.in.read();
    }


    private static void spawnTestOkStMeth(final CountDownLatch countDownLatch) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("Test-Ok-St-Meth");
                ClassWithLocks.staticMethodOne();
                countDownLatch.countDown();
            }
        }).start();
    }

    private static void spawnTestOkMeth(final CountDownLatch countDownLatch) {
        final ClassWithLocks classWithLocks = new ClassWithLocks();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("Test-Ok-Meth");
                classWithLocks.instanceMethodOne();
                countDownLatch.countDown();
            }
        }).start();
    }

    private static void spawnTestOkBlock(final CountDownLatch countDownLatch) {
        final ClassWithLocks classWithLocks = new ClassWithLocks();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("Test-Ok-Block");
                classWithLocks.blockOne();
                countDownLatch.countDown();
            }
        }).start();
    }

    private static void spawnTestExStMeth(final CountDownLatch countDownLatch) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("Test-Ex-St-Meth");
                try {
                    ClassWithLocks.staticMethodWithException();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }
        }).start();

    }

    private static void spawnTestExMeth(final CountDownLatch countDownLatch) {
        final ClassWithLocks classWithLocks = new ClassWithLocks();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("Test-Ex-Meth");
                try {
                    classWithLocks.instanceMethodWithException();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }
        }).start();
    }

    private static void spawnTestExBlock(final CountDownLatch countDownLatch) {
        final ClassWithLocks classWithLocks = new ClassWithLocks();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("Test-Ex-Block");
                try {
                    classWithLocks.blockWithException();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }
        }).start();
    }
}
