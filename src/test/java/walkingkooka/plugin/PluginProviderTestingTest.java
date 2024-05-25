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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;

import java.util.Objects;
import java.util.Set;

public final class PluginProviderTestingTest implements PluginProviderTesting {

    private final static PluginProviderName PLUGIN_PROVIDER_NAME = PluginProviderName.with("TestPluginProvider567");

    private final static PluginName PLUGIN_NAME1 = PluginName.with("Plugin-1");

    private final static PluginName PLUGIN_NAME2 = PluginName.with("Plugin-2");

    private final static PluginInfo PLUGIN_INFO1 = PluginInfo.with(
            Url.parseAbsolute("https://example.com/plugin-1"),
            PLUGIN_NAME1
    );

    private final static PluginInfo PLUGIN_INFO2 = PluginInfo.with(
            Url.parseAbsolute("https://example.com/plugin-2"),
            PLUGIN_NAME2
    );

    private final static TestPlugin1 PLUGIN_1 = new TestPlugin1();

    private final static TestPlugin2 PLUGIN_2 = new TestPlugin2();

    @Test
    public void testName() {
        this.nameAndCheck(
                PLUGIN_PROVIDER_NAME
        );
    }

    @Test
    public void testPlugin() {
        this.pluginAndCheck(
                PLUGIN_NAME1,
                TestPlugin1.class,
                PLUGIN_1
        );
    }

    @Test
    public void testPlugin2() {
        this.pluginAndCheck(
                PLUGIN_NAME2,
                TestPlugin2.class,
                PLUGIN_2
        );
    }

    @Test
    public void testPlugins() {
        this.pluginsAndCheck(
                TestPlugin1.class,
                PLUGIN_1
        );
    }

    @Test
    public void testPlugins2() {
        this.pluginsAndCheck(
                TestPlugin2.class,
                PLUGIN_2
        );
    }

    @Test
    public void testPluginInfos() {
        this.pluginInfosAndCheck(
                PLUGIN_INFO1,
                PLUGIN_INFO2
        );
    }

    @Override
    public PluginProvider createPluginProvider() {
        return new PluginProvider() {

            @Override
            public PluginProviderName name() {
                return PLUGIN_PROVIDER_NAME;
            }

            @Override
            public <T> T plugin(final PluginName name,
                                final Class<T> type) {
                Objects.requireNonNull(name, "name");
                Objects.requireNonNull(type, "type");

                if (PLUGIN_NAME1.equals(name)) {
                    return type.cast(PLUGIN_1);
                }
                if (PLUGIN_NAME2.equals(name)) {
                    return type.cast(PLUGIN_2);
                }
                throw new IllegalArgumentException("Unknown plugin " + name + " " + type.getName());
            }

            @Override
            public <T> Set<T> plugins(final Class<T> type) {
                Objects.requireNonNull(type, "type");

                switch (type.getSimpleName()) {
                    case "TestPlugin1":
                        return Cast.to(
                                Sets.of(PLUGIN_1)
                        );
                    case "TestPlugin2":
                        return Cast.to(
                                Sets.of(PLUGIN_2)
                        );
                    default:
                        throw new UnsupportedOperationException("Unknown type " + type.getName());
                }
            }

            @Override
            public Set<PluginInfo> pluginInfos() {
                return Sets.of(
                        PLUGIN_INFO1,
                        PLUGIN_INFO2
                );
            }
        };
    }

    static class TestPlugin1 {

    }

    static class TestPlugin2 {

    }
}
