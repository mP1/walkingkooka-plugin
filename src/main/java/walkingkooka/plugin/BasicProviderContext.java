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

import walkingkooka.convert.ConverterLike;
import walkingkooka.convert.ConverterLikeDelegator;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.plugin.store.PluginStore;

import java.util.Objects;

/**
 * A {@link ProviderContext} that delegates to a {@link EnvironmentContext}.
 */
final class BasicProviderContext implements ProviderContext,
    ConverterLikeDelegator,
    EnvironmentContextDelegator {

    static BasicProviderContext with(final ConverterLike converterLike,
                                     final EnvironmentContext environmentContext,
                                     final PluginStore pluginStore) {
        return new BasicProviderContext(
            Objects.requireNonNull(converterLike, "converterLike"),
            Objects.requireNonNull(environmentContext, "environmentContext"),
            Objects.requireNonNull(pluginStore, "pluginStore")
        );
    }

    private BasicProviderContext(final ConverterLike converterLike,
                                 final EnvironmentContext environmentContext,
                                 final PluginStore pluginStore) {
        this.converterLike = converterLike;
        this.environmentContext = environmentContext;
        this.pluginStore = pluginStore;
    }

    @Override
    public PluginStore pluginStore() {
        return this.pluginStore;
    }

    private final PluginStore pluginStore;

    // ConverterLikeDelegator...........................................................................................

    @Override
    public ConverterLike converterLike() {
        return this.converterLike;
    }

    private final ConverterLike converterLike;

    // EnvironmentContext...............................................................................................

    @Override
    public ProviderContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.environmentContext.cloneEnvironment()
        );
    }

    // setEnvironmentContext............................................................................................

    @Override
    public ProviderContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final EnvironmentContext before = this.environmentContext;

        return before == environmentContext ?
            this :
            with(
                this.converterLike,
                environmentContext,
                this.pluginStore
            );
    }

    // EnvironmentContextDelegator......................................................................................

    @Override
    public EnvironmentContext environmentContext() {
        return this.environmentContext;
    }

    private final EnvironmentContext environmentContext;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.converterLike,
            this.environmentContext,
            this.pluginStore
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof BasicProviderContext &&
                this.equals0((BasicProviderContext) other));
    }

    private boolean equals0(final BasicProviderContext other) {
        return this.converterLike.equals(other.converterLike) &&
            this.environmentContext.equals(other.environmentContext) &&
            this.pluginStore.equals(other.pluginStore);
    }

    @Override
    public String toString() {
        return this.environmentContext.toString();
    }
}
