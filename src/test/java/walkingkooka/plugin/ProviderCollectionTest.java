/*
 * Copyright 2020 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.HasName;
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

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ProviderCollectionTest implements ProviderTesting<ProviderCollection<StringName, TestPluginInfo, TestProvider, TestSelector, TestService>, StringName, TestPluginInfo, TestSelector, TestService>,
        ClassTesting<ProviderCollection<StringName, TestPluginInfo, TestProvider, TestSelector, TestService>>,
        ToStringTesting<ProviderCollection<StringName, TestPluginInfo, TestProvider, TestSelector, TestService>> {

    private final static TestService SERVICE1 = new TestService();
    private final static TestService SERVICE2 = new TestService();

    private final static TestService SERVICE3 = new TestService();

    private final static Function<TestSelector, StringName> INPUT_TO_NAME = TestSelector::name;

    private final static BiFunction<TestProvider, TestSelector, Optional<TestService>> PROVIDER_GETTER = (p, s) -> p.get(s);

    private final static Function<TestProvider, Set<TestPluginInfo>> INFO_GETTER = (p) -> p.infos();

    private final static String PROVIDED_LABEL = TestService.class.getSimpleName();

    private final static TestProvider PROVIDER1 = new TestProvider("service-1", SERVICE1);

    private final static TestProvider PROVIDER2 = new TestProvider("service-2", SERVICE2);

    private final static TestProvider PROVIDER3 = new TestProvider("service-3", SERVICE3);

    private final static Set<TestProvider> PROVIDERS = Sets.of(
            PROVIDER1,
            PROVIDER2,
            PROVIDER3
    );

    @Test
    public void testWithNullInputToNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> ProviderCollection.with(
                        null,
                        PROVIDER_GETTER,
                        INFO_GETTER,
                        PROVIDED_LABEL,
                        PROVIDERS
                )
        );
    }

    @Test
    public void testWithNullProviderGetterFails() {
        assertThrows(
                NullPointerException.class,
                () -> ProviderCollection.with(
                        INPUT_TO_NAME,
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
                        INPUT_TO_NAME,
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
                        INPUT_TO_NAME,
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
                        INPUT_TO_NAME,
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
                        INPUT_TO_NAME,
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
                        INPUT_TO_NAME,
                        PROVIDER_GETTER,
                        INFO_GETTER,
                        PROVIDED_LABEL,
                        Sets.empty()
                )
        );
    }

    @Test
    public void testWithDuplicatePluginsFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> ProviderCollection.with(
                        INPUT_TO_NAME,
                        PROVIDER_GETTER,
                        INFO_GETTER,
                        PROVIDED_LABEL,
                        Sets.of(
                                PROVIDER1,
                                PROVIDER2,
                                PROVIDER3,
                                new TestProvider("service-1", "http://example.com/duplicate-service1", SERVICE1)
                        )
                )
        );
        this.checkEquals(
                "Found multiple TestService for service-1(http://example.com/duplicate-service1, https://example.com/service-1)",
                thrown.getMessage()
        );
    }

    @Test
    public void testWithDuplicatePluginsFails2() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> ProviderCollection.with(
                        INPUT_TO_NAME,
                        PROVIDER_GETTER,
                        INFO_GETTER,
                        PROVIDED_LABEL,
                        Sets.of(
                                PROVIDER1,
                                PROVIDER2,
                                PROVIDER3,
                                new TestProvider("service-1", "http://example.com/duplicate-service1", SERVICE1),
                                new TestProvider("service-2", "http://example.com/duplicate-service2", SERVICE2)
                        )
                )
        );
        this.checkEquals(
                "Found multiple TestService for service-1(http://example.com/duplicate-service1, https://example.com/service-1), service-2(http://example.com/duplicate-service2, https://example.com/service-2)",
                thrown.getMessage()
        );
    }

    @Test
    public void testGet() {
        this.getAndCheck(
                new TestSelector("service-1"),
                SERVICE1
        );
    }

    @Test
    public void testGet2() {
        this.getAndCheck(
                new TestSelector("service-2"),
                SERVICE2
        );
    }

    @Test
    public void testGet3() {
        this.getAndCheck(
                new TestSelector("service-3"),
                SERVICE3
        );
    }

    @Test
    public void testGetUnknown() {
        this.getAndCheck(
                new TestSelector("unknown")
        );
    }

    @Test
    public void TestInfos() {
        this.infosAndCheck(
                new TestPluginInfo("service-1"),
                new TestPluginInfo("service-2"),
                new TestPluginInfo("service-3")
        );
    }

    @Test
    public void TestInfosMissingFromGet() {
        final ProviderCollection<StringName, TestPluginInfo, TestProvider, TestSelector, TestService> collection = ProviderCollection.with(
                INPUT_TO_NAME,
                PROVIDER_GETTER,
                INFO_GETTER,
                PROVIDED_LABEL,
                Sets.of(
                        new TestProvider("service-1", null),
                        new TestProvider("service-2", null),
                        new TestProvider("service-3", null)
                )
        );

        this.infosAndCheck(
                collection,
                new TestPluginInfo("service-1"),
                new TestPluginInfo("service-2"),
                new TestPluginInfo("service-3")
        );

        // verify the info is present but the service itself is missing(absent)
        this.getAndCheck(
                collection,
                new TestSelector("service-1")
        );

        this.getAndCheck(
                collection,
                new TestSelector("service-2")
        );

        this.getAndCheck(
                collection,
                new TestSelector("service-3")
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                ProviderCollection.with(
                        INPUT_TO_NAME,
                        PROVIDER_GETTER,
                        INFO_GETTER,
                        PROVIDED_LABEL,
                        PROVIDERS
                ),
                PROVIDER1 + ", " + PROVIDER2 + ", " + PROVIDER3
        );
    }

    @Override
    public ProviderCollection<StringName, TestPluginInfo, TestProvider, TestSelector, TestService> createProvider() {
        return ProviderCollection.with(
                INPUT_TO_NAME,
                PROVIDER_GETTER,
                INFO_GETTER,
                PROVIDED_LABEL,
                PROVIDERS
        );
    }

    static class TestService {
    }

    static class TestProvider implements Provider<StringName, TestPluginInfo, TestSelector, TestService> {

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

        @Override
        public Optional<TestService> get(final TestSelector nameAnd) {
            return Optional.ofNullable(
                    this.name.equals(nameAnd.name) ?
                            this.service :
                            null
            );
        }

        final TestService service;

        @Override
        public Set<TestPluginInfo> infos() {
            return Sets.of(
                    this.info
            );
        }

        private TestPluginInfo info;

        final StringName name;
    }

    static class TestSelector implements HasName<StringName> {

        TestSelector(final String name) {
            this.name = Names.string(name);
        }

        @Override
        public StringName name() {
            return this.name;
        }

        private StringName name;

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
            this.name = Names.string(name);
            this.url = Url.parseAbsolute(url);
        }

        @Override
        public StringName name() {
            return this.name;
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

    // ClassTesting.....................................................................................................

    @Override
    public Class<ProviderCollection<StringName, TestPluginInfo, TestProvider, TestSelector, TestService>> type() {
        return Cast.to(ProviderCollection.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
