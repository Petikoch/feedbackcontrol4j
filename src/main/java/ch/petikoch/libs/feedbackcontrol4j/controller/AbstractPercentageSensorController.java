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
package ch.petikoch.libs.feedbackcontrol4j.controller;

import ch.petikoch.libs.feedbackcontrol4j.datatypes.Percentage;
import ch.petikoch.libs.feedbackcontrol4j.sensor.Sensor;
import ch.petikoch.libs.feedbackcontrol4j.util.Nullable;
import ch.petikoch.libs.feedbackcontrol4j.util.Preconditions;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractPercentageSensorController<I, O> implements Controller<Percentage, I, O> {

    private final AtomicReference<Percentage> setpointRef = new AtomicReference<>();
    private final Sensor<Percentage> sensor;

    public AbstractPercentageSensorController(Percentage initialSetpoint,
                                              Sensor<Percentage> sensor) {
        this.sensor = sensor;
        setpoint(initialSetpoint);
    }

    @Override
    public final void setpoint(final Percentage newSetpoint) {
        Preconditions.checkNotNull(newSetpoint);
        setpointRef.set(newSetpoint);
    }

    @Nullable
    protected final Percentage getCurrentSensorValue() {
        return sensor.currentValue();
    }

    protected final Percentage getSetpoint() {
        return setpointRef.get();
    }
}

