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
import walkingkooka.Binary;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.map.Maps;
import walkingkooka.reflect.ClassName;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginArchiveManifestTest implements HashCodeEqualsDefinedTesting2<PluginArchiveManifest>,
    JarFileTesting {

    // fromArchive......................................................................................................

    @Test
    public void testFromArchiveWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginArchiveManifest.fromArchive(null)
        );
    }

    @Test
    public void testFromArchive() throws IOException {
        final String manifest = (
            "Manifest-Version: 1.0\r\n" +
                "plugin-name: test-plugin-name-111\r\n" +
                "plugin-provider-factory-className: example.TestPluginName111\r\n"
        );

        this.checkEquals(
            PluginArchiveManifest.fromManifest(
                JarFileTesting.manifest(manifest)
            ),
            PluginArchiveManifest.fromArchive(
                Binary.with(
                    JarFileTesting.jarFile(
                        manifest,
                        Maps.empty()
                    )
                )
            )
        );
    }

    // fromManifest.....................................................................................................

    @Test
    public void testFromManifestNullManifestFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginArchiveManifest.fromManifest(null)
        );
    }

    @Test
    public void testFromManifestManifestMissingPluginNameFails() {
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
    public void testFromManifestManifestMissingPluginProviderFactoryClassNameFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createPluginArchiveManifest("plugin-name: test-plugin-123\n")
        );

        this.checkEquals(
            "Manifest missing entry \"plugin-provider-factory-className\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testFromManifest() {
        this.checkEquals(
            ClassName.with("example.TestPluginProvider123"),
            this.createObject().className()
        );
    }

    // with.............................................................................................................

    @Test
    public void testWithNullPluginNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginArchiveManifest.with(
                null,
                ClassName.INT
            )
        );
    }

    @Test
    public void testWithNullClassNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> PluginArchiveManifest.with(
                PluginName.with("test-plugin-111"),
                null
            )
        );
    }

    @Test
    public void testWith() {
        final PluginName pluginName = PluginName.with("test-plugin-123");
        final ClassName className = ClassName.with(this.getClass().getCanonicalName());

        final PluginArchiveManifest manifest = PluginArchiveManifest.with(
            pluginName,
            className
        );

        this.checkEquals(
            pluginName,
            manifest.pluginName()
        );
        this.checkEquals(
            className,
            manifest.className()
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentClassName() {
        this.checkNotEquals(
            this.createPluginArchiveManifest(
                "plugin-name: test-plugin-123\n" +
                    "plugin-provider-factory-className: example.DifferentPluginProvider\n"
            )
        );
    }

    @Override
    public PluginArchiveManifest createObject() {
        return this.createPluginArchiveManifest(
            "plugin-name: test-plugin-123\n" +
                "plugin-provider-factory-className: example.TestPluginProvider123\n"
        );
    }

    private PluginArchiveManifest createPluginArchiveManifest(final String content) {
        try {
            return PluginArchiveManifest.fromManifest(
                JarFileTesting.manifest(content)
            );
        } catch (final IOException cause) {
            throw new RuntimeException(cause);
        }
    }
}
