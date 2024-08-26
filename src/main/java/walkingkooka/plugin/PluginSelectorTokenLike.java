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

import walkingkooka.collect.list.Lists;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Objects;

/**
 * This interface defines properties and some marshalling helpers for a selector token.
 */
public interface PluginSelectorTokenLike<A extends PluginSelectorTokenAlternativeLike> extends HasText,
        TreePrintable {

    String label();

    List<A> alternatives();

    // TreePrintable....................................................................................................

    @Override
    default void printTree(final IndentingPrinter printer) {
        printer.println(this.label());
        printer.println(this.text());

        printer.indent();
        {
            for (final A alternative : this.alternatives()) {
                alternative.printTree(printer);
            }
        }
        printer.outdent();
    }

    // Json.............................................................................................................

    default JsonNode marshall(final JsonNodeMarshallContext context) {
        Objects.requireNonNull(context, "context");
        
        final List<JsonNode> children = Lists.array();
        
        final String label = this.label();
        if(false == label.isEmpty()) {
            children.add(
                    JsonNode.string(label)
                            .setName(PluginSelectorTokenLikeJsonConstants.LABEL_PROPERTY)
            );
        }

        final String text = this.text();
        if(false == text.isEmpty()) {
            children.add(
                    JsonNode.string(text)
                            .setName(PluginSelectorTokenLikeJsonConstants.TEXT_PROPERTY)
            );
        }

        final List<?> alternatives = this.alternatives();
        if(false == alternatives.isEmpty()) {
            children.add(
                    context.marshallCollection(
                            alternatives
                    ).setName(PluginSelectorTokenLikeJsonConstants.ALTERNATIVES_PROPERTY)
            );
        }

        return JsonNode.object()
                .setChildren(children);
    }

    /**
     * Factory that creates a {@link PluginSelectorTokenAlternativeLike} parse a {@link JsonNode}.
     */
    static <C extends PluginSelectorTokenLike<A>, A extends PluginSelectorTokenAlternativeLike> C unmarshall(final JsonNode node,
                                                                                                             final JsonNodeUnmarshallContext context,
                                                                                                             final PluginSelectorTokenLikeFactory<C, A> factory,
                                                                                                             final Class<A> alternativesType) {
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(factory, "factory");

        String label = "";
        String text = "";
        List<A> alternatives = Lists.empty();

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();

            switch (child.name().value()) {
                case PluginSelectorTokenLikeJsonConstants.LABEL_PROPERTY_STRING:
                    label = child.stringOrFail();
                    break;
                case PluginSelectorTokenLikeJsonConstants.TEXT_PROPERTY_STRING:
                    text = child.stringOrFail();
                    break;
                case PluginSelectorTokenLikeJsonConstants.ALTERNATIVES_PROPERTY_STRING:
                    alternatives = context.unmarshallList(
                            child,
                            alternativesType
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
            }
        }

        return factory.create(
                label,
                text,
                alternatives
        );
    }
}
