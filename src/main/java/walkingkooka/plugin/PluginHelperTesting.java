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
import walkingkooka.naming.Name;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface PluginHelperTesting<H extends PluginHelper<N, I, IS, S, A>,
        N extends Name & Comparable<N>,
        I extends PluginInfoLike<I, N>,
        IS extends PluginInfoSetLike<IS, I, N>,
        S extends PluginSelectorLike<N>,
        A extends PluginAliasLike<N, S, A>>
            extends ClassTesting<H> {

    // name.............................................................................................................

    @Test
    default void testNameWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginHelper().name(null)
        );
    }

    default void nameAndCheck(final String text,
                              final N expected) {
        this.nameAndCheck(
                this.createPluginHelper(),
                text,
                expected
        );
    }

    default void nameAndCheck(final H helper,
                              final String text,
                              final N expected) {
        this.checkEquals(
                expected,
                helper.name(text)
        );
    }

    // parseName.......................................................................................................

    @Test
    default void testParseNameWithNullTextCursorFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginHelper()
                        .parseName(
                                null,
                                ParserContexts.fake()
                        )
        );
    }

    @Test
    default void testParseNameWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginHelper()
                        .parseName(
                                TextCursors.fake(),
                                null
                        )
        );
    }

    default void parseNameAndCheck(final TextCursor cursor,
                                   final ParserContext context) {
        this.parseNameAndCheck(
                this.createPluginHelper(),
                cursor,
                context
        );
    }

    default void parseNameAndCheck(final TextCursor cursor,
                                   final ParserContext context,
                                   final N expected) {
        this.parseNameAndCheck(
                this.createPluginHelper(),
                cursor,
                context,
                expected
        );
    }

    default void parseNameAndCheck(final TextCursor cursor,
                                   final ParserContext context,
                                   final Optional<N> expected) {
        this.parseNameAndCheck(
                this.createPluginHelper(),
                cursor,
                context,
                expected
        );
    }

    default void parseNameAndCheck(final H helper,
                                   final TextCursor cursor,
                                   final ParserContext context) {
        this.parseNameAndCheck(
                helper,
                cursor,
                context,
                Optional.empty()
        );
    }

    default void parseNameAndCheck(final H helper,
                                   final TextCursor cursor,
                                   final ParserContext context,
                                   final N expected) {
        this.parseNameAndCheck(
                helper,
                cursor,
                context,
                Optional.of(expected)
        );
    }

    default void parseNameAndCheck(final H helper,
                                   final TextCursor cursor,
                                   final ParserContext context,
                                   final Optional<N> expected) {
        this.checkEquals(
                expected,
                helper.parseName(
                        cursor,
                        context
                )
        );
    }

    // names............................................................................................................

    @Test
    default void testNamesWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginHelper().names(null)
        );
    }

    default void namesAndCheck(final Set<N> names,
                               final Set<N> expected) {
        this.namesAndCheck(
                this.createPluginHelper(),
                names,
                expected
        );
    }

    default void namesAndCheck(final H helper,
                               final Set<N> names,
                               final Set<N> expected) {
        this.checkEquals(
                expected,
                helper.names(names)
        );
    }

    // name.............................................................................................................

    @Test
    default void testParseInfoWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginHelper().parseInfo(null)
        );
    }

    default void parseInfoAndCheck(final String text,
                                   final I expected) {
        this.parseInfoAndCheck(
                this.createPluginHelper(),
                text,
                expected
        );
    }

    default void parseInfoAndCheck(final H helper,
                                   final String text,
                                   final I expected) {
        this.checkEquals(
                expected,
                helper.parseInfo(text)
        );
    }

    // infoSet..........................................................................................................

    @Test
    default void testInfoSetWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginHelper()
                        .infoSet(null)
        );
    }

    default void infoSetAndCheck(final Set<I> infoSet,
                                 final IS expected) {
        this.infoSetAndCheck(
                this.createPluginHelper(),
                infoSet,
                expected
        );
    }

    default void infoSetAndCheck(final H helper,
                                 final Set<I> infoSet,
                                 final IS expected) {
        this.checkEquals(
                expected,
                helper.infoSet(infoSet)
        );
    }

    // parseSelector....................................................................................................

    @Test
    default void testParseSelectorWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginHelper()
                        .parseSelector(null)
        );
    }

    default void parseSelectorAndCheck(final String text,
                                       final S expected) {
        this.parseSelectorAndCheck(
                this.createPluginHelper(),
                text,
                expected
        );
    }

    default void parseSelectorAndCheck(final H helper,
                                       final String text,
                                       final S expected) {
        this.checkEquals(
                expected,
                helper.parseSelector(text)
        );
    }

    // alias............................................................................................................

    @Test
    default void testAliasWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginHelper()
                        .alias(
                                null,
                                Optional.empty(),
                                Optional.empty()
                        )
        );
    }

    @Test
    default void testAliasWithNullSelectorFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginHelper()
                        .alias(
                                this.createName(),
                                null,
                                Optional.empty()
                        )
        );
    }

    @Test
    default void testAliasWithNullUrlFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginHelper()
                        .alias(
                                this.createName(),
                                Optional.empty(),
                                null
                        )
        );
    }

    H createPluginHelper();


    N createName();
}
