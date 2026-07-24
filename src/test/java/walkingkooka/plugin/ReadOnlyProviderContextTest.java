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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterLike;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.ReadOnlyEnvironmentValueException;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.plugin.store.PluginStores;
import walkingkooka.text.LineEnding;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReadOnlyProviderContextTest implements ProviderContextTesting<ReadOnlyProviderContext>,
    HashCodeEqualsDefinedTesting2<ReadOnlyProviderContext> {

    private final static ConverterLike CAN_CONVERT = ConverterContexts.fake();

    private final static PluginStore PLUGIN_STORE = PluginStores.fake();

    @Test
    public void testWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> ReadOnlyProviderContext.with(null)
        );
    }

    @Test
    public void testWithSame() {
        final ReadOnlyProviderContext context = this.createContext();
        assertSame(
            context,
            ReadOnlyProviderContext.with(context)
        );
    }

    // cloneEnvironment.................................................................................................

    @Test
    public void testCloneEnvironment() {
        final ReadOnlyProviderContext context = this.createContext();
        assertNotSame(
            context,
            context.cloneEnvironment()
        );
    }

    @Test
    public void testCloneEnvironmentAndSetLineEnding() {
        final ReadOnlyProviderContext context = this.createContext();

        this.setLineEndingAndCheck(
            context.cloneEnvironment(),
            DIFFERENT_LINE_ENDING
        );
    }

    @Test
    public void testCloneEnvironmentAndSetLocale() {
        final ReadOnlyProviderContext context = this.createContext();

        final ProviderContext clone = context.cloneEnvironment();
        clone.setLocale(DIFFERENT_LOCALE);

        this.localeAndCheck(
            clone,
            DIFFERENT_LOCALE
        );
    }

    @Test
    public void testCloneEnvironmentAndSetUser() {
        final ReadOnlyProviderContext context = this.createContext();

        final EmailAddress user = EmailAddress.parse("different@example.com");

        this.checkNotEquals(
            user,
            context.user()
        );

        this.setUserAndCheck(
            context.cloneEnvironment(),
            Optional.of(user)
        );
    }

    @Test
    public void testCloneEnvironmentAndSetEnvironmentValue() {
        final ReadOnlyProviderContext context = this.createContext();

        final ProviderContext cloned = context.cloneEnvironment();
        assertNotSame(
            context,
            cloned
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "hello",
            String.class
        );
        final String value = "World123";

        this.setEnvironmentValueAndCheck(
            cloned,
            name,
            value
        );
    }

    // setEnvironmentContext............................................................................................

    @Test
    public void testSetEnvironmentContext() {
        final ReadOnlyProviderContext before = this.createContext();

        final EnvironmentContext different = EnvironmentContexts.empty(
            CHARSET,
            CURRENCY,
            INDENTATION,
            LineEnding.CR,
            Locale.FRENCH,
            HAS_NOW,
            Optional.of(USER)
        );

        final ProviderContext after = before.setEnvironmentContext(different);

        assertNotSame(
            before,
            after
        );

        this.checkEquals(
            BasicProviderContext.with(
                CAN_CONVERT,
                different,
                PLUGIN_STORE
            ),
            after
        );
    }

    @Test
    public void testSetEnvironmentContextAndSetCharset() {
        this.charsetAndCheck(
            this.createContext()
                .setEnvironmentContext(
                    ENVIRONMENT_CONTEXT.cloneEnvironment()
                ),
            CHARSET
        );
    }
    
    @Test
    public void testSetEnvironmentContextAndSetCurrency() {
        this.setCurrencyAndCheck(
            this.createContext()
                .setEnvironmentContext(
                ENVIRONMENT_CONTEXT.cloneEnvironment()
            ),
            DIFFERENT_CURRENCY
        );
    }

    @Test
    public void testSetEnvironmentContextAndSetLineEnding() {
        this.setLineEndingAndCheck(
            this.createContext()
                .setEnvironmentContext(
                    ENVIRONMENT_CONTEXT.cloneEnvironment()
            ),
            DIFFERENT_LINE_ENDING
        );
    }

    @Test
    public void testSetEnvironmentContextAndSetLocale() {
        this.setLocaleAndCheck(
            this.createContext()
                .setEnvironmentContext(
                    ENVIRONMENT_CONTEXT.cloneEnvironment()
                ),
            DIFFERENT_LOCALE
        );
    }

    @Test
    public void testSetEnvironmentContextAndSetUser() {
        this.setUserAndCheck(
            this.createContext()
                .setEnvironmentContext(
                    ENVIRONMENT_CONTEXT.cloneEnvironment()
                ).cloneEnvironment(),
            DIFFERENT_USER
        );
    }

    // setEnvironmentValue..............................................................................................

    @Test
    public void testSetEnvironmentValueWithLocaleFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .setEnvironmentValue(
                    EnvironmentValueName.LOCALE,
                    LOCALE
                )
        );
    }

    @Override
    public void testSetEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testRemoveEnvironmentWithLocaleFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .removeEnvironmentValue(EnvironmentValueName.LOCALE)
        );
    }

    @Override
    public void testRemoveEnvironmentValueWithNowFails() {
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

    @Test
    public void testSetLocaleFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .setLocale(LOCALE)
        );
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
    public ReadOnlyProviderContext createContext() {
        return ReadOnlyProviderContext.with(
            this.createWrappedContext()
        );
    }

    private ProviderContext createWrappedContext() {
        return ProviderContexts.basic(
            CAN_CONVERT,
            ENVIRONMENT_CONTEXT.cloneEnvironment(),
            PLUGIN_STORE
        );
    }

    // setUser..........................................................................................................

    @Test
    public void testSetUserFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .setUser(
                    EnvironmentContext.ANONYMOUS
                )
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentContext() {
        this.checkNotEquals(
            ReadOnlyProviderContext.with(
                ProviderContexts.basic(
                    CAN_CONVERT,
                    EnvironmentContexts.empty(
                        CHARSET,
                        CURRENCY,
                        INDENTATION,
                        LINE_ENDING,
                        LOCALE,
                        HAS_NOW,
                        OPTIONAL_USER
                    ),
                    PLUGIN_STORE
                )
            )
        );
    }
    
    @Override
    public ReadOnlyProviderContext createObject() {
        return this.createContext();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final ProviderContext wrapped = this.createWrappedContext();
        final ReadOnlyProviderContext context = ReadOnlyProviderContext.with(wrapped);

        this.toStringContainsCheck(
            context,
            "ReadOnly " + wrapped
        );
    }

    // class............................................................................................................

    @Override
    public Class<ReadOnlyProviderContext> type() {
        return ReadOnlyProviderContext.class;
    }
}
