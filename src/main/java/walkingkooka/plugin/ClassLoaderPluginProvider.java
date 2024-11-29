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
import walkingkooka.text.CharSequences;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.jar.Manifest;

/**
 * A factory that creates a {@link PluginProvider} from the given {@link ClassLoader} which is assumed to contain a JAR file.
 */
@GwtIncompatible
final class ClassLoaderPluginProvider {

    /**
     * A {@link PluginProvider} that eagerly loads the manifest looking for the plugin-provider-factory and uses that class
     * name to create the instance which will be the wrapped {@link PluginProvider}.
     */
    static PluginProvider createFromManifest(final ClassLoader classLoader) throws IOException,
            ClassNotFoundException {
        Objects.requireNonNull(classLoader, "classLoader");

        final InputStream inputStream = classLoader.getResourceAsStream(PluginProviders.MANIFEST_MF_PATH);
        if (null == inputStream) {
            throw new IOException("Missing " + PluginProviders.MANIFEST_MF_PATH);
        }

        final Manifest manifest = new Manifest();
        manifest.read(inputStream);

        final PluginArchiveManifest pluginArchiveManifest = PluginArchiveManifest.with(manifest);
        final String className = pluginArchiveManifest.className()
                .value();
        final Class<?> pluginProviderFactory = classLoader.loadClass(className);

        try {
            return (PluginProvider)
                    pluginProviderFactory.getDeclaredConstructor()
                            .newInstance();
        } catch (final NoSuchMethodException missing) {
            // Manifest: plugin-provider-factory-className: "x.yZ" missing no args constructor
            throw illegalArgumentException(
                    className,
                    "Missing no argument constructor",
                    missing
            );
        } catch (final IllegalAccessException | InvocationTargetException | InstantiationException cause) {
            throw illegalArgumentException(
                    className,
                    "Instance creation failed",
                    cause
            );
        }
    }

    private static IllegalArgumentException illegalArgumentException(final String className,
                                                                     final String content,
                                                                     final Throwable cause) {
        return new IllegalArgumentException(
                "Manifest " +
                        PluginProviders.PLUGIN_PROVIDER_FACTORY +
                        ": " +
                        CharSequences.quoteAndEscape(className) +
                        " " +
                        content,
                cause
        );
    }
}
