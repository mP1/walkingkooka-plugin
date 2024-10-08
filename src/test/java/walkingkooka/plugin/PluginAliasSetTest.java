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
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.Url;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.parser.ParserException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginAliasSetTest implements PluginAliasSetLikeTesting<StringName,
        TestPluginInfo,
        TestPluginInfoSet,
        TestPluginSelector,
        TestPluginAlias,
        PluginAliasSet<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias>> {

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

    @Test
    public void testWithNullPluginHelperFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginAliasSet.parse(
                        TEXT,
                        null
                )
        );
    }

    // parse............................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseWithEmpty() {
        this.parseStringAndCheck(
                "",
                new PluginAliasSet<>(
                        SortedSets.empty(), // aliases
                        Maps.empty(), // alias -> selector
                        Sets.empty(), // alias selectors
                        Maps.empty(), // name -> name
                        Sets.empty(), // names
                        TestPluginInfoSet.EMPTY, // infos
                        TestPluginHelper.INSTANCE
                )
        );
    }

    @Test
    public void testParseWithInvalidInitialCharacterFails() {
        this.parseStringInvalidCharacterFails(
                "[abs",
                '['
        );
    }

    @Test
    public void testParseWithTrailingSeparatorFails() {
        this.parseStringInvalidCharacterFails(
                "plugin111, ",
                ' '
        );
    }

    @Test
    public void testParseWithSpaces() {
        this.parseStringAndCheck(
                " ",
                new PluginAliasSet<>(
                        SortedSets.empty(), // aliases
                        Maps.empty(), // alias -> selector
                        Sets.empty(), // alias selectors
                        Maps.empty(), // name -> name
                        Sets.empty(), // names
                        TestPluginInfoSet.EMPTY, // infos
                        TestPluginHelper.INSTANCE
                )
        );
    }

    @Test
    public void testParseWithName() {
        this.parseStringAndCheck(
                "plugin111",
                new PluginAliasSet<>(
                        SortedSets.of(
                                TestPluginAlias.with(
                                        Names.string("plugin111"),
                                        Optional.empty(), // selector
                                        Optional.empty() // url
                                )
                        ), // aliases
                        Maps.empty(), // alias -> selector
                        Sets.empty(), // alias selectors
                        Maps.of(
                                NAME1,
                                NAME1
                        ), // name -> name
                        Sets.of(NAME1), // names
                        TestPluginInfoSet.EMPTY, // infos
                        TestPluginHelper.INSTANCE
                )
        );
    }

    @Test
    public void testParseWithNameSpace() {
        this.parseStringAndCheck(
                "plugin111 ",
                new PluginAliasSet<>(
                        SortedSets.of(
                                TestPluginAlias.with(
                                        Names.string("plugin111"),
                                        Optional.empty(), // selector
                                        Optional.empty() // url
                                )
                        ), // aliases
                        Maps.empty(), // alias -> selector
                        Sets.empty(), // alias selectors
                        Maps.of(
                                NAME1,
                                NAME1
                        ), // name -> name
                        Sets.of(NAME1), // names
                        TestPluginInfoSet.EMPTY, // infos
                        TestPluginHelper.INSTANCE
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorSpaceUrl() {
        this.parseStringAndCheck(
                "alias111 plugin111 https://example.com/alias111",
                new PluginAliasSet<>(
                        SortedSets.of(
                                TestPluginAlias.with(
                                        Names.string("alias111"),
                                        Optional.of(
                                                TestPluginSelector.parse("plugin111")
                                        ), // selector
                                        Optional.of(
                                                Url.parseAbsolute("https://example.com/alias111")
                                        ) // url
                                )
                        ), // aliases
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
                        ), // infos
                        TestPluginHelper.INSTANCE
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
                "alias111 plugin111() https://example.com/plugin111",
                new PluginAliasSet<>(
                        SortedSets.of(
                                TestPluginAlias.with(
                                        Names.string("alias111"),
                                        Optional.of(
                                                TestPluginSelector.parse("plugin111()")
                                        ), // selector
                                        Optional.of(
                                                Url.parseAbsolute("https://example.com/plugin111")
                                        ) // url
                                )
                        ), // aliases
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
                                        TestPluginInfo.parse("https://example.com/plugin111 alias111")
                                )
                        ), // infos
                        TestPluginHelper.INSTANCE
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorOpenSpaceCloseSpaceUrl() {
        this.parseStringAndCheck(
                "alias111 plugin111( ) https://example.com/plugin111",
                new PluginAliasSet<>(
                        SortedSets.of(
                                TestPluginAlias.with(
                                        Names.string("alias111"),
                                        Optional.of(
                                                TestPluginSelector.parse("plugin111( )")
                                        ), // selector
                                        Optional.of(
                                                Url.parseAbsolute("https://example.com/plugin111")
                                        ) // url
                                )
                        ), // aliases
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
                                        TestPluginInfo.parse("https://example.com/plugin111 alias111")
                                )
                        ), // infos
                        TestPluginHelper.INSTANCE
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorOpenNumberCloseSpaceUrl() {
        this.parseStringAndCheck(
                "alias111 plugin111(999) https://example.com/plugin111",
                new PluginAliasSet<>(
                        SortedSets.of(
                                TestPluginAlias.with(
                                        Names.string("alias111"),
                                        Optional.of(
                                                TestPluginSelector.parse("plugin111(999)")
                                        ), // selector
                                        Optional.of(
                                                Url.parseAbsolute("https://example.com/plugin111")
                                        ) // url
                                )
                        ), // aliases
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
                                        TestPluginInfo.parse("https://example.com/plugin111 alias111")
                                )
                        ), // infos
                        TestPluginHelper.INSTANCE
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorOpenQuotedStringCloseSpaceUrl() {
        this.parseStringAndCheck(
                "alias111 plugin111(\"Hello\") https://example.com/plugin111",
                new PluginAliasSet<>(
                        SortedSets.of(
                                TestPluginAlias.with(
                                        Names.string("alias111"),
                                        Optional.of(
                                                TestPluginSelector.parse("plugin111(\"Hello\")")
                                        ), // selector
                                        Optional.of(
                                                Url.parseAbsolute("https://example.com/plugin111")
                                        ) // url
                                )
                        ), // aliases
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
                                        TestPluginInfo.parse("https://example.com/plugin111 alias111")
                                )
                        ), // infos
                        TestPluginHelper.INSTANCE
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorOpenNumberCommaEnvironmentalValueCommaQuotedStringCloseSpaceUrl() {
        this.parseStringAndCheck(
                "alias111 plugin111(888,$Magic,\"Hello\") https://example.com/plugin111",
                new PluginAliasSet<>(
                        SortedSets.of(
                                TestPluginAlias.with(
                                        Names.string("alias111"),
                                        Optional.of(
                                                TestPluginSelector.parse("plugin111(888,$Magic,\"Hello\")")
                                        ), // selector
                                        Optional.of(
                                                Url.parseAbsolute("https://example.com/plugin111")
                                        ) // url
                                )
                        ), // aliases
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
                                        TestPluginInfo.parse("https://example.com/plugin111 alias111")
                                )
                        ), // infos
                        TestPluginHelper.INSTANCE
                )
        );
    }

    @Test
    public void testParseWithNameCommaName() {
        this.parseStringAndCheck(
                "plugin111, plugin222",
                new PluginAliasSet<>(
                        SortedSets.of(
                                TestPluginAlias.with(
                                        Names.string("plugin111"),
                                        Optional.empty(),
                                        Optional.empty()
                                ),
                                TestPluginAlias.with(
                                        Names.string("plugin222"),
                                        Optional.empty(),
                                        Optional.empty()
                                )
                        ), // aliases
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
                        TestPluginInfoSet.EMPTY, // infos
                        TestPluginHelper.INSTANCE
                )
        );
    }

    @Test
    public void testParseWithAliasCommaName() {
        this.parseStringAndCheck(
                "alias111 plugin111, plugin222",
                new PluginAliasSet<>(
                        SortedSets.of(
                                TestPluginAlias.with(
                                        Names.string("alias111"),
                                        Optional.of(
                                                TestPluginSelector.parse("plugin111")
                                        ),
                                        Optional.empty()
                                ),
                                TestPluginAlias.with(
                                        Names.string("plugin222"),
                                        Optional.empty(),
                                        Optional.empty()
                                )
                        ), // aliases
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
                        TestPluginInfoSet.EMPTY, // infos
                        TestPluginHelper.INSTANCE
                )
        );
    }

    @Test
    public void testParseWithAliasCommaDuplicateAlias() {
        this.parseStringFails(
                "alias111 plugin111(\"Hello111\"), alias111 plugin222(\"Hello222\")",
                new IllegalArgumentException("Duplicate name/alias: alias111")
        );
    }

    @Test
    public void testParseWithAliasSelectorCommaAliasSelector() {
        this.parseStringAndCheck(
                "alias111 plugin111(\"Hello111\"), alias222 plugin222(\"Hello222\")",
                new PluginAliasSet<>(
                        SortedSets.of(
                                TestPluginAlias.with(
                                        Names.string("alias111"),
                                        Optional.of(
                                                TestPluginSelector.parse("plugin111(\"Hello111\")")
                                        ),
                                        Optional.empty()
                                ),
                                TestPluginAlias.with(
                                        Names.string("alias222"),
                                        Optional.of(
                                                TestPluginSelector.parse("plugin222(\"Hello222\")")
                                        ),
                                        Optional.empty()
                                )
                        ), // aliases
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
                        TestPluginInfoSet.EMPTY, // infos
                        TestPluginHelper.INSTANCE
                )
        );
    }

    @Test
    public void testParseWithAliasWithoutInfoAndNameFails() {
        this.parseStringFails(
                "alias111 plugin111, plugin111",
                new IllegalArgumentException("Duplicate name/alias: alias111")
        );
    }

    @Test
    public void testParseWithDuplicateAliasFails() {
        this.parseStringFails(
                "alias111 plugin111, alias111 plugin333",
                new IllegalArgumentException("Duplicate name/alias: alias111")
        );
    }

    @Test
    public void testParseWithDuplicateAliasFails2() {
        this.parseStringFails(
                "alias111 plugin111, plugin222, alias111 plugin333",
                new IllegalArgumentException("Duplicate name/alias: alias111")
        );
    }

    @Test
    public void testParseWithDuplicateNameMappingFails() {
        this.parseStringFails(
                "alias111 plugin111, alias222 plugin111",
                new IllegalArgumentException("Duplicate alias: alias111 and alias222")
        );
    }

    @Test
    public void testParseWithDuplicateNameMappingFails2() {
        this.parseStringFails(
                "alias111 plugin111, alias222 plugin111, plugin222",
                new IllegalArgumentException("Duplicate alias: alias111 and alias222")
        );
    }

    @Test
    public void testParseMultipleAliases() {
        this.parseStringAndCheck(
                "alias111 plugin111 https://www.example.com/alias111 , alias222 plugin111 https://www.example.com/alias222",
                new PluginAliasSet<>(
                        SortedSets.of(
                                TestPluginAlias.with(
                                        NAME1_ALIAS,
                                        Optional.of(
                                                SELECTOR1
                                        ),
                                        Optional.of(
                                                Url.parseAbsolute("https://www.example.com/alias111")
                                        )
                                ),
                                TestPluginAlias.with(
                                        NAME2_ALIAS,
                                        Optional.of(
                                                SELECTOR1
                                        ),
                                        Optional.of(
                                                Url.parseAbsolute("https://www.example.com/alias222")
                                        )
                                )
                        ), // aliases
                        Maps.of(
                                NAME1_ALIAS,
                                SELECTOR1,
                                NAME2_ALIAS,
                                SELECTOR2
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
                        TestPluginInfoSet.EMPTY, // infos
                        TestPluginHelper.INSTANCE
                )
        );
    }

    @Override
    public PluginAliasSet<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias> parseString(final String text) {
        return PluginAliasSet.parse(
                text,
                TestPluginHelper.INSTANCE
        );
    }

    // names............................................................................................................

    @Test
    public void testNames() {
        this.namesAndCheck(
                "name1, name2",
                Names.string("name1"),
                Names.string("name2")
        );
    }

    @Test
    public void testNamesDoesntIncludeAliases() {
        this.namesAndCheck(
                "name1, alias2 name2",
                Names.string("name1")
        );
    }

    private void namesAndCheck(final String text,
                               final StringName... expected) {
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
                        .names,
                () -> "names in " + text
        );
    }

    // name.............................................................................................................

    @Test
    public void testNameCaseSensitive() {
        this.nameAndCheck(
                "name1, name2",
                CaseSensitivity.SENSITIVE,
                Names.string("name1"),
                Names.string("name1")
        );
    }

    @Test
    public void testNameDifferentCaseImportant() {
        this.nameAndCheck(
                "name1, name2",
                CaseSensitivity.SENSITIVE,
                Names.string("NAME1")
        );
    }

    @Test
    public void testNameDifferentCaseUnimportant() {
        this.nameAndCheck(
                "name1, name2",
                CaseSensitivity.INSENSITIVE,
                Names.string("NAME1"),
                Names.string("name1")
        );
    }

    private void nameAndCheck(final String text,
                              final CaseSensitivity caseSensitivity,
                              final StringName name) {
        this.nameAndCheck(
                text,
                caseSensitivity,
                name,
                Optional.empty()
        );
    }

    private void nameAndCheck(final String text,
                              final CaseSensitivity caseSensitivity,
                              final StringName name,
                              final StringName expected) {
        this.nameAndCheck(
                text,
                caseSensitivity,
                name,
                Optional.of(expected)
        );
    }

    private void nameAndCheck(final String text,
                              final CaseSensitivity caseSensitivity,
                              final StringName name,
                              final Optional<StringName> expected) {
        this.checkEquals(
                expected,
                PluginAliasSet.parse(
                        text,
                        new TestPluginHelper(caseSensitivity)
                ).name(name),
                () -> "name  " + name + " in " + text
        );
    }

    // aliasesWithoutInfos..............................................................................................

    @Test
    public void testAliasesWithoutInfos() {
        this.aliasesWithoutInfosAndCheck(
                "alias1 name1, alias2 name2, name3",
                Names.string("alias1"),
                Names.string("alias2")
        );
    }

    @Test
    public void testAliasesWIthoutInfosIgnoresAliasWithInfo() {
        this.aliasesWithoutInfosAndCheck(
                "alias1 name1, alias2 name2 https://example.com , name3",
                Names.string("alias1")
        );
    }

    private void aliasesWithoutInfosAndCheck(final String text,
                                             final StringName... expected) {
        this.aliasesWithoutInfosAndCheck(
                text,
                Sets.of(expected)
        );
    }

    private void aliasesWithoutInfosAndCheck(final String text,
                                             final Set<StringName> expected) {
        this.checkEquals(
                expected,
                this.parseString(text)
                        .aliasesWithoutInfos,
                () -> "aliasesWithoutInfos in " + text
        );
    }

    // alias............................................................................................................

    @Test
    public void testAliasCaseSensitive() {
        this.aliasAndCheck(
                "alias1 name1, alias2 name2",
                CaseSensitivity.SENSITIVE,
                Names.string("alias1"),
                TestPluginSelector.parse("name1")
        );
    }

    @Test
    public void testAliasDifferentCaseImportant() {
        this.aliasAndCheck(
                "alias1 name1, alias2 name2",
                CaseSensitivity.SENSITIVE,
                Names.string("ALIAS1")
        );
    }

    @Test
    public void testAliasDifferentCaseUnimportant() {
        this.aliasAndCheck(
                "alias1 name1, alias2 name2",
                CaseSensitivity.INSENSITIVE,
                Names.string("ALIAS1"),
                TestPluginSelector.parse("name1")
        );
    }

    private void aliasAndCheck(final String text,
                               final CaseSensitivity caseSensitivity,
                               final StringName alias) {
        this.aliasAndCheck(
                text,
                caseSensitivity,
                alias,
                Optional.empty()
        );
    }

    private void aliasAndCheck(final String text,
                               final CaseSensitivity caseSensitivity,
                               final StringName alias,
                               final TestPluginSelector expected) {
        this.aliasAndCheck(
                text,
                caseSensitivity,
                alias,
                Optional.of(expected)
        );
    }

    private void aliasAndCheck(final String text,
                               final CaseSensitivity caseSensitivity,
                               final StringName alias,
                               final Optional<TestPluginSelector> expected) {
        this.checkEquals(
                expected,
                PluginAliasSet.parse(
                        text,
                        new TestPluginHelper(caseSensitivity)
                ).alias(alias),
                () -> "alias  " + alias + " in " + text
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
    public void testTextWithAliasNameUrl2() {
        this.parseAndTextCheck(
                "alias111 name111 https://example.com/name111 , alias222 name222 https://example.com/name222"
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

    // merge............................................................................................................

    @Test
    public void testMergeWithUnknownNameFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.parseString("unknown222")
                        .merge(
                                TestPluginInfoSet.parse("https://example.com/111 plugin111")
                        )
        );

        this.checkEquals(
                "Unknown StringName(s): unknown222",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testMergeWithUnknownNameFails2() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.parseString("unknown222, unknown333, plugin444")
                        .merge(
                                TestPluginInfoSet.parse("https://example.com/111 plugin111, https://example.com/444 plugin444")
                        )
        );

        this.checkEquals(
                "Unknown StringName(s): unknown222,unknown333",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testMergeWithUnknownAliasFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.parseString("unknown222 plugin222")
                        .merge(
                                TestPluginInfoSet.parse("https://example.com/111 plugin111")
                        )
        );

        this.checkEquals(
                "Unknown StringName(s): plugin222",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testMergeWithOnlyNames() {
        this.mergeAndCheck(
                "plugin111",
                "https://example.com/111 plugin111, https://example.com/222 plugin222", // plugin222 not mentioned should NOT appear in INFO
                "https://example.com/111 plugin111"
        );
    }

    @Test
    public void testMergeWithOnlyNames2() {
        this.mergeAndCheck(
                "plugin111, plugin222",
                "https://example.com/111 plugin111, https://example.com/222 plugin222",
                "https://example.com/111 plugin111, https://example.com/222 plugin222"
        );
    }

    @Test
    public void testMergeWithNamesAndAliases() {
        this.mergeAndCheck(
                "plugin111, alias222 plugin222",
                "https://example.com/111 plugin111, https://example.com/222 plugin222, https://example.com/333 plugin333",
                "https://example.com/111 plugin111, https://example.com/222 alias222"
        );
    }

    @Test
    public void testMergeWithNamesAndAliases2() {
        this.mergeAndCheck(
                "plugin111, alias222 plugin222",
                "https://example.com/111 plugin111, https://example.com/222 plugin222",
                "https://example.com/111 plugin111, https://example.com/222 alias222"
        );
    }

    @Test
    public void testMergeWithNamesAndAliasesWithIntroducedUrl() {
        this.mergeAndCheck(
                "plugin111, alias999 plugin111 https://example.com/999",
                "https://example.com/111 plugin111, https://example.com/222 plugin222",
                "https://example.com/111 plugin111, https://example.com/999 alias999"
        );
    }

    @Test
    public void testMergeWithNamesAndAliasesWithIntroducedUrl2() {
        this.mergeAndCheck(
                "alias999 plugin111 https://example.com/999 , plugin222, alias333 plugin333",
                "https://example.com/111 plugin111, https://example.com/222 plugin222, https://example.com/333 plugin333",
                "https://example.com/222 plugin222, https://example.com/333 alias333, https://example.com/999 alias999"
        );
    }

    private void mergeAndCheck(final String alias,
                               final String infos,
                               final String expected) {
        this.mergeAndCheck(
                this.parseString(alias),
                TestPluginInfoSet.parse(infos),
                TestPluginInfoSet.parse(expected)
        );
    }

    // ImmutableSet.....................................................................................................

    @Test
    public void testConcatDuplicateAliasFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createSet()
                        .concat(
                                TestPluginAlias.with(
                                        Names.string("alias1"),
                                        Optional.of(
                                                TestPluginSelector.parse("name999")
                                        ),
                                        Optional.of(
                                                Url.parseAbsolute("https://example.com/999")
                                        )
                                )
                        )
        );

        this.checkEquals(
                "Duplicate name/alias: alias1",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testConcatDuplicateUrlFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createSet()
                        .concat(
                                TestPluginAlias.with(
                                        Names.string("alias999"),
                                        Optional.of(
                                                TestPluginSelector.parse("name999")
                                        ),
                                        Optional.of(
                                                Url.parseAbsolute("https://example.com/1")
                                        )
                                )
                        )
        );

        this.checkEquals(
                "Duplicate url https://example.com/1",
                thrown.getMessage(),
                "message"
        );
    }

    private final static TestPluginAlias ALIAS1 = TestPluginAlias.with(
            Names.string("alias1"),
            Optional.of(
                    TestPluginSelector.parse("name1")
            ),
            Optional.of(
                    Url.parseAbsolute("https://example.com/1")
            )
    );

    private final static TestPluginAlias ALIAS2 = TestPluginAlias.with(
            Names.string("alias2"),
            Optional.of(
                    TestPluginSelector.parse("name2")
            ),
            Optional.of(
                    Url.parseAbsolute("https://example.com/2")
            )
    );

    @Override
    public PluginAliasSet<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias> createSet() {
        return new PluginAliasSet<>(
                SortedSets.of(
                        ALIAS1,
                        ALIAS2
                ),
                Maps.empty(), // alias -> selector
                Sets.empty(), // alias selectors
                Maps.empty(), // name -> name
                Sets.empty(), // names
                TestPluginInfoSet.EMPTY, // infos
                TestPluginHelper.INSTANCE
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintWithOnlyNames() {
        this.treePrintAndCheck(
                this.parseString("name1, name2"),
                "name1\n" +
                        "name2\n"
        );
    }

    @Test
    public void testTreePrintWithOnlyNamesAndAlias() {
        this.treePrintAndCheck(
                this.parseString("name1, alias2 name2"),
                "alias2\n" +
                        "  name2\n" +
                        "name1\n"
        );
    }

    @Test
    public void testTreePrintWithOnlyNamesAndAliasAndInfos() {
        this.treePrintAndCheck(
                this.parseString("name1, alias2 name2 https://example.com/name2"),
                "alias2\n" +
                        "  name2\n" +
                        "  https://example.com/name2\n" +
                        "name1\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<PluginAliasSet<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias>> type() {
        return Cast.to(PluginAliasSet.class);
    }
}
