/*
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
package ch.petikoch.libs.feedbackcontrol4j.control

import ch.petikoch.libs.feedbackcontrol4j.controllable.Controllable
import ch.petikoch.libs.feedbackcontrol4j.controller.PlusMinusOneController
import ch.petikoch.libs.feedbackcontrol4j.datatypes.Percentage
import ch.petikoch.libs.feedbackcontrol4j.sensor.Sensor
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class TimeperiodicFeedbackControlLoopTest extends Specification {

    def pollingConditions = new PollingConditions()

    def 'Cache Demo with PlusMinusOneController'() {
        given:
        def cache = new Cache()
        def hitrateSensor = new HitrateSensor()
        def hitrateSetpoint = Percentage.fromNumber(70)
        def controller = new PlusMinusOneController(hitrateSetpoint, hitrateSensor)
        def controlLoop = new TimeperiodicFeedbackControlLoop<>(controller, cache)

        when:
        cache.cacheSize.set(42)
        controlLoop.setInterval(10) // == start
        hitrateSensor.currentHitrateRef.set(Percentage.fromNumber(60))

        then:
        pollingConditions.eventually {
            assert cache.cacheSize.get() >= 43
        }

        when:
        hitrateSensor.currentHitrateRef.set(Percentage.fromNumber(80))

        then:
        pollingConditions.eventually {
            assert cache.cacheSize.get() <= 42
        }

        cleanup:
        controlLoop.stop()
    }

    @ThreadSafe
    static class Cache implements Controllable<Integer, Integer> {

        final AtomicInteger cacheSize = new AtomicInteger()

        @Override
        Integer getControllerInput() {
            return cacheSize.get()
        }

        @Override
        void applyControllerOutput(Integer controllerOutput) {
            this.cacheSize.set(controllerOutput)
            println 'new cache size: ' + controllerOutput
        }
    }

    @ThreadSafe
    static class HitrateSensor implements Sensor<Percentage> {

        final AtomicReference<Percentage> currentHitrateRef = new AtomicReference<>()

        @Override
        Percentage currentValue() {
            return currentHitrateRef.get()
        }
    }
}
