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

import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Comparators;
import walkingkooka.naming.HasName;
import walkingkooka.naming.Name;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a selector for a plugin holding the name and additional text that could hold some extra parameters.
 * The plugin will need to parse or handle the text itself.
 * <br>
 * Note the selector should handle serialization itself, with marshalling using the {@link #toString()} into a
 * {@link walkingkooka.tree.json.JsonString} and unmarshalling parsing an equivalent {@link walkingkooka.tree.json.JsonString}.
 */
public final class PluginSelector<N extends Name & Comparable<N>> implements HasName<N>,
    HasText,
    Comparable<PluginSelector<N>>,
    TreePrintable {

    /**
     * Parses the given text into a selector, giving the component {@link Name} and {@link String text} to the provided factory
     * <br>
     * Note the format of the text is name OPTIONAL-SPACE followed by TEXT. Note the TEXT supports escaping
     * <pre>
     * text-format-pattern @
     * </pre>
     */
    public static <N extends Name & Comparable<N>> PluginSelector<N> parse(final String text,
                                                                           final Function<String, N> nameFactory) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final String textAfter;
        final String nameText;
        final int length = text.length();

        int endOfName = -1;
        int startOfText = -1;

        for (int i = 0; i < length; i++) {
            final char c = text.charAt(i);

            switch (c) {
                case ' ':
                    // spaces before name are illegal
                    if (0 == i) {
                        throw new InvalidCharacterException(
                            text,
                            i
                        );
                    }
                    endOfName = i;
                    startOfText = i + 1;
                    i = length;
                    break;
                case '(':
                    endOfName = i;
                    startOfText = i;
                    i = length;
                    break;
                default:
                    break;
            }
        }

        if (-1 == endOfName) {
            nameText = text;
            textAfter = "";
        } else {
            nameText = text.substring(0, endOfName);
            textAfter = text.substring(startOfText);
        }

        return new PluginSelector<>(
            nameFactory.apply(nameText),
            textAfter
        );
    }

    /**
     * Creates a new {@link PluginSelector}.
     */
    public static <N extends Name & Comparable<N>> PluginSelector<N> with(final N name,
                                                                          final String text) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(text, "text");

        return new PluginSelector<>(
            name,
            text
        );
    }

    private PluginSelector(final N name,
                           final String valueText) {
        this.name = name;
        this.valueText = valueText;
    }

    @Override
    public N name() {
        return this.name;
    }

    public PluginSelector<N> setName(final N name) {
        Objects.requireNonNull(name, "name");

        return this.name.equals(name) ?
            this :
            new PluginSelector<>(
                name,
                this.valueText
            );
    }

    private final N name;

    /**
     * Returns the value as text with no escaping.
     */
    public String valueText() {
        return this.valueText;
    }

    /**
     * Would be setter that returns a {@link PluginSelector} with the given text creating a new instance if necessary.
     */
    public PluginSelector<N> setValueText(final String text) {
        Objects.requireNonNull(text, "text");

        return this.valueText.equals(text) ?
            this :
            new PluginSelector<>(
                this.name,
                text
            );
    }

    private final String valueText;

    /**
     * Serializes the given values producing text. Only String, Double and PluginSelector values are supported.
     */
    public PluginSelector<N> setValues(final List<?> values) {
        Objects.requireNonNull(values, "values");

        final StringBuilder b = new StringBuilder();

        String separator = PluginExpressionParser.PARAMETER_BEGIN;

        for (final Object value : values) {
            b.append(separator);
            separator = PluginExpressionParser.PARAMETER_SEPARATOR + " ";

            if (null == value) {
                throw new IllegalArgumentException("Null values are not supported");
            }

            if (value instanceof Double) {
                // TODO should probably verify double in string form doesnt result in exponent.
                final Double doubleValue = (Double) value;

                final Long longValue = doubleValue.longValue();
                if (longValue.doubleValue() == doubleValue.doubleValue()) {
                    b.append(longValue);
                } else {
                    b.append(doubleValue);
                }
                continue;
            }

            if (value instanceof String) {
                final String string = (String) value;
                b.append(
                    CharSequences.quoteAndEscape(
                        string
                    )
                );
                continue;
            }

            if (value instanceof PluginSelectorLike) {
                final PluginSelectorLike<?> pluginSelectorLike = (PluginSelectorLike<?>) value;
                b.append(pluginSelectorLike.name())
                    .append(pluginSelectorLike.valueText());
                continue;
            }

            throw new IllegalArgumentException("Unsupported value " + CharSequences.quoteIfChars(value) + " " + value.getClass().getName() + " expected only double | String | PluginSelector");
        }

        if (b.length() > 0) {
            b.append(PluginExpressionParser.PARAMETER_END);
        }

        return this.setValueText(
            b.toString()
        );
    }

    // evaluateValueText................................................................................................

    /**
     * Parses the {@link #valueText()} as an expression that contains an optional parameter list which may include
     * <ul>
     * <li>{@link PluginNameLike}</li>
     * <li>double literals including negative or leading minus signs.</li>
     * <li>a double quoted string literal</li>
     * </ul>
     * Sample text.
     * <pre>
     * number-to-number
     * collection ( number-to-boolean, number-number, string-to-local-date "yyyy-mm-dd")
     * </pre>
     * The <code>provider</code> will be used to fetch <code>plugin</code>> with any parameters.
     */
    public <T> T evaluateValueText(final BiFunction<TextCursor, ParserContext, Optional<N>> nameParserAndFactory,
                                   final PluginSelectorEvaluateValueTextProvider<N, T> provider,
                                   final ProviderContext context) {
        Objects.requireNonNull(nameParserAndFactory, "nameParserAndFactory");
        Objects.requireNonNull(provider, "provider");
        Objects.requireNonNull(context, "context");

        final String nameText = this.name()
            .value();
        final PluginExpressionParser<N> nameParser = PluginExpressionParser.with(
            nameText,
            nameParserAndFactory
        );

        final Optional<N> name = nameParser.name();
        if (false == name.isPresent() || nameParser.isNotEmpty()) {
            throw new IllegalArgumentException(
                "Unable to parse name in " +
                    CharSequences.quoteAndEscape(nameText)
            );
        }

        final PluginExpressionParser<N> parser = PluginExpressionParser.with(
            this.valueText(),
            nameParserAndFactory
        );

        final List<?> parameters = this.parseParameters(
            parser,
            provider,
            context
        );

        parser.spaces();

        if (parser.isNotEmpty()) {
            this.invalidCharacter(parser);
        }

        return provider.get(
            name.get(),
            parameters,
            context
        );
    }

    /**
     * Attempts to parse an optional plugin including its parameters which must be within parens.
     */
    private <T> Optional<T> parseNameAndParameters(final PluginExpressionParser<N> parser,
                                                   final PluginSelectorEvaluateValueTextProvider<N, T> provider,
                                                   final ProviderContext context) {
        final Optional<N> name = parser.name();

        final T plugin;
        if (name.isPresent()) {
            plugin = provider.get(
                name.get(),
                this.parseParameters(
                    parser,
                    provider,
                    context
                ),
                context
            );
        } else {
            plugin = null;
        }

        return Optional.ofNullable(plugin);
    }

    /**
     * Tries to parse a parameter list if an initial OPEN-PARENS is present.
     * <pre>
     * ( 1.23, "string-literal", $environmental-variable, plugin-name )
     * </pre>
     */
    private <T> List<Object> parseParameters(final PluginExpressionParser<N> parser,
                                             final PluginSelectorEvaluateValueTextProvider<N, T> provider,
                                             final ProviderContext context) {
        parser.spaces();

        final List<Object> parameters = Lists.array();

        if (parser.parametersBegin()) {
            for (; ; ) {
                parser.spaces();

                {
                    final Optional<?> environmentValue = parser.environmentValue(context);
                    if (environmentValue.isPresent()) {
                        parameters.add(environmentValue.get());
                        continue;
                    }
                }

                // try parsing for a plugin with or without parameters
                {
                    final Optional<T> plugin = this.parseNameAndParameters(
                        parser,
                        provider,
                        context
                    );
                    if (plugin.isPresent()) {
                        parameters.add(plugin.get());
                        continue;
                    }
                }

                // try for a double literal
                {
                    final Optional<Double> number = parser.number();
                    if (number.isPresent()) {
                        parameters.add(number.get());
                        continue;
                    }
                }

                // try for a string literal
                {
                    try {
                        final Optional<String> string = parser.doubleQuotedString();
                        if (string.isPresent()) {
                            parameters.add(string.get());
                            continue;
                        }
                    } catch (final ParserException cause) {
                        throw new IllegalArgumentException(cause.getMessage(), cause);
                    }
                }

                if (parser.parameterSeparator()) {
                    continue;
                }

                if (parser.parametersEnd()) {
                    break;
                }

                // must be an invalid character complain!
                this.invalidCharacter(parser);
            }
        }

        return PluginSelectorParameterList.with(
            parameters,
            this.name
        );
    }

    /**
     * Helper that reports an invalid character.
     */
    private void invalidCharacter(final PluginExpressionParser<?> parser) {
        final TextCursorLineInfo lineInfo = parser.cursor.lineInfo();
        final int pos = Math.max(
            lineInfo.textOffset() - 1,
            0
        );

        throw new InvalidCharacterException(
            lineInfo.text()
                .toString(),
            pos
        ).setTextAndPosition(
            this.toString(),
            this.name().textLength() + pos
        );
    }

    // Comparable.......................................................................................................

    /**
     * First compare both names and then compare the text
     */
    @Override
    public int compareTo(final PluginSelector<N> other) {
        int result = this.name.compareTo(other.name);

        if (Comparators.EQUAL == result) {
            result = this.valueText.compareTo(other.valueText);
        }

        return result;
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.name,
            this.valueText
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof PluginSelector && this.equals0((PluginSelector<?>) other);
    }

    private boolean equals0(final PluginSelector<?> other) {
        return this.name.equals(other.name) &&
            this.valueText.equals(other.valueText);
    }

    /**
     * Note it is intentional that the {@link #valueText()} is in it raw form, this is to ensure that {@link #parse(String, Function)}
     * is able to successfully parse the string returned by {@link #toString()}.
     */
    @Override
    public String toString() {
        final String name = this.name.value();
        final String valueText = this.valueText;

        return valueText.isEmpty() ?
            name :
            valueText.startsWith("(") ?
                name.concat(valueText) :
                name + " " + valueText;
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        return this.toString();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.name.toString());

        final String valueText = this.valueText;
        if (false == valueText.isEmpty()) {
            printer.indent();
            {
                printer.println(
                    CharSequences.quoteAndEscape(valueText)
                );
            }
            printer.outdent();
        }
    }

    // json.............................................................................................................

    /**
     * Marshalls this instance to a {@link JsonNode} using the {@link #toString()} representation.
     */
    public JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshall(this.toString());
    }
}
