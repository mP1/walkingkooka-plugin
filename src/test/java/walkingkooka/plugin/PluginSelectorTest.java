/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.naming.HasNameTesting;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.naming.StringPath;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.StringParserToken;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginSelectorTest implements ClassTesting2<PluginSelector<StringName>>,
    ComparableTesting2<PluginSelector<StringName>>,
    HashCodeEqualsDefinedTesting2<PluginSelector<StringName>>,
    HasNameTesting<StringName>,
    HasTextTesting,
    ToStringTesting<PluginSelector<StringName>>,
    ParseStringTesting<PluginSelector<StringName>>,
    TreePrintableTesting {

    private final static StringName NAME = Names.string("magic-plugin-123");

    private final static String VALUE_TEXT = " @@";

    @Test
    public void testWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginSelector.with(
                null,
                VALUE_TEXT
            )
        );
    }

    @Test
    public void testWithNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginSelector.with(
                NAME,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final PluginSelector<StringName> selector = PluginSelector.with(
            NAME,
            VALUE_TEXT
        );

        this.nameAndCheck(
            selector,
            NAME
        );
        this.valueTextAndCheck(
            selector,
            VALUE_TEXT
        );
    }

    // setName..........................................................................................................

    @Test
    public void testSetNameWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginSelector.with(
                NAME,
                VALUE_TEXT
            ).setName(null)
        );
    }

    @Test
    public void testSetNameWithSame() {
        final PluginSelector<StringName> selector = PluginSelector.with(
            NAME,
            VALUE_TEXT
        );
        assertSame(
            selector,
            selector.setName(NAME)
        );
    }

    @Test
    public void testSetNameWithDifferent() {
        final PluginSelector<StringName> selector = PluginSelector.with(
            NAME,
            VALUE_TEXT
        );
        final StringName different = Names.string("different");

        this.checkEquals(
            PluginSelector.with(
                different,
                VALUE_TEXT
            ),
            selector.setName(different)
        );
    }

    // setValueText.....................................................................................................

    @Test
    public void testSetValueTextWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginSelector.with(
                NAME,
                VALUE_TEXT
            ).setValueText(null)
        );
    }

    @Test
    public void testSetValueTextWithSame() {
        final PluginSelector<StringName> selector = PluginSelector.with(
            NAME,
            VALUE_TEXT
        );

        assertSame(
            selector,
            selector.setValueText(VALUE_TEXT)
        );
    }

    @Test
    public void testSetValueTextWithSame2() {
        final String text = "(\"Hello\")";

        final PluginSelector<StringName> selector = PluginSelector.with(
            NAME,
            text
        );
        assertSame(
            selector,
            selector.setValueText(text)
        );
    }

    @Test
    public void testSetValueTextWithDifferentText() {
        final PluginSelector<StringName> selector = PluginSelector.with(
            NAME,
            VALUE_TEXT
        );
        final String differentText = "(\"Different\")";
        final PluginSelector<StringName> different = selector.setValueText(differentText);

        assertNotSame(
            different,
            selector
        );
        this.nameAndCheck(
            different,
            NAME
        );
        this.valueTextAndCheck(
            different,
            differentText
        );
        this.valueTextAndCheck(
            selector,
            VALUE_TEXT
        );
    }

    private void valueTextAndCheck(final PluginSelector<StringName> selector,
                                   final String expected) {
        this.checkEquals(
            expected,
            selector.valueText(),
            selector::toString
        );
    }

    // setValue.........................................................................................................

    @Test
    public void testSetValueWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginSelector.with(
                NAME,
                VALUE_TEXT
            ).setValues(null)
        );
    }

    @Test
    public void testSetValuesIncludesNull() {
        assertThrows(
            IllegalArgumentException.class,
            () -> PluginSelector.with(
                NAME,
                VALUE_TEXT
            ).setValues(
                Arrays.asList(new Object[]{null})
            )
        );
    }

    @Test
    public void testSetValuesIncludesInvalidValue() {
        assertThrows(
            IllegalArgumentException.class,
            () -> PluginSelector.with(
                NAME,
                VALUE_TEXT
            ).setValues(
                Lists.of(true)
            )
        );
    }

    @Test
    public void testSetValues() {
        this.checkEquals(
            PluginSelector.with(
                NAME,
                "(\"Hello\", 2.5, plugin3)"
            ),
            PluginSelector.with(
                NAME,
                VALUE_TEXT
            ).setValues(
                Lists.of(
                    "Hello",
                    2.5,
                    TestPluginSelector.parse("plugin3")
                )
            )
        );
    }

    @Test
    public void testSetValuesWithWholeNumber() {
        this.checkEquals(
            PluginSelector.with(
                NAME,
                "(\"Hello\", 22, plugin3)"
            ),
            PluginSelector.with(
                NAME,
                VALUE_TEXT
            ).setValues(
                Lists.of(
                    "Hello",
                    22.0,
                    TestPluginSelector.parse("plugin3")
                )
            )
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseStringFails() {
        this.parseStringInvalidCharacterFails(
            "hello/",
            StringPath.SEPARATOR.character()
        );
    }

    @Test
    public void testParseSpaceNameFails() {
        this.parseStringInvalidCharacterFails(
            " hello",
            ' '
        );
    }

    @Test
    public void testParseStringName() {
        final String text = "magic-plugin";

        this.parseStringAndCheck(
            text,
            PluginSelector.with(
                Names.string(text),
                ""
            )
        );
    }

    @Test
    public void testParseStringNameSpace() {
        final String text = "magic-plugin";

        this.parseStringAndCheck(
            text + " ",
            PluginSelector.with(
                Names.string(text),
                ""
            )
        );
    }

    @Test
    public void testParseStringNameSpacePatternText() {
        final String name = "magic-plugin";
        final String patternText = "@@";

        this.parseStringAndCheck(
            name + " " + patternText,
            PluginSelector.with(
                Names.string(name),
                patternText
            )
        );
    }

    // this.parse must be able to parse all PluginSelector<StringName>.toString.

    @Test
    public void testParseToString() {
        final PluginSelector<StringName> selector = PluginSelector.with(
            Names.string("magic-plugin-123"),
            ""
        );

        this.parseStringAndCheck(
            selector.toString(),
            selector
        );
    }

    @Test
    public void testParseToStringWithText() {
        final PluginSelector<StringName> selector = PluginSelector.with(
            Names.string("magic-plugin-123"),
            "hello"
        );

        this.parseStringAndCheck(
            selector.toString(),
            selector
        );
    }

    @Override
    public PluginSelector<StringName> parseString(final String text) {
        return PluginSelector.parse(
            text,
            Names::string
        );
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // EvaluateValueText................................................................................................

    private final static StringName NAME2 = Names.string("converter2");

    private final static StringName NAME3 = Names.string("converter3");

    private final static BiFunction<TextCursor, ParserContext, Optional<StringName>> NAME_PARSER_AND_FACTORY = (final TextCursor text,
                                                                                                                final ParserContext context) ->
        Parsers.initialAndPartCharPredicateString(
            (i) -> i >= 'a' && i <= 'z',
            (i) -> i >= 'a' && i <= 'z' || i >= '0' && i <= '9' || i == '-',
            1, // minLength
            32 // maxLength
        ).parse(
            text,
            context
        ).map(
            (final ParserToken token) -> Names.string(
                token.cast(StringParserToken.class).value()
            )
        );

    private final static PluginSelectorEvaluateValueTextProvider<StringName, TestProvided> PROVIDER = (final StringName name,
                                                                                                       final List<?> values,
                                                                                                       final ProviderContext context) -> new TestProvided(name, values);

    private final static ProviderContext CONTEXT = new FakeProviderContext() {
        @Override
        public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
            return Cast.to(
                Optional.ofNullable(
                    ENVIRONMENT_VALUE_NAME_1.value().equals(name.value()) ?
                        ENVIRONMENT_VALUE_1 :
                        ENVIRONMENT_VALUE_NAME_2.value().equals(name.value()) ?
                            ENVIRONMENT_VALUE_2 :
                            null
                )
            );
        }
    };

    private static class TestProvided {
        TestProvided(final StringName name,
                     final Object... values) {
            this(
                name,
                Lists.of(values)
            );
        }

        TestProvided(final StringName name,
                     final List<?> values) {
            this.name = Objects.requireNonNull(name, "name");
            this.values = values;
        }

        final StringName name;
        final List<?> values;

        @Override
        public int hashCode() {
            return Objects.hash(
                this.name,
                this.values
            );
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof TestProvided && this.equals0((TestProvided) other);
        }

        private boolean equals0(final TestProvided other) {
            return this.name.equals(other.name) &&
                this.values.equals(other.values);
        }

        @Override
        public String toString() {
            return (
                this.name +
                    " " +
                    this.values.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",", "(", ")"))
            ).toString();
        }
    }

    @Test
    public void testEvaluateValueTextWithWithNullNameParserAndFactoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginSelector.parse(
                "name",
                Names::string
            ).evaluateValueText(
                null,
                PROVIDER,
                CONTEXT
            )
        );
    }

    @Test
    public void testEvaluateValueTextWithWithNullProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginSelector.parse(
                "name",
                Names::string
            ).evaluateValueText(
                NAME_PARSER_AND_FACTORY,
                null,
                CONTEXT
            )
        );
    }

    @Test
    public void testEvaluateValueTextWithWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginSelector.parse(
                "name",
                Names::string
            ).evaluateValueText(
                NAME_PARSER_AND_FACTORY,
                PROVIDER,
                null
            )
        );
    }

    @Test
    public void testEvaluateValueTextFails() {
        final String text = NAME + " text/plain";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> PluginSelector.parse(
                text,
                Names::string
            ).evaluateValueText(
                NAME_PARSER_AND_FACTORY,
                PROVIDER,
                CONTEXT
            )
        );

        this.checkEquals(
            new InvalidCharacterException(
                text,
                text.indexOf(' ')
            ).getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testEvaluateValueTextWithWhenEmpty() {
        this.evaluateValueTextAndCheck(
            NAME + "",
            new TestProvided(NAME)
        );
    }

    @Test
    public void testEvaluateValueTextWithSpacesText() {
        this.evaluateValueTextAndCheck(
            NAME + " ",
            new TestProvided(NAME)
        );
    }

    @Test
    public void testEvaluateValueTextWithSpacesText2() {
        this.evaluateValueTextAndCheck(
            NAME + "   ",
            new TestProvided(NAME)
        );
    }

    @Test
    public void testEvaluateValueTextWithOpenParensFail() {
        this.evaluateValueTextFails(
            NAME + "(",
            "Invalid character '(' at 16"
        );
    }

    @Test
    public void testEvaluateValueTextWithDoubleLiteral() {
        this.evaluateValueTextAndCheck(
            NAME + " (1)",
            new TestProvided(NAME, 1.0)
        );
    }

    @Test
    public void testEvaluateValueTextWithNegativeDoubleLiteral() {
        this.evaluateValueTextAndCheck(
            NAME + " (-1)",
            new TestProvided(NAME, -1.0)
        );
    }

    @Test
    public void testEvaluateValueTextWithDoubleLiteralWithDecimals() {
        this.evaluateValueTextAndCheck(
            NAME + " (1.25)",
            NAME_PARSER_AND_FACTORY,
            PROVIDER,
            CONTEXT,
            new TestProvided(NAME, 1.25)
        );
    }

    @Test
    public void testEvaluateValueTextWithDoubleMissingClosingParensFail() {
        this.evaluateValueTextFails(
            "super-magic-converter123(1",
            "Invalid character '1' at 25"
        );
    }

    @Test
    public void testEvaluateValueTextWithStringUnclosedFail() {
        this.evaluateValueTextFails(
            NAME + " (\"unclosed",
            "Missing closing '\"'"
        );
    }

    @Test
    public void testEvaluateValueTextWithEmptyParameterList() {
        this.evaluateValueTextAndCheck(
            NAME + " ()",
            new TestProvided(NAME)
        );
    }

    @Test
    public void testEvaluateValueTextWithEmptyParameterListWithExtraSpaces() {
        this.evaluateValueTextAndCheck(
            NAME + "  ( )",
            new TestProvided(NAME)
        );
    }

    @Test
    public void testEvaluateValueTextWithEmptyParameterListWithExtraSpaces2() {
        this.evaluateValueTextAndCheck(
            NAME + "  (   )",
            new TestProvided(NAME)
        );
    }

    @Test
    public void testEvaluateValueTextWithStringLiteral() {
        this.evaluateValueTextAndCheck(
            NAME + " (\"string-literal-parameter\")",
            new TestProvided(NAME, "string-literal-parameter")
        );
    }

    @Test
    public void testEvaluateValueTextWithStringLiteralStringLiteral() {
        this.evaluateValueTextAndCheck(
            NAME + " (\"string-literal-parameter-1\",\"string-literal-parameter-2\")",
            new TestProvided(NAME, "string-literal-parameter-1", "string-literal-parameter-2")
        );
    }

    @Test
    public void testEvaluateValueTextWithStringLiteralStringLiteralWithExtraSpaceIgnored() {
        this.evaluateValueTextAndCheck(
            NAME + "  ( \"string-literal-parameter-1\" , \"string-literal-parameter-2\" )",
            new TestProvided(NAME, "string-literal-parameter-1", "string-literal-parameter-2")
        );
    }

    private final static EnvironmentValueName<String> ENVIRONMENT_VALUE_NAME_1 = EnvironmentValueName.with(
        "environment-value-name-1",
        String.class
    );

    private final static String ENVIRONMENT_VALUE_1 = "environment-value-1";

    private final static EnvironmentValueName<Double> ENVIRONMENT_VALUE_NAME_2 = EnvironmentValueName.with(
        "environment-value-name-2",
        Double.class
    );

    private final static double ENVIRONMENT_VALUE_2 = 2.5;

    @Test
    public void testEvaluateValueTextWithEnvironmentValueName() {
        this.evaluateValueTextAndCheck(
            NAME + "  ( $" + ENVIRONMENT_VALUE_NAME_1 + ")",
            new TestProvided(NAME, ENVIRONMENT_VALUE_1)
        );
    }

    @Test
    public void testEvaluateValueTextWithEnvironmentValueName2() {
        this.evaluateValueTextAndCheck(
            NAME + "  ( $" + ENVIRONMENT_VALUE_NAME_1 + ",$" + ENVIRONMENT_VALUE_NAME_2 + ",\"string-literal-parameter-3\" )",
            new TestProvided(NAME, ENVIRONMENT_VALUE_1, ENVIRONMENT_VALUE_2, "string-literal-parameter-3")
        );
    }

    @Test
    public void testEvaluateValueTextWithProvided() {
        this.evaluateValueTextAndCheck(
            NAME + " (" + NAME2 + ")",
            new TestProvided(
                NAME,
                new TestProvided(NAME2)
            )
        );
    }

    @Test
    public void testEvaluateValueTextWithProvidedProvided() {
        this.evaluateValueTextAndCheck(
            NAME + " (" + NAME2 + "," + NAME3 + ")",
            new TestProvided(
                NAME,
                new TestProvided(NAME2),
                new TestProvided(NAME3)
            )
        );
    }

    @Test
    public void testEvaluateValueTextWithNestedProvided() {
        this.evaluateValueTextAndCheck(
            NAME + " (" + NAME2 + "(" + NAME3 + "))",
            new TestProvided(
                NAME,
                new TestProvided(
                    NAME2,
                    new TestProvided(NAME3)
                )
            )
        );
    }

    private void evaluateValueTextFails(final String selector,
                                        final String expected) {
        this.evaluateValueTextFails(
            selector,
            NAME_PARSER_AND_FACTORY,
            PROVIDER,
            CONTEXT,
            expected
        );
    }

    private void evaluateValueTextFails(final String selector,
                                        final BiFunction<TextCursor, ParserContext, Optional<StringName>> nameParserAndFactory,
                                        final PluginSelectorEvaluateValueTextProvider<StringName, TestProvided> provider,
                                        final ProviderContext context,
                                        final String expected) {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> PluginSelector.parse(
                selector,
                Names::string
            ).evaluateValueText(
                nameParserAndFactory,
                provider,
                context
            )
        );
        this.checkEquals(
            expected,
            thrown.getMessage()
        );
    }

    private void evaluateValueTextAndCheck(final String selector,
                                           final TestProvided expected) {
        this.evaluateValueTextAndCheck(
            selector,
            NAME_PARSER_AND_FACTORY,
            PROVIDER,
            CONTEXT,
            expected
        );
    }

    private void evaluateValueTextAndCheck(final String selector,
                                           final BiFunction<TextCursor, ParserContext, Optional<StringName>> nameParserAndCreator,
                                           final PluginSelectorEvaluateValueTextProvider<StringName, TestProvided> provider,
                                           final ProviderContext context,
                                           final TestProvided expected) {
        this.checkEquals(
            expected,
            PluginSelector.parse(
                selector,
                Names::string
            ).evaluateValueText(
                nameParserAndCreator,
                provider,
                context
            )
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(
            PluginSelector.with(
                Names.string("different"),
                VALUE_TEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentValueText() {
        this.checkNotEquals(
            PluginSelector.with(
                NAME,
                "different"
            )
        );
    }

    @Test
    public void testCompareToDifferentName() {
        final String valueText = "\"Hello\"";

        this.compareToAndCheckLess(
            PluginSelector.with(
                Names.string("a1"),
                valueText
            ),
            PluginSelector.with(
                Names.string("b2"),
                valueText
            )
        );
    }

    @Test
    public void testCompareToDifferentValue() {
        this.compareToAndCheckLess(
            PluginSelector.with(
                NAME,
                "a1"
            ),
            PluginSelector.with(
                NAME,
                "b2"
            )
        );
    }

    @Test
    public void testCompareToDifferentValueCaseImportant() {
        this.compareToAndCheckLess(
            PluginSelector.with(
                NAME,
                "A1"
            ),
            PluginSelector.with(
                NAME,
                "a"
            )
        );
    }

    @Override
    public PluginSelector<StringName> createComparable() {
        return PluginSelector.with(
            NAME,
            VALUE_TEXT
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            PluginSelector.with(
                NAME,
                ""
            ),
            "magic-plugin-123"
        );
    }

    @Test
    public void testToStringWithValueText() {
        this.toStringAndCheck(
            PluginSelector.with(
                NAME,
                VALUE_TEXT
            ),
            NAME + " " + VALUE_TEXT
        );
    }

    @Test
    public void testToStringWithQuotes() {
        this.toStringAndCheck(
            PluginSelector.with(
                NAME,
                "\"Hello\""
            ),
            "magic-plugin-123 \"Hello\""
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<PluginSelector<StringName>> type() {
        return Cast.to(PluginSelector.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintWithoutText() {
        this.treePrintAndCheck(
            this.parseString("abc123"),
            "abc123\n"
        );
    }

    @Test
    public void testTreePrintWithText() {
        this.treePrintAndCheck(
            this.parseString("magic-plugin-123 @@"),
            "magic-plugin-123\n" +
                "  \"@@\"\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.checkEquals(
            JsonNode.string(NAME + " " + VALUE_TEXT),
            PluginSelector.with(
                NAME,
                VALUE_TEXT
            ).marshall(
                JsonNodeMarshallContexts.basic()
            )
        );
    }
}
