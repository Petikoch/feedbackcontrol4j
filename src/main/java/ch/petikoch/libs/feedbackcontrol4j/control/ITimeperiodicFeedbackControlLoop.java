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

/**
 * Triggers the control action based on time. E.g. every 1000 milliseconds.
 */
public interface ITimeperiodicFeedbackControlLoop extends IEventBasedFeedbackControlLoop {

    /**
     * Set the time interval to call the {@link #doControl()} method.
     *
     * @param millis every n milliseconds after last finished call
     */
    void setInterval(final long millis);

    /**
     * To stop the control loop.
     */
    void stop();
}
