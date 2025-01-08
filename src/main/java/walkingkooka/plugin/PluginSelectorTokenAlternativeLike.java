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

import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallException;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * An interface that should be implemented by all selector token alternatives.
 */
public interface PluginSelectorTokenAlternativeLike extends HasText,
    TreePrintable {

    String label();

    // TreePrintable....................................................................................................

    @Override
    default void printTree(final IndentingPrinter printer) {
        printer.println(this.label());
        printer.println(this.text());
    }

    // Json.............................................................................................................

    default JsonNode marshall(final JsonNodeMarshallContext context) {
        Objects.requireNonNull(context, "context");

        return JsonNode.object()
            .set(
                PluginSelectorTokenAlternativeLikeJsonConstants.LABEL_PROPERTY,
                JsonNode.string(this.label())
            ).set(
                PluginSelectorTokenAlternativeLikeJsonConstants.TEXT_PROPERTY,
                JsonNode.string(this.text())
            );
    }

    /**
     * Factory that creates a {@link PluginSelectorTokenAlternativeLike} parse a {@link JsonNode}.
     */
    static <T extends PluginSelectorTokenAlternativeLike> T unmarshall(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context,
                                                                       final BiFunction<String, String, T> factory) {
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(factory, "factory");

        String label = null;
        String text = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();

            switch (child.name().value()) {
                case PluginSelectorTokenAlternativeLikeJsonConstants.LABEL_PROPERTY_STRING:
                    label = child.stringOrFail();
                    break;
                case PluginSelectorTokenAlternativeLikeJsonConstants.TEXT_PROPERTY_STRING:
                    text = child.stringOrFail();
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
            }
        }

        if (null == label) {
            throw new JsonNodeUnmarshallException("Missing label", node);
        }
        if (null == text) {
            throw new JsonNodeUnmarshallException("Missing text", node);
        }

        return factory.apply(
            label,
            text
        );
    }
}
