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
import walkingkooka.plugin.ProviderContextTestingTest.TestProviderContext;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.plugin.store.PluginStores;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.StorageEnvironmentContextDelegator;
import walkingkooka.storage.StoragePath;

import java.util.Objects;

public final class ProviderContextTestingTest implements ProviderContextTesting<TestProviderContext> {

    @Override
    public void testEnvironmentValueLineEndingEqualsLineEnding() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEnvironmentValueLocaleEqualsLocale() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEnvironmentValueNowEqualsNow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEnvironmentValueUserEqualsUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testRemoveEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetCurrencyWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetIndentationWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLineEndingWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetTimeOffsetWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetUserWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentContextWithEqualEnvironmentContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestProviderContext createContext() {
        return new TestProviderContext();
    }

    @Override
    public String typeNameSuffix() {
        return ProviderContext.class.getSimpleName();
    }

    @Override
    public Class<TestProviderContext> type() {
        return TestProviderContext.class;
    }

    final static class TestProviderContext implements ProviderContext,
        StorageEnvironmentContextDelegator {

        @Override
        public boolean canConvert(final Object value,
                                  final Class<?> type) {
            return CONVERTER_LIKE.canConvert(
                value,
                type
            );
        }

        @Override
        public <T> Either<T, String> convert(final Object value,
                                             final Class<T> type) {
            return CONVERTER_LIKE.convert(
                value,
                type
            );
        }

        @Override
        public PluginStore pluginStore() {
            return PluginStores.fake();
        }

        @Override
        public MediaType detect(final String text,
                                final Binary binary) {
            return MEDIA_TYPE_DETECTOR.detect(
                text,
                binary
            );
        }

        @Override
        public StoragePath parseStoragePath(final String text) {
            return StoragePath.parse(text);
        }

        @Override
        public ProviderContext cloneEnvironment() {
            return new TestProviderContext();
        }

        @Override
        public ProviderContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");
            throw new UnsupportedOperationException();
        }

        @Override
        public StorageEnvironmentContext storageEnvironmentContext() {
            return this.storageEnvironmentContext;
        }

        private final StorageEnvironmentContext storageEnvironmentContext = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
