package walkingkooka.plugin;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginSelectorTextComponentTest implements PluginSelectorTextComponentLikeTesting<PluginSelectorTextComponent<PluginSelectorTextComponentAlternative>, PluginSelectorTextComponentAlternative>,
        ClassTesting2<PluginSelectorTextComponent<PluginSelectorTextComponentAlternative>> {

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

    @Override
    public PluginSelectorTextComponent<PluginSelectorTextComponentAlternative> createPluginSelectorTextComponentLike(final String label,
                                                                                                                     final String text,
                                                                                                                     final List<PluginSelectorTextComponentAlternative> alternatives) {
        return PluginSelectorTextComponent.with(
                label,
                text,
                alternatives
        );
    }

    @Override
    public PluginSelectorTextComponentAlternative createPluginSelectorTextComponentAlternativesLike(final String label,
                                                                                                    final String text) {
        return PluginSelectorTextComponentAlternative.with(
                label,
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

    // json..............................................................................................................

    @Override
    public PluginSelectorTextComponent<PluginSelectorTextComponentAlternative> unmarshall(final JsonNode json,
                                                                                          final JsonNodeUnmarshallContext context) {
        return PluginSelectorTextComponentLike.unmarshall(
                json,
                context,
                PluginSelectorTextComponent::with,
                PluginSelectorTextComponentAlternative.class
        );
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(PluginSelectorTextComponent.class),
                (json, context) -> PluginSelectorTextComponentLike.unmarshall(
                        json,
                        context,
                        PluginSelectorTextComponent::with,
                        PluginSelectorTextComponentAlternative.class
                ),
                PluginSelectorTextComponent::marshall,
                PluginSelectorTextComponent.class
        );
    }

    // Class............................................................................................................

    @Override
    public Class<PluginSelectorTextComponent<PluginSelectorTextComponentAlternative>> type() {
        return Cast.to(PluginSelectorTextComponent.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
