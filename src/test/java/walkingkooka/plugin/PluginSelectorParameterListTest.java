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
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.naming.Names;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginSelectorParameterListTest implements ImmutableListTesting<PluginSelectorParameterList, Object> {

    @Test
    public void testGetNegativeIndexFails2() {
        final ArrayIndexOutOfBoundsException thrown = assertThrows(
            ArrayIndexOutOfBoundsException.class,
            () -> this.createList()
                .get(-1)
        );

        this.checkEquals(
            "HelloPlugin: Invalid parameter -1",
            thrown.getMessage()
        );
    }

    @Test
    public void testGetIndexFails2() {
        final ArrayIndexOutOfBoundsException thrown = assertThrows(
            ArrayIndexOutOfBoundsException.class,
            () -> this.createList()
                .get(4)
        );

        this.checkEquals(
            "HelloPlugin: Missing parameter 4",
            thrown.getMessage()
        );
    }

    @Test
    public void testGet() {
        final PluginSelectorParameterList list = this.createList();

        this.getAndCheck(
            list,
            1,
            11
        );
    }

    @Test
    public void testGetNullElement() {
        final PluginSelectorParameterList list = this.createList();

        this.getAndCheck(
            list,
            2,
            null
        );
    }

    @Override
    public void testDeleteAllWithEmptyCollection() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testDeleteIfWithNeverPredicate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetElementsSame() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PluginSelectorParameterList createList() {
        return PluginSelectorParameterList.with(
            Lists.of(
                0,
                11,
                null,
                333
            ),
            Names.string("HelloPlugin")
        );
    }

    // class............................................................................................................

    @Override
    public Class<PluginSelectorParameterList> type() {
        return PluginSelectorParameterList.class;
    }
}
