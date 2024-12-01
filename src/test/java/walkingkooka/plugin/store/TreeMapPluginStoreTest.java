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

package walkingkooka.plugin.store;

import walkingkooka.Binary;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.PluginName;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

public final class TreeMapPluginStoreTest implements PluginStoreTesting<TreeMapPluginStore> {

    private final static PluginName PLUGIN_NAME = PluginName.with("TestPlugin123");

    @Override
    public void testAddSaveWatcherAndSaveTwiceFiresOnce() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TreeMapPluginStore createStore() {
        return TreeMapPluginStore.empty();
    }

    @Override
    public PluginName id() {
        return PLUGIN_NAME;
    }

    @Override
    public Plugin value() {
        return Plugin.with(
                PLUGIN_NAME,
                "example.jar",
                Binary.with("Hello".getBytes(Charset.defaultCharset())),
                EmailAddress.parse("user@example.com"),
                LocalDateTime.of(
                        1999,
                        12,
                        31,
                        12,
                        58
                )
        );
    }

    // class............................................................................................................

    @Override
    public Class<TreeMapPluginStore> type() {
        return TreeMapPluginStore.class;
    }
}
