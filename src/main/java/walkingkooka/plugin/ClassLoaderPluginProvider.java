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

import walkingkooka.text.CharSequences;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

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

        final Attributes attributes = manifest.getMainAttributes();
        final String className = attributes.getValue(PluginProviders.PLUGIN_PROVIDER_FACTORY);
        if(null == className) {
            throw new IllegalArgumentException("Manifest missing entry " + CharSequences.quoteAndEscape(PluginProviders.PLUGIN_PROVIDER_FACTORY));
        }

        final Class<?> pluginProviderFactory = classLoader.loadClass(className);

        try {
            return (PluginProvider)
                    pluginProviderFactory.getDeclaredConstructor()
                            .newInstance();
        } catch (final NoSuchMethodException missing) {
            throw new IllegalArgumentException("PluginProviderFactory " + CharSequences.quoteAndEscape(className) + " missing no args constructor");
        } catch (final IllegalAccessException | InvocationTargetException | InstantiationException cause) {
            throw new IllegalArgumentException("Unable to create instanceof " + CharSequences.quoteAndEscape(className), cause);
        }
    }
}
