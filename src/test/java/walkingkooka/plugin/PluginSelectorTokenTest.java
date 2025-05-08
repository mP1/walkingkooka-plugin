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

public final class PluginSelectorTokenTest implements PluginSelectorTokenLikeTesting<PluginSelectorToken<PluginSelectorTokenAlternative>, PluginSelectorTokenAlternative>,
    ClassTesting2<PluginSelectorToken<PluginSelectorTokenAlternative>> {

    private final static String LABEL = "Label123";

    private final static String TEXT = "Text123";

    private final static List<PluginSelectorTokenAlternative> ALTERNATIVES = Lists.of(
        PluginSelectorTokenAlternative.with(
            "alternative-label-1",
            "alternative-text-1"
        ),
        PluginSelectorTokenAlternative.with(
            "alternative-label-2",
            "alternative-text-2"
        )
    );

    @Test
    @Override
    public void testWithNullLabelFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginSelectorToken.with(
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
            () -> PluginSelectorToken.with(
                LABEL,
                null,
                ALTERNATIVES
            )
        );
    }

    @Test
    @Override
    public void testWith() {
        final PluginSelectorToken<PluginSelectorTokenAlternative> component = PluginSelectorToken.with(
            LABEL,
            TEXT,
            ALTERNATIVES
        );
        this.checkEquals(
            LABEL,
            component.label(),
            "label"
        );
        this.textAndCheck(
            component,
            TEXT
        );
        this.checkEquals(
            ALTERNATIVES,
            component.alternatives(),
            "label"
        );
    }

    @Test
    public void testWithEmptyLabelEmptyTextAndEmptyAlternatives() {
        final String label = "";
        final String text = "";
        final List<PluginSelectorTokenAlternative> alternatives = Lists.empty();

        final PluginSelectorToken<PluginSelectorTokenAlternative> component = PluginSelectorToken.with(
            label,
            text,
            alternatives
        );
        this.checkEquals(
            label,
            component.label(),
            "label"
        );
        this.textAndCheck(
            component,
            text
        );
    }

    @Override
    public PluginSelectorToken<PluginSelectorTokenAlternative> createPluginSelectorTokenLike(final String label,
                                                                                             final String text,
                                                                                             final List<PluginSelectorTokenAlternative> alternatives) {
        return PluginSelectorToken.with(
            label,
            text,
            alternatives
        );
    }

    @Override
    public PluginSelectorTokenAlternative createPluginSelectorTokenAlternativesLike(final String label,
                                                                                    final String text) {
        return PluginSelectorTokenAlternative.with(
            label,
            text
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    @Override
    public void testEqualsDifferentLabel() {
        this.checkNotEquals(
            PluginSelectorToken.with(
                "different " + LABEL,
                TEXT,
                ALTERNATIVES
            )
        );
    }

    @Test
    @Override
    public void testEqualsDifferentText() {
        this.checkNotEquals(
            PluginSelectorToken.with(
                LABEL,
                "different " + TEXT,
                ALTERNATIVES
            )
        );
    }

    @Test
    @Override
    public void testEqualsDifferentAlternatives() {
        this.checkNotEquals(
            PluginSelectorToken.with(
                LABEL,
                TEXT,
                Lists.empty()
            )
        );
    }

    @Override
    public PluginSelectorToken<PluginSelectorTokenAlternative> createObject() {
        return PluginSelectorToken.with(
            LABEL,
            TEXT,
            ALTERNATIVES
        );
    }

    // TreePrintable.....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            PluginSelectorToken.with(
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
    public PluginSelectorToken<PluginSelectorTokenAlternative> unmarshall(final JsonNode json,
                                                                          final JsonNodeUnmarshallContext context) {
        return PluginSelectorTokenLike.unmarshall(
            json,
            context,
            PluginSelectorToken::with,
            PluginSelectorTokenAlternative.class
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(PluginSelectorToken.class),
            (json, context) -> PluginSelectorTokenLike.unmarshall(
                json,
                context,
                PluginSelectorToken::with,
                PluginSelectorTokenAlternative.class
            ),
            PluginSelectorToken::marshall,
            PluginSelectorToken.class
        );
    }

    // Class............................................................................................................

    @Override
    public Class<PluginSelectorToken<PluginSelectorTokenAlternative>> type() {
        return Cast.to(PluginSelectorToken.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
