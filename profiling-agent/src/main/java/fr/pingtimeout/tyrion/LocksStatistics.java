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

package fr.pingtimeout.tyrion;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LocksStatistics implements LocksStatisticsMXBean {
    INSTANCE;

    static Logger LOG = LoggerFactory.getLogger(LocksStatistics.class);

    public static void createInstanceAndRegisterAsMXBeanLater() {
        long fiveSecondsInMillis = TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS);
        Timer timer = new Timer();
        TimerTask registerAsMXBean = new TimerTask() {
            @Override
            public void run() {
                LocksStatistics.INSTANCE.registerAsMXBean();
            }
        };

        timer.schedule(registerAsMXBean, fiveSecondsInMillis);
    }

    public static boolean createInstanceAndRegisterAsMXBean() {
        try {
            LocksStatistics.INSTANCE.registerAsMXBean();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getStatistics() {
        StringBuffer result = new StringBuffer();
        for (Map.Entry<Object, Map<Thread, Integer>> entry : LockInterceptor.USED_LOCKS_COUNTERS.entrySet()) {
            result.append(entry.getKey()).append(" is accessed by ");
            for (Map.Entry<Thread, Integer> accessEntry : entry.getValue().entrySet()) {
                result.append(accessEntry.getKey()).append(" ").append(accessEntry.getValue()).append(" times, ");
            }
            result.append("\n");
        }
        return result.toString();
    }

    private void registerAsMXBean() {
        try {
            String name = getClass().getPackage().getName() + ":type=" + getClass().getName();
            ObjectName objectName = new ObjectName(name);
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            platformMBeanServer.registerMBean(this, objectName);
        } catch (Exception ignored) {
            LOG.warn("Unable to register LocksStatistics as a MXBean, data will not be available through JMX. Cause : {}", ignored.getMessage());
            LOG.debug("Stacktrace : ", ignored);
        }
    }
}
