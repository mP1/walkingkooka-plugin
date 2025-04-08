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

package walkingkooka.plugin.store;

import walkingkooka.convert.ConverterContexts;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContextDelegator;
import walkingkooka.plugin.ProviderContextTesting;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.plugin.store.ProviderContextDelegatorTest.TestProviderContextDelegator;

import java.time.LocalDateTime;

public final class ProviderContextDelegatorTest implements ProviderContextTesting<TestProviderContextDelegator> {

    @Override
    public void testTypeNaming() {
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
                    LocalDateTime::now,
                    EnvironmentContext.ANONYMOUS
                ),
                PluginStores.treeMap()
            );
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
