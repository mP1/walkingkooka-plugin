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

import org.junit.jupiter.api.Test;
import walkingkooka.classloader.ClassLoaderResourceProviders;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.text.CharSequences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

public final class ClassLoaderPluginProviderTest implements PluginProviderTesting<PluginProvider> {

    @Test
    public void testLoadManifestAndGetPlugin() throws Exception {
        final ClassLoader classLoader = this.createClassLoader();
        final PluginProvider pluginProvider = createPluginProvider(
                classLoader
        );

        log("ClassLoader using JAR=" + classLoader);
        log("pluginProvider.classLoader=" + pluginProvider.getClass().getClassLoader()); // should be same as classLoader
        log("Test.classLoader=" + this.getClass().getClassLoader());

        final TestPlugin plugin = pluginProvider.plugin(
                PLUGIN_NAME,
                TestPlugin.class
        );
        this.checkEquals(
                classLoader,
                pluginProvider.getClass().getClassLoader(),
                "pluginProvider classLoader"
        );

        this.checkEquals(
                plugin.getClass().getClassLoader(),
                classLoader,
                "plugin classLoader"
        );
    }

    private ClassLoader createClassLoader() {
        try {
            final byte[] txtResource = new byte[]{
                    'X',
                    'Y',
                    'Z'
            };
            final String testPluginProviderClassName = "/walkingkooka/plugin/ClassLoaderPluginProviderTest$TestPluginProvider.class";

            final byte[] testPluginProviderClass = this.getClass()
                    .getResourceAsStream(testPluginProviderClassName)
                    .readAllBytes();

            final String testPluginImplClassName = "/walkingkooka/plugin/ClassLoaderPluginProviderTest$TestPluginImpl.class";

            final byte[] testPluginImplClass = this.getClass()
                    .getResourceAsStream(testPluginImplClassName)
                    .readAllBytes();

            // create a jar file with the following:
            //   *.txt file which is ignored.
            //   manifest which points to TestPluginProvider
            //   TestPluginProvider which will create an instance of TestPluginImpl
            //   TestPluginImpl
            final byte[] jar = createJar(
                    Maps.of(
                            "resource123.txt", // ignored!
                            txtResource,
                            testPluginProviderClassName,
                            testPluginProviderClass,
                            testPluginImplClassName,
                            testPluginImplClass,
                            PluginProviders.MANIFEST_MF_PATH,
                            ("plugin-provider-factory-className: walkingkooka.plugin.ClassLoaderPluginProviderTest$TestPluginProvider\n")
                                    .getBytes(StandardCharsets.UTF_8)
                    )
            );

            return ClassLoaderResourceProviders.classLoader(
                    new ClassLoader() {

                        @Override
                        protected Class<?> loadClass(final String name,
                                                     final boolean resolve) throws ClassNotFoundException {
                            log("TestClassLoader.loading " + name);

                            // dont want the following 2 classes to be loaded by system
                            if (name.equals("walkingkooka.plugin.ClassLoaderPluginProviderTest$TestPluginProvider")) {
                                throw new ClassNotFoundException(name);
                            }
                            if (name.equals("walkingkooka.plugin.ClassLoaderPluginProviderTest$TestPluginImpl")) {
                                throw new ClassNotFoundException(name);
                            }
                            final Class<?> loaded = super.loadClass(name, resolve);
                            log("TestClassLoader.loadedClass using " + loaded.getName() + " " + loaded.getClassLoader());
                            return loaded;
                        }

                        @Override
                        public URL getResource(final String name) {
                            return null;
                        }

                        @Override
                        public Enumeration<URL> getResources(final String name) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public InputStream getResourceAsStream(final String name) {
                            return null;
                        }
                    },
                    ClassLoaderResourceProviders.jarFileWithLibs(
                            new JarInputStream(
                                    new ByteArrayInputStream(jar)
                            )
                    )
            );
        } catch (final IOException cause) {
            throw new Error(cause);
        }
    }

    private static byte[] createJar(final Map<String, byte[]> contents) throws IOException {
        try (final ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            final JarOutputStream jarOut = new JarOutputStream(bytes);

            for (final Map.Entry<String, byte[]> mapEntry : contents.entrySet()) {
                final JarEntry jarEntry = new JarEntry(mapEntry.getKey());

                final byte[] resource = mapEntry.getValue();
                jarEntry.setSize(resource.length);
                jarOut.putNextEntry(jarEntry);
                jarOut.write(resource);
                jarOut.closeEntry();
            }

            jarOut.flush();
            jarOut.finish();
            jarOut.close();

            return bytes.toByteArray();
        }
    }

    @Override
    public PluginProvider createPluginProvider() {
        try {
            return this.createPluginProvider(
                    this.createClassLoader()
            );
        } catch (final IOException | ClassNotFoundException cause) {
            throw new Error(cause);
        }
    }

    private PluginProvider createPluginProvider(final ClassLoader classLoader) throws IOException, ClassNotFoundException {
        return PluginProviders.classLoader(classLoader);
    }

    // ALL classes/constants below must be public to prevent IllegalAccessErrors from created ClassLoader
    public static class TestPluginProvider implements PluginProvider {
        @Override
        public <T> T plugin(final PluginName name,
                            final Class<T> type) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(type, "type");

            if (name.equals(PLUGIN_NAME)) {
                return type.cast(new TestPluginImpl());
            }

            throw new IllegalArgumentException("Unknown plugin with name " + CharSequences.quoteAndEscape(name.value()));
        }

        @Override
        public <T> Set<T> plugins(final Class<T> type) {
            Objects.requireNonNull(type, "type");

            return Sets.of(
                    type.cast(
                            new TestPluginImpl()
                    )
            );
        }

        @Override
        public Set<PluginInfo> pluginInfos() {
            return Sets.of(
                    PLUGIN_INFO
            );
        }
    }

    public final static PluginName PLUGIN_NAME = PluginName.with("PluginName123");

    public final static PluginInfo PLUGIN_INFO = PluginInfo.with(
            Url.parseAbsolute("https://example.com/Plugin123"),
            PLUGIN_NAME
    );

    public interface TestPlugin {

    }

    public static class TestPluginImpl implements TestPlugin {

    }

    public static void log(final String message) {
        System.out.println(message);
    }
}