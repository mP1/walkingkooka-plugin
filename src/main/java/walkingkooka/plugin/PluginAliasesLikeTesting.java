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
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Name;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface PluginAliasesLikeTesting<L extends PluginAliasesLike<N, I, IS, S>, N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, IS extends PluginInfoSetLike<IS, I, N>, S extends PluginSelectorLike<N>> extends ClassTesting2<L>,
        TreePrintableTesting {

    // names............................................................................................................

    default void namesAndCheck(final L pluginNames,
                               final N... expected) {
        this.namesAndCheck(
                pluginNames,
                Sets.of(expected)
        );
    }

    default void namesAndCheck(final L pluginNames,
                               final Set<N> expected) {
        this.checkEquals(
                expected,
                pluginNames.names(),
                () -> pluginNames + " names"
        );
    }

    // name.............................................................................................................

    @Test
    default void testNamesWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginAliases().name(null)
        );
    }

    default void nameAndCheck(final L pluginAliases,
                              final N name) {
        this.nameAndCheck(
                pluginAliases,
                name,
                Optional.empty()
        );
    }

    default void nameAndCheck(final L pluginAliases,
                              final N name,
                              final N expected) {
        this.nameAndCheck(
                pluginAliases,
                name,
                Optional.of(expected)
        );
    }

    default void nameAndCheck(final L pluginAliases,
                              final N name,
                              final Optional<N> expected) {
        this.checkEquals(
                expected,
                pluginAliases.name(name),
                () -> "name " + name
        );
    }

    // aliases..........................................................................................................

    default void aliasesAndCheck(final L pluginAliases,
                                 final N... expected) {
        this.aliasesAndCheck(
                pluginAliases,
                Sets.of(expected)
        );
    }

    default void aliasesAndCheck(final L pluginAliases,
                                 final Set<N> expected) {
        this.checkEquals(
                expected,
                pluginAliases.aliases(),
                () -> pluginAliases + " aliases"
        );
    }

    // alias............................................................................................................

    @Test
    default void testAliasWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginAliases().alias(null)
        );
    }

    default void aliasAndCheck(final L pluginAliases,
                               final N alias) {
        this.aliasAndCheck(
                pluginAliases,
                alias,
                Optional.empty()
        );
    }

    default void aliasAndCheck(final L pluginAliases,
                               final N alias,
                               final S expected) {
        this.aliasAndCheck(
                pluginAliases,
                alias,
                Optional.of(expected)
        );
    }

    default void aliasAndCheck(final L pluginAliases,
                               final N alias,
                               final Optional<S> expected) {
        this.checkEquals(
                expected,
                pluginAliases.alias(alias),
                () -> "alias " + alias
        );
    }

    // infos............................................................................................................

    default void infosAndCheck(final L pluginInfos,
                               final I... expected) {
        this.infosAndCheck(
                pluginInfos,
                Sets.of(expected)
        );
    }

    default void infosAndCheck(final L pluginInfos,
                               final Set<I> expected) {
        this.checkEquals(
                pluginInfos.infos(),
                expected,
                "infos"
        );
    }

    L createPluginAliases();

    // class............................................................................................................
    @Override
    default JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
