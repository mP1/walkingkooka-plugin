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
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A definition of aliases including simple name entries.
 * <pre>
 * PluginNameWithoutAlias
 * PluginAliasName SPACE OriginalPluginName
 * PluginAliasName SPACE https://url SPACE OriginalPluginName OPEN-PAREN PluginName | $EnvironmentalValue | StringLiteral | DoubleLiteral RIGHT-PARENS
 * </pre>
 */
public final class PluginAliases<N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, IS extends PluginInfoSetLike<IS, I, N>, S extends PluginSelectorLike<N>> implements TreePrintable {

    /**
     * Parses the given text which defines names, aliases, selectors and possibly URLs. Note that selectors are only validated for syntax, environmental value names are not validated.
     */
    public static <N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, IS extends PluginInfoSetLike<IS, I, N>, S extends PluginSelectorLike<N>> PluginAliases<N, I, IS, S> parse(final String text,
                                                                                                                                                                                             final BiFunction<TextCursor, ParserContext, Optional<N>> nameFactory,
                                                                                                                                                                                             final BiFunction<AbsoluteUrl, N, I> infoFactory,
                                                                                                                                                                                             final IS infos,
                                                                                                                                                                                             final Function<String, S> selectorFactory) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(nameFactory, "nameFactory");
        Objects.requireNonNull(infoFactory, "infoFactory");
        Objects.requireNonNull(infos, "infos");
        Objects.requireNonNull(selectorFactory, "selectorFactory");

        return parse0(
                PluginExpressionParser.with(
                        text,
                        nameFactory,
                        PluginAliasesProviderContext.INSTANCE
                ),
                infoFactory,
                infos,
                selectorFactory
        );
    }

    private static <N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, IS extends PluginInfoSetLike<IS, I, N>, S extends PluginSelectorLike<N>> PluginAliases<N, I, IS, S> parse0(final PluginExpressionParser<N> parser,
                                                                                                                                                                                               final BiFunction<AbsoluteUrl, N, I> infoFactory,
                                                                                                                                                                                               final IS infos,
                                                                                                                                                                                               final Function<String, S> selectorFactory) {
        final Function<N, I> nameToInfo = nameToInfo(infos);
        final Consumer<AbsoluteUrl> duplicateUrl = urlToInfo(infos);

        final Map<N, S> aliases = Maps.sorted();
        final Map<N, N> names = Maps.sorted();
        IS newInfos = infos;

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
                            aliases,
                            names
                    );
                    names.put(
                            name,
                            name
                    );

                    requireSeparator = true;
                } else {
                    parser.spaces();

                    final S selector = maybeSelector.get();
                    final N alias = nameOrAlias.get();

                    final Optional<AbsoluteUrl> maybeUrl = parser.url();

                    if (maybeUrl.isPresent()) {
                        // url present add a new INFO
                        final AbsoluteUrl url = maybeUrl.get();
                        duplicateUrl.accept(url);

                        newInfos = newInfos.concat(
                                infoFactory.apply(
                                        url,
                                        alias
                                )
                        );
                    } else {
                        // replace old INFO with new alias
                        final I old = nameToInfo.apply(selector.name());

                        newInfos = newInfos.replace(
                                old,
                                old.setName(alias)
                        );
                    }
                    duplicateCheck(
                            alias,
                            aliases,
                            names
                    );
                    aliases.put(
                            alias,
                            selector
                    );

                    requireSeparator = true;
                }
            }
        }

        return new PluginAliases<>(
                aliases,
                names,
                newInfos
        );
    }

    /**
     * Returns a {@link Function} which will complain if a {@link Name} is absent from the {@link PluginInfoSetLike}, returning the {@link PluginInfoLike}.
     */
    private static <N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, IS extends PluginInfoSetLike<IS, I, N>> Function<N, I> nameToInfo(final IS infos) {
        final Map<N, I> nameToInfo = Maps.sorted();

        for (final I info : infos) {
            nameToInfo.put(
                    info.name(),
                    info
            );
        }

        return (n) -> {
            final I i = nameToInfo.get(n);
            if (null == i) {
                throw new IllegalArgumentException("Unknown " + n);
            }
            return i;
        };
    }

    /**
     * Returns a {@link Consumer} which will complain if a given {@link AbsoluteUrl} is already present in the infos.
     */
    private static <N extends Name & Comparable<N>, I extends PluginInfoLike<I, N>, IS extends PluginInfoSetLike<IS, I, N>> Consumer<AbsoluteUrl> urlToInfo(final IS infos) {
        final Map<AbsoluteUrl, I> urlToInfo = Maps.hash();

        for (final I info : infos) {
            urlToInfo.put(
                    info.url(),
                    info
            );
        }

        return (u) -> {
            final I i = urlToInfo.get(u);
            if (null != i) {
                throw new IllegalArgumentException("Duplicate url " + i);
            }
        };
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
            throw new IllegalArgumentException("Duplicate " + name);
        }
    }

    PluginAliases(final Map<N, S> aliases,
                  final Map<N, N> names,
                  final IS infos) {
        this.aliases = aliases;
        this.names = names;
        this.infos = infos;
    }

    /**
     * Returns the selector if one is present for the given {@link Name alias}.
     */
    public Optional<S> alias(final N name) {
        Objects.requireNonNull(name, "name");

        return Optional.ofNullable(
                this.aliases.get(name)
        );
    }

    private final Map<N, S> aliases;

    /**
     * Queries the target name applying any aliases, or returning the name if no alias was present.
     * Note any aliases will not be returned and should be queried first.
     */
    public Optional<N> name(final N name) {
        Objects.requireNonNull(name, "name");

        return Optional.ofNullable(
                this.names.get(name)
        );
    }

    /**
     * Maps a {@link Name} to its target name, including name changes or aliases.
     */
    private final Map<N, N> names;

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
                this.aliases,
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
        return this.aliases.equals(other.aliases) &&
                this.names.equals(other.names) &&
                this.infos.equals(other.infos);
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();

        b.append("aliases: ");
        b.append(this.aliases);

        b.append(", names: ");
        b.append(this.names);

        b.append(", infos: ");
        b.append(this.infos);

        return b.toString();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        if (false == this.aliases.isEmpty()) {
            printer.println("aliases");
            {
                printer.indent();

                for (final Entry<N, S> nameAndSelector : this.aliases.entrySet()) {
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

        if (false == this.names.isEmpty()) {
            printer.println("names");
            {
                printer.indent();

                for (final Entry<N, N> nameAndName : this.names.entrySet()) {
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

        printer.println("infos");
        {
            printer.indent();
            this.infos.printTree(printer);
            printer.outdent();
        }
    }
}
