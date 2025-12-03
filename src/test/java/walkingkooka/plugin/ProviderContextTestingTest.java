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

import walkingkooka.Either;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContextTestingTest.TestProviderContext;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.plugin.store.PluginStores;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class ProviderContextTestingTest implements ProviderContextTesting<TestProviderContext> {

    @Override
    public void testEnvironmentValueLocaleEqualsLocale() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEnvironmentValueUserEqualsUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferent() {
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

    final static class TestProviderContext implements ProviderContext {

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
        public LineEnding lineEnding() {
            return LineEnding.NL;
        }

        @Override
        public ProviderContext setLineEnding(final LineEnding lineEnding) {
            return this.setEnvironmentValue(
                LINE_ENDING,
                lineEnding
            );
        }
        
        @Override
        public Locale locale() {
            return Locale.ENGLISH;
        }

        @Override
        public ProviderContext setLocale(final Locale locale) {
            return this.setEnvironmentValue(
                EnvironmentValueName.LOCALE,
                locale
            );
        }

        @Override
        public ProviderContext setUser(final Optional<EmailAddress> user) {
            return user.isPresent() ?
                this.setEnvironmentValue(
                    EnvironmentValueName.USER,
                    user.orElse(null)
                ) :
                this.removeEnvironmentValue(
                    EnvironmentValueName.USER
                );
        }

        @Override
        public ProviderContext cloneEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ProviderContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
            Objects.requireNonNull(name, "name");

            throw new UnsupportedOperationException();
        }

        @Override
        public Set<EnvironmentValueName<?>> environmentValueNames() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> ProviderContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                       final T value) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(value, "value");
            throw new UnsupportedOperationException();
        }

        @Override
        public ProviderContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
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
