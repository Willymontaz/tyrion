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
* A GUI that takes this log file and produce reports on it

The Java agent has been created with a "low overhead" policy in mind, so that it can be added to a JVM and then forgotten, until its data are really needed.  This policy is comparable to the GC logs one : always enabled, almost invisible, VERY useful.

## Bytecode instrumentation

The agent transforms bytecode so that every synchronized block starts and ends with a call to Tyrion. Consider, for example, the following method :

    public void foo() {
      synchronized(bar) {
        //...
      }
    }

After being instrumented, it will be the following :

    public void foo() {
      synchronized(bar) {
        LocksInterceptor.enteredSynchronizedBlock(bar);
        //...
        LocksInterceptor.leavingSynchronizedBlock(bar);
      }
    }

The same rule applies for synchronzied methods.


