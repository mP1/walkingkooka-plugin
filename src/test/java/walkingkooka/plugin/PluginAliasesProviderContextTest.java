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

public final class PluginAliasesProviderContextTest implements ProviderContextTesting<PluginAliasesProviderContext> {

    @Override
    public void testUserNotNull() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PluginAliasesProviderContext createContext() {
        return PluginAliasesProviderContext.INSTANCE;
    }

    @Override
    public String typeNameSuffix() {
        return ProviderContext.class.getSimpleName();
    }

    // class............................................................................................................

    @Override
    public Class<PluginAliasesProviderContext> type() {
        return PluginAliasesProviderContext.class;
    }
}
