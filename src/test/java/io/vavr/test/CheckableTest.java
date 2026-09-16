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

import org.junit.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckableTest {

    // -- check()

    @Test
    public void shouldDelegateNoArgCheckThroughSizeAndTriesOverload() {
        final int[] capturedSize = { -1 };
        final int[] capturedTries = { -1 };
        final Checkable checkable = new Checkable() {
            @Override
            public CheckResult check(Random randomNumberGenerator, int size, int tries) {
                return new CheckResult.Satisfied("shouldDelegate", 0, false);
            }

            @Override
            public CheckResult check(int size, int tries) {
                capturedSize[0] = size;
                capturedTries[0] = tries;
                return check(RNG.get(), size, tries);
            }
        };

        final CheckResult result = checkable.check();

        assertThat(capturedSize[0]).isEqualTo(Checkable.DEFAULT_SIZE);
        assertThat(capturedTries[0]).isEqualTo(Checkable.DEFAULT_TRIES);
        assertThat(result.isSatisfied()).isTrue();
    }
}
