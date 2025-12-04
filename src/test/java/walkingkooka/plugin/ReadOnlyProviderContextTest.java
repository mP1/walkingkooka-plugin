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
import walkingkooka.convert.CanConvert;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.datetime.HasNow;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.plugin.store.PluginStores;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReadOnlyProviderContextTest implements ProviderContextTesting<ReadOnlyProviderContext>,
    HashCodeEqualsDefinedTesting2<ReadOnlyProviderContext> {

    private final static CanConvert CAN_CONVERT = ConverterContexts.fake();

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

    // setEnvironmentContext............................................................................................

    @Test
    public void testSetEnvironmentContext() {
        final ReadOnlyProviderContext context = this.createContext();

        final EnvironmentContext different = EnvironmentContexts.empty(
                LineEnding.CR,
                Locale.FRENCH,
                HAS_NOW,
                Optional.of(USER)
            );

        final ProviderContext set = context.setEnvironmentContext(different);

        assertNotSame(
            context,
            set
        );

        this.checkEquals(
            ReadOnlyProviderContext.with(
                BasicProviderContext.with(
                    CAN_CONVERT,
                    different,
                    PLUGIN_STORE
                )
            ),
            set
        );
    }

    // setEnvironmentValue..............................................................................................

    @Test
    public void testSetEnvironmentValueWithLocaleFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .setEnvironmentValue(
                    EnvironmentValueName.LOCALE,
                    LOCALE
                )
        );
    }

    @Test
    public void testRemoveEnvironmentWithLocaleFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .removeEnvironmentValue(EnvironmentValueName.LOCALE)
        );
    }

    @Test
    public void testSetLocaleFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .setLocale(LOCALE)
        );
    }

    @Override
    public void testSetLocaleWithDifferent() {
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
            UnsupportedOperationException.class,
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
