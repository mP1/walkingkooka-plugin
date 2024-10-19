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
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Name;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface PluginAliasSetLikeTesting<N extends Name & Comparable<N>,
        I extends PluginInfoLike<I, N>,
        IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
        S extends PluginSelectorLike<N>,
        A extends PluginAliasLike<N, S, A>,
        AS extends PluginAliasSetLike<N, I, IS, S, A, AS>>
    extends ImmutableSortedSetTesting<AS, A>,
        HasTextTesting,
        TreePrintableTesting,
        ParseStringTesting<AS>,
        ToStringTesting<AS>,
        HasUrlFragmentTesting {

    // name.............................................................................................................

    @Test
    default void testNameWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSet().name(null)
        );
    }

    default void nameAndCheck(final String text,
                              final N name) {
        this.nameAndCheck(
                text,
                name,
                Optional.empty()
        );
    }

    default void nameAndCheck(final String text,
                              final N name,
                              final N expected) {
        this.nameAndCheck(
                text,
                name,
                Optional.of(expected)
        );
    }

    default void nameAndCheck(final String text,
                              final N name,
                              final Optional<N> expected) {
        this.nameAndCheck(
                this.parseString(text),
                name,
                expected
        );
    }

    default void nameAndCheck(final AS aliases,
                              final N name) {
        this.nameAndCheck(
                aliases,
                name,
                Optional.empty()
        );
    }

    default void nameAndCheck(final AS aliases,
                              final N name,
                              final N expected) {
        this.nameAndCheck(
                aliases,
                name,
                Optional.of(expected)
        );
    }

    default void nameAndCheck(final AS aliases,
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

    default void aliasAndCheck(final String text,
                               final N alias) {
        this.aliasAndCheck(
                this.parseString(text),
                alias,
                Optional.empty()
        );
    }

    default void aliasAndCheck(final String text,
                               final N alias,
                               final S expected) {
        this.aliasAndCheck(
                this.parseString(text),
                alias,
                Optional.of(expected)
        );
    }

    default void aliasAndCheck(final String text,
                               final N alias,
                               final Optional<S> expected) {
        this.aliasAndCheck(
                this.parseString(text),
                alias,
                expected
        );
    }

    default void aliasAndCheck(final AS aliases,
                               final N alias) {
        this.aliasAndCheck(
                aliases,
                alias,
                Optional.empty()
        );
    }

    default void aliasAndCheck(final AS aliases,
                               final N alias,
                               final S expected) {
        this.aliasAndCheck(
                aliases,
                alias,
                Optional.of(expected)
        );
    }

    default void aliasAndCheck(final AS aliases,
                               final N alias,
                               final Optional<S> expected) {
        this.checkEquals(
                expected,
                aliases.alias(alias),
                () -> "alias  " + alias + " in " + aliases
        );
    }

    // merge............................................................................................................

    @Test
    default void testMergeWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSet().merge(null)
        );
    }

    default void mergeAndCheck(final AS aliases,
                               final IS providerInfos,
                               final I... expected) {
        this.mergeAndCheck(
                aliases,
                providerInfos,
                Sets.of(expected)
        );
    }

    default void mergeAndCheck(final AS aliases,
                               final IS providerInfos,
                               final Set<I> expected) {
        this.checkEquals(
                expected,
                aliases.merge(providerInfos),
                "merge"
        );
    }

    // ParseString......................................................................................................

    @Override
    default void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    default Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    default RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // containsNameOrAlias.....................................................................................................

    @Test
    default void testContainsNameOrAliasWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSet()
                        .containsNameOrAlias(null)
        );
    }

    private void containsNameAndCheck(final String aliases,
                                      final N nameOrAlias,
                                      final boolean expected) {
        this.containsNameOrAliasAndCheck(
                this.parseString(aliases),
                nameOrAlias,
                expected
        );
    }

    private void containsNameOrAliasAndCheck(final AS aliases,
                                             final N nameOrAlias,
                                             final boolean expected) {
        this.checkEquals(
                expected,
                aliases.containsNameOrAlias(nameOrAlias),
                () -> aliases.text() + " containsNameOrAlias " + nameOrAlias
        );
    }

    // UrlFragment......................................................................................................

    @Test
    default void testUrlFragment() {
        final AS aliases = this.createSet();

        this.urlFragmentAndCheck(
                aliases,
                aliases.text()
        );
    }

    // class............................................................................................................

    @Override
    default void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
