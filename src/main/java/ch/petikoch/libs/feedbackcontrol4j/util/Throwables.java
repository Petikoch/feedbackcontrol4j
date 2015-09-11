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
package ch.petikoch.libs.feedbackcontrol4j.util;

import static ch.petikoch.libs.feedbackcontrol4j.util.Preconditions.checkNotNull;

public class Throwables {

    private Throwables() {
    }

    public static <T extends Throwable> void propagateIfInstanceOf(@Nullable Throwable throwable,
                                                                   Class<T> declaredType) throws T {
        if (throwable != null && declaredType.isInstance(throwable)) {
            throw declaredType.cast(throwable);
        }
    }

    public static void propagateIfPossible(@Nullable Throwable throwable) {
        propagateIfInstanceOf(throwable, Error.class);
        propagateIfInstanceOf(throwable, RuntimeException.class);
    }

    public static RuntimeException propagate(Throwable throwable) {
        propagateIfPossible(checkNotNull(throwable));
        throw new RuntimeException(throwable);
    }

}
