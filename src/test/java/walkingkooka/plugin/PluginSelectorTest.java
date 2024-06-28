/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginSelectorTest implements ClassTesting2<PluginSelector<StringName>>,
        HashCodeEqualsDefinedTesting2<PluginSelector<StringName>>,
        HasTextTesting,
        ToStringTesting<PluginSelector<StringName>>,
        ParseStringTesting<PluginSelector<StringName>>,
        TreePrintableTesting {

    private final static StringName NAME = Names.string("magic-plugin-123");

    private final static String TEXT = "@@";

    @Test
    public void testWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginSelector.with(
                        null,
                        TEXT
                )
        );
    }

    @Test
    public void testWithNullTextFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginSelector.with(
                        NAME,
                        null
                )
        );
    }

    @Test
    public void testWith() {
        final PluginSelector<StringName> selector = PluginSelector.with(
                NAME,
                TEXT
        );

        this.checkEquals(NAME, selector.name(), "name");
        this.textAndCheck(
                selector,
                TEXT
        );
    }
    
    // parse............................................................................................................

    @Test
    public void testParseStringName() {
        final String text = "magic-plugin";
        
        this.parseStringAndCheck(
                text,
                PluginSelector.with(
                        Names.string(text),
                        ""
                )
        );
    }

    @Test
    public void testParseStringNameSpace() {
        final String text = "magic-plugin";
        
        this.parseStringAndCheck(
                text + " ",
                PluginSelector.with(
                        Names.string(text),
                        ""
                )
        );
    }

    @Test
    public void testParseStringNameSpacePatternText() {
        final String name = "magic-plugin";
        final String patternText = "@@";

        this.parseStringAndCheck(
                name + " " + patternText,
                PluginSelector.with(
                        Names.string(name),
                        patternText
                )
        );
    }

    // this.parse must be able to parse all PluginSelector<StringName>.toString.

    @Test
    public void testParseToString() {
        final PluginSelector<StringName> selector = PluginSelector.with(
                Names.string("magic-plugin-123"),
                ""
        );

        this.parseStringAndCheck(
                selector.toString(),
                selector
        );
    }

    @Test
    public void testParseToStringWithText() {
        final PluginSelector<StringName> selector = PluginSelector.with(
                Names.string("magic-plugin-123"),
                "hello"
        );

        this.parseStringAndCheck(
                selector.toString(),
                selector
        );
    }

    @Override
    public PluginSelector<StringName> parseString(final String text) {
        return PluginSelector.parse(
                text,
                Names::string
        );
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(
                PluginSelector.with(
                        Names.string("different"),
                        TEXT
                )
        );
    }

    @Test
    public void testEqualsDifferentText() {
        this.checkNotEquals(
                PluginSelector.with(
                        NAME,
                        "different"
                )
        );
    }

    @Override
    public PluginSelector<StringName> createObject() {
        return PluginSelector.with(
                NAME,
                TEXT
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                PluginSelector.with(
                        NAME,
                        ""
                ),
                "magic-plugin-123"
        );
    }

    @Test
    public void testToStringWithText() {
        this.toStringAndCheck(
                PluginSelector.with(
                        NAME,
                        TEXT
                ),
                "magic-plugin-123 @@"
        );
    }

    @Test
    public void testToStringWithQuotes() {
        this.toStringAndCheck(
                PluginSelector.with(
                        NAME,
                        "\"Hello\""
                ),
                "magic-plugin-123 \"Hello\""
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<PluginSelector<StringName>> type() {
        return Cast.to(PluginSelector.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintWithoutText() {
        this.treePrintAndCheck(
                this.parseString("abc123"),
                "abc123\n"
        );
    }

    @Test
    public void testTreePrintWithText() {
        this.treePrintAndCheck(
                this.parseString("magic-plugin-123 @@"),
                "magic-plugin-123\n" +
                        "  \"@@\"\n"
        );
    }
}
