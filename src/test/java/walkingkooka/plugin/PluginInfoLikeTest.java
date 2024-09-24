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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginInfoLikeTest.TestPluginInfo;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Set;

public final class PluginInfoLikeTest implements PluginInfoLikeTesting<TestPluginInfo, StringName> {

    // name.............................................................................................................

    @Test
    public void testSort() {
        final TestPluginInfo info1 = new TestPluginInfo(
                Url.parseAbsolute("https://example.com/1"),
                Names.string("plugin1")
        );
        final TestPluginInfo info2 = new TestPluginInfo(
                Url.parseAbsolute("https://example.com/2"),
                Names.string("plugin2")
        );
        final TestPluginInfo info3 = new TestPluginInfo(
                Url.parseAbsolute("https://example.com/different-1"),
                Names.string("plugin1")
        );

        final Set<TestPluginInfo> infos = SortedSets.tree();
        infos.add(info1);
        infos.add(info2);
        infos.add(info3);

        this.checkEquals(
                Lists.of(
                        info1,
                        info3,
                        info2
                ),
                Lists.of(
                        infos.toArray()
                )
        );
    }

    @Override
    public StringName createName(final String value) {
        return Names.string(value);
    }

    @Override
    public TestPluginInfo createPluginInfoLike(final AbsoluteUrl url,
                                               final StringName name) {
        return new TestPluginInfo(
                Objects.requireNonNull(url, "url"),
                Objects.requireNonNull(name, "name")
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseEmptyFails() {
        this.parseStringFails(
                "",
                new IllegalArgumentException("Missing url")
        );
    }

    @Test
    public void testParseMissingUrlFails() {
        this.parseStringFails(
                " ",
                new IllegalArgumentException("Missing url")
        );
    }

    @Test
    public void testParseUrlFails() {
        this.parseStringFails(
                " https://example.com/1",
                new IllegalArgumentException("Missing name")
        );
    }

    @Test
    public void testParseUrlSpaceName() {
        this.parseStringAndCheck(
                "https://example.com/1 plugin1",
                new TestPluginInfo(
                        Url.parseAbsolute("https://example.com/1"),
                        Names.string("plugin1")
                )
        );
    }

    @Test
    public void testParseUrlIncludesCommaSpaceName() {
        this.parseStringAndCheck(
                "https://example.com/1,2 plugin1",
                new TestPluginInfo(
                        Url.parseAbsolute("https://example.com/1,2"),
                        Names.string("plugin1")
                )
        );
    }

    @Test
    public void testParseSpaceUrlSpaceName() {
        this.parseStringAndCheck(
                " https://example.com/1 plugin1",
                new TestPluginInfo(
                        Url.parseAbsolute("https://example.com/1"),
                        Names.string("plugin1")
                )
        );
    }

    @Test
    public void testParseSpaceSpaceUrlSpaceName() {
        this.parseStringAndCheck(
                "  https://example.com/1 plugin1",
                new TestPluginInfo(
                        Url.parseAbsolute("https://example.com/1"),
                        Names.string("plugin1")
                )
        );
    }

    @Test
    public void testParseSpaceSpaceUrlSpaceSpaceName() {
        this.parseStringAndCheck(
                "  https://example.com/1  plugin1",
                new TestPluginInfo(
                        Url.parseAbsolute("https://example.com/1"),
                        Names.string("plugin1")
                )
        );
    }

    @Test
    public void testParseSpaceSpaceUrlSpaceSpaceNameSpace() {
        this.parseStringAndCheck(
                "  https://example.com/1  plugin1 ",
                new TestPluginInfo(
                        Url.parseAbsolute("https://example.com/1"),
                        Names.string("plugin1")
                )
        );
    }

    @Test
    public void testParseSpaceSpaceUrlSpaceSpaceNameSpaceSpace() {
        this.parseStringAndCheck(
                "  https://example.com/1  plugin1  ",
                new TestPluginInfo(
                        Url.parseAbsolute("https://example.com/1"),
                        Names.string("plugin1")
                )
        );
    }

    @Test
    public void testParseUrlSpaceNameSpaceTokenFails() {
        this.parseStringInvalidCharacterFails(
                " https://example.com/1 name X",
                'X'
        );
    }


    @Override
    public TestPluginInfo parseString(final String text) {
        return TestPluginInfo.parse(text);
    }

    // class............................................................................................................

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<TestPluginInfo> type() {
        return TestPluginInfo.class;
    }

    // json.............................................................................................................

    @Override
    public TestPluginInfo unmarshall(final JsonNode json,
                                     final JsonNodeUnmarshallContext context) {
        return TestPluginInfo.unmarshall(
                json,
                context
        );
    }

    public static class TestPluginInfo implements PluginInfoLike<TestPluginInfo, StringName> {

        public static TestPluginInfo parse(final String text) {
            return PluginInfoLike.parse(
                    text,
                    Names::string,
                    TestPluginInfo::new
            );
        }

        TestPluginInfo(final AbsoluteUrl url,
                       final StringName name) {
            this.url = url;
            this.name = name;
        }

        TestPluginInfo(final String url,
                       final String name) {
            this(
                    Url.parseAbsolute(url),
                    Names.string(name)
            );
        }

        @Override
        public StringName name() {
            return this.name;
        }

        @Override
        public TestPluginInfo setName(final StringName name) {
            Objects.requireNonNull(name, "name");

            return this.name.equals(name) ?
                    this :
                    new TestPluginInfo(
                            this.url,
                            name
                    );
        }

        private final StringName name;

        @Override
        public AbsoluteUrl url() {
            return this.url;
        }

        private final AbsoluteUrl url;

        // object.......................................................................................................

        @Override
        public int hashCode() {
            return Objects.hash(
                    this.url,
                    this.name
            );
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof TestPluginInfo && this.equals0((TestPluginInfo) other);
        }

        private boolean equals0(final TestPluginInfo other) {
            return this.url.equals(other.url) &&
                    this.name.equals(other.name);
        }

        @Override
        public String toString() {
            return PluginInfoLike.toString(this);
        }

        // json.........................................................................................................

        // @VisibleForTesting
        static TestPluginInfo unmarshall(final JsonNode node,
                                         final JsonNodeUnmarshallContext context) {
            return PluginInfoLike.unmarshall(
                    node,
                    context,
                    Names::string,
                    TestPluginInfo::new
            );
        }
    }

    @BeforeAll
    public static void beforeAll() {
        unregister = JsonNodeContext.register(
                JsonNodeContext.computeTypeName(TestPluginInfo.class),
                TestPluginInfo::unmarshall,
                TestPluginInfo::marshall,
                TestPluginInfo.class
        );
    }

    private static Runnable unregister;

    @AfterAll
    public static void afterAll() {
        if(null != unregister) {
            unregister.run();
        }
    }
}
