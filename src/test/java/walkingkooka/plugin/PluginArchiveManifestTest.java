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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.reflect.ClassName;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.jar.Manifest;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginArchiveManifestTest implements HashCodeEqualsDefinedTesting2<PluginArchiveManifest> {

    @Test
    public void testWithNullManifestFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginArchiveManifest.with(null)
        );
    }

    @Test
    public void testWithManifestMissingPluginNameFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createPluginArchiveManifest("")
        );

        this.checkEquals(
                "Manifest missing entry \"plugin-name\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testWithManifestMissingPluginProviderFactoryClassNameFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createPluginArchiveManifest("plugin-name: TestPlugin123\n")
        );

        this.checkEquals(
                "Manifest missing entry \"plugin-provider-factory-className\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testWith() {
        this.checkEquals(
                ClassName.with("example.TestPluginProvider123"),
                this.createObject().className()
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentClassName() {
        this.checkNotEquals(
                this.createPluginArchiveManifest(
                        "plugin-name: TestPlugin123\n" +
                                "plugin-provider-factory-className: example.DifferentPluginProvider\n"
                )
        );
    }

    @Override
    public PluginArchiveManifest createObject() {
        return this.createPluginArchiveManifest(
                "plugin-name: TestPlugin123\n" +
                        "plugin-provider-factory-className: example.TestPluginProvider123\n"
        );
    }

    private PluginArchiveManifest createPluginArchiveManifest(final String content) {
        try {
            final Manifest manifest = new Manifest();
            manifest.read(
                    new ByteArrayInputStream(
                            content.getBytes(StandardCharsets.UTF_8)
                    )
            );

            return PluginArchiveManifest.with(manifest);
        } catch (final IOException cause) {
            throw new RuntimeException(cause);
        }
    }
}
