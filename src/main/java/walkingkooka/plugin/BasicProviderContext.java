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

import walkingkooka.convert.CanConvert;
import walkingkooka.convert.CanConvertDelegator;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.store.PluginStore;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link ProviderContext} that delegates to a {@link EnvironmentContext}.
 */
final class BasicProviderContext implements ProviderContext,
    CanConvertDelegator,
    EnvironmentContextDelegator {

    static BasicProviderContext with(final CanConvert canConvert,
                                     final EnvironmentContext environmentContext,
                                     final PluginStore pluginStore) {
        return new BasicProviderContext(
            Objects.requireNonNull(canConvert, "canConvert"),
            Objects.requireNonNull(environmentContext, "environmentContext"),
            Objects.requireNonNull(pluginStore, "pluginStore")
        );
    }

    private BasicProviderContext(final CanConvert canConvert,
                                 final EnvironmentContext environmentContext,
                                 final PluginStore pluginStore) {
        this.canConvert = canConvert;
        this.environmentContext = environmentContext;
        this.pluginStore = pluginStore;
    }

    @Override
    public PluginStore pluginStore() {
        return this.pluginStore;
    }

    private final PluginStore pluginStore;

    // CanConvertDelegator..............................................................................................

    @Override
    public CanConvert canConvert() {
        return this.canConvert;
    }

    private final CanConvert canConvert;

    // EnvironmentContext...............................................................................................

    @Override
    public ProviderContext cloneEnvironment() {
        final EnvironmentContext environmentContext = this.environmentContext;
        final EnvironmentContext cloned = environmentContext.cloneEnvironment();

        return environmentContext.equals(cloned) ?
            this :
            with(
                this.canConvert,
                cloned,
                this.pluginStore
            );
    }

    // EnvironmentContextDelegator......................................................................................

    @Override
    public EnvironmentContext environmentContext() {
        return this.environmentContext;
    }

    private final EnvironmentContext environmentContext;

    @Override
    public Locale locale() {
        return this.environmentContext.locale();
    }

    @Override
    public ProviderContext setLocale(final Locale locale) {
        return this.setEnvironmentValue(
            EnvironmentValueName.LOCALE,
            locale
        );
    }

    @Override
    public ProviderContext setUser(final Optional<EmailAddress> user) {
        return user.isPresent() ?
            this.setEnvironmentValue(
                EnvironmentValueName.USER,
                user.orElse(null)
            ) :
            this.removeEnvironmentValue(
                EnvironmentValueName.USER
            );
    }

    @Override
    public <T> ProviderContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                   final T value) {
        this.environmentContext.setEnvironmentValue(
            name,
            value
        );
        return this;
    }

    @Override
    public ProviderContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.environmentContext.removeEnvironmentValue(name);
        return this;
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.environmentContext.toString();
    }
}
