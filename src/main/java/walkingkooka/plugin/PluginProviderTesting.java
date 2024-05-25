/*
 * Copyright 2020 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface PluginProviderTesting<P extends PluginProvider> extends PluginProviderLikeTesting<P, PluginProviderName>,
        TreePrintableTesting {

    @Test
    default void testPluginWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginProvider().plugin(
                        null,
                        Void.class
                )
        );
    }

    @Test
    default void testPluginWithNullTypeFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginProvider().plugin(
                        null,
                        Void.class
                )
        );
    }


    default <T> T pluginAndCheck(final PluginName name,
                                 final Class<T> type,
                                 final T expected) {
        return this.pluginAndCheck(
                this.createPluginProvider(),
                name,
                type,
                expected
        );
    }

    default <T> T pluginAndCheck(final P provider,
                                 final PluginName name,
                                 final Class<T> type,
                                 final T expected) {
        final T plugin = provider.plugin(
                name,
                type
        );
        this.checkEquals(
                expected,
                plugin,
                () -> "plugin " + name + " type " + type.getSimpleName()
        );
        return plugin;
    }

    @Test
    default void testPluginsWithNullTypeFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginProvider()
                        .plugins(null)
        );
    }

    default <T> Set<T> pluginsAndCheck(final Class<T> type,
                                       final T... expected) {
        return this.pluginsAndCheck(
                type,
                Sets.of(expected)
        );
    }

    default <T> Set<T> pluginsAndCheck(final Class<T> type,
                                       final Set<T> expected) {
        return this.pluginsAndCheck(
                this.createPluginProvider(),
                type,
                expected
        );
    }

    default <T> Set<T> pluginsAndCheck(final P provider,
                                       final Class<T> type,
                                       final T... expected) {
        return this.pluginsAndCheck(
                provider,
                type,
                Sets.of(expected)
        );
    }

    default <T> Set<T> pluginsAndCheck(final P provider,
                                       final Class<T> type,
                                       final Set<T> expected) {
        final Set<T> plugins = provider.plugins(
                type
        );
        this.checkEquals(
                expected,
                plugins,
                () -> "plugins type " + type.getSimpleName()
        );
        return plugins;
    }

    default void pluginInfosAndCheck(final PluginInfo... expected) {
        this.pluginInfosAndCheck(
                this.createPluginProvider(),
                expected
        );
    }

    default void pluginInfosAndCheck(final P provider,
                                     final PluginInfo... expected) {
        this.pluginInfosAndCheck(
                provider,
                Sets.of(expected)
        );
    }

    default void pluginInfosAndCheck(final Set<PluginInfo> expected) {
        this.pluginInfosAndCheck(
                this.createPluginProvider(),
                expected
        );
    }

    default void pluginInfosAndCheck(final P provider,
                                     final Set<PluginInfo> expected) {
        this.checkEquals(
                expected,
                provider.pluginInfos(),
                () -> "pluginInfos" + provider
        );
    }

    // factory..........................................................................................................

    P createPluginProvider();
}
