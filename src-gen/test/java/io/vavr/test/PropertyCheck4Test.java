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

/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*\
   G E N E R A T O R   C R A F T E D
\*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.vavr.CheckedFunction4;
import io.vavr.Tuple;
import org.junit.Test;

public class PropertyCheck4Test {

    static final Arbitrary<Object> OBJECTS = Gen.of(null).arbitrary();

    @Test
    public void shouldApplyForAllOfArity4() {
        final Property.ForAll4<Object, Object, Object, Object> forAll = Property.def("test").forAll(null, null, null, null);
        assertThat(forAll).isNotNull();
    }

    @Test
    public void shouldApplySuchThatOfArity4() {
        final Property.ForAll4<Object, Object, Object, Object> forAll = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS);
        final CheckedFunction4<Object, Object, Object, Object, Boolean> predicate = (o1, o2, o3, o4) -> true;
        final Property.Property4<Object, Object, Object, Object> suchThat = forAll.suchThat(predicate);
        assertThat(suchThat).isNotNull();
    }

    @Test
    public void shouldCheckTrueProperty4() {
        final Property.ForAll4<Object, Object, Object, Object> forAll = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS);
        final CheckedFunction4<Object, Object, Object, Object, Boolean> predicate = (o1, o2, o3, o4) -> true;
        final CheckResult result = forAll.suchThat(predicate).check();
        assertThat(result.isSatisfied()).isTrue();
        assertThat(result.isExhausted()).isFalse();
    }

    @Test
    public void shouldCheckFalseProperty4() {
        final Property.ForAll4<Object, Object, Object, Object> forAll = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS);
        final CheckedFunction4<Object, Object, Object, Object, Boolean> predicate = (o1, o2, o3, o4) -> false;
        final CheckResult result = forAll.suchThat(predicate).check();
        assertThat(result.isFalsified()).isTrue();
        assertThat(result.message().isEmpty()).isTrue();
    }

    @Test
    public void shouldCheckSuccessfulPredicateResult4() {
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThatResult((o1, o2, o3, o4) -> PredicateResult.success()).check(0, 3);
        assertThat(result.isSatisfied()).isTrue();
        assertThat(result.isExhausted()).isFalse();
        assertThat(result.count()).isEqualTo(3);
        assertThat(result.message().isEmpty()).isTrue();
    }

    @Test
    public void shouldReportPredicateFailureMessage4() {
        final CheckResult result = Property.def("test")
                .forAll(Gen.of(1).arbitrary(), Gen.of(2).arbitrary(), Gen.of(3).arbitrary(), Gen.of(4).arbitrary())
                .suchThatResult((o1, o2, o3, o4) -> PredicateResult.failure("failed: " + Tuple.of(o1, o2, o3, o4)))
                .check(0, 3);
        assertThat(result.isFalsified()).isTrue();
        assertThat(result.isErroneous()).isFalse();
        assertThat(result.count()).isEqualTo(1);
        assertThat(result.sample().get()).isEqualTo(Tuple.of(1, 2, 3, 4));
        assertThat(result.message().get()).isEqualTo("failed: (1, 2, 3, 4)");
        assertThat(result.error().isEmpty()).isTrue();
        assertThatThrownBy(result::assertIsSatisfied)
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("failed: (1, 2, 3, 4)");
    }

    @Test
    public void shouldCheckErroneousPredicateResult4() {
        final Exception cause = new Exception("yay! (this is a negative test)");
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThatResult((o1, o2, o3, o4) -> { throw cause; }).check(0, 3);
        assertThat(result.isErroneous()).isTrue();
        assertThat(result.error().get()).hasCause(cause);
        assertThat(result.sample().isDefined()).isTrue();
        assertThat(result.message().isEmpty()).isTrue();
    }

    @Test
    public void shouldReportNullPredicateResultAsErroneous4() {
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThatResult((o1, o2, o3, o4) -> null).check(0, 3);
        assertThat(result.isErroneous()).isTrue();
        assertThat(result.error().get()).hasCauseInstanceOf(NullPointerException.class);
        assertThat(result.sample().isDefined()).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void shouldRejectNullResultPredicate4() {
        Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS).suchThatResult(null);
    }

    @Test
    public void shouldReportPostconditionFailureMessage4() {
        final CheckResult result = Property.def("test")
                .forAll(Gen.of(1).arbitrary(), Gen.of(2).arbitrary(), Gen.of(3).arbitrary(), Gen.of(4).arbitrary())
                .suchThat((o1, o2, o3, o4) -> true)
                .impliesResult((o1, o2, o3, o4) -> PredicateResult.failure("postcondition: " + Tuple.of(o1, o2, o3, o4)))
                .check(0, 3);
        assertThat(result.isFalsified()).isTrue();
        assertThat(result.sample().get()).isEqualTo(Tuple.of(1, 2, 3, 4));
        assertThat(result.message().get()).isEqualTo("postcondition: (1, 2, 3, 4)");
    }

    @Test
    public void shouldCheckSuccessfulResultImplication4() {
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThatResult((o1, o2, o3, o4) -> PredicateResult.success())
                .impliesResult((o1, o2, o3, o4) -> PredicateResult.success()).check(0, 3);
        assertThat(result.isSatisfied()).isTrue();
        assertThat(result.isExhausted()).isFalse();
        assertThat(result.message().isEmpty()).isTrue();
    }

    @Test
    public void shouldSkipResultPostconditionForFalseBooleanPrecondition4() {
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThat((o1, o2, o3, o4) -> false)
                .impliesResult((o1, o2, o3, o4) -> { throw new AssertionError("must not run"); }).check(0, 3);
        assertThat(result.isSatisfied()).isTrue();
        assertThat(result.isExhausted()).isTrue();
        assertThat(result.message().isEmpty()).isTrue();
    }

    @Test
    public void shouldDiscardRejectedPreconditionMessage4() {
        final Property.Property4<Object, Object, Object, Object> property = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThatResult((o1, o2, o3, o4) -> PredicateResult.failure("rejected input"));
        final CheckResult booleanResult = property
                .implies((o1, o2, o3, o4) -> { throw new AssertionError("must not run"); }).check(0, 3);
        final CheckResult detailedResult = property
                .impliesResult((o1, o2, o3, o4) -> { throw new AssertionError("must not run"); }).check(0, 3);
        for (CheckResult result : new CheckResult[] { booleanResult, detailedResult }) {
            assertThat(result.isSatisfied()).isTrue();
            assertThat(result.isExhausted()).isTrue();
            assertThat(result.count()).isEqualTo(3);
            assertThat(result.message().isEmpty()).isTrue();
        }
    }

    @Test
    public void shouldAllowBooleanPostconditionAfterPredicateResult4() {
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThatResult((o1, o2, o3, o4) -> PredicateResult.success())
                .implies((o1, o2, o3, o4) -> false).check(0, 3);
        assertThat(result.isFalsified()).isTrue();
        assertThat(result.message().isEmpty()).isTrue();
    }

    @Test
    public void shouldReportNullPostconditionResultAsErroneous4() {
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThat((o1, o2, o3, o4) -> true).impliesResult((o1, o2, o3, o4) -> null).check(0, 3);
        assertThat(result.isErroneous()).isTrue();
        assertThat(result.error().get()).hasCauseInstanceOf(NullPointerException.class);
        assertThat(result.sample().isDefined()).isTrue();
    }

    @Test
    public void shouldCheckErroneousPostconditionResult4() {
        final Exception cause = new Exception("yay! (this is a negative test)");
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThat((o1, o2, o3, o4) -> true).impliesResult((o1, o2, o3, o4) -> { throw cause; }).check(0, 3);
        assertThat(result.isErroneous()).isTrue();
        assertThat(result.error().get()).hasCause(cause);
        assertThat(result.sample().isDefined()).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void shouldRejectNullResultPostcondition4() {
        Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS).suchThat((o1, o2, o3, o4) -> true).impliesResult(null);
    }

    @Test
    public void shouldCheckErroneousProperty4() {
        final Property.ForAll4<Object, Object, Object, Object> forAll = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS);
        final CheckedFunction4<Object, Object, Object, Object, Boolean> predicate = (o1, o2, o3, o4) -> { throw new RuntimeException("yay! (this is a negative test)"); };
        final CheckResult result = forAll.suchThat(predicate).check();
        assertThat(result.isErroneous()).isTrue();
    }

    @Test
    public void shouldCheckProperty4ImplicationWithTruePrecondition() {
        final Property.ForAll4<Object, Object, Object, Object> forAll = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS);
        final CheckedFunction4<Object, Object, Object, Object, Boolean> p1 = (o1, o2, o3, o4) -> true;
        final CheckedFunction4<Object, Object, Object, Object, Boolean> p2 = (o1, o2, o3, o4) -> true;
        final CheckResult result = forAll.suchThat(p1).implies(p2).check();
        assertThat(result.isSatisfied()).isTrue();
        assertThat(result.isExhausted()).isFalse();
    }

    @Test
    public void shouldCheckProperty4ImplicationWithFalsePrecondition() {
        final Property.ForAll4<Object, Object, Object, Object> forAll = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS);
        final CheckedFunction4<Object, Object, Object, Object, Boolean> p1 = (o1, o2, o3, o4) -> false;
        final CheckedFunction4<Object, Object, Object, Object, Boolean> p2 = (o1, o2, o3, o4) -> true;
        final CheckResult result = forAll.suchThat(p1).implies(p2).check();
        assertThat(result.isSatisfied()).isTrue();
        assertThat(result.isExhausted()).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnProperty4CheckGivenNegativeTries() {
        Property.def("test")
            .forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS)
            .suchThat((o1, o2, o3, o4) -> true)
            .check(Checkable.RNG.get(), 0, -1);
    }

    @Test
    public void shouldReturnErroneousProperty4CheckResultIfGenFails() {
        final Arbitrary<Object> failingGen = Gen.fail("yay! (this is a negative test)").arbitrary();
        final CheckResult result = Property.def("test")
            .forAll(failingGen, OBJECTS, OBJECTS, OBJECTS)
            .suchThat((o1, o2, o3, o4) -> true)
            .check();
        assertThat(result.isErroneous()).isTrue();
    }

    @Test
    public void shouldReturnErroneousProperty4CheckResultIfArbitraryFails() {
        final Arbitrary<Object> failingArbitrary = size -> { throw new RuntimeException("yay! (this is a negative test)"); };
        final CheckResult result = Property.def("test")
            .forAll(failingArbitrary, OBJECTS, OBJECTS, OBJECTS)
            .suchThat((o1, o2, o3, o4) -> true)
            .check();
        assertThat(result.isErroneous()).isTrue();
    }
}