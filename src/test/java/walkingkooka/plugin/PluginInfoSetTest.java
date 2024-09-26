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
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.iterator.IteratorTesting;
import walkingkooka.collect.set.ImmutableSetTesting;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginInfoSetTest implements ImmutableSetTesting<PluginInfoSet<StringName, TestPluginInfo>, TestPluginInfo>,
        IteratorTesting,
        ParseStringTesting<PluginInfoSet<StringName, TestPluginInfo>>,
        HasTextTesting,
        TreePrintableTesting,
        ToStringTesting<PluginInfoSet<StringName, TestPluginInfo>>,
        HashCodeEqualsDefinedTesting2<PluginInfoSet<StringName, TestPluginInfo>> {

    private final static AbsoluteUrl URL1 = Url.parseAbsolute("https://example.com/a1");

    private final static StringName NAME1 = Names.string("a1");

    private final static TestPluginInfo INFO1 = new TestPluginInfo(
            URL1,
            NAME1
    );

    private final static AbsoluteUrl URL2 = Url.parseAbsolute("https://example.com/b2");

    private final static StringName NAME2 = Names.string("b2");

    private final static TestPluginInfo INFO2 = new TestPluginInfo(
            URL2,
            NAME2
    );

    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginInfoSet.with(null)
        );
    }

    @Test
    public void testWithDuplicateUrlFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> PluginInfoSet.with(
                        Sets.of(
                                INFO1,
                                INFO2,
                                new TestPluginInfo(
                                        INFO1.url(),
                                        Names.string("c3")
                                )
                        )
                )
        );

        this.checkEquals(
                "Duplicate url \"" + URL1 + "\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testWithDuplicateNameFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> PluginInfoSet.with(
                        Sets.of(
                                INFO1,
                                INFO2,
                                new TestPluginInfo(
                                        Url.parseAbsolute("https://example.com/3c"),
                                        NAME1
                                )
                        )
                )
        );

        this.checkEquals(
                "Duplicate name \"a1\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testWith() {
        final PluginInfoSet<StringName, TestPluginInfo> infos = PluginInfoSet.with(
                Sets.of(
                        INFO1,
                        INFO2
                )
        );

        this.checkEquals(
                Sets.of(
                        URL1,
                        URL2
                ),
                infos.url(),
                () -> "urls " + infos
        );

        this.checkEquals(
                Sets.of(
                        NAME1,
                        NAME2
                ),
                infos.names(),
                () -> "names " + infos
        );
    }

    @Test
    public void testWithSetCopied() {
        final Set<TestPluginInfo> set = Sets.hash();
        set.add(INFO1);
        set.add(INFO2);

        final PluginInfoSet<StringName, TestPluginInfo> infos = PluginInfoSet.with(set);

        set.clear();

        this.checkEquals(
                Sets.of(
                        INFO1,
                        INFO2
                ),
                infos
        );
    }

    // contains.........................................................................................................

    @Test
    public void testContains() {
        this.containsAndCheck(
                this.createSet(),
                INFO1
        );
    }

    @Test
    public void testContainsAbsent() {
        this.containsAndCheckAbsent(
                this.createSet(),
                TestPluginInfo.parse("https://example.com/absent Z")
        );
    }

    // iterator.........................................................................................................

    @Test
    public void testIterator() {
        this.iterateAndCheck(
                this.createSet()
                        .iterator(),
                INFO1,
                INFO2
        );
    }

    @Test
    public void testIteratorHasNext() {
        this.iterateUsingHasNextAndCheck(
                this.createSet()
                        .iterator(),
                INFO1,
                INFO2
        );
    }

    // remove...........................................................................................................

    @Test
    public void testRemoveFails() {
        this.removeFails(
                this.createSet(),
                INFO1
        );
    }

    @Override
    public PluginInfoSet<StringName, TestPluginInfo> createSet() {
        return PluginInfoSet.with(
                Sets.of(
                        INFO1,
                        INFO2
                )
        );
    }

    // parseString......................................................................................................

    @Test
    public void testParseNullInfoParserFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginInfoSet.parse(
                        "",
                        null
                )
        );
    }

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseEmpty() {
        this.parseStringAndCheck(
                "",
                new PluginInfoSet<StringName, TestPluginInfo>(
                        SortedSets.empty(), // infos
                        Sets.empty(), // urls
                        Sets.empty() // names
                )
        );
    }

    @Test
    public void testParseInfo() {
        this.parseStringAndCheck(
                "" + INFO1,
                PluginInfoSet.with(
                        Sets.of(INFO1)
                )
        );
    }

    @Test
    public void testParseSpaceInfoSpace() {
        this.parseStringAndCheck(
                " " + INFO1 + " ",
                PluginInfoSet.with(
                        Sets.of(INFO1)
                )
        );
    }

    @Test
    public void testParseInfoCommaInfo() {
        this.parseStringAndCheck(
                "" + INFO1 + "," + INFO2,
                PluginInfoSet.with(
                        Sets.of(
                                INFO1,
                                INFO2
                        )
                )
        );
    }

    @Test
    public void testParseInfoSpaceCommaSpaceInfo() {
        this.parseStringAndCheck(
                "" + INFO1 + " , " + INFO2,
                PluginInfoSet.with(
                        Sets.of(
                                INFO1,
                                INFO2
                        )
                )
        );
    }

    @Override
    public PluginInfoSet<StringName, TestPluginInfo> parseString(final String text) {
        return PluginInfoSet.parse(
                text,
                TestPluginInfo::parse
        );
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // HasText..........................................................................................................

    @Test
    public void testText() {
        this.textAndCheck(
                PluginInfoSet.with(
                        Sets.of(
                                INFO1
                        )
                ),
                "https://example.com/a1 a1"
        );
    }

    @Test
    public void testText2() {
        this.textAndCheck(
                PluginInfoSet.with(
                        Sets.of(
                                INFO1,
                                INFO2
                        )
                ),
                "https://example.com/a1 a1,https://example.com/b2 b2"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                PluginInfoSet.with(
                        Sets.of(
                                INFO1,
                                INFO2
                        )
                ),
                        "https://example.com/a1 a1\n" +
                        "https://example.com/b2 b2\n"
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(
                PluginInfoSet.with(
                        Sets.of(
                                INFO1
                        )
                ),
                PluginInfoSet.with(
                        Sets.of(
                                INFO1,
                                INFO2
                        )
                )
        );
    }

    @Override
    public PluginInfoSet<StringName, TestPluginInfo> createObject() {
        return PluginInfoSet.with(
                Sets.of(
                        INFO1,
                        INFO2
                )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                PluginInfoSet.with(
                        Sets.of(
                                INFO1,
                                INFO2
                        )
                ),
                "[https://example.com/a1 a1, https://example.com/b2 b2]"
        );
    }

    // class............................................................................................................

    @Override
    public Class<PluginInfoSet<StringName, TestPluginInfo>> type() {
        return Cast.to(PluginInfoSet.class);
    }
}
