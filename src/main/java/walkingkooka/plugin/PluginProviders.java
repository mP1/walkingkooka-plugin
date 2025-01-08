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
import walkingkooka.reflect.PublicStaticHelper;

import java.io.IOException;

public final class PluginProviders implements PublicStaticHelper {

    /**
     * A {@link PluginProvider} that eagerly loads the manifest looking for the plugin-provider-factory and uses that class
     * name to create the instance which will be the wrapped {@link PluginProvider}.
     */
    @GwtIncompatible
    public static PluginProvider classLoader(final ClassLoader classLoader) throws IOException,
        ClassNotFoundException {
        return ClassLoaderPluginProvider.createFromManifest(classLoader);
    }

    /**
     * {@see FakePluginProvider}
     */
    public static PluginProvider fake() {
        return new FakePluginProvider();
    }

    /**
     * Stop creation
     */
    private PluginProviders() {
        throw new UnsupportedOperationException();
    }
}
