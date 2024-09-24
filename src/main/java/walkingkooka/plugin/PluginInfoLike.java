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

import walkingkooka.compare.Comparators;
import walkingkooka.naming.HasName;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasAbsoluteUrl;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Captures the common members for a plugin INFO.
 * <br>
 * The type parameter N does not extend {@link PluginNameLike} because of {@link walkingkooka.tree.expression.ExpressionFunctionName}.
 * <br>
 * Note each {@link PluginInfoLike} must provide a public static parse method which must also be able to parse {@link Object#toString()}.
 */
public interface PluginInfoLike<I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> extends
        HasName<N>,
        HasAbsoluteUrl,
        Comparable<I>,
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
    static <I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> I parse(final String text,
                                                                                    final Function<String, N> nameFactory,
                                                                                    final BiFunction<AbsoluteUrl, N, I> infoFactory) {
        final PluginInfoLikeParser<N> parser = PluginInfoLikeParser.with(
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

        return infoFactory.apply(
                url,
                name
        );
    }

    static String toString(final PluginInfoLike<?, ?> pluginInfoLike) {
        Objects.requireNonNull(pluginInfoLike, "pluginInfoLike");

        return pluginInfoLike.url() + " " + pluginInfoLike.name();
    }

    // Comparable.......................................................................................................

    @Override
    default int compareTo(final I other) {
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

    // HateosResource...................................................................................................

    @Override
    default String hateosLinkId() {
        return this.name().value();
    }

    @Override
    default Optional<N> id() {
        return Optional.of(
                this.name()
        );
    }

    I setName(final N name);

    // json.............................................................................................................

    /**
     * Marshalls this {@link PluginInfoLike} into a {@link JsonNode}.
     */
    default JsonNode marshall(final JsonNodeMarshallContext context) {
        Objects.requireNonNull(context, "context");

        return JsonNode.string(
                this.toString()
        );
    }

    static <I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> I unmarshall(final JsonNode node,
                                                                                         final JsonNodeUnmarshallContext context,
                                                                                         final Function<String, N> nameFactory,
                                                                                         final BiFunction<AbsoluteUrl, N, I> factory) {
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(nameFactory, "nameFactory");
        Objects.requireNonNull(factory, "factory");

        return parse(
                node.stringOrFail(),
                nameFactory,
                factory
        );
    }
}
