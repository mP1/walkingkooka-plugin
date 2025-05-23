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
import walkingkooka.collect.set.SortedSets;
import walkingkooka.naming.Name;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.TreePrintable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * A {@link ImmutableSortedSet} holding {@link PluginAliasLike} entries.
 */
public interface PluginAliasSetLike<N extends Name & Comparable<N>,
    I extends PluginInfoLike<I, N>,
    IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
    S extends PluginSelectorLike<N>,
    A extends PluginAliasLike<N, S, A>,
    AS extends PluginAliasSetLike<N, I, IS, S, A, AS>>
    extends ImmutableSortedSet<A>,
    HasText,
    HasUrlFragment,
    TreePrintable {

    /**
     * Returns a {@link PluginSelectorLike} replacing the {@link Name} if it was an alias.
     */
    S selector(final S selector);

    /**
     * Returns the {@link PluginSelectorLike} for the given {@link Name alias} if one exists.
     */
    Optional<S> aliasSelector(final N alias);

    /**
     * Returns the target name resolving any alias if necessary
     */
    Optional<N> aliasOrName(final N name);

    /**
     * Returns a {@link PluginInfoSetLike} that applies alies/names to the given {@link PluginInfoSetLike provider infos}.
     */
    IS merge(final IS providerInfo);

    /**
     * Tests if an {@link PluginAliasLike} with the {@link Name name} or alias is present. Note aliases included.
     */
    boolean containsAliasOrName(final N aliasOrName);

    /**
     * If the {@link PluginAliasLike} name exists then replace the alias with the same {@link Name} or concat because it
     * is new.
     */
    AS concatOrReplace(final A alias);

    /**
     * Removes any {@link PluginAliasLike} with the given {@link Name name or alias}.
     */
    default AS deleteAliasOrName(final N aliasOrName) {
        Objects.requireNonNull(aliasOrName, "aliasOrName");

        // filter keep all other entries that do not have the name or alias.
        return this.setElements(
            this.toSet()
                .stream()
                .filter(
                    p -> false ==
                        (
                            aliasOrName.equals(p.name()) ||
                                aliasOrName.equals(
                                    p.selector()
                                        .map(PluginSelectorLike::name)
                                        .orElse(null)
                                )
                        )
                ).collect(Collectors.toCollection(SortedSets::tree))
        );
    }

    /**
     * Returns a {@link PluginAliasSetLike} after removing any with the {@link Name alias or name}.
     */
    AS deleteAliasOrNameAll(final Collection<N> aliasOrNames);

    /**
     * Returns a {@link PluginAliasSetLike} after keeping alias/names that also exist in the given {@link Collection}.
     */
    AS keepAliasOrNameAll(final Collection<N> aliasOrNames);

    // HasUrlFragment...................................................................................................

    /**
     * Returns a {@link UrlFragment} holding the {@link #text()}.
     */
    @Override
    default UrlFragment urlFragment() {
        return UrlFragment.with(this.text());
    }

    // ImmutableSortedSet...............................................................................................

    @Override
    AS subSet(final A from,
              final A to);

    @Override
    AS headSet(A from);

    @Override
    AS tailSet(final A to);

    @Override
    AS concat(final A alias);

    @Override
    AS concatAll(final Collection<A> aliases);

    @Override
    AS delete(final A alias);

    @Override
    AS deleteAll(Collection<A> aliases);

    @Override
    AS replace(final A oldAlias,
               final A newAlias);

    @Override
    default AS setElements(final Set<A> aliases) {
        Objects.requireNonNull(aliases, "aliases");

        return this.setElements(
            aliases instanceof SortedSet ?
                (SortedSet<A>) aliases :
                new TreeSet<>(aliases)
        );
    }

    @Override
    AS setElements(final SortedSet<A> set);

    @Override
    default AS setElementsFailIfDifferent(final Set<A> aliases) {
        Objects.requireNonNull(aliases, "aliases");

        return this.setElementsFailIfDifferent(
            aliases instanceof SortedSet ?
                (SortedSet<A>) aliases :
                new TreeSet<>(aliases)
        );
    }

    @Override
    AS setElementsFailIfDifferent(final SortedSet<A> aliases);
}
