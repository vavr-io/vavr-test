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

import io.vavr.Tuple;
import io.vavr.control.Option;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PredicateResultTest {

    @Test
    public void shouldRepresentSuccessWithoutMessage() {
        final PredicateResult result = PredicateResult.success();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.message()).isEqualTo(Option.none());
    }

    @Test
    public void shouldRepresentFailureWithMessage() {
        final PredicateResult result = PredicateResult.failure("expected 42");
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).isEqualTo(Option.some("expected 42"));
    }

    @Test
    public void shouldTreatEmptyMessageAsFailure() {
        final PredicateResult result = PredicateResult.failure("");
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).isEqualTo(Option.some(""));
        assertThat(Property.def("test").forAll(Gen.of(0).arbitrary())
                .suchThatResult(n -> result).check(0, 1).isFalsified()).isTrue();
    }

    @Test
    public void shouldRejectNullFailureMessage() {
        assertThatThrownBy(() -> PredicateResult.failure(null))
                .isInstanceOf(NullPointerException.class).hasMessage("message is null");
    }

    @Test
    public void shouldStopAtFirstFailureAndKeepItsSampleAndMessage() {
        final AtomicInteger evaluations = new AtomicInteger();
        final Arbitrary<Integer> inputs = size -> random -> evaluations.incrementAndGet();
        final CheckResult result = Property.def("less than three").forAll(inputs)
                .suchThatResult(n -> n < 3 ? PredicateResult.success() : PredicateResult.failure("too large: " + n))
                .check(0, 10);
        assertThat(result.isFalsified()).isTrue();
        assertThat(result.count()).isEqualTo(3);
        assertThat(result.sample().get()).isEqualTo(Tuple.of(3));
        assertThat(result.message().get()).isEqualTo("too large: 3");
        assertThat(evaluations.get()).isEqualTo(3);
    }

    @Test
    public void shouldKeepPostconditionMessageAfterRejectedSamples() {
        final AtomicInteger inputs = new AtomicInteger();
        final AtomicInteger postconditions = new AtomicInteger();
        final Arbitrary<Integer> values = size -> random -> inputs.incrementAndGet();
        final CheckResult result = Property.def("test").forAll(values)
                .suchThatResult(n -> n < 3 ? PredicateResult.failure("skip: " + n) : PredicateResult.success())
                .impliesResult(n -> {
                    postconditions.incrementAndGet();
                    return PredicateResult.failure("postcondition: " + n);
                }).check(0, 10);
        assertThat(result.isFalsified()).isTrue();
        assertThat(result.count()).isEqualTo(3);
        assertThat(result.sample().get()).isEqualTo(Tuple.of(3));
        assertThat(result.message().get()).isEqualTo("postcondition: 3");
        assertThat(postconditions.get()).isEqualTo(1);
    }

    @Test
    public void shouldNotRetainMessageBetweenChecks() {
        final AtomicInteger inputs = new AtomicInteger();
        final Arbitrary<Integer> values = size -> random -> inputs.incrementAndGet();
        final Checkable property = Property.def("test").forAll(values)
                .suchThatResult(n -> n == 1 ? PredicateResult.failure("first check") : PredicateResult.success());
        assertThat(property.check(0, 1).message()).isEqualTo(Option.some("first check"));
        final CheckResult second = property.check(0, 1);
        assertThat(second.isSatisfied()).isTrue();
        assertThat(second.message()).isEqualTo(Option.none());
    }

    @Test
    public void shouldPreserveMessagesWhenComposingProperties() {
        final Checkable first = Property.def("first").forAll(Gen.of(1).arbitrary())
                .suchThatResult(n -> PredicateResult.failure("first failure"));
        final Checkable second = Property.def("second").forAll(Gen.of(2).arbitrary())
                .suchThatResult(n -> PredicateResult.failure("second failure"));
        final CheckResult conjunction = first.and(second).check(0, 1);
        assertThat(conjunction.propertyName()).isEqualTo("first");
        assertThat(conjunction.sample().get()).isEqualTo(Tuple.of(1));
        assertThat(conjunction.message().get()).isEqualTo("first failure");
        final CheckResult disjunction = first.or(second).check(0, 1);
        assertThat(disjunction.propertyName()).isEqualTo("second");
        assertThat(disjunction.sample().get()).isEqualTo(Tuple.of(2));
        assertThat(disjunction.message().get()).isEqualTo("second failure");
    }

    @Test
    public void shouldNotEvaluateResultPredicateWithZeroTries() {
        final CheckResult result = Property.def("test").forAll(Gen.of(1).arbitrary())
                .suchThatResult(n -> { throw new AssertionError("must not run"); }).check(0, 0);
        assertThat(result.isSatisfied()).isTrue();
        assertThat(result.isExhausted()).isTrue();
        assertThat(result.count()).isZero();
        assertThat(result.message().isEmpty()).isTrue();
    }
}
