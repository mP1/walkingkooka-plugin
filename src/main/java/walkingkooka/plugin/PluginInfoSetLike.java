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

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.ImmutableSet;
import walkingkooka.collect.set.ImmutableSetDefaults;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.naming.HasName;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasAbsoluteUrl;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.AbstractSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A {@link Set} that holds {@link PluginInfoLike} and a few related helpers.
 */
public interface PluginInfoSetLike<S extends PluginInfoSetLike<S, I, N>, I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> extends ImmutableSet<I>,
        ImmutableSetDefaults<S, I>,
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
    default S filter(final S provider) {
        Objects.requireNonNull(provider, "provider");

        final Set<AbsoluteUrl> providerUrls = provider.stream()
                .map(HasAbsoluteUrl::url)
                .collect(Collectors.toSet());

        return this.setElements(
                this.stream()
                        .filter(i -> providerUrls.contains(i.url()))
                        .collect(Collectors.toSet())
        );
    }

    // renameIfPresent..................................................................................................

    /**
     * Renames any infos if another {@link PluginNameLike} is present, that is another info with the same {@link AbsoluteUrl}.
     */
    default S renameIfPresent(final S renameInfos) {
        Objects.requireNonNull(renameInfos, "renameInfos");

        final Map<AbsoluteUrl, I> urlToInfo = Maps.hash();

        for (final I info : this) {
            urlToInfo.put(
                    info.url(),
                    info
            );
        }

        for (final I renameInfo : renameInfos) {
            final AbsoluteUrl renameInfoUrl = renameInfo.url();
            final I info = urlToInfo.get(renameInfoUrl);
            if(null != info) {
                urlToInfo.put(
                        renameInfoUrl,
                        renameInfo
                );
            }
        }

        final Set<I> newInfos = Sets.hash();
        newInfos.addAll(urlToInfo.values());
        return this.setElements(newInfos);
    }

    // parse............................................................................................................

    /**
     * Parses some text (actually a csv) holding multiple {@link PluginInfoLike} instances.
     * <pre>
     * SPACE*
     * URL
     * SPACE+
     * NAME
     * SPACE*
     * SEPARATOR-COMMA
     * </pre>
     *
     * <pre>
     * https://example.com/service-111 service-111,https://example.com/service-222 service-222
     * </pre>
     */
    static <S extends PluginInfoSetLike<S, I, N>, I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> S parse(final String text,
                                                                                                                          final Function<String, I> infoParser,
                                                                                                                          final Function<Set<I>, S> setFactory) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(infoParser, "infoParser");

        final Set<I> infos = SortedSets.tree();

        final PluginInfoSetLikeParser<N, I> parser = PluginInfoSetLikeParser.with(
                text,
                infoParser
        );

        parser.spaces();

        if (false == parser.isEmpty()) {
            for (; ; ) {
                parser.spaces();

                infos.add(parser.info());

                parser.spaces();

                if (PluginInfoSetLike.SEPARATOR.string().equals(parser.comma())) {
                    continue;
                }

                if (parser.isEmpty()) {
                    break;
                }

                parser.invalidCharacterException();
            }
        }

        return setFactory.apply(infos);
    }

    /**
     * Returns all the names in this set.
     */
    default Set<N> names() {
        return SortedSets.immutable(
                this.stream()
                        .map(HasName::name)
                        .collect(Collectors.toCollection(SortedSets::tree))
        );
    }

    /**
     * Returns all the {@link AbsoluteUrl} in this set.
     */
    default Set<AbsoluteUrl> url() {
        return Sets.immutable(
                this.stream()
                        .map(HasAbsoluteUrl::url)
                        .collect(
                                Collectors.toCollection(
                                        Sets::hash
                                )
                        )
        );
    }

    // HasText..........................................................................................................

    @Override
    default String text() {
        return SEPARATOR.toSeparatedString(
                this,
                Object::toString
        );
    }

    // HasUrlFragment...................................................................................................

    /**
     * Custom UrlFragment support is required otherwise the {@link AbstractSet#toString} will be used when creating a
     * save token which will eventually cause failure when the text is parsed back into the {@link PluginInfoSetLike}.
     */
    @Override
    default UrlFragment urlFragment() {
        return UrlFragment.with(
                this.text()
        );
    }

    // TreePrintable....................................................................................................

    @Override
    default void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            for (final I info : this) {
                TreePrintable.printTreeOrToString(
                        info,
                        printer
                );
                printer.lineStart();
            }
        }
        printer.outdent();
    }
}
