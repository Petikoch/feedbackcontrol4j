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
package ch.petikoch.libs.feedbackcontrol4j.control;

import ch.petikoch.libs.feedbackcontrol4j.controllable.Controllable;
import ch.petikoch.libs.feedbackcontrol4j.controller.Controller;
import ch.petikoch.libs.feedbackcontrol4j.filter.Filter;
import ch.petikoch.libs.feedbackcontrol4j.util.Nullable;

import java.util.Arrays;
import java.util.Collections;

/**
 * An implementation of a feedback control closed loop. The controlling is triggered through the {@link #doControl()} method call.
 *
 * @param <S> type of setpoint
 * @param <V> type of controllable value
 */
public class EventBasedFeedbackControlLoop<S, V> implements IEventBasedFeedbackControlLoop {

    private final Controller<S, V> controller;
    private final Iterable<Filter<V>> controllerOutputFilters;
    private final Controllable<V> controllable;

    public EventBasedFeedbackControlLoop(final Controller<S, V> controller,
                                         final Controllable<V> controllable) {
        this(controller, (Filter<V>) null, controllable);
    }

    public EventBasedFeedbackControlLoop(final Controller<S, V> controller,
                                         @Nullable
                                         final Filter<V> controllerOutputFilter,
                                         final Controllable<V> controllable) {
        this(controller,
                controllerOutputFilter != null ? Arrays.asList(controllerOutputFilter) : Collections.<Filter<V>>emptyList(),
                controllable);
    }

    public EventBasedFeedbackControlLoop(final Controller<S, V> controller,
                                         @Nullable
                                         final Iterable<Filter<V>> controllerOutputFilters,
                                         final Controllable<V> controllable) {
        this.controller = controller;
        if (controllerOutputFilters != null) {
            this.controllerOutputFilters = controllerOutputFilters;
        } else {
            this.controllerOutputFilters = Collections.emptyList();
        }
        this.controllable = controllable;
    }

    @Override
    public void doControl() {
        final V controllerInput = controllable.getActualValue();
        if (controllerInput != null) {
            V controllerOutput = controller.calculateNewControllableValue(controllerInput);
            V filteredControllerOutput = controllerOutput;
            for (Filter<V> filter : controllerOutputFilters) {
                filteredControllerOutput = filter.filter(filteredControllerOutput);
            }
            controllable.applyControllerOutput(filteredControllerOutput);
        } else {
            // ignore
        }
    }
}
