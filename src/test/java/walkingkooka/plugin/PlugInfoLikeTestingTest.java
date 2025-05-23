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
import walkingkooka.InvalidCharacterException;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.PlugInfoLikeTestingTest.TestPluginInfoLike;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

public final class PlugInfoLikeTestingTest implements PluginInfoLikeTesting<TestPluginInfoLike, StringName> {

    @Override
    public StringName createName(final String value) {
        return Names.string(value);
    }

    @Override
    public TestPluginInfoLike createPluginInfoLike(final AbsoluteUrl url,
                                                   final StringName name) {
        return new TestPluginInfoLike(url, name);
    }

    // parse............................................................................................................

    @Test
    public void testParseInvalidUrlFails() {
        final String text = "/host/path test-name-123";

        this.parseStringFails(
            text,
            new IllegalArgumentException("no protocol: /host/path")
        );
    }

    @Test
    public void testParseMissingNameFails() {
        final String text = "https://example.com/path";

        this.parseStringFails(
            text,
            new IllegalArgumentException("Missing name")
        );
    }

    @Test
    public void testParseInvalidNameFails() {
        final String text = "https://example.com/path #test-name/123";

        // slash within StringName will throw a InvalidCharacterException.
        this.parseStringFails(
            text,
            new InvalidCharacterException(text, text.lastIndexOf('/'))
        );
    }

    @Override
    public TestPluginInfoLike parseString(final String text) {
        return TestPluginInfoLike.parse(text);
    }

    // json.............................................................................................................

    @Override
    public TestPluginInfoLike unmarshall(final JsonNode node,
                                         final JsonNodeUnmarshallContext context) {
        return unmarshall2(node, context);
    }

    @Override
    public Class<TestPluginInfoLike> type() {
        return TestPluginInfoLike.class;
    }

    public static class TestPluginInfoLike implements PluginInfoLike<TestPluginInfoLike, StringName> {

        public static TestPluginInfoLike parse(final String text) {
            return new TestPluginInfoLike(
                PluginInfo.parse(
                    text,
                    Names::string
                )
            );
        }

        TestPluginInfoLike(final AbsoluteUrl url,
                           final StringName name) {
            this(
                PluginInfo.with(
                    url,
                    name
                )
            );
        }

        private TestPluginInfoLike(final PluginInfo<StringName> info) {
            Objects.requireNonNull(info, "info");
            this.info = info;
        }

        @Override
        public StringName name() {
            return this.info.name();
        }

        @Override
        public TestPluginInfoLike setName(final StringName name) {
            return this.name().equals(name) ?
                this :
                new TestPluginInfoLike(
                    this.info.setName(name)
                );
        }

        @Override
        public AbsoluteUrl url() {
            return this.info.url();
        }

        private final PluginInfo<StringName> info;

        // Comparable...................................................................................................

        @Override
        public int compareTo(final TestPluginInfoLike other) {
            return this.info.compareTo(other.info);
        }

        // Object.......................................................................................................

        @Override
        public int hashCode() {
            return this.info.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            return this == other ||
                other instanceof TestPluginInfoLike &&
                    this.equals0(Cast.to(other));
        }

        private boolean equals0(final TestPluginInfoLike other) {
            return this.info.equals(other.info);
        }

        @Override
        public String toString() {
            return this.info.toString();
        }

        private JsonNode marshall(final JsonNodeMarshallContext context) {
            Objects.requireNonNull(context, "context");

            return JsonNode.string(
                this.toString()
            );
        }
    }

    // Json.............................................................................................................

    static TestPluginInfoLike unmarshall2(final JsonNode node,
                                          final JsonNodeUnmarshallContext context) {
        return TestPluginInfoLike.parse(
            node.stringOrFail()
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(TestPluginInfoLike.class),
            PlugInfoLikeTestingTest::unmarshall2,
            TestPluginInfoLike::marshall,
            TestPluginInfoLike.class
        );
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }
}
