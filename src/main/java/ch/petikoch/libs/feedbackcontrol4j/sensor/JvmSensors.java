/**
 * Copyright 2015 Peti Koch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.petikoch.libs.feedbackcontrol4j.sensor;

import ch.petikoch.libs.feedbackcontrol4j.datatypes.Percentage;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class JvmSensors {

    private JvmSensors() {
    }

    public static Sensor<Percentage> HOST_CPU_LOAD = new HostCpuLoadSensor();

    public static Sensor<Percentage> PROCESS_MEM_LOAD = new ProcessMemLoadSensor();

    private static class HostCpuLoadSensor implements Sensor<Percentage> {

        private final OperatingSystemMXBean osMxBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        @Override
        public Percentage currentValue() {
            //http://stackoverflow.com/questions/47177/how-to-monitor-the-computers-cpu-memory-and-disk-usage-in-java
            double systemCpuLoad = osMxBean.getSystemCpuLoad();
            if (systemCpuLoad >= 0.0) {
                double percentage = systemCpuLoad * 100;
                int displayValue = (int) Math.round(percentage);
                return Percentage.fromNumber(displayValue);
            } else {
                return null; //unknown
            }
        }
    }

    private static class ProcessMemLoadSensor implements Sensor<Percentage> {

        private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        @Override
        public Percentage currentValue() {
            MemoryUsage usage = memoryMXBean.getHeapMemoryUsage();
            if (usage != null) {
                long max = usage.getMax();
                long used = usage.getUsed();
                if (max >= 0 && used >= 0) {
                    int percentage = (int) (100 * used / max);
                    return Percentage.fromNumber(percentage);
                }
            }
            return Percentage.fromNumber(0);
        }
    }
}
