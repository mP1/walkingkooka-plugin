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
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.Optional;

public final class PluginAliasSetLikeTest implements ClassTesting<PluginAliasSetLike<StringName,
    TestPluginInfo,
    TestPluginInfoSet,
    TestPluginSelector,
    TestPluginAlias,
    TestPluginAliasSet>> {

    @Test
    public void testSetElementsSetWithSet() {
        final TestPluginAliasSet test = TestPluginAliasSet.with(
            SortedSets.empty()
        );

        final TestPluginAlias alias = TestPluginAlias.with(
            Names.string("Name"),
            Optional.empty(), // selector
            Optional.empty() // url
        );

        this.checkEquals(
            TestPluginAliasSet.with(
                SortedSets.of(alias)
            ),
            test.setElements(
                Sets.of(alias)
            )
        );
    }

    @Test
    public void testSetElementsSetWithSortedSet() {
        final TestPluginAliasSet test = TestPluginAliasSet.with(
            SortedSets.empty()
        );

        final TestPluginAlias alias = TestPluginAlias.with(
            Names.string("Name"),
            Optional.empty(), // selector
            Optional.empty() // url
        );

        this.checkEquals(
            TestPluginAliasSet.with(
                SortedSets.of(alias)
            ),
            test.setElements(
                SortedSets.of(alias)
            )
        );
    }

    @Test
    public void testSetElementsFailIfDifferentSetWithSet() {
        final TestPluginAlias alias = TestPluginAlias.with(
            Names.string("Name"),
            Optional.empty(), // selector
            Optional.empty() // url
        );

        final TestPluginAliasSet test = TestPluginAliasSet.with(
            SortedSets.of(alias)
        );

        this.checkEquals(
            TestPluginAliasSet.with(
                SortedSets.of(alias)
            ),
            test.setElementsFailIfDifferent(
                Sets.of(alias)
            )
        );
    }

    @Test
    public void testSetElementsFailIfDifferentSetWithSortedSet() {
        final TestPluginAlias alias = TestPluginAlias.with(
            Names.string("Name"),
            Optional.empty(), // selector
            Optional.empty() // url
        );

        final TestPluginAliasSet test = TestPluginAliasSet.with(
            SortedSets.of(alias)
        );

        this.checkEquals(
            TestPluginAliasSet.with(
                SortedSets.of(alias)
            ),
            test.setElementsFailIfDifferent(
                SortedSets.of(alias)
            )
        );
    }

    // class............................................................................................................

    @Override
    public Class<PluginAliasSetLike<StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias, TestPluginAliasSet>> type() {
        return Cast.to(PluginAliasSetLike.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
