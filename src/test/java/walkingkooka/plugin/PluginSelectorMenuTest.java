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
import walkingkooka.plugin.PluginSelectorMenuTest.TestPluginSelector;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;
import java.util.List;

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

    static class TestPluginSelector implements PluginSelectorLike<StringName> {

        static TestPluginSelector parse(final String selector) {
            return new TestPluginSelector(
                    PluginSelector.parse(
                            selector,
                            Names::string
                    )
            );
        }

        TestPluginSelector(final PluginSelector<StringName> pluginSelector) {
            this.pluginSelector = pluginSelector;
        }

        @Override
        public PluginSelectorLike<StringName> setName(final StringName name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PluginSelectorLike<StringName> setText(final String text) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PluginSelectorLike<StringName> setValues(final List<?> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public StringName name() {
            return this.pluginSelector.name();
        }

        @Override
        public String text() {
            return this.pluginSelector.text();
        }

        @Override
        public void printTree(final IndentingPrinter printer) {
            this.pluginSelector.printTree(printer);
        }

        private final PluginSelector<StringName> pluginSelector;

        @Override
        public int hashCode() {
            return this.pluginSelector.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof TestPluginSelector && this.equals0((TestPluginSelector)other);
        }

        private boolean equals0(final TestPluginSelector other) {
            return this.pluginSelector.equals(other.pluginSelector);
        }

        @Override
        public String toString() {
            return this.pluginSelector.toString();
        }

        JsonNode marshall(final JsonNodeMarshallContext context) {
            return this.pluginSelector.marshall(context);
        }

        static TestPluginSelector unmarshall(final JsonNode json,
                                             final JsonNodeUnmarshallContext context) {
            return parse(json.stringOrFail());
        }
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(TestPluginSelector.class),
                TestPluginSelector::unmarshall,
                TestPluginSelector::marshall,
                TestPluginSelector.class
        );
    }
}
