package walkingkooka.plugin;

import walkingkooka.Cast;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * A pair holding label and text both of which can be anything except for null. One example of this class utility is within a list of alternatives for a single component within a format pattern.
 */
public final class PluginSelectorTokenAlternative implements PluginSelectorTokenAlternativeLike {

    public static PluginSelectorTokenAlternative with(final String label,
                                                      final String text) {
        return new PluginSelectorTokenAlternative(
            Objects.requireNonNull(label, "label"),
            Objects.requireNonNull(text, "text")
        );
    }

    private PluginSelectorTokenAlternative(final String label, final String text) {
        this.label = label;
        this.text = text;
    }

    @Override
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
            other instanceof PluginSelectorTokenAlternative &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final PluginSelectorTokenAlternative other) {
        return this.label.equals(other.label) &&
            this.text.equals(other.text);
    }

    @Override
    public String toString() {
        return CharSequences.quoteAndEscape(this.label) + " " + CharSequences.quoteAndEscape(this.text);
    }

    // json.............................................................................................................

    static PluginSelectorTokenAlternative unmarshall(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return PluginSelectorTokenAlternativeLike.unmarshall(
            node,
            context,
            PluginSelectorTokenAlternative::with
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(PluginSelectorTokenAlternative.class),
            PluginSelectorTokenAlternative::unmarshall,
            PluginSelectorTokenAlternative::marshall,
            PluginSelectorTokenAlternative.class
        );
    }
}
