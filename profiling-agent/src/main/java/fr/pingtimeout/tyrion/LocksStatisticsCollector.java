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

import fr.pingtimeout.tyrion.data.LockFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public enum LocksStatisticsCollector implements LocksStatisticsMXBean {
    INSTANCE;

    public static void createInstanceAndRegisterAsMXBeanLater() {
        long fiveSecondsInMillis = TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS);
        Timer timer = new Timer();
        TimerTask registerAsMXBean = new TimerTask() {
            @Override
            public void run() {
                LocksStatisticsCollector.INSTANCE.registerAsMXBean();
            }
        };

        timer.schedule(registerAsMXBean, fiveSecondsInMillis);
    }

    public static boolean createInstanceAndRegisterAsMXBean(String outputFile) {
        try {
            INSTANCE.registerAsMXBean();
            INSTANCE.outputFile = outputFile;
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private String outputFile;


    private void registerAsMXBean() {
        try {
            String name = getClass().getPackage().getName() + ":type=" + getClass().getName();
            ObjectName objectName = new ObjectName(name);
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            platformMBeanServer.registerMBean(this, objectName);
        } catch (Exception ignored) {
            Logger.warn("Unable to register LocksStatisticsCollector as a MXBean, data will not be available through JMX. Cause : %s", ignored.getMessage());
            Logger.debug(ignored);
        }
    }


    @Override
    public String dumpLocks() {
        String result = "";
        if (outputFile.length() != 0) {
            try (FileWriter fileWriter = new FileWriter(outputFile);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                bufferedWriter.write(LockFactory.dumpAllLocksToJSON());
                result = "Successfully dumped locks statistics";
            } catch (Exception ignored) {
                result = "Could not dump locks";
                Logger.warn(result);
                Logger.debug("Stacktrace : ", ignored);
            }
        }
        return result;
    }
}
