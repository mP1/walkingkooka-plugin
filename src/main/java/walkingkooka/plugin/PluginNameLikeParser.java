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
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;

import java.util.function.Function;

/**
 * A parser that may be used to parse a CSV of names with optional surrounding {@link Name names}.
 */
final class PluginNameLikeParser<N extends Name & Comparable<N>> {

    static <N extends Name & Comparable<N>> PluginNameLikeParser<N> with(final String text,
                                                                         final Function<String, N> nameParser) {
        return new PluginNameLikeParser<>(
                text,
                nameParser
        );
    }

    private PluginNameLikeParser(final String text,
                                 final Function<String, N> nameParser) {
        this.text = text;
        this.cursor = TextCursors.charSequence(text);
        this.nameParser = nameParser;
    }

    String spaces() {
        return SPACES.parse(
                        this.cursor,
                        CONTEXT
                ).map(ParserToken::text)
                .orElse("");
    }

    private final static Parser<ParserContext> SPACES = Parsers.stringCharPredicate(
            CharPredicates.is(' '),
            1,
            Character.MAX_VALUE
    );

    N name() {
        final String name = NAME.parse(
                        this.cursor,
                        CONTEXT
                ).map(ParserToken::text)
                .orElse("");

        if(name.isEmpty()) {
            this.invalidCharacterException();
        }

        try {
            return this.nameParser.apply(name);
        } catch (final InvalidCharacterException cause) {
            throw this.handleInvalidCharacterException(
                    cause,
                    name
            );
        }
    }

    private final Function<String, N> nameParser;

    private final static Parser<ParserContext> NAME = Parsers.stringCharPredicate(
            CharPredicates.any(" " + PluginInfoSetLike.SEPARATOR.character()).negate(),
            1,
            Character.MAX_VALUE
    );

    String comma() {
        return COMMA.parse(
                        this.cursor,
                        CONTEXT
                ).map(ParserToken::text)
                .orElse("");
    }

    private final static Parser<ParserContext> COMMA = Parsers.character(
            CharPredicates.is(
                    PluginInfoSetLike.SEPARATOR.character()
            )
    );

    boolean isEmpty() {
        return this.cursor.isEmpty();
    }

    private final static ParserContext CONTEXT = ParserContexts.fake();

    private final String text;

    private final TextCursor cursor;

    private InvalidCharacterException handleInvalidCharacterException(final InvalidCharacterException cause,
                                                                      final String token) {
        return cause.setTextAndPosition(
                this.text,
                this.cursor.lineInfo()
                        .textOffset() -
                        token.length() +
                        cause.position()
        );
    }

    void invalidCharacterException() {
        throw new InvalidCharacterException(
                this.text,
                this.cursor.lineInfo()
                        .textOffset()
        );
    }
}
