/*
 * Copyright 2020 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.naming.HasName;
import walkingkooka.naming.Name;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a selector for a plugin holding the name and additional text that could hold some extra parameters.
 * The plugin will need to parse or handle the text itself.
 * <br>
 * Note the selector should handle serialization itself, with marshalling using the {@link #toString()} into a
 * {@link walkingkooka.tree.json.JsonString} and unmarshalling parsing an equivalent {@link walkingkooka.tree.json.JsonString}.
 */
public final class PluginSelector<N extends Name> implements HasName<N>, HasText, TreePrintable {

    /**
     * Parses the given text into a selector, giving the component {@link Name} and {@link String text} to the provided factory
     * <br>
     * Note the format of the text is name OPTIONAL-SPACE followed by TEXT. Note the TEXT supports escaping
     * <pre>
     * text-format-pattern @
     * </pre>
     */
    public static <N extends Name> PluginSelector<N> parse(final String text,
                                                           final Function<String, N> nameFactory) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final String textAfter;
        final String nameText;
        final int space = text.indexOf(' ');
        if (-1 == space) {
            nameText = text;
            textAfter = "";
        } else {
            nameText = text.substring(0, space);
            textAfter = text.substring(space + 1);
        }

        try {
            return new PluginSelector<>(
                    nameFactory.apply(nameText),
                    textAfter
            );
        } catch (final InvalidCharacterException cause) {
            throw cause.appendToMessage(" in " + CharSequences.quoteAndEscape(text));
        }
    }


    /**
     * Creates a new {@link PluginSelector}.
     */
    public static <N extends Name> PluginSelector<N> with(final N name,
                                                          final String text) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(text, "text");

        return new PluginSelector<>(
                name,
                text
        );
    }

    private PluginSelector(final N name,
                          final String text) {
        this.name = name;
        this.text = text;
    }

    @Override
    public N name() {
        return this.name;
    }

    private final N name;

    /**
     * Returns the text with no escaping.
     */
    @Override
    public String text() {
        return this.text;
    }
    
    private final String text;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.name,
                this.text
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof PluginSelector && this.equals0((PluginSelector<?>) other);
    }

    private boolean equals0(final PluginSelector<?> other) {
        return this.name.equals(other.name) &&
                this.text.equals(other.text);
    }

    /**
     * Note it is intentional that the {@link #text()} is in it raw form, this is to ensure that {@link #parse(String, Function)}
     * is able to successfully parse the string returned by {@link #toString()}.
     */
    @Override
    public String toString() {
        final String name = this.name.toString();
        final String text = this.text;

        return text.isEmpty() ?
                name :
                name + " " + text;
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.name.toString());

        final String text = this.text;
        if (false == text.isEmpty()) {
            printer.indent();
            {
                printer.println(
                        CharSequences.quoteAndEscape(text)
                );
            }
            printer.outdent();
        }
    }
}
