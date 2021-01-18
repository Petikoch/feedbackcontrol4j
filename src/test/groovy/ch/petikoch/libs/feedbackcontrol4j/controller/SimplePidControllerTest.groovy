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

class SimplePidControllerTest extends Specification {

    def 'Happyflow showcase with P=1'() {
        given:
        def setpoint = Percentage.fromNumber(50)
        def sensor = new TestSensor()
        def testee = new SimplePidController(1, 0, setpoint, sensor)

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

    def 'Happyflow showcase with P=10'() {
        given:
        def setpoint = Percentage.fromNumber(50)
        def sensor = new TestSensor()
        def testee = new SimplePidController(10, 0, setpoint, sensor)

        when:
        sensor.currentValue = Percentage.fromNumber(49)
        def result = testee.calculateNewControllableValue(4)

        then:
        result == 14

        when:
        sensor.currentValue = Percentage.fromNumber(51)
        result = testee.calculateNewControllableValue(14)

        then:
        result == 4

        when:
        sensor.currentValue = Percentage.fromNumber(50)
        result = testee.calculateNewControllableValue(4)

        then:
        result == 4
    }

    def 'Happyflow showcase with P=0 and I=2'() {
        given:
        def setpoint = Percentage.fromNumber(50)
        def sensor = new TestSensor()
        def testee = new SimplePidController(0, 2, setpoint, sensor)

        when:
        sensor.currentValue = Percentage.fromNumber(49)
        def result = testee.calculateNewControllableValue(4)

        then:
        result == 6

        when:
        result = testee.calculateNewControllableValue(4) // = the controllable did not adjust -> try harder!

        then:
        result == 8

        when:
        result = testee.calculateNewControllableValue(4) // = the controllable still did not adjust -> try even harder!!!

        then:
        result == 10
    }

    private static class TestSensor implements Sensor<Percentage> {

        Percentage currentValue

        @Override
        Percentage currentValue() {
            return currentValue
        }
    }
}
