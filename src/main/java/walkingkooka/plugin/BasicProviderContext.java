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

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.plugin.store.PluginStore;

import java.util.Objects;

/**
 * A {@link ProviderContext} that delegates to a {@link EnvironmentContext}.
 */
final class BasicProviderContext implements ProviderContext, EnvironmentContextDelegator {

    static BasicProviderContext with(final EnvironmentContext environmentContext,
                                     final PluginStore pluginStore) {
        return new BasicProviderContext(
            Objects.requireNonNull(environmentContext, "environmentContext"),
            Objects.requireNonNull(pluginStore, "pluginStore")
        );
    }

    private BasicProviderContext(final EnvironmentContext environmentContext,
                                 final PluginStore pluginStore) {
        this.environmentContext = environmentContext;
        this.pluginStore = pluginStore;
    }

    @Override
    public EnvironmentContext environmentContext() {
        return this.environmentContext;
    }

    private final EnvironmentContext environmentContext;

    @Override
    public PluginStore pluginStore() {
        return this.pluginStore;
    }

    private final PluginStore pluginStore;

    @Override
    public String toString() {
        return this.environmentContext.toString();
    }
}
