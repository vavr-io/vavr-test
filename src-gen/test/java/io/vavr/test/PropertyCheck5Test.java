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

import io.vavr.CheckedFunction5;
import io.vavr.Tuple;
import org.junit.Test;

public class PropertyCheck5Test {

    static final Arbitrary<Object> OBJECTS = Gen.of(null).arbitrary();

    @Test
    public void shouldApplyForAllOfArity5() {
        final Property.ForAll5<Object, Object, Object, Object, Object> forAll = Property.def("test").forAll(null, null, null, null, null);
        assertThat(forAll).isNotNull();
    }

    @Test
    public void shouldApplySuchThatOfArity5() {
        final Property.ForAll5<Object, Object, Object, Object, Object> forAll = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS);
        final CheckedFunction5<Object, Object, Object, Object, Object, Boolean> predicate = (o1, o2, o3, o4, o5) -> true;
        final Property.Property5<Object, Object, Object, Object, Object> suchThat = forAll.suchThat(predicate);
        assertThat(suchThat).isNotNull();
    }

    @Test
    public void shouldCheckTrueProperty5() {
        final Property.ForAll5<Object, Object, Object, Object, Object> forAll = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS);
        final CheckedFunction5<Object, Object, Object, Object, Object, Boolean> predicate = (o1, o2, o3, o4, o5) -> true;
        final CheckResult result = forAll.suchThat(predicate).check();
        assertThat(result.isSatisfied()).isTrue();
        assertThat(result.isExhausted()).isFalse();
    }

    @Test
    public void shouldCheckFalseProperty5() {
        final Property.ForAll5<Object, Object, Object, Object, Object> forAll = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS);
        final CheckedFunction5<Object, Object, Object, Object, Object, Boolean> predicate = (o1, o2, o3, o4, o5) -> false;
        final CheckResult result = forAll.suchThat(predicate).check();
        assertThat(result.isFalsified()).isTrue();
        assertThat(result.message().isEmpty()).isTrue();
    }

    @Test
    public void shouldCheckSuccessfulPredicateResult5() {
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThatResult((o1, o2, o3, o4, o5) -> PredicateResult.success()).check(0, 3);
        assertThat(result.isSatisfied()).isTrue();
        assertThat(result.isExhausted()).isFalse();
        assertThat(result.count()).isEqualTo(3);
        assertThat(result.message().isEmpty()).isTrue();
    }

    @Test
    public void shouldReportPredicateFailureMessage5() {
        final CheckResult result = Property.def("test")
                .forAll(Gen.of(1).arbitrary(), Gen.of(2).arbitrary(), Gen.of(3).arbitrary(), Gen.of(4).arbitrary(), Gen.of(5).arbitrary())
                .suchThatResult((o1, o2, o3, o4, o5) -> PredicateResult.failure("failed: " + Tuple.of(o1, o2, o3, o4, o5)))
                .check(0, 3);
        assertThat(result.isFalsified()).isTrue();
        assertThat(result.isErroneous()).isFalse();
        assertThat(result.count()).isEqualTo(1);
        assertThat(result.sample().get()).isEqualTo(Tuple.of(1, 2, 3, 4, 5));
        assertThat(result.message().get()).isEqualTo("failed: (1, 2, 3, 4, 5)");
        assertThat(result.error().isEmpty()).isTrue();
        assertThatThrownBy(result::assertIsSatisfied)
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("failed: (1, 2, 3, 4, 5)");
    }

    @Test
    public void shouldCheckErroneousPredicateResult5() {
        final Exception cause = new Exception("yay! (this is a negative test)");
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThatResult((o1, o2, o3, o4, o5) -> { throw cause; }).check(0, 3);
        assertThat(result.isErroneous()).isTrue();
        assertThat(result.error().get()).hasCause(cause);
        assertThat(result.sample().isDefined()).isTrue();
        assertThat(result.message().isEmpty()).isTrue();
    }

    @Test
    public void shouldReportNullPredicateResultAsErroneous5() {
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThatResult((o1, o2, o3, o4, o5) -> null).check(0, 3);
        assertThat(result.isErroneous()).isTrue();
        assertThat(result.error().get()).hasCauseInstanceOf(NullPointerException.class);
        assertThat(result.sample().isDefined()).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void shouldRejectNullResultPredicate5() {
        Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS).suchThatResult(null);
    }

    @Test
    public void shouldReportPostconditionFailureMessage5() {
        final CheckResult result = Property.def("test")
                .forAll(Gen.of(1).arbitrary(), Gen.of(2).arbitrary(), Gen.of(3).arbitrary(), Gen.of(4).arbitrary(), Gen.of(5).arbitrary())
                .suchThat((o1, o2, o3, o4, o5) -> true)
                .impliesResult((o1, o2, o3, o4, o5) -> PredicateResult.failure("postcondition: " + Tuple.of(o1, o2, o3, o4, o5)))
                .check(0, 3);
        assertThat(result.isFalsified()).isTrue();
        assertThat(result.sample().get()).isEqualTo(Tuple.of(1, 2, 3, 4, 5));
        assertThat(result.message().get()).isEqualTo("postcondition: (1, 2, 3, 4, 5)");
    }

    @Test
    public void shouldCheckSuccessfulResultImplication5() {
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThatResult((o1, o2, o3, o4, o5) -> PredicateResult.success())
                .impliesResult((o1, o2, o3, o4, o5) -> PredicateResult.success()).check(0, 3);
        assertThat(result.isSatisfied()).isTrue();
        assertThat(result.isExhausted()).isFalse();
        assertThat(result.message().isEmpty()).isTrue();
    }

    @Test
    public void shouldSkipResultPostconditionForFalseBooleanPrecondition5() {
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThat((o1, o2, o3, o4, o5) -> false)
                .impliesResult((o1, o2, o3, o4, o5) -> { throw new AssertionError("must not run"); }).check(0, 3);
        assertThat(result.isSatisfied()).isTrue();
        assertThat(result.isExhausted()).isTrue();
        assertThat(result.message().isEmpty()).isTrue();
    }

    @Test
    public void shouldDiscardRejectedPreconditionMessage5() {
        final Property.Property5<Object, Object, Object, Object, Object> property = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThatResult((o1, o2, o3, o4, o5) -> PredicateResult.failure("rejected input"));
        final CheckResult booleanResult = property
                .implies((o1, o2, o3, o4, o5) -> { throw new AssertionError("must not run"); }).check(0, 3);
        final CheckResult detailedResult = property
                .impliesResult((o1, o2, o3, o4, o5) -> { throw new AssertionError("must not run"); }).check(0, 3);
        for (CheckResult result : new CheckResult[] { booleanResult, detailedResult }) {
            assertThat(result.isSatisfied()).isTrue();
            assertThat(result.isExhausted()).isTrue();
            assertThat(result.count()).isEqualTo(3);
            assertThat(result.message().isEmpty()).isTrue();
        }
    }

    @Test
    public void shouldAllowBooleanPostconditionAfterPredicateResult5() {
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThatResult((o1, o2, o3, o4, o5) -> PredicateResult.success())
                .implies((o1, o2, o3, o4, o5) -> false).check(0, 3);
        assertThat(result.isFalsified()).isTrue();
        assertThat(result.message().isEmpty()).isTrue();
    }

    @Test
    public void shouldReportNullPostconditionResultAsErroneous5() {
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThat((o1, o2, o3, o4, o5) -> true).impliesResult((o1, o2, o3, o4, o5) -> null).check(0, 3);
        assertThat(result.isErroneous()).isTrue();
        assertThat(result.error().get()).hasCauseInstanceOf(NullPointerException.class);
        assertThat(result.sample().isDefined()).isTrue();
    }

    @Test
    public void shouldCheckErroneousPostconditionResult5() {
        final Exception cause = new Exception("yay! (this is a negative test)");
        final CheckResult result = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS)
                .suchThat((o1, o2, o3, o4, o5) -> true).impliesResult((o1, o2, o3, o4, o5) -> { throw cause; }).check(0, 3);
        assertThat(result.isErroneous()).isTrue();
        assertThat(result.error().get()).hasCause(cause);
        assertThat(result.sample().isDefined()).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void shouldRejectNullResultPostcondition5() {
        Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS).suchThat((o1, o2, o3, o4, o5) -> true).impliesResult(null);
    }

    @Test
    public void shouldCheckErroneousProperty5() {
        final Property.ForAll5<Object, Object, Object, Object, Object> forAll = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS);
        final CheckedFunction5<Object, Object, Object, Object, Object, Boolean> predicate = (o1, o2, o3, o4, o5) -> { throw new RuntimeException("yay! (this is a negative test)"); };
        final CheckResult result = forAll.suchThat(predicate).check();
        assertThat(result.isErroneous()).isTrue();
    }

    @Test
    public void shouldCheckProperty5ImplicationWithTruePrecondition() {
        final Property.ForAll5<Object, Object, Object, Object, Object> forAll = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS);
        final CheckedFunction5<Object, Object, Object, Object, Object, Boolean> p1 = (o1, o2, o3, o4, o5) -> true;
        final CheckedFunction5<Object, Object, Object, Object, Object, Boolean> p2 = (o1, o2, o3, o4, o5) -> true;
        final CheckResult result = forAll.suchThat(p1).implies(p2).check();
        assertThat(result.isSatisfied()).isTrue();
        assertThat(result.isExhausted()).isFalse();
    }

    @Test
    public void shouldCheckProperty5ImplicationWithFalsePrecondition() {
        final Property.ForAll5<Object, Object, Object, Object, Object> forAll = Property.def("test").forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS);
        final CheckedFunction5<Object, Object, Object, Object, Object, Boolean> p1 = (o1, o2, o3, o4, o5) -> false;
        final CheckedFunction5<Object, Object, Object, Object, Object, Boolean> p2 = (o1, o2, o3, o4, o5) -> true;
        final CheckResult result = forAll.suchThat(p1).implies(p2).check();
        assertThat(result.isSatisfied()).isTrue();
        assertThat(result.isExhausted()).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnProperty5CheckGivenNegativeTries() {
        Property.def("test")
            .forAll(OBJECTS, OBJECTS, OBJECTS, OBJECTS, OBJECTS)
            .suchThat((o1, o2, o3, o4, o5) -> true)
            .check(Checkable.RNG.get(), 0, -1);
    }

    @Test
    public void shouldReturnErroneousProperty5CheckResultIfGenFails() {
        final Arbitrary<Object> failingGen = Gen.fail("yay! (this is a negative test)").arbitrary();
        final CheckResult result = Property.def("test")
            .forAll(failingGen, OBJECTS, OBJECTS, OBJECTS, OBJECTS)
            .suchThat((o1, o2, o3, o4, o5) -> true)
            .check();
        assertThat(result.isErroneous()).isTrue();
    }

    @Test
    public void shouldReturnErroneousProperty5CheckResultIfArbitraryFails() {
        final Arbitrary<Object> failingArbitrary = size -> { throw new RuntimeException("yay! (this is a negative test)"); };
        final CheckResult result = Property.def("test")
            .forAll(failingArbitrary, OBJECTS, OBJECTS, OBJECTS, OBJECTS)
            .suchThat((o1, o2, o3, o4, o5) -> true)
            .check();
        assertThat(result.isErroneous()).isTrue();
    }
}