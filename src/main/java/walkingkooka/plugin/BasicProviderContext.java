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

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link ProviderContext} that delegates to a {@link EnvironmentContext}.
 */
final class BasicProviderContext implements ProviderContext {

    static BasicProviderContext with(final EnvironmentContext environmentContext) {
        return new BasicProviderContext(
                Objects.requireNonNull(environmentContext, "environmentContext")
        );
    }

    private BasicProviderContext(final EnvironmentContext environmentContext) {
        this.environmentContext = environmentContext;
    }

    @Override
    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        return this.environmentContext.environmentValue(name);
    }

    private final EnvironmentContext environmentContext;

    @Override
    public String toString() {
        return this.environmentContext.toString();
    }
}
