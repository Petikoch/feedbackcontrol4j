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

public class PlusMinusOneController extends AbstractPercentageSensorController<Integer> {

    public PlusMinusOneController(Percentage initialSetpoint, Sensor<Percentage> sensor) {
        super(initialSetpoint, sensor);
    }

    @Override
    public Integer calculateNewControllableValue(Integer controllableActualValue) {
        Percentage currentSetpoint = getSetpoint();
        Percentage currentSensorValue = getCurrentSensorValue();
        if (currentSensorValue != null) {
            int compareToResult = currentSensorValue.compareTo(currentSetpoint);
            int result;
            if (compareToResult < 0) {
                result = controllableActualValue + 1;
            } else if (compareToResult == 0) {
                if ((controllableActualValue > 0) &&
                        (currentSetpoint.equals(Percentage.MIN_VALUE) || currentSetpoint.equals(Percentage.MAX_VALUE))) {
                    // Corner case: try to reduce, always.
                    result = controllableActualValue - 1;
                } else {
                    // don't change
                    result = controllableActualValue;
                }
            } else if (compareToResult > 0) {
                result = controllableActualValue - 1;
            } else {
                throw new IllegalStateException("Unhandled case: " + compareToResult);
            }
            return result;
        } else {
            return controllableActualValue;
        }
    }
}

