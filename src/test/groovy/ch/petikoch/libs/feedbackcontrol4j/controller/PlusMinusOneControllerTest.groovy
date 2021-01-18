/*
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
package ch.petikoch.libs.feedbackcontrol4j.controller

import ch.petikoch.libs.feedbackcontrol4j.datatypes.Percentage
import ch.petikoch.libs.feedbackcontrol4j.sensor.Sensor
import spock.lang.Specification

class PlusMinusOneControllerTest extends Specification {

    def 'Happyflow showcase'() {
        given:
        def setpoint = Percentage.fromNumber(50)
        def sensor = new TestSensor()
        def testee = new PlusMinusOneController(setpoint, sensor)

        when:
        sensor.currentValue = Percentage.fromNumber(49)
        def result = testee.calculateNewControllableValue(4)

        then:
        result == 5

        when:
        sensor.currentValue = Percentage.fromNumber(51)
        result = testee.calculateNewControllableValue(5)

        then:
        result == 4

        when:
        sensor.currentValue = Percentage.fromNumber(50)
        result = testee.calculateNewControllableValue(4)

        then:
        result == 4
    }

    def 'corner case 100% setpoint'() {
        given:
        def setpoint = Percentage.fromNumber(100)
        def sensor = new TestSensor()
        def testee = new PlusMinusOneController(setpoint, sensor)

        when:
        sensor.currentValue = Percentage.fromNumber(99)
        def result = testee.calculateNewControllableValue(4)

        then:
        result == 5

        when:
        sensor.currentValue = Percentage.fromNumber(100)
        result = testee.calculateNewControllableValue(5)

        then:
        result == 4
    }

    def 'corner case 0% setpoint'() {
        given:
        def setpoint = Percentage.fromNumber(0)
        def sensor = new TestSensor()
        def testee = new PlusMinusOneController(setpoint, sensor)

        when:
        sensor.currentValue = Percentage.fromNumber(50)
        def result = testee.calculateNewControllableValue(2)

        then:
        result == 1

        when:
        sensor.currentValue = Percentage.fromNumber(0)
        result = testee.calculateNewControllableValue(1)

        then:
        result == 0

        when:
        sensor.currentValue = Percentage.fromNumber(0)
        result = testee.calculateNewControllableValue(0)

        then:
        result == 0
    }

    private static class TestSensor implements Sensor<Percentage> {

        Percentage currentValue

        @Override
        Percentage currentValue() {
            return currentValue
        }
    }
}
