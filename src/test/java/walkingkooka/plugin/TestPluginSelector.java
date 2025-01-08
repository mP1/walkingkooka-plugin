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

import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Objects;

final class TestPluginSelector implements PluginSelectorLike<StringName> {

    static TestPluginSelector parse(final String selector) {
        return new TestPluginSelector(
            PluginSelector.parse(
                selector,
                Names::string
            )
        );
    }


    TestPluginSelector(final String name) {
        this(
            Names.string(name)
        );
    }

    TestPluginSelector(final StringName name) {
        this(
            name,
            ""
        );
    }

    TestPluginSelector(final StringName name,
                       final String valueText) {
        this(
            PluginSelector.with(
                name,
                valueText
            )
        );
    }

    TestPluginSelector(final PluginSelector<StringName> pluginSelector) {
        this.pluginSelector = pluginSelector;
    }

    @Override
    public StringName name() {
        return this.pluginSelector.name();
    }

    @Override
    public TestPluginSelector setName(final StringName name) {
        Objects.requireNonNull(name, "name");

        return this.name().equals(name) ?
            this :
            new TestPluginSelector(this.pluginSelector.setName(name));
    }

    @Override
    public String valueText() {
        return this.pluginSelector.valueText();
    }

    @Override
    public TestPluginSelector setValueText(final String valueText) {
        return new TestPluginSelector(
            this.pluginSelector.setValueText(valueText)
        );
    }

    @Override
    public TestPluginSelector setValues(final List<?> values) {
        throw new UnsupportedOperationException();
    }

    private final PluginSelector<StringName> pluginSelector;

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        this.pluginSelector.printTree(printer);
    }

    // object.......................................................................................................

    @Override
    public int hashCode() {
        return this.pluginSelector.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof TestPluginSelector && this.equals0((TestPluginSelector) other);
    }

    private boolean equals0(final TestPluginSelector other) {
        return this.pluginSelector.equals(other.pluginSelector);
    }

    @Override
    public String toString() {
        return this.pluginSelector.toString();
    }

    // json.............................................................................................................

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return this.pluginSelector.marshall(context);
    }

    // @VisibleForTesting
    static TestPluginSelector unmarshall(final JsonNode node,
                                         final JsonNodeUnmarshallContext context) {
        return TestPluginSelector.parse(
            node.stringOrFail()
        );
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

