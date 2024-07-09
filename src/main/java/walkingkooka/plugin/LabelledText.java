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

import walkingkooka.Cast;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallException;

import java.util.Objects;

/**
 * A pair holding label and text both of which can be anything except for null. One example of this class utility is within a list of alternatives for a single component within a format pattern.
 */
public final class LabelledText implements HasText,
        TreePrintable {

    public static LabelledText with(final String label,
                                    final String text) {
        return new LabelledText(
                Objects.requireNonNull(label, "label"),
                Objects.requireNonNull(text, "text")
        );
    }

    private LabelledText(final String label, final String text) {
        this.label = label;
        this.text = text;
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

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.label,
                this.text
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof LabelledText &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final LabelledText other) {
        return this.label.equals(other.label) &&
                this.text.equals(other.text);
    }

    @Override
    public String toString() {
        return CharSequences.quoteAndEscape(this.label) + " " + CharSequences.quoteAndEscape(this.text);
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.label);
        printer.println(this.text);
    }

    // Json.............................................................................................................

    private final static String LABEL_PROPERTY_STRING = "label";
    private final static String TEXT_PROPERTY_STRING = "text";

    final static JsonPropertyName LABEL_PROPERTY = JsonPropertyName.with(LABEL_PROPERTY_STRING);
    final static JsonPropertyName TEXT_PROPERTY = JsonPropertyName.with(TEXT_PROPERTY_STRING);

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .set(
                        LABEL_PROPERTY,
                        JsonNode.string(this.label)
                ).set(
                        TEXT_PROPERTY,
                        JsonNode.string(this.text)
                );
    }

    /**
     * Factory that creates a {@link LabelledText} parse a {@link JsonNode}.
     */
    static LabelledText unmarshall(final JsonNode node,
                                   final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(node, "node");

        String label = null;
        String text = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();

            switch (child.name().value()) {
                case LABEL_PROPERTY_STRING:
                    label = child.stringOrFail();
                    break;
                case TEXT_PROPERTY_STRING:
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

        return with(
                label,
                text
        );
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(LabelledText.class),
                LabelledText::unmarshall,
                LabelledText::marshall,
                LabelledText.class
        );
    }
}
