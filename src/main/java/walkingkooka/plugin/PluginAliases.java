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
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A definition of aliases including simple name entries.
 * <pre>
 * PluginNameWithoutAlias
 * PluginAliasName SPACE OriginalPluginName
 * PluginAliasName SPACE https://url SPACE OriginalPluginName OPEN-PAREN PluginName | $EnvironmentalValue | StringLiteral | DoubleLiteral RIGHT-PARENS
 * </pre>
 * Note this only holds aliases and names, no attempt is made to validate whether names actually exist or whether duplicate names or aliases are present.
 */
public final class PluginAliases<N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, IS extends PluginInfoSetLike<IS, I, N>, S extends PluginSelectorLike<N>> implements HasText,
        TreePrintable {

    /**
     * Parses the given text which defines names, aliases, selectors and possibly URLs. Note that selectors are only validated for syntax, environmental value names are not validated.
     */
    public static <N extends Name & Comparable<N>,
            I extends PluginInfoLike<I, N>,
            IS extends PluginInfoSetLike<IS, I, N>,
            S extends PluginSelectorLike<N>>
    PluginAliases<N, I, IS, S> parse(final String text,
                                     final BiFunction<TextCursor, ParserContext, Optional<N>> nameFactory,
                                     final BiFunction<AbsoluteUrl, N, I> infoFactory,
                                     final Function<Set<I>, IS> infoSetFactory,
                                     final Function<String, S> selectorFactory) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(nameFactory, "nameFactory");
        Objects.requireNonNull(infoFactory, "infoFactory");
        Objects.requireNonNull(infoSetFactory, "infoSetFactory");
        Objects.requireNonNull(selectorFactory, "selectorFactory");

        return parse0(
                PluginExpressionParser.with(
                        text,
                        nameFactory,
                        PluginAliasesProviderContext.INSTANCE
                ),
                infoFactory,
                infoSetFactory,
                selectorFactory
        );
    }

    private static <N extends Name & Comparable<N>,
            I extends PluginInfoLike<I, N>,
            IS extends PluginInfoSetLike<IS, I, N>,
            S extends PluginSelectorLike<N>> PluginAliases<N, I, IS, S> parse0(final PluginExpressionParser<N> parser,
                                                                               final BiFunction<AbsoluteUrl, N, I> infoFactory,
                                                                               final Function<Set<I>, IS> infoSetFactory,
                                                                               final Function<String, S> selectorFactory) {
        final Set<AbsoluteUrl> urls = Sets.hash();

        final Map<N, S> nameToAlias = Maps.sorted();
        final Map<N, N> nameToName = Maps.sorted();

        final SortedSet<N> aliases = SortedSets.tree();
        final SortedSet<N> names = SortedSets.tree();

        final Set<I> infos = SortedSets.tree();

        // name
        // alias-name SPACE name
        // alias-name SPACE selector SPACE url

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

                final Optional<S> maybeSelector = tryParseSelector(
                        parser,
                        selectorFactory
                );

                if (false == maybeSelector.isPresent()) {
                    // name END
                    final N name = nameOrAlias.get();
                    duplicateCheck(
                            name,
                            nameToAlias,
                            nameToName
                    );
                    nameToName.put(
                            name,
                            name
                    );

                    names.add(name);

                    requireSeparator = true;
                } else {
                    parser.spaces();

                    final S selector = maybeSelector.get();
                    final N alias = nameOrAlias.get();

                    final Optional<AbsoluteUrl> maybeUrl = parser.url();

                    if (maybeUrl.isPresent()) {
                        // url present add a new INFO
                        final AbsoluteUrl url = maybeUrl.get();
                        if(false == urls.add(url)) {
                            throw new IllegalArgumentException("Duplicate url " + url);
                        }

                        infos.add(
                                infoFactory.apply(
                                        url,
                                        alias
                                )
                        );
                    } else {
                        aliases.add(alias);
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
                    names.add(
                            selector.name()
                    );

                    requireSeparator = true;
                }
            }
        }

        return new PluginAliases<>(
                nameToAlias,
                Sets.immutable(aliases),
                nameToName,
                SortedSets.immutable(names),
                infoSetFactory.apply(infos)
        );
    }

    /**
     * Tries to parse a selector expression returning a {@link PluginSelectorLike}.
     */
    private static <N extends Name & Comparable<N>, S extends PluginSelectorLike<N>> Optional<S> tryParseSelector(final PluginExpressionParser<N> parser,
                                                                                                                  final Function<String, S> selectorFactory) {
        final TextCursorSavePoint selectorStart = parser.cursor.save();

        S selector = null;
        final Optional<N> selectorName = parser.name();
        if (selectorName.isPresent()) {
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
            }

            selector = selectorFactory.apply(
                    selectorStart.textBetween()
                            .toString()
            );
        }

        return Optional.ofNullable(selector);
    }

    private static <N extends Name & Comparable<N>, S extends PluginSelectorLike<N>> void duplicateCheck(final N name,
                                                                                                         final Map<N, S> aliases,
                                                                                                         final Map<N, N> names) {
        if (aliases.containsKey(name) || names.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate name: " + CharSequences.quoteAndEscape(name.value()));
        }
    }

    // @VisibleForTesting
    PluginAliases(final Map<N, S> nameToAliases,
                  final Set<N> aliases,
                  final Map<N, N> nameToName,
                  final Set<N> names,
                  final IS infos) {
        this.nameToAliases = nameToAliases;
        this.aliases = aliases;

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
     * Getter that returns all alias {@link Name} for aliases without a {@link PluginInfoLike}.
     */
    public Set<N> aliases() {
        return this.aliases;
    }

    private final Set<N> aliases;

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
     * Returns all {@link Name} mappings which will also including the target for any alias, but not the alias itself.
     */
    public Set<N> names() {
        return this.names;
    }

    private final Set<N> names;

    /**
     * Returns all {@link PluginInfoSetLike} including those belonging to new aliases definitions.
     */
    public IS infos() {
        return this.infos;
    }

    private final IS infos;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.nameToAliases,
                this.names,
                this.infos
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof PluginAliases &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final PluginAliases<?, ?, ?, ?> other) {
        return this.nameToAliases.equals(other.nameToAliases) &&
                this.nameToName.equals(other.nameToName) &&
                this.infos.equals(other.infos);
    }

    @Override
    public String toString() {
        return this.text();
    }

    // HasText..........................................................................................................

    /**
     * Returns text which sorts all entries by alias or name.
     * <br>
     * This will be useful when marshalling an alias to JSON and using the parse method to read it back.
     */
    @Override
    public String text() {
        if (null == this.text) {
            final Map<N, S> nameOrAliasToSelector = Maps.sorted();

            nameOrAliasToSelector.putAll(
                    this.nameToAliases
            );

            for (final Entry<N, N> nameToName : this.nameToName.entrySet()) {
                nameOrAliasToSelector.put(
                        nameToName.getKey(),
                        null
                );
            }

            final Map<N, AbsoluteUrl> nameToUrl = Maps.sorted();
            for (final I info : this.infos) {
                nameToUrl.put(
                        info.name(),
                        info.url()
                );
            }

            final StringBuilder b = new StringBuilder();

            String separator = "";

            for (final Entry<N, S> nameAndSelector : nameOrAliasToSelector.entrySet()) {
                b.append(separator);
                separator = ", ";

                final N name = nameAndSelector.getKey();
                b.append(name);

                final S selector = nameAndSelector.getValue();
                if (null != selector) {
                    b.append(' ')
                            .append(selector.toString().trim());

                    final AbsoluteUrl url = nameToUrl.get(name);
                    if (null != url) {
                        b.append(' ')
                                .append(url);
                        separator = " , "; // need a space after URL and before COMMA otherwise COMMA will be parsed as part of URL
                    }
                }
            }

            this.text = b.toString();
        }

        return this.text;
    }

    private String text;

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        if (false == this.nameToAliases.isEmpty()) {
            printer.println("aliases");
            {
                printer.indent();

                for (final Entry<N, S> nameAndSelector : this.nameToAliases.entrySet()) {
                    printer.println(nameAndSelector.getKey().toString());
                    printer.indent();
                    {
                        nameAndSelector.getValue()
                                .printTree(printer);
                    }
                    printer.outdent();
                }

                printer.outdent();
            }
        }

        if (false == this.nameToName.isEmpty()) {
            printer.println("names");
            {
                printer.indent();

                for (final Entry<N, N> nameAndName : this.nameToName.entrySet()) {
                    printer.println(nameAndName.getKey().toString());
                    printer.indent();
                    {
                        printer.println(
                                nameAndName.getValue().toString()
                        );
                    }
                    printer.outdent();
                }

                printer.outdent();
            }
        }

        if (false == this.infos.isEmpty()) {
            printer.println("infos");
            {
                printer.indent();
                this.infos.printTree(printer);
                printer.outdent();
            }
        }
    }
}
