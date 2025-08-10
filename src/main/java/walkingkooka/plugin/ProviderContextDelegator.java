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
import walkingkooka.plugin.store.PluginStore;

public interface ProviderContextDelegator extends ProviderContext,
    EnvironmentContextDelegator,
    CanConvertDelegator {

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

    // CanConvert.......................................................................................................

    @Override
    default CanConvert canConvert() {
        return this.providerContext();
    }
}
