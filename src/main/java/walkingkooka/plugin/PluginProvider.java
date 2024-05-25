/*
 * Copyright 2020 Miroslav Pokorny (github.com/mP1)
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

import java.util.Set;

/**
 * A provider that gives plugins with methods to fetch by {@link PluginName} or share {@link PluginInfo}.
 */
public interface PluginProvider {

    /**
     * Fetches the plugin with the given {@link PluginName} and {@link Class type}.
     */
    <T> T plugin(final PluginName name,
                 final Class<T> type);

    /**
     * Returns all the plugins of the given {@link Class type}.
     */
    <T> Set<T> plugins(final Class<T> type);


    /**
     * Returns the {@link PluginInfo} for all plugins.
     */
    Set<PluginInfo> pluginInfos();

    /**
     * Returns the name of this provider. This might include some version numbering info.
     */
    PluginProviderName name();
}
