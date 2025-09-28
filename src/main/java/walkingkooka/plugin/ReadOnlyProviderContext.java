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
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.store.PluginStore;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link ProviderContext} where all setXXX and removeXXX methods throw {@link UnsupportedOperationException}.
 * Note the {@link #cloneEnvironment()} returns a clone of the wrapped {@link ProviderContext}.
 */
final class ReadOnlyProviderContext implements ProviderContext {

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
    }

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

    @Override
    public ProviderContext cloneEnvironment() {
        return this.context.cloneEnvironment();
    }

    @Override
    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        return this.context.environmentValue(name);
    }

    @Override
    public Set<EnvironmentValueName<?>> environmentValueNames() {
        return this.context.environmentValueNames();
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
    public Optional<EmailAddress> user() {
        return this.context.user();
    }

    @Override
    public ProviderContext setUser(final Optional<EmailAddress> user) {
        Objects.requireNonNull(user, "user");

        throw new UnsupportedOperationException();
    }

    @Override
    public LocalDateTime now() {
        return this.context.now();
    }

    @Override
    public Locale locale() {
        return this.context.locale();
    }

    @Override
    public ProviderContext setLocale(final Locale locale) {
        Objects.requireNonNull(locale, "locale");

        throw new UnsupportedOperationException();
    }

    @Override
    public PluginStore pluginStore() {
        return this.context.pluginStore();
    }

    private final ProviderContext context;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "ReadOnly " + this.context;
    }
}
