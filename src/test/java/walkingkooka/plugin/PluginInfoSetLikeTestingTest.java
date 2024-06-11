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
import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginInfoSetLikeTestingTest.TestPluginInfo;
import walkingkooka.plugin.PluginInfoSetLikeTestingTest.TestPluginInfoSet;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public final class PluginInfoSetLikeTestingTest implements PluginInfoSetLikeTesting<TestPluginInfoSet, TestPluginInfo, StringName> {

    // keepSameUrl......................................................................................................

    @Test
    public void testKeepSameUrl() {
        final TestPluginInfo info1 = new TestPluginInfo(
                "https://example.com/test-111",
                "test-111"
        );
        final TestPluginInfo info2 = new TestPluginInfo(
                "https://example.com/test-222",
                "test-222"
        );
        final TestPluginInfo info3 = new TestPluginInfo(
                "https://example.com/test-333",
                "test-333"
        );

        this.checkEquals(
                new TestPluginInfoSet(
                        Sets.of(
                                info1,
                                info2
                        )
                ),
                new TestPluginInfoSet(
                        Sets.of(
                                info1,
                                info2,
                                info3
                        )
                ).keepSameUrl(
                        new TestPluginInfoSet(
                                Sets.of(
                                        info1,
                                        info2
                                )
                        )
                )
        );
    }

    @Test
    public void testKeepSameUrlDifferentNames() {
        final TestPluginInfo info1 = new TestPluginInfo(
                "https://example.com/test-111",
                "test-111"
        );
        final TestPluginInfo info2 = new TestPluginInfo(
                "https://example.com/test-222",
                "test-222"
        );
        final TestPluginInfo info3 = new TestPluginInfo(
                "https://example.com/test-222",
                "test-222"
        );

        this.checkEquals(
                new TestPluginInfoSet(
                        Sets.of(
                                info1,
                                info2
                        )
                ),
                new TestPluginInfoSet(
                        Sets.of(
                                info1,
                                info2,
                                info3
                        )
                ).keepSameUrl(
                        new TestPluginInfoSet(
                                Sets.of(
                                        new TestPluginInfo(
                                                "https://example.com/test-111",
                                                "test-original-111"
                                        ),
                                        new TestPluginInfo(
                                                "https://example.com/test-222",
                                                "test-original-222"
                                        )
                                )
                        )
                )
        );
    }

    @Test
    public void testKeepSameUrlFiltered() {
        final TestPluginInfo info1 = new TestPluginInfo(
                "https://example.com/test-111",
                "test-111"
        );
        final TestPluginInfo info2 = new TestPluginInfo(
                "https://example.com/test-222",
                "test-222"
        );
        final TestPluginInfo info3 = new TestPluginInfo(
                "https://example.com/test-222",
                "test-222"
        );

        this.checkEquals(
                new TestPluginInfoSet(
                        Sets.of(
                                info1
                        )
                ),
                new TestPluginInfoSet(
                        Sets.of(
                                info1,
                                info2,
                                info3
                        )
                ).keepSameUrl(
                        new TestPluginInfoSet(
                                Sets.of(
                                        new TestPluginInfo(
                                                "https://example.com/test-111",
                                                "test-original-111"
                                        )
                                )
                        )
                )
        );
    }

    // parse............................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseInfoFails() {
        final String text = "https://example.com/test-123 test/123";
        this.parseStringInvalidCharacterFails(
                text,
                text.lastIndexOf('/')
        );
    }

    @Test
    public void testParseInfoMissingNameFails() {
        final String text = "https://example.com/test-111 test-111,https://example.com/test-222";
        this.parseStringFails(
                text,
                new IllegalArgumentException("Missing name")
        );
    }

    @Test
    public void testParseOne() {
        this.parseStringAndCheck(
                "https://example.com/test-123 test-123",
                new TestPluginInfoSet(
                        Sets.of(
                                new TestPluginInfo(
                                        "https://example.com/test-123",
                                        "test-123"
                                )
                        )
                )
        );
    }

    @Test
    public void testParseSecondInfoFails() {
        final String text = "https://example.com/test-111 test-111,https://example.com/test-222 test/222";
        this.parseStringInvalidCharacterFails(
                text,
                text.lastIndexOf('/')
        );
    }

    @Test
    public void testParseMany() {
        this.parseStringAndCheck(
                "https://example.com/test-111 test-111,https://example.com/test-222 test-222",
                new TestPluginInfoSet(
                        Sets.of(
                                new TestPluginInfo(
                                        "https://example.com/test-111",
                                        "test-111"
                                ),
                                new TestPluginInfo(
                                        "https://example.com/test-222",
                                        "test-222"
                                )
                        )
                )
        );
    }

    @Test
    public void testParseMany3() {
        this.parseStringAndCheck(
                "https://example.com/test-111 test-111,https://example.com/test-222 test-222,https://example.com/test-333 test-333",
                new TestPluginInfoSet(
                        Sets.of(
                                new TestPluginInfo(
                                        "https://example.com/test-111",
                                        "test-111"
                                ),
                                new TestPluginInfo(
                                        "https://example.com/test-222",
                                        "test-222"
                                ),
                                new TestPluginInfo(
                                        "https://example.com/test-333",
                                        "test-333"
                                )
                        )
                )
        );
    }

    @Override
    public TestPluginInfoSet parseString(final String text) {
        return TestPluginInfoSet.parse(text);
    }

    // Set..............................................................................................................

    @Override
    public TestPluginInfoSet createSet() {
        return new TestPluginInfoSet(
                Sets.of(
                        new TestPluginInfo(
                                "https://example.com/test-plugin-1",
                                "test-plugin-1"
                        ),
                        new TestPluginInfo(
                                "https://example.com/test-plugin-2",
                                "test-plugin-2"
                        )
                )
        );
    }

    @Override
    public TestPluginInfoSet unmarshall(final JsonNode node,
                                        final JsonNodeUnmarshallContext context) {
        return TestPluginInfoSet.unmarshall(
                node,
                context
        );
    }

    @Override
    public Class<TestPluginInfoSet> type() {
        return TestPluginInfoSet.class;
    }

    static class TestPluginInfoSet extends AbstractSet<TestPluginInfo> implements PluginInfoSetLike<TestPluginInfo, StringName> {

        static TestPluginInfoSet parse(final String text) {
            return PluginInfoSetLike.parse(
                    text,
                    TestPluginInfo::parse,
                    TestPluginInfoSet::new
            );
        }

        TestPluginInfoSet(final Set<TestPluginInfo> set) {
            this.set = set;
        }

        @Override
        public Iterator<TestPluginInfo> iterator() {
            return Iterators.readOnly(this.set.iterator());
        }

        @Override
        public int size() {
            return this.set.size();
        }

        private final Set<TestPluginInfo> set;

        // json.............................................................................................................

        static {
            JsonNodeContext.register(
                    JsonNodeContext.computeTypeName(TestPluginInfoSet.class),
                    TestPluginInfoSet::unmarshall,
                    TestPluginInfoSet::marshall,
                    TestPluginInfoSet.class
            );
        }

        private JsonNode marshall(final JsonNodeMarshallContext context) {
            return context.marshallCollection(this);
        }

        // @VisibleForTesting
        static TestPluginInfoSet unmarshall(final JsonNode node,
                                            final JsonNodeUnmarshallContext context) {
            return new TestPluginInfoSet(
                    context.unmarshallSet(
                            node,
                            TestPluginInfo.class
                    )
            );
        }

        // toString.....................................................................................................

        @Override
        public String toString() {
            return PluginInfoSetLike.toString(this);
        }
    }

    static class TestPluginInfo implements PluginInfoLike<TestPluginInfo, StringName> {

        static TestPluginInfo parse(final String text) {
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

        static {
            JsonNodeContext.register(
                    JsonNodeContext.computeTypeName(TestPluginInfo.class),
                    TestPluginInfo::unmarshall,
                    TestPluginInfo::marshall,
                    TestPluginInfo.class
            );
        }

        // @VisibleForTesting
        static TestPluginInfo unmarshall(final JsonNode node,
                                         final JsonNodeUnmarshallContext context) {
            return PluginInfoLike.unmarshall(
                    node,
                    context,
                    StringName.class,
                    TestPluginInfo::new
            );
        }
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
             this.createSet(),
             "TestPluginInfoSet\n" +
                     "  https://example.com/test-plugin-1 test-plugin-1\n" +
                     "  https://example.com/test-plugin-2 test-plugin-2\n"
        );
    }
}
