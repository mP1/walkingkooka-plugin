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

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Objects;

/**
 * Should be used to present a text component within a {@link PluginSelector} along with possible alternatives.
 * <pre>
 * dd / mm / yyyy
 *
 * DAY SLASH MONTH SLASH YEAR
 * </pre>
 * The above spreadsheet format pattern has 5 {@link PluginSelectorTextComponent}, the dd component would have several
 * alternatives such as D, DDD, DDDD, DDDDD
 */
public final class PluginSelectorTextComponent implements PluginSelectorTextComponentLike<PluginSelectorTextComponent, PluginSelectorTextComponentAlternative> {

    public static PluginSelectorTextComponent with(final String label,
                                                   final String text,
                                                   final List<PluginSelectorTextComponentAlternative> alternatives) {
        return new PluginSelectorTextComponent(
                Objects.requireNonNull(label, "label"),
                Objects.requireNonNull(text, "text"),
                Lists.immutable(
                        Objects.requireNonNull(alternatives, "alternatives")
                )
        );
    }

    private PluginSelectorTextComponent(final String label,
                                        final String text,
                                        final List<PluginSelectorTextComponentAlternative> alternatives) {
        this.label = label;
        this.text = text;
        this.alternatives = alternatives;
    }

    public String label() {
        return this.label;
    }

    private final String label;

    @Override
    public String text() {
        return this.text;
    }

    private final String text;

    public List<PluginSelectorTextComponentAlternative> alternatives() {
        return this.alternatives;
    }

    private final List<PluginSelectorTextComponentAlternative> alternatives;

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.label,
                this.text,
                this.alternatives
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof PluginSelectorTextComponent &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final PluginSelectorTextComponent other) {
        return this.label.equals(other.label) &&
                this.text.equals(other.text) &&
                this.alternatives.equals(other.alternatives);
    }

    @Override
    public String toString() {
        return CharSequences.quoteAndEscape(this.label) +
                " " +
                CharSequences.quoteAndEscape(this.text) +
                " " +
                this.alternatives;
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.label);
        printer.println(this.text);

        printer.indent();
        {
            for (final PluginSelectorTextComponentAlternative pluginSelectorTextComponentAlternative : this.alternatives) {
                pluginSelectorTextComponentAlternative.printTree(printer);
            }
        }
        printer.outdent();
    }

    // Json.............................................................................................................

    /**
     * Factory that creates a {@link PluginSelectorTextComponent} from a {@link JsonNode}.
     */
    static PluginSelectorTextComponent unmarshall(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {
        return PluginSelectorTextComponentLike.unmarshall(
                node,
                context,
                PluginSelectorTextComponent::with,
                PluginSelectorTextComponentAlternative.class
        );
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(PluginSelectorTextComponent.class),
                PluginSelectorTextComponent::unmarshall,
                PluginSelectorTextComponent::marshall,
                PluginSelectorTextComponent.class
        );
    }
}
