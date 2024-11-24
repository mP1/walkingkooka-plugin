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

import org.junit.jupiter.api.Test;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.FakeEnvironmentContext;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.plugin.store.PluginStores;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicProviderContextTest implements ProviderContextTesting<BasicProviderContext> {

    private final static EnvironmentValueName<String> VAR = EnvironmentValueName.with("magic");

    private final static String VAR_VALUE = "MagicValue123";

    private final static EnvironmentContext ENVIRONMENT_CONTEXT = new FakeEnvironmentContext() {
        @Override
        public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
            Objects.requireNonNull(name, "name");

            return Optional.ofNullable(
                    VAR.equals(name) ?
                            (T) VAR_VALUE :
                            null
            );
        }
    };

    private final static PluginStore PLUGIN_STORE = PluginStores.fake();


    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicProviderContext.with(
                        null,
                        PLUGIN_STORE
                )
        );
    }

    @Test
    public void testWithNullPluginStoreFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicProviderContext.with(
                        ENVIRONMENT_CONTEXT,
                        null
                )
        );
    }

    // environmentValue.................................................................................................

    @Test
    public void testEnvironmentValue() {
        this.environmentValueAndCheck(
                this.createContext(),
                VAR,
                VAR_VALUE
        );
    }

    @Test
    public void testEnvironmentValueUnknown() {
        this.environmentValueAndCheck(
                this.createContext(),
                EnvironmentValueName.with("Unknown")
        );
    }

    // pluginStore.....................................................................................................

    @Test
    public void testPluginStore() {
        this.pluginStoreAndCheck(
                this.createContext(),
                PLUGIN_STORE
        );
    }


    @Override
    public BasicProviderContext createContext() {
        return BasicProviderContext.with(
                ENVIRONMENT_CONTEXT,
                PLUGIN_STORE
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createContext(),
                ENVIRONMENT_CONTEXT.toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicProviderContext> type() {
        return BasicProviderContext.class;
    }

    @Override
    public String typeNameSuffix() {
        return ProviderContext.class.getSimpleName();
    }
}
