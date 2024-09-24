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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderCollectionTest.TestPluginInfo;
import walkingkooka.plugin.ProviderCollectionTest.TestProvider;
import walkingkooka.plugin.ProviderCollectionTest.TestSelector;
import walkingkooka.plugin.ProviderCollectionTest.TestService;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.IndentingPrinter;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ProviderCollectionTest implements ClassTesting<ProviderCollection<TestProvider, StringName, TestPluginInfo, TestSelector, TestService>>,
        ToStringTesting<ProviderCollection<TestProvider, StringName, TestPluginInfo, TestSelector, TestService>> {

    private final static TestService SERVICE1 = new TestService();

    private final static TestService SERVICE2 = new TestService();

    private final static TestService SERVICE3 = new TestService();

    private final static ProviderCollectionProviderGetter<TestProvider, StringName, TestSelector, TestService> PROVIDER_GETTER = new ProviderCollectionProviderGetter<>() {
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
                               final TestSelector selector,
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
                new TestSelector(SERVICE_1_NAME),
                SERVICE1
        );
    }

    @Test
    public void testGetSelector2() {
        this.getSelectorAndCheck(
                new TestSelector(SERVICE_2_NAME),
                SERVICE2
        );
    }

    @Test
    public void testGetSelector3() {
        this.getSelectorAndCheck(
                new TestSelector(SERVICE_3_NAME),
                SERVICE3
        );
    }

    @Test
    public void testGetSelectorDuplicate() {
        final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestSelector, TestService> provider = ProviderCollection.with(
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
                new TestSelector(SERVICE_1_NAME)
        );

        this.getSelectorAndCheck(
                provider,
                new TestSelector(SERVICE_2_NAME),
                SERVICE2
        );
    }

    @Test
    public void testGetSelectorUnknownFails() {
        this.getSelectorFails(
                new TestSelector("unknown")
        );
    }

    private void getSelectorFails(final TestSelector selector) {
        this.getSelectorFails(
                this.createProvider(),
                selector
        );
    }

    private void getSelectorFails(final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestSelector, TestService> provider,
                                  final TestSelector selector) {
        assertThrows(
                IllegalArgumentException.class,
                () -> provider.get(
                                selector,
                                CONTEXT
                        )
        );
    }

    private void getSelectorAndCheck(final TestSelector selector,
                                     final TestService expected) {
        this.getSelectorAndCheck(
                this.createProvider(),
                selector,
                expected
        );
    }

    private void getSelectorAndCheck(final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestSelector, TestService> provider,
                                     final TestSelector selector,
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
        final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestSelector, TestService> provider = ProviderCollection.with(
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

    private void getNameFails(final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestSelector, TestService> provider,
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

    private void getNameAndCheck(final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestSelector, TestService> provider,
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
                new TestPluginInfo(SERVICE_1_NAME),
                new TestPluginInfo(SERVICE_2_NAME),
                new TestPluginInfo(SERVICE_3_NAME)
        );
    }

    @Test
    public void testInfosIncludesDuplicate() {
        final String url1 = "https://example.com/service-1";
        final String url2 = "https://example.com/service-2";
        final String url3 = "https://example.com/service-3";
        final String url4 = "https://example.com/service-4";

        final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestSelector, TestService> collection = ProviderCollection.with(
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
                new TestPluginInfo(SERVICE_1_NAME, url1),
                new TestPluginInfo(SERVICE_2_NAME, url2),
                new TestPluginInfo(SERVICE_3_NAME, url3),
                new TestPluginInfo(SERVICE_1_NAME, url4)
        );
    }

    @Test
    public void TestInfosMissingFromGet() {
        final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestSelector, TestService> collection = ProviderCollection.with(
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
                new TestPluginInfo(SERVICE_1_NAME),
                new TestPluginInfo(SERVICE_2_NAME),
                new TestPluginInfo(SERVICE_3_NAME)
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

    private void infosAndCheck(final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestSelector, TestService> provider,
                               final TestPluginInfo... infos) {
        this.infosAndCheck(
                provider,
                Sets.of(infos)
        );
    }

    private void infosAndCheck(final ProviderCollection<TestProvider, StringName, TestPluginInfo, TestSelector, TestService> provider,
                               final Set<TestPluginInfo> infos) {
        this.checkEquals(
                infos,
                provider.infos(),
                provider::toString
        );
    }

    private ProviderCollection<TestProvider, StringName, TestPluginInfo, TestSelector, TestService> createProvider() {
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
                    new TestPluginInfo(name) :
                    new TestPluginInfo(name, url);
            this.service = service;
        }

        public TestService get(final TestSelector selector,
                               final ProviderContext context) {
            return this.get(
                    selector.name,
                    VALUES,
                    context
            );
        }

        public TestService get(final StringName name,
                               final List<?> values,
                               final ProviderContext context) {
            if(false == this.name.equals(name) || false == VALUES.equals(values)) {
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

        private TestPluginInfo info;

        final StringName name;
    }

    static class TestSelector implements PluginSelectorLike<StringName> {

        TestSelector(final String name) {
            this.name = Names.string(name);
        }

        @Override
        public StringName name() {
            return this.name;
        }

        @Override
        public TestSelector setName(final StringName name) {
            throw new UnsupportedOperationException();
        }

        private StringName name;

        @Override
        public String text() {
            return "";
        }

        @Override
        public TestSelector setText(final String text) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TestSelector setValues(final List<?> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void printTree(final IndentingPrinter printer) {
            printer.println(this.name.value());
        }

        @Override
        public String toString() {
            return this.name.toString();
        }
    }

    static class TestPluginInfo implements PluginInfoLike<TestPluginInfo, StringName> {

        TestPluginInfo(final String name) {
            this(
                    name,
                    "https://example.com/" + name
            );
        }

        TestPluginInfo(final String name,
                       final String url) {
            this(
                    Url.parseAbsolute(url),
                    Names.string(name)
            );
        }

        TestPluginInfo(final AbsoluteUrl url,
                       final StringName name) {
            this.name = name;
            this.url = url;
        }

        @Override
        public StringName name() {
            return this.name;
        }

        @Override
        public TestPluginInfo setName(final StringName name) {
            Objects.requireNonNull(name, "name");

            return this.name.equals(name) ?
                    this :
                    new TestPluginInfo(
                            this.url,
                            name
                    );
        }

        private StringName name;

        @Override
        public AbsoluteUrl url() {
            return this.url;
        }

        private final AbsoluteUrl url;

        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            return this == other ||
                    other instanceof TestPluginInfo && this.equals0((TestPluginInfo) other);
        }

        private boolean equals0(final TestPluginInfo other) {
            return this.toString().equals(other.toString());
        }

        @Override
        public String toString() {
            return this.name + " " + this.url;
        }
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
    public Class<ProviderCollection<TestProvider, StringName, TestPluginInfo, TestSelector, TestService>> type() {
        return Cast.to(ProviderCollection.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
