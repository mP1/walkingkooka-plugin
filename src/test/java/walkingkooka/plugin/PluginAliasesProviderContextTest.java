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

import walkingkooka.storage.StorageEnvironmentContext;

public final class PluginAliasesProviderContextTest implements ProviderContextTesting<PluginAliasesProviderContext> {

    @Override
    public void testEnvironmentValueLineEndingEqualsLineEnding() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEnvironmentValueLocaleEqualsLocale() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEnvironmentValueNowEqualsNow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEnvironmentValueUserEqualsUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseEnvironmentValueNameWithNullUnknownFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseEnvironmentValueNameWithCharset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseEnvironmentValueNameWithCurrentWorkingDirectory() {
        this.parseEnvironmentValueNameAndCheck(StorageEnvironmentContext.CURRENT_WORKING_DIRECTORY);
    }

    @Override
    public void testParseEnvironmentValueNameWithCurrency() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseEnvironmentValueNameWithHomeDirectory() {
        this.parseEnvironmentValueNameAndCheck(StorageEnvironmentContext.HOME_DIRECTORY);
    }

    @Override
    public void testParseEnvironmentValueNameWithIndentation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseEnvironmentValueNameWithLineEnding() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseEnvironmentValueNameWithLocale() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseEnvironmentValueNameWithNow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseEnvironmentValueNameWithTimeOffset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseEnvironmentValueNameWithUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testRemoveEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentContextWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentContextWithEqualEnvironmentContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetCurrencyWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetIndentationWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLineEndingWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetTimeOffsetWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetUserWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

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
