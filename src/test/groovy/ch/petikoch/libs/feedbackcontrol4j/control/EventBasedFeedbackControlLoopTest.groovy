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
import ch.petikoch.libs.feedbackcontrol4j.filter.StaticRangeLimitingFilter
import ch.petikoch.libs.feedbackcontrol4j.sensor.Sensor
import spock.lang.Specification

class EventBasedFeedbackControlLoopTest extends Specification {

    def 'Cache Demo with PlusMinusOneController'() {
        given:
        def cache = new Cache()
        def hitrateSensor = new HitrateSensor()
        def hitrateSetpoint = Percentage.fromNumber(70)
        def controller = new PlusMinusOneController(hitrateSetpoint, hitrateSensor)
        def controlLoop = new EventBasedFeedbackControlLoop<>(controller, cache)

        when:
        cache.cacheSize = 42
        hitrateSensor.currentHitrate = Percentage.fromNumber(60)
        controlLoop.doControl()

        then:
        cache.cacheSize == 43

        when:
        hitrateSensor.currentHitrate = Percentage.fromNumber(80)
        controlLoop.doControl()

        then:
        cache.cacheSize == 42
    }

    def 'Cache Demo with PlusMinusOneController and cache size range limiting filter'() {
        given:
        def cache = new Cache()
        def hitrateSensor = new HitrateSensor()
        def hitrateSetpoint = Percentage.fromNumber(70)
        def cacheSizeFilter = new StaticRangeLimitingFilter(10, 100)
        def controller = new PlusMinusOneController(hitrateSetpoint, hitrateSensor)
        def controlLoop = new EventBasedFeedbackControlLoop<>(controller, cacheSizeFilter, cache)

        when:
        cache.cacheSize = 99
        hitrateSensor.currentHitrate = Percentage.fromNumber(60)
        controlLoop.doControl()

        then:
        cache.cacheSize == 100

        when:
        hitrateSensor.currentHitrate = Percentage.fromNumber(61)
        controlLoop.doControl()

        then:
        cache.cacheSize == 100
    }

    static class Cache implements Controllable<Integer> {

        int cacheSize

        @Override
        Integer getActualValue() {
            return cacheSize
        }

        @Override
        void applyControllerOutput(Integer newValue) {
            this.cacheSize = newValue
        }
    }

    static class HitrateSensor implements Sensor<Percentage> {

        Percentage currentHitrate

        @Override
        Percentage currentValue() {
            return currentHitrate
        }
    }

}
