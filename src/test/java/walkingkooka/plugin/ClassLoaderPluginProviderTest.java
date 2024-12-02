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
import walkingkooka.classloader.ClassLoaderResourceProviders;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.text.CharSequences;
import walkingkooka.text.LineEnding;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarInputStream;

public final class ClassLoaderPluginProviderTest implements PluginProviderTesting<PluginProvider> {

    @Test
    public void testLoadManifestAndGetPlugin() throws Exception {
        final ClassLoader classLoader = this.createClassLoader();
        final TestPluginProvider pluginProvider = createPluginProvider(
                classLoader
        );

        log("ClassLoader using JAR=" + classLoader);
        log("pluginProvider.classLoader=" + pluginProvider.getClass().getClassLoader()); // should be same as classLoader
        log("Test.classLoader=" + this.getClass().getClassLoader());

        final TestPlugin plugin = pluginProvider.plugin(
                PLUGIN_NAME,
                TestPlugin.class
        ).get();
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
            final String testPluginProviderImplClassName = "/walkingkooka/plugin/ClassLoaderPluginProviderTest$TestPluginProviderImpl.class";

            final byte[] testPluginProviderClass = this.getClass()
                    .getResourceAsStream(testPluginProviderImplClassName)
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
            final byte[] jar = this.jarFile(
                    "Manifest-Version: 1.0\r\n"+
                            "plugin-name: TestPlugin123\r\n" +
                            "plugin-provider-factory-className: walkingkooka.plugin.ClassLoaderPluginProviderTest$TestPluginProviderImpl\r\n",
                    Maps.of(
                            "resource123.txt", // ignored!
                            txtResource,
                            testPluginProviderImplClassName,
                            testPluginProviderClass,
                            testPluginImplClassName,
                            testPluginImplClass
                    )
            );

            return ClassLoaderResourceProviders.classLoader(
                    new ClassLoader() {

                        @Override
                        protected Class<?> loadClass(final String name,
                                                     final boolean resolve) throws ClassNotFoundException {
                            log("TestClassLoader.loading " + name);

                            // dont want the following 2 classes to be loaded by system
                            if (name.equals("walkingkooka.plugin.ClassLoaderPluginProviderTest$TestPluginProviderImpl")) {
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
                            ),
                            LineEnding.NL
                    )
            );
        } catch (final IOException cause) {
            throw new Error(cause);
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

    private TestPluginProvider createPluginProvider(final ClassLoader classLoader) throws IOException, ClassNotFoundException {
        return (TestPluginProvider)
                PluginProviders.classLoader(classLoader);
    }

    // this class must be loaded by the system class loader and not the custom plugin classloader.
    public interface TestPluginProvider extends PluginProvider {

        <T> Optional<T> plugin(final PluginName name,
                               final Class<T> type);

        <T> Set<T> plugins(final Class<T> type);
    }

    // ALL classes/constants below must be public to prevent IllegalAccessErrors from created ClassLoader
    public static class TestPluginProviderImpl implements TestPluginProvider {

        public <T> Optional<T> plugin(final PluginName name,
                                      final Class<T> type) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(type, "type");

            if (name.equals(PLUGIN_NAME)) {
                return Optional.of(
                        type.cast(
                                new TestPluginImpl()
                        )
                );
            }

            throw new IllegalArgumentException("Unknown plugin with name " + CharSequences.quoteAndEscape(name.value()));
        }

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

        @Override
        public PluginProviderName name() {
            return PLUGIN_PROVIDER_NAME;
        }

        @Override
        public AbsoluteUrl url() {
            return PLUGIN_PROVIDER_URL;
        }
    }

    public final static PluginProviderName PLUGIN_PROVIDER_NAME = PluginProviderName.with("TestPluginProvider123");

    public final static AbsoluteUrl PLUGIN_PROVIDER_URL = Url.parseAbsolute("https://example.com/TestPluginProvider123");

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
