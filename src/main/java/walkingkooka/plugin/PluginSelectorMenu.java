/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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

import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.naming.Name;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * Instances represent a single menu item for a {@link PluginSelectorLike}. Some selectors should have multiple menu items,
 * such as SpreadsheetFormatterName#DATE_FORMAT_PATTERN which might have a SHORT, MEDIUM, LONG menu items. Instances of
 * this class should be wrapped by another class which will also register itself to support json marshall/unmarshalling.
 */
public final class PluginSelectorMenu<P extends PluginSelectorLike<N>, N extends Name> implements PluginSelectorMenuLike<P, N> {

    /**
     * Factory that creates a {@link PluginSelectorMenu}.
     */
    public static <P extends PluginSelectorLike<N>, N extends Name> PluginSelectorMenu<P, N> with(final String label,
                                                                                                  final P selector) {
        return new PluginSelectorMenu<>(
                CharSequences.failIfNullOrEmpty(label, "label"),
                Objects.requireNonNull(selector, "selector")
        );
    }

    private PluginSelectorMenu(final String label,
                               final P selector) {
        this.label = label;
        this.selector = selector;
    }

    /**
     * The label for this {@link PluginSelector}.
     */
    @Override
    public String label() {
        return this.label;
    }

    private final String label;

    /**
     * The selector for this menu item. This will probably become the HistoryToken#setSave value.
     */
    @Override
    public P selector() {
        return this.selector;
    }

    private final P selector;

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
        return other == this ||
                other instanceof PluginSelectorMenu && this.equals0((PluginSelectorMenu<?, ?>) other);
    }

    private boolean equals0(final PluginSelectorMenu<?, ?> other) {
        return this.label.equals(other.label) &&
                this.selector.equals(other.selector);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.label)
                .value(this.selector)
                .build();
    }

    // json.............................................................................................................

    public static <P extends PluginSelectorLike<N>, N extends Name> PluginSelectorMenu<P, N> unmarshall(final JsonNode node,
                                                                                                        final JsonNodeUnmarshallContext context,
                                                                                                        final Class<P> pluginSelectorType) {
        String label = null;
        P selector = null;


        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case LABEL_PROPERTY_STRING:
                    label = context.unmarshall(
                            child,
                            String.class
                    );
                    break;
                case SELECTOR_PROPERTY_STRING:
                    selector = context.unmarshall(
                            child,
                            pluginSelectorType
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        if (null == label) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(LABEL_PROPERTY, node);
        }
        if (null == selector) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(SELECTOR_PROPERTY, node);
        }
        return with(
                label,
                selector
        );
    }

    /**
     * The wrapper class should call this method when asked to marshall itself.
     */
    public JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .setChildren(
                        Lists.of(
                                context.marshall(this.label)
                                        .setName(LABEL_PROPERTY),
                                context.marshall(this.selector)
                                        .setName(SELECTOR_PROPERTY)
                        )
                );
    }

    private final static String LABEL_PROPERTY_STRING = "label";

    private final static String SELECTOR_PROPERTY_STRING = "selector";

    // @VisibleForTesting
    final static JsonPropertyName LABEL_PROPERTY = JsonPropertyName.with(LABEL_PROPERTY_STRING);

    final static JsonPropertyName SELECTOR_PROPERTY = JsonPropertyName.with(SELECTOR_PROPERTY_STRING);
}
