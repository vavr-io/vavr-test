/* ____  ______________  ________________________  __________
 * \   \/   /      \   \/   /   __/   /      \   \/   /      \
 *  \______/___/\___\______/___/_____/___/\___\______/___/\___\
 *
 * Copyright 2014-2025 Vavr, https://vavr.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vavr.test;

import io.vavr.control.Option;

import java.io.Serializable;
import java.util.Objects;

/**
 * The result of evaluating a predicate for one sample, with a message explaining a failure.
 * Use with {@code suchThatResult} or {@code impliesResult} on a property builder.
 */
public final class PredicateResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final PredicateResult SUCCESS = new PredicateResult(null);

    private final String message;

    private PredicateResult(String message) {
        this.message = message;
    }

    /**
     * Returns a successful predicate result.
     *
     * @return a successful result without a message
     */
    public static PredicateResult success() {
        return SUCCESS;
    }

    /**
     * Returns a failed predicate result with an explanation.
     *
     * @param message the explanation of the failure
     * @return a failed result with the given message
     * @throws NullPointerException if message is null
     */
    public static PredicateResult failure(String message) {
        return new PredicateResult(Objects.requireNonNull(message, "message is null"));
    }

    /**
     * Tests whether the predicate succeeded.
     *
     * @return true if the predicate succeeded, false otherwise
     */
    public boolean isSuccess() {
        return message == null;
    }

    /**
     * Returns the explanation of a predicate failure, if any.
     *
     * @return the failure message, or none for a successful result
     */
    public Option<String> message() {
        return Option.of(message);
    }
}
