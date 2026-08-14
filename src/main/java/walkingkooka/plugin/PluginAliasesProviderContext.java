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

import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.EnvironmentWatcher;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.StorageEnvironmentContextDelegator;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.store.Store;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link ProviderContext} that always returns a dummy value for any given {@link EnvironmentValueName}.
 * This is necessary because {@link PluginInfoSetLikeParser} will attempt to resolve environment names into values when asked to consume a selector.
 * Validating values exist for any name is not a goal of {@link PluginAlias#parse(String, PluginHelper)}.
 */
final class PluginAliasesProviderContext implements ProviderContext,
    StorageEnvironmentContextDelegator {

    /**
     * Singleton
     */
    final static PluginAliasesProviderContext INSTANCE = new PluginAliasesProviderContext();

    private PluginAliasesProviderContext() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MediaType detect(final String filename,
                            final Binary binary) {
        return MediaTypeDetectors.binary()
            .detect(
                filename,
                binary
            );
    }

    @Override
    public boolean canReadStorage(final StoragePath path) {
        Objects.requireNonNull(path, "path");
        return false;
    }

    @Override
    public boolean canWriteStorage(final StoragePath path) {
        Objects.requireNonNull(path, "path");
        return false;
    }

    @Override
    public Optional<StorageValue> loadStorage(final StoragePath path) {
        Objects.requireNonNull(path, "path");
        return Optional.empty();
    }

    @Override
    public List<StorageValueInfo> listStorage(final StoragePath path,
                                              final int offset,
                                              final int count) {
        Objects.requireNonNull(path, "path");
        Store.checkOffsetAndCount(
            offset,
            count
        );
        return Lists.empty();
    }

    @Override
    public void setAuditInfoStorage(final StorageValueInfo info) {
        Objects.requireNonNull(info, "info");
        throw new UnsupportedOperationException();
    }

    @Override
    public StoragePath parseStoragePath(final String text) {
        return StoragePath.parse(text);
    }

    @Override
    public PluginStore pluginStore() {
        throw new UnsupportedOperationException();
    }

    // EnvironmentContext...............................................................................................

    @Override
    public ProviderContext cloneEnvironment() {
        return this;
    }

    @Override
    public ProviderContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        throw new UnsupportedOperationException();
    }

    // StorageEnvironmentContextDelegator...............................................................................

    @Override
    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        Objects.requireNonNull(name, "name");

        return Cast.to(DUMMY);
    }

    private final static Optional<?> DUMMY = Optional.of("Dummy");

    @Override
    public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
        Objects.requireNonNull(name, "name");

        // nop
    }

    @Override
    public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                        final T value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");

        throw name.readOnlyEnvironmentValueException();
    }

    @Override
    public Set<EnvironmentValueName<?>> environmentValueNames() {
        return Sets.empty();
    }

    @Override
    public Runnable addEnvironmentWatcherOnce(final EnvironmentWatcher watcher) {
        Objects.requireNonNull(watcher, "watcher");
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addEnvironmentWatcher(final EnvironmentWatcher watcher) {
        Objects.requireNonNull(watcher, "watcher");
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageEnvironmentContext storageEnvironmentContext() {
        return this;
    }

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
