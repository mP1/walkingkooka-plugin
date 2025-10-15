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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginProviderTestingTest.TestPluginProvider;

import java.util.Set;

public final class PluginProviderTestingTest implements PluginProviderTesting<TestPluginProvider> {

    private final static PluginProviderName PLUGIN_PROVIDER_NAME = PluginProviderName.with("TestPluginProvider567");

    private final static AbsoluteUrl PLUGIN_PROVIDER_URL = Url.parseAbsolute("https://example.com/PluginProviderTestingTest");

    private final static PluginName PLUGIN_NAME1 = PluginName.with("plugin-1");

    private final static PluginName PLUGIN_NAME2 = PluginName.with("plugin-2");

    private final static PluginInfo<PluginName> PLUGIN_INFO1 = PluginInfo.with(
        Url.parseAbsolute("https://example.com/plugin-1"),
        PLUGIN_NAME1
    );

    private final static PluginInfo<PluginName> PLUGIN_INFO2 = PluginInfo.with(
        Url.parseAbsolute("https://example.com/plugin-2"),
        PLUGIN_NAME2
    );

    private final static TestPlugin1 PLUGIN_1 = new TestPlugin1();

    private final static TestPlugin2 PLUGIN_2 = new TestPlugin2();

    @Test
    public void testName() {
        this.nameAndCheck(
            this.createPluginProvider(),
            PLUGIN_PROVIDER_NAME
        );
    }

    @Test
    public void testPluginInfos() {
        this.pluginInfosAndCheck(
            PLUGIN_INFO1,
            PLUGIN_INFO2
        );
    }

    @Test
    public void testUrl() {
        this.urlAndCheck(
            this.createPluginProvider(),
            PLUGIN_PROVIDER_URL
        );
    }

    @Override
    public TestPluginProvider createPluginProvider() {
        return new TestPluginProvider();
    }

    static class TestPluginProvider implements PluginProvider {

        @Override
        public PluginProviderName name() {
            return PLUGIN_PROVIDER_NAME;
        }

        @Override
        public Set<PluginInfo> pluginInfos() {
            return Sets.of(
                PLUGIN_INFO1,
                PLUGIN_INFO2
            );
        }

        @Override
        public AbsoluteUrl url() {
            return PLUGIN_PROVIDER_URL;
        }
    }

    static class TestPlugin1 {

    }

    static class TestPlugin2 {

    }
}
