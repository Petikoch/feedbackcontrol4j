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

public class Percentage implements Comparable<Percentage> {

    private static final short INTERNAL_VALUE_MIN = 0;
    private static final short INTERNAL_VALUE_MAX = 100;

    public static final Percentage MIN_VALUE = fromNumber(INTERNAL_VALUE_MIN);
    public static final Percentage MAX_VALUE = fromNumber(INTERNAL_VALUE_MAX);

    private final short internalValue;

    protected Percentage(final short value) {
        this.internalValue = value;
    }

    public static Percentage fromString(String value) {
        String withoutProzent = value.replace("%", "");
        return new Percentage(Short.parseShort(withoutProzent.trim()));
    }

    public static Percentage fromNumber(Number value) {
        return new Percentage(value.shortValue());
    }

    public Short toNumber() {
        return internalValue;
    }

    @Override
    public String toString() {
        return internalValue + "%";
    }

    @Override
    public int compareTo(final Percentage o) {
        Short thiz = Short.valueOf(internalValue);
        Short that = Short.valueOf(o.internalValue);
        return thiz.compareTo(that);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Percentage that = (Percentage) o;

        return internalValue == that.internalValue;
    }

    @Override
    public int hashCode() {
        return (int) internalValue;
    }
}
