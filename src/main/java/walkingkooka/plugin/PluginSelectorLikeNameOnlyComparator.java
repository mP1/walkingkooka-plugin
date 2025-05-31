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

import walkingkooka.naming.Name;

import java.util.Comparator;

/**
 * A {@link Comparator} that uses ONLY the {@link PluginSelectorLike#name()} ignoring any additional parameter values in the selector.
 */
final class PluginSelectorLikeNameOnlyComparator<P extends PluginSelectorLike<N>, N extends Name & Comparable<N>>
    implements Comparator<P> {

    /**
     * Type-safe instance getter.
     */
    static <P extends PluginSelectorLike<N>, N extends Name & Comparable<N>> PluginSelectorLikeNameOnlyComparator<P, N> instance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private final static PluginSelectorLikeNameOnlyComparator INSTANCE = new PluginSelectorLikeNameOnlyComparator<>();

    private PluginSelectorLikeNameOnlyComparator() {
        super();
    }

    @Override
    public int compare(final P left,
                       final P right) {
        return left.name()
            .compareTo(
                right.name()
            );
    }

    // toString.........................................................................................................

    @Override
    public String toString() {
        return "nameOnlyComparator";
    }
}
