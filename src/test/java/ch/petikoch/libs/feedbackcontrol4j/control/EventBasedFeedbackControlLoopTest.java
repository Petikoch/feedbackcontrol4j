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
package ch.petikoch.libs.feedbackcontrol4j.control;

import ch.petikoch.libs.feedbackcontrol4j.controllable.Controllable;
import ch.petikoch.libs.feedbackcontrol4j.controller.PlusMinusOneController;
import ch.petikoch.libs.feedbackcontrol4j.datatypes.Percentage;
import ch.petikoch.libs.feedbackcontrol4j.filter.StaticRangeLimitingFilter;
import ch.petikoch.libs.feedbackcontrol4j.sensor.Sensor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventBasedFeedbackControlLoopTest {

    @Test
    void cache_demo_with_PlusMinusOneController() {
        //given
        Cache cache = new Cache();
        HitrateSensor hitrateSensor = new HitrateSensor();
        Percentage hitrateSetpoint = Percentage.fromNumber(70);
        PlusMinusOneController controller = new PlusMinusOneController(hitrateSetpoint, hitrateSensor);
        EventBasedFeedbackControlLoop<Percentage, Integer> controlLoop = new EventBasedFeedbackControlLoop<>(controller, cache);

        //when
        cache.setCacheSize(42);
        hitrateSensor.setCurrentHitrate(Percentage.fromNumber(60));
        controlLoop.doControl();

        //then
        assertThat(cache.getCacheSize()).isEqualTo(43);

        //when
        hitrateSensor.setCurrentHitrate(Percentage.fromNumber(80));
        controlLoop.doControl();

        //then
        assertThat(cache.getCacheSize()).isEqualTo(42);
    }

    @Test
    void cache_demo_with_PlusMinusOneController_and_cache_size_range_limiting_filter() {
        //given
        Cache cache = new Cache();
        HitrateSensor hitrateSensor = new HitrateSensor();
        Percentage hitrateSetpoint = Percentage.fromNumber(70);
        StaticRangeLimitingFilter cacheSizeFilter = new StaticRangeLimitingFilter(10, 100);
        PlusMinusOneController controller = new PlusMinusOneController(hitrateSetpoint, hitrateSensor);
        EventBasedFeedbackControlLoop<Percentage, Integer> controlLoop = new EventBasedFeedbackControlLoop<>(controller, cacheSizeFilter, cache);

        //when
        cache.setCacheSize(99);
        hitrateSensor.setCurrentHitrate(Percentage.fromNumber(60));
        controlLoop.doControl();

        //then
        assertThat(cache.getCacheSize()).isEqualTo(100);

        //when
        hitrateSensor.setCurrentHitrate(Percentage.fromNumber(61));
        controlLoop.doControl();

        //then
        assertThat(cache.getCacheSize()).isEqualTo(100);
    }

    private static class Cache implements Controllable<Integer> {

        private int cacheSize;

        public void setCacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
        }

        public int getCacheSize() {
            return cacheSize;
        }

        @Override
        public Integer getActualValue() {
            return cacheSize;
        }

        @Override
        public void applyControllerOutput(Integer newValue) {
            this.cacheSize = newValue;
        }
    }

    private static class HitrateSensor implements Sensor<Percentage> {

        private Percentage currentHitrate;

        public void setCurrentHitrate(Percentage currentHitrate) {
            this.currentHitrate = currentHitrate;
        }

        @Override
        public Percentage currentValue() {
            return currentHitrate;
        }
    }

}
