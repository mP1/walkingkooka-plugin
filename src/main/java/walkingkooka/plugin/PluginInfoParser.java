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
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;

import java.util.Objects;
import java.util.function.Function;

final class PluginInfoParser<N extends Name & Comparable<N>> implements CanBeEmpty {

    static <N extends Name & Comparable<N>> PluginInfoParser<N> with(final String text,
                                                                     final Function<String, N> name) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(name, "name");

        return new PluginInfoParser<>(
            text,
            name
        );
    }

    private PluginInfoParser(final String text,
                             final Function<String, N> name) {
        this.text = text;
        this.cursor = TextCursors.charSequence(text);
        this.name = name;
    }

    String spaces() {
        return SKIP_SPACES.parse(
                this.cursor,
                CONTEXT
            ).map(ParserToken::text)
            .orElse("");
    }

    private final static Parser<ParserContext> SKIP_SPACES = Parsers.charPredicateString(
        CharPredicates.is(' '),
        1,
        Character.MAX_VALUE
    );

    AbsoluteUrl url() {
        final String token = this.token();
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Missing url");
        }

        try {
            return Url.parseAbsolute(token);
        } catch (final InvalidCharacterException cause) {
            throw this.handleInvalidCharacterException(
                cause,
                token
            );
        }
    }

    N name() {
        final String token = this.token();
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Missing name");
        }

        try {
            return this.name.apply(token);
        } catch (final InvalidCharacterException cause) {
            throw this.handleInvalidCharacterException(
                cause,
                token
            );
        }
    }

    private final Function<String, N> name;

    private String token() {
        return SKIP_NOT_SPACES.parse(
                this.cursor,
                CONTEXT
            ).map(ParserToken::text)
            .orElse("");
    }

    private final static Parser<ParserContext> SKIP_NOT_SPACES = Parsers.charPredicateString(
        CharPredicates.is(' ').negate(),
        1,
        Character.MAX_VALUE
    );

    @Override
    public boolean isEmpty() {
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
        throw this.cursor.lineInfo()
            .invalidCharacterException()
            .get();
    }
}
