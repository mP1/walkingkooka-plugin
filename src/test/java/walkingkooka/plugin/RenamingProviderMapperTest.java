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
 * WITHPROVIDER WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
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
import walkingkooka.plugin.RenamingProviderMapperTest.TestInfo;
import walkingkooka.plugin.RenamingProviderMapperTest.TestInfoSet;
import walkingkooka.plugin.RenamingProviderMapperTest.TestName;
import walkingkooka.plugin.RenamingProviderMapperTest.TestSelector;
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

public final class RenamingProviderMapperTest implements TreePrintableTesting,
        ClassTesting2<RenamingProviderMapper<TestName, TestSelector, TestInfo, TestInfoSet>>,
        ToStringTesting<RenamingProviderMapper<TestName, TestSelector, TestInfo, TestInfoSet>> {

    private final static TestName NAME_RENAME = new TestName("RenameName");

    private final static TestName NAME_PROVIDER = new TestName("ProviderName");

    private final static AbsoluteUrl URL_RENAME_PROVIDER = Url.parseAbsolute("https://example.com/RenamedName-RenamedProviderName");

    private final static TestInfo INFO_RENAME = new TestInfo(
            NAME_RENAME,
            URL_RENAME_PROVIDER
    );

    private final static TestInfo INFO_RENAME_PROVIDER = new TestInfo(
            NAME_PROVIDER,
            URL_RENAME_PROVIDER
    );

    private final static TestName NAME_BOTH = new TestName("NameBoth");

    private final static AbsoluteUrl URL_BOTH = Url.parseAbsolute("https://example.com/NameBoth");

    private final static TestInfo INFO_BOTH = new TestInfo(
            NAME_BOTH,
            URL_BOTH
    );

    private final static AbsoluteUrl URL_RENAME_ONLY = Url.parseAbsolute("https://example.com/RenameOnly");

    private final static TestName NAME_RENAME_ONLY = new TestName("RenameOnlyName");

    private final static TestInfo INFO_RENAME_ONLY = new TestInfo(
            NAME_RENAME_ONLY,
            URL_RENAME_ONLY
    );

    private final static TestName NAME_PROVIDER_ONLY = new TestName("ProviderOnlyName");

    private final static AbsoluteUrl URL_PROVIDER_ONLY = Url.parseAbsolute("https://example.com/ProviderOnly");

    private final static TestInfo INFO_PROVIDER_ONLY = new TestInfo(
            NAME_PROVIDER_ONLY,
            URL_PROVIDER_ONLY
    );

    // TestInfoSet......................................................................................................

    private final static TestInfoSet INFOS_RENAME = new TestInfoSet(
            Sets.of(
                    INFO_RENAME,
                    INFO_BOTH,
                    INFO_RENAME_ONLY
            )
    );

    private final static TestInfoSet INFOS_PROVIDER = new TestInfoSet(
            Sets.of(
                    INFO_RENAME_PROVIDER,
                    INFO_BOTH,
                    INFO_PROVIDER_ONLY
            )
    );

    private final static Function<TestName, RuntimeException> UNKNOWN = (n) -> new UnknownTestNameException(
            "Unknown TestName " + n
    );

    private final static RenamingProviderMapper<TestName, TestSelector, TestInfo, TestInfoSet> MAPPER = RenamingProviderMapper.with(
            INFOS_RENAME,
            INFOS_PROVIDER,
            UNKNOWN
    );

    // with.............................................................................................................

    @Test
    public void testWithNullRenamingInfosFails() {
        assertThrows(
                NullPointerException.class,
                () -> RenamingProviderMapper.with(
                        null,
                        INFOS_PROVIDER,
                        UNKNOWN
                )
        );
    }

    @Test
    public void testWithNullProviderInfosFails() {
        assertThrows(
                NullPointerException.class,
                () -> RenamingProviderMapper.with(
                        INFOS_RENAME,
                        null,
                        UNKNOWN
                )
        );
    }

    @Test
    public void testWithNullUnknownFails() {
        assertThrows(
                NullPointerException.class,
                () -> RenamingProviderMapper.with(
                        INFOS_RENAME,
                        INFOS_PROVIDER,
                        null
                )
        );
    }

    // name.............................................................................................................

    @Test
    public void testNameFilteredNameFailed() {
        assertThrows(
                UnknownTestNameException.class,
                () -> MAPPER.name(NAME_PROVIDER)
        );
    }

    @Test
    public void testNameUnknownFails() {
        assertThrows(
                UnknownTestNameException.class,
                () -> MAPPER.name(NAME_RENAME_ONLY)
        );
    }

    @Test
    public void testName() {
        this.nameAndCheck(
                NAME_BOTH,
                NAME_BOTH
        );
    }

    @Test
    public void testNameMapped() {
        this.nameAndCheck(
                NAME_RENAME,
                NAME_PROVIDER
        );
    }

    @Test
    public void testNameProviderOnly() {
        this.nameAndCheck(
                NAME_PROVIDER_ONLY,
                NAME_PROVIDER_ONLY
        );
    }

    private void nameAndCheck(final TestName name,
                              final TestName expected) {
        this.checkEquals(
                expected,
                MAPPER.name(name),
                () -> "name " + name + " " + MAPPER
        );
    }

    // selector.........................................................................................................

    @Test
    public void testSelectorFilteredSelectorFailed() {
        assertThrows(
                UnknownTestNameException.class,
                () -> MAPPER.selector(
                        new TestSelector(NAME_PROVIDER)
                )
        );
    }

    @Test
    public void testSelectorUnknownFails() {
        assertThrows(
                UnknownTestNameException.class,
                () -> MAPPER.selector(
                        new TestSelector(NAME_RENAME_ONLY)
                )
        );
    }

    @Test
    public void testSelector() {
        final TestSelector selector = new TestSelector(NAME_BOTH);

        this.selectorAndCheck(
                selector,
                selector
        );
    }

    @Test
    public void testSelectorMapped() {
        this.selectorAndCheck(
                new TestSelector(NAME_RENAME),
                new TestSelector(NAME_PROVIDER)
        );
    }

    @Test
    public void testSelectorProviderOnly() {
        this.selectorAndCheck(
                new TestSelector(NAME_PROVIDER_ONLY),
                new TestSelector(NAME_PROVIDER_ONLY)
        );
    }

    private void selectorAndCheck(final TestSelector selector,
                                  final TestSelector expected) {
        this.checkEquals(
                expected,
                MAPPER.selector(selector),
                () -> "selector " + selector + " " + MAPPER
        );
    }

    // infos............................................................................................................

    @Test
    public void testInfos() {
        this.checkEquals(
                new TestInfoSet(
                        Sets.of(
                                INFO_RENAME,
                                INFO_PROVIDER_ONLY,
                                INFO_BOTH
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
                "https://example.com/NameBoth NameBoth,https://example.com/ProviderOnly ProviderOnlyName,https://example.com/RenamedName-RenamedProviderName RenameName"
        );
    }

    // class............................................................................................................

    @Override
    public Class<RenamingProviderMapper<TestName, TestSelector, TestInfo, TestInfoSet>> type() {
        return Cast.to(RenamingProviderMapper.class);
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
