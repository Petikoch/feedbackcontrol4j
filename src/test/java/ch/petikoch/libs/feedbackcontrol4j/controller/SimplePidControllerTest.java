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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimplePidControllerTest {

    @Test
    void happyflow_showcase_with_p_1() {
        //given
        Percentage setpoint = Percentage.fromNumber(50);
        TestSensor sensor = new TestSensor();
        SimplePidController testee = new SimplePidController(1, 0, setpoint, sensor);

        //when
        sensor.setCurrentValue(Percentage.fromNumber(49));
        int result = testee.calculateNewControllableValue(4);

        //then
        assertThat(result).isEqualTo(5);

        //when
        sensor.setCurrentValue(Percentage.fromNumber(51));
        result = testee.calculateNewControllableValue(5);

        //then
        assertThat(result).isEqualTo(4);

        //when
        sensor.setCurrentValue(Percentage.fromNumber(50));
        result = testee.calculateNewControllableValue(4);

        //then
        assertThat(result).isEqualTo(4);
    }

    @Test
    void happyflow_showcase_with_p_10() {
        //given
        Percentage setpoint = Percentage.fromNumber(50);
        TestSensor sensor = new TestSensor();
        SimplePidController testee = new SimplePidController(10, 0, setpoint, sensor);

        //when
        sensor.setCurrentValue(Percentage.fromNumber(49));
        int result = testee.calculateNewControllableValue(4);

        //then
        assertThat(result).isEqualTo(14);

        //when
        sensor.setCurrentValue(Percentage.fromNumber(51));
        result = testee.calculateNewControllableValue(14);

        //then
        assertThat(result).isEqualTo(4);

        //when
        sensor.setCurrentValue(Percentage.fromNumber(50));
        result = testee.calculateNewControllableValue(4);

        //then
        assertThat(result).isEqualTo(4);
    }

    @Test
    void happyflow_showcase_with_p_0_I_2() {
        //given
        Percentage setpoint = Percentage.fromNumber(50);
        TestSensor sensor = new TestSensor();
        SimplePidController testee = new SimplePidController(0, 2, setpoint, sensor);

        //when
        sensor.setCurrentValue(Percentage.fromNumber(49));
        int result = testee.calculateNewControllableValue(4);

        //then
        assertThat(result).isEqualTo(6);

        //when
        result = testee.calculateNewControllableValue(4); // = the controllable did not adjust -> try harder!

        //then
        assertThat(result).isEqualTo(8);

        //when
        result = testee.calculateNewControllableValue(4); // = the controllable still did not adjust -> try even harder!!!

        //then
        assertThat(result).isEqualTo(10);
    }

    private static class TestSensor implements Sensor<Percentage> {

        private Percentage currentValue;

        @Override
        public Percentage currentValue() {
            return currentValue;
        }

        public void setCurrentValue(Percentage currentValue) {
            this.currentValue = currentValue;
        }
    }
}
