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
package ch.petikoch.libs.feedbackcontrol4j.datatypes;

import ch.petikoch.libs.feedbackcontrol4j.util.Nullable;

public class Pair<T, V> {

    private final T value1;
    private final V value2;

    public Pair(@Nullable T value1,
                @Nullable V value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    @Nullable
    public T getValue1() {
        return value1;
    }

    @Nullable
    public V getValue2() {
        return value2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (value1 != null ? !value1.equals(pair.value1) : pair.value1 != null) return false;
        return !(value2 != null ? !value2.equals(pair.value2) : pair.value2 != null);

    }

    @Override
    public int hashCode() {
        int result = value1 != null ? value1.hashCode() : 0;
        result = 31 * result + (value2 != null ? value2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "value1=" + value1 +
                ", value2=" + value2 +
                '}';
    }
}
