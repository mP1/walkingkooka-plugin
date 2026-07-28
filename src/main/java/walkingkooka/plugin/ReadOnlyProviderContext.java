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
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.predicate.Predicates;

import java.util.Objects;

/**
 * A {@link ProviderContext} where all setXXX and removeXXX methods throw {@link UnsupportedOperationException}.
 * Note the {@link #cloneEnvironment()} returns a clone of the wrapped {@link ProviderContext}.
 */
final class ReadOnlyProviderContext implements ProviderContext,
    EnvironmentContextDelegator {

    static ReadOnlyProviderContext with(final ProviderContext context) {
        ReadOnlyProviderContext readOnlyProviderContext;

        if (context instanceof ReadOnlyProviderContext) {
            readOnlyProviderContext = (ReadOnlyProviderContext) context;
        } else {
            readOnlyProviderContext = new ReadOnlyProviderContext(
                Objects.requireNonNull(context, "context")
            );
        }

        return readOnlyProviderContext;
    }

    private ReadOnlyProviderContext(final ProviderContext context) {
        this.context = context;

        this.readOnlyEnvironmentContext = EnvironmentContexts.readOnly(
            Predicates.always(), // all values are readonly
            context
        );
    }

    @Override
    public PluginStore pluginStore() {
        return this.context.pluginStore();
    }

    // ConverterLike....................................................................................................

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.context.canConvert(
            value,
            type
        );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.context.convert(
            value,
            type
        );
    }

    // EnvironmentContext...............................................................................................

    @Override
    public ProviderContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.context.cloneEnvironment()
        );
    }

    @Override
    public ProviderContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final ProviderContext before = this.context;
        final ProviderContext after = before.setEnvironmentContext(environmentContext);
        return before == after ?
            this :
            after;
    }

    // EnvironmentContextDelegator......................................................................................

    @Override
    public EnvironmentContext environmentContext() {
        return this.readOnlyEnvironmentContext;
    }

    private final EnvironmentContext readOnlyEnvironmentContext;

    private final ProviderContext context;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.context.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof ReadOnlyProviderContext &&
                this.equals0((ReadOnlyProviderContext) other));
    }

    private boolean equals0(final ReadOnlyProviderContext other) {
        return this.context.equals(other.context);
    }

    @Override
    public String toString() {
        return "ReadOnly " + this.context;
    }
}
