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
import walkingkooka.collect.set.ImmutableSortedSetDefaults;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
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

/**
 * A {@link Set} containing unique alias/name mappings. Instances of this class should be wrapped.
 */
public final class PluginAliasSet<N extends Name & Comparable<N>,
        I extends PluginInfoLike<I, N>,
        IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
        S extends PluginSelectorLike<N>,
        A extends PluginAliasLike<N, S, A>,
        AS extends PluginAliasSetLike<N, I, IS, S, A, AS>>
        extends AbstractSet<A>
        implements ImmutableSortedSetDefaults<PluginAliasSet<N, I, IS, S, A, AS>, A>,
        HasText,
        TreePrintable {

    /**
     * The separator character between name/alias declarations.
     */
    public final static CharacterConstant SEPARATOR = CharacterConstant.with(PluginExpressionParser.PARAMETER_SEPARATOR_CHARACTER);

    public static <N extends Name & Comparable<N>,
            I extends PluginInfoLike<I, N>,
            IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
            S extends PluginSelectorLike<N>,
            A extends PluginAliasLike<N, S, A>,
            AS extends PluginAliasSetLike<N, I, IS, S, A, AS>>
    PluginAliasSet<N, I, IS, S, A, AS> parse(final String text,
                                             final PluginHelper<N, I, IS, S, A, AS> helper) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(helper, "helper");

        return parse0(
                PluginExpressionParser.with(
                        text,
                        helper::parseName
                ),
                helper
        );
    }

    private static <N extends Name & Comparable<N>,
            I extends PluginInfoLike<I, N>,
            IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
            S extends PluginSelectorLike<N>,
            A extends PluginAliasLike<N, S, A>,
            AS extends PluginAliasSetLike<N, I, IS, S, A, AS>>
    PluginAliasSet<N, I, IS, S, A, AS> parse0(final PluginExpressionParser<N> parser,
                                              final PluginHelper<N, I, IS, S, A, AS> helper) {

        final SortedSet<A> aliases = SortedSets.tree();

        boolean requireSeparator = false;

        while (parser.isNotEmpty()) {
            parser.spaces();
            if (parser.isEmpty()) {
                break;
            }

            if (requireSeparator) {
                if (false == parser.parameterSeparator()) {
                    throw parser.invalidCharacter();
                }
                parser.spaces();
            }

            final A pluginAliasLike = helper.alias(
                    PluginAlias.parse0(
                            parser,
                            helper,
                            PluginAliasesProviderContext.INSTANCE
                    )
            );

            if(false == aliases.add(pluginAliasLike)) {
                throw new IllegalArgumentException("Duplicate " + pluginAliasLike);
            }
            requireSeparator = true;
        }

        return prepare(
                aliases,
                helper
        );
    }

    public static <N extends Name & Comparable<N>,
            I extends PluginInfoLike<I, N>,
            IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
            S extends PluginSelectorLike<N>,
            A extends PluginAliasLike<N, S, A>,
            AS extends PluginAliasSetLike<N, I, IS, S, A, AS>>
    PluginAliasSet<N, I, IS, S, A, AS> with(final SortedSet<A> aliases,
                                            final PluginHelper<N, I, IS, S, A, AS> helper) {
        Objects.requireNonNull(aliases, "aliases");
        Objects.requireNonNull(helper, "helper");

        return aliases instanceof PluginAliasSet ?
                Cast.to(aliases) :
                prepare(
                        SortedSets.immutable(aliases),
                        helper
                );
    }

    private static <N extends Name & Comparable<N>,
            I extends PluginInfoLike<I, N>,
            IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
            S extends PluginSelectorLike<N>,
            A extends PluginAliasLike<N, S, A>,
            AS extends PluginAliasSetLike<N, I, IS, S, A, AS>>
    PluginAliasSet<N, I, IS, S, A, AS> prepare(final SortedSet<A> aliases,
                                               final PluginHelper<N, I, IS, S, A, AS> helper) {
        Objects.requireNonNull(aliases, "aliases");

        final Set<AbsoluteUrl> infoUrls = Sets.hash();

        final Comparator<N> nameComparator = helper.nameComparator();

        final Map<N, S> aliasToSelector = Maps.sorted(nameComparator);
        final Map<N, N> aliasOrNameToName = Maps.sorted(nameComparator);
        final Map<N, N> nameToAlias = Maps.sorted(nameComparator);

        final SortedSet<N> aliasesWithoutInfos = SortedSets.tree(nameComparator);
        final SortedSet<N> namesNotAliases = SortedSets.tree(nameComparator);

        for (final A pluginAlias : aliases) {
            final N nameOrAlias = pluginAlias.name();

            duplicateCheck(
                    nameOrAlias,
                    aliasToSelector,
                    aliasOrNameToName
            );

            final Optional<S> maybeSelector = pluginAlias.selector();
            if (false == maybeSelector.isPresent()) {
                final N name = nameOrAlias;

                if(null != aliasOrNameToName.put(
                        name,
                        name
                )) {
                    throw new IllegalArgumentException("Duplicate name: " + name);
                };

                namesNotAliases.add(name);
            } else {
                final S selector = maybeSelector.get();
                final N alias = nameOrAlias;
                duplicateCheck(
                        alias,
                        aliasToSelector,
                        aliasOrNameToName
                );

                final Optional<AbsoluteUrl> maybeUrl = pluginAlias.url();

                if (maybeUrl.isPresent()) {
                    // url present add a new INFO
                    final AbsoluteUrl url = maybeUrl.get();
                    if (false == infoUrls.add(url)) {
                        throw new IllegalArgumentException("Duplicate url: " + url);
                    }
                } else {
                    if (false == aliasesWithoutInfos.add(alias)) {
                        throw new IllegalArgumentException("Duplicate alias: " + alias);
                    }
                    final N duplicate = nameToAlias.put(
                            selector.name(),
                            alias
                    );
                    if (null != duplicate) {
                        throw new IllegalArgumentException("Duplicate alias: " + duplicate + " and " + alias);
                    }
                }

                aliasToSelector.put(
                        alias,
                        selector
                );
            }
        }


        // both alias to a name and the name cannot both exist if the alias does not have a different info
        final String duplicateNamesAliases = aliasesWithoutInfos.stream()
                .filter(a -> {
                    final N aliasName = a;
                    final S selector = aliasToSelector.get(aliasName);
                    return null != selector && namesNotAliases.contains(selector.name());
                }).map(Name::toString)
                .collect(Collectors.joining(", "));
        if (false == duplicateNamesAliases.isEmpty()) {
            throw new IllegalArgumentException("Duplicate name/alias: " + duplicateNamesAliases);
        }

        return new PluginAliasSet<>(
                aliases,
                aliasToSelector,
                aliasOrNameToName,
                namesNotAliases,
                helper
        );
    }

    private static <N extends Name & Comparable<N>, S extends PluginSelectorLike<N>> void duplicateCheck(final N name,
                                                                                                         final Map<N, S> aliasToSelector,
                                                                                                         final Map<N, N> aliasOrNameToName) {
        if (aliasToSelector.containsKey(name) || aliasOrNameToName.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate name/alias: " + name);
        }
    }

    // @VisibleForTesting
    PluginAliasSet(final SortedSet<A> pluginAliasLikes,
                   final Map<N, S> aliasToSelector,
                   final Map<N, N> aliasOrNameToName,
                   final Set<N> namesNotAliases,
                   final PluginHelper<N, I, IS, S, A, AS> helper) {
        this.pluginAliasLikes = pluginAliasLikes;

        this.aliasToSelector = aliasToSelector;

        this.aliasOrNameToName = aliasOrNameToName;
        this.namesNotAliases = namesNotAliases;

        this.helper = helper;
    }

    /**
     * Returns the selector if one is present for the given {@link Name alias}.
     */
    public Optional<S> aliasSelector(final N alias) {
        Objects.requireNonNull(alias, "alias");

        return Optional.ofNullable(
                this.aliasToSelector.get(alias)
        );
    }

    private final Map<N, S> aliasToSelector;

    /**
     * Returns the target name resolving any alias if necessary
     */
    public Optional<N> aliasOrName(final N aliasOrName) {
        Objects.requireNonNull(aliasOrName, "aliasOrName");

        return Optional.ofNullable(
                this.aliasOrNameToName.get(aliasOrName)
        );
    }

    /**
     * Maps a {@link Name} to its target name, including name changes or aliases.
     */
    private final Map<N, N> aliasOrNameToName;

    /**
     * Accepts some {@link PluginInfoSetLike} and uses the aliases mappings within to produce a final {@link PluginInfoSetLike}.
     * Note aliases/name mappings not present in the provider {@link PluginInfoSetLike} will be silently removed.
     */
    public IS merge(final IS providerInfos) {
        Objects.requireNonNull(providerInfos, "providerInfos");

        final PluginHelper<N, I, IS, S, A, AS> helper = this.helper;
        final Comparator<N> nameComparator = helper.nameComparator();

        // Fix all INFOs for each alias
        IS newInfos = providerInfos;

        final Set<N> aliasesWithoutInfos = this.pluginAliasLikes.stream()
                .filter(pal -> pal.selector().isPresent() && false == pal.url().isPresent())
                .map(pal -> pal.name())
                .collect(Collectors.toSet());

        // remove $newInfos which are not referenced by name or alias
        final Set<I> unreferencedProviderInfos = Sets.hash();

        final Set<N> namesNotAliases = this.namesNotAliases;

        for (final I providerInfo : providerInfos) {
            if (false == namesNotAliases.contains(providerInfo.name())) {
                unreferencedProviderInfos.add(providerInfo);
            }
        }

        newInfos = newInfos.deleteAll(unreferencedProviderInfos);

        final IS aliasesInfos = helper.infoSet(
                this.pluginAliasLikes.stream()
                        .filter(pal -> pal.url().isPresent())
                        .map(pal -> helper.info(
                                        pal.url()
                                                .get(),
                                        pal.name()
                                )
                        ).collect(Collectors.toCollection(SortedSets::tree))
        );

        // remove unmentioned provider.infos

        if (aliasesWithoutInfos.size() + aliasesInfos.size() > 0) {
            final Map<N, I> nameToProviderInfo = Maps.sorted(nameComparator);

            for (final I providerInfo : providerInfos) {
                nameToProviderInfo.put(
                        providerInfo.name(),
                        providerInfo
                );
            }

            for (final N aliasName : aliasesWithoutInfos) {
                final Optional<S> selector = this.aliasSelector(aliasName);
                if (selector.isPresent()) {
                    final I providerInfo = nameToProviderInfo.get(
                            selector.get()
                                    .name()
                    );
                    if (null != providerInfo) {

                        if (newInfos.contains(providerInfo)) {
                            newInfos = newInfos.replace(
                                    providerInfo,
                                    providerInfo.setName(aliasName)
                            );

                        } else {
                            newInfos = newInfos.concat(
                                    providerInfo.setName(aliasName)
                            );
                        }
                    }
                }
            }

            final Set<N> providerName = providerInfos.names();

            for (final I aliasInfo : aliasesInfos) {
                // get name for alias and verify name is present in providerInfo.names
                final N alias = aliasInfo.name();
                final Optional<S> selector = this.aliasSelector(alias);
                if (selector.isPresent()) {
                    if (providerName.contains(selector.get().name())) {

                        final I providerInfo = nameToProviderInfo.get(alias);
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
            }
        }

        return newInfos;
    }

    /**
     * Contains all {@link Name} mappings which will also including the target for any alias, but not the alias itself.
     */
    // @VisibleForTesting
    final Set<N> namesNotAliases;

    /**
     * Tests if the given {@link Name name} or alias will replace an existing {@link PluginAlias}, using the {@link PluginAlias#name()}.
     */
    public boolean containsAliasOrName(final N aliasOrName) {
        return null != this.findPluginAliasLikeWithNameOrAlias(aliasOrName);
    }

    private A findPluginAliasLikeWithNameOrAlias(final N nameOrAlias) {
        Objects.requireNonNull(nameOrAlias, "nameOrAlias");

        if (null == this.nameToPluginAliasLike) {
            final Map<N, A> nameToPluginAliasLike = Maps.sorted(this.helper.nameComparator());

            for (final A alias : this.pluginAliasLikes) {
                nameToPluginAliasLike.put(
                        alias.name(),
                        alias
                );

                final N selectorName = alias.selector()
                        .map(PluginSelectorLike::name)
                        .orElse(null);

                if (null != selectorName) {
                    nameToPluginAliasLike.put(
                            selectorName,
                            alias
                    );
                }
            }

            this.nameToPluginAliasLike = nameToPluginAliasLike;
        }

        return this.nameToPluginAliasLike.get(nameOrAlias);
    }

    /**
     * A {@link Map} where the {@link Name} maybe the name or alias taken from any {@link PluginAlias}.
     */
    private Map<N, A> nameToPluginAliasLike;

    /**
     * If the {@link PluginAliasLike} name exists then replace the alias with the same {@link Name} or concat because it
     * is new.
     */
    public PluginAliasSet<N, I, IS, S, A, AS> concatOrReplace(final A alias) {
        Objects.requireNonNull(alias, "alias");

        PluginAliasSet<N, I, IS, S, A, AS> pluginAliasSet = null;

        A pluginAliasWithName = this.findPluginAliasLikeWithNameOrAlias(alias.name());
        if (null != pluginAliasWithName) {
            pluginAliasSet = this.replace(
                    pluginAliasWithName,
                    alias
            );
        } else {
            final S selector = alias.selector()
                    .orElse(null);
            if (null != selector) {
                pluginAliasWithName = this.findPluginAliasLikeWithNameOrAlias(selector.name());

                if (null != pluginAliasWithName) {
                    pluginAliasSet = this.replace(
                            pluginAliasWithName,
                            alias
                    );
                }
            }

            if (null == pluginAliasSet) {
                pluginAliasSet = this.concat(alias);
            }
        }

        return pluginAliasSet;
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        if (null == this.text) {
            this.text = this.pluginAliasLikes.stream()
                    .map(this::aliasText)
                    .collect(Collectors.joining(SEPARATOR_SPACE))
                    .trim();
        }

        return this.text;
    }

    /**
     * Includes extra logic to insert a SPACE after a {@link PluginAliasLike#text()}, if the alias includes a {@link AbsoluteUrl}.
     * <br>
     * This is necessary because a URL can include a COMMA but not a SPACE.
     * <br>
     * alias1 name1 https://example.com/name1 ,alias2 name2 https://example.com/name2
     */
    private String aliasText(final A alias) {
        final String text = alias.text();
        return alias.url()
                .isPresent() ?
                text.concat(" ") :
                text;
    }

    private String text;

    private final static String SEPARATOR_SPACE = SEPARATOR + " ";

    // ImmutableSortedSet...............................................................................................

    @Override
    public Iterator<A> iterator() {
        return this.pluginAliasLikes.iterator();
    }

    @Override
    public int size() {
        return this.pluginAliasLikes.size();
    }

    @Override
    public Comparator<? super A> comparator() {
        return this.pluginAliasLikes.comparator();
    }

    @Override
    public PluginAliasSet<N, I, IS, S, A, AS> subSet(final A from,
                                                     final A to) {
        return prepare(
                this.pluginAliasLikes.subSet(
                        from,
                        to
                ),
                this.helper
        );
    }

    @Override
    public PluginAliasSet<N, I, IS, S, A, AS> headSet(final A alias) {
        return prepare(
                this.pluginAliasLikes.headSet(alias),
                this.helper
        );
    }

    @Override
    public PluginAliasSet<N, I, IS, S, A, AS> tailSet(final A alias) {
        return prepare(
                this.pluginAliasLikes.tailSet(alias),
                this.helper
        );
    }

    @Override
    public A first() {
        return this.pluginAliasLikes.first();
    }

    @Override
    public A last() {
        return this.pluginAliasLikes.last();
    }

    @Override
    public SortedSet<A> toSet() {
        final SortedSet<A> pluginAliasLikes = SortedSets.tree(this.comparator());
        pluginAliasLikes.addAll(this.pluginAliasLikes);
        return pluginAliasLikes;
    }

    @Override
    public PluginAliasSet<N, I, IS, S, A, AS> setElements(final SortedSet<A> pluginAliasLikes) {
        final PluginAliasSet<N, I, IS, S, A, AS> copy = with(
                pluginAliasLikes,
                this.helper
        );

        return this.equals(copy) ?
                this :
                copy;
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        for (final A pluginAlias : this.pluginAliasLikes) {
            pluginAlias.printTree(printer);
        }
    }

    private final SortedSet<A> pluginAliasLikes;

    private final PluginHelper<N, I, IS, S, A, AS> helper;
}
