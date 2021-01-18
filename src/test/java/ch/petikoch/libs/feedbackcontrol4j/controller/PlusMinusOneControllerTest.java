/**
 * Copyright 2021 Peti Koch
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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

class PlusMinusOneControllerTest {

    @Test
    void happyflow_showcase() {
        //given
        Percentage setpoint = Percentage.fromNumber(50);
        TestSensor sensor = new TestSensor();
        Controller<Percentage, Integer> testee = new PlusMinusOneController(setpoint, sensor);

        //when
        sensor.setCurrentValue(Percentage.fromNumber(49));
        Integer result = testee.calculateNewControllableValue(4);

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
    void corner_case_100_percent_setpoint() {
        //given
        Percentage setpoint = Percentage.fromNumber(100);
        TestSensor sensor = new TestSensor();
        Controller<Percentage, Integer> testee = new PlusMinusOneController(setpoint, sensor);

        //when
        sensor.setCurrentValue(Percentage.fromNumber(99));
        Integer result = testee.calculateNewControllableValue(4);

        //then
        assertThat(result).isEqualTo(5);

        //when
        sensor.setCurrentValue(Percentage.fromNumber(100));
        result = testee.calculateNewControllableValue(5);

        //then
        assertThat(result).isEqualTo(4);
    }

    @Test
    void corner_case_0_percent_setpoint() {
        //given
        Percentage setpoint = Percentage.fromNumber(0);
        TestSensor sensor = new TestSensor();
        Controller<Percentage, Integer> testee = new PlusMinusOneController(setpoint, sensor);

        //when
        sensor.setCurrentValue(Percentage.fromNumber(50));
        int result = testee.calculateNewControllableValue(2);

        //then
        assertThat(result).isEqualTo(1);

        //when
        sensor.setCurrentValue(Percentage.fromNumber(0));
        result = testee.calculateNewControllableValue(1);

        //then
        assertThat(result).isEqualTo(0);

        //when
        sensor.setCurrentValue(Percentage.fromNumber(0));
        result = testee.calculateNewControllableValue(0);

        //then
        assertThat(result).isEqualTo(0);
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
