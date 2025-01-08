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

import walkingkooka.collect.set.SortedSets;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserContext;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A helper that includes methods to create or parse strings into Plugin classes.
 */
public interface PluginHelper<N extends Name & Comparable<N>,
    I extends PluginInfoLike<I, N>,
    IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
    S extends PluginSelectorLike<N>,
    A extends PluginAliasLike<N, S, A>,
    AS extends PluginAliasSetLike<N, I, IS, S, A, AS>> {

    /**
     * Factory that creates a {@link Name} with the given text.
     */
    N name(final String text);

    /**
     * Parses and consumes text returning a {@link Name} if a match was possible.
     */
    Optional<N> parseName(final TextCursor cursor,
                          final ParserContext context);

    /**
     * Creates an immutable {@link Set} of the given {@link Name}.
     */
    Set<N> names(final Set<N> names);

    /**
     * A {@link Comparator} that may be used to sort {@link Name}.
     */
    Comparator<N> nameComparator();

    /**
     * A function that reports an unknown name.
     */
    Function<N, RuntimeException> unknownName();

    /**
     * Parses the info text into a {@link PluginInfoLike}
     */
    I parseInfo(final String text);

    /**
     * Creates an {@link PluginInfoLike} with the given {@link AbsoluteUrl} and {@link Name}.
     */
    I info(final AbsoluteUrl url,
           final N name);

    /**
     * Creates an {@link PluginInfoSetLike} from the given {@link Set} of {@link PluginInfoLike}.
     */
    IS infoSet(final Set<I> infos);

    /**
     * Parses the given text into a {@link PluginSelectorLike}.
     */
    S parseSelector(final String text);

    /**
     * Factory that creates an {@link PluginAliasLike}.
     */
    A alias(final N name,
            final Optional<S> selector,
            final Optional<AbsoluteUrl> url);

    /**
     * Factory that wraps the given {@link PluginAlias}.
     */
    A alias(final PluginAlias<N, S> pluginAlias);

    /**
     * Factory that creates an {@link PluginAliasSetLike} with the {@link SortedSet} of {@link PluginAliasLike}.
     */
    AS aliasSet(final SortedSet<A> aliases);

    /**
     * Builds a {@link PluginAliasSetLike} from the given {@link PluginInfoSetLike}.
     */
    default AS toAliasSet(final IS infos) {
        Objects.requireNonNull(infos, "infos");

        return this.aliasSet(
            infos.stream()
                .map(this::toAlias)
                .collect(Collectors.toCollection(SortedSets::tree))
        );
    }

    private A toAlias(final I info) {
        return this.alias(
            info.name(),
            Optional.empty(), // selector
            Optional.empty() // url
        );
    }

    /**
     * A label which may be used in messages.
     */
    String label();
}
