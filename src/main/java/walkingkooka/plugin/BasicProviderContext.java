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
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageContextDelegator;

import java.util.Objects;

/**
 * A {@link ProviderContext} that delegates to a {@link EnvironmentContext}.
 */
final class BasicProviderContext implements ProviderContext,
    StorageContextDelegator {

    static BasicProviderContext with(final PluginStore pluginStore,
                                     final StorageContext storageContext) {
        return new BasicProviderContext(
            Objects.requireNonNull(pluginStore, "pluginStore"),
            Objects.requireNonNull(storageContext, "storageContext")
        );
    }

    private BasicProviderContext(final PluginStore pluginStore,
                                 final StorageContext storageContext) {
        super();
        this.pluginStore = pluginStore;
        this.storageContext = storageContext;
    }

    @Override
    public PluginStore pluginStore() {
        return this.pluginStore;
    }

    private final PluginStore pluginStore;

    // StorageEnvironmentContext........................................................................................

    @Override
    public ProviderContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.storageContext.cloneEnvironment()
        );
    }

    // setEnvironmentContext............................................................................................

    @Override
    public ProviderContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final StorageContext before = this.storageContext;
        final StorageContext after = before.setEnvironmentContext(environmentContext);

        return before == after ?
            this :
            with(
                this.pluginStore,
                after
            );
    }

    // StorageContextDelegator..........................................................................................

    @Override
    public StorageContext storageContext() {
        return this.storageContext;
    }

    private final StorageContext storageContext;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.pluginStore,
            this.storageContext
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof BasicProviderContext &&
                this.equals0((BasicProviderContext) other));
    }

    private boolean equals0(final BasicProviderContext other) {
        return this.pluginStore.equals(other.pluginStore) &&
            this.storageContext.equals(other.storageContext);
    }

    @Override
    public String toString() {
        return this.storageContext.toString();
    }
}
