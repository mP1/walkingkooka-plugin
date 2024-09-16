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
import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.FilteredProviderMapperTest.TestInfo;
import walkingkooka.plugin.FilteredProviderMapperTest.TestInfoSet;
import walkingkooka.plugin.FilteredProviderMapperTest.TestName;
import walkingkooka.plugin.FilteredProviderMapperTest.TestSelector;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class FilteredProviderMapperTest implements TreePrintableTesting,
        ClassTesting2<FilteredProviderMapper<TestName, TestSelector, TestInfo, TestInfoSet>>,
        ToStringTesting<FilteredProviderMapper<TestName, TestSelector, TestInfo, TestInfoSet>> {

    private final static TestName NAME_IN = new TestName("NameIn1");

    private final static TestName NAME_OUT = new TestName("NameOut1");

    private final static TestName NAME = new TestName("Name");

    private final static TestName NAME_IN_ONLY = new TestName("NameInOnly");

    private final static TestName NAME_OUT_ONLY = new TestName("NameOutOnly");

    private final static AbsoluteUrl URL_IN_OUT = Url.parseAbsolute("https://example.com/NameIn1NameOut1");

    private final static AbsoluteUrl URL = Url.parseAbsolute("https://example.com/Name");

    private final static AbsoluteUrl URL_IN_ONLY = Url.parseAbsolute("https://example.com/InOnly");

    private final static AbsoluteUrl URL_OUT_ONLY = Url.parseAbsolute("https://example.com/OutOnly");

    private final static TestInfo INFO_IN = new TestInfo(
            NAME_IN,
            URL_IN_OUT
    );

    private final static TestInfo INFO_OUT = new TestInfo(
            NAME_OUT,
            URL_IN_OUT
    );

    private final static TestInfo INFO = new TestInfo(
            NAME,
            URL
    );

    private final static TestInfo INFO_IN_ONLY = new TestInfo(
            NAME_IN_ONLY,
            URL_IN_ONLY
    );

    private final static TestInfo INFO_OUT_ONLY = new TestInfo(
            NAME_OUT_ONLY,
            URL_OUT_ONLY
    );

    private final static TestInfoSet INFOS_IN = new TestInfoSet(
            Sets.of(
                    INFO_IN,
                    INFO,
                    INFO_IN_ONLY
            )
    );

    private final static TestInfoSet INFOS_OUT = new TestInfoSet(
            Sets.of(
                    INFO_OUT,
                    INFO,
                    INFO_OUT_ONLY
            )
    );

    private final static Function<TestName, RuntimeException> UNKNOWN = (n) -> new UnknownTestNameException(
            "Unknown TestName " + n
    );

    private final static FilteredProviderMapper<TestName, TestSelector, TestInfo, TestInfoSet> MAPPER = FilteredProviderMapper.with(
            INFOS_IN,
            INFOS_OUT,
            UNKNOWN
    );

    // with.............................................................................................................

    @Test
    public void testWithNullInFails() {
        assertThrows(
                NullPointerException.class,
                () -> FilteredProviderMapper.with(
                        null,
                        INFOS_OUT,
                        UNKNOWN
                )
        );
    }

    @Test
    public void testWithNullOutFails() {
        assertThrows(
                NullPointerException.class,
                () -> FilteredProviderMapper.with(
                        INFOS_IN,
                        null,
                        UNKNOWN
                )
        );
    }

    @Test
    public void testWithNullUnknownFails() {
        assertThrows(
                NullPointerException.class,
                () -> FilteredProviderMapper.with(
                        INFOS_IN,
                        INFOS_OUT,
                        null
                )
        );
    }

    // name.............................................................................................................

    @Test
    public void testNameFilteredNameFailed() {
        assertThrows(
                UnknownTestNameException.class,
                () -> MAPPER.name(NAME_OUT)
        );
    }

    @Test
    public void testNameFilteredNameFailed2() {
        assertThrows(
                UnknownTestNameException.class,
                () -> MAPPER.name(NAME_OUT_ONLY)
        );
    }

    @Test
    public void testNameUnknownFails() {
        assertThrows(
                UnknownTestNameException.class,
                () -> MAPPER.name(NAME_IN_ONLY)
        );
    }

    @Test
    public void testName() {
        this.checkEquals(
                NAME,
                MAPPER.name(NAME)
        );
    }

    @Test
    public void testNameMapped() {
        this.checkEquals(
                NAME_OUT,
                MAPPER.name(NAME_IN)
        );
    }

    // selector.............................................................................................................

    @Test
    public void testSelectorFilteredSelectorFailed() {
        assertThrows(
                UnknownTestNameException.class,
                () -> MAPPER.selector(
                        new TestSelector(NAME_OUT)
                )
        );
    }

    @Test
    public void testSelectorFilteredSelectorFailed2() {
        assertThrows(
                UnknownTestNameException.class,
                () -> MAPPER.selector(
                        new TestSelector(NAME_OUT_ONLY)
                )
        );
    }

    @Test
    public void testSelectorUnknownFails() {
        assertThrows(
                UnknownTestNameException.class,
                () -> MAPPER.selector(
                        new TestSelector(NAME_IN_ONLY)
                )
        );
    }

    @Test
    public void testSelector() {
        final TestSelector selector = new TestSelector(NAME);

        this.checkEquals(
                selector,
                MAPPER.selector(selector)
        );
    }

    @Test
    public void testSelectorMapped() {
        this.checkEquals(
                new TestSelector(NAME_OUT),
                MAPPER.selector(
                        new TestSelector(NAME_IN)
                )
        );
    }

    // infos.............................................................................................................

    @Test
    public void testInfos() {
        this.checkEquals(
                new TestInfoSet(
                        Sets.of(
                                INFO_IN,
                                INFO
                        )
                ),
                MAPPER.infos()
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                MAPPER,
                "https://example.com/Name Name,https://example.com/NameIn1NameOut1 NameIn1"
        );
    }

    // class............................................................................................................

    @Override
    public Class<FilteredProviderMapper<TestName, TestSelector, TestInfo, TestInfoSet>> type() {
        return Cast.to(FilteredProviderMapper.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // helpers..........................................................................................................

    public static class TestName implements Name,
            Comparable<TestName> {

        TestName(final String name) {
            this.name = name;
        }

        @Override
        public String value() {
            return this.name;
        }

        private final String name;

        @Override
        public int compareTo(final TestName other) {
            return this.name.compareTo(other.name);
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof TestName && this.equals0((TestName) other);
        }

        private boolean equals0(final TestName other) {
            return this.compareTo(other) == 0;
        }

        @Override
        public String toString() {
            return this.name.toString();
        }

        @Override
        public CaseSensitivity caseSensitivity() {
            return CaseSensitivity.SENSITIVE;
        }
    }

    public static class TestSelector implements PluginSelectorLike<TestName> {

        TestSelector(final TestName name) {
            this.name = name;
        }

        @Override
        public TestName name() {
            return this.name;
        }

        private final TestName name;

        @Override
        public TestSelector setName(final TestName name) {
            return new TestSelector(name);
        }

        @Override
        public String text() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PluginSelectorLike<TestName> setText(final String text) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PluginSelectorLike<TestName> setValues(List<?> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void printTree(final IndentingPrinter printer) {
            printer.println(this.name().toString());
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        public boolean equals(final Object other) {
            return this == other || other instanceof TestSelector && this.equals0((TestSelector) other);
        }

        private boolean equals0(final TestSelector other) {
            return this.name.equals(other.name);
        }

        @Override
        public String toString() {
            return this.name.toString();
        }
    }

    public static class TestInfo implements PluginInfoLike<TestInfo, TestName> {

        TestInfo(final TestName name,
                 final AbsoluteUrl url) {
            this.name = name;
            this.url = url;
        }

        @Override
        public TestName name() {
            return this.name;
        }

        private final TestName name;

        @Override
        public AbsoluteUrl url() {
            return this.url;
        }

        private final AbsoluteUrl url;

        @Override
        public int hashCode() {
            return Objects.hash(
                    this.name,
                    this.url
            );
        }

        public boolean equals(final Object other) {
            return this == other || other instanceof TestInfo && this.equals0((TestInfo) other);
        }

        private boolean equals0(final TestInfo other) {
            return this.name.equals(other.name) &&
                    this.url.equals(other.url);
        }

        @Override
        public String toString() {
            return this.url + " " + this.name;
        }
    }

    public static class TestInfoSet extends AbstractSet<TestInfo> implements PluginInfoSetLike<TestInfoSet, TestInfo, TestName> {

        TestInfoSet(final Set<TestInfo> infos) {
            this.infos = new TreeSet<>(infos);
        }

        @Override
        public Iterator<TestInfo> iterator() {
            return Iterators.readOnly(
                    this.infos.iterator()
            );
        }

        @Override
        public int size() {
            return this.infos.size();
        }

        @Override
        public TestInfoSet setElements(final Set<TestInfo> infos) {
            return new TestInfoSet(
                    new TreeSet<>(infos)
            );
        }

        @Override
        public Set<TestInfo> toSet() {
            return new TreeSet<>(this.infos);
        }

        private final Set<TestInfo> infos;
    }

    static class UnknownTestNameException extends IllegalArgumentException {
        private static final long serialVersionUID = 1;

        UnknownTestNameException(final String message) {
            super(message);
        }
    }
}
