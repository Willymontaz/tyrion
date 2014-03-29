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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

public class SimpleTest {

    static class ClassWithLocks {
        final Object lock = new Object();

        static class InternalException extends RuntimeException {
            InternalException(String message) {
                super(message);
            }
        }

        static synchronized long staticSynchronizedMethodWithDelegate() {
            return staticSynchronizedMethod();
        }

        static synchronized long staticSynchronizedMethod() {
            return 1L;
        }

        synchronized long synchronizedMethodWithDelegate() {
            return synchronizedMethod();
        }

        synchronized long synchronizedMethod() {
            return 2L;
        }

        long synchronizedBlockWithDelegate() {
            synchronized (lock) {
                return synchronizedBlock();
            }
        }

        long synchronizedBlock() {
            synchronized (lock) {
                return 3L;
            }
        }

        synchronized static long staticSynchronizedMethodWithException() {
            throw new InternalException("From static method");
        }

        synchronized long synchronizedMethodWithException() {
            throw new InternalException("From instance method");
        }

        long synchronizedBlockWithException() {
            synchronized (lock) {
                throw new InternalException("From static block");
            }
        }

    }

    public static void main(String... args) throws Exception {
        System.out.println("INFO - This test will create threads with the following behaviour :");
        System.out.println("INFO - * staticSynchronizedMethodWithDelegate  - accesses 2 synchronized static methods (reentrant) and returns normally");
        System.out.println("INFO - * synchronizedMethodWithDelegate        - accesses 2 synchronized instance methods (reentrant) and returns normally");
        System.out.println("INFO - * synchronizedBlockWithDelegate         - accesses 2 synchronized blocks (reentrant) and returns normally");
        System.out.println("INFO - * staticSynchronizedMethodWithException - accesses 1 synchronized static method and throw an Exception");
        System.out.println("INFO - * synchronizedMethodWithException       - accesses 1 synchronized instance method and throw an Exception");
        System.out.println("INFO - * synchronizedBlockWithException        - accesses 1 synchronized block and throw an Exception");
        System.out.println("");

        PrintStream stdErr = System.err;
        try (ByteArrayOutputStream interceptedStdErrBuffer = new ByteArrayOutputStream();
             PrintStream interceptedStdErr = new PrintStream(interceptedStdErrBuffer, true)) {
            System.setErr(interceptedStdErr);

            CountDownLatch countDownLatch = new CountDownLatch(6);
            startStaticSynchronizedNethodWithDelegate(countDownLatch);
            startSynchonizedMethodWithDelegate(countDownLatch);
            startSynchronizedBlockWithDelegate(countDownLatch);
            startStaticSynchronizedMethodWithException(countDownLatch);
            startSynchronizedMethodWithException(countDownLatch);
            startSynchronizedBlocksWithException(countDownLatch);
            countDownLatch.await();

            Thread.sleep(1600);

            String errorMessages = interceptedStdErrBuffer.toString();
            if (errorMessages.length() > 0) {
                System.err.println("ERROR - the test generated data on stderr, this should not happen");
                System.err.println("ERROR - content of stderr :");
                System.err.println(errorMessages);
            } else {
                System.out.println("Test completed. Now please check the output file.");
            }
        } finally {
            System.setErr(stdErr);
        }
    }


    private static void startStaticSynchronizedNethodWithDelegate(final CountDownLatch countDownLatch) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("staticSynchronizedMethodWithDelegate");
                ClassWithLocks.staticSynchronizedMethodWithDelegate();
                countDownLatch.countDown();
            }
        }).start();
    }

    private static void startSynchonizedMethodWithDelegate(final CountDownLatch countDownLatch) {
        final ClassWithLocks classWithLocks = new ClassWithLocks();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("synchronizedMethodWithDelegate");
                classWithLocks.synchronizedMethodWithDelegate();
                countDownLatch.countDown();
            }
        }).start();
    }

    private static void startSynchronizedBlockWithDelegate(final CountDownLatch countDownLatch) {
        final ClassWithLocks classWithLocks = new ClassWithLocks();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("synchronizedBlockWithDelegate");
                classWithLocks.synchronizedBlockWithDelegate();
                countDownLatch.countDown();
            }
        }).start();
    }

    private static void startStaticSynchronizedMethodWithException(final CountDownLatch countDownLatch) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("staticSynchronizedMethodWithException");
                try {
                    ClassWithLocks.staticSynchronizedMethodWithException();
                } catch (ClassWithLocks.InternalException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            }
        }).start();

    }

    private static void startSynchronizedMethodWithException(final CountDownLatch countDownLatch) {
        final ClassWithLocks classWithLocks = new ClassWithLocks();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("synchronizedMethodWithException");
                try {
                    classWithLocks.synchronizedMethodWithException();
                } catch (ClassWithLocks.InternalException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            }
        }).start();
    }

    private static void startSynchronizedBlocksWithException(final CountDownLatch countDownLatch) {
        final ClassWithLocks classWithLocks = new ClassWithLocks();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("synchronizedBlockWithException");
                try {
                    classWithLocks.synchronizedBlockWithException();
                } catch (ClassWithLocks.InternalException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            }
        }).start();
    }
}

