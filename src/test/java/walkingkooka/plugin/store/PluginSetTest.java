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

package walkingkooka.plugin.store;

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.collect.set.ImmutableSortedSetTesting;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.PluginName;
import walkingkooka.plugin.PluginNameSet;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class PluginSetTest implements ImmutableSortedSetTesting<PluginSet, Plugin>,
        JsonNodeMarshallingTesting<PluginSet> {

    @Test
    public void testWithEmpty() {
        assertSame(
                PluginSet.EMPTY,
                PluginSet.with(
                        SortedSets.empty()
                )
        );
    }

    @Test
    public void testDeleteBecomesEmpty() {
        final Plugin plugin = Plugin.with(
                PluginName.with("TestPlugin111"),
                "test-plugin-111.jar",
                Binary.with("content".getBytes(Charset.defaultCharset())),
                EmailAddress.parse("user1@example.com"),
                LocalDateTime.of(
                        1999,
                        12,
                        31,
                        12,
                        58,
                        59
                )
        );

        assertSame(
                PluginSet.EMPTY,
                PluginSet.with(
                        SortedSets.of(plugin)
                ).delete(plugin)
        );
    }

    @Override
    public PluginSet createSet() {
        return PluginSet.with(
                SortedSets.of(
                        Plugin.with(
                                PluginName.with("TestPlugin111"),
                                "test-plugin-111.jar",
                                Binary.with("content".getBytes(Charset.defaultCharset())),
                                EmailAddress.parse("user1@example.com"),
                                LocalDateTime.of(
                                        1999,
                                        12,
                                        31,
                                        12,
                                        58,
                                        59
                                )
                        ),
                        Plugin.with(
                                PluginName.with("TestPlugin222"),
                                "test-plugin-222.jar",
                                Binary.with("content".getBytes(Charset.defaultCharset())),
                                EmailAddress.parse("user2@example.com"),
                                LocalDateTime.of(
                                        1999,
                                        12,
                                        31,
                                        2,
                                        22,
                                        22
                                )
                        )
                )
        );
    }

    // names............................................................................................................

    @Test
    public void testNames() {
        this.checkEquals(
                PluginNameSet.with(
                        SortedSets.of(
                                PluginName.with("TestPlugin111"),
                                PluginName.with("TestPlugin222")
                        )
                ),
                this.createSet().names()
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
                this.createJsonNodeMarshallingValue(),
                "[\n" +
                        "  {\n" +
                        "    \"name\": \"TestPlugin111\",\n" +
                        "    \"filename\": \"test-plugin-111.jar\",\n" +
                        "    \"archive\": \"Y29udGVudA==\",\n" +
                        "    \"user\": \"user1@example.com\",\n" +
                        "    \"timestamp\": \"1999-12-31T12:58:59\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"name\": \"TestPlugin222\",\n" +
                        "    \"filename\": \"test-plugin-222.jar\",\n" +
                        "    \"archive\": \"Y29udGVudA==\",\n" +
                        "    \"user\": \"user1@example.com\",\n" +
                        "    \"timestamp\": \"1999-12-31T12:58:59\"\n" +
                        "  }\n" +
                        "]"
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
                "[\n" +
                        "  {\n" +
                        "    \"name\": \"TestPlugin111\",\n" +
                        "    \"filename\": \"test-plugin-111.jar\",\n" +
                        "    \"archive\": \"Y29udGVudA==\",\n" +
                        "    \"user\": \"user1@example.com\",\n" +
                        "    \"timestamp\": \"1999-12-31T12:58:59\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"name\": \"TestPlugin222\",\n" +
                        "    \"filename\": \"test-plugin-222.jar\",\n" +
                        "    \"archive\": \"Y29udGVudA==\",\n" +
                        "    \"user\": \"user1@example.com\",\n" +
                        "    \"timestamp\": \"1999-12-31T12:58:59\"\n" +
                        "  }\n" +
                        "]",
                this.createJsonNodeMarshallingValue()
        );
    }

    @Override
    public PluginSet unmarshall(final JsonNode json, 
                                final JsonNodeUnmarshallContext context) {
        return PluginSet.unmarshall(
                json,
                context
        );
    }

    @Override
    public PluginSet createJsonNodeMarshallingValue() {
        return PluginSet.with(
                SortedSets.of(
                        Plugin.with(
                                PluginName.with("TestPlugin111"),
                                "test-plugin-111.jar",
                                Binary.with("content".getBytes(Charset.defaultCharset())),
                                EmailAddress.parse("user1@example.com"),
                                LocalDateTime.of(
                                        1999,
                                        12,
                                        31,
                                        12,
                                        58,
                                        59
                                )
                        ),
                        Plugin.with(
                                PluginName.with("TestPlugin222"),
                                "test-plugin-222.jar",
                                Binary.with("content".getBytes(Charset.defaultCharset())),
                                EmailAddress.parse("user1@example.com"),
                                LocalDateTime.of(
                                        1999,
                                        12,
                                        31,
                                        12,
                                        58,
                                        59
                                )
                        )
                )
        );
    }

    // class.............................................................................................................

    @Override
    public Class<PluginSet> type() {
        return PluginSet.class;
    }
}
