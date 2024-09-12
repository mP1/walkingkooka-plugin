/*
 * Copyright 2024 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.plugin;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Name;
import walkingkooka.plugin.FilteredProviderGuardTest.TestName;
import walkingkooka.plugin.FilteredProviderGuardTest.TestSelector;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.printer.IndentingPrinter;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class FilteredProviderGuardTest implements ClassTesting2<FilteredProviderGuard<TestName, TestSelector>> {

    private final static TestName NAME = new TestName("Hello123");

    private final static Function<TestName, RuntimeException> UNKNOWN = (n) -> new IllegalArgumentException(
            "Unknown TestName " + n
    );

    @Test
    public void testWithNullSetFails() {
        assertThrows(
                NullPointerException.class,
                () -> FilteredProviderGuard.with(
                        null,
                        UNKNOWN
                )
        );
    }

    @Test
    public void testWithNullUnknownFails() {
        assertThrows(
                NullPointerException.class,
                () -> FilteredProviderGuard.with(
                        Sets.of(NAME),
                        null
                )
        );
    }

    // name.............................................................................................................

    @Test
    public void testName() {
        this.guard()
                .name(NAME);
    }

    @Test
    public void testNameWithNullFails() {
        final NullPointerException thrown = assertThrows(
                NullPointerException.class,
                () -> this.guard()
                        .name(null)
        );

        this.checkEquals(
                "name",
                thrown.getMessage()
        );
    }

    @Test
    public void testNameUnknownFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.guard().name(new TestName("Unknown123"))
        );

        this.checkEquals(
                "Unknown TestName Unknown123",
                thrown.getMessage()
        );
    }

    // selector........................................................................................................

    @Test
    public void testSelector() {
        this.guard()
                .selector(
                        new TestSelector(
                                NAME
                        )
                );
    }

    @Test
    public void testSelectorWithNullFails() {
        final NullPointerException thrown = assertThrows(
                NullPointerException.class,
                () -> this.guard()
                        .selector(null)
        );

        this.checkEquals(
                "selector",
                thrown.getMessage()
        );
    }

    @Test
    public void testSelectorUnknownFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.guard()
                        .selector(
                                new TestSelector(
                                        new TestName("Unknown123")
                                )
                        )
        );

        this.checkEquals(
                "Unknown TestName Unknown123",
                thrown.getMessage()
        );
    }

    private FilteredProviderGuard<TestName, TestSelector> guard() {
        return FilteredProviderGuard.with(
                Sets.of(
                        NAME
                ),
                UNKNOWN
        );
    }

    // class............................................................................................................

    @Override
    public Class<FilteredProviderGuard<TestName, TestSelector>> type() {
        return Cast.to(FilteredProviderGuard.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    public static class TestName implements Name,
            Comparable<TestName> {

        TestName(final String name) {
            this.name = name;
        }

        @Override
        public String value() {
            return this.name;
        }

        private final String name;

        @Override
        public int compareTo(final TestName other) {
            return this.name.compareTo(other.name);
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof TestName && this.equals0((TestName) other);
        }

        private boolean equals0(final TestName other) {
            return this.compareTo(other) == 0;
        }

        @Override
        public String toString() {
            return this.name.toString();
        }

        @Override
        public CaseSensitivity caseSensitivity() {
            return CaseSensitivity.SENSITIVE;
        }
    }

    public static class TestSelector implements PluginSelectorLike<TestName> {

        TestSelector(final TestName name) {
            this.name = name;
        }

        @Override
        public TestName name() {
            return this.name;
        }

        private final TestName name;

        @Override
        public PluginSelectorLike<TestName> setName(final TestName name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String text() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PluginSelectorLike<TestName> setText(final String text) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PluginSelectorLike<TestName> setValues(List<?> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void printTree(final IndentingPrinter printer) {
            printer.println(this.name().toString());
        }

        @Override
        public String toString() {
            return this.name().toString();
        }
    }
}
