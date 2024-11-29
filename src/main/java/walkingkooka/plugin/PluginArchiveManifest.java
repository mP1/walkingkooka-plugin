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
import walkingkooka.Cast;
import walkingkooka.reflect.ClassName;
import walkingkooka.text.CharSequences;

import java.io.IOException;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Abstraction around a plugin {@link Manifest} verifying it is complete, complaining about missing required entries.
 */
@GwtIncompatible
public final class PluginArchiveManifest {

    /**
     * Factory that creates a {@link PluginArchiveManifest}.
     */
    public static PluginArchiveManifest with(final Manifest manifest) throws IOException {
        Objects.requireNonNull(manifest, "manifest");

        final Attributes attributes = manifest.getMainAttributes();
        final String className = attributes.getValue(PluginProviders.PLUGIN_PROVIDER_FACTORY);
        if (null == className) {
            throw new IllegalArgumentException("Manifest missing entry " + CharSequences.quoteAndEscape(PluginProviders.PLUGIN_PROVIDER_FACTORY));
        }

        return new PluginArchiveManifest(
                ClassName.with(className)
        );
    }

    private PluginArchiveManifest(final ClassName className) {
        this.className = className;
    }

    public ClassName className() {
        return this.className;
    }

    ;
    private ClassName className;

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
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
        return this.className.equals(other.className());
    }

    @Override
    public String toString() {
        return this.className.toString();
    }
}
