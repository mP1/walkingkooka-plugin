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
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContextTestingTest.TestProviderContext;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.plugin.store.PluginStores;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class ProviderContextTestingTest implements ProviderContextTesting<TestProviderContext> {

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

    final static class TestProviderContext implements ProviderContext {

        public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
            Objects.requireNonNull(name, "name");

            throw new UnsupportedOperationException();
        }

        @Override
        public LocalDateTime now() {
            return LocalDateTime.now();
        }

        @Override
        public Optional<EmailAddress> user() {
            return EnvironmentContext.ANONYMOUS;
        }

        @Override
        public PluginStore pluginStore() {
            return PluginStores.fake();
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
