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
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
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

    @Override
    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        return this.context.environmentValue(name);
    }

    @Override
    public Set<EnvironmentValueName<?>> environmentValueNames() {
        return this.context.environmentValueNames();
    }

    @Override
    public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                        final T value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");

        throw name.readOnlyEnvironmentValueException();
    }

    @Override
    public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
        Objects.requireNonNull(name, "name");

        throw name.readOnlyEnvironmentValueException();
    }

    @Override
    public Optional<EmailAddress> user() {
        return this.context.user();
    }

    @Override
    public void setUser(final Optional<EmailAddress> user) {
        Objects.requireNonNull(user, "user");

        throw USER.readOnlyEnvironmentValueException();
    }

    @Override
    public LocalDateTime now() {
        return this.context.now();
    }

    @Override
    public Currency currency() {
        return this.context.currency();
    }

    @Override
    public void setCurrency(final Currency currency) {
        Objects.requireNonNull(currency, "currency");

        throw CURRENCY.readOnlyEnvironmentValueException();
    }
    
    @Override
    public Indentation indentation() {
        return this.context.indentation();
    }

    @Override
    public void setIndentation(final Indentation indentation) {
        Objects.requireNonNull(indentation, "indentation");

        throw INDENTATION.readOnlyEnvironmentValueException();
    }
    
    @Override
    public LineEnding lineEnding() {
        return this.context.lineEnding();
    }

    @Override
    public void setLineEnding(final LineEnding lineEnding) {
        Objects.requireNonNull(lineEnding, "lineEnding");

        throw LINE_ENDING.readOnlyEnvironmentValueException();
    }
    
    @Override
    public Locale locale() {
        return this.context.locale();
    }

    @Override
    public void setLocale(final Locale locale) {
        Objects.requireNonNull(locale, "locale");

        throw LOCALE.readOnlyEnvironmentValueException();
    }

    @Override
    public ZoneOffset timeOffset() {
        return this.context.timeOffset();
    }

    @Override
    public void setTimeOffset(final ZoneOffset timeOffset) {
        Objects.requireNonNull(timeOffset, "timeOffset");

        throw TIME_OFFSET.readOnlyEnvironmentValueException();
    }

    @Override
    public PluginStore pluginStore() {
        return this.context.pluginStore();
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
