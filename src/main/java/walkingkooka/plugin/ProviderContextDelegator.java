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
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.plugin.store.PluginStore;

public interface ProviderContextDelegator extends ProviderContext,
        EnvironmentContextDelegator {

    @Override
    default EnvironmentContext environmentContext() {
        return this.providerContext();
    }

    @Override
    default PluginStore pluginStore() {
        return this.providerContext()
                .pluginStore();
    }

    ProviderContext providerContext();
}
