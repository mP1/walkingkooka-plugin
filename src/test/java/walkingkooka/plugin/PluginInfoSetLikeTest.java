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
import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.ImmutableSetDefaults;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Name;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginInfoSetLikeTest.TestPluginInfo;
import walkingkooka.plugin.PluginInfoSetLikeTest.TestPluginInfoSet;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginInfoSetLikeTest implements PluginInfoSetLikeTesting<TestPluginInfoSet, TestPluginInfo, StringName> {

    private final static List<Runnable> unregister = Lists.array();

    @BeforeAll
    public static void beforeAll() {
        unregister.add(
                JsonNodeContext.register(
                        JsonNodeContext.computeTypeName(TestPluginInfoSet.class),
                        TestPluginInfoSet::unmarshall,
                        TestPluginInfoSet::marshall,
                        TestPluginInfoSet.class
                )
        );

        unregister.add(
                JsonNodeContext.register(
                        JsonNodeContext.computeTypeName(TestPluginInfo.class),
                        TestPluginInfo::unmarshall,
                        TestPluginInfo::marshall,
                        TestPluginInfo.class
                )
        );
    }

    @AfterAll
    public static void afterAll() {
        unregister.forEach(Runnable::run);
    }

    // filter............................................................................................................

    static <S extends PluginInfoSetLike<S, I, N>, I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> S delete(final S s,
                                                                                                                           final I i) {
        return s.delete(
                i
        );
    }

    @Test
    public void testFilterWithNullProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> new TestPluginInfoSet(
                        Sets.empty()
                ).filter(null)
        );
    }

    @Test
    public void testFilterWithSame() {
        final TestPluginInfoSet set = this.createSet();

        this.checkEquals(
                set,
                set.filter(set)
        );
    }

    @Test
    public void testFilterLess() {
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

        this.filterAndCheck(
                Sets.of(
                        info1,
                        info2
                ),
                Sets.of(
                        info1,
                        info2,
                        info3
                ),
                info1,
                info2
        );
    }

    @Test
    public void testFilterDifferentNames() {
        final TestPluginInfo info1 = new TestPluginInfo(
                "https://example.com/test-111",
                "test-111"
        );
        final TestPluginInfo info2 = new TestPluginInfo(
                "https://example.com/test-222",
                "test-222"
        );

        this.filterAndCheck(
                Sets.of(
                        info1,
                        info2
                ),
                Sets.of(
                        new TestPluginInfo(
                                "https://example.com/test-111",
                                "test-original-111"
                        ),
                        new TestPluginInfo(
                                "https://example.com/test-222",
                                "test-original-222"
                        )
                ),
                info1,
                info2
        );
    }

    @Test
    public void testFilterMoreSelectedRemoved() {
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

        this.filterAndCheck(
                Sets.of(
                        info1,
                        info2,
                        info3
                ),
                Sets.of(
                        info1,
                        info2
                ),
                info1,
                info2
        );
    }

    private void filterAndCheck(final Set<TestPluginInfo> selected,
                                final Set<TestPluginInfo> provider,
                                final TestPluginInfo... expected) {
        this.filterAndCheck(
                selected,
                provider,
                Sets.of(
                        expected
                )
        );
    }

    private void filterAndCheck(final Set<TestPluginInfo> selected,
                                final Set<TestPluginInfo> provider,
                                final Set<TestPluginInfo> expected) {
        this.checkEquals(
                expected,
                new TestPluginInfoSet(
                        selected
                ).filter(
                        new TestPluginInfoSet(
                                provider
                        )
                ),
                () -> selected + " filter " + provider
        );
    }

    // merge............................................................................................................

    @Test
    public void testMergedWithNullViewFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginInfoSetLike.<TestPluginInfo, StringName>merge(
                        null, // view
                        Sets.empty() // target
                )
        );
    }

    @Test
    public void testMergedWithNullTargetFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginInfoSetLike.<TestPluginInfo, StringName>merge(
                        Sets.empty(), // view
                        null // target
                )
        );
    }

    @Test
    public void testMergedWithSame() {
        final TestPluginInfoSet set = this.createSet();

        this.checkEquals(
                set,
                PluginInfoSetLike.merge(
                        set,
                        this.createSet()
                )
        );
    }

    @Test
    public void testMerged() {
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

        this.mergeAndCheck(
                Sets.of(
                        info1,
                        info2,
                        info3
                ),
                Sets.of(
                        info1,
                        info2
                ),
                info1,
                info2,
                info3
        );
    }

    @Test
    public void testMergedDifferentNames() {
        final TestPluginInfo info1 = new TestPluginInfo(
                "https://example.com/test-111",
                "test-111"
        );
        final TestPluginInfo info2 = new TestPluginInfo(
                "https://example.com/test-222",
                "test-222"
        );

        this.mergeAndCheck(
                Sets.of(
                        info1,
                        info2
                ),
                Sets.of(
                        new TestPluginInfo(
                                "https://example.com/test-111",
                                "test-original-111"
                        ),
                        new TestPluginInfo(
                                "https://example.com/test-222",
                                "test-original-222"
                        )
                ),
                info1,
                info2
        );
    }

    @Test
    public void testMergedSomeReplacedSomeAdded() {
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

        this.mergeAndCheck(
                Sets.of(
                        info1,
                        info2
                ),
                Sets.of(
                        new TestPluginInfo(
                                "https://example.com/test-111",
                                "test-original-111"
                        ),
                        info3
                ),
                info1,
                info2,
                info3
        );
    }

    private void mergeAndCheck(final Set<TestPluginInfo> view,
                               final Set<TestPluginInfo> target,
                               final TestPluginInfo... expected) {
        this.mergeAndCheck(
                view,
                target,
                Sets.of(
                        expected
                )
        );
    }

    // nameMapper.......................................................................................................

    private void mergeAndCheck(final Set<TestPluginInfo> view,
                               final Set<TestPluginInfo> target,
                               final Set<TestPluginInfo> expected) {
        this.checkEquals(
                expected,
                PluginInfoSetLike.merge(
                        view,
                        target
                )
        );
    }

    @Test
    public void testNameMapperWithNullViewFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginInfoSetLike.<TestPluginInfo, StringName>nameMapper(
                        null, // view
                        Sets.empty() // target
                )
        );
    }

    @Test
    public void testNameMapperWithNullTargetFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginInfoSetLike.<TestPluginInfo, StringName>nameMapper(
                        Sets.empty(), // view
                        null // target
                )
        );
    }

    @Test
    public void testNameMapperFromView() {
        final String url = "https://example.com/test-111";
        final String targetName = "test-999";
        final String viewName = "test-111";

        this.nameMapperApplyAndCheck(
                new TestPluginInfoSet(
                        Sets.of(
                                new TestPluginInfo(
                                        url,
                                        viewName
                                )
                        )
                ),
                Sets.of(
                        new TestPluginInfo(
                                url,
                                targetName
                        )
                ),
                Names.string(viewName),
                Names.string(targetName) // the name from target
        );
    }

    @Test
    public void testNameMapperWithReplaced() {
        final String url = "https://example.com/test-111";
        final String name = "test-999";

        this.nameMapperApplyAndCheck(
                new TestPluginInfoSet(
                        Sets.of(
                                new TestPluginInfo(
                                        url,
                                        "test-111"
                                )
                        )
                ),
                Sets.of(
                        new TestPluginInfo(
                                url,
                                name
                        )
                ),
                Names.string(name) // target name should have been replaced by view name
        );
    }

    @Test
    public void testNameMapperUnknown() {
        this.nameMapperApplyAndCheck(
                new TestPluginInfoSet(
                        Sets.of(
                                new TestPluginInfo(
                                        "https://example.com/test-111",
                                        "test-111"
                                )
                        )
                ),
                Sets.of(
                        new TestPluginInfo(
                                "https://example.com/test-999",
                                "test-999"
                        )
                ),
                Names.string("test-404")
        );
    }

    private void nameMapperApplyAndCheck(final TestPluginInfoSet set,
                                         final Set<TestPluginInfo> other,
                                         final StringName name) {
        this.nameMapperApplyAndCheck(
                set,
                other,
                name,
                Optional.empty()
        );
    }

    private void nameMapperApplyAndCheck(final TestPluginInfoSet set,
                                         final Set<TestPluginInfo> other,
                                         final StringName name,
                                         final StringName expected) {
        this.nameMapperApplyAndCheck(
                set,
                other,
                name,
                Optional.of(expected)
        );
    }

    private void nameMapperApplyAndCheck(final TestPluginInfoSet set,
                                         final Set<TestPluginInfo> other,
                                         final StringName name,
                                         final Optional<StringName> expected) {
        this.checkEquals(
                expected,
                PluginInfoSetLike.nameMapper(
                        set,
                        other
                ).apply(name)
        );
    }

    @Test
    public void testEmptyConcatAndText() {
        this.textAndCheck(
                TestPluginInfoSet.EMPTY.concat(
                        new TestPluginInfo(
                                "https://example.com/test-111",
                                "test-111"
                        )
                ),
                "https://example.com/test-111 test-111"
        );
    }

    @Test
    public void testDeleteAndText() {
        this.textAndCheck(
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
                ).delete(
                        new TestPluginInfo(
                                "https://example.com/test-333",
                                "test-333"
                        )
                ),
                "https://example.com/test-111 test-111,https://example.com/test-222 test-222"
        );
    }

    @Test
    public void testDeleteAndText2() {
        this.textAndCheck(
                delete(
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
                        ),
                        new TestPluginInfo(
                                "https://example.com/test-333",
                                "test-333"
                        )
                ),
                "https://example.com/test-111 test-111,https://example.com/test-222 test-222"
        );
    }

    // set..............................................................................................................

    @Override
    public TestPluginInfoSet createSet() {
        return new TestPluginInfoSet(
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
        );
    }

    @Override
    public TestPluginInfo info() {
        return new TestPluginInfo(
                "https://example.com/test-111",
                "test-111"
        );
    }

    @Override
    public Class<TestPluginInfoSet> type() {
        return TestPluginInfoSet.class;
    }

    // parse............................................................................................................
    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseEmpty() {
        this.parseStringAndCheck(
                "",
                new TestPluginInfoSet(
                        Sets.empty()
                )
        );
    }

    @Test
    public void testParseUrlStringName() {
        final String text = "https://example.com/1 test-1";

        this.parseStringAndCheck(
                text,
                new TestPluginInfoSet(
                        Sets.of(
                                TestPluginInfo.parse(text)
                        )
                )
        );
    }

    @Test
    public void testParseStringUrlStringNameString() {
        final String text = " https://example.com/1 test-1 ";

        this.parseStringAndCheck(
                text,
                new TestPluginInfoSet(
                        Sets.of(
                                TestPluginInfo.parse(text)
                        )
                )
        );
    }

    @Test
    public void testParseUrlStringNameCommaFails() {
        this.parseStringFails(
                "https://example.com/1 test-1,",
                new IllegalArgumentException("Missing url")
        );
    }

    @Test
    public void testParseUrlStringNameCommaSpaceFails() {
        this.parseStringFails(
                "https://example.com/1 test-1,   ",
                new IllegalArgumentException("Missing url")
        );
    }

    @Test
    public void testParseUrlStringNameCommaUrlFails() {
        this.parseStringFails(
                "https://example.com/1 test-1,https://example.com/2",
                new IllegalArgumentException("Missing name")
        );
    }

    @Test
    public void testParseUrlStringNameCommaUrlStringFails() {
        this.parseStringFails(
                "https://example.com/1 test-1,https://example.com/2 ",
                new IllegalArgumentException("Missing name")
        );
    }

    @Test
    public void testParseStringUrlStringNameStringCommaUrlStringName() {
        final String text1 = " https://example.com/1 test-1 ";
        final String text2 = "https://example.com/2 test-2";

        this.parseStringAndCheck(
                text1 + "," + text2,
                new TestPluginInfoSet(
                        Sets.of(
                                TestPluginInfo.parse(text1),
                                TestPluginInfo.parse(text2)
                        )
                )
        );
    }

    @Test
    public void testParseStringUrlStringNameStringCommaSpaceUrlStringNameSpace() {
        final String text1 = " https://example.com/1 test-1 ";
        final String text2 = " https://example.com/2 test-2 ";

        this.parseStringAndCheck(
                text1 + "," + text2,
                new TestPluginInfoSet(
                        Sets.of(
                                TestPluginInfo.parse(text1),
                                TestPluginInfo.parse(text2)
                        )
                )
        );
    }

    @Test
    public void testParseStringSpaceUrlStringSpaceNameSpaceStringCommaSpaceSpaceUrlSpaceStringNameSpaceSpace() {
        final String text1 = "  https://example.com/1  test-1  ";
        final String text2 = "  https://example.com/2  test-2  ";

        this.parseStringAndCheck(
                text1 + "," + text2,
                new TestPluginInfoSet(
                        Sets.of(
                                TestPluginInfo.parse(text1),
                                TestPluginInfo.parse(text2)
                        )
                )
        );
    }

    @Override
    public TestPluginInfoSet parseString(final String text) {
        return TestPluginInfoSet.parse(text);
    }

    // json.............................................................................................................

    @Override
    public TestPluginInfoSet unmarshall(final JsonNode json,
                                        final JsonNodeUnmarshallContext context) {
        return TestPluginInfoSet.unmarshall(
                json,
                context
        );
    }

    static class TestPluginInfoSet extends AbstractSet<TestPluginInfo> implements
            ImmutableSetDefaults<TestPluginInfoSet, TestPluginInfo>,
            PluginInfoSetLike<TestPluginInfoSet, TestPluginInfo, StringName> {

        public final static TestPluginInfoSet EMPTY = new TestPluginInfoSet(Sets.empty());
        private final Set<TestPluginInfo> set;

        TestPluginInfoSet(final Set<TestPluginInfo> set) {
            this.set = set;
        }

        static TestPluginInfoSet parse(final String text) {
            return PluginInfoSetLike.parse(
                    text,
                    TestPluginInfo::parse,
                    (s) -> s.isEmpty() ?
                            EMPTY :
                            new TestPluginInfoSet(s)
            );
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

        @Override
        public Iterator<TestPluginInfo> iterator() {
            return Iterators.readOnly(this.set.iterator());
        }

        // json.............................................................................................................

        @Override
        public int size() {
            return this.set.size();
        }

        private JsonNode marshall(final JsonNodeMarshallContext context) {
            return context.marshallCollection(this);
        }

        // toString.....................................................................................................

        @Override
        public String toString() {
            return this.text();
        }

        // ImmutableSet.................................................................................................

        @Override
        public TestPluginInfoSet setElements(final Set<TestPluginInfo> elements) {
            Objects.requireNonNull(elements, "elements");

            final TestPluginInfoSet copy = new TestPluginInfoSet(elements);
            return this.equals(copy) ? this : copy;
        }

        @Override
        public Set<TestPluginInfo> toSet() {
            return new TreeSet<>(this.set);
        }
    }

    static class TestPluginInfo implements PluginInfoLike<TestPluginInfo, StringName> {

        private final StringName name;
        private final AbsoluteUrl url;

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

        static TestPluginInfo parse(final String text) {
            return PluginInfoLike.parse(
                    text,
                    Names::string,
                    TestPluginInfo::new
            );
        }

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

        @Override
        public StringName name() {
            return this.name;
        }

        // object.......................................................................................................

        @Override
        public AbsoluteUrl url() {
            return this.url;
        }

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

        // json.........................................................................................................

        @Override
        public String toString() {
            return PluginInfoLike.toString(this);
        }
    }
}
