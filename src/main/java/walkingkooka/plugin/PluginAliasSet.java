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

import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.ImmutableSortedSet;
import walkingkooka.collect.set.ImmutableSortedSetDefaults;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

public final class PluginAliasSet<N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, IS extends PluginInfoSetLike<IS, I, N>, S extends PluginSelectorLike<N>> extends AbstractSet<PluginAlias<N, S>>
        implements ImmutableSortedSet<PluginAlias<N, S>>,
        ImmutableSortedSetDefaults<PluginAliasSet<N, I, IS, S>, PluginAlias<N, S>>,
        HasText,
        TreePrintable {

    /**
     * The separator character between name/alias declarations.
     */
    public final static CharacterConstant SEPARATOR = CharacterConstant.with(PluginExpressionParser.PARAMETER_SEPARATOR_CHARACTER);

    public static <N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, IS extends PluginInfoSetLike<IS, I, N>, S extends PluginSelectorLike<N>> PluginAliasSet<N, I, IS, S> parse(final String text,
                                                                                                                                                                                              final PluginHelper<N, I, IS, S> helper) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(helper, "helper");

        return parse0(
                PluginExpressionParser.with(
                        text,
                        helper::parseName,
                        PluginAliasesProviderContext.INSTANCE
                ),
                helper
        );
    }


    private static <N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, IS extends PluginInfoSetLike<IS, I, N>, S extends PluginSelectorLike<N>> PluginAliasSet<N, I, IS, S> parse0(final PluginExpressionParser<N> parser,
                                                                                                                                                                                                final PluginHelper<N, I, IS, S> helper) {

        final SortedSet<PluginAlias<N, S>> aliases = SortedSets.tree();

        boolean requireSeparator = false;

        while (false == parser.isEmpty()) {
            parser.spaces();

            if (requireSeparator) {
                if (false == parser.parameterSeparator()) {
                    throw parser.invalidCharacter();
                }
                parser.spaces();
            }

            // name
            final Optional<N> nameOrAlias = parser.name();
            if (nameOrAlias.isPresent()) {
                parser.spaces();

                final PluginAlias<N, S> pluginAlias;

                final Optional<S> maybeSelector = tryParseSelector(
                        parser,
                        helper
                );

                if (false == maybeSelector.isPresent()) {
                    // name END
                    pluginAlias = PluginAlias.with(
                            nameOrAlias.get(),
                            Optional.empty(), // no selector
                            Optional.empty() // no url
                    );

                    requireSeparator = true;
                } else {
                    parser.spaces();

                    final S selector = maybeSelector.get();
                    final N alias = nameOrAlias.get();

                    pluginAlias = PluginAlias.with(
                            alias,
                            Optional.of(selector), // selector
                            parser.url() // url
                    );

                    requireSeparator = true;
                }

                aliases.add(pluginAlias);
            }
        }

        return withoutCopying(
                aliases,
                helper
        );
    }

    /**
     * Tries to parse a selector expression returning a {@link PluginSelectorLike}.
     */
    private static <N extends Name & Comparable<N>,
            I extends PluginInfoLike<I, N>,
            IS extends PluginInfoSetLike<IS, I, N>,
            S extends PluginSelectorLike<N>> Optional<S> tryParseSelector(final PluginExpressionParser<N> parser,
                                                                          final PluginHelper<N, I, IS, S> helper) {
        final TextCursorSavePoint start = parser.cursor.save();
        TextCursorSavePoint end = null;

        S selector = null;
        final Optional<N> selectorName = parser.name();
        if (selectorName.isPresent()) {
            end = parser.cursor.save();

            parser.spaces();

            if (parser.parametersBegin()) {
                boolean requireComma = false;

                for (; ; ) {
                    parser.spaces();

                    if (parser.parametersEnd()) {
                        break;
                    }

                    if (requireComma) {
                        if (false == parser.parameterSeparator()) {
                            throw parser.invalidCharacter();
                        }
                    }

                    for (; ; ) {
                        if (parser.environmentValue().isPresent()) {
                            break;
                        }

                        if (parser.doubleQuotedString().isPresent()) {
                            break;
                        }

                        if (parser.number().isPresent()) {
                            break;
                        }

                        throw parser.invalidCharacter();
                    }

                    requireComma = true;
                }
            } else {
                end.restore(); // reset cursor back to any space after name
            }

            selector = helper.parseSelector(
                    start.textBetween()
                            .toString()
            );
        }

        return Optional.ofNullable(selector);
    }

    public static <N extends Name & Comparable<N>,
            I extends PluginInfoLike<I, N>,
            IS extends PluginInfoSetLike<IS, I, N>,
            S extends PluginSelectorLike<N>> PluginAliasSet<N, I, IS, S> with(final SortedSet<PluginAlias<N, S>> aliases,
                                                                              final PluginHelper<N, I, IS, S> helper) {
        Objects.requireNonNull(aliases, "aliases");
        Objects.requireNonNull(helper, "helper");

        return aliases instanceof PluginAliasSet ?
                Cast.to(aliases) :
                withoutCopying(
                        SortedSets.immutable(aliases),
                        helper
                );
    }

    private static <N extends Name & Comparable<N>,
            I extends PluginInfoLike<I, N>,
            IS extends PluginInfoSetLike<IS, I, N>,
            S extends PluginSelectorLike<N>> PluginAliasSet<N, I, IS, S> withoutCopying(final SortedSet<PluginAlias<N, S>> aliases,
                                                                                        final PluginHelper<N, I, IS, S> helper) {
        Objects.requireNonNull(aliases, "aliases");

        final Set<AbsoluteUrl> urls = Sets.hash();

        final Comparator<N> nameComparator = helper.nameComparator();

        final Map<N, S> nameToAlias = Maps.sorted(nameComparator);
        final Map<N, N> nameToName = Maps.sorted(nameComparator);

        final SortedSet<N> aliasesWithoutInfos = SortedSets.tree(nameComparator);
        final SortedSet<N> names = SortedSets.tree(nameComparator);

        final Set<I> infos = SortedSets.tree();

        for(final PluginAlias<N, S> pluginAlias : aliases) {
            final N nameOrAlias = pluginAlias.name();

            duplicateCheck(
                    nameOrAlias,
                    nameToAlias,
                    nameToName
            );

            final Optional<S> maybeSelector = pluginAlias.selector();
            if(false == maybeSelector.isPresent()) {
                final N name = nameOrAlias;

                nameToName.put(
                        name,
                        name
                );

                names.add(name);
            } else {
                final S selector = maybeSelector.get();
                final N alias = nameOrAlias;

                final Optional<AbsoluteUrl> maybeUrl = pluginAlias.url();

                if (maybeUrl.isPresent()) {
                    // url present add a new INFO
                    final AbsoluteUrl url = maybeUrl.get();
                    if(false == urls.add(url)) {
                        throw new IllegalArgumentException("Duplicate url " + url);
                    }

                    infos.add(
                            helper.info(
                                    url,
                                    alias
                            )
                    );
                } else {
                    aliasesWithoutInfos.add(alias);
                }

                duplicateCheck(
                        alias,
                        nameToAlias,
                        nameToName
                );
                nameToAlias.put(
                        alias,
                        selector
                );
//                names.add(
//                        selector.name()
//                );
            }
        }

        return new PluginAliasSet<>(
                aliases,
                nameToAlias,
                aliasesWithoutInfos,
                nameToName,
                names,
                helper.infoSet(infos),
                helper
        );
    }

    private static <N extends Name & Comparable<N>, S extends PluginSelectorLike<N>> void duplicateCheck(final N name,
                                                                                                         final Map<N, S> aliases,
                                                                                                         final Map<N, N> names) {
        if (aliases.containsKey(name) || names.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate name: " + CharSequences.quoteAndEscape(name.value()));
        }
    }

    // @VisibleForTesting
    PluginAliasSet(final SortedSet<PluginAlias<N, S>> sortedSet,
                   final Map<N, S> nameToAliases,
                   final Set<N> aliasesWithoutInfos,
                   final Map<N, N> nameToName,
                   final Set<N> names,
                   final IS infos,
                   final PluginHelper<N, I, IS, S> helper) {
        this.sortedSet = sortedSet;
        this.helper = helper;

        this.nameToAliases = nameToAliases;
        this.aliasesWithoutInfos = aliasesWithoutInfos;

        this.nameToName = nameToName;
        this.names = names;

        this.infos = infos;
    }

    /**
     * Returns the selector if one is present for the given {@link Name alias}.
     */
    public Optional<S> alias(final N name) {
        Objects.requireNonNull(name, "name");

        return Optional.ofNullable(
                this.nameToAliases.get(name)
        );
    }

    private final Map<N, S> nameToAliases;

    /**
     * Contains all alias {@link Name} for aliases without a {@link PluginInfoLike}.
     */
    // @VisibleForTesting
    final Set<N> aliasesWithoutInfos;

    /**
     * Queries the target name applying any aliases, or returning the name if no alias was present.
     * Note any aliases will not be returned and should be queried first.
     */
    public Optional<N> name(final N name) {
        Objects.requireNonNull(name, "name");

        return Optional.ofNullable(
                this.nameToName.get(name)
        );
    }

    /**
     * Maps a {@link Name} to its target name, including name changes or aliases.
     */
    private final Map<N, N> nameToName;

    /**
     * Contains all {@link Name} mappings which will also including the target for any alias, but not the alias itself.
     */
    // @VisibleForTesting
    final Set<N> names;

    /**
     * Contains all {@link PluginInfoSetLike} including those belonging to new aliases definitions.
     */
    // @VisibleForTesting
    final IS infos;

    /**
     * Accepts some {@link PluginInfoSetLike} and uses the aliases mappings within to produce a final {@link PluginInfoSetLike}
     */
    public IS merge(final IS providerInfos) {
        Objects.requireNonNull(providerInfos, "providerInfos");

        // verify all aliases -> name and names exist
        final Set<N> providerNames = providerInfos.names();

        final Set<N> unknownNames = SortedSets.tree();

        final Set<N> names = this.names;
        names.stream()
                .filter(n -> false == providerNames.contains(n))
                .forEach(unknownNames::add);

        this.nameToAliases.values()
                .stream()
                .map(s -> s.name())
                .filter(n -> false == providerNames.contains(n))
                .forEach(unknownNames::add);


        // Fix all INFOs for each alias
        IS newInfos = providerInfos;

        final Set<N> aliasNames = this.aliasesWithoutInfos;

        // remove $newInfos which are not referenced by name or alias
        final Set<I> unreferencedProviderInfos = Sets.hash();

        for(final I providerInfo : providerInfos) {
            if(false == names.contains(providerInfo.name())) {
                unreferencedProviderInfos.add(providerInfo);
            }
        }

        newInfos = newInfos.deleteAll(unreferencedProviderInfos);

        final IS aliasesInfos = this.infos;

        // remove unmentioned provider.infos

        if (aliasNames.size() + aliasesInfos.size() > 0) {
            final Map<N, I> nameToProviderInfo = Maps.sorted();

            for (final I providerInfo : providerInfos) {
                nameToProviderInfo.put(
                        providerInfo.name(),
                        providerInfo
                );
            }

            for (final N aliasName : aliasNames) {
                final Optional<S> selector = this.alias(aliasName);
                if (selector.isPresent()) {
                    final I providerInfo = nameToProviderInfo.get(
                            selector.get()
                                    .name()
                    );
                    if (null != providerInfo) {
                        newInfos = newInfos.replace(
                                providerInfo,
                                providerInfo.setName(aliasName)
                        );
                    }
                }
            }

            for (final I aliasInfo : aliasesInfos) {
                final N name = aliasInfo.name();
                final I providerInfo = nameToProviderInfo.get(name);
                if (null != providerInfo) {
                    newInfos = newInfos.replace(
                            providerInfo,
                            aliasInfo
                    );
                } else {
                    newInfos = newInfos.concat(
                            aliasInfo
                    );
                }
            }
        }

        if (false == unknownNames.isEmpty()) {
            throw new IllegalArgumentException(
                    "Unknown " +
                            this.helper.label() +
                            "(s): " +
                            CharacterConstant.COMMA.toSeparatedString(
                                    unknownNames,
                                    N::toString
                            )
            );
        }

        return newInfos;
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        if (null == this.text) {
            this.text = this.sortedSet.stream()
                    .map(PluginAlias::textAndSpace)
                    .collect(Collectors.joining(SEPARATOR_SPACE))
                    .trim();
        }

        return this.text;
    }

    private String text;

    private final static String SEPARATOR_SPACE = SEPARATOR + " ";

    // ImmutableSortedSet...............................................................................................

    @Override
    public Iterator<PluginAlias<N, S>> iterator() {
        return this.sortedSet.iterator();
    }

    @Override
    public int size() {
        return this.sortedSet.size();
    }

    @Override
    public Comparator<? super PluginAlias<N, S>> comparator() {
        return this.sortedSet.comparator();
    }

    @Override
    public PluginAliasSet<N, I, IS, S> subSet(final PluginAlias<N, S> from,
                                              final PluginAlias<N, S> to) {
        return withoutCopying(
                this.sortedSet.subSet(
                        from,
                        to
                ),
                this.helper
        );
    }

    @Override
    public PluginAliasSet<N, I, IS, S> headSet(final PluginAlias<N, S> alias) {
        return withoutCopying(
                this.sortedSet.headSet(alias),
                this.helper
        );
    }

    @Override
    public PluginAliasSet<N, I, IS, S> tailSet(final PluginAlias<N, S> alias) {
        return withoutCopying(
                this.sortedSet.tailSet(alias),
                this.helper
        );
    }

    @Override
    public PluginAlias<N, S> first() {
        return this.sortedSet.first();
    }

    @Override
    public PluginAlias<N, S> last() {
        return this.sortedSet.last();
    }

    @Override
    public SortedSet<PluginAlias<N, S>> toSet() {
        final SortedSet<PluginAlias<N, S>> sortedSet = SortedSets.tree(this.comparator());
        sortedSet.addAll(this.sortedSet);
        return sortedSet;
    }

    @Override
    public PluginAliasSet<N, I, IS, S> setElements(final SortedSet<PluginAlias<N, S>> sortedSet) {
        final PluginAliasSet<N, I, IS, S> copy = with(
                sortedSet,
                this.helper
        );

        return this.equals(copy) ?
                this :
                copy;
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        for(final PluginAlias<N, S> pluginAlias : this.sortedSet) {
            pluginAlias.printTree(printer);
        }
    }

    private final SortedSet<PluginAlias<N, S>> sortedSet;

    private final PluginHelper<N, I, IS, S> helper;
}
