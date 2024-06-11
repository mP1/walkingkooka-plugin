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
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderTestingTest.TestPluginInfo;
import walkingkooka.plugin.ProviderTestingTest.TestProvided;
import walkingkooka.plugin.ProviderTestingTest.TestProvider;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ProviderTestingTest implements ProviderTesting<TestProvider, StringName, TestPluginInfo, String, TestProvided> {

    private final static TestProvided PROVIDED_A1 = new TestProvided("a1");

    private final static TestProvided PROVIDED_B2 = new TestProvided("b2");

    private final static TestPluginInfo INFO1 = new TestPluginInfo("https://example.com/a1", PROVIDED_A1.value);

    private final static TestPluginInfo INFO2 = new TestPluginInfo("https://example.com/b2", PROVIDED_B2.value);

    @Test
    public void testGet() {
        this.getAndCheck(
                PROVIDED_A1.value,
                PROVIDED_A1
        );
    }

    @Test
    public void testGet2() {
        this.getAndCheck(
                PROVIDED_B2.value,
                PROVIDED_B2
        );
    }

    @Test
    public void testGetUnknown() {
        this.getAndCheck(
                "Unknown"
        );
    }

    @Test
    public void testInfos() {
        this.infosAndCheck(
                INFO1,
                INFO2
        );
    }

    @Override
    public ProviderTestingTest.TestProvider createProvider() {
        return new TestProvider();
    }

    static class TestProvider implements Provider<StringName, TestPluginInfo, String, TestProvided> {
        @Override
        public Optional<TestProvided> get(final String string) {
            if (string.equals(PROVIDED_A1.value)) {
                return Optional.of(PROVIDED_A1);
            }
            if (string.equals(PROVIDED_B2.value)) {
                return Optional.of(PROVIDED_B2);
            }
            return Optional.empty();
        }

        @Override
        public Set<TestPluginInfo> infos() {
            return Sets.of(
                    INFO1,
                    INFO2
            );
        }
    }

    static class TestPluginInfo implements PluginInfoLike<TestPluginInfo, StringName> {

        TestPluginInfo(final String url,
                       final String name) {
            this(
                    Url.parseAbsolute(url),
                    Names.string(name)
            );
        }

        TestPluginInfo(final AbsoluteUrl url,
                       final StringName name) {
            this.name = name;
            this.url = url;
        }

        @Override
        public StringName name() {
            return this.name;
        }

        private final StringName name;

        @Override
        public AbsoluteUrl url() {
            return this.url;
        }

        private final AbsoluteUrl url;

        @Override
        public int hashCode() {
            return Objects.hash(
                    this.name,
                    this.url
            );
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof TestPluginInfo && equals0((TestPluginInfo) other);
        }

        private boolean equals0(final TestPluginInfo other) {
            return this.name.equals(other.name) &&
                    this.url.equals(other.url);
        }

        @Override
        public String toString() {
            return PluginInfoLike.toString(this);
        }
    }

    static class TestProvided {

        TestProvided(final String value) {
            this.value = value;
        }

        private final String value;

        @Override
        public String toString() {
            return this.value;
        }
    }
}
