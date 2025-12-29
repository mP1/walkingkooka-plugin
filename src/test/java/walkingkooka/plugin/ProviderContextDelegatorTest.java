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

import walkingkooka.convert.ConverterContexts;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.plugin.ProviderContextDelegatorTest.TestProviderContextDelegator;
import walkingkooka.plugin.store.PluginStores;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

public final class ProviderContextDelegatorTest implements ProviderContextTesting<TestProviderContextDelegator> {

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testRemoveEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentValueWithNowFails() {
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
    public void testSetUserWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestProviderContextDelegator createContext() {
        return new TestProviderContextDelegator();
    }

    @Override
    public Class<TestProviderContextDelegator> type() {
        return TestProviderContextDelegator.class;
    }

    final static class TestProviderContextDelegator implements ProviderContextDelegator {

        @Override
        public ProviderContext providerContext() {
            return ProviderContexts.basic(
                ConverterContexts.fake(),
                EnvironmentContexts.empty(
                    LineEnding.NL,
                    Locale.FRANCE,
                    () -> LocalDateTime.MIN,
                    EnvironmentContext.ANONYMOUS
                ),
                PluginStores.treeMap()
            );
        }

        @Override
        public ProviderContext setLocale(final Locale locale) {
            return this.setEnvironmentValue(
                EnvironmentValueName.LOCALE,
                locale
            );
        }

        @Override
        public ProviderContext cloneEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ProviderContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");

            return new TestProviderContextDelegator();
        }

        @Override
        public <T> ProviderContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                       final T value) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(value, "value");

            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
