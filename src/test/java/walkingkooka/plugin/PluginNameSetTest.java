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
import walkingkooka.collect.set.ImmutableSortedSetTesting;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginNameSetTest implements ImmutableSortedSetTesting<PluginNameSet, PluginName>,
    HasTextTesting,
    ParseStringTesting<PluginNameSet>,
    TreePrintableTesting,
    JsonNodeMarshallingTesting<PluginNameSet>,
    HasUrlFragmentTesting {

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginNameSet.with(null)
        );
    }

    @Test
    public void testDeleteBecomesEmpty() {
        final PluginName name = PluginName.with("Plugin111");

        assertSame(
            PluginNameSet.EMPTY,
            PluginNameSet.with(
                SortedSets.of(name)
            ).delete(name)
        );
    }

    @Override
    public PluginNameSet createSet() {
        return PluginNameSet.with(
            SortedSets.of(
                PluginName.with("Plugin111"),
                PluginName.with("Plugin222")
            )
        );
    }

    // parseString......................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails(
            "plugin!1, plugin2",
            '!'
        );
    }

    @Test
    public void testParseInvalidCharacterSecondPluginNameFails() {
        this.parseStringInvalidCharacterFails(
            "plugin1, plugin2!",
            '!'
        );
    }

    @Test
    public void testParseEmpty() {
        assertSame(
            PluginNameSet.EMPTY,
            this.parseStringAndCheck(
                "",
                PluginNameSet.EMPTY
            )
        );
    }

    @Test
    public void testParseSpaces() {
        assertSame(
            PluginNameSet.EMPTY,
            this.parseStringAndCheck(
                "   ",
                PluginNameSet.EMPTY
            )
        );
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck(
            "Plugin111,Plugin222",
            this.createSet()
        );
    }

    @Test
    public void testParseWithExtraSpaces() {
        this.parseStringAndCheck(
            " Plugin111 , Plugin222 ",
            this.createSet()
        );
    }

    @Override
    public PluginNameSet parseString(final String text) {
        return PluginNameSet.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException type) {
        return type;
    }

    // HasText..........................................................................................................

    @Test
    public void testText() {
        this.textAndCheck(
            this.createSet(),
            "Plugin111,Plugin222"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createSet(),
            "Plugin111\n" +
                "Plugin222\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            "\"Plugin111,Plugin222\""
        );
    }

    @Override
    public PluginNameSet unmarshall(final JsonNode jsonNode,
                                    final JsonNodeUnmarshallContext context) {
        return PluginNameSet.unmarshall(
            jsonNode,
            context
        );
    }

    @Override
    public PluginNameSet createJsonNodeMarshallingValue() {
        return this.createSet();
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testUrlFragment() {
        this.urlFragmentAndCheck(
            this.createSet(),
            "Plugin111,Plugin222"
        );
    }

    // class............................................................................................................

    @Override
    public Class<PluginNameSet> type() {
        return PluginNameSet.class;
    }
}
