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
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageWatcher;

import java.util.List;
import java.util.Objects;

/**
 * A {@link walkingkooka.Context} that should be passed to all {@link Provider} public methods.
 * Note ALL {@link walkingkooka.storage.Storage} mutable methods from {@link StorageContext} should throw {@link UnsupportedOperationException}.
 */
public interface ProviderContext extends StorageContext {

    @Override
    default StorageValue saveStorage(final StorageValue storageValue) {
        Objects.requireNonNull(storageValue, "storageValue");

        throw new UnsupportedOperationException();
    }

    @Override
    default void deleteStorage(final StoragePath storagePath) {
        Objects.requireNonNull(storagePath, "storagePath");

        throw new UnsupportedOperationException();
    }

    @Override
    default void mountStorage(final StorageMountPoint<?> storageMountPoint) {
        Objects.requireNonNull(storageMountPoint, "storageMountPoint");

        throw new UnsupportedOperationException();
    }

    @Override
    default void unmountStorage(final StoragePath storagePath) {
        Objects.requireNonNull(storagePath, "storagePath");

        throw new UnsupportedOperationException();
    }

    @Override
    default List<StorageMountPoint<?>> storageMountPoints() {
        throw new UnsupportedOperationException();
    }

    @Override
    default Runnable addStorageWatcher(final StorageWatcher watcher) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Runnable addStorageWatcherOnce(final StorageWatcher watcher) {
        throw new UnsupportedOperationException();
    }

    @Override
    ProviderContext cloneEnvironment();

    @Override
    ProviderContext setEnvironmentContext(EnvironmentContext environmentContext);

    /**
     * A {@link PluginStore} holding plugins.
     */
    PluginStore pluginStore();
}
