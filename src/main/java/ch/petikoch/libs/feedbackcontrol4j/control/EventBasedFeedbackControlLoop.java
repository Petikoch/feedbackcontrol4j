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
 * @param <I> type of input value
 * @param <O> type of output value
 */
public class EventBasedFeedbackControlLoop<S, I, O> implements IEventBasedFeedbackControlLoop {

    private final Controller<S, I, O> controller;
    private final Iterable<Filter<O>> controllerOutputFilters;
    private final Controllable<I, O> controllable;

    public EventBasedFeedbackControlLoop(final Controller<S, I, O> controller,
                                         final Controllable<I, O> controllable) {
        this(controller, (Filter<O>) null, controllable);
    }

    public EventBasedFeedbackControlLoop(final Controller<S, I, O> controller,
                                         @Nullable
                                         final Filter<O> controllerOutputFilter,
                                         final Controllable<I, O> controllable) {
        this(controller,
                controllerOutputFilter != null ? Arrays.asList(controllerOutputFilter) : Collections.<Filter<O>>emptyList(),
                controllable);
    }

    public EventBasedFeedbackControlLoop(final Controller<S, I, O> controller,
                                         @Nullable
                                         final Iterable<Filter<O>> controllerOutputFilters,
                                         final Controllable<I, O> controllable) {
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
        final I controllerInput = controllable.getControllerInput();
        if (controllerInput != null) {
            O controllerOutput = controller.calculateNewControllableValue(controllerInput);
            O filteredControllerOutput = controllerOutput;
            for (Filter<O> filter : controllerOutputFilters) {
                filteredControllerOutput = filter.filter(filteredControllerOutput);
            }
            controllable.applyControllerOutput(filteredControllerOutput);
        } else {
            // ignore
        }
    }
}
