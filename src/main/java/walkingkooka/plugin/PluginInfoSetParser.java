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

final class PluginInfoSetParser<N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>> {

    static <N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>> PluginInfoSetParser<N, I> with(final String text,
                                                                                                           final Function<String, I> infoParser) {
        return new PluginInfoSetParser<>(
            text,
            infoParser
        );
    }

    private PluginInfoSetParser(final String text,
                                final Function<String, I> infoParser) {
        this.text = text;
        this.cursor = TextCursors.charSequence(text);
        this.infoParser = infoParser;
    }

    String spaces() {
        return SPACES.parse(
                this.cursor,
                CONTEXT
            ).map(ParserToken::text)
            .orElse("");
    }

    private final static Parser<ParserContext> SPACES = Parsers.charPredicateString(
        CharPredicates.is(' '),
        1,
        Character.MAX_VALUE
    );

    I info() {
        final String url = this.url();
        final String spaces = this.spaces();
        final String name = this.name();

        final String token = url +
            spaces +
            name;

        try {
            return this.infoParser.apply(token);
        } catch (final InvalidCharacterException cause) {
            throw this.handleInvalidCharacterException(
                cause,
                token
            );
        }
    }

    private final Function<String, I> infoParser;

    private String url() {
        return URL.parse(
                this.cursor,
                CONTEXT
            ).map(ParserToken::text)
            .orElse("");
    }

    private final static Parser<ParserContext> URL = Parsers.charPredicateString(
        CharPredicates.any(" ").negate(),
        1,
        Character.MAX_VALUE
    );

    private String name() {
        return NAME.parse(
                this.cursor,
                CONTEXT
            ).map(ParserToken::text)
            .orElse("");
    }

    private final static Parser<ParserContext> NAME = Parsers.charPredicateString(
        CharPredicates.any(" " + PluginInfoSetLike.SEPARATOR.character()).negate(),
        PluginName.MIN_LENGTH,
        PluginName.MAX_LENGTH
    );

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
}
