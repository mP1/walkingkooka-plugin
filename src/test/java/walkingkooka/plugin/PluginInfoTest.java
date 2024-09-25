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
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.naming.HasNameTesting;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasAbsoluteUrlTesting;
import walkingkooka.net.Url;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginInfoTest implements ClassTesting2<PluginInfo<PluginName>>,
        HasNameTesting<PluginName>,
        HasAbsoluteUrlTesting<PluginInfo<PluginName>>,
        HashCodeEqualsDefinedTesting2<PluginInfo<PluginName>>,
        ComparableTesting2<PluginInfo<PluginName>> {

    private final static AbsoluteUrl URL = Url.parseAbsolute("https://example.com");

    private final static PluginName NAME = PluginName.with("plugin-123");

    @Test
    public void testWithNullUrlFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginInfo.with(
                        null,
                        NAME
                )
        );
    }

    @Test
    public void testWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginInfo.with(
                        URL,
                        null
                )
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentUrl() {
        this.checkNotEquals(
                PluginInfo.with(
                        Url.parseAbsolute("http://example.com/different"),
                        NAME
                )
        );
    }

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(
                PluginInfo.with(
                        URL,
                        PluginName.with("different-123")
                )
        );
    }

    @Override
    public PluginInfo<PluginName> createObject() {
        return PluginInfo.with(
                URL,
                NAME
        );
    }

    // Comparable.......................................................................................................

    @Test
    public void testCompareLess() {
        this.compareToAndCheckLess(
                PluginInfo.with(
                        URL,
                        PluginName.with("xyz-456")
                )
        );
    }

    @Override
    public PluginInfo<PluginName> createComparable() {
        return this.createObject();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<PluginInfo<PluginName>> type() {
        return Cast.to(PluginInfo.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
