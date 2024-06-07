/*
 * Copyright 2020 Miroslav Pokorny (github.com/mP1)
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

import walkingkooka.Cast;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.PlugInfoLikeTestingTest.TestPlugInfoLike;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

public final class PlugInfoLikeTestingTest implements PluginInfoLikeTesting<TestPlugInfoLike, StringName> {

    @Override
    public StringName createName(final String value) {
        return Names.string(value);
    }

    @Override
    public TestPlugInfoLike createSpreadsheetComponentInfo(final AbsoluteUrl url,
                                                           final StringName name) {
        return new TestPlugInfoLike(url, name);
    }

    @Override
    public TestPlugInfoLike parseString(final String text) {
        return TestPlugInfoLike.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> caught) {
        return caught;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }

    @Override
    public TestPlugInfoLike unmarshall(final JsonNode node,
                                       final JsonNodeUnmarshallContext context) {
        return unmarshall2(node, context);
    }

    @Override
    public Class<TestPlugInfoLike> type() {
        return TestPlugInfoLike.class;
    }

    public static class TestPlugInfoLike implements PluginInfoLike<TestPlugInfoLike, StringName> {

        public static TestPlugInfoLike parse(final String text) {
            CharSequences.failIfNullOrEmpty(text, "text");

            final int space = text.indexOf(' ');

            return new TestPlugInfoLike(
                    Url.parseAbsolute(text.substring(0, space)),
                    Names.string(text.substring(space + 1))
            );
        }

        TestPlugInfoLike(final AbsoluteUrl url,
                         final StringName name) {
            Objects.requireNonNull(url, "url");
            Objects.requireNonNull(name, "name");
            this.url = url;
            this.name = name;
        }

        @Override
        public StringName name() {
            return this.name;
        }

        private final StringName name;

        @Override
        public AbsoluteUrl url() {
            return this.url;
        }

        private final AbsoluteUrl url;

        @Override
        public int hashCode() {
            return Objects.hash(this.url, this.name);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other ||
                    other instanceof TestPlugInfoLike &&
                            this.equals0(Cast.to(other));
        }

        private boolean equals0(final TestPlugInfoLike other) {
            return this.url.equals(other.url) &&
                    this.name.equals(other.name);
        }

        @Override
        public String toString() {
            return this.url + " " + this.name;
        }
    }

    // Json.............................................................................................................

    static TestPlugInfoLike unmarshall2(final JsonNode node,
                                       final JsonNodeUnmarshallContext context) {
        return PluginInfoLike.unmarshall(
                node,
                context,
                StringName.class,
                TestPlugInfoLike::new
        );
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(TestPlugInfoLike.class),
                PlugInfoLikeTestingTest::unmarshall2,
                TestPlugInfoLike::marshall,
                TestPlugInfoLike.class
        );
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }
}
