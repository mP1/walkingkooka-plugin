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

import walkingkooka.net.AbsoluteUrl;

import java.util.Set;

/**
 * A provider that gives plugins with methods to fetch by {@link PluginName} or share {@link PluginInfo}.
 */
public interface PluginProvider extends PluginProviderLike<PluginProviderName> {

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
    @Override
    PluginProviderName name();

    /**
     * This url is not an address to an actual web page or other web resource, but rather is intended as a means of
     * uniquely identify this provider. An example of this utility is it will be used to identify and remove an old {@link PluginProviderName}
     * when a new form is uploaded with the same url.
     * <br>
     * Version info should be included in the {@link #name}.
     */
    @Override
    AbsoluteUrl url();
}
