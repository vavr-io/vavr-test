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

import io.vavr.Tuple2;
import io.vavr.collection.List;

interface GenModule {

    static <T> Gen<T> withEdges(Gen<T> gen, List<T> edgeCases) {
        final Gen<T> edges = Gen.choose(edgeCases);
        return random -> (random.nextBoolean() ? edges : gen).apply(random);
    }

    static Gen<Integer> chooseInt(int min, int max) {
        if (min == max) {
            return Gen.of(min);
        }
        final long range = (long) max - min + 1;
        if (range <= Integer.MAX_VALUE) {
            return random -> random.nextInt((int) range) + min;
        }
        // A wider range contains at least half of all ints, so rejection is inexpensive.
        return random -> {
            int value;
            do {
                value = random.nextInt();
            } while (value < min || value > max);
            return value;
        };
    }

    static Gen<Long> chooseLong(long min, long max) {
        if (min == max) {
            return Gen.of(min);
        }
        final long range = max - min + 1;
        return random -> {
            if (range > 0) {
                long bits;
                long value;
                do {
                    bits = random.nextLong() >>> 1;
                    value = bits % range;
                    // Reject the incomplete final bucket to avoid modulo bias.
                } while (bits - value + (range - 1) < 0);
                return min + value;
            }
            // An overflowing range contains at least half of all longs.
            long value;
            do {
                value = random.nextLong();
            } while (value < min || value > max);
            return value;
        };
    }

    /**
     * Chooses a Gen according to the given frequencies.
     *
     * @param n    a random value between 1 and sum(frequencies)
     * @param iter a non-empty Iterator of (frequency, Gen) pairs
     * @param <T>  type of generated values
     * @return A value generator, chosen according to the given frequencies and the underlying n
     */
    static <T> Gen<T> frequency(int n, java.util.Iterator<Tuple2<Integer, Gen<T>>> iter) {
        do {
            final Tuple2<Integer, Gen<T>> freqGen = iter.next();
            final int k = freqGen._1;
            if (n <= k) {
                return freqGen._2;
            } else {
                n = n - k;
            }
        } while (true);
    }

}
