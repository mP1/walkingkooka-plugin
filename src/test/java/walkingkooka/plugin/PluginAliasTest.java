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
import walkingkooka.ToStringTesting;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.naming.HasNameTesting;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginAliasTest implements ComparableTesting2<PluginAlias<StringName, TestPluginSelector>>,
    HasNameTesting<StringName>,
    ParseStringTesting<PluginAlias<StringName, TestPluginSelector>>,
    HasTextTesting,
    TreePrintableTesting,
    ToStringTesting<PluginAlias<StringName, TestPluginSelector>>,
    ClassTesting<PluginAlias<StringName, TestPluginSelector>> {

    private final static StringName NAME = Names.string("Name123");

    private final static Optional<TestPluginSelector> SELECTOR = Optional.of(
        TestPluginSelector.parse("magic(345)")
    );

    private final static Optional<AbsoluteUrl> URL = Optional.of(Url.parseAbsolute("https://example.com/"));

    // with...........................................................................................................

    @Test
    public void testWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginAlias.with(
                null,
                SELECTOR,
                URL
            )
        );
    }

    @Test
    public void testWithNullSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginAlias.with(
                NAME,
                null,
                URL
            )
        );
    }

    @Test
    public void testWithNullUrlFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginAlias.with(
                NAME,
                SELECTOR,
                null
            )
        );
    }

    @Test
    public void testWithUrlMissingSelectorFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> PluginAlias.with(
                NAME,
                Optional.empty(),
                URL
            )
        );
        this.checkEquals(
            thrown.getMessage(),
            "Name123 missing selector when url=https://example.com/"
        );
    }

    @Test
    public void testWith() {
        final PluginAlias<StringName, TestPluginSelector> alias = PluginAlias.with(
            NAME,
            SELECTOR,
            URL
        );

        this.checkEquals(NAME, alias.name(), "name");
        this.checkEquals(SELECTOR, alias.selector(), "selector");
        this.checkEquals(URL, alias.url(), "url");
    }

    // parse............................................................................................................

    @Test
    public void testParseName() {
        this.parseStringAndCheck(
            "name1",
            PluginAlias.with(
                Names.string("name1"),
                Optional.empty(),
                Optional.empty()
            )
        );
    }

    @Test
    public void testParseNameSpace() {
        this.parseStringAndCheck(
            "name1 ",
            PluginAlias.with(
                Names.string("name1"),
                Optional.empty(),
                Optional.empty()
            )
        );
    }

    @Test
    public void testParseSpaceName() {
        this.parseStringAndCheck(
            " name1",
            PluginAlias.with(
                Names.string("name1"),
                Optional.empty(),
                Optional.empty()
            )
        );
    }

    @Test
    public void testParseAliasInvalidSelectorFails() {
        this.parseStringInvalidCharacterFails(
            "alias1 !",
            '!'
        );
    }

    @Test
    public void testParseAliasAndName() {
        this.parseStringAndCheck(
            "alias1 name1",
            PluginAlias.with(
                Names.string("alias1"),
                Optional.of(
                    TestPluginSelector.parse("name1")
                ),
                Optional.empty()
            )
        );
    }

    @Test
    public void testParseAliasSpacesName() {
        this.parseStringAndCheck(
            "alias1  name1",
            PluginAlias.with(
                Names.string("alias1"),
                Optional.of(
                    TestPluginSelector.parse("name1")
                ),
                Optional.empty()
            )
        );
    }

    @Test
    public void testParseAliasAndNameSpace() {
        this.parseStringAndCheck(
            "alias1 name1 ",
            PluginAlias.with(
                Names.string("alias1"),
                Optional.of(
                    TestPluginSelector.parse("name1")
                ),
                Optional.empty()
            )
        );
    }

    @Test
    public void testParseAliasAndNameWithQuoteString() {
        final String selector = "name1(\"abc\")";

        this.parseStringAndCheck(
            "alias1 " + selector,
            PluginAlias.with(
                Names.string("alias1"),
                Optional.of(
                    TestPluginSelector.parse(selector)
                ),
                Optional.empty()
            )
        );
    }

    @Test
    public void testParseAliasAndNameWithNumberLiteral() {
        final String selector = "name1(1)";

        this.parseStringAndCheck(
            "alias1 " + selector,
            PluginAlias.with(
                Names.string("alias1"),
                Optional.of(
                    TestPluginSelector.parse(selector)
                ),
                Optional.empty()
            )
        );
    }

    @Test
    public void testParseAliasAndNameWithEnvironmentalValue() {
        final String selector = "name1($environmental-value)";

        this.parseStringAndCheck(
            "alias1 " + selector,
            PluginAlias.with(
                Names.string("alias1"),
                Optional.of(
                    TestPluginSelector.parse(selector)
                ),
                Optional.empty()
            )
        );
    }

    @Test
    public void testParseAliasAndNameWithValues() {
        final String selector = "name1(\"abc\", 1, $environmental-value)";

        this.parseStringAndCheck(
            "alias1 " + selector,
            PluginAlias.with(
                Names.string("alias1"),
                Optional.of(
                    TestPluginSelector.parse(selector)
                ),
                Optional.empty()
            )
        );
    }

    @Test
    public void testParseAliasNameUrl() {
        this.parseStringAndCheck(
            "alias1 name1 https://example.com",
            PluginAlias.with(
                Names.string("alias1"),
                Optional.of(
                    TestPluginSelector.parse("name1")
                ),
                Optional.of(
                    Url.parseAbsolute("https://example.com")
                )
            )
        );
    }

    @Test
    public void testParseAliasNameSpacesUrl() {
        this.parseStringAndCheck(
            "alias1 name1  https://example.com",
            PluginAlias.with(
                Names.string("alias1"),
                Optional.of(
                    TestPluginSelector.parse("name1")
                ),
                Optional.of(
                    Url.parseAbsolute("https://example.com")
                )
            )
        );
    }

    @Test
    public void testParseAliasNameSpaceUrlSpace() {
        this.parseStringAndCheck(
            "alias1 name1  https://example.com ",
            PluginAlias.with(
                Names.string("alias1"),
                Optional.of(
                    TestPluginSelector.parse("name1")
                ),
                Optional.of(
                    Url.parseAbsolute("https://example.com")
                )
            )
        );
    }

    @Test
    public void testParseAliasSelectorUrlInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails(
            "alias1 name https://example.com !",
            '!'
        );
    }

    @Override
    public PluginAlias<StringName, TestPluginSelector> parseString(final String text) {
        return PluginAlias.parse(
            text,
            TestPluginHelper.INSTANCE
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
    public void testTextWithName() {
        this.textAndCheck(
            PluginAlias.with(
                NAME,
                Optional.empty(),
                Optional.empty()
            ),
            "Name123"
        );
    }

    @Test
    public void testTextWithNameAndSelector() {
        this.textAndCheck(
            PluginAlias.with(
                NAME,
                SELECTOR,
                Optional.empty()
            ),
            "Name123 magic(345)"
        );
    }

    @Test
    public void testTextWithNameAndSelectorAndUrl() {
        this.textAndCheck(
            PluginAlias.with(
                NAME,
                SELECTOR,
                URL
            ),
            "Name123 magic(345) https://example.com/"
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(
            PluginAlias.with(
                Names.string("different"),
                SELECTOR,
                URL
            )
        );
    }

    @Test
    public void testEqualsDifferentSelector() {
        this.checkNotEquals(
            PluginAlias.with(
                NAME,
                Optional.of(
                    TestPluginSelector.parse("different")
                ),
                URL
            )
        );
    }

    @Test
    public void testEqualsDifferentUrl() {
        this.checkNotEquals(
            PluginAlias.with(
                NAME,
                SELECTOR,
                Optional.empty()
            )
        );
    }

    @Test
    public void testCompareToWhenNameSameMissingSelector() {
        this.compareToAndCheckEquals(
            PluginAlias.with(
                NAME,
                Optional.empty(),
                Optional.empty()
            ),
            PluginAlias.with(
                NAME,
                Optional.empty(),
                Optional.empty()
            )
        );
    }

    @Test
    public void testCompareToWhenNameDifferent() {
        this.compareToAndCheckLess(
            PluginAlias.with(
                Names.string("abc"),
                SELECTOR,
                URL
            ),
            PluginAlias.with(
                Names.string("xyz"),
                SELECTOR,
                URL
            )
        );
    }

    @Test
    public void testCompareToWhenSelectorDifferent() {
        this.compareToAndCheckLess(
            PluginAlias.with(
                NAME,
                Optional.of(
                    TestPluginSelector.parse("abc")
                ),
                URL
            ),
            PluginAlias.with(
                NAME,
                Optional.of(
                    TestPluginSelector.parse("xyz")
                ),
                URL
            )
        );
    }

    @Test
    public void testCompareToWhenSelectorDifferent2() {
        this.compareToAndCheckLess(
            PluginAlias.with(
                NAME,
                Optional.empty(),
                Optional.empty()
            ),
            PluginAlias.with(
                NAME,
                Optional.of(
                    TestPluginSelector.parse("xyz")
                ),
                URL
            )
        );
    }

    @Test
    public void testCompareToWhenUrlMissing() {
        this.compareToAndCheckEquals(
            PluginAlias.with(
                NAME,
                SELECTOR,
                Optional.empty()
            ),
            PluginAlias.with(
                NAME,
                SELECTOR,
                Optional.empty()
            )
        );
    }

    @Test
    public void testCompareToWhenUrlDifferentOneMissing() {
        this.compareToAndCheckLess(
            PluginAlias.with(
                NAME,
                SELECTOR,
                Optional.empty()
            ),
            PluginAlias.with(
                NAME,
                SELECTOR,
                URL
            )
        );
    }

    @Test
    public void testCompareToWhenUrlDifferent2() {
        this.compareToAndCheckLess(
            PluginAlias.with(
                NAME,
                SELECTOR,
                Optional.of(
                    Url.parseAbsolute("https://example.com/111")
                )
            ),
            PluginAlias.with(
                NAME,
                SELECTOR,
                Optional.of(
                    Url.parseAbsolute("https://EXAMPLE.com/222")
                )
            )
        );
    }

    @Test
    public void testCompareToWhenUrlDifferent3() {
        this.compareToAndCheckLess(
            PluginAlias.with(
                NAME,
                SELECTOR,
                Optional.empty()
            ),
            PluginAlias.with(
                NAME,
                SELECTOR,
                URL
            )
        );
    }

    @Override
    public PluginAlias<StringName, TestPluginSelector> createComparable() {
        return PluginAlias.with(
            NAME,
            SELECTOR,
            URL
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            PluginAlias.with(
                NAME,
                SELECTOR,
                URL
            ),
            "Name123 magic(345) https://example.com/"
        );
    }
    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            PluginAlias.with(
                NAME,
                SELECTOR,
                URL
            ),
            "Name123\n" +
                "  magic\n" +
                "    \"(345)\"\n" +
                "  https://example.com/\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<PluginAlias<StringName, TestPluginSelector>> type() {
        return Cast.to(PluginAlias.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
