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

import walkingkooka.naming.Name;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;

/**
 * Value type that holds a sample for a selector.
 * @param <S>
 * @param <N>
 */
public final class PluginSelectorSample<S extends PluginSelectorLike<N>, N extends Name & Comparable<N>> implements TreePrintable {

    public static <S extends PluginSelectorLike<N>, N extends Name & Comparable<N>> PluginSelectorSample<S, N> with(final String label,
                                                                                                                    final S selector) {
        return new PluginSelectorSample<>(
                CharSequences.failIfNullOrEmpty(label, "label"),
                Objects.requireNonNull(selector, "selector")
        );
    }

    private PluginSelectorSample(final String label,
                                 final S selector) {
        this.label = label;
        this.selector = selector;
    }

    public String label() {
        return this.label;
    }

    private final String label;

    public S selector() {
        return this.selector;
    }

    private final S selector;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.label,
                this.selector
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof PluginSelector && this.equals0((PluginSelectorSample<?, ?>) other);
    }

    private boolean equals0(final PluginSelectorSample<?, ?> other) {
        return this.label.equals(other.label) &&
                this.selector.equals(other.selector);
    }

    @Override
    public String toString() {
        return this.label + " " + selector;
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.label.toString());

        printer.indent();
        {
            this.selector.printTree(printer);
        }
        printer.outdent();
    }
}
