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

import ch.petikoch.libs.feedbackcontrol4j.datatypes.Pair;
import ch.petikoch.libs.feedbackcontrol4j.util.ThreadSafe;

/**
 * Keeps a signal in certain "dynamically calculated" range.
 */
@ThreadSafe // if the RangeCalculator is
public class DynamicRangeLimitingFilter implements Filter<Integer> {

    private final RangeCalculator rangeCalculator;

    public DynamicRangeLimitingFilter(final RangeCalculator rangeCalculator) {
        this.rangeCalculator = rangeCalculator;
    }

    @Override
    public Integer filter(final Integer input) {
        Pair<Integer, Integer> currentRange = rangeCalculator.calculateRange();
        final Integer currentLowerLimit = currentRange.getValue1();
        final Integer currentUpperLimit = currentRange.getValue2();
        if ((currentUpperLimit != null) && (input > currentUpperLimit)) {
            return currentUpperLimit;
        }
        if ((currentLowerLimit != null) && (input < currentLowerLimit)) {
            return currentLowerLimit;
        }
        return input;
    }

    @ThreadSafe // implementations should be threadsafe
    public interface RangeCalculator {
        Pair<Integer, Integer> calculateRange();
    }

}
