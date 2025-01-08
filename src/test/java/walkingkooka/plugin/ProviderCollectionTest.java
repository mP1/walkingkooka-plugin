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
import walkingkooka.Cast;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderCollectionTest.TestProvider;
import walkingkooka.plugin.ProviderCollectionTest.TestService;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ProviderCollectionTest implements ClassTesting<ProviderCollection<TestProvider, StringName, TestPluginInfo, TestPluginSelector, TestService>>,
    ToStringTesting<ProviderCollection<TestProvider, StringName, TestPluginInfo, TestPluginSelector, TestService>> {

    private final static TestService SERVICE1 = new TestService();

    private final static TestService SERVICE2 = new TestService();

    private final static TestService SERVICE3 = new TestService();

    private final static ProviderCollectionProviderGetter<TestProvider, StringName, TestPluginSelector, TestService> PROVIDER_GETTER = new ProviderCollectionProviderGetter<>() {
        @Override
        public TestService get(final TestProvider provider,
                               final StringName name,
                               final List<?> values,
                               final ProviderContext context) {
            return provider.get(
                name,
                values,
                context
            );
        }

        @Override
        public TestService get(final TestProvider provider,
                               final TestPluginSelector selector,
                               final ProviderContext context) {
            return provider.get(
                selector,
                context
            );
        }
    };

    private final static Function<TestProvider, Set<TestPluginInfo>> INFO_GETTER = (p) -> p.infos();

    private final static String PROVIDED_LABEL = TestService.class.getSimpleName();

    private static final String SERVICE_1_NAME = "service-1";

    private final static TestProvider PROVIDER1 = new TestProvider(SERVICE_1_NAME, SERVICE1);

    private static final String SERVICE_2_NAME = "service-2";

    private final static TestProvider PROVIDER2 = new TestProvider(SERVICE_2_NAME, SERVICE2);

    private static final String SERVICE_3_NAME = "service-3";

    private final static TestProvider PROVIDER3 = new TestProvider(SERVICE_3_NAME, SERVICE3);

    private final static Set<TestProvider> PROVIDERS = Sets.of(
        PROVIDER1,
        PROVIDER2,
        PROVIDER3
    );

    private final static List<?> VALUES = Lists.of("value");

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullProviderGetterFails() {
        assertThrows(
            NullPointerException.class,
            () -> ProviderCollection.with(
                null,
                INFO_GETTER,
                PROVIDED_LABEL,
                PROVIDERS
            )
        );
    }

    @Test
    public void testWithNullInfoGetterFails() {
        assertThrows(
            NullPointerException.class,
            () -> ProviderCollection.with(
                PROVIDER_GETTER,
                null,
                PROVIDED_LABEL,
                PROVIDERS
            )
        );
    }

    @Test
    public void testWithNullProvidedFails() {
        assertThrows(
            NullPointerException.class,
            () -> ProviderCollection.with(
                PROVIDER_GETTER,
                INFO_GETTER,
                null,
                PROVIDERS
            )
        );
    }

    @Test
    public void testWithEmptyProvidedLabelFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> ProviderCollection.with(
                PROVIDER_GETTER,
                INFO_GETTER,
                "",
                PROVIDERS
            )
        );
    }

    @Test
    public void testWithNullProvidersFails() {
        assertThrows(
            NullPointerException.class,
            () -> ProviderCollection.with(
                PROVIDER_GETTER,
                INFO_GETTER,
                PROVIDED_LABEL,
                null
            )
        );
    }

    @Test
    public void testWithEmptyProvidersFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> ProviderCollection.with(
                PROVIDER_GETTER,
                INFO_GETTER,
                PROVIDED_LABEL,
                Sets.empty()
            )
        );
    }

    // get(PluginSelectorLike)..........................................................................................

    @Test
    public void testGetSelector() {
        this.getSelectorAndCheck(
            new TestPluginSelector(SERVICE_1_NAME),
            SERVICE1
        );
    }

    @Test
    public void testGetSelector2() {
        this.getSelectorAndCheck(
            new TestPluginSelector(SERVICE_2_NAME),
            SERVICE2
        );
    }

    @Test
    public void testGetSelector3() {
        this.getSelectorAndCheck(
            new TestPluginSelector(SERVICE_3_NAME),
            SERVICE3
        );
    }

    @Test
    public void testGetSelectorDuplicate() {
        final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestPluginSelector, TestService> provider = ProviderCollection.with(
            PROVIDER_GETTER,
            INFO_GETTER,
            PROVIDED_LABEL,
            Sets.of(
                PROVIDER1,
                PROVIDER2,
                PROVIDER3,
                new TestProvider(SERVICE_1_NAME, "http://example.com/duplicate-service1", SERVICE1)
            )
        );

        this.getSelectorFails(
            provider,
            new TestPluginSelector(SERVICE_1_NAME)
        );

        this.getSelectorAndCheck(
            provider,
            new TestPluginSelector(SERVICE_2_NAME),
            SERVICE2
        );
    }

    @Test
    public void testGetSelectorUnknownFails() {
        this.getSelectorFails(
            new TestPluginSelector("unknown")
        );
    }

    private void getSelectorFails(final TestPluginSelector selector) {
        this.getSelectorFails(
            this.createProvider(),
            selector
        );
    }

    private void getSelectorFails(final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestPluginSelector, TestService> provider,
                                  final TestPluginSelector selector) {
        assertThrows(
            IllegalArgumentException.class,
            () -> provider.get(
                selector,
                CONTEXT
            )
        );
    }

    private void getSelectorAndCheck(final TestPluginSelector selector,
                                     final TestService expected) {
        this.getSelectorAndCheck(
            this.createProvider(),
            selector,
            expected
        );
    }

    private void getSelectorAndCheck(final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestPluginSelector, TestService> provider,
                                     final TestPluginSelector selector,
                                     final TestService expected) {
        this.checkEquals(
            expected,
            provider.get(
                selector,
                CONTEXT
            ),
            () -> provider + " selector " + selector
        );
    }

    // get(PluginNameLike)..........................................................................................

    @Test
    public void testGetName() {
        this.getNameAndCheck(
            SERVICE_1_NAME,
            VALUES,
            SERVICE1
        );
    }

    @Test
    public void testGetName2() {
        this.getNameAndCheck(
            SERVICE_2_NAME,
            VALUES,
            SERVICE2
        );
    }

    @Test
    public void testGetName3() {
        this.getNameAndCheck(
            SERVICE_3_NAME,
            VALUES,
            SERVICE3
        );
    }

    @Test
    public void testGetNameUnknownFails() {
        this.getNameFails(
            Names.string("unknown"),
            VALUES
        );
    }

    @Test
    public void testGetNameDuplicateFails() {
        final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestPluginSelector, TestService> provider = ProviderCollection.with(
            PROVIDER_GETTER,
            INFO_GETTER,
            PROVIDED_LABEL,
            Sets.of(
                PROVIDER1,
                PROVIDER2,
                PROVIDER3,
                new TestProvider(SERVICE_1_NAME, "http://example.com/duplicate-service1", SERVICE1)
            )
        );

        this.getNameFails(
            provider,
            Names.string(SERVICE_1_NAME),
            VALUES
        );

        this.getNameAndCheck(
            provider,
            Names.string(SERVICE_2_NAME),
            VALUES,
            SERVICE2
        );
    }

    private void getNameFails(final StringName name,
                              final List<?> values) {
        getNameFails(
            this.createProvider(),
            name,
            values
        );
    }

    private void getNameFails(final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestPluginSelector, TestService> provider,
                              final StringName name,
                              final List<?> values) {
        assertThrows(
            IllegalArgumentException.class,
            () -> provider.get(
                name,
                values,
                CONTEXT
            )
        );
    }

    private void getNameAndCheck(final String name,
                                 final List<?> values,
                                 final TestService expected) {
        this.getNameAndCheck(
            Names.string(name),
            values,
            expected
        );
    }

    private void getNameAndCheck(final StringName name,
                                 final List<?> values,
                                 final TestService expected) {
        this.getNameAndCheck(
            this.createProvider(),
            name,
            values,
            expected
        );
    }

    private void getNameAndCheck(final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestPluginSelector, TestService> provider,
                                 final StringName name,
                                 final List<?> values,
                                 final TestService expected) {
        this.checkEquals(
            expected,
            provider.get(
                name,
                values,
                CONTEXT
            ),
            () -> provider + " name " + name
        );
    }

    // infos............................................................................................................

    @Test
    public void TestInfos() {
        this.infosAndCheck(
            testPluginInfo(SERVICE_1_NAME),
            testPluginInfo(SERVICE_2_NAME),
            testPluginInfo(SERVICE_3_NAME)
        );
    }

    @Test
    public void testInfosIncludesDuplicate() {
        final String url1 = "https://example.com/service-1";
        final String url2 = "https://example.com/service-2";
        final String url3 = "https://example.com/service-3";
        final String url4 = "https://example.com/service-4";

        final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestPluginSelector, TestService> collection = ProviderCollection.with(
            PROVIDER_GETTER,
            INFO_GETTER,
            PROVIDED_LABEL,
            Sets.of(
                new TestProvider(SERVICE_1_NAME, url1, SERVICE1),
                new TestProvider(SERVICE_2_NAME, url2, SERVICE2),
                new TestProvider(SERVICE_3_NAME, url3, SERVICE3),
                new TestProvider(SERVICE_1_NAME, url4, SERVICE1)
            )
        );

        this.infosAndCheck(
            collection,
            testPluginInfo(SERVICE_1_NAME, url1),
            testPluginInfo(SERVICE_2_NAME, url2),
            testPluginInfo(SERVICE_3_NAME, url3),
            testPluginInfo(SERVICE_1_NAME, url4)
        );
    }

    @Test
    public void TestInfosMissingFromGet() {
        final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestPluginSelector, TestService> collection = ProviderCollection.with(
            PROVIDER_GETTER,
            INFO_GETTER,
            PROVIDED_LABEL,
            Sets.of(
                new TestProvider(SERVICE_1_NAME, null),
                new TestProvider(SERVICE_2_NAME, null),
                new TestProvider(SERVICE_3_NAME, null)
            )
        );

        this.infosAndCheck(
            collection,
            testPluginInfo(SERVICE_1_NAME),
            testPluginInfo(SERVICE_2_NAME),
            testPluginInfo(SERVICE_3_NAME)
        );
    }

    @Test
    public void testInfoReadOnly() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createProvider().infos()
                .clear()
        );
    }

    private void infosAndCheck(final TestPluginInfo... infos) {
        this.infosAndCheck(
            this.createProvider(),
            infos
        );
    }

    private void infosAndCheck(final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestPluginSelector, TestService> provider,
                               final TestPluginInfo... infos) {
        this.infosAndCheck(
            provider,
            Sets.of(infos)
        );
    }

    private void infosAndCheck(final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestPluginSelector, TestService> provider,
                               final Set<TestPluginInfo> infos) {
        this.checkEquals(
            infos,
            provider.infos(),
            provider::toString
        );
    }

    private ProviderCollection<TestProvider, StringName, TestPluginInfo, TestPluginSelector, TestService> createProvider() {
        return ProviderCollection.with(
            PROVIDER_GETTER,
            INFO_GETTER,
            PROVIDED_LABEL,
            PROVIDERS
        );
    }

    static class TestService {
    }

    static class TestProvider implements Provider {

        TestProvider(final String name,
                     final TestService service) {
            this(
                name,
                null,
                service
            );
        }

        TestProvider(final String name,
                     final String url,
                     final TestService service) {
            this.name = Names.string(name);
            this.info = null == url ?
                testPluginInfo(name) :
                testPluginInfo(name, url);
            this.service = service;
        }

        public TestService get(final TestPluginSelector selector,
                               final ProviderContext context) {
            return this.get(
                selector.name(),
                VALUES,
                context
            );
        }

        public TestService get(final StringName name,
                               final List<?> values,
                               final ProviderContext context) {
            if (false == this.name.equals(name) || false == VALUES.equals(values)) {
                throw new IllegalArgumentException("Unknown " + name + " or " + values);
            }
            return this.service;
        }

        final TestService service;

        public Set<TestPluginInfo> infos() {
            return Sets.of(
                this.info
            );
        }

        private final TestPluginInfo info;

        final StringName name;
    }

    private static TestPluginInfo testPluginInfo(final String name) {
        return testPluginInfo(
            name,
            "https://example.com/" + name
        );
    }

    private static TestPluginInfo testPluginInfo(final String name,
                                                 final String url) {
        return new TestPluginInfo(
            Url.parseAbsolute(url),
            Names.string(name)
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            ProviderCollection.with(
                PROVIDER_GETTER,
                INFO_GETTER,
                PROVIDED_LABEL,
                PROVIDERS
            ),
            PROVIDER1 + ", " + PROVIDER2 + ", " + PROVIDER3
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<ProviderCollection<TestProvider, StringName, TestPluginInfo, TestPluginSelector, TestService>> type() {
        return Cast.to(ProviderCollection.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
