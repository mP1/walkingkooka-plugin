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
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class FilteredProviderMapperTest implements TreePrintableTesting,
        ClassTesting2<FilteredProviderMapper<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias>>,
        ToStringTesting<FilteredProviderMapper<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias>> {

    private final static StringName RENAMED_RENAME_NAME = Names.string("RenamedRenameName1");

    private final static StringName RENAMED_PROVIDER_NAME = Names.string("RenamedProviderName1");

    private final static AbsoluteUrl RENAMED_URL = Url.parseAbsolute("https://example.com/" + RENAMED_RENAME_NAME + "/" + RENAMED_PROVIDER_NAME);

    private final static TestPluginInfo RENAMED_FILTERED_INFO = new TestPluginInfo(
            RENAMED_URL,
            RENAMED_RENAME_NAME
    );

    private final static TestPluginInfo RENAMED_PROVIDER_INFO = new TestPluginInfo(
            RENAMED_URL,
            RENAMED_PROVIDER_NAME
    );

    private final static StringName BOTH_NAME = Names.string("Name");

    private final static AbsoluteUrl BOTH_URL = Url.parseAbsolute("https://example.com/" + BOTH_NAME);

    private final static TestPluginInfo BOTH_INFO = new TestPluginInfo(
            BOTH_URL,
            BOTH_NAME
    );

    private final static StringName FILTERED_ONLY_NAME = Names.string("FilteredOnlyName");

    private final static AbsoluteUrl FILTERED_ONLY_URL = Url.parseAbsolute("https://example.com/" + FILTERED_ONLY_NAME);

    private final static TestPluginInfo FILTERED_ONLY_INFO = new TestPluginInfo(
            FILTERED_ONLY_URL,
            FILTERED_ONLY_NAME
    );

    private final static StringName PROVIDER_ONLY_NAME = Names.string("ProviderOnlyName");

    private final static AbsoluteUrl PROVIDER_ONLY_URL = Url.parseAbsolute("https://example.com/" + PROVIDER_ONLY_NAME);

    private final static TestPluginInfo PROVIDER_ONLY_INFO = new TestPluginInfo(
            PROVIDER_ONLY_URL,
            PROVIDER_ONLY_NAME
    );

    private final static TestPluginInfoSet FILTERED_INFOS = new TestPluginInfoSet(
            Sets.of(
                    RENAMED_FILTERED_INFO,
                    BOTH_INFO,
                    FILTERED_ONLY_INFO
            )
    );

    private final static TestPluginInfoSet PROVIDER_INFOS = new TestPluginInfoSet(
            Sets.of(
                    RENAMED_PROVIDER_INFO,
                    BOTH_INFO,
                    PROVIDER_ONLY_INFO
            )
    );

    private final static TestPluginHelper HELPER = TestPluginHelper.INSTANCE;

    private final static FilteredProviderMapper<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias> MAPPER = FilteredProviderMapper.with(
            FILTERED_INFOS,
            PROVIDER_INFOS,
            HELPER
    );

    // with.............................................................................................................

    @Test
    public void testWithNullMappingInfosFails() {
        assertThrows(
                NullPointerException.class,
                () -> FilteredProviderMapper.with(
                        null,
                        PROVIDER_INFOS,
                        HELPER
                )
        );
    }

    @Test
    public void testWithNullProviderInfosFails() {
        assertThrows(
                NullPointerException.class,
                () -> FilteredProviderMapper.with(
                        FILTERED_INFOS,
                        null,
                        HELPER
                )
        );
    }

    @Test
    public void testWithNullHelperFails() {
        assertThrows(
                NullPointerException.class,
                () -> FilteredProviderMapper.with(
                        FILTERED_INFOS,
                        PROVIDER_INFOS,
                        null
                )
        );
    }

    // name.............................................................................................................

    @Test
    public void testNameFilteredNameFailed() {
        assertThrows(
                UnknownStringNameException.class,
                () -> MAPPER.name(RENAMED_PROVIDER_NAME)
        );
    }

    @Test
    public void testNameFilteredNameFailed2() {
        assertThrows(
                UnknownStringNameException.class,
                () -> MAPPER.name(PROVIDER_ONLY_NAME)
        );
    }

    @Test
    public void testNameUnknownFails() {
        assertThrows(
                UnknownStringNameException.class,
                () -> MAPPER.name(FILTERED_ONLY_NAME)
        );
    }

    @Test
    public void testName() {
        this.checkEquals(
                BOTH_NAME,
                MAPPER.name(BOTH_NAME)
        );
    }

    @Test
    public void testNameMapped() {
        this.checkEquals(
                RENAMED_PROVIDER_NAME,
                MAPPER.name(RENAMED_RENAME_NAME)
        );
    }

    // selector.............................................................................................................

    @Test
    public void testSelectorFilteredSelectorFailed() {
        assertThrows(
                UnknownStringNameException.class,
                () -> MAPPER.selector(
                        new TestPluginSelector(RENAMED_PROVIDER_NAME)
                )
        );
    }

    @Test
    public void testSelectorFilteredSelectorFailed2() {
        assertThrows(
                UnknownStringNameException.class,
                () -> MAPPER.selector(
                        new TestPluginSelector(PROVIDER_ONLY_NAME)
                )
        );
    }

    @Test
    public void testSelectorUnknownFails() {
        assertThrows(
                UnknownStringNameException.class,
                () -> MAPPER.selector(
                        new TestPluginSelector(FILTERED_ONLY_NAME)
                )
        );
    }

    @Test
    public void testSelector() {
        final TestPluginSelector selector = new TestPluginSelector(BOTH_NAME);

        this.checkEquals(
                selector,
                MAPPER.selector(selector)
        );
    }

    @Test
    public void testSelectorMapped() {
        this.checkEquals(
                new TestPluginSelector(RENAMED_PROVIDER_NAME),
                MAPPER.selector(
                        new TestPluginSelector(RENAMED_RENAME_NAME)
                )
        );
    }

    // infos.............................................................................................................

    @Test
    public void testInfos() {
        this.checkEquals(
                new TestPluginInfoSet(
                        Sets.of(
                                RENAMED_FILTERED_INFO,
                                BOTH_INFO
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
                "https://example.com/Name Name,https://example.com/RenamedRenameName1/RenamedProviderName1 RenamedRenameName1"
        );
    }

    // class............................................................................................................

    @Override
    public Class<FilteredProviderMapper<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias>> type() {
        return Cast.to(FilteredProviderMapper.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
