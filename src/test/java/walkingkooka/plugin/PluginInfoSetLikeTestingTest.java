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
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.StringName;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class PluginInfoSetLikeTestingTest implements PluginInfoSetLikeTesting<StringName, TestPluginInfo, TestPluginInfoSet> {

    // parse............................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseUrlSpaceNameFails() {
        final String text = "https://example.com/test-123 test/123";
        this.parseStringInvalidCharacterFails(
                text,
                text.lastIndexOf('/')
        );
    }

    @Test
    public void testParseInfoCommaUrlSpaceMissingNameFails() {
        final String text = "https://example.com/test-111 test-111,https://example.com/test-222";
        this.parseStringFails(
                text,
                new IllegalArgumentException("Missing name")
        );
    }

    @Test
    public void testParseUrlSpaceName() {
        this.parseStringAndCheck(
                "https://example.com/test-123 test-123",
                new TestPluginInfoSet(
                        Sets.of(
                                new TestPluginInfo(
                                        "https://example.com/test-123",
                                        "test-123"
                                )
                        )
                )
        );
    }

    @Test
    public void testParseSpaceUrlSpaceName() {
        this.parseStringAndCheck(
                " https://example.com/test-123 test-123",
                new TestPluginInfoSet(
                        Sets.of(
                                new TestPluginInfo(
                                        "https://example.com/test-123",
                                        "test-123"
                                )
                        )
                )
        );
    }

    @Test
    public void testParseSpaceSpaceUrlSpaceName() {
        this.parseStringAndCheck(
                "  https://example.com/test-123 test-123",
                new TestPluginInfoSet(
                        Sets.of(
                                new TestPluginInfo(
                                        "https://example.com/test-123",
                                        "test-123"
                                )
                        )
                )
        );
    }

    @Test
    public void testParseUrlSpaceSpaceName() {
        this.parseStringAndCheck(
                "https://example.com/test-123  test-123",
                new TestPluginInfoSet(
                        Sets.of(
                                new TestPluginInfo(
                                        "https://example.com/test-123",
                                        "test-123"
                                )
                        )
                )
        );
    }

    @Test
    public void testParseSecondInfoFails() {
        final String text = "https://example.com/test-111 test-111,https://example.com/test-222 test/222";
        this.parseStringInvalidCharacterFails(
                text,
                text.lastIndexOf('/')
        );
    }

    @Test
    public void testParseMany() {
        this.parseStringAndCheck(
                "https://example.com/test-111 test-111,https://example.com/test-222 test-222",
                new TestPluginInfoSet(
                        Sets.of(
                                new TestPluginInfo(
                                        "https://example.com/test-111",
                                        "test-111"
                                ),
                                new TestPluginInfo(
                                        "https://example.com/test-222",
                                        "test-222"
                                )
                        )
                )
        );
    }

    @Test
    public void testParseMany3() {
        this.parseStringAndCheck(
                "https://example.com/test-111 test-111,https://example.com/test-222 test-222,https://example.com/test-333 test-333",
                new TestPluginInfoSet(
                        Sets.of(
                                new TestPluginInfo(
                                        "https://example.com/test-111",
                                        "test-111"
                                ),
                                new TestPluginInfo(
                                        "https://example.com/test-222",
                                        "test-222"
                                ),
                                new TestPluginInfo(
                                        "https://example.com/test-333",
                                        "test-333"
                                )
                        )
                )
        );
    }

    @Override
    public TestPluginInfoSet parseString(final String text) {
        return TestPluginInfoSet.parse(text);
    }

    // Set..............................................................................................................

    @Override
    public TestPluginInfoSet createSet() {
        return new TestPluginInfoSet(
                Sets.of(
                        new TestPluginInfo(
                                "https://example.com/test-plugin-1",
                                "test-plugin-1"
                        ),
                        new TestPluginInfo(
                                "https://example.com/test-plugin-2",
                                "test-plugin-2"
                        )
                )
        );
    }

    public TestPluginInfo info() {
        return new TestPluginInfo(
                "https://example.com/test-plugin-111",
                "test-plugin-111"
        );
    }

    @Override
    public TestPluginInfoSet unmarshall(final JsonNode node,
                                            final JsonNodeUnmarshallContext context) {
        return TestPluginInfoSet.unmarshall(
                node,
                context
        );
    }

    // class............................................................................................................

    @Override
    public Class<TestPluginInfoSet> type() {
        return TestPluginInfoSet.class;
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
             this.createSet(),
             "TestPluginInfoSet\n" +
                     "  https://example.com/test-plugin-1 test-plugin-1\n" +
                     "  https://example.com/test-plugin-2 test-plugin-2\n"
        );
    }
}
