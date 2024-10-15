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

import walkingkooka.collect.set.ImmutableSortedSet;
import walkingkooka.naming.Name;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.TreePrintable;

import java.util.Optional;

/**
 * A {@link ImmutableSortedSet} holding {@link PluginAliasLike} entries.
 */
public interface PluginAliasSetLike<N extends Name & Comparable<N>,
        I extends PluginInfoLike<I, N>,
        IS extends PluginInfoSetLike<IS, I, N>,
        S extends PluginSelectorLike<N>,
        A extends PluginAliasLike<N, S, A>>
        extends ImmutableSortedSet<A>,
            HasText,
            HasUrlFragment,
            TreePrintable {

    /**
     * A mapping function that returns the {@link PluginSelectorLike selector} for the given {@link Name} which is probably an alias.
     */
    Optional<S> alias(final N name);

    /**
     * Queries the target name applying any aliases, or returning the name if no alias was present.
     * Note any aliases will not be returned and should be queried first.
     */
    Optional<N> name(final N name);

    /**
     * Returns a {@link PluginInfoSetLike} that applies alies/names to the given {@link PluginInfoSetLike provider infos}.
     */
    IS merge(final IS providerInfo);

    /**
     * Tests if an alias with the {@link Name} is present. Note aliases are ignored.
     */
    boolean containsName(final N name);

    // HasUrlFragment...................................................................................................

    /**
     * Returns a {@link UrlFragment} holding the {@link #text()}.
     */
    @Override
    default UrlFragment urlFragment() {
        return UrlFragment.with(this.text());
    }
}
