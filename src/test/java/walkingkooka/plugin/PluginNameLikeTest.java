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
import walkingkooka.plugin.PluginNameLikeTest.TestPluginNameLike;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;

final class PluginNameLikeTest implements ClassTesting<TestPluginNameLike> {

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testKebabToTitleCase() {
        this.checkEquals(
                "Title 123",
                new TestPluginNameLike("title-123")
                        .kebabToTitleCase()
        );
    }

    @Test
    public void testKebabToTitleCase2() {
        this.checkEquals(
                "Title Xyz 123",
                new TestPluginNameLike("title-xyz-123")
                        .kebabToTitleCase()
        );
    }

    // class............................................................................................................

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public Class<TestPluginNameLike> type() {
        return TestPluginNameLike.class;
    }

    public static class TestPluginNameLike implements PluginNameLike<TestPluginNameLike> {

        TestPluginNameLike(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return this.value;
        }

        private final String value;

        @Override
        public int compareTo(final TestPluginNameLike other) {
            return this.value.compareTo(other.value());
        }

        @Override
        public CaseSensitivity caseSensitivity() {
            return CaseSensitivity.SENSITIVE;
        }

        @Override
        public int hashCode() {
            return this.caseSensitivity()
                    .hash(this.value);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof TestPluginNameLike && this.equals0((TestPluginNameLike) other);
        }

        private boolean equals0(final TestPluginNameLike other) {
            return this.caseSensitivity()
                    .equals(
                    this.value,
                    other.value
            );
        }
    }
}