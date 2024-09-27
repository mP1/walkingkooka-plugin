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
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginAliasesTest implements ParseStringTesting<PluginAliases<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector>>,
        TreePrintableTesting,
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

    private final static TestPluginInfoSet INFOS = new TestPluginInfoSet(
            Sets.of(
                    INFO1,
                    INFO2
            )
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

    private final static Function<String, TestPluginSelector> SELECTOR_FACTORY = TestPluginSelector::parse;

    @Test
    public void testWithNullNameFactoryFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginAliases.parse(
                        TEXT,
                        null,
                        INFO_FACTORY,
                        INFOS,
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
                        INFOS,
                        SELECTOR_FACTORY
                )
        );
    }

    @Test
    public void testWithNullInfosFails() {
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
                        INFOS,
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
        this.parseStringAndCheck2(
                "",
                INFOS,
                new PluginAliases<>(
                        Maps.empty(), // alias -> selector
                        Maps.empty(), // name -> name
                        INFOS
                )
        );
    }

    @Test
    public void testParseWithName() {
        this.parseStringAndCheck2(
                "plugin111",
                INFOS,
                new PluginAliases<>(
                        Maps.empty(), // alias -> selector
                        Maps.of(
                                NAME1,
                                NAME1
                        ), // name -> name
                        INFOS
                )
        );
    }

    @Test
    public void testParseWithNameSpace() {
        this.parseStringAndCheck2(
                "plugin111 ",
                INFOS,
                new PluginAliases<>(
                        Maps.empty(), // alias -> selector
                        Maps.of(
                                NAME1,
                                NAME1
                        ), // name -> name
                        INFOS
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorSpaceUrl() {
        this.parseStringAndCheck2(
                "alias111 plugin111 https://example.com/alias111",
                INFOS,
                new PluginAliases<>(
                        Maps.of(
                                INFO1_ALIAS.name(),
                                SELECTOR1
                        ), // alias -> selector
                        Maps.empty(), // name -> name
                        new TestPluginInfoSet(
                                Sets.of(
                                        TestPluginInfo.parse("https://example.com/alias111 alias111"),
                                        INFO1,
                                        INFO2
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
        this.parseStringAndCheck2(
                "alias111 plugin111() https://example.com/alias111",
                INFOS,
                new PluginAliases<>(
                        Maps.of(
                                INFO1_ALIAS.name(),
                                SELECTOR1.setText("()")
                        ), // alias -> selector
                        Maps.empty(), // name -> name
                        new TestPluginInfoSet(
                                Sets.of(
                                        TestPluginInfo.parse("https://example.com/alias111 alias111"),
                                        INFO1,
                                        INFO2
                                )
                        )
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorOpenSpaceCloseSpaceUrl() {
        this.parseStringAndCheck2(
                "alias111 plugin111( ) https://example.com/alias111",
                INFOS,
                new PluginAliases<>(
                        Maps.of(
                                INFO1_ALIAS.name(),
                                SELECTOR1.setText("( )")
                        ), // alias -> selector
                        Maps.empty(), // name -> name
                        new TestPluginInfoSet(
                                Sets.of(
                                        TestPluginInfo.parse("https://example.com/alias111 alias111"),
                                        INFO1,
                                        INFO2
                                )
                        )
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorOpenNumberCloseSpaceUrl() {
        this.parseStringAndCheck2(
                "alias111 plugin111(999) https://example.com/alias111",
                INFOS,
                new PluginAliases<>(
                        Maps.of(
                                INFO1_ALIAS.name(),
                                SELECTOR1.setText("(999)")
                        ), // alias -> selector
                        Maps.empty(), // name -> name
                        new TestPluginInfoSet(
                                Sets.of(
                                        TestPluginInfo.parse("https://example.com/alias111 alias111"),
                                        INFO1,
                                        INFO2
                                )
                        )
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorOpenQuotedStringCloseSpaceUrl() {
        this.parseStringAndCheck2(
                "alias111 plugin111(\"Hello\") https://example.com/alias111",
                INFOS,
                new PluginAliases<>(
                        Maps.of(
                                INFO1_ALIAS.name(),
                                SELECTOR1.setText("(\"Hello\")")
                        ), // alias -> selector
                        Maps.empty(), // name -> name
                        new TestPluginInfoSet(
                                Sets.of(
                                        TestPluginInfo.parse("https://example.com/alias111 alias111"),
                                        INFO1,
                                        INFO2
                                )
                        )
                )
        );
    }

    @Test
    public void testParseWithAliasSpaceSelectorOpenNumberCommaEnvironmentalValueCommaQuotedStringCloseSpaceUrl() {
        this.parseStringAndCheck2(
                "alias111 plugin111(888,$Magic,\"Hello\") https://example.com/alias111",
                INFOS,
                new PluginAliases<>(
                        Maps.of(
                                INFO1_ALIAS.name(),
                                SELECTOR1.setText("(888,$Magic,\"Hello\")")
                        ), // alias -> selector
                        Maps.empty(), // name -> name
                        new TestPluginInfoSet(
                                Sets.of(
                                        TestPluginInfo.parse("https://example.com/alias111 alias111"),
                                        INFO1,
                                        INFO2
                                )
                        )
                )
        );
    }

    @Test
    public void testParseWithNameCommaName() {
        this.parseStringAndCheck2(
                "plugin111, plugin222",
                INFOS,
                new PluginAliases<>(
                        Maps.empty(), // alias -> selector
                        Maps.of(
                                NAME1,
                                NAME1,
                                NAME2,
                                NAME2
                        ), // name -> name
                        new TestPluginInfoSet(
                                Sets.of(
                                        INFO1,
                                        INFO2
                                )
                        )
                )
        );
    }

    @Test
    public void testParseWithAliasCommaName() {
        this.parseStringAndCheck2(
                "alias111 plugin111, plugin222",
                INFOS,
                new PluginAliases<>(
                        Maps.of(
                                INFO1_ALIAS.name(),
                                SELECTOR1
                        ), // alias -> selector
                        Maps.of(
                                NAME2,
                                NAME2
                        ), // name -> name
                        new TestPluginInfoSet(
                                Sets.of(
                                        INFO1.setName(NAME1_ALIAS), // same url with alias
                                        INFO2
                                )
                        )
                )
        );
    }

    @Test
    public void testParseWithAliasCommaDuplicateAlias() {
        this.parseStringFails(
                "alias111 plugin111(\"Hello111\"), alias111 plugin222(\"Hello222\")",
                new IllegalArgumentException("Duplicate name \"alias111\"")
        );
    }

    @Test
    public void testParseWithAliasSelectorCommaAliasSelector() {
        this.parseStringAndCheck2(
                "alias111 plugin111(\"Hello111\"), alias222 plugin222(\"Hello222\")",
                INFOS,
                new PluginAliases<>(
                        Maps.of(
                                INFO1_ALIAS.name(),
                                SELECTOR1.setText("(\"Hello111\")"),
                                INFO2_ALIAS.name(),
                                SELECTOR2.setText("(\"Hello222\")")
                        ), // alias -> selector
                        Maps.empty(), // name -> name
                        new TestPluginInfoSet(
                                Sets.of(
                                        INFO1.setName(NAME1_ALIAS),
                                        INFO2.setName(NAME2_ALIAS)
                                )
                        )
                )
        );
    }

    @Override
    public PluginAliases<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector> parseString(final String text) {
        return this.parseString(
                text,
                INFOS
        );
    }

    private PluginAliases<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector> parseString(final String text,
                                                                                                         final TestPluginInfoSet infos) {
        return PluginAliases.parse(
                text,
                NAME_FACTORY,
                INFO_FACTORY, // Info factory
                infos,
                SELECTOR_FACTORY
        );
    }

    private void parseStringAndCheck2(final String text,
                                      final TestPluginInfoSet infos,
                                      final PluginAliases<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector> expected) {
        this.checkEquals(
                expected,
                this.parseString(
                        text,
                        infos
                ),
                () -> "parse " + CharSequences.quoteAndEscape(text)
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
