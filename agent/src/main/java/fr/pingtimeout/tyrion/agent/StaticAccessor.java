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

package fr.pingtimeout.tyrion.agent;

public enum StaticAccessor {
    BEFORE_MONITORENTER_ON_OBJECT("enteringCriticalSection", "(Ljava/lang/Object;)V"),
    AFTER_MONITORENTER_ON_OBJECT("enteredCriticalSection", "(Ljava/lang/Object;)V"),
    BEFORE_MONITOREXIT_ON_OBJECT("leavingCriticalSection", "(Ljava/lang/Object;)V"),

    BEFORE_MONITORENTER_ON_CLASS("enteringCriticalSection", "(Ljava/lang/Class;)V"),
    AFTER_MONITORENTER_ON_CLASS("enteredCriticalSection", "(Ljava/lang/Class;)V"),
    BEFORE_MONITOREXIT_ON_CLASS("leavingCriticalSection", "(Ljava/lang/Class;)V"),

    RETRIEVE_CLASS_BY_NAME("classForName", "(Ljava/lang/String;)Ljava/lang/Class;")
    ;


    public static final String CLASS_FQN = StaticAccessor.class.getName().replace('.', '/');

    public static LockInterceptor lockInterceptor = LockInterceptor.getInstance();


    private final String methodName;
    private final String signature;

    StaticAccessor(String methodName, String signature) {
        this.methodName = methodName;
        this.signature = signature;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getSignature() {
        return signature;
    }


    public static void enteringCriticalSection(Object lock) {
        lockInterceptor.enteringCriticalSection(lock);
    }

    public static void enteredCriticalSection(Object lock) {
        lockInterceptor.enteredCriticalSection(lock);
    }

    public static void leavingCriticalSection(Object lock) {
        lockInterceptor.leavingCriticalSection(lock);
    }

    public static void enteredCriticalSection(Class<?> lock) {
        lockInterceptor.enteredCriticalSection(lock);
    }

    public static void leavingCriticalSection(Class<?> lock) {
        lockInterceptor.leavingCriticalSection(lock);
    }

    public static Class<?> classForName(String className) {
        return lockInterceptor.classForName(className);
    }
}
