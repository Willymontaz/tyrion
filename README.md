# Tyrion Lock Profiler

Tyrion is a lock profiler that extracts runtime locks information from your application an lets you solve your locking issues.

## How does it work ?

The two main parts of the lock profiler are :

* A Java Agent that collects locking information and creates a locking-report file
* A standalone application that parses this report and answer your questions

The Java Agent is designed to have a low overhead on your application, so that you don't magically solve your race conditions by slowing your code (but that would be fun).

This policy is comparable to the GC logs one : should always be enabled, are almost invisible, are VERY useful.

## Goals

The main goal of Tyrion is to give a clear view on how an application uses locks.  For instance, the following questions should be answered easily :

* In my application, which locks are used only by one thread ?
* In my application, which locks are contended ?
* How much time does my threads spend in critical sections ?
* ...

## Bytecode instrumentation

The agent transforms bytecode so that every synchronized block/method starts and ends with a call to Tyrion.

Note that the transformations applied can cause unwanted side effects when used with other bytecode manipulation frameworks.

### For synchronized blocks
The following method contains a synchronized block.

    public void foo() {
      synchronized(bar) {
        // original code
      }
    }

After instrumentation, the method pseudocode will be like the following :

    public void foo() {
      // Record time before trying to enter the synchronized block on bar
      synchronized(bar) {
        // Record time once in the synchronized block on bar
        // original code
        // Record time before leaving the synchronized block on bar
      }
    }

### For synchonized methods
Synchronized methods are converted into regular method with synchronized blocks.

    public synchronized void foo() {
      // original code
    }

After instrumentation, the method pseudocode will be like the following :

    public void foo() {
      // Record time before trying to enter the synchronized block on this
      synchronized(this) {
        // Record time once in the synchronized block on this
        // original code
        // Record time before leaving the synchronized block on this
      }
    }

### For static synchonized methods
Static synchronized methods are converted the same way.

    class Baz {
      public static synchronized void foo() {
        // original code
      }
    }

After instrumentation, the method pseudocode will be like the following :

    class Baz {
      public static void foo() {
        // Record time before trying to enter the synchronized block on class Baz
        synchronized(Baz.class) {
          // Record time once in the synchronized block on class Baz
          // original code
          // Record time before leaving the synchronized block on class Baz
        }
      }
    }





