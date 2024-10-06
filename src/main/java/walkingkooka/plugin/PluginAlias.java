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
import walkingkooka.naming.HasName;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;
import java.util.Optional;

/**
 * An individual declaration of a name or alias.
 */
public final class PluginAlias<N extends Name & Comparable<N>, S extends PluginSelectorLike<N>> implements Comparable<PluginAlias<N, S>>,
        HasName<N>,
        HasText,
        TreePrintable {

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
        if(false == selector.isPresent() && url.isPresent()) {
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

    public Optional<S> selector() {
        return this.selector;
    }

    private final Optional<S> selector;

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
                    result = selector.text()
                            .compareTo(
                                    otherSelector.text()
                            );

                    if (Comparators.EQUAL == result) {

                        final AbsoluteUrl url = this.url.orElse(null);
                        final AbsoluteUrl otherUrl = other.url.orElse(null);

                        if (null != url && null != otherUrl) {
                            result = url.compareTo(otherUrl);
                        } else {
                            result = null == url ?
                                    Comparators.LESS :
                                    Comparators.MORE;
                        }

                    }
                }
            } else {
                result = null == selector ?
                        Comparators.LESS :
                        Comparators.MORE;
            }
        }
        return result;
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        return this.buildText("");
    }

    // @see PluginAliasSet#text
    String textAndSpace() {
        return this.buildText(" ");
    }

    private String buildText(final String afterUrl) {
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
                b.append(afterUrl);
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
