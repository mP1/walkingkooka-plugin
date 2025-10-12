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
import walkingkooka.compare.Comparators;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.printer.IndentingPrinter;

import java.util.Objects;
import java.util.Optional;

/**
 * An individual declaration of a name or alias.
 */
public final class PluginAlias<N extends Name & Comparable<N>, S extends PluginSelectorLike<N>> implements PluginAliasLike<N, S, PluginAlias<N, S>> {

    /**
     * Parses the text holding a {@link PluginAlias} using the given {@link PluginHelper}.
     */
    public static <N extends Name & Comparable<N>,
        I extends PluginInfoLike<I, N>,
        IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
        S extends PluginSelectorLike<N>,
        A extends PluginAliasLike<N, S, A>,
        AS extends PluginAliasSetLike<N, I, IS, S, A, AS>>
    PluginAlias<N, S> parse(final String text,
                            final PluginHelper<N, I, IS, S, A, AS> helper) {
        final PluginExpressionParser<N> parser = PluginExpressionParser.with(
            text,
            helper::parseName
        );
        final PluginAlias<N, S> alias = parse0(
            parser,
            helper,
            PluginAliasesProviderContext.INSTANCE
        );

        if (parser.cursor.isNotEmpty()) {
            throw parser.invalidCharacter();
        }

        return alias;
    }

    static <N extends Name & Comparable<N>,
        I extends PluginInfoLike<I, N>,
        IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
        S extends PluginSelectorLike<N>,
        A extends PluginAliasLike<N, S, A>,
        AS extends PluginAliasSetLike<N, I, IS, S, A, AS>>
    PluginAlias<N, S> parse0(final PluginExpressionParser<N> parser,
                             final PluginHelper<N, I, IS, S, A, AS> helper,
                             final ProviderContext context) {
        parser.spaces();

        // name
        final Optional<N> nameOrAlias = parser.name();
        if (nameOrAlias.isPresent()) {
            parser.spaces();

            final Optional<S> maybeSelector = tryParseSelector(
                parser,
                helper,
                context
            );

            final PluginAlias<N, S> pluginAlias;

            if (false == maybeSelector.isPresent()) {
                // name END
                pluginAlias = PluginAlias.with(
                    nameOrAlias.get(),
                    maybeSelector, // no selector
                    Optional.empty() // no url
                );

            } else {
                parser.spaces();
                pluginAlias = PluginAlias.with(
                    nameOrAlias.get(),
                    maybeSelector, // selector
                    parser.url() // url
                );

                // there could be spaces after the url
                parser.spaces();
            }
            return pluginAlias;
        }
        throw parser.invalidCharacter();
    }

    /**
     * Tries to parse a selector expression returning a {@link PluginSelectorLike}.
     */
    private static <N extends Name & Comparable<N>,
        I extends PluginInfoLike<I, N>,
        IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
        S extends PluginSelectorLike<N>,
        A extends PluginAliasLike<N, S, A>,
        AS extends PluginAliasSetLike<N, I, IS, S, A, AS>>
    Optional<S> tryParseSelector(final PluginExpressionParser<N> parser,
                                 final PluginHelper<N, I, IS, S, A, AS> helper,
                                 final ProviderContext context) {
        final TextCursorSavePoint start = parser.cursor.save();
        TextCursorSavePoint end;

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

                        parser.spaces();
                    }

                    for (; ; ) {
                        if (parser.environmentValue(
                            context
                        ).isPresent()) {
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

    /**
     * Factory that creates a new {@link PluginAlias} with the given values.
     */
    public static <N extends Name & Comparable<N>, S extends PluginSelectorLike<N>> PluginAlias<N, S> with(final N name,
                                                                                                           final Optional<S> selector,
                                                                                                           final Optional<AbsoluteUrl> url) {
        return new PluginAlias<>(
            Objects.requireNonNull(name, "name"),
            Objects.requireNonNull(selector, "selector"),
            Objects.requireNonNull(url, "url")
        );
    }

    private PluginAlias(final N name,
                        final Optional<S> selector,
                        final Optional<AbsoluteUrl> url) {
        if (false == selector.isPresent() && url.isPresent()) {
            throw new IllegalArgumentException(name + " missing selector when url=" + url.get());
        }

        this.name = name;
        this.selector = selector;
        this.url = url;
    }

    /**
     * Getter that returns the name or alias name.
     */
    @Override
    public N name() {
        return this.name;
    }

    private final N name;

    @Override
    public Optional<S> selector() {
        return this.selector;
    }

    private final Optional<S> selector;

    @Override
    public Optional<AbsoluteUrl> url() {
        return this.url;
    }

    private final Optional<AbsoluteUrl> url;

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final PluginAlias<N, S> other) {
        int result = this.name.compareTo(other.name);
        if (Comparators.EQUAL == result) {

            final S selector = this.selector.orElse(null);
            final S otherSelector = other.selector.orElse(null);
            if (null != selector && null != otherSelector) {
                result = selector.name().compareTo(otherSelector.name());
                if (Comparators.EQUAL == result) {
                    result = selector.valueText()
                        .compareTo(
                            otherSelector.valueText()
                        );

                    if (Comparators.EQUAL == result) {

                        final AbsoluteUrl url = this.url.orElse(null);
                        final AbsoluteUrl otherUrl = other.url.orElse(null);

                        if (null != url && null != otherUrl) {
                            result = url.compareTo(otherUrl);
                        } else {
                            result = null == url ?
                                null == otherUrl ?
                                    Comparators.EQUAL :
                                    Comparators.LESS :
                                Comparators.MORE;
                        }
                    }
                }
            } else {
                result = null == selector ?
                    null == otherSelector ?
                        Comparators.EQUAL :
                        Comparators.LESS :
                    Comparators.MORE;
            }
        }
        return result;
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        final StringBuilder b = new StringBuilder();
        b.append(this.name);

        {
            final Optional<S> selector = this.selector;
            if (selector.isPresent()) {
                b.append(' ');
                b.append(selector.get());
            }
        }

        {
            final Optional<AbsoluteUrl> url = this.url;
            if (url.isPresent()) {
                b.append(' ');
                b.append(url.get());
            }
        }

        return b.toString();
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.name,
            this.selector,
            this.url
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof PluginAlias &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final PluginAlias<?, ?> other) {
        return this.name.equals(other.name) &&
            this.selector.equals(other.selector) &&
            this.url.equals(other.url);
    }

    @Override
    public String toString() {
        return this.text();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.name.value());

        printer.indent();
        {
            final Optional<S> selector = this.selector;
            if (selector.isPresent()) {
                selector.get().printTree(printer);
            }

            final Optional<AbsoluteUrl> url = this.url;
            if (url.isPresent()) {
                printer.println(
                    url.get()
                        .toString()
                );
            }
        }

        printer.outdent();
    }
}
