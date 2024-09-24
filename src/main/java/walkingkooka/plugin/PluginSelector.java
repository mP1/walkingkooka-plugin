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
public final class PluginSelector<N extends Name> implements HasName<N>, HasText, TreePrintable {

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
        final int space = text.indexOf(' ');
        if (-1 == space) {
            nameText = text;
            textAfter = "";
        } else {
            nameText = text.substring(0, space);
            textAfter = text.substring(space + 1);
        }

        try {
            return new PluginSelector<>(
                    nameFactory.apply(nameText),
                    textAfter
            );
        } catch (final InvalidCharacterException cause) {
            throw cause.appendToMessage(" in " + CharSequences.quoteAndEscape(text));
        }
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
                           final String text) {
        this.name = name;
        this.text = text;
    }

    @Override
    public N name() {
        return this.name;
    }

    private final N name;

    /**
     * Returns the text with no escaping.
     */
    @Override
    public String text() {
        return this.text;
    }

    /**
     * Would be setter that returns a {@link PluginSelector} with the given text creating a new instance if necessary.
     */
    public PluginSelector<N> setText(final String text) {
        Objects.requireNonNull(text, "text");
        return this.text.equals(text) ?
                this :
                new PluginSelector<>(
                        this.name,
                        text
                );
    }

    private final String text;

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
                        .append(pluginSelectorLike.text());
                continue;
            }

            throw new IllegalArgumentException("Unsupported value " + CharSequences.quoteIfChars(value) + " " + value.getClass().getName() + " expected only double | String | PluginSelector");
        }

        if (b.length() > 0) {
            b.append(PluginExpressionParser.PARAMETER_END);
        }

        return this.setText(b.toString());
    }

    // evaluateText...............................................................................................

    /**
     * Parses the {@link #text()} as an expression that contains an optional parameter list which may include
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
     * The <code>provider</code> will be used to fetch <code>provided</code>> with any parameters.
     */
    public <N extends Name & Comparable<N>, T> T evaluateText(final BiFunction<TextCursor, ParserContext, Optional<N>> nameParserAndFactory,
                                                              final PluginSelectorEvaluateTextProvider<N, T> provider,
                                                              final ProviderContext context) {
        Objects.requireNonNull(nameParserAndFactory, "nameParserAndFactory");
        Objects.requireNonNull(provider, "provider");
        Objects.requireNonNull(context, "context");

        final String nameText = this.name()
                .value();
        final PluginExpressionParser<N> nameParser = PluginExpressionParser.with(
                nameText,
                nameParserAndFactory,
                context
        );

        final Optional<N> name = nameParser.name();
        if (false == name.isPresent() || false == nameParser.isEmpty()) {
            throw new IllegalArgumentException(
                    "Unable to parse name in " +
                            CharSequences.quoteAndEscape(nameText)
            );
        }

        final PluginExpressionParser<N> parser = PluginExpressionParser.with(
                this.text(),
                nameParserAndFactory,
                context
        );

        final List<?> parameters = parseParameters(
                parser,
                provider
        );

        parser.spaces();

        if (false == parser.isEmpty()) {
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
    private <N extends Name & Comparable<N>, T> Optional<T> parseNameAndParameters(final PluginExpressionParser<N> parser,
                                                                                   final PluginSelectorEvaluateTextProvider<N, T> provider) {
        final Optional<N> name = parser.name();

        final T provided;
        if (name.isPresent()) {
            provided = provider.get(
                    name.get(),
                    parseParameters(
                            parser,
                            provider
                    ),
                    parser.context
            );
        } else {
            provided = null;
        }

        return Optional.ofNullable(provided);
    }

    /**
     * Tries to parse a parameter list if an initial OPEN-PARENS is present.
     * <pre>
     * ( 1.23, "string-literal", $environmental-variable, plugin-name )
     * </pre>
     */
    private <N extends Name & Comparable<N>, T> List<Object> parseParameters(final PluginExpressionParser<N> parser,
                                                                             final PluginSelectorEvaluateTextProvider<N, T> provider) {
        parser.spaces();

        final List<Object> parameters = Lists.array();

        if (parser.parametersBegin()) {
            for (; ; ) {
                parser.spaces();

                {
                    final Optional<?> environmentValue = parser.environmentValue();
                    if (environmentValue.isPresent()) {
                        parameters.add(environmentValue.get());
                        continue;
                    }
                }

                // try parsing for a provided with or without parameters
                {
                    final Optional<T> plugin = parseNameAndParameters(
                            parser,
                            provider
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
                        final Optional<String> string = parser.string();
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

        return Lists.immutable(parameters);
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
                this.name().textLength() + pos + 1 // +1 or the SPACE following the name
        );
    }
    
    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.name,
                this.text
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof PluginSelector && this.equals0((PluginSelector<?>) other);
    }

    private boolean equals0(final PluginSelector<?> other) {
        return this.name.equals(other.name) &&
                this.text.equals(other.text);
    }

    /**
     * Note it is intentional that the {@link #text()} is in it raw form, this is to ensure that {@link #parse(String, Function)}
     * is able to successfully parse the string returned by {@link #toString()}.
     */
    @Override
    public String toString() {
        final String name = this.name.toString();
        final String text = this.text;

        return text.isEmpty() ?
                name :
                name + " " + text;
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.name.toString());

        final String text = this.text;
        if (false == text.isEmpty()) {
            printer.indent();
            {
                printer.println(
                        CharSequences.quoteAndEscape(text)
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
