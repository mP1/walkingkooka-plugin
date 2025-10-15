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

import walkingkooka.collect.list.Lists;
import walkingkooka.environment.EnvironmentStartup;
import walkingkooka.net.NetStartup;
import walkingkooka.net.Url;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.tree.json.marshall.JsonNodeContext;

/**
 * Used to force all values types to {@link JsonNodeContext#register}
 */
public final class PluginStartup implements PublicStaticHelper {

    static {
        EnvironmentStartup.init();
        NetStartup.init();

        try {
            PluginAliasSet.with(
                null,
                null
            );
        } catch (final NullPointerException ignore) {
            // NOP
        }

        // register json marshallers/unmarshallers.
        final PluginName pluginName = PluginName.with("hello");

        PluginInfo.with(
            Url.parseAbsolute("https://example.com/Hello"),
            pluginName
        );

        try {
            PluginInfoSet.with(
                null
            );
        } catch (final NullPointerException ignore) {
            // NOP
        }

        PluginNameSet.with(
            Lists.of(pluginName)
        );

        PluginSelector.with(
            pluginName,
            "Hello"
        );
    }

    public static void init() {
        // NOP
    }

    private PluginStartup() {
        throw new UnsupportedOperationException();
    }
}
