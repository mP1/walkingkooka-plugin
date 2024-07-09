package walkingkooka.plugin;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginSelectorTextComponentTest implements HashCodeEqualsDefinedTesting2<PluginSelectorTextComponent>,
        HasTextTesting,
        ClassTesting2<PluginSelectorTextComponent>,
        JsonNodeMarshallingTesting<PluginSelectorTextComponent>,
        TreePrintableTesting {

    private final static String LABEL = "Label123";

    private final static String TEXT = "Text123";

    private final static List<PluginSelectorTextComponentAlternative> ALTERNATIVES = Lists.of(
            PluginSelectorTextComponentAlternative.with(
                    "alternative-label-1",
                    "alternative-text-1"
            ),
            PluginSelectorTextComponentAlternative.with(
                    "alternative-label-2",
                    "alternative-text-2"
            )
    );

    @Test
    public void testWithNullLabelFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginSelectorTextComponent.with(
                        null,
                        TEXT,
                        ALTERNATIVES
                )
        );
    }

    @Test
    public void testWithNullLTextFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginSelectorTextComponent.with(
                        LABEL,
                        null,
                        ALTERNATIVES
                )
        );
    }

    @Test
    public void testWith() {
        final PluginSelectorTextComponent pluginSelectorTextComponent = PluginSelectorTextComponent.with(
                LABEL,
                TEXT,
                ALTERNATIVES
        );
        this.checkEquals(
                LABEL,
                pluginSelectorTextComponent.label(),
                "label"
        );
        this.textAndCheck(
                pluginSelectorTextComponent,
                TEXT
        );
        this.checkEquals(
                ALTERNATIVES,
                pluginSelectorTextComponent.alternatives(),
                "label"
        );
    }

    @Test
    public void testWithEmptyLabelEmptyTextAndEmptyAlternatives() {
        final String label = "";
        final String text = "";
        final List<PluginSelectorTextComponentAlternative> alternatives = Lists.empty();

        final PluginSelectorTextComponent pluginSelectorTextComponent = PluginSelectorTextComponent.with(
                label,
                text,
                alternatives
        );
        this.checkEquals(
                label,
                pluginSelectorTextComponent.label(),
                "label"
        );
        this.textAndCheck(
                pluginSelectorTextComponent,
                text
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentLabel() {
        this.checkNotEquals(
                PluginSelectorTextComponent.with(
                        "different " + LABEL,
                        TEXT,
                        ALTERNATIVES
                )
        );
    }

    @Test
    public void testEqualsDifferentText() {
        this.checkNotEquals(
                PluginSelectorTextComponent.with(
                        LABEL,
                        "different " + TEXT,
                        ALTERNATIVES
                )
        );
    }

    @Test
    public void testEqualsDifferentAlternatives() {
        this.checkNotEquals(
                PluginSelectorTextComponent.with(
                        LABEL,
                        TEXT,
                        Lists.empty()
                )
        );
    }

    @Override
    public PluginSelectorTextComponent createObject() {
        return PluginSelectorTextComponent.with(
                LABEL,
                TEXT,
                ALTERNATIVES
        );
    }

    // TreePrintable.....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                PluginSelectorTextComponent.with(
                        LABEL,
                        TEXT,
                        ALTERNATIVES
                ),
                "Label123\n" +
                        "Text123\n" +
                        "  alternative-label-1\n" +
                        "  alternative-text-1\n" +
                        "  alternative-label-2\n" +
                        "  alternative-text-2\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testJsonMarshall() {
        this.marshallAndCheck(
                PluginSelectorTextComponent.with(
                        LABEL,
                        TEXT,
                        ALTERNATIVES
                ),
                "{\n" +
                        "  \"label\": \"Label123\",\n" +
                        "  \"text\": \"Text123\",\n" +
                        "  \"alternatives\": [\n" +
                        "    {\n" +
                        "      \"label\": \"alternative-label-1\",\n" +
                        "      \"text\": \"alternative-text-1\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"label\": \"alternative-label-2\",\n" +
                        "      \"text\": \"alternative-text-2\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"
        );
    }

    @Test
    public void testJsonUnmarshall() {
        this.unmarshallAndCheck(
                "{\n" +
                        "  \"label\": \"Label123\",\n" +
                        "  \"text\": \"Text123\",\n" +
                        "  \"alternatives\": [\n" +
                        "    {\n" +
                        "      \"label\": \"alternative-label-1\",\n" +
                        "      \"text\": \"alternative-text-1\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"label\": \"alternative-label-2\",\n" +
                        "      \"text\": \"alternative-text-2\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}",
                PluginSelectorTextComponent.with(
                        LABEL,
                        TEXT,
                        ALTERNATIVES
                )
        );
    }

    @Override
    public PluginSelectorTextComponent unmarshall(final JsonNode json,
                                                  final JsonNodeUnmarshallContext context) {
        return PluginSelectorTextComponent.unmarshall(
                json,
                context
        );
    }

    @Override
    public PluginSelectorTextComponent createJsonNodeMarshallingValue() {
        return PluginSelectorTextComponent.with(
                LABEL,
                TEXT,
                ALTERNATIVES
        );
    }

    // Class............................................................................................................

    @Override
    public Class<PluginSelectorTextComponent> type() {
        return PluginSelectorTextComponent.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
