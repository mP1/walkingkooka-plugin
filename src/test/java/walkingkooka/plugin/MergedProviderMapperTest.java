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
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MergedProviderMapperTest implements TreePrintableTesting,
    ClassTesting2<MergedProviderMapper<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias, TestPluginAliasSet>>,
    ToStringTesting<MergedProviderMapper<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias, TestPluginAliasSet>> {

    private final static StringName NAME_RENAME = Names.string("RenameName");

    private final static StringName NAME_PROVIDER = Names.string("ProviderName");

    private final static AbsoluteUrl URL_RENAME_PROVIDER = Url.parseAbsolute("https://example.com/RenamedName-RenamedProviderName");

    private final static TestPluginInfo INFO_RENAME = new TestPluginInfo(
        URL_RENAME_PROVIDER,
        NAME_RENAME
    );

    private final static TestPluginInfo INFO_RENAME_PROVIDER = new TestPluginInfo(
        URL_RENAME_PROVIDER,
        NAME_PROVIDER
    );

    private final static StringName NAME_BOTH = Names.string("NameBoth");

    private final static AbsoluteUrl URL_BOTH = Url.parseAbsolute("https://example.com/NameBoth");

    private final static TestPluginInfo INFO_BOTH = new TestPluginInfo(
        URL_BOTH,
        NAME_BOTH
    );

    private final static AbsoluteUrl URL_RENAME_ONLY = Url.parseAbsolute("https://example.com/RenameOnly");

    private final static StringName NAME_RENAME_ONLY = Names.string("RenameOnlyName");

    private final static TestPluginInfo INFO_RENAME_ONLY = new TestPluginInfo(
        URL_RENAME_ONLY,
        NAME_RENAME_ONLY
    );

    private final static StringName NAME_PROVIDER_ONLY = Names.string("ProviderOnlyName");

    private final static AbsoluteUrl URL_PROVIDER_ONLY = Url.parseAbsolute("https://example.com/ProviderOnly");

    private final static TestPluginInfo INFO_PROVIDER_ONLY = new TestPluginInfo(
        URL_PROVIDER_ONLY,
        NAME_PROVIDER_ONLY
    );

    // TestPluginInfoSet......................................................................................................

    private final static TestPluginInfoSet INFOS_RENAME = new TestPluginInfoSet(
        Sets.of(
            INFO_RENAME,
            INFO_BOTH,
            INFO_RENAME_ONLY
        )
    );

    private final static TestPluginInfoSet INFOS_PROVIDER = new TestPluginInfoSet(
        Sets.of(
            INFO_RENAME_PROVIDER,
            INFO_BOTH,
            INFO_PROVIDER_ONLY
        )
    );

    private final static TestPluginHelper HELPER = TestPluginHelper.INSTANCE;

    private final static MergedProviderMapper<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias, TestPluginAliasSet> MAPPER = MergedProviderMapper.with(
        INFOS_RENAME,
        INFOS_PROVIDER,
        HELPER
    );

    // with.............................................................................................................

    @Test
    public void testWithNullRenamingInfosFails() {
        assertThrows(
            NullPointerException.class,
            () -> MergedProviderMapper.with(
                null,
                INFOS_PROVIDER,
                HELPER
            )
        );
    }

    @Test
    public void testWithNullProviderInfosFails() {
        assertThrows(
            NullPointerException.class,
            () -> MergedProviderMapper.with(
                INFOS_RENAME,
                null,
                HELPER
            )
        );
    }

    @Test
    public void testWithNullHelperFails() {
        assertThrows(
            NullPointerException.class,
            () -> MergedProviderMapper.with(
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
            UnknownStringNameException.class,
            () -> MAPPER.name(NAME_PROVIDER)
        );
    }

    @Test
    public void testNameUnknownFails() {
        assertThrows(
            UnknownStringNameException.class,
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

    private void nameAndCheck(final StringName name,
                              final StringName expected) {
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
            UnknownStringNameException.class,
            () -> MAPPER.selector(
                new TestPluginSelector(NAME_PROVIDER)
            )
        );
    }

    @Test
    public void testSelectorUnknownFails() {
        assertThrows(
            UnknownStringNameException.class,
            () -> MAPPER.selector(
                new TestPluginSelector(NAME_RENAME_ONLY)
            )
        );
    }

    @Test
    public void testSelector() {
        final TestPluginSelector selector = new TestPluginSelector(NAME_BOTH);

        this.selectorAndCheck(
            selector,
            selector
        );
    }

    @Test
    public void testSelectorMapped() {
        this.selectorAndCheck(
            new TestPluginSelector(NAME_RENAME),
            new TestPluginSelector(NAME_PROVIDER)
        );
    }

    @Test
    public void testSelectorProviderOnly() {
        this.selectorAndCheck(
            new TestPluginSelector(NAME_PROVIDER_ONLY),
            new TestPluginSelector(NAME_PROVIDER_ONLY)
        );
    }

    private void selectorAndCheck(final TestPluginSelector selector,
                                  final TestPluginSelector expected) {
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
            new TestPluginInfoSet(
                Sets.of(
                    INFO_RENAME_ONLY,
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
            "https://example.com/NameBoth NameBoth,https://example.com/ProviderOnly ProviderOnlyName,https://example.com/RenamedName-RenamedProviderName RenameName,https://example.com/RenameOnly RenameOnlyName"
        );
    }

    // class............................................................................................................

    @Override
    public Class<MergedProviderMapper<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias, TestPluginAliasSet>> type() {
        return Cast.to(MergedProviderMapper.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
