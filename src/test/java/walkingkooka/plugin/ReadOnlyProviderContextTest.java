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
import walkingkooka.datetime.HasNow;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.ReadOnlyEnvironmentValueException;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.plugin.store.PluginStores;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReadOnlyProviderContextTest implements ProviderContextTesting<ReadOnlyProviderContext>,
    HashCodeEqualsDefinedTesting2<ReadOnlyProviderContext> {

    private final static ConverterLike CAN_CONVERT = ConverterContexts.fake();

    private final static Currency CURRENCY = Currency.getInstance("AUD");

    private final static LineEnding LINE_ENDING = LineEnding.NL;

    private final static Locale LOCALE = Locale.forLanguageTag("en-AU");

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58,
        59
    );

    private final static HasNow HAS_NOW = () -> NOW;

    private final static EmailAddress USER = EmailAddress.parse("user@example.com");

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

        final LineEnding lineEnding = LineEnding.CRNL;
        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        this.setLineEndingAndCheck(
            context.cloneEnvironment(),
            lineEnding
        );
    }

    @Test
    public void testCloneEnvironmentAndSetLocale() {
        final ReadOnlyProviderContext context = this.createContext();

        final Locale locale = Locale.FRANCE;
        this.checkNotEquals(
            LOCALE,
            locale
        );

        final ProviderContext clone = context.cloneEnvironment();
        clone.setLocale(locale);

        this.localeAndCheck(
            clone,
            locale
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
    public void testSetEnvironmentContextAndSetCurrency() {
        final ReadOnlyProviderContext context = this.createContext();

        final Currency currency = Currency.getInstance("NZD");

        this.currencyAndCheck(
            context.setEnvironmentContext(
                EnvironmentContexts.empty(
                    currency,
                    INDENTATION,
                    LINE_ENDING,
                    Locale.FRENCH,
                    HAS_NOW,
                    Optional.of(USER)
                )
            ),
            currency
        );
    }

    @Test
    public void testSetEnvironmentContextAndSetLineEnding() {
        final ReadOnlyProviderContext context = this.createContext();

        final LineEnding lineEnding = LineEnding.CRNL;

        this.lineEndingAndCheck(
            context.setEnvironmentContext(
                EnvironmentContexts.empty(
                    CURRENCY,
                    INDENTATION,
                    lineEnding,
                    Locale.FRENCH,
                    HAS_NOW,
                    Optional.of(USER)
                )
            ),
            lineEnding
        );
    }

    @Test
    public void testSetEnvironmentContextAndSetLocale() {
        final ReadOnlyProviderContext context = this.createContext();

        final Locale locale = Locale.GERMAN;

        this.localeAndCheck(
            context.setEnvironmentContext(
                EnvironmentContexts.empty(
                    CURRENCY,
                    INDENTATION,
                    LINE_ENDING,
                    locale,
                    HAS_NOW,
                    Optional.of(USER)
                )
            ),
            locale
        );
    }

    @Test
    public void testSetEnvironmentContextAndSetUser() {
        final ReadOnlyProviderContext context = this.createContext();

        final Optional<EmailAddress> user = Optional.of(
            EmailAddress.parse("different@example.com")
        );

        this.checkNotEquals(
            Optional.of(USER),
            user
        );

        this.setUserAndCheck(
            context.setEnvironmentContext(
                EnvironmentContexts.empty(
                    CURRENCY,
                    INDENTATION,
                    LINE_ENDING,
                    LOCALE,
                    HAS_NOW,
                    Optional.of(USER)
                )
            ).cloneEnvironment(),
            user
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
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    CURRENCY,
                    INDENTATION,
                    LINE_ENDING,
                    LOCALE,
                    HAS_NOW,
                    Optional.of(USER)
                )
            ),
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
                        CURRENCY,
                        INDENTATION,
                        LINE_ENDING,
                        LOCALE,
                        HAS_NOW,
                        Optional.of(USER)
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
