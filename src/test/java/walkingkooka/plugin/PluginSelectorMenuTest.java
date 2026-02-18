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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;

public final class PluginSelectorMenuTest implements PluginSelectorMenuLikeTesting<PluginSelectorMenu<TestPluginSelector, StringName>, TestPluginSelector, StringName> {

    private final static String LABEL = "Label Short123";

    private final static TestPluginSelector SELECTOR = new TestPluginSelector(
        PluginSelector.parse(
            "TestSelector",
            Names::string
        )
    );

    // with.............................................................................................................


    @Override
    public PluginSelectorMenu<TestPluginSelector, StringName> createPluginSelectorMenu(final String label,
                                                                                       final TestPluginSelector selector) {
        return PluginSelectorMenu.with(
            label,
            selector
        );
    }

    @Override
    public TestPluginSelector createPluginSelector() {
        return TestPluginSelector.parse("TestSelector");
    }

    @Override
    public TestPluginSelector createDifferentPluginSelector() {
        return TestPluginSelector.parse("TestDifferentSelector");
    }

    // json.............................................................................................................

    private final static JsonNode JSON = JsonNode.parse("{\n" +
        "  \"label\": \"Label Short123\",\n" +
        "  \"selector\": \"TestSelector\"\n" +
        "}");

    @Test
    public void testMarshall() {
        this.checkEquals(
            JSON,
            PluginSelectorMenu.with(
                LABEL,
                SELECTOR
            ).marshall(JsonNodeMarshallContexts.basic())
        );
    }

    @Test
    public void testUnmarshall() {
        this.checkEquals(
            PluginSelectorMenu.with(
                LABEL,
                SELECTOR
            ),
            PluginSelectorMenu.unmarshall(
                JSON,
                JsonNodeUnmarshallContexts.basic(
                    (String cc) -> {
                        throw new UnsupportedOperationException();
                    },
                    (String lt) -> {
                        throw new UnsupportedOperationException();
                    },
                    ExpressionNumberKind.BIG_DECIMAL,
                    MathContext.DECIMAL32
                ),
                TestPluginSelector.class
            )
        );
    }

    // class............................................................................................................

    @Override
    public Class<PluginSelectorMenu<TestPluginSelector, StringName>> type() {
        return Cast.to(
            PluginSelectorMenu.class
        );
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
