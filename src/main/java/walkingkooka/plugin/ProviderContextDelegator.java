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
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageContextDelegator;
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageWatcher;

import java.util.List;

public interface ProviderContextDelegator extends ProviderContext,
    StorageContextDelegator {

    @Override
    default PluginStore pluginStore() {
        return this.providerContext()
            .pluginStore();
    }

    ProviderContext providerContext();

    // StorageContextDelegator..........................................................................................

    @Override
    default StorageValue saveStorage(final StorageValue storageValue) {
        return ProviderContext.super.saveStorage(storageValue);
    }

    @Override
    default void deleteStorage(final StoragePath storagePath) {
        ProviderContext.super.deleteStorage(storagePath);
    }

    @Override
    default void mountStorage(final StorageMountPoint<?> storageMountPoint) {
        ProviderContext.super.mountStorage(storageMountPoint);
    }

    @Override
    default void unmountStorage(final StoragePath storagePath) {
        ProviderContext.super.unmountStorage(storagePath);
    }

    @Override
    default List<StorageMountPoint<?>> storageMountPoints() {
        return ProviderContext.super.storageMountPoints();
    }

    @Override
    default Runnable addStorageWatcher(final StorageWatcher watcher) {
        return ProviderContext.super.addStorageWatcher(watcher);
    }

    @Override
    default Runnable addStorageWatcherOnce(final StorageWatcher watcher) {
        return ProviderContext.super.addStorageWatcherOnce(watcher);
    }

    @Override
    default StorageContext storageContext() {
        return this.providerContext();
    }

    // ConverterLike....................................................................................................

    @Override
    default ConverterLike converterLike() {
        return this.providerContext();
    }
}
