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
package ch.petikoch.libs.feedbackcontrol4j.filter;

/**
 * Keeps a signal in certain static range. The range can be modified at runtime through {@link #setRange(int, int)}.
 */
@ThreadSafe
public class StaticRangeLimitingFilter implements Filter<Integer> {

    private final Object internalLock = new Object();

    private int currentLowerLimit;
    private int currentUpperLimit;

    public StaticRangeLimitingFilter(final int lowerLimit,
                                     final int upperLimit) {
        setRange(lowerLimit, upperLimit);
    }

    @Override
    public Integer filter(final Integer input) {
        synchronized (internalLock) {
            if (input > currentUpperLimit) {
                return currentUpperLimit;
            }
            if (input < currentLowerLimit) {
                return currentLowerLimit;
            }
            return input;
        }
    }

    public void setRange(final int lowerLimit, final int upperLimit) {
        synchronized (internalLock) {
            currentLowerLimit = lowerLimit;
            currentUpperLimit = upperLimit;
        }
    }
}
