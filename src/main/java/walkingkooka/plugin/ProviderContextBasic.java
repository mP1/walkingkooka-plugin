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
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageContextDelegator;
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;

import java.util.List;
import java.util.Objects;

/**
 * A {@link ProviderContext} that delegates to a {@link EnvironmentContext}.
 */
final class ProviderContextBasic implements ProviderContext,
    StorageContextDelegator {

    static ProviderContextBasic with(final StorageContext storageContext) {
        return new ProviderContextBasic(
            Objects.requireNonNull(storageContext, "storageContext")
        );
    }

    private ProviderContextBasic(final StorageContext storageContext) {
        super();
        this.storageContext = storageContext;
    }

    @Override
    public StorageValue saveStorage(final StorageValue storageValue) {
        return ProviderContext.super.saveStorage(storageValue);
    }

    @Override
    public void deleteStorage(final StoragePath storagePath) {
        ProviderContext.super.deleteStorage(storagePath);
    }

    @Override
    public void mountStorage(final StorageMountPoint<?> storageMountPoint) {
        ProviderContext.super.mountStorage(storageMountPoint);
    }

    @Override
    public void unmountStorage(final StoragePath storagePath) {
        ProviderContext.super.unmountStorage(storagePath);
    }

    @Override
    public List<StorageMountPoint<?>> storageMountPoints() {
        return ProviderContext.super.storageMountPoints();
    }

    // StorageEnvironmentContext........................................................................................

    @Override
    public ProviderContext cloneEnvironment() {
        return with(
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
            with(after);
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
        return this.storageContext.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof ProviderContextBasic &&
                this.equals0((ProviderContextBasic) other));
    }

    private boolean equals0(final ProviderContextBasic other) {
        return this.storageContext.equals(other.storageContext);
    }

    @Override
    public String toString() {
        return this.storageContext.toString();
    }
}
