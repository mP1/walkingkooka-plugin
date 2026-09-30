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
import walkingkooka.plugin.ProviderContextDelegatorTest.TestProviderContextDelegator;

import java.util.Objects;

public final class ProviderContextDelegatorTest implements ProviderContextTesting<TestProviderContextDelegator> {

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEnvironmentContext() {
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
                STORAGE_CONTEXT.cloneEnvironment()
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
        public EnvironmentContext environmentContext() {
            return this.environmentContext;
        }

        private final EnvironmentContext environmentContext = ENVIRONMENT_CONTEXT.cloneEnvironment();

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
