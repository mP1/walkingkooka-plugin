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

import walkingkooka.Cast;
import walkingkooka.environment.EnvironmentValueName;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link ProviderContext} that always returns a dummy value for any given {@link EnvironmentValueName}.
 * This is necessary because {@link PluginInfoSetParser} will attempt to resolve environment names into values when asked to consume a selector.
 * Validating values exist for any name is not a goal of {@link PluginAliases#parse(String, BiFunction, BiFunction, PluginInfoSetLike, Function)}.
 */
final class PluginAliasesProviderContext implements ProviderContext {

    /**
     * Singleton
     */
    final static PluginAliasesProviderContext INSTANCE = new PluginAliasesProviderContext();

    private PluginAliasesProviderContext() {
        super();
    }

    @Override
    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        Objects.requireNonNull(name, "name");

        return Cast.to(DUMMY);
    }

    private final static Optional<?> DUMMY = Optional.of("Dummy");

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
