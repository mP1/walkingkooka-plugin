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
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;

final class PluginNameSetParser implements CanBeEmpty {

    static PluginNameSetParser with(final String text) {
        return new PluginNameSetParser(
            text
        );
    }

    private PluginNameSetParser(final String text) {
        this.text = text;
        this.cursor = TextCursors.charSequence(text);
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

    String name() {
        return NAME.parse(
                this.cursor,
                CONTEXT
            ).map(ParserToken::text)
            .orElse("");
    }

    private final static Parser<ParserContext> NAME = Parsers.charPredicateString(
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

    @Override
    public boolean isEmpty() {
        return this.cursor.isEmpty();
    }

    private final static ParserContext CONTEXT = ParserContexts.fake();

    private final String text;

    final TextCursor cursor;

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
