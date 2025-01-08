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

import walkingkooka.CanBeEmpty;
import walkingkooka.InvalidCharacterException;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.DoubleParserToken;
import walkingkooka.text.cursor.parser.DoubleQuotedParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;

import java.math.MathContext;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A parser that may be used to parse individual tokens within a plugin expression such as a selector.
 */
final class PluginExpressionParser<N extends Name & Comparable<N>> implements CanBeEmpty {

    static <N extends Name & Comparable<N>> PluginExpressionParser<N> with(final String text,
                                                                           final BiFunction<TextCursor, ParserContext, Optional<N>> nameParser) {
        return new PluginExpressionParser<>(
            Objects.requireNonNull(text, "text"),
            Objects.requireNonNull(nameParser, "parseName")
        );
    }

    private PluginExpressionParser(final String text,
                                   final BiFunction<TextCursor, ParserContext, Optional<N>> nameParser) {
        this.cursor = TextCursors.charSequence(text);
        this.nameParser = nameParser;
    }

    /**
     * Tries to consume a {@link Name}
     */
    Optional<N> name() {
        return this.nameParser.apply(
            this.cursor,
            PARSER_CONTEXT
        );
    }

    private final BiFunction<TextCursor, ParserContext, Optional<N>> nameParser;

    /**
     * Consumes any whitespace, don't really care how many or if any were skipped.
     */
    boolean spaces() {
        return text(SPACE)
            .isPresent();
    }

    private final static Parser<ParserContext> SPACE = Parsers.character(CharPredicates.whitespace())
        .repeating();

    /**
     * Matches a LEFT PARENS which marks the start of a plugin parameters.
     */
    boolean parametersBegin() {
        return this.text(PARAMETER_BEGIN_PARSER)
            .isPresent();
    }

    final static String PARAMETER_BEGIN = "(";

    private final static Parser<ParserContext> PARAMETER_BEGIN_PARSER = Parsers.string(
        PARAMETER_BEGIN,
        CaseSensitivity.SENSITIVE
    );

    /**
     * Matches a COMMA which separates individual parameters.
     */
    boolean parameterSeparator() {
        return this.text(PARAMETER_SEPARATOR_PARSER)
            .isPresent();
    }

    final static char PARAMETER_SEPARATOR_CHARACTER = ',';

    final static String PARAMETER_SEPARATOR = "" + PARAMETER_SEPARATOR_CHARACTER;

    private final static Parser<ParserContext> PARAMETER_SEPARATOR_PARSER = Parsers.string(
        PARAMETER_SEPARATOR,
        CaseSensitivity.SENSITIVE
    );

    /**
     * Matches a RIGHT PARENS which marks the end of a plugin parameters.
     */
    boolean parametersEnd() {
        return this.text(PARAMETER_END_PARSER)
            .isPresent();
    }

    final static String PARAMETER_END = ")";

    private final static Parser<ParserContext> PARAMETER_END_PARSER = Parsers.string(
        PARAMETER_END,
        CaseSensitivity.SENSITIVE
    );

    /**
     * Tries to parse a number value.
     */
    Optional<Double> number() {
        return this.token(
            NUMBER_LITERAL,
            t -> t.cast(DoubleParserToken.class).value()
        );
    }

    /**
     * Number literal parameters are double literals using DOT as the decimal separator.
     */
    private final static Parser<ParserContext> NUMBER_LITERAL = Parsers.doubleParser();

    /**
     * Tries to parse a string literal.
     */
    Optional<String> doubleQuotedString() {
        return this.token(
            DOUBLE_QUOTED_STRING_LITERAL,
            t -> t.cast(DoubleQuotedParserToken.class).value()
        );
    }

    /**
     * String literal parameters must be double-quoted and support backslash escaping.
     */
    private final static Parser<ParserContext> DOUBLE_QUOTED_STRING_LITERAL = Parsers.doubleQuoted();

    /**
     * Tries to parse a url.
     */
    Optional<AbsoluteUrl> url() {
        Optional<AbsoluteUrl> url;

        final TextCursorSavePoint save = this.cursor.save();
        try {
            url = this.token(
                NON_SPACE_TOKEN,
                t -> Url.parseAbsolute(t.text())
            );
        } catch (final RuntimeException badUrl) {
            save.restore();
            url = Optional.empty();
        }
        return url;
    }

    /**
     * Parsers a token being terminated by whitespace
     */
    private final static Parser<ParserContext> NON_SPACE_TOKEN = Parsers.stringCharPredicate(
        CharPredicates.whitespace().negate(),
        1,
        Integer.MAX_VALUE
    );

    /**
     * Tries to parse an environmental variable returning its actual value from the {@link ProviderContext}.
     */
    Optional<Object> environmentValue(final ProviderContext context) {
        return this.token(
            ENVIRONMENT_VALUE_NAME,
            s -> context.environmentValueOrFail(
                EnvironmentValueName.with(
                    s.text()
                        .substring(1) // skip leading DOLLAR-SIGN
                )
            )
        );
    }

    /**
     * Parses a DOLLAR-SIGN then {@link EnvironmentValueName}
     */
    private final static Parser<ParserContext> ENVIRONMENT_VALUE_NAME = Parsers.sequenceParserBuilder()
        .required(
            Parsers.string("$", CaseSensitivity.SENSITIVE)
        ).required(
            Parsers.stringInitialAndPartCharPredicate(
                EnvironmentValueName.INITIAL,
                EnvironmentValueName.PART,
                2,
                EnvironmentValueName.MAX_LENGTH
            )
        ).build();

    private Optional<String> text(final Parser<ParserContext> parser) {
        return this.token(
            parser,
            ParserToken::text
        );
    }

    private <T> Optional<T> token(final Parser<ParserContext> parser,
                                  final Function<ParserToken, T> mapper) {
        return parser.parse(
            this.cursor,
            PARSER_CONTEXT
        ).map(mapper);
    }

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
    InvalidCharacterException invalidCharacter() {
        final TextCursorLineInfo lineInfo = this.cursor.lineInfo();

        final String text = lineInfo.text()
            .toString();
        int pos = lineInfo.textOffset();
        if (pos >= text.length()) {
            pos--;
        }

        return new InvalidCharacterException(
            text,
            pos
        );
    }

    @Override
    public boolean isEmpty() {
        return this.cursor.isEmpty();
    }

    final TextCursor cursor;

    @Override
    public String toString() {
        return this.cursor.toString();
    }
}
