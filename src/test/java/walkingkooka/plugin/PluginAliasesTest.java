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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginAliasesTest implements ParseStringTesting<PluginAliases<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector>>,
        HasTextTesting,
        TreePrintableTesting,
        HashCodeEqualsDefinedTesting2<PluginAliases<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector>>,
        ToStringTesting<PluginAliases<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector>>,
        ClassTesting<PluginAliases<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector>> {

    // parse............................................................................................................

    private final static TestPluginInfo INFO1 = TestPluginInfo.parse("https://example.com/plugin111 plugin111");

    private final static StringName NAME1 = INFO1.name();

    private final static TestPluginInfo INFO1_ALIAS = TestPluginInfo.parse("https://example.com/plugin111 alias111");

    private final static StringName NAME1_ALIAS = INFO1_ALIAS.name();

    private final static TestPluginSelector SELECTOR1 = new TestPluginSelector(
            NAME1,
            ""
    );

    private final static TestPluginInfo INFO2 = TestPluginInfo.parse("https://example.com/plugin222 plugin222");

    private final static StringName NAME2 = INFO2.name();

    private final static TestPluginInfo INFO2_ALIAS = TestPluginInfo.parse("https://example.com/plugin222 alias222");

    private final static StringName NAME2_ALIAS = INFO2_ALIAS.name();

    private final static TestPluginSelector SELECTOR2 = new TestPluginSelector(
            NAME2,
            ""
    );

    private final static String TEXT = "";

    private final static BiFunction<TextCursor, ParserContext, Optional<StringName>> NAME_FACTORY = (t, c) -> Parsers.stringInitialAndPartCharPredicate(
            CharPredicates.letter(),
            CharPredicates.letterOrDigit(),
            1,
            32
    ).parse(
            t,
            c
    ).map(
            tt -> Names.string(tt.text())
    );

    private final static BiFunction<AbsoluteUrl, StringName, TestPluginInfo> INFO_FACTORY = TestPluginInfo::new;

    private final static Function<Set<TestPluginInfo>, TestPluginInfoSet> INFO_SET_FACTORY = TestPluginInfoSet::new;

    private final static Function<String, TestPluginSelector> SELECTOR_FACTORY = TestPluginSelector::parse;

    @Test
    public void testWithNullNameFactoryFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginAliases.parse(
                        TEXT,
                        null,
                        INFO_FACTORY,
                        INFO_SET_FACTORY,
                        SELECTOR_FACTORY
                )
        );
    }

    @Test
    public void testWithNullInfoFactoryFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginAliases.parse(
                        TEXT,
                        NAME_FACTORY,
                        null,
                        INFO_SET_FACTORY,
                        SELECTOR_FACTORY
                )
        );
    }

    @Test
    public void testWithNullInfoSetFactoryFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginAliases.parse(
                        TEXT,
                        NAME_FACTORY,
                        INFO_FACTORY,
                        null,
                        SELECTOR_FACTORY
                )
        );
    }

    @Test
    public void testWithNullSelectorFactoryFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginAliases.parse(
                        TEXT,
                        NAME_FACTORY,
                        INFO_FACTORY,
                        INFO_SET_FACTORY,
                        null
                )
        );
    }

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseWithEmpty() {
        this.parseStringAndCheck(
                "",
                new PluginAliases<>(
                        Maps.empty(), // alias -> selector
                        Sets.empty(), // alias selectors
                        Maps.empty(), // name -> name
                        Sets.empty(), // names
                        TestPluginInfoSet.EMPTY
                )
        );
    }

    @Test
    public void testParseWithName() {
        this.parseStringAndCheck(
                "plugin111",
                new PluginAliases<>(
                        Maps.empty(), // alias -> selector
                        Sets.empty(), // alias selectors
                        Maps.of(
                                NAME1,
                                NAME1
                        ), // name -> name
                        Sets.of(NAME1), // names
                        TestPluginInfoSet.EMPTY
                )
        );
    }

    @Test
    public void testParseWithNameSpace() {
        this.parseStringAndCheck(
                "plugin111 ",
                new PluginAliases<>(
                        Maps.empty(), // alias -> selector
                        Sets.empty(), // alias selectors
                        Maps.of(
                                NAME1,
                                NAME1
                        ), // name -> name
                        Sets.of(NAME1), // names
                        TestPluginInfoSet.EMPTY
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorSpaceUrl() {
        this.parseStringAndCheck(
                "alias111 plugin111 https://example.com/alias111",
                new PluginAliases<>(
                        Maps.of(
                                NAME1_ALIAS,
                                SELECTOR1
                        ), // alias -> selector
                        Sets.of(
                                NAME1_ALIAS
                        ), // alias selectors
                        Maps.empty(), // name -> name
                        Sets.of(NAME1), // names
                        new TestPluginInfoSet(
                                Sets.of(
                                        TestPluginInfo.parse("https://example.com/alias111 alias111")
                                )
                        )
                )
        );
    }

    @Test
    public void testParseWithIncompleteSelectorFails() {
        this.parseStringInvalidCharacterFails(
                "alias111 plugin111(",
                '('
        );
    }

    @Test
    public void testParseWithIncompleteSelectorFails2() {
        this.parseStringFails(
                "alias111 plugin111( \"Hello",
                new ParserException("Missing terminating '\"'")
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorOpenCloseSpaceUrl() {
        this.parseStringAndCheck(
                "alias111 plugin111() https://example.com/alias111",
                new PluginAliases<>(
                        Maps.of(
                                NAME1_ALIAS,
                                SELECTOR1.setText("()")
                        ), // alias -> selector
                        Sets.of(
                                NAME1_ALIAS
                        ), // alias selectors
                        Maps.empty(), // name -> name
                        Sets.of(NAME1), // names
                        new TestPluginInfoSet(
                                Sets.of(
                                        TestPluginInfo.parse("https://example.com/alias111 alias111")
                                )
                        )
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorOpenSpaceCloseSpaceUrl() {
        this.parseStringAndCheck(
                "alias111 plugin111( ) https://example.com/alias111",
                new PluginAliases<>(
                        Maps.of(
                                NAME1_ALIAS,
                                SELECTOR1.setText("( )")
                        ), // alias -> selector
                        Sets.of(
                                NAME1_ALIAS
                        ), // alias selectors
                        Maps.empty(), // name -> name
                        Sets.of(NAME1), // names
                        new TestPluginInfoSet(
                                Sets.of(
                                        TestPluginInfo.parse("https://example.com/alias111 alias111")
                                )
                        )
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorOpenNumberCloseSpaceUrl() {
        this.parseStringAndCheck(
                "alias111 plugin111(999) https://example.com/alias111",
                new PluginAliases<>(
                        Maps.of(
                                NAME1_ALIAS,
                                SELECTOR1.setText("(999)")
                        ), // alias -> selector
                        Sets.of(
                                NAME1_ALIAS
                        ), // alias selectors
                        Maps.empty(), // name -> name
                        Sets.of(NAME1), // names
                        new TestPluginInfoSet(
                                Sets.of(
                                        TestPluginInfo.parse("https://example.com/alias111 alias111")
                                )
                        )
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorOpenQuotedStringCloseSpaceUrl() {
        this.parseStringAndCheck(
                "alias111 plugin111(\"Hello\") https://example.com/alias111",
                new PluginAliases<>(
                        Maps.of(
                                NAME1_ALIAS,
                                SELECTOR1.setText("(\"Hello\")")
                        ), // alias -> selector
                        Sets.of(
                                NAME1_ALIAS
                        ), // alias selectors
                        Maps.empty(), // name -> name
                        Sets.of(NAME1), // names
                        new TestPluginInfoSet(
                                Sets.of(
                                        TestPluginInfo.parse("https://example.com/alias111 alias111")
                                )
                        )
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorOpenNumberCommaEnvironmentalValueCommaQuotedStringCloseSpaceUrl() {
        this.parseStringAndCheck(
                "alias111 plugin111(888,$Magic,\"Hello\") https://example.com/alias111",
                new PluginAliases<>(
                        Maps.of(
                                NAME1_ALIAS,
                                SELECTOR1.setText("(888,$Magic,\"Hello\")")
                        ), // alias -> selector
                        Sets.of(
                                NAME1_ALIAS
                        ), // alias selectors
                        Maps.empty(), // name -> name
                        Sets.of(NAME1), // names
                        new TestPluginInfoSet(
                                Sets.of(
                                        TestPluginInfo.parse("https://example.com/alias111 alias111")
                                )
                        )
                )
        );
    }

    @Test
    public void testParseWithNameCommaName() {
        this.parseStringAndCheck(
                "plugin111, plugin222",
                new PluginAliases<>(
                        Maps.empty(), // alias -> selector
                        Sets.empty(), // alias selectors
                        Maps.of(
                                NAME1,
                                NAME1,
                                NAME2,
                                NAME2
                        ), // name -> name
                        Sets.of(
                                NAME1,
                                NAME2
                        ), // names
                        TestPluginInfoSet.EMPTY
                )
        );
    }

    @Test
    public void testParseWithAliasCommaName() {
        this.parseStringAndCheck(
                "alias111 plugin111, plugin222",
                new PluginAliases<>(
                        Maps.of(
                                NAME1_ALIAS,
                                SELECTOR1
                        ), // alias -> selector
                        Sets.of(
                                NAME1_ALIAS
                        ), // alias selectors
                        Maps.of(
                                NAME2,
                                NAME2
                        ), // name -> name
                        Sets.of(
                                NAME1,
                                NAME2
                        ), // names
                        TestPluginInfoSet.EMPTY
                )
        );
    }

    @Test
    public void testParseWithAliasCommaDuplicateAlias() {
        this.parseStringFails(
                "alias111 plugin111(\"Hello111\"), alias111 plugin222(\"Hello222\")",
                new IllegalArgumentException("Duplicate name: \"alias111\"")
        );
    }

    @Test
    public void testParseWithAliasSelectorCommaAliasSelector() {
        this.parseStringAndCheck(
                "alias111 plugin111(\"Hello111\"), alias222 plugin222(\"Hello222\")",
                new PluginAliases<>(
                        Maps.of(
                                NAME1_ALIAS,
                                SELECTOR1.setText("(\"Hello111\")"),
                                NAME2_ALIAS,
                                SELECTOR2.setText("(\"Hello222\")")
                        ), // alias -> selector
                        Sets.of(
                                NAME1_ALIAS,
                                NAME2_ALIAS
                        ), // alias selectors
                        Maps.empty(), // name -> name
                        Sets.of(
                                NAME1,
                                NAME2
                        ), // names
                        TestPluginInfoSet.EMPTY
                )
        );
    }

    @Override
    public PluginAliases<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector> parseString(final String text) {
        return PluginAliases.parse(
                text,
                NAME_FACTORY,
                INFO_FACTORY, // Info factory
                INFO_SET_FACTORY,
                SELECTOR_FACTORY
        );
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // name.............................................................................................................

    @Test
    public void testNames() {
        this.namesAndCheck(
                "name1, name2",
                Names.string("name1"),
                Names.string("name2")
        );
    }

    @Test
    public void testNamesIncludesAliases() {
        this.namesAndCheck(
                "name1, alias2 name2",
                Names.string("name1"),
                Names.string("name2")
        );
    }

    private void namesAndCheck(final String text,
                               final StringName ... expected) {
        this.namesAndCheck(
                text,
                Sets.of(expected)
        );
    }

    private void namesAndCheck(final String text,
                               final Set<StringName> expected) {
        this.checkEquals(
                expected,
                this.parseString(text)
                        .names(),
                () -> "names in " + text
        );
    }

    // text.............................................................................................................

    @Test
    public void testTextWithEmpty() {
        this.parseAndTextCheck(
                ""
        );
    }

    @Test
    public void testTextWithName() {
        this.parseAndTextCheck(
                "name111"
        );
    }

    @Test
    public void testTextWithAlias() {
        this.parseAndTextCheck(
                "alias111 name111"
        );
    }

    @Test
    public void testTextWithAliasWithParameters() {
        this.parseAndTextCheck(
                "alias111 name111(\"Hello\")"
        );
    }

    @Test
    public void testTextWithAliasExtraSpacesName() {
        this.parseAndTextCheck(
                "alias111   name111",
                "alias111 name111"
        );
    }

    @Test
    public void testTextWithAliasNameUrl() {
        this.parseAndTextCheck(
                "alias111 name111 https://example.com/name111"
        );
    }

    @Test
    public void testTextWithAliasNameExtraSpacesUrl() {
        this.parseAndTextCheck(
                "alias111 name111  https://example.com/name111",
                "alias111 name111 https://example.com/name111"
        );
    }

    @Test
    public void testTextWithSortedNames() {
        this.parseAndTextCheck(
                "a1, b2, c3",
                "a1, b2, c3"
        );
    }

    @Test
    public void testTextWithUnsortedNames() {
        this.parseAndTextCheck(
                "c3, b2, a1",
                "a1, b2, c3"
        );
    }

    @Test
    public void testTextWithUnsortedAliases() {
        this.parseAndTextCheck(
                "c3 x, b2 y, a1 z",
                "a1 z, b2 y, c3 x"
        );
    }

    @Test
    public void testTextWithUnsortedAliasesSomeWithParameters() {
        this.parseAndTextCheck(
                "c3 x, b2 y(\"Hello\"), a1 z",
                "a1 z, b2 y(\"Hello\"), c3 x"
        );
    }

    private void parseAndTextCheck(final String text) {
        this.parseAndTextCheck(
                text,
                text
        );
    }

    private void parseAndTextCheck(final String text,
                                   final String expected) {
        this.textAndCheck(
                this.parseString(text),
                expected
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintWithOnlyNames() {
        this.treePrintAndCheck(
                this.parseString("name1, name2"),
                "names\n" +
                        "  name1\n" +
                        "    name1\n" +
                        "  name2\n" +
                        "    name2\n"
        );
    }

    @Test
    public void testTreePrintWithOnlyNamesAndAlias() {
        this.treePrintAndCheck(
                this.parseString("name1, alias2 name2"),
                "aliases\n" +
                        "  alias2\n" +
                        "    name2\n" +
                        "names\n" +
                        "  name1\n" +
                        "    name1\n"
        );
    }

    @Test
    public void testTreePrintWithOnlyNamesAndAliasAndInfos() {
        this.treePrintAndCheck(
                this.parseString("name1, alias2 name2 https://example.com/name2"),
                "aliases\n" +
                        "  alias2\n" +
                        "    name2\n" +
                        "names\n" +
                        "  name1\n" +
                        "    name1\n" +
                        "infos\n" +
                        "  TestPluginInfoSet\n" +
                        "    https://example.com/name2 alias2\n"
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(
                this.parseString("alias1 name1, name2, alias3 name3(333) https://example.com/333"),
                this.parseString("alias1 name1, name2")
        );
    }

    @Override
    public PluginAliases<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector> createObject() {
        return this.parseString("alias1 name1, name2");
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.parseString("name1, alias2 name2 https://example.com/name2"),
                "alias2 name2 https://example.com/name2 , name1"
        );
    }

    // class............................................................................................................

    @Override
    public Class<PluginAliases<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector>> type() {
        return Cast.to(PluginAliases.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
