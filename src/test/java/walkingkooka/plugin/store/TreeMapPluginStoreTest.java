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

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.PluginName;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

public final class TreeMapPluginStoreTest implements PluginStoreTesting<TreeMapPluginStore> {

    private final static PluginName PLUGIN_NAME = PluginName.with("test-plugin-123");

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
        return plugin(PLUGIN_NAME.value());
    }

    // filter...........................................................................................................

    @Test
    public void testFilterWithEmptyQuery() {
        final Plugin plugin1 = plugin("plugin-111");

        final TreeMapPluginStore store = TreeMapPluginStore.empty();
        store.save(plugin1);

        this.filterAndCheck(
            store,
            "",
            0,
            2
        );
    }

    @Test
    public void testFilterWithMatchesAllQuery() {
        final Plugin plugin1 = plugin("plugin-111");
        final Plugin plugin2 = plugin("plugin-222");

        final TreeMapPluginStore store = TreeMapPluginStore.empty();
        store.save(plugin1);
        store.save(plugin2);

        this.filterAndCheck(
            store,
            "*",
            0,
            2,
            plugin1,
            plugin2
        );
    }

    @Test
    public void testFilterWithMatchesAllOffset() {
        final Plugin plugin1 = plugin("plugin-111");
        final Plugin plugin2 = plugin("plugin-222");

        final TreeMapPluginStore store = TreeMapPluginStore.empty();
        store.save(plugin1);
        store.save(plugin2);

        this.filterAndCheck(
            store,
            "*",
            1,
            2,
            plugin2
        );
    }

    @Test
    public void testFilterWithMatchesAllCount() {
        final Plugin plugin1 = plugin("plugin-111");
        final Plugin plugin2 = plugin("plugin-222");

        final TreeMapPluginStore store = TreeMapPluginStore.empty();
        store.save(plugin1);
        store.save(plugin2);

        this.filterAndCheck(
            store,
            "*",
            0,
            1,
            plugin1
        );
    }

    @Test
    public void testFilterWithFilteringQueryOffsetCount() {
        final Plugin plugin1 = plugin("plugin-111");
        final Plugin plugin2 = plugin("plugin-222");
        final Plugin plugin3 = plugin("plugin-333");
        final Plugin plugin4 = plugin("plugin-444");
        final Plugin plugin5 = plugin("plugin-555");
        final Plugin plugin6 = plugin("plugin-666");

        final TreeMapPluginStore store = TreeMapPluginStore.empty();
        store.save(plugin1);
        store.save(plugin2);
        store.save(plugin3);
        store.save(plugin4);
        store.save(plugin5);
        store.save(plugin6);

        this.filterAndCheck(
            store,
            "*2* *4* *6*",
            1,
            2,
            plugin4,
            plugin6
        );
    }

    private static Plugin plugin(final String name) {
        return Plugin.with(
            PluginName.with(name),
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
