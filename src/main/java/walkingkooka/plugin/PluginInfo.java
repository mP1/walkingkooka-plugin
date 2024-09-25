/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.net.HasAbsoluteUrl;
import walkingkooka.net.http.server.hateos.HateosResource;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Provides a unique identifying {@link AbsoluteUrl} and {@link Name}.
 */
public final class PluginInfo<N extends Name & Comparable<N>> implements HasName<N>,
        HasAbsoluteUrl,
        Comparable<PluginInfo<N>>,
        HateosResource<N> {

    /**
     * Useful helper that should be used by {@link PluginInfoLike} implementation parse methods.
     * <pre>
     *     SPACE*
     *     URL
     *     SPACE+
     *     NAME
     *     SPACE*
     * </pre>
     */
    public static <N extends Name & Comparable<N>> PluginInfo<N> parse(final String text,
                                                                       final Function<String, N> nameFactory) {
        final PluginInfoParser<N> parser = PluginInfoParser.with(
                text,
                nameFactory
        );

        parser.spaces();

        final AbsoluteUrl url = parser.url();

        parser.spaces();

        final N name = parser.name();

        parser.spaces();

        if(false == parser.isEmpty()) {
            parser.invalidCharacterException();
        }

        return new PluginInfo<>(
                url,
                name
        );
    }

    public static <N extends Name & Comparable<N>> PluginInfo<N> with(final AbsoluteUrl url,
                                                                      final N name) {
        return new PluginInfo<>(
                Objects.requireNonNull(url, "url"),
                Objects.requireNonNull(name, "name")
        );
    }

    private PluginInfo(final AbsoluteUrl url,
                       final N name) {
        this.url = url;
        this.name = name;
    }

    // HasAbsoluteUrl...................................................................................................

    @Override
    public AbsoluteUrl url() {
        return this.url;
    }

    private final AbsoluteUrl url;

    // HasName..........................................................................................................

    @Override
    public N name() {
        return this.name;
    }

    public PluginInfo<N> setName(final N name) {
        Objects.requireNonNull(name, "name");

        return this.name.equals(name) ?
                this :
                new PluginInfo<>(
                        this.url,
                        name
                );
    }

    private final N name;

    // HateosResource...................................................................................................

    @Override
    public String hateosLinkId() {
        return this.name()
                .value();
    }

    @Override
    public Optional<N> id() {
        return Optional.of(
                this.name()
        );
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final PluginInfo<N> other) {
        int compare = this.name().compareTo(other.name());

        if (Comparators.EQUAL == compare) {
            final AbsoluteUrl url = this.url().normalize();
            final AbsoluteUrl otherUrl = other.url().normalize();

            compare = url.host()
                    .compareTo(otherUrl.host());
            if (Comparators.EQUAL == compare) {
                compare = url.relativeUrl()
                        .toString()
                        .compareTo(
                                otherUrl.relativeUrl()
                                        .toString()
                        );
            }
        }

        return compare;
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.url,
                this.name
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof PluginInfo &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final PluginInfo<?> other) {
        return this.url.equals(other.url) &&
                this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return this.url + " " + this.name;
    }
}
