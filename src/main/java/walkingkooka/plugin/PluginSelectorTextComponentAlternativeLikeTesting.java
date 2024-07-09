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
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface PluginSelectorTextComponentAlternativeLikeTesting<T extends PluginSelectorTextComponentAlternativeLike> extends HasTextTesting,
        TreePrintableTesting,
        HashCodeEqualsDefinedTesting2<T> {

    String LABEL = "Label123";

    String TEXT = "Text123";

    @Test
    default void testWithNullLabelFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginSelectorTextComponentAlternativeLike(
                        null,
                        TEXT
                )
        );
    }

    @Test
    default void testWithNullTextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginSelectorTextComponentAlternativeLike(
                        LABEL,
                        null
                )
        );
    }

    @Test
    default void testWith() {
        final T alternative = this.createPluginSelectorTextComponentAlternativeLike(
                LABEL,
                TEXT
        );
        this.checkEquals(
                LABEL,
                alternative.label(),
                "label"
        );
        this.textAndCheck(
                alternative,
                TEXT
        );
    }

    @Test
    default void testWithEmptyLabelAndEmptyText() {
        final String label = "";
        final String text = "";

        final T alternative = this.createPluginSelectorTextComponentAlternativeLike(
                label,
                text
        );
        this.checkEquals(
                label,
                alternative.label(),
                "label"
        );
        this.textAndCheck(
                alternative,
                text
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    default void testEqualsDifferentLabel() {
        this.checkNotEquals(
                this.createPluginSelectorTextComponentAlternativeLike(
                        "different " + LABEL,
                        TEXT
                )
        );
    }

    @Test
    default void testEqualsDifferentText() {
        this.checkNotEquals(
                this.createPluginSelectorTextComponentAlternativeLike(
                        LABEL,
                        "different " + TEXT
                )
        );
    }

    @Override
    default T createObject() {
        return this.createPluginSelectorTextComponentAlternativeLike(
                LABEL,
                TEXT
        );
    }

    T createPluginSelectorTextComponentAlternativeLike(final String label,
                                                       final String text);
}
