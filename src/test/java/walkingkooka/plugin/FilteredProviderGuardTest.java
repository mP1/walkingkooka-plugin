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
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class FilteredProviderGuardTest implements ClassTesting2<FilteredProviderGuard<StringName, TestPluginSelector>> {

    private final static StringName NAME = Names.string("Hello123");

    @Test
    public void testWithNullSetFails() {
        assertThrows(
                NullPointerException.class,
                () -> FilteredProviderGuard.with(
                        null,
                        TestPluginHelper.INSTANCE
                )
        );
    }

    @Test
    public void testWithNullPluginFails() {
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
                () -> this.guard().name(Names.string("Unknown123"))
        );

        this.checkEquals(
                "Unknown StringName Unknown123",
                thrown.getMessage()
        );
    }

    // selector........................................................................................................

    @Test
    public void testSelector() {
        this.guard()
                .selector(
                        new TestPluginSelector(
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
                                new TestPluginSelector(
                                        Names.string("Unknown123")
                                )
                        )
        );

        this.checkEquals(
                "Unknown StringName Unknown123",
                thrown.getMessage()
        );
    }

    private FilteredProviderGuard<StringName, TestPluginSelector> guard() {
        return FilteredProviderGuard.with(
                Sets.of(
                        NAME
                ),
                TestPluginHelper.INSTANCE
        );
    }

    // class............................................................................................................

    @Override
    public Class<FilteredProviderGuard<StringName, TestPluginSelector>> type() {
        return Cast.to(FilteredProviderGuard.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
