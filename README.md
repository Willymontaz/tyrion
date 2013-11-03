# Tyrion Lock Profiler

A Java agent that extracts runtime locking information from a JVM.

## Goals

The main goal of Tyrion is to give a clear view on how an application uses locks.  For instance, the following questions should be answered easily :

* In my application, which locks are never used ?
* In my application, which locks are used only by one thread ?
* In my application, which locks are contended ?
* How much time does my threads spend in critical sections ?
* ...

## Components

Tyrion is composed by two main components :

* A java agent that collects information in a JVM and produces a log file
* A GUI (not ready yet) that takes this log file and produce reports on it

The Java agent has been created with a "low overhead" policy in mind, so that it can be added to a JVM and then forgotten, until its data are really needed.  This policy is comparable to the GC logs one : always enabled, almost invisible, VERY useful.

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
      public void foo() {
        // Record time before trying to enter the synchronized block on class Baz
        synchronized(Baz.class) {
          // Record time once in the synchronized block on class Baz
          // original code
          // Record time before leaving the synchronized block on class Baz
        }
      }
    }





