# Tyrion Lock Profiler

Tyrion is a lock profiler that extracts runtime locks information from your application an lets you solve your locking issues.

## How does it work ?

The two main parts of the lock profiler are :

* A Java Agent that collects locking information and creates a locking-report file
* A standalone application that parses this report and answer your questions

The Java Agent is designed to have a low overhead on your application, so that you don't solve race conditions by just adding the profiler to your application (but that would be fun).

This policy is comparable to the GC logs one : should always be enabled, are almost invisible, are VERY useful.

## Goals

The main goal of Tyrion is to give a clear view on how an application uses locks.  For instance, the following questions should be answered easily :

* In my application, which locks are used only by one thread ?
* In my application, which locks are contended ?
* How much time does my threads spend in critical sections ?
* ...

## Bytecode instrumentation

The agent transforms bytecode so that every synchronized block starts and ends with a call to Tyrion. Consider, for example, the following method :

    public void foo() {
      synchronized(bar) {
        // Your business logic...
      }
    }

After being instrumented, it will be the following :

    public void foo() {
      synchronized(bar) {
        LocksInterceptor.enteredSynchronizedBlock(bar);
        // Your business logic...
        LocksInterceptor.leavingSynchronizedBlock(bar);
      }
    }

The same rule applies for synchronzied methods.

## Licence

Tyrion is distributed under the GPL v3 licence.

