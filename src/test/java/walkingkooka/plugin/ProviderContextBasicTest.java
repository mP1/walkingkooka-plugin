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
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterLike;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyLocaleContexts;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.HasAuditInfoTesting;
import walkingkooka.logging.CanLog;
import walkingkooka.logging.CanLogs;
import walkingkooka.logging.LoggingLevel;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageContexts;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.StorageEnvironmentContexts;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.Storages;
import walkingkooka.text.printer.Printers;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ProviderContextBasicTest implements ProviderContextTesting<ProviderContextBasic>,
    HasAuditInfoTesting,
    HashCodeEqualsDefinedTesting2<ProviderContextBasic> {

    private final static ConverterLike CAN_CONVERT = ConverterContexts.basic(
        false, // canNumbersHaveGroupSeparator
        Converters.EXCEL_1900_DATE_SYSTEM_OFFSET, // dateOffset
        ',', // valueSeparator
        Converters.textToLocalDate(
            (x) -> DateTimeFormatter.ofPattern("yyyy MM dd")
        ), // converter
        BinaryNumberConverterFunctions.fake(), // multiplier
        BINARY_TEXT_CONTEXT,
        CurrencyLocaleContexts.fake(),
        DateTimeContexts.fake(),
        DecimalNumberContexts.fake()
    );

    private final static EnvironmentValueName<String> VAR = EnvironmentValueName.with(
        "magic",
        String.class
    );

    private final static String VAR_VALUE = "MagicValue123";

    // with.............................................................................................................

    @Test
    public void testWithNullStorageContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> ProviderContextBasic.with(
                null
            )
        );
    }

    // loadStorage......................................................................................................

    @Test
    public void testLoadStorage() {
        final Storage<StorageContext> storage = Storages.treeMapStore();

        final StoragePath path = StoragePath.parse("/value111");
        final StorageValue value = StorageValue.with(path)
            .setValue(
                Optional.of(111)
            );

        final ProviderContextBasic context = this.createContext(storage);

        storage.save(
            value,
            context
        );

        this.loadStorageAndCheck(
            context,
            path,
            value
        );
    }

    // listStorage......................................................................................................

    @Test
    public void testListStorage() {
        final Storage<StorageContext> storage = Storages.treeMapStore();

        final StoragePath path = StoragePath.parse("/value111");
        final StorageValue value = StorageValue.with(path)
            .setValue(
                Optional.of(111)
            );

        final ProviderContextBasic context = this.createContext(storage);

        storage.save(
            value,
            context
        );

        this.listStorageAndCheck(
            context,
            StoragePath.ROOT,
            0,
            100,
            StorageValueInfo.with(
                path,
                AUDIT_INFO
            )
        );
    }
    // cloneEnvironment.................................................................................................

    @Test
    public void testCloneEnvironment() {
        final StorageEnvironmentContext storageEnvironmentContext = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final ProviderContext before = ProviderContexts.basic(
            StorageContexts.basic(
                CAN_CONVERT,
                MEDIA_TYPE_DETECTOR,
                STORAGE,
                storageEnvironmentContext
            )
        );

        final ProviderContext after = before.cloneEnvironment();

        assertNotSame(
            before,
            after
        );

        this.checkEquals(
            before,
            after
        );
    }

    // setEnvironmentContext............................................................................................

    @Test
    public void testSetEnvironmentContextWithSame() {
        final StorageEnvironmentContext storageEnvironmentContext = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final ProviderContext providerContext = ProviderContexts.basic(
            StorageContexts.basic(
                CAN_CONVERT,
                MEDIA_TYPE_DETECTOR,
                STORAGE,
                storageEnvironmentContext
            )
        );

        assertSame(
            providerContext,
            providerContext.setEnvironmentContext(storageEnvironmentContext)
        );
    }

    @Test
    public void testSetEnvironmentContext() {
        final ProviderContextBasic context = this.createContext();

        final StorageEnvironmentContext differentStorageEnvironmentContext = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();
        differentStorageEnvironmentContext.setLocale(DIFFERENT_LOCALE);

        final ProviderContext differentProviderContext = ProviderContexts.basic(
            StorageContexts.basic(
                CAN_CONVERT,
                MEDIA_TYPE_DETECTOR,
                STORAGE,
                differentStorageEnvironmentContext
            )
        );

        this.checkNotEquals(
            context,
            differentProviderContext
        );

        final ProviderContext set = context.setEnvironmentContext(differentStorageEnvironmentContext);

        this.checkEquals(
            ProviderContexts.basic(
                StorageContexts.basic(
                    CAN_CONVERT,
                    MEDIA_TYPE_DETECTOR,
                    STORAGE,
                    differentStorageEnvironmentContext
                )
            ),
            set
        );
    }

    // setUser..........................................................................................................

    @Test
    public void testSetUser() {
        final StorageEnvironmentContext storageEnvironmentContext = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final ProviderContextBasic context = ProviderContextBasic.with(
            StorageContexts.basic(
                CAN_CONVERT,
                MEDIA_TYPE_DETECTOR,
                STORAGE,
                storageEnvironmentContext
            )
        );

        this.setUserAndCheck(
            context,
            DIFFERENT_USER
        );

        this.userAndCheck(
            storageEnvironmentContext,
            DIFFERENT_USER
        );
    }

    // environmentValue.................................................................................................

    @Test
    public void testEnvironmentValue() {
        final StorageEnvironmentContext storageEnvironmentContext = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();
        VAR.setEnvironmentValue(
            VAR_VALUE,
            storageEnvironmentContext
        );

        this.environmentValueAndCheck(
            ProviderContextBasic.with(
                StorageContexts.basic(
                    CAN_CONVERT,
                    MEDIA_TYPE_DETECTOR,
                    STORAGE,
                    storageEnvironmentContext
                )
            ),
            VAR,
            VAR_VALUE
        );
    }

    @Test
    public void testEnvironmentValueUnknown() {
        this.environmentValueAndCheck(
            this.createContext(),
            EnvironmentValueName.with(
                "Unknown",
                Void.class
            )
        );
    }

    @Test
    @Override
    public void testEnvironmentContext() {
        final StorageContext storageContext = StorageContexts.basic(
            CAN_CONVERT,
            MEDIA_TYPE_DETECTOR,
            STORAGE,
            STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );

        this.environmentContextAndCheck(
            ProviderContextBasic.with(
                storageContext
            ),
            storageContext
        );
    }

    // logXXX...........................................................................................................

    private final static String MESSAGE1 = "Message111";
    private final static String MESSAGE2 = "Message222";
    private final static String MESSAGE3 = "Message333";
    private final static String MESSAGE4 = "Message444";

    @Test
    public void testLogDisabled() {
        final ProviderContextBasic context = this.createContext(
            CanLogs.fake()
        );

        this.isLoggingEnabledAndCheck(
            context,
            LoggingLevel.DEBUG,
            false
        );

        context.debug(MESSAGE1);
    }

    @Test
    public void testLogEnabled() {
        final StringBuilder b = new StringBuilder();

        final ProviderContextBasic context = this.createContext(b);
        context.setLoggingLevel(LoggingLevel.DEBUG);

        this.isLoggingEnabledAndCheck(
            context,
            LoggingLevel.DEBUG,
            true
        );

        context.debug(MESSAGE1);

        this.checkEquals(
            MESSAGE1 + LINE_ENDING,
            b.toString()
        );
    }

    @Test
    public void testLoggingLevelChanged() {
        final StringBuilder b = new StringBuilder();

        final ProviderContextBasic context = this.createContext(b);

        context.setLoggingLevel(LoggingLevel.INFO);
        context.debug(MESSAGE1);
        context.info(MESSAGE2);

        context.setLoggingLevel(LoggingLevel.WARN);
        context.warn(MESSAGE3);

        context.setLoggingLevel(LoggingLevel.NONE);
        context.warn(MESSAGE4);

        this.checkEquals(
            MESSAGE2 + LINE_ENDING +
                MESSAGE3 + LINE_ENDING,
            b.toString()
        );
    }

    @Override
    public ProviderContextBasic createContext() {
        return this.createContext(STORAGE);
    }

    private ProviderContextBasic createContext(final Storage<StorageContext> storage) {
        return ProviderContextBasic.with(
            StorageContexts.basic(
                CAN_CONVERT,
                MEDIA_TYPE_DETECTOR,
                storage,
                STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
            )
        );
    }

    private ProviderContextBasic createContext(final StringBuilder b) {
        return this.createContext(
            CanLogs.printer(
                Printers.stringBuilder(
                    b,
                    LINE_ENDING
                )
            )
        );
    }

    private ProviderContextBasic createContext(final CanLog canLog) {
        return ProviderContextBasic.with(
            StorageContexts.basic(
                CAN_CONVERT,
                MEDIA_TYPE_DETECTOR,
                STORAGE,
                StorageEnvironmentContexts.basic(
                ENVIRONMENT_CONTEXT.environment()
                    .setCanLog(canLog)
                    .environmentContext()
                    .cloneEnvironment()
                )
            )
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentStorageEnvironmentContext() {
        this.checkNotEquals(
            ProviderContextBasic.with(
                StorageContexts.basic(
                    CAN_CONVERT,
                    MEDIA_TYPE_DETECTOR,
                    STORAGE,
                    DIFFERENT_STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
                )
            )
        );
    }

    @Override
    public ProviderContextBasic createObject() {
        return this.createContext();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            StorageContexts.basic(
                CAN_CONVERT,
                MEDIA_TYPE_DETECTOR,
                STORAGE,
                STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
            ).toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<ProviderContextBasic> type() {
        return ProviderContextBasic.class;
    }

    @Override
    public String typeNameSuffix() {
        return ProviderContext.class.getSimpleName();
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
