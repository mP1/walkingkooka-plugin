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
import walkingkooka.naming.Name;
import walkingkooka.naming.StringName;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginInfoSetLikeTest implements PluginInfoSetLikeTesting<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias, TestPluginAliasSet> {

    // filter............................................................................................................

    static <N extends Name & Comparable<N>, 
            I extends PluginInfoLike<I, N>, 
            IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
            S extends PluginSelectorLike<N>,
            A extends PluginAliasLike<N, S, A>,
            AS extends PluginAliasSetLike<N, I, IS, S, A, AS>> IS delete(final IS s,
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

    // renameIfPresent..................................................................................................

    @Test
    public void testRenameIfPresentWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> new TestPluginInfoSet(
                        Sets.empty()
                ).renameIfPresent(null)
        );
    }

    @Test
    public void testRenameIfPresentEmptyRenames() {
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

        final Set<TestPluginInfo> infos = Sets.of(
                info1,
                info2,
                info3
        );

        this.renameIfPresentAndCheck(
                infos,
                Sets.empty(),
                infos
        );
    }

    @Test
    public void testRenameIfPresentIgnoreUnknownRenames() {
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

        final Set<TestPluginInfo> infos = Sets.of(
                info1,
                info2,
                info3
        );

        this.renameIfPresentAndCheck(
                infos,
                Sets.of(
                        new TestPluginInfo(
                                "https://example.com/test-444",
                                "test-444"
                        )
                ),
                infos
        );
    }

    @Test
    public void testRenameIfPresentSomeRenames() {
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
        final TestPluginInfo info1Renamed = new TestPluginInfo(
                "https://example.com/test-111",
                "test-111-renamed"
        );

        this.renameIfPresentAndCheck(
                Sets.of(
                        info1,
                        info2,
                        info3
                ),
                Sets.of(
                        info1Renamed
                ),
                info1Renamed,
                info2,
                info3
        );
    }

    @Test
    public void testRenameIfPresentSomeRenames2() {
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
        final TestPluginInfo info1Renamed = new TestPluginInfo(
                "https://example.com/test-111",
                "test-111-renamed"
        );

        final TestPluginInfo info2Renamed = new TestPluginInfo(
                "https://example.com/test-222",
                "test-222-renamed"
        );

        this.renameIfPresentAndCheck(
                Sets.of(
                        info1,
                        info2,
                        info3
                ),
                Sets.of(
                        info1Renamed,
                        info2Renamed
                ),
                info1Renamed,
                info2Renamed,
                info3
        );
    }

    @Test
    public void testRenameIfPresentIgnoreUnknownRenames2() {
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

        final Set<TestPluginInfo> infos = Sets.of(
                info1,
                info2,
                info3
        );

        this.renameIfPresentAndCheck(
                infos,
                Sets.of(
                        new TestPluginInfo(
                                "https://example.com/test-ignored-rename-444",
                                "test-ignored-rename-444"
                        )
                ),
                infos
        );
    }

    @Test
    public void testRenameIfPresentSomeRenamesAndIgnoredRenames() {
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
        final TestPluginInfo info1Renamed = new TestPluginInfo(
                "https://example.com/test-111",
                "test-111-renamed"
        );

        final TestPluginInfo info2Renamed = new TestPluginInfo(
                "https://example.com/test-222",
                "test-222-renamed"
        );

        this.renameIfPresentAndCheck(
                Sets.of(
                        info1,
                        info2,
                        info3
                ),
                Sets.of(
                        info1Renamed,
                        info2Renamed,
                        new TestPluginInfo(
                                "https://example.com/test-444",
                                "test-444-ignored"
                        )
                ),
                info1Renamed,
                info2Renamed,
                info3
        );
    }

    private void renameIfPresentAndCheck(final Set<TestPluginInfo> infos,
                                         final Set<TestPluginInfo> renameInfos,
                                         final TestPluginInfo... expected) {
        this.renameIfPresentAndCheck(
                infos,
                renameInfos,
                Sets.of(
                        expected
                )
        );
    }

    private void renameIfPresentAndCheck(final Set<TestPluginInfo> infos,
                                         final Set<TestPluginInfo> renameInfos,
                                         final Set<TestPluginInfo> expected) {
        this.checkEquals(
                expected,
                new TestPluginInfoSet(
                        infos
                ).renameIfPresent(
                        new TestPluginInfoSet(
                                renameInfos
                        )
                ),
                () -> infos + " renameIfPresent " + renameInfos
        );
    }

    // urls.............................................................................................................

    @Test
    public void testUrls() {
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
                Sets.of(
                        info1.url(),
                        info2.url(),
                        info3.url()
                ),
                new TestPluginInfoSet(
                        Sets.of(
                                info1,
                                info2,
                                info3
                        )
                ).url()
        );
    }

    // aliasSet.........................................................................................................

    @Test
    public void testAliasSet() {
        this.aliasSetAndCheck(
                TestPluginInfoSet.parse("https://example.com/name1 name1, https://example.com/name2 name2, https://example.com/name3 name3"),
                TestPluginAliasSet.parse("name1, name2, name3")
        );
    }

    // misc.............................................................................................................

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
}
