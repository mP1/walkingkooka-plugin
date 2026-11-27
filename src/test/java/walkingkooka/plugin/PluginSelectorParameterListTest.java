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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginSelectorParameterListTest implements ImmutableListTesting<PluginSelectorParameterList, Object> {

    private static final walkingkooka.naming.StringName PLUGIN_NAME = Names.string("HelloPlugin");

    private final static String STRING1 = "AAA";

    private final static String STRING2 = "BBB";

    private final static String STRING3 = "CCC";

    private final static String STRING4 = "DDD";

    private final static String STRING5 = "EEE";

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
    public void testSetElementsWithSameList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetElementsWithSameChildren() {
        throw new UnsupportedOperationException();
    }

    // subList..........................................................................................................

    private final static List<Object> ELEMENTS = Lists.of(
        STRING1,
        STRING2,
        STRING3,
        STRING4,
        STRING5
    );

    @Test
    public void testSubList() {
        this.subListAndCheck(
            new PluginSelectorParameterList(
                ELEMENTS,
                PLUGIN_NAME
            ),
            0,
            2,
            new PluginSelectorParameterList(
                Lists.of(
                    STRING1,
                    STRING2
                ),
                PLUGIN_NAME
            )
        );
    }

    @Test
    public void testSubList2() {
        this.subListAndCheck(
            new PluginSelectorParameterList(
                ELEMENTS,
                PLUGIN_NAME
            ),
            2,
            4,
            new PluginSelectorParameterList(
                Lists.of(
                    STRING3,
                    STRING4
                ),
                PLUGIN_NAME
            )
        );
    }

    @Test
    public void testSubListSubList() {
        this.subListAndCheck(
            new PluginSelectorParameterList(
                ELEMENTS,
                PLUGIN_NAME
            ).subList(
                1,
                4
            ), // 2, 3, 4
            1,
            3,
            new PluginSelectorParameterList(
                Lists.of(
                    STRING3,
                    STRING4
                ),
                PLUGIN_NAME
            )
        );
    }

    @Override
    public void testSubListWithZeroFromAndToSize() {
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
            PLUGIN_NAME
        );
    }

    // firstOrEmpty.....................................................................................................

    @Test
    public void testFirstOrEmptyWhenEmpty() {
        this.firstOrEmptyAndCheck(
            PluginSelectorParameterList.with(
                Lists.empty(),
                PLUGIN_NAME
            )
        );
    }

    @Test
    public void testFirstOrEmptyWhenNotEmpty() {
        this.firstOrEmptyAndCheck(
            PluginSelectorParameterList.with(
                Lists.of(
                    "111",
                    "2222"
                ),
                PLUGIN_NAME
            ),
            "111"
        );
    }

    // reverse..........................................................................................................

    @Override
    public void testReversed() {
        throw new UnsupportedOperationException();
    }

    // class............................................................................................................

    @Override
    public Class<PluginSelectorParameterList> type() {
        return PluginSelectorParameterList.class;
    }
}
