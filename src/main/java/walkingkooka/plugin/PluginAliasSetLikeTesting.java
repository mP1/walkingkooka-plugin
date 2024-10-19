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
    default void testAliasOrNameWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSet().aliasOrName(null)
        );
    }

    default void aliasOrNameAndCheck(final String text,
                                     final N aliasOrName) {
        this.aliasOrNameAndCheck(
                text,
                aliasOrName,
                Optional.empty()
        );
    }

    default void aliasOrNameAndCheck(final String text,
                                     final N aliasOrName,
                                     final N expected) {
        this.aliasOrNameAndCheck(
                text,
                aliasOrName,
                Optional.of(expected)
        );
    }

    default void aliasOrNameAndCheck(final String text,
                                     final N aliasOrName,
                                     final Optional<N> expected) {
        this.aliasOrNameAndCheck(
                this.parseString(text),
                aliasOrName,
                expected
        );
    }

    default void aliasOrNameAndCheck(final AS aliases,
                                     final N aliasOrName) {
        this.aliasOrNameAndCheck(
                aliases,
                aliasOrName,
                Optional.empty()
        );
    }

    default void aliasOrNameAndCheck(final AS aliases,
                                     final N aliasOrName,
                                     final N expected) {
        this.aliasOrNameAndCheck(
                aliases,
                aliasOrName,
                Optional.of(expected)
        );
    }

    default void aliasOrNameAndCheck(final AS aliases,
                                     final N aliasOrName,
                                     final Optional<N> expected) {
        this.checkEquals(
                expected,
                aliases.aliasOrName(aliasOrName),
                () -> "aliasOrName  " + aliasOrName + " in " + aliases
        );
    }

    // aliasSelector....................................................................................................

    @Test
    default void testAliasSelectorWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSet().aliasSelector(null)
        );
    }

    default void aliasSelectorAndCheck(final String text,
                                       final N alias) {
        this.aliasSelectorAndCheck(
                this.parseString(text),
                alias,
                Optional.empty()
        );
    }

    default void aliasSelectorAndCheck(final String text,
                                       final N alias,
                                       final S expected) {
        this.aliasSelectorAndCheck(
                this.parseString(text),
                alias,
                Optional.of(expected)
        );
    }

    default void aliasSelectorAndCheck(final String text,
                                       final N alias,
                                       final Optional<S> expected) {
        this.aliasSelectorAndCheck(
                this.parseString(text),
                alias,
                expected
        );
    }

    default void aliasSelectorAndCheck(final AS aliases,
                                       final N alias) {
        this.aliasSelectorAndCheck(
                aliases,
                alias,
                Optional.empty()
        );
    }

    default void aliasSelectorAndCheck(final AS aliases,
                                       final N alias,
                                       final S expected) {
        this.aliasSelectorAndCheck(
                aliases,
                alias,
                Optional.of(expected)
        );
    }

    default void aliasSelectorAndCheck(final AS aliases,
                                       final N alias,
                                       final Optional<S> expected) {
        this.checkEquals(
                expected,
                aliases.aliasSelector(alias),
                () -> "aliasSelector  " + alias + " in " + aliases
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
