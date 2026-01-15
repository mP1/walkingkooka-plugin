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

import walkingkooka.convert.ConverterLike;
import walkingkooka.convert.ConverterLikeDelegator;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.text.LineEnding;

import java.util.Optional;

public interface ProviderContextDelegator extends ProviderContext,
    EnvironmentContextDelegator,
    ConverterLikeDelegator {

    @Override
    default ProviderContext setLineEnding(final LineEnding lineEnding) {
        this.environmentContext()
            .setLineEnding(lineEnding);
        return this;
    }

    @Override
    default ProviderContext setUser(final Optional<EmailAddress> user) {
        this.environmentContext()
            .setUser(user);
        return this;
    }

    @Override
    default <T> ProviderContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                    final T value) {
        this.environmentContext()
            .setEnvironmentValue(
                name,
                value
            );
        return this;
    }

    @Override
    default ProviderContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.environmentContext()
            .removeEnvironmentValue(name);
        return this;
    }

    @Override
    default PluginStore pluginStore() {
        return this.providerContext()
            .pluginStore();
    }

    ProviderContext providerContext();

    // EnvironmentContext...............................................................................................

    @Override
    default EnvironmentContext environmentContext() {
        return this.providerContext();
    }

    // ConverterLike....................................................................................................

    @Override
    default ConverterLike converterLike() {
        return this.providerContext();
    }
}
