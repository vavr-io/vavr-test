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

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class ArbitraryTest {

    // equally distributed random number generator
    private static final Random RANDOM = new Random();

    // -- apply

    @Test
    public void shouldApplyIntegerObject() {
        final Gen<BinaryTree<Integer>> gen = new ArbitraryBinaryTree(0, 0).apply(0);
        assertThat(gen).isNotNull();
    }

    // -- flatMap

    @Test
    public void shouldFlatMapArbitrary() {
        final Arbitrary<Integer> arbitraryInt = size -> Gen.choose(-size, size);
        final Arbitrary<BinaryTree<Integer>> arbitraryTree = arbitraryInt.flatMap(i -> new ArbitraryBinaryTree(-i, i));
        assertThat(arbitraryTree.apply(0).apply(RANDOM)).isNotNull();
    }

    // -- map

    @Test
    public void shouldMapArbitrary() {
        final Arbitrary<Integer> arbitraryInt = size -> Gen.choose(-size, size);
        final Arbitrary<BinaryTree<Integer>> arbitraryTree = arbitraryInt.map(BinaryTree::leaf);
        assertThat(arbitraryTree.apply(0).apply(RANDOM)).isNotNull();
    }

    // -- filter

    @Test
    public void shouldFilterArbitrary() {
        final Arbitrary<Integer> ints = Arbitrary.integer();
        final Arbitrary<Integer> evenInts = ints.filter(i -> i % 2 == 0);
        assertThat(evenInts.apply(10).apply(RANDOM)).isNotNull();
    }

    // -- peek

    @Test
    public void shouldPeekArbitrary() {
        final int[] actual = new int[] { Integer.MIN_VALUE };
        final int expected = Arbitrary.integer().peek(i -> actual[0] = i).apply(10).apply(RANDOM);
        assertThat(actual[0]).isEqualTo(expected);
    }

    // factory methods

    @Test
    public void shouldCreateArbitraryInteger() {
        final Arbitrary<Integer> arbitrary = Arbitrary.integer();
        final Integer actual = arbitrary.apply(10).apply(RANDOM);
        assertThat(actual).isNotNull();
    }

    @Test
    public void shouldFavorIntegerBoundariesAndValuesAroundZero() {
        final List<Integer> values = samples(Arbitrary.integer().apply(10000));
        final List<Integer> edges = List.of(-10000, -9999, -1, 0, 1, 9999, 10000);
        assertThat(values).containsAll(edges).allMatch(i -> i >= -10000 && i <= 10000);
        assertThat(values.count(edges::contains)).isBetween(400, 600);
        assertThat(samples(Arbitrary.integer().apply(-10000))).isEqualTo(values);
    }

    @Test
    public void shouldGenerateIntegersForZeroAndExtremeSizes() {
        assertThat(samples(Arbitrary.integer().apply(0))).containsOnly(0);
        assertThat(samples(Arbitrary.integer().apply(Integer.MAX_VALUE)))
                .contains(-Integer.MAX_VALUE, -1, 0, 1, Integer.MAX_VALUE)
                .doesNotContain(Integer.MIN_VALUE);
        assertThat(samples(Arbitrary.integer().apply(Integer.MIN_VALUE)))
                .contains(Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE);
    }

    @Test
    public void shouldFavorEmptySingletonAndMaximumLengths() {
        assertLengthEdges(Arbitrary.string(Gen.of('a')).apply(100).map(String::length));
        assertLengthEdges(Arbitrary.list(Arbitrary.of(1)).apply(100).map(List::length));
        assertLengthEdges(Arbitrary.stream(Arbitrary.of(1)).apply(100).map(Stream::length));
    }

    @Test
    public void shouldKeepSmallCollectionLengthsWithinTheSizeHint() {
        for (int size = 1; size <= 2; size++) {
            final int max = size;
            assertThat(samples(Arbitrary.string(Gen.of('a')).apply(size).map(String::length)))
                    .contains(0, size).allMatch(length -> length >= 0 && length <= max);
            assertThat(samples(Arbitrary.list(Arbitrary.of(1)).apply(size).map(List::length)))
                    .contains(0, size).allMatch(length -> length >= 0 && length <= max);
            assertThat(samples(Arbitrary.stream(Arbitrary.of(1)).apply(size).map(Stream::length)))
                    .contains(0, size).allMatch(length -> length >= 0 && length <= max);
        }
    }

    @Test
    public void shouldGenerateEmptyCollectionsWithoutDrawingElementsForNonpositiveSizes() {
        for (int size : new int[] { 0, -1, Integer.MIN_VALUE }) {
            assertThat(Arbitrary.string(Gen.fail()).apply(size).apply(RANDOM)).isEmpty();
            assertThat(Arbitrary.list(Gen.fail().arbitrary()).apply(size).apply(RANDOM)).isEmpty();
            assertThat(Arbitrary.stream(Gen.fail().arbitrary()).apply(size).apply(RANDOM)).isEmpty();
        }
    }

    @Test
    public void shouldCreateArbitraryString() {
        final Arbitrary<String> arbitrary = Arbitrary.string(Gen.choose('a', 'z'));
        final String actual = arbitrary.apply(10).apply(RANDOM);
        assertThat(actual).isNotNull();
    }

    @Test
    public void shouldCreateArbitraryList() {
        final Arbitrary<List<Integer>> arbitrary = Arbitrary.list(Arbitrary.integer());
        final List<Integer> actual = arbitrary.apply(10).apply(RANDOM);
        assertThat(actual).isNotNull();
    }

    @Test
    public void shouldCreateArbitraryStream() {
        final Arbitrary<Stream<Integer>> arbitrary = Arbitrary.stream(Arbitrary.integer());
        final Stream<Integer> actual = arbitrary.apply(10).apply(RANDOM);
        assertThat(actual).isNotNull();
    }

    @Test
    public void shouldCreateFixedContentArbitrary() {
        final Gen<String> arbitrary = Arbitrary.of("test", "content").apply(10);
        for (int i = 0; i < 100; i++) {
            assertThat(arbitrary.apply(RANDOM)).isIn("test", "content");
        }
    }

    @Test
    public void shouldCreateNonDistinctArbitrary() {
        final Gen<String> arbitrary = Arbitrary.string(Gen.choose('a', 'b')).apply(2);
        List.range(0, 1000)
                .map(i -> arbitrary.apply(RANDOM))
                .groupBy(Function1.identity())
                .forEach((key, value) -> assertThat(value.length())
                        .describedAs(key)
                        .isGreaterThan(1));
    }

    @Test
    public void shouldCreateDistinctArbitrary() {
        final Gen<String> distinctArbitrary = Arbitrary.string(Gen.choose('a', 'b')).distinct().apply(100);
        List.range(0, 1000)
                .map(i -> distinctArbitrary.apply(RANDOM))
                .groupBy(Function1.identity())
                .forEach((key, value) -> assertThat(value.length())
                        .describedAs(key)
                        .isEqualTo(1));
    }

    @Test
    public void shouldCreateDistinctByArbitrary() {
        final Gen<String> distinctByArbitrary = Arbitrary.string(Gen.choose('a', 'b'))
                .distinctBy(Comparator.naturalOrder()).apply(100);
        List.range(0, 10000)
                .map(i -> distinctByArbitrary.apply(RANDOM))
                .groupBy(Function1.identity())
                .forEach((key, value) -> assertThat(value.length())
                        .describedAs(key)
                        .isEqualTo(1));
    }

    @Test
    public void shouldCreateInterspersedFixedContentArbitrary() {
        final Gen<String> arbitrary = Arbitrary.of("test")
                .intersperse(Arbitrary.of("content"))
                .apply(10);
        for (int i = 0; i < 100; i++) {
            assertThat(arbitrary.apply(RANDOM)).isIn("test", "content");
        }
    }

    @Test
    public void shouldCreateInterspersedFixedContentArbitraryWithConstantOrder() {
        final Gen<String> arbitrary = Arbitrary.of("test")
                .intersperse(Arbitrary.of("content"))
                .apply(10);
        final Iterator<Stream<String>> generatedStringPairs = Stream.range(0, 10)
                .map(i -> arbitrary.apply(RANDOM))
                .grouped(2);
        for (Stream<String> stringPairs : generatedStringPairs) {
            assertThat(stringPairs.mkString(",")).isEqualTo("test,content");
        }

    }

    @Test
    public void shouldCreateCharArrayArbitrary() {
        final Gen<String> arbitrary = Arbitrary.string(Gen.choose("test".toCharArray()))
                .filter(s -> !"".equals(s))
                .apply(1);
        for (int i = 0; i < 100; i++) {
            assertThat(arbitrary.apply(RANDOM)).isIn("t", "e", "s");
        }
    }

    @Test
    public void shouldCreateArbitraryStreamAndEvaluateAllElements() {
        final Arbitrary<Stream<Integer>> arbitrary = Arbitrary.stream(Arbitrary.integer());
        final Stream<Integer> actual = arbitrary.apply(10).apply(new Random() {
            private static final long serialVersionUID = 1L;
            @Override
            public int nextInt(int bound) {
                return bound - 1;
            }
        });
        assertThat(actual.length()).isEqualTo(10);
    }

    @Test
    public void shouldCreateArbitraryLocalDateTime(){
        final Arbitrary<LocalDateTime> date = Arbitrary.localDateTime();

        assertThat(date).isNotNull();
    }


    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullMedianLocalDateTime(){
        Arbitrary.localDateTime(null, ChronoUnit.DAYS);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullChronoUnit(){
        Arbitrary.localDateTime(LocalDateTime.now(), null);
    }

    @Test
    public void shouldCreateArbitraryLocalDateTimeAdjustedWithGivenChronoUnit(){
        final LocalDateTime median = LocalDateTime.of(2017, 2, 17, 3, 40);
        final Arbitrary<LocalDateTime> arbitrary = Arbitrary.localDateTime(median, ChronoUnit.YEARS);

        final List<LocalDateTime> dates = samples(arbitrary.apply(100));
        final List<LocalDateTime> edges = List.of(median.minusYears(100), median, median.plusYears(100));

        assertThat(dates).containsAll(edges)
                .allMatch(date -> !date.isBefore(median.minusYears(100)) && !date.isAfter(median.plusYears(100)));
        assertThat(dates.count(edges::contains)).isBetween(400, 600);
        assertThat(samples(arbitrary.apply(-100))).isEqualTo(dates);
    }

    @Test
    public void shouldIncludeExactDateBoundariesForSubMillisecondRanges() {
        final LocalDateTime median = LocalDateTime.of(2020, 2, 29, 12, 0, 0, 123456789);
        final List<LocalDateTime> dates = samples(Arbitrary.localDateTime(median, ChronoUnit.NANOS).apply(1));
        assertThat(dates).containsOnly(median.minusNanos(1), median, median.plusNanos(1))
                .contains(median.minusNanos(1), median, median.plusNanos(1));
    }

    @Test
    public void shouldCreateMedianLocalDateTimeIfSizeIsZero(){
        final LocalDateTime median = LocalDateTime.now();

        final Arbitrary<LocalDateTime> arbitrary = Arbitrary.localDateTime(median, ChronoUnit.DAYS);

        final LocalDateTime date = arbitrary.apply(0).apply(RANDOM);

        assertThat(date).isEqualTo(median);
    }

    @Test
    public void shouldCreateDatesInInRangeOfSize(){
        final LocalDateTime median = LocalDateTime.now();
        final Arbitrary<LocalDateTime> arbitrary = Arbitrary.localDateTime(median, ChronoUnit.DAYS);

        Property.def("With size of 100 days, dates should be in range of +/- 100 days")
                .forAll(arbitrary)
                .suchThat(d -> !d.isBefore(median.minusDays(100)) && !d.isAfter(median.plusDays(100)))
                .check(new Random(0L), 100, 1000).assertIsSatisfied();
    }

    @Test
    public void shouldIgnoreNegativeSignInRangeOfDates(){
        final LocalDateTime median = LocalDateTime.now();
        final Arbitrary<LocalDateTime> arbitrary = Arbitrary.localDateTime(median, ChronoUnit.DAYS);

        Property.def("With negative size of -100 days, dates should be in range of +/- 100 days")
                .forAll(arbitrary)
                .suchThat(d -> !d.isBefore(median.minusDays(100)) && !d.isAfter(median.plusDays(100)))
                .check(new Random(0L), -100, 1000).assertIsSatisfied();
    }

    @Test
    public void shouldGenerateVariedDatesAndReplayWithTheSameSeed(){
        final Arbitrary<LocalDateTime> dates = Arbitrary.localDateTime();
        final Gen<LocalDateTime> gen = dates.apply(100);
        final List<LocalDateTime> values = samples(gen);

        assertThat(values.distinct().size()).isGreaterThan(100);
        assertThat(samples(gen)).isEqualTo(values);
    }

    // -- transform

    @Test
    public void shouldTransformArbitrary() {
        final Arbitrary<Integer> arbitrary = ignored -> Gen.of(1);
        final String s = arbitrary.transform(a -> a.apply(0).apply(RANDOM).toString());
        assertThat(s).isEqualTo("1");
    }

    // helpers

    private static <T> List<T> samples(Gen<T> gen) {
        final Random random = new Random(0L);
        return List.fill(1000, () -> gen.apply(random));
    }

    private static void assertLengthEdges(Gen<Integer> lengths) {
        final List<Integer> values = samples(lengths);
        final List<Integer> edges = List.of(0, 1, 99, 100);
        assertThat(values).containsAll(edges).allMatch(length -> length >= 0 && length <= 100);
        assertThat(values.count(edges::contains)).isBetween(400, 650);
    }

    /**
     * Represents arbitrary binary trees of a certain depth n with values of type int.
     */
    static class ArbitraryBinaryTree implements Arbitrary<BinaryTree<Integer>> {

        final int minValue;
        final int maxValue;

        ArbitraryBinaryTree(int minValue, int maxValue) {
            this.minValue = Math.min(minValue, maxValue);
            this.maxValue = Math.max(minValue, maxValue);
        }

        @Override
        public Gen<BinaryTree<Integer>> apply(int n) {
            return random -> Gen.choose(minValue, maxValue).flatMap(value -> {
                        if (n == 0) {
                            return Gen.of(BinaryTree.leaf(value));
                        } else {
                            return Gen.frequency(
                                    Tuple.of(1, Gen.of(BinaryTree.leaf(value))),
                                    Tuple.of(4, Gen.of(BinaryTree.branch(apply(n / 2).apply(random), value, apply(n / 2).apply(random))))
                            );
                        }
                    }
            ).apply(random);
        }
    }

    interface BinaryTree<T> {

        static <T> Branch<T> branch(BinaryTree<T> left, T value, BinaryTree<T> right) {
            return new Branch<>(left, value, right);
        }

        static <T> Branch<T> leaf(T value) {
            return new Branch<>(empty(), value, empty());
        }

        static <T> Empty<T> empty() {
            return Empty.instance();
        }

        class Branch<T> implements BinaryTree<T> {

            final BinaryTree<T> left;
            final T value;
            final BinaryTree<T> right;

            Branch(BinaryTree<T> left, T value, BinaryTree<T> right) {
                this.left = left;
                this.value = value;
                this.right = right;
            }
        }

        class Empty<T> implements BinaryTree<T> {

            private static final Empty<?> INSTANCE = new Empty<>();

            @SuppressWarnings("unchecked")
            static <T> Empty<T> instance() {
                return (Empty<T>) INSTANCE;
            }
        }
    }
}
