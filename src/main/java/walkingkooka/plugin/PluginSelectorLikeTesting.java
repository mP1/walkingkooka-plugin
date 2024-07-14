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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.naming.HasNameTesting;
import walkingkooka.naming.Name;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface PluginSelectorLikeTesting<T extends PluginSelectorLike<N>, N extends Name> extends TreePrintableTesting,
        HasNameTesting<N>,
        HasTextTesting,
        HashCodeEqualsDefinedTesting2<T>,
        ToStringTesting<T>,
        ParseStringTesting<T>,
        JsonNodeMarshallingTesting<T>,
        ClassTesting<T>,
        TypeNameTesting<T> {

    String TEXT = "";

    @Test
    default void testWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginSelectorLike(
                        null,
                        TEXT
                )
        );
    }

    @Test
    default void testWithNullTextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginSelectorLike(
                        this.createName("name"),
                        null
                )
        );
    }

    @Test
    default void testWith() {
        final N name = this.createName("name");
        final T selector = this.createPluginSelectorLike(
                name,
                TEXT
        );

        this.nameAndCheck(selector, name);
        this.textAndCheck(
                selector,
                TEXT
        );
    }

    // setName..........................................................................................................

    @Test
    default void testSetNameWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginSelectorLike(
                        this.createName("name"),
                        TEXT
                ).setName(null)
        );
    }

    @Test
    default void testSetNameWithSame() {
        final N name = this.createName("name");
        final T selector = this.createPluginSelectorLike(
                name,
                TEXT
        );
        assertSame(
                selector,
                selector.setName(name)
        );
    }

    @Test
    default void testSetNameWithDifferent() {
        final T selector = this.createPluginSelectorLike(
                this.createName("name"),
                TEXT
        );
        final N differentName = this.createName("different");
        final T different = (T) selector.setName(differentName);

        assertNotSame(
                different,
                selector
        );
        this.checkEquals(
                differentName,
                different.name(),
                "name"
        );
        this.textAndCheck(
                selector,
                TEXT
        );
    }

    // setText..........................................................................................................

    @Test
    default void testSetTextWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginSelectorLike(
                        this.createName("name"),
                        TEXT
                ).setText(null)
        );
    }

    @Test
    default void testSetTextWithSame() {
        final T selector = this.createPluginSelectorLike(
                this.createName("name"),
                TEXT
        );
        assertSame(
                selector,
                selector.setText(TEXT)
        );
    }

    @Test
    default void testSetTextWithSame2() {
        final String text = "(\"Hello\")";

        final T selector = this.createPluginSelectorLike(
                this.createName("name"),
                text
        );
        assertSame(
                selector,
                selector.setText(text)
        );
    }

    @Test
    default void testSetTextWithDifferentText() {
        final N name = this.createName("name");
        final T selector = this.createPluginSelectorLike(
                name,
                TEXT
        );
        final String differentText = "(\"Different\")";
        final T different = (T) selector.setText(differentText);

        assertNotSame(
                different,
                selector
        );
        this.nameAndCheck(
                different,
                name
        );
        this.textAndCheck(
                different,
                differentText
        );
        this.textAndCheck(
                selector,
                TEXT
        );
    }

    // setValues........................................................................................................

    @Test
    default void testSetValuesWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginSelectorLike(
                        this.createName("name"),
                        TEXT
                ).setValues(null)
        );
    }

    @Test
    default void testSetValuesWithSame() {
        final T selector = this.createPluginSelectorLike(
                this.createName("name"),
                TEXT
        );
        assertSame(
                selector,
                selector.setValues(Lists.empty())
        );
    }

    @Test
    default void testSetValues() {
        final T selector = this.createPluginSelectorLike(
                this.createName("name1"),
                TEXT
        );
        final T different = (T) selector.setValues(
                Lists.of(
                        "Hello1",
                        1.0,
                        this.createPluginSelectorLike(
                                this.createName("nested2"),
                                "(\"Hello2\", 2)"
                        )
                )
        );
        this.textAndCheck(
                different,
                "(\"Hello1\", 1.0, nested2(\"Hello2\", 2))"
        );
    }

    // equals...........................................................................................................

    @Test
    default void testEqualsDifferentName() {
        this.checkNotEquals(
                this.createPluginSelectorLike(
                        this.createName("different"),
                        TEXT
                )
        );
    }

    @Test
    default void testEqualsDifferentText() {
        this.checkNotEquals(
                this.createPluginSelectorLike(
                        this.createName("name123"),
                        "different"
                )
        );
    }

    @Override
    default T createObject() {
        return this.createPluginSelectorLike(
                this.createName("name123"),
                TEXT
        );
    }

    // ToString.........................................................................................................

    @Test
    default void testToString() {
        this.toStringAndCheck(
                this.createPluginSelectorLike(
                        this.createName("super123"),
                        "\"Hello\""
                ),
                "super123 " + "\"Hello\""
        );
    }

    // parse............................................................................................................

    @Test
    default void testParseInvalidNameFails() {
        this.parseStringFails(
                "A!34",
                new InvalidCharacterException("A!34", 1)
                        .appendToMessage(" in \"A!34\"")
        );
    }

    @Test
    default void testParseName() {
        final String text = "super123";
        this.parseStringAndCheck(
                text,
                this.createPluginSelectorLike(
                        this.createName(text),
                        ""
                )
        );
    }

    // PluginSelectorLike.parse must be able to parse all PluginSelectorLike.toString.

    @Test
    default void testParseToString() {
        final T selector = this.parseString("supermagic123 \"hello\"");

        this.parseStringAndCheck(
                selector.toString(),
                selector
        );
    }

    @Override
    default Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    default RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // TreePrintable....................................................................................................

    @Test
    default void testTreePrintWithoutText() {
        this.treePrintAndCheck(
                this.createPluginSelectorLike(
                        this.createName("abc123"),
                        ""
                ),
                "abc123\n"
        );
    }

    @Test
    default void testTreePrintWithText() {
        this.treePrintAndCheck(
                this.createPluginSelectorLike(
                        this.createName("abc123"),
                        "\"$0.00\""
                ),
                "abc123\n" +
                        "  \"$0.00\"\n"
        );
    }

    T createPluginSelectorLike(final N name,
                               final String text);

    N createName(final String name);

    // TypeNameTesting..................................................................................................

    @Override
    default String typeNameSuffix() {
        return "Selector";
    }
}
