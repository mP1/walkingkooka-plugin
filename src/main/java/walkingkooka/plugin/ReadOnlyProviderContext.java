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
import walkingkooka.Either;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.net.header.MediaType;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.predicate.Predicates;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.StorageEnvironmentContextDelegator;
import walkingkooka.storage.StorageEnvironmentContexts;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link ProviderContext} where all setXXX and removeXXX methods throw {@link UnsupportedOperationException}.
 * Note the {@link #cloneEnvironment()} returns a clone of the wrapped {@link ProviderContext}.
 */
final class ReadOnlyProviderContext implements ProviderContext,
    StorageEnvironmentContextDelegator {

    static ReadOnlyProviderContext with(final ProviderContext context) {
        ReadOnlyProviderContext readOnlyProviderContext;

        if (context instanceof ReadOnlyProviderContext) {
            readOnlyProviderContext = (ReadOnlyProviderContext) context;
        } else {
            readOnlyProviderContext = new ReadOnlyProviderContext(
                Objects.requireNonNull(context, "context")
            );
        }

        return readOnlyProviderContext;
    }

    private ReadOnlyProviderContext(final ProviderContext context) {
        this.context = context;

        this.readOnlyStorageEnvironmentContext = StorageEnvironmentContexts.readOnly(
            Predicates.always(), // all values are readonly
            context
        );
    }

    @Override
    public MediaType detect(final String filename,
                            final Binary binary) {
        return this.context.detect(
            filename,
            binary
        );
    }

    @Override
    public StoragePath parseStoragePath(final String text) {
        return this.context.parseStoragePath(text);
    }

    @Override
    public PluginStore pluginStore() {
        return this.context.pluginStore();
    }

    // ConverterLike....................................................................................................

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.context.canConvert(
            value,
            type
        );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.context.convert(
            value,
            type
        );
    }

    @Override
    public boolean canReadStorage(final StoragePath path) {
        return this.context.canReadStorage(path);
    }

    @Override
    public boolean canWriteStorage(final StoragePath path) {
        Objects.requireNonNull(path, "path");
        return false;
    }

    @Override
    public Optional<StorageValue> loadStorage(final StoragePath path) {
        return this.context.loadStorage(path);
    }

    @Override
    public List<StorageValueInfo> listStorage(final StoragePath parent,
                                              final int offset,
                                              final int count) {
        return this.context.listStorage(
            parent,
            offset,
            count
        );
    }

    @Override
    public void setAuditInfoStorage(final StorageValueInfo info) {
        Objects.requireNonNull(info, "info");

        throw new UnsupportedOperationException();
    }

    // StorageEnvironmentContext........................................................................................

    @Override
    public ProviderContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.context.cloneEnvironment()
        );
    }

    @Override
    public ProviderContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final ProviderContext before = this.context;
        final ProviderContext after = before.setEnvironmentContext(environmentContext);
        return before == after ?
            this :
            after;
    }

    // StorageEnvironmentContextDelegator...............................................................................

    @Override
    public StorageEnvironmentContext storageEnvironmentContext() {
        return this.readOnlyStorageEnvironmentContext;
    }

    private final StorageEnvironmentContext readOnlyStorageEnvironmentContext;

    private final ProviderContext context;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.context.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof ReadOnlyProviderContext &&
                this.equals0((ReadOnlyProviderContext) other));
    }

    private boolean equals0(final ReadOnlyProviderContext other) {
        return this.context.equals(other.context);
    }

    @Override
    public String toString() {
        return "ReadOnly " + this.context;
    }
}
