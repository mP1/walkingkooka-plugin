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
import walkingkooka.collect.set.ImmutableSortedSetTesting;
import walkingkooka.collect.set.SortedSets;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginNameSetTest implements ImmutableSortedSetTesting<PluginNameSet, PluginName> {

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginNameSet.with(null)
        );
    }

    @Override
    public PluginNameSet createSet() {
        return PluginNameSet.with(
                SortedSets.of(
                        PluginName.with("Plugin111"),
                        PluginName.with("Plugin222")
                )
        );
    }

    // class............................................................................................................

    @Override
    public Class<PluginNameSet> type() {
        return PluginNameSet.class;
    }
}
