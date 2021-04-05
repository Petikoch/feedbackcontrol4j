/**
 * Copyright 2021 Peti Koch
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
package ch.petikoch.libs.feedbackcontrol4j.controller;

import ch.petikoch.libs.feedbackcontrol4j.datatypes.Percentage;
import ch.petikoch.libs.feedbackcontrol4j.sensor.Sensor;
import ch.petikoch.libs.feedbackcontrol4j.util.Preconditions;
import ch.petikoch.libs.feedbackcontrol4j.util.ThreadSafe;

/**
 * An experimental, simple PID controller implementation without concerning time-change.
 */
@ThreadSafe
public class SimplePidController extends AbstractPercentageSensorController<Integer> {

    private final int kp, ki, kd;
    private int i, d, prev;

    private final Object lock = new Object();

    public SimplePidController(int kp,
                               int ki,
                               Percentage initialSetpoint,
                               Sensor<Percentage> sensor) {
        this(kp, ki, 0, initialSetpoint, sensor);
    }

    public SimplePidController(int kp,
                               int ki,
                               int kd,
                               Percentage initialSetpoint,
                               Sensor<Percentage> sensor) {
        super(initialSetpoint, sensor);

        Preconditions.checkArgument(kp >= 0);
        Preconditions.checkArgument(ki >= 0);
        Preconditions.checkArgument(kd >= 0);

        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    @Override
    public Integer calculateNewControllableValue(Integer controllableActualValue) {
        Percentage currentSetpoint = getSetpoint();
        Percentage currentSensorValue = getCurrentSensorValue();
        if (currentSensorValue != null) {
            int e = currentSetpoint.toNumber() - currentSensorValue.toNumber();
            synchronized (lock) {
                i += e;
                d = (e - prev);
                prev = e;
            }

            return controllableActualValue + ((kp * e) + (ki * i) + (kd * d));
        } else {
            return controllableActualValue;
        }
    }
}
