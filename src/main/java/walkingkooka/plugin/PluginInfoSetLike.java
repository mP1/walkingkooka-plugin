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
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.TreePrintable;

import java.util.Set;

/**
 * A {@link Set} that holds {@link PluginInfoLike} and a few related helpers.
 */
public interface PluginInfoSetLike<S extends PluginInfoSetLike<S, I, N>, I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> extends ImmutableSet<I>,
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
    S filter(final S infos);

    // renameIfPresent..................................................................................................

    /**
     * Renames any infos if another {@link PluginNameLike} is present, that is another info with the same {@link AbsoluteUrl}.
     */
    S renameIfPresent(final S renameInfos);

    /**
     * Returns all the names in this set.
     */
    Set<N> names();

    /**
     * Returns all the {@link AbsoluteUrl} in this set.
     */
    Set<AbsoluteUrl> url();

    // ImmutableSet.....................................................................................................
    @Override
    S concat(final I info);

    @Override
    S delete(final I info);

    @Override
    S replace(final I newInfo,
              final I oldInfo);

    @Override
    S setElements(final Set<I> newElements);
}
