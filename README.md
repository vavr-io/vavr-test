[![build](https://github.com/vavr-io/vavr-test/actions/workflows/build.yml/badge.svg)](https://github.com/vavr-io/vavr-test/actions/workflows/build.yml)
[![Maven Central Version](https://img.shields.io/maven-central/v/io.vavr/vavr-test)](https://central.sonatype.com/artifact/io.vavr/vavr-test)

# [Vavr](https://vavr.io/) Property Testing

Vavr Test is a property testing library for Java 8+. Describe a rule that should hold for your code, supply generators for its inputs, and check that rule against randomly generated examples. A failing check reports a counterexample or an error that you can inspect.

## Installation

Add Vavr Test to your test dependencies. Vavr itself is included as a transitive dependency.

### Maven

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr-test</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

### Gradle

```groovy
repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'io.vavr:vavr-test:1.0.0'
}
```

## Quick start

This example checks that reversing a list twice returns the original list. It uses JUnit 4, which you should add separately if your project does not already use it.

```java
import io.vavr.collection.List;
import io.vavr.test.Arbitrary;
import io.vavr.test.Property;
import org.junit.Test;

public class ListPropertiesTest {

    @Test
    public void reversingTwiceReturnsTheOriginalList() {
        Arbitrary<List<Integer>> lists = Arbitrary.list(Arbitrary.integer());

        Property.def("reversing a list twice returns the original list")
                .forAll(lists)
                .suchThat(list -> list.reverse().reverse().equals(list))
                .check()
                .assertIsSatisfied();
    }
}
```

`Property.def` names the property, `forAll` supplies its inputs, and `suchThat` defines the predicate to test. Properties can take one to eight generated inputs.

By default, `check()` makes up to 1,000 attempts with a generator size of 100, stopping at the first failure or error. It returns a `CheckResult`; call `assertIsSatisfied()` to make an unsuccessful check fail your test with an `AssertionError`. The same assertion works with other test frameworks or in a plain Java program.

## Generating inputs

`Gen<T>` produces a value using a supplied `java.util.Random`. `Arbitrary<T>` takes a size hint and returns a `Gen<T>`. Convert a generator with `gen.arbitrary()` when it does not need to use the size hint.

| Expression | Generated values |
| --- | --- |
| `Arbitrary.integer()` | Integers from `-size` to `size`, inclusive |
| `Gen.choose(1, 100).arbitrary()` | Integers from 1 to 100, inclusive, independent of size |
| `Arbitrary.of("red", "green", "blue")` | Values chosen from a fixed set |
| `Gen.of(42).arbitrary()` | The constant value 42 |
| `Arbitrary.string(Gen.choose('a', 'z'))` | Lowercase strings with lengths from 0 to `size` |
| `Arbitrary.list(Arbitrary.integer())` | Vavr lists with lengths from 0 to `size` |
| `Arbitrary.stream(Arbitrary.integer())` | Vavr streams with lengths from 0 to `size` |

Both `Gen` and `Arbitrary` support `map`, `flatMap`, and `filter`. Use `map` to transform values and `flatMap` to generate values that depend on earlier choices. For example, this generator produces integer ranges whose upper bound is at least their lower bound:

```java
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.test.Arbitrary;
import io.vavr.test.Gen;

Arbitrary<Tuple2<Integer, Integer>> ranges = Gen.choose(0, 100)
        .flatMap(lower -> Gen.choose(lower, 100)
                .map(upper -> Tuple.of(lower, upper)))
        .arbitrary();
```

`filter` repeatedly draws values until its predicate succeeds. Prefer generating valid values directly when a filter would reject most inputs. `Gen.oneOf` chooses between generators, and `Gen.frequency` lets you weight those choices.

## Configuring checks

Use `check(size, tries)` to set the size hint and attempt count, or `check(random, size, tries)` to also supply the random number generator:

```java
import io.vavr.test.Arbitrary;
import io.vavr.test.Checkable;
import io.vavr.test.Property;
import java.util.Random;

Arbitrary<Integer> integers = Arbitrary.integer();

Checkable addition = Property.def("integer addition is commutative")
        .forAll(integers, integers)
        .suchThat((a, b) -> a + b == b + a);

addition.check(new Random(42L), 500, 10_000)
        .assertIsSatisfied();
```

The size hint stays constant throughout a check. Each arbitrary interprets it independently: in this example, integers range from -500 to 500. A fixed seed lets you reproduce inputs when the generators use the supplied random source and otherwise behave deterministically. Use a fresh `Random` with the same seed for each replay.

You can also combine checkable properties with `and` and `or`. These evaluate the first property and check the second only when needed to determine the combined result.

## Preconditions

Use `suchThat(precondition).implies(postcondition)` for a property that applies only to some inputs. The postcondition runs only when the precondition holds:

```java
import io.vavr.test.Arbitrary;
import io.vavr.test.Property;

Property.def("a nonzero integer divided by itself is one")
        .forAll(Arbitrary.integer())
        .suchThat(n -> n != 0)
        .implies(n -> n / n == 1)
        .check()
        .assertIsSatisfiedWithExhaustion(false);
```

An input that does not satisfy the precondition still uses an attempt. If no input satisfies it, the result is both satisfied and exhausted. `assertIsSatisfiedWithExhaustion(false)` checks that the property passed and that at least one input exercised the postcondition. A check with zero attempts is also exhausted.

## Inspecting results

Every check returns a `CheckResult`:

| Method | Meaning |
| --- | --- |
| `isSatisfied()` | No tested input falsified the property or caused an error |
| `isFalsified()` | An input made the predicate return `false` |
| `isErroneous()` | An error occurred while generating inputs or evaluating the property |
| `isExhausted()` | The check was satisfied without exercising any applicable input |
| `propertyName()` | The name passed to `Property.def` |
| `count()` | The number of attempts performed, including inputs rejected by a precondition |
| `sample()` | An optional Vavr tuple containing the failing inputs, when available |
| `error()` | An optional error with details of the failure |

Use `assertIsSatisfied()`, `assertIsSatisfiedWithExhaustion(false)`, `assertIsFalsified()`, or `assertIsErroneous()` to assert the expected outcome. For the full API, see the [Javadoc](https://javadoc.io/doc/io.vavr/vavr-test).

## Building and contributing

Use a JDK 8 or newer and Maven 3.9 or newer. Run the tests with:

```shell
mvn test
```

Build the JAR, sources, and Javadoc with:

```shell
mvn package
```

The build regenerates `src-gen/` from [generator/Generator.scala](generator/Generator.scala). Make changes to generated properties and their tests in the generator, since edits under `src-gen/` are overwritten during the build. Handwritten sources and tests live under `src/main/java` and `src/test/java`.

Bug reports and pull requests are welcome on [GitHub](https://github.com/vavr-io/vavr-test/issues). Include a failing property, its input generators, and the seed and check settings when reporting a reproducible failure.

## License

Vavr Test is licensed under the [Apache License 2.0](LICENSE).
