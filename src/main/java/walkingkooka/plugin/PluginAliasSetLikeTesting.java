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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.set.ImmutableSortedSetTesting;
import walkingkooka.naming.Name;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface PluginAliasSetLikeTesting<N extends Name & Comparable<N>,
        I extends PluginInfoLike<I, N>,
        IS extends PluginInfoSetLike<IS, I, N>,
        S extends PluginSelectorLike<N>,
        A extends PluginAliasLike<N, S, A>,
        AS extends PluginAliasSetLike<N, I, IS, S, A>>
    extends ImmutableSortedSetTesting<AS, A>,
        HasTextTesting,
        TreePrintableTesting,
        ParseStringTesting<AS>,
        ToStringTesting<AS> {

    // name.............................................................................................................

    @Test
    default void testNameWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSet().name(null)
        );
    }

    private void nameAndCheck(final String text,
                              final N name) {
        this.nameAndCheck(
                text,
                name,
                Optional.empty()
        );
    }

    private void nameAndCheck(final String text,
                              final N name,
                              final N expected) {
        this.nameAndCheck(
                text,
                name,
                Optional.of(expected)
        );
    }

    private void nameAndCheck(final String text,
                              final N name,
                              final Optional<N> expected) {
        this.nameAndCheck(
                this.parseString(text),
                name,
                expected
        );
    }

    private void nameAndCheck(final AS aliases,
                              final N name) {
        this.nameAndCheck(
                aliases,
                name,
                Optional.empty()
        );
    }

    private void nameAndCheck(final AS aliases,
                              final N name,
                              final N expected) {
        this.nameAndCheck(
                aliases,
                name,
                Optional.of(expected)
        );
    }

    private void nameAndCheck(final AS aliases,
                              final N name,
                              final Optional<N> expected) {
        this.checkEquals(
                expected,
                aliases.name(name),
                () -> "name  " + name + " in " + aliases
        );
    }

    // alias............................................................................................................

    @Test
    default void testAliasWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSet().alias(null)
        );
    }

    private void aliasAndCheck(final String text,
                               final N alias) {
        this.aliasAndCheck(
                this.parseString(text),
                alias,
                Optional.empty()
        );
    }

    private void aliasAndCheck(final String text,
                               final N alias,
                               final S expected) {
        this.aliasAndCheck(
                this.parseString(text),
                alias,
                Optional.of(expected)
        );
    }

    private void aliasAndCheck(final String text,
                               final N alias,
                               final Optional<S> expected) {
        this.aliasAndCheck(
                this.parseString(text),
                alias,
                expected
        );
    }

    private void aliasAndCheck(final AS aliases,
                               final N alias) {
        this.aliasAndCheck(
                aliases,
                alias,
                Optional.empty()
        );
    }

    private void aliasAndCheck(final AS aliases,
                               final N alias,
                               final S expected) {
        this.aliasAndCheck(
                aliases,
                alias,
                Optional.of(expected)
        );
    }

    private void aliasAndCheck(final AS aliases,
                               final N alias,
                               final Optional<S> expected) {
        this.checkEquals(
                expected,
                aliases.alias(alias),
                () -> "alias  " + alias + " in " + aliases
        );
    }
    
    // ParseString......................................................................................................

    @Override
    default Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    default RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }
}
