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
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.naming.HasName;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.DoubleParserToken;
import walkingkooka.text.cursor.parser.DoubleQuotedParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.math.MathContext;
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
    public static <N extends Name> PluginSelector<N> parse(final String text,
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
    public static <N extends Name> PluginSelector<N> with(final N name,
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
     * collection ( number-to-boolen, number-number, string-to-local-date "yyyy-mm-dd")
     * </pre>
     * The <code>provider</code> will be used to fetch <code>provided</code>> with any parameters.
     */
    public <N extends Name, T> T evaluateText(final BiFunction<TextCursor, ParserContext, Optional<N>> nameParserAndFactory,
                                              final BiFunction<N, List<?>, T> provider) {
        Objects.requireNonNull(nameParserAndFactory, "nameParserAndFactory");
        Objects.requireNonNull(provider, "provider");

        final String nameText = this.name().value();
        final TextCursor nameCursor = TextCursors.charSequence(nameText);
        final Optional<N> maybeName = nameParserAndFactory.apply(
                nameCursor,
                PARSER_CONTEXT
        );
        if (false == maybeName.isPresent() || false == nameCursor.isEmpty()) {
            throw new IllegalArgumentException(
                    "Unable to parse name in " +
                            CharSequences.quoteAndEscape(nameText)
            );
        }

        final TextCursor cursor = TextCursors.charSequence(this.text());

        final List<?> parameters = parseParameters(
                cursor,
                nameParserAndFactory,
                provider
        );

        skipSpaces(cursor);

        if (false == cursor.isEmpty()) {
            invalidCharacter(cursor);
        }

        return provider.apply(
                maybeName.get(),
                parameters
        );
    }

    /**
     * Attempts to parse an optional plugin including its parameters which must be within parens.
     */
    private <N extends Name, T> Optional<T> parseNameParametersAndCreate(final TextCursor cursor,
                                                                         final BiFunction<TextCursor, ParserContext, Optional<N>> nameParserAndFactory,
                                                                         final BiFunction<N, List<?>, T> provider) {
        final Optional<N> maybeName = nameParserAndFactory.apply(
                cursor,
                PARSER_CONTEXT
        );

        final T provided;
        if (maybeName.isPresent()) {
            provided = provider.apply(
                    maybeName.get(),
                    parseParameters(
                            cursor,
                            nameParserAndFactory,
                            provider
                    )
            );
        } else {
            provided = null;
        }

        return Optional.ofNullable(provided);
    }

    /**
     * Tries to parse a parameter list if an OPEN-PARENS is present.
     */
    private <N extends Name, T> List<Object> parseParameters(final TextCursor cursor,
                                                             final BiFunction<TextCursor, ParserContext, Optional<N>> nameParserAndFactory,
                                                             final BiFunction<N, List<?>, T> provider) {
        skipSpaces(cursor);

        final List<Object> parameters = Lists.array();

        if (tryMatch(PARAMETER_BEGIN, cursor)) {
            for (; ; ) {
                skipSpaces(cursor);

                // try parsing for a provided with or without parameters
                final Optional<T> provided = parseNameParametersAndCreate(
                        cursor,
                        nameParserAndFactory,
                        provider
                );
                if (provided.isPresent()) {
                    parameters.add(provided.get());
                    continue;
                }

                // try for a double literal
                final Optional<Double> maybeNumber = NUMBER_LITERAL.parse(
                        cursor,
                        PARSER_CONTEXT
                ).map(
                        t -> t.cast(DoubleParserToken.class).value()
                );
                if (maybeNumber.isPresent()) {
                    parameters.add(maybeNumber.get());
                    continue;
                }

                // try for a string literal
                try {
                    final Optional<String> maybeString = STRING_LITERAL.parse(
                            cursor,
                            PARSER_CONTEXT
                    ).map(
                            t -> t.cast(DoubleQuotedParserToken.class).value()
                    );
                    if (maybeString.isPresent()) {
                        parameters.add(maybeString.get());
                        continue;
                    }
                } catch (final ParserException cause) {
                    throw new IllegalArgumentException(cause.getMessage(), cause);
                }

                if (tryMatch(PARAMETER_SEPARATOR, cursor)) {
                    continue;
                }

                if (tryMatch(PARAMETER_END, cursor)) {
                    break;
                }

                // must be an invalid character complain!
                invalidCharacter(cursor);
            }
        }

        return Lists.immutable(parameters);
    }

    /**
     * Consumes any whitespace, don't really care how many or if any were skipped.
     */
    private static void skipSpaces(final TextCursor cursor) {
        SPACE.parse(cursor, PARSER_CONTEXT);
    }

    /**
     * Matches any whitespace.
     */
    private final static Parser<ParserContext> SPACE = Parsers.character(CharPredicates.whitespace())
            .repeating();

    /**
     * Returns true if the token represented by the given {@link Parser} was found.
     */
    private static boolean tryMatch(final Parser<ParserContext> parser,
                                    final TextCursor cursor) {
        return parser.parse(
                cursor,
                PARSER_CONTEXT
        ).isPresent();
    }

    /**
     * Matches a LEFT PARENS which marks the start of a plugin parameters.
     */
    private final static Parser<ParserContext> PARAMETER_BEGIN = Parsers.string("(", CaseSensitivity.SENSITIVE);

    /**
     * Matches a COMMA which separates individual parameters.
     */
    private final static Parser<ParserContext> PARAMETER_SEPARATOR = Parsers.string(",", CaseSensitivity.SENSITIVE);

    /**
     * Matches a RIGHT PARENS which marks the end of a plugin parameters.
     */
    private final static Parser<ParserContext> PARAMETER_END = Parsers.string(")", CaseSensitivity.SENSITIVE);

    /**
     * Number literal parameters are double literals using DOT as the decimal separator.
     */
    private final static Parser<ParserContext> NUMBER_LITERAL = Parsers.doubleParser();

    /**
     * String literal parameters must be double-quoted and support backslash escaping.
     */
    private final static Parser<ParserContext> STRING_LITERAL = Parsers.doubleQuoted();

    /**
     * Singleton which can be reused.
     */
    private final static ParserContext PARSER_CONTEXT = ParserContexts.basic(
            DateTimeContexts.fake(), // dates are not supported
            DecimalNumberContexts.american(MathContext.UNLIMITED) // only the decimal char is actually required.
    );

    /**
     * Helper that reports an invalid character.
     */
    private void invalidCharacter(final TextCursor cursor) {
        final TextCursorLineInfo lineInfo = cursor.lineInfo();
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
}
