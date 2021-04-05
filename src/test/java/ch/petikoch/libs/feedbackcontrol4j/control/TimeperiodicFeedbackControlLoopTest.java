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
import ch.petikoch.libs.feedbackcontrol4j.sensor.Sensor;
import ch.petikoch.libs.feedbackcontrol4j.util.ThreadSafe;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.await;

class TimeperiodicFeedbackControlLoopTest {

    @Test
    void Cache_Demo_with_PlusMinusOneController() {
        //given
        Cache cache = new Cache();
        HitrateSensor hitrateSensor = new HitrateSensor();
        Percentage hitrateSetpoint = Percentage.fromNumber(70);
        PlusMinusOneController controller = new PlusMinusOneController(hitrateSetpoint, hitrateSensor);
        TimeperiodicFeedbackControlLoop<Percentage, Integer> controlLoop = new TimeperiodicFeedbackControlLoop<>(controller, cache);

        //when
        cache.cacheSize.set(42);
        controlLoop.setInterval(10); // == start
        hitrateSensor.currentHitrateRef.set(Percentage.fromNumber(60));

        //then
        await().until(() -> cache.cacheSize.get() >= 43);

        //when
        hitrateSensor.currentHitrateRef.set(Percentage.fromNumber(80));

        //then
        await().until(() -> cache.cacheSize.get() <= 42);

        //cleanup
        controlLoop.stop();
    }

    @ThreadSafe
    private static class Cache implements Controllable<Integer> {

        final AtomicInteger cacheSize = new AtomicInteger();

        @Override
        public Integer getActualValue() {
            return cacheSize.get();
        }

        @Override
        public void applyControllerOutput(Integer newValue) {
            this.cacheSize.set(newValue);
            System.out.println("new cache size: " + newValue);
        }
    }

    @ThreadSafe
    private static class HitrateSensor implements Sensor<Percentage> {

        final AtomicReference<Percentage> currentHitrateRef = new AtomicReference<>();

        @Override
        public Percentage currentValue() {
            return currentHitrateRef.get();
        }
    }
}