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
import walkingkooka.naming.Name;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printers;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface PluginSelectorMenuLikeTesting<M extends PluginSelectorMenuLike<P, N>, P extends PluginSelectorLike<N>, N extends Name & Comparable<N>> extends ClassTesting<M>,
        HashCodeEqualsDefinedTesting2<M>,
        TreePrintableTesting {

    @Test
    default void testWithNullLabelFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginSelectorMenu(
                        null,
                        this.createPluginSelector()
                )
        );
    }

    @Test
    default void testWithEmptyLabelFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> this.createPluginSelectorMenu(
                        "",
                        this.createPluginSelector()
                )
        );
    }

    @Test
    default void testWithNullPluginSelectorFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPluginSelectorMenu(
                        "Label123",
                        null
                )
        );
    }

    @Test
    default void testWith() {
        final String label = "Label123";
        final P pluginSelector = this.createPluginSelector();

        final M menu = this.createPluginSelectorMenu(
                label,
                pluginSelector
        );
        this.checkEquals(
                label,
                menu.label(),
                "label"
        );
        this.checkEquals(
                pluginSelector,
                menu.selector(),
                "selector"
        );
    }

    M createPluginSelectorMenu(final String label,
                               final P selector);

    P createPluginSelector();


    // TreePrintable....................................................................................................

    @Test
    default void testPrintTree() {
        final String label = "Label123";
        final P selector = this.createPluginSelector();

        final StringBuilder b = new StringBuilder();
        final IndentingPrinter printer = Printers.stringBuilder(
                b,
                EOL
        ).indenting(INDENTATION);

        printer.println(label);
        printer.indent();
        {
            selector.printTree(printer);
        }
        printer.outdent();
        printer.flush();

        this.treePrintAndCheck(
                this.createPluginSelectorMenu(
                        label,
                        selector
                ),
                b.toString()
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    default void testEqualsDifferentLabel() {
        this.checkNotEquals(
                this.createPluginSelectorMenu(
                        "Different Label123",
                        this.createPluginSelector()
                )
        );
    }

    @Test
    default void testEqualsDifferentSelector() {
        this.checkNotEquals(
                this.createPluginSelectorMenu(
                        "Label123",
                        this.createDifferentPluginSelector()
                )
        );
    }

    @Override
    default M createObject() {
        return this.createPluginSelectorMenu(
                "Label123",
                this.createPluginSelector()
        );
    }

    P createDifferentPluginSelector();
}
