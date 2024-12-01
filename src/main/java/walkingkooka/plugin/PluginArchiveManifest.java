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

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.reflect.ClassName;
import walkingkooka.text.CharSequences;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Abstraction around a plugin {@link Manifest} verifying it is complete, complaining about missing required entries.
 */
@GwtIncompatible
public final class PluginArchiveManifest {

    /**
     * The path within a JAR file to the MANIFEST.MF file.
     */
    public final static String MANIFEST_MF_PATH = "META-INF/MANIFEST.MF";

    public final static String PLUGIN_NAME = "plugin-name";

    public final static String PLUGIN_PROVIDER_FACTORY_CLASSNAME = "plugin-provider-factory-className";

    public static PluginArchiveManifest fromArchive(final Binary archive) {
        return PluginArchiveManifestReader.fromArchive(archive);
    }

    /**
     * Factory that creates a {@link PluginArchiveManifest}.
     */
    public static PluginArchiveManifest with(final Manifest manifest) throws IOException {
        Objects.requireNonNull(manifest, "manifest");

        final Attributes attributes = manifest.getMainAttributes();

        return new PluginArchiveManifest(
                attribute(
                        attributes,
                        PLUGIN_NAME,
                        PluginName::with
                ),
                attribute(
                        attributes,
                        PLUGIN_PROVIDER_FACTORY_CLASSNAME,
                        ClassName::with
                )
        );
    }

    private static <T> T attribute(final Attributes attributes,
                                   final String name,
                                   final Function<String, T> parser) {
        final String value = attributes.getValue(name);
        if (null == value) {
            throw new IllegalArgumentException("Manifest missing entry " + CharSequences.quoteAndEscape(name));
        }

        return parser.apply(value);
    }

    private PluginArchiveManifest(final PluginName pluginName,
                                  final ClassName className) {
        this.pluginName = pluginName;
        this.className = className;
    }

    public PluginName pluginName() {
        return this.pluginName;
    }

    private final PluginName pluginName;

    public ClassName className() {
        return this.className;
    }

    ;
    private ClassName className;

    // hashCode/equals..................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.pluginName,
                this.className
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof PluginArchiveManifest &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final PluginArchiveManifest other) {
        return this.pluginName.equals(other.pluginName) &&
                this.className.equals(other.className());
    }

    @Override
    public String toString() {
        return this.pluginName + " " + this.className;
    }
}
