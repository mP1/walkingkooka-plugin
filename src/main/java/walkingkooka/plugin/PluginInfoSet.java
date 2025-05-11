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

import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.ImmutableSetDefaults;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A {@link Set} of {@link PluginInfoLike}. Instances are intended to be wrapped.
 */
public final class PluginInfoSet<N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>> extends AbstractSet<I> implements ImmutableSetDefaults<PluginInfoSet<N, I>, I>,
    HasText,
    HasUrlFragment,
    TreePrintable {

    /**
     * Parses the text into a {@link PluginInfoSet}.
     */
    public static <N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>> PluginInfoSet<N, I> parse(final String text,
                                                                                                             final Function<String, I> infoParser) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(infoParser, "infoParser");

        final SortedSet<I> infos = SortedSets.tree();

        final PluginInfoSetLikeParser<N, I> parser = PluginInfoSetLikeParser.with(
            text,
            infoParser
        );

        parser.whitespace();

        if (parser.isNotEmpty()) {
            for (; ; ) {
                parser.whitespace();

                infos.add(parser.info());

                parser.whitespace();

                if (SEPARATOR.string().equals(parser.comma())) {
                    continue;
                }

                if (parser.isEmpty()) {
                    break;
                }

                parser.invalidCharacterException();
            }
        }

        return prepare(infos);
    }

    /**
     * The character that separates multiple {@link PluginInfoLike}.
     */
    private final static CharacterConstant SEPARATOR = CharacterConstant.COMMA;

    /**
     * Factory that creates a {@link PluginInfoSet} after taking a copy.
     */
    public static <N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>> PluginInfoSet<N, I> with(final Set<I> infos) {
        Objects.requireNonNull(infos, "infos");

        return prepare(
            copy(infos)
        );
    }

    private static <N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>> SortedSet<I> copy(final Set<I> infos) {
        return infos instanceof SortedSet ?
            SortedSets.immutable((SortedSet<I>) infos) :
            new TreeSet<>(infos);
    }

    private static <N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>> PluginInfoSet<N, I> prepare(final SortedSet<I> infos) {
        Objects.requireNonNull(infos, "infos");

        final SortedSet<I> infosCopy = SortedSets.tree();
        final Set<AbsoluteUrl> urls = Sets.hash();
        final Set<N> names = SortedSets.tree();

        for (final I info : infos) {
            final AbsoluteUrl url = info.url();
            if (false == urls.add(url)) {
                throw new IllegalArgumentException("Duplicate url " + CharSequences.quoteAndEscape(url.toString()));
            }

            final N name = info.name();
            if (false == names.add(name)) {
                throw new IllegalArgumentException("Duplicate name " + CharSequences.quoteAndEscape(name.toString()));
            }

            infosCopy.add(info);
        }

        return new PluginInfoSet<>(
            infosCopy,
            Sets.readOnly(urls),
            Sets.readOnly(names)
        );
    }

    // @VisibleForTesting
    PluginInfoSet(final SortedSet<I> infos,
                  final Set<AbsoluteUrl> urls,
                  final Set<N> names) {
        this.infos = infos;
        this.urls = urls;
        this.names = names;
    }

    /**
     * Returns all the {@link Name names} in this set.
     */
    public Set<N> names() {
        return this.names;
    }

    private final Set<N> names;

    /**
     * Returns all the {@link AbsoluteUrl} for all {@link PluginInfoLike}.
     */
    public Set<AbsoluteUrl> url() {
        return this.urls;
    }

    private final Set<AbsoluteUrl> urls;

    // filter...........................................................................................................

    /**
     * Returns a filtered {@link PluginInfoSetLike} only keeping {@link PluginInfoLike} that exist in the provider with the same {@link AbsoluteUrl}.
     */
    public PluginInfoSet<N, I> filter(final PluginInfoSet<N, I> infos) {
        Objects.requireNonNull(infos, "infos");

        final Set<AbsoluteUrl> infoUrls = infos.url();

        return this.setElements(
            this.stream()
                .filter(i -> infoUrls.contains(i.url()))
                .collect(Collectors.toSet())
        );
    }

    // renameIfPresent..................................................................................................

    /**
     * Renames any infos if another {@link PluginNameLike} is present, that is another info with the same {@link AbsoluteUrl}.
     */
    public PluginInfoSet<N, I> renameIfPresent(final PluginInfoSet<N, I> renameInfos) {
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
            if (null != info) {
                urlToInfo.put(
                    renameInfoUrl,
                    renameInfo
                );
            }
        }

        final SortedSet<I> newInfos = SortedSets.tree();
        newInfos.addAll(urlToInfo.values());
        return this.setElements0(newInfos);
    }

    // AbstractSet......................................................................................................

    @Override
    public Iterator<I> iterator() {
        return Iterators.readOnly(
            this.infos.iterator()
        );
    }

    @Override
    public int size() {
        return this.infos.size();
    }

    @Override
    public void elementCheck(final I info) {
        Objects.requireNonNull(info, "info");
    }

    @Override
    public PluginInfoSet<N, I> setElements(final Set<I> infos) {
        return this.setElements0(
            copy(infos)
        );
    }

    private PluginInfoSet<N, I> setElements0(final SortedSet<I> infos) {
        return this.infos.equals(infos) ?
            this :
            prepare(infos);
    }

    @Override
    public Set<I> toSet() {
        return new TreeSet<>(this.infos);
    }

    private final Set<I> infos;

    // HasText..........................................................................................................

    @Override
    public String text() {
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
    public UrlFragment urlFragment() {
        return UrlFragment.with(
            this.text()
        );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        for (final I info : this) {
            TreePrintable.printTreeOrToString(
                info,
                printer
            );
            printer.lineStart();
        }
    }

    // json.............................................................................................................

    public JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.text());
    }

    public static <N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>> PluginInfoSet<N, I> unmarshall(final JsonNode json,
                                                                                                                  final Function<String, I> infoParser,
                                                                                                                  final JsonNodeUnmarshallContext context) {
        return parse(
            json.stringOrFail(),
            infoParser
        );
    }
}
