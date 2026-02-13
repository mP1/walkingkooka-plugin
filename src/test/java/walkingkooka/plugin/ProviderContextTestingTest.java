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
import walkingkooka.environment.EnvironmentValueWatcher;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContextTestingTest.TestProviderContext;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.plugin.store.PluginStores;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
        public Currency currency() {
            return Currency.getInstance("AUD");
        }

        @Override
        public void setCurrency(final Currency currency) {
            this.setEnvironmentValue(
                CURRENCY,
                currency
            );
        }
        
        @Override
        public Indentation indentation() {
            return Indentation.SPACES2;
        }

        @Override
        public void setIndentation(final Indentation indentation) {
            this.setEnvironmentValue(
                INDENTATION,
                indentation
            );
        }
        
        @Override
        public LineEnding lineEnding() {
            return LineEnding.NL;
        }

        @Override
        public void setLineEnding(final LineEnding lineEnding) {
            this.setEnvironmentValue(
                LINE_ENDING,
                lineEnding
            );
        }
        
        @Override
        public Locale locale() {
            return Locale.ENGLISH;
        }

        @Override
        public void setLocale(final Locale locale) {
            this.setEnvironmentValue(
                EnvironmentValueName.LOCALE,
                locale
            );
        }

        @Override
        public ZoneOffset timeOffset() {
            return ZoneOffset.ofHours(1);
        }

        @Override
        public void setTimeOffset(final ZoneOffset timeOffset) {
            this.setEnvironmentValue(
                EnvironmentValueName.TIME_OFFSET,
                timeOffset
            );
        }

        @Override
        public void setUser(final Optional<EmailAddress> user) {
            if (user.isPresent()) {
                this.setEnvironmentValue(
                    EnvironmentValueName.USER,
                    user.orElse(null)
                );
            } else {
                this.removeEnvironmentValue(
                    EnvironmentValueName.USER
                );
            }
        }

        @Override
        public ProviderContext cloneEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ProviderContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");
            return new TestProviderContext();
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
        public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                            final T value) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(value, "value");
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
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
        public Runnable addEventValueWatcher(final EnvironmentValueWatcher watcher) {
            Objects.requireNonNull(watcher, "watcher");
            throw new UnsupportedOperationException();
        }

        @Override
        public Runnable addEventValueWatcherOnce(final EnvironmentValueWatcher watcher) {
            Objects.requireNonNull(watcher, "watcher");
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
