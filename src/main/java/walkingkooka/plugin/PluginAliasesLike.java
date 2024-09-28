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

import java.util.Optional;
import java.util.Set;

/**
 * Defines several methods that are common for alias and name definitions for a plugin.
 */
public interface PluginAliasesLike<N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, IS extends PluginInfoSetLike<IS, I, N>, S extends PluginSelectorLike<N>> {

    /**
     * A mapping function that returns the {@link PluginSelectorLike selector} for the given {@link Name} which is probably an alias.
     */
    Optional<S> alias(final N name);

    /**
     * Getter that returns all alias {@link Name} for aliases without a {@link PluginInfoLike}.
     */
    Set<N> aliases();

    /**
     * Queries the target name applying any aliases, or returning the name if no alias was present.
     * Note any aliases will not be returned and should be queried first.
     */
    Optional<N> name(final N name);

    /**
     * Returns all {@link Name} mappings which will also including the target for any alias, but not the alias itself.
     */
    Set<N> names();

    /**
     * Returns all {@link PluginInfoSetLike} including those belonging to new aliases definitions.
     */
    IS infos();
}
