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

import walkingkooka.collect.set.ImmutableSet;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.TreePrintable;

import java.util.Collection;
import java.util.Set;

/**
 * A {@link Set} that holds {@link PluginInfoLike} and a few related helpers.
 */
public interface PluginInfoSetLike<N extends Name & Comparable<N>,
    I extends PluginInfoLike<I, N>,
    IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
    S extends PluginSelectorLike<N>,
    A extends PluginAliasLike<N, S, A>,
    AS extends PluginAliasSetLike<N, I, IS, S, A, AS>>
    extends ImmutableSet<I>,
    HasText,
    HasUrlFragment,
    TreePrintable {

    /**
     * The character that separates multiple {@link PluginInfoLike}.
     */
    CharacterConstant SEPARATOR = CharacterConstant.COMMA;

    // filter...........................................................................................................

    /**
     * Returns a filtered {@link PluginInfoSetLike} only keeping {@link PluginInfoLike} that exist in the provider with the same {@link AbsoluteUrl}.
     */
    IS filter(final IS infos);

    // renameIfPresent..................................................................................................

    /**
     * Renames any infos if another {@link PluginNameLike} is present, that is another info with the same {@link AbsoluteUrl}.
     */
    IS renameIfPresent(final IS renameInfos);

    /**
     * Returns all the names in this set.
     */
    Set<N> names();

    /**
     * Returns all the {@link AbsoluteUrl} in this set.
     */
    Set<AbsoluteUrl> url();

    /**
     * Returns an {@link PluginAliasSetLike} with a {@link PluginAliasLike} created for each {@link PluginInfoLike} sharing only the {@link Name}.
     */
    AS aliasSet();

    // ImmutableSet.....................................................................................................
    @Override
    IS concat(final I info);

    @Override
    IS delete(final I info);

    @Override
    IS deleteAll(final Collection<I> infos);

    @Override
    IS replace(final I newInfo,
               final I oldInfo);

    @Override
    IS setElements(final Collection<I> newElements);

    // HasUrlFragment...................................................................................................

    /**
     * Returns a {@link UrlFragment} holding the {@link #text()}.
     */
    @Override
    default UrlFragment urlFragment() {
        return UrlFragment.with(this.text());
    }
}
