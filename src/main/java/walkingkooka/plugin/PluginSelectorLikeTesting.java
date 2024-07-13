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
import walkingkooka.ToStringTesting;
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

    T createPluginSelectorLike(final N name,
                               final String text);

    N createName(final String name);

    // TypeNameTesting..................................................................................................

    @Override
    default String typeNameSuffix() {
        return "Selector";
    }
}