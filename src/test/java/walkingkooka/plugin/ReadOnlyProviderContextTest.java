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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.store.PluginStores;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReadOnlyProviderContextTest implements ProviderContextTesting<ReadOnlyProviderContext> {

    private final static Locale LOCALE = Locale.forLanguageTag("en-AU");

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58,
        59
    );

    private final static EmailAddress USER = EmailAddress.parse("user@example.com");

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

    @Test
    public void testCloneEnvironmentAndSetLocale() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .cloneEnvironment()
                .setLocale(LOCALE)
        );
    }

    @Test
    public void testSetEnvironmentWithLocaleFails() {
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
    public ReadOnlyProviderContext createContext() {
        return ReadOnlyProviderContext.with(
            this.createWrappedContext()
        );
    }

    private ProviderContext createWrappedContext() {
        return ProviderContexts.basic(
            ConverterContexts.fake(),
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    LOCALE,
                    () -> NOW,
                    Optional.of(USER)
                )
            ),
            PluginStores.fake()
        );
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
