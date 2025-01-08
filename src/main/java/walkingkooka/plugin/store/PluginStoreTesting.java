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

package walkingkooka.plugin.store;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.PluginName;
import walkingkooka.store.StoreTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface PluginStoreTesting<T extends PluginStore> extends StoreTesting<T, PluginName, Plugin> {

    @Test
    default void testFilterWithNullQueryFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .filter(
                    null,
                    0,
                    1
                )
        );
    }

    @Test
    default void testFilterWithNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .filter(
                    "query",
                    -1,
                    1
                )
        );
    }

    @Test
    default void testFilterWithNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .filter(
                    "query",
                    0,
                    -1
                )
        );
    }

    default void filterAndCheck(final T store,
                                final String query,
                                final int offset,
                                final int count,
                                final Plugin... plugins) {
        this.filterAndCheck(
            store,
            query,
            offset,
            count,
            Lists.of(plugins)
        );
    }

    default void filterAndCheck(final T store,
                                final String query,
                                final int offset,
                                final int count,
                                final List<Plugin> plugins) {
        this.checkEquals(
            plugins,
            store.filter(
                query,
                offset,
                count
            )
        );
    }
}
