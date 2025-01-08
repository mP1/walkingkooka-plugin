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

import walkingkooka.collect.set.Sets;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Set;

public interface PluginProviderTesting<P extends PluginProvider> extends PluginProviderLikeTesting<P, PluginProviderName>,
    JarFileTesting,
    TreePrintableTesting {

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
