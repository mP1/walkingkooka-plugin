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

import walkingkooka.ContextTesting;
import walkingkooka.environment.EnvironmentContextTesting2;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.text.printer.TreePrintableTesting;

public interface ProviderContextTesting<C extends ProviderContext> extends ContextTesting<C>,
    EnvironmentContextTesting2<C>,
    TreePrintableTesting {

    // pluginStore......................................................................................................

    default void pluginStoreAndCheck(final ProviderContext providerContext,
                                     final PluginStore expected) {
        this.checkEquals(
            expected,
            providerContext.pluginStore(),
            "pluginStore"
        );
    }

    // class............................................................................................................

    @Override
    default String typeNameSuffix() {
        return ProviderContext.class.getSimpleName();
    }
}
